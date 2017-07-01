/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultSimulationInterface;
import net.finmath.exception.CalculationException;
import net.finmath.optimizer.SolverException;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * This class implements the calculation of the constrained worst case CVA as suggested in
 * Glasserman & Yang, Bounding Wrong-Way Risk in CVA Calculation, May 2015 https://poseidon01.ssrn.com/delivery.php?ID=863073094087023019070098104086096072049033032050001006088089088085002120018120120089010103119001122038023006084106001112120073055043092008037065026124090069068000079080064121094005094099124094077119087076098080126072005116099124076072113065088105&EXT=pdf
 * <br> This method uses the default probabilities and the net present value simulations of the product provided by {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultSimulationInterface }
 * to calculate the constrained worst case in the method {@link #getWorstCaseCVANotToFarFromIndependence(double, RandomVariableInterface[], int, double[], double, double, double)}.
 * 
 * 
 * @author Anton Sporrer
 *
 */
public class ConstrainedWorstCaseCVA extends AbstractCVA {

	// The error bounds used in the calculation of the constrained worst case CVA.
	double terminationCriterionRows;
	double terminationCriterionColumns;
	double terminationCriterionColumnsAbsolut; 
	
	/**
	 * The standard values are assumed for the error bounds used in the CVA calculation. 
	 * 
	 * @param lossGivenDefault The loss given default.
	 */
	public ConstrainedWorstCaseCVA(double lossGivenDefault) {
		this(lossGivenDefault, 0.0000001, 0.0000001, 0.00000000001);
	}

	/**
	 * @param lossGivenDefault
	 * @param terminationCriterionRows The error bound for the relative distance of the path probability and the sum of each row.
	 * @param terminationCriterionColumns The error bound for the relative distance of the default probability and the sum of each column.
	 * @param terminationCriterionColumnsAbsolut The error bound for the absolute distance of the default probability and the sum of each column.
	 */
	public ConstrainedWorstCaseCVA( double lossGivenDefault, double terminationCriterionRows, double terminationCriterionColumns, double terminationCriterionColumnsAbsolut ) {
		super(lossGivenDefault);
		this.terminationCriterionRows = terminationCriterionRows;
		this.terminationCriterionColumns = terminationCriterionColumns;
		this.terminationCriterionColumnsAbsolut = terminationCriterionColumnsAbsolut;
	}
	

	/**
	 * 
	 * This method uses the iterartive proportional fitting procedure (IPFP) described in 
	 * Glasserman & Yang, Bounding Wrong-Way Risk in CVA Calculation, May 2015 https://poseidon01.ssrn.com/delivery.php?ID=863073094087023019070098104086096072049033032050001006088089088085002120018120120089010103119001122038023006084106001112120073055043092008037065026124090069068000079080064121094005094099124094077119087076098080126072005116099124076072113065088105&EXT=pdf
	 *  to calculate the worst case CVA under a constraint.
	 * The constraint is that the eligible distributions used for the CVA calculation must not differ too much from the following target distribution. The target
	 * distribution is the independent distribution of following marginals. The first marginal is the distribution on the set {1, ... , d, d+1},
	 * which is the distribution of default on the set of time intervals { ( t<sub>0</sub>, t<sub>1</sub> ], ... , ( t<sub>d-1</sub>, t<sub>d</sub> ]  , ( t<sub>d</sub>, infinity ) } .
	 * The uniform distribution on the discrete set of simulated floored and discounted net presend value paths is the second marginal distribution.
	 * Both marginal distributions are provided through the parameter npvAndDefaultSimulation.
	 * 
	 * @param npvAndDefaultSimulation
	 * @param penaltyFactor This factor determines how much the worst case distribution is allowed to differ from the independent distribution. 
	 * @return The constraint worst case CVA
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws CalculationException
	 */
	public double getConstrainedWorstCaseCVA( NPVAndDefaultSimulationInterface< ? extends ProductConditionalFairValue_ModelInterface> npvAndDefaultSimulation, double penaltyFactor ) throws InterruptedException, ExecutionException, CalculationException {
		
		// The number of intervals into which the positive time line is divided. 
		// Including the interval from the last time discretization point to infinity.
		int numberOfTimeDiscretizationIntervalls = npvAndDefaultSimulation.getTimeDiscretization().getNumberOfTimeSteps() + 1;
		
		// At array index i this array stores the discounted and floored net present value at discretization time t_{i + 1}.
		RandomVariableInterface[] discountedFlooredNPV = new RandomVariableInterface[ numberOfTimeDiscretizationIntervalls - 1 ];
		
		// At array index i this array stores the probability of default occurring in the interval (t_{ i }, t_{ i + 1 }].
		double[] defaultProbabilities = new double[numberOfTimeDiscretizationIntervalls];
		
		
		////
		// Assigning the discounted and floored net present values.
		////
		
		for( int timeIndex = 0; timeIndex < numberOfTimeDiscretizationIntervalls - 1 ; timeIndex++ ) {
			discountedFlooredNPV[timeIndex] = npvAndDefaultSimulation.getDiscountedNPV( timeIndex + 1, 0 ).floor(0.0); 
		}
		

		////
		// Assigning the Default Probabilities
		////
		
		defaultProbabilities[0] = npvAndDefaultSimulation.getDefaultProbability(1);
		
		for(int timeIndex = 1; timeIndex < numberOfTimeDiscretizationIntervalls - 1 ; timeIndex++) {
			defaultProbabilities[timeIndex] = npvAndDefaultSimulation.getDefaultProbability( timeIndex + 1 ) - npvAndDefaultSimulation.getDefaultProbability( timeIndex ); 	
		}
		
		// Calculating the probability that default occurrs after the last time discretization point.
		defaultProbabilities[numberOfTimeDiscretizationIntervalls - 1] = 1.0 - npvAndDefaultSimulation.getDefaultProbability( numberOfTimeDiscretizationIntervalls - 1 );
		
		
		// Calling the method actually calculating the constrained worst case CVA (assumed LGD is one) and multiplying the actual LGD.
		return this.getLGD() * getWorstCaseCVANotToFarFromIndependence( penaltyFactor, discountedFlooredNPV, npvAndDefaultSimulation.getNumberOfPaths(), defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut );
		
	}
	
	
	/**
	 * 
	 * This method uses the iterartive proportional fitting procedure (IPFP) to calculate the worst case CVA under a constraint.
	 * The constraint is that the eligible distributions used for the CVA calculation must not differ too much from the following target distribution. The target
	 * distribution is the independent distribution of following marginals. The first marginal is the distribution on the set {1, ... , d, d+1},
	 * which is the distribution of default on the set of time intervals { ( t<sub>0</sub>, t<sub>1</sub> ], ... , ( t<sub>d-1</sub>, t<sub>d</sub> ]  , ( t<sub>d</sub>, infinity ) } .
	 * The uniform distribution on the discrete set of simulated paths stored in discountedFlooredNPV is the second marginal distribution.
	 * 
	 * @param penaltyFactor This factor determines how much the worst case distribution is allowed to differ from the independent distribution. 
	 * @param discountedFlooredNPV (Numeraire(0)/Numeraire(t_i) * (NPV(t_i))<sup> + </sup>)<sub> i = 1, ... , d </sub>
	 * @param numberOfSimulationPaths The number of simulated paths.
	 * @param defaultProbabilities The probabilities of default occurring in the time intervals  (t_0, t_{1}], ... , (t_{d-1}, t_d], (t_d, infinity).
	 * @return Worst Case CVA under constraint
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public double getWorstCaseCVANotToFarFromIndependence(final double penaltyFactor, final RandomVariableInterface[] discountedFlooredNPV, final int numberOfSimulationPaths, final double[] defaultProbabilities, final double terminationCriterionRows, final double terminationCriterionColumns, final double terminationCriterionColumnsAbsolut ) throws InterruptedException, ExecutionException {
		
		// TODO: Add error handling in case there are too few paths or time steps.
		
		////
		// Calculating the Initial Matrix for the IPFP.
		////
		
		// The number of columns and the number of rows are fetched.
		final int numberOfColumns = defaultProbabilities.length;
		final int numberOfRows = numberOfSimulationPaths;
		
		// The initial matrix is allocated.
		final double[][] matrixA = new double[numberOfRows][numberOfColumns];
		// The matrix used as next step in the iteration is allocated.
		final double[][] matrixB = new double[numberOfRows][numberOfColumns];
		
		// The probability of the occurrence of a particular path.
		// A uniform distribution is assumed.
		final double pathProbability = 1.0 / numberOfRows;
		
		// These variables are used to terminate the IPFP in case the marginal 
		// distributions are close enough to the target marginals.
		Boolean rowsAreEligibleForTermination  = false;
		Boolean columnsAreEligibleForTermination  = false;
		
		
		////
		// Multi-Threading Preparations
		////
		
		// We do not allocate more threads then twice the number of processors.
		int numberOfThreads = Math.min(Math.max(2 * Runtime.getRuntime().availableProcessors(),1),defaultProbabilities.length);
		
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		
		// Using Callable instead of runnable such that invokeAll can be invoked.
		Set<Callable<Double>> callablesInitialMatrix = new HashSet<Callable<Double>>();
		
		
		////
		// First, the initial matrix is assigned as follows. 
		// Where F_{i,j} := q_{j} * (1 / numberOfRows).
		// Where F_{i,j} is the independent joint distribution of the following marginal distributions. 
		// The default probabilities on the different interval indices and the uniform distribution on path set.
		// matrix[i][j] = exp( penaltyFactor * discountedFlooredNPV[i][j] ) * F_{i,j}.
		////
		
		// The last column is assigned.
		callablesInitialMatrix.add( new Callable<Double>() { 
			
			public Double call() {
				
				// This is the value that would be lost if the default occurred at this time index.
				// The last time index encodes that no default occurs in the observed time horizon.
				// Therefore no loss occurs.
				double currentEntry = 0.0;
				
				double currentIndependentCommonDistributionWeight = 0.0;
				
				// Iterating over each row in the current column.
				for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
					
					// This is q_{numberOfColumns-1} * (1 / numberOfRows) =: F_{rowIndex, numberOfColumns-1}. 
					// The probability of the occurrence of the numberOfColumns-1 and the rowIndex assuming 
					// an independent distribution of the marginals.
					currentIndependentCommonDistributionWeight = pathProbability * defaultProbabilities[ numberOfColumns - 1 ]; 
					
					// Multiply the current entry by the penalty factor and apply exp. Then multiply with F_{rowIndex, fixedColumnIndex}.
					matrixA[rowIndex][ numberOfColumns - 1 ] = Math.exp( penaltyFactor * currentEntry ) * currentIndependentCommonDistributionWeight;
					
				}
				
				// Not used.
				return 0.0;
			} 
			
		});
		
		// Then all columns except the last one of the matrix are assigned.
		for(int columnIndex = 0; columnIndex < numberOfColumns - 1; columnIndex++) {
		
			final int fixedColumnIndex = columnIndex;
			
			callablesInitialMatrix.add( new Callable<Double>() { 
				public Double call() {
					
					double currentEntry = 0.0;
					
					double currentIndependentCommonDistributionWeight = 0.0;
					
					// Speeds up performance significantly.
					double[] currentColumn = discountedFlooredNPV[fixedColumnIndex].getRealizations();
					
					// Iterating over each row in the current column.
					for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
						
						// First the value of the process at a given time index and realization is fetched.
						// This is the value that would be lost if the default occurred at this time index on 
						// this path with a loss given default of 1.
						currentEntry = currentColumn[rowIndex];
						
						// This is q_{fixedColumnIndex} * (1 / numberOfRows) =: F_{rowIndex, fixedColumnINdex}. 
						// The probability of the occurrence of the fixedColumnIndex and the rowIndex assuming 
						// an independent distribution of the marginals.
						currentIndependentCommonDistributionWeight = pathProbability * defaultProbabilities[fixedColumnIndex]; 
						
						// Multiply the current entry by the penalty factor and apply exp then multiply with F_{rowIndex, fixedColumnINdex}.
						matrixA[rowIndex][fixedColumnIndex] = Math.exp( penaltyFactor * currentEntry ) * currentIndependentCommonDistributionWeight;
						
// TODO: Comment out debug code.
//if(true) {
//	System.out.println("matrixA[rowIndex][columnIndex]: " + matrixA[rowIndex][fixedColumnIndex]);
//	System.out.println("NPV or C is: " + currentEntry);
//	System.out.println("Common Distribution Weight: " + currentIndependentCommonDistributionWeight);
//	System.out.println("Penalty Factor: " + penaltyFactor);
//	System.out.println("The row index, column index are: " + rowIndex+ "," + fixedColumnIndex);
//	}
						
					}
					
					return 0.0;
				}
			});
		
		}
	
	
		// Waits until each thread executed the call code. TODO: Error handling, overflow handling
		executor.invokeAll(callablesInitialMatrix);

		
		////
		// Setup for the row-wise iteration step.
		////
		
		
		// TODO Continue proof read.
		// The number of blocks into which the matrix is split. When each block has at most rowPerBlock rows.
		// If the number of rows is not divisible by the number of threads one additional block is needed
		// to cover the whole matrix.
		final int numberOfMatrixBlocks = (numberOfRows % numberOfThreads) == 0 ? numberOfThreads : Math.min( numberOfThreads + 1, numberOfRows);
		
		// The number of rows each block has.
		final int rowsPerBlock = numberOfRows / Math.min(numberOfRows, numberOfThreads);
		

		////
		// Performing the Iteration Steps
		////
	
		// Initializations for Multi-Threading
		
		Set<Callable<Boolean>> callablesForRowSum = new HashSet<Callable<Boolean>>();
		List<Future<Boolean>> partialRowTests = new ArrayList<Future<Boolean>>();
		
		Set<Callable<Double>> callablesForColumnSums = new HashSet<Callable<Double>>();
		List<Future<Double>> partialColumnTests = new ArrayList<Future<Double>>();
		
		//TODO: Add abort criterion if the marginal distributions reached the wanted distribution. Is that the right criterion?
		
		for(int iterationIndex = 0; iterationIndex < 100; iterationIndex++) {
			
			////
			// Row-wise Renormalization Step
			////
			
			// Calculating the row operations block-wise.
			// Each block is treated by a thread.
			
			callablesForRowSum = new HashSet<Callable<Boolean>>();
			
			// Iterating over all blocks. 
			for(int blockIndex = 0; blockIndex < numberOfMatrixBlocks; blockIndex++) {
			
				// The matrix-index of the first row in the current block.
				final int fixedIndexFirstRowOfBlock = blockIndex * rowsPerBlock;
				// If we are in the last block the rows only iterate to the final row of the
				// whole matrix.
				final int fixedIndexLastRowOfBlockPlusOne = ( blockIndex != (numberOfMatrixBlocks - 1) ) ? (blockIndex+1) * rowsPerBlock : numberOfRows;
				
// TODO: Comment out after Debugging:
final int iterationIndexDebug = iterationIndex;
				
				callablesForRowSum.add(new Callable<Boolean>() {
					
					public Boolean call() {
						
						////
						// In the first step all rows are summed. 
						// And each entry of the current row is divided by the sum of the current row.
						////
						
						// Variables for Kahan summation
						double currentRowSum = 0.0;
						double helperNextSum = 0.0;
						
						double nextToAdd = 0.0;
						double negativLostDigitsStorage = 0.0;
						
						// At the end of the iteration over each row this variable shows the following.
						// It shows if all row sums satisfy that their relative distance to the desired path
						// probability is smaller than the termination criterion value.
						Boolean allRowsSatisfyTerminationCriterion = true;
						
						// Iterating over each row in the current block
						for(int rowIndex = fixedIndexFirstRowOfBlock; rowIndex < fixedIndexLastRowOfBlockPlusOne; rowIndex++ ) {
							
							// Resetting the variables after the previous row has been summed.
							currentRowSum = 0.0;
							
							nextToAdd = 0.0;
							negativLostDigitsStorage = 0.0;
							helperNextSum = 0.0;
							
							// Kahan summation for summing the current row.
							for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
								
								nextToAdd = matrixA[rowIndex][columnIndex] - negativLostDigitsStorage;
								helperNextSum += nextToAdd;
								negativLostDigitsStorage = (helperNextSum - currentRowSum) - nextToAdd;
								currentRowSum = helperNextSum;
								
							}
							
							// Testing current row if the termination criterion is satisfied.
							if( Math.abs( currentRowSum - pathProbability ) / pathProbability > terminationCriterionRows ) {
								allRowsSatisfyTerminationCriterion = false;
							}
					
// Debug-Code 
//							if(rowIndex == 0) {
//								System.out.println(" Row : " + rowIndex + "has running weight sum: " + currentRowSum);
//							}
							
							// Renormalizing the current row.
							for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
								matrixB[rowIndex][columnIndex] = matrixA[rowIndex][columnIndex] / currentRowSum * pathProbability;
								
// TODO: Debug Code: Comment out after test.
//if( iterationIndexDebug == 2 && rowIndex == 22  ) {
//		System.out.println("matrixB[rowIndex][columnIndex]: " + matrixB[rowIndex][columnIndex]);
//		System.out.println("matrixA[rowIndex][columnIndex]: " + matrixA[rowIndex][columnIndex]);
//		System.out.println("Sum in Denominator is: " + currentRowSum);
//		System.out.println("The row index, column index are: " + rowIndex+ "," + columnIndex);
//		System.out.println("The iteration index is: " + iterationIndexDebug);
//	}


							}
									
						}
						
						// Returning true if all rows of the current block satisfy the termination criterion. Otherwise false.
						return allRowsSatisfyTerminationCriterion;
					}
				});
			
			}
			
			// Waits until each thread executed the call code. TODO: Error handling
			// The partial sums are initialized and fetched. 
			partialRowTests = new ArrayList<Future<Boolean>>();
			partialRowTests = executor.invokeAll(callablesForRowSum);
			
			// Setting variable to true before the iteration.
			rowsAreEligibleForTermination = true;
			
			// Only if all rows satisfy the termination criterion the termination variable stays true.
			for(int indexRowsBlock = 0; indexRowsBlock < numberOfMatrixBlocks; indexRowsBlock++) {
				if(  ! ( partialRowTests.get(indexRowsBlock).get() )  ) {
					rowsAreEligibleForTermination = false;
				}  
			}
			
			
			////
			// Column-wise Renormalization Step
			// All columns are summed.
			// Each column entry is divided by the summed current column.
			// The resulting value is then multiplied by the default probability 
			// associated with the current column.
			////
			
			// The set of Callables is reset.
			callablesForColumnSums = new HashSet<Callable<Double>>();
			
			for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
				
				final double currentDefaultProbability = defaultProbabilities[columnIndex];
				
				final double[][] fixedMatrix = matrixB;
				
				final int fixedColumnIndex = columnIndex;
				
				callablesForColumnSums.add( new Callable<Double>() {
						public Double call() throws SolverException {
							
							double runningColumnSum = 0;
							double helperNextSum = 0.0;
							
							double negativLostDigitsStorage = 0.0;
							double nextToAdd = 0.0;
						
							for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {

								nextToAdd = fixedMatrix[rowIndex][fixedColumnIndex] - negativLostDigitsStorage;
								helperNextSum += nextToAdd;
								negativLostDigitsStorage = ( helperNextSum - runningColumnSum ) - nextToAdd;
								runningColumnSum = helperNextSum;
								
							}
							
							for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
							
								matrixA[rowIndex][fixedColumnIndex] = matrixB[rowIndex][fixedColumnIndex] / runningColumnSum * currentDefaultProbability;
								
							}
// Debug Code:
//							System.out.println("Columnindex" + fixedColumnIndex + ", running probability weight sum: " + runningColumnSum);
							
						
							return Math.abs( runningColumnSum - currentDefaultProbability );
						}
					}); 

			}
					
			// Waits until each thread executed the call code. TODO: Error handling
			// The partial sums are initialized and fetched. 
			partialColumnTests = new ArrayList<Future<Double>>();
			partialColumnTests = executor.invokeAll(callablesForColumnSums);
		
			// Setting variable to true before the iteration.
			columnsAreEligibleForTermination = true;
			
			// Only if all columns satisfy the termination criterion the termination variable stays true.
			for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
				
				if(defaultProbabilities[columnIndex] > 0) {
					if( (partialColumnTests.get(columnIndex).get() / defaultProbabilities[columnIndex]) > terminationCriterionColumnsAbsolut  ) {
						columnsAreEligibleForTermination = false;
					}
				}
				else {
					if(  partialColumnTests.get(columnIndex).get() > terminationCriterionColumns ) {
						columnsAreEligibleForTermination = false;
					}
				}
			}
			
		
			// Testing if both termination criterion are satisfied in this case the iteration is exited.
			if( rowsAreEligibleForTermination && columnsAreEligibleForTermination ) { 
				break; 
			}
			
		}
		
		
		////
		// Calculating the CVA with respect to the calculated worst case distribution under constraint.
		////
		
		// The set of Callables is reset.
		Set<Callable<Double>> callablesFinalSum = new HashSet<Callable<Double>>();
		
		for(int columnIndex = 0; columnIndex < numberOfColumns - 1; columnIndex++) {
		
			final int fixedColumnIndex = columnIndex;
			
			callablesFinalSum.add(new Callable<Double>() {
				public Double call() {
					
//					System.out.println("Only column index: " + fixedColumnIndex); // !
					
					double runningSum = 0.0;
					double helperNextSum = 0.0;
					
					double nextToAdd = 0.0;
					double negativLostDigitsStorage = 0.0;
					
					double[] currentColumn = discountedFlooredNPV[fixedColumnIndex].getRealizations();
					
					for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
				
						nextToAdd = currentColumn[rowIndex] * matrixA[rowIndex][fixedColumnIndex] 
								- negativLostDigitsStorage;
						helperNextSum = runningSum + nextToAdd;
						negativLostDigitsStorage = ( helperNextSum - runningSum ) - nextToAdd;
						runningSum = helperNextSum;
						
					}
//					System.out.println("Column " + fixedColumnIndex + " has running cva sum: " + runningSum); // !
					return runningSum;
					
				}
			});

		}
		
		// The partial sums are initialized and fetched. 
		List<Future<Double>> partialSums = new ArrayList<Future<Double>>();
		partialSums = executor.invokeAll(callablesFinalSum);
		
		
		////
		// The partial sums are added together.
		////
	
		double worstCaseCVAUnderConstraint = 0.0;
	
		for(int columnIndex = 0; columnIndex < numberOfColumns - 1 /* The last column is the zero column */; columnIndex++) {
			
			worstCaseCVAUnderConstraint += partialSums.get(columnIndex).get();
			
		}
		
		
		////
		// The executor has to be shutdown before leaving the method.
		// Otherwise the threads will continue to run.
		////
		
		try {
			executor.shutdown();
		}
		catch(SecurityException e) {
			// TODO: Improve exception handling here
		}
		
		return worstCaseCVAUnderConstraint;
	
	}

	
			// TODO: Delete after Tests are completed.
		//		/**
		//		 * 
		//		 * This method uses the iterartive proportional fitting procedure (IPFP) to calculate the worst case CVA under a constraint.
		//		 * The constraint is that the eligible distributions must not differ too much from the following target distribution. The target
		//		 * distribution is the independent distribution of the discountedFlooredNPV and the default.
		//		 * 
		//		 * @param penaltyFactor This factor determines how much the worst case distribution is allowed to differ from the independent distribution. 
		//		 * @param discountedFlooredNPV (Numeraire(0)/Numeraire(t_i) * (NPV(t_i))<sup> + </sup>)<sub> i = 1, ... , d </sub>
		//		 * @param numberOfSimulationPaths The number of simulated paths.
		//		 * @param defaultProbabilities The default probabilities in the time intervalls  (t_0, t_{1}], ... , (t_{d-1}, t_d], (t_d, infinity).
		//		 * @return
		//		 * @throws ExecutionException 
		//		 * @throws InterruptedException 
		//		 */
		//		public double getWorstCaseCVANotToFarFromIndependence(final double penaltyFactor, final RandomVariableInterface[] discountedFlooredNPV, final int numberOfSimulationPaths, final double[] defaultProbabilities ) throws InterruptedException, ExecutionException {
		//			
		//			////
		//			// Calculating the Initial Matrix for the IPFP.
		//			////
		//			
		//			// The number of columns and the number of rows are fetched.
		//			final int numberOfColumns = defaultProbabilities.length;
		//			final int numberOfRows = numberOfSimulationPaths;
		//			
		//			// The initial matrix is allocated.
		//			final double[][] firstStepMatrix = new double[numberOfRows][numberOfColumns];
		//			
		//			
		//			// First the initial matrix is assigned as follows. Where F_{i,j} := q_{j} * (1 / numberOfRows)
		//			// initialMatrix[i][j] = exp( penaltyFactor * discountedFlooredNPV[i][j] ) * F_{i,j}.
			//
		//			double currentEntry = 0.0;
		//			
		//			double currentIndependentCommonDistributionWeight = 0.0;
		//			
		//			// All columns except the last one of the matrix are assigned.
		//			for(int columnIndex = 0; columnIndex < numberOfColumns - 1 /* the last column is treated differently */; columnIndex++) {
			//	
		//				// Iterating over each row in the current column.
		//				for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//					
		//					// First the value of the process at a given time index and realization is fetched.
		//					// This is the value that would be lost if the default occurred at this time index on 
		//					// this path with a loss given default of 1.
		//					currentEntry = discountedFlooredNPV[columnIndex].getRealizations()[rowIndex];
		//					
		//					// This is q_{fixedColumnIndex} * (1 / numberOfRows) =: F_{rowIndex, fixedColumnINdex}. 
		//					// The probability of the occurrence of the fixedColumnIndex and the rowIndex assuming 
		//					// an independent distribution of the marginals.
		//					currentIndependentCommonDistributionWeight = 1.0 / numberOfRows * defaultProbabilities[columnIndex]; 
		//					
		//					// Multiply the current entry by the penalty factor and apply exp then multiply with F_{rowIndex, fixedColumnINdex}.
		//					firstStepMatrix[rowIndex][columnIndex] = Math.exp( penaltyFactor * currentEntry ) * currentIndependentCommonDistributionWeight;
		//					
		//				}
		//					
		//			}
			//
		//			
		//			// Iterating over each row in the last column.
		//			for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//				
			//
		//				// This is the value that would be lost if the default occurred at this time index on 
		//				// this path with a loss given default of 1.
		//				// The default does not occur before the last observed point in time.
		//				// Therefore the loss is zero.
		//				currentEntry = 0.0;
		//				
		//				// This is q_{fixedColumnIndex} * (1 / numberOfRows) =: F_{rowIndex, fixedColumnINdex}. 
		//				// The probability of the occurrence of the fixedColumnIndex and the rowIndex assuming 
		//				// an independent distribution of the marginals.
		//				currentIndependentCommonDistributionWeight = 1.0 / numberOfRows * defaultProbabilities[ numberOfColumns - 1 ]; 
		//				
		//				// Multiply the current entry by the penalty factor and apply exp then multiply with F_{rowIndex, fixedColumnINdex}.
		//				firstStepMatrix[ rowIndex ][ numberOfColumns - 1 ] = Math.exp( penaltyFactor * currentEntry ) * currentIndependentCommonDistributionWeight;
		//				
		//			}
		//			
		//			
		//			double[][] nextStepMatrix = new double[numberOfRows][numberOfColumns];
		//			
		//			
		//			int numberOfIterations = 1;
		//			
		//			double pathProbability = 1.0 / numberOfRows;
		//			
		//			for(int iterationIndex = 0; iterationIndex < numberOfIterations; iterationIndex++) {
		//			
		//				////
		//				// In the first step all rows are summed. 
		//				// And each entry of the current row is divided by the sum of the current row.
		//				////
		//				
		//				double currentRowSum = 0.0;
		//				
		//				double nextToAdd = 0.0;
		//				double negativLostDigitsStorage = 0.0;
		//				double helperNextSum = 0.0;
		//				
		//				for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//					
		//					currentRowSum = 0.0;
		//					
		//					nextToAdd = 0.0;
		//					negativLostDigitsStorage = 0.0;
		//					helperNextSum = 0.0;
		//					
		//					for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
		//						
		//						nextToAdd = firstStepMatrix[rowIndex][columnIndex] - negativLostDigitsStorage;
		//						helperNextSum += nextToAdd;
		//						negativLostDigitsStorage = (helperNextSum - currentRowSum) - nextToAdd;
		//						currentRowSum = helperNextSum;
		//						
		//					}
		//					
		//					for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
		//						nextStepMatrix[rowIndex][columnIndex] = firstStepMatrix[rowIndex][columnIndex] / currentRowSum * pathProbability;
		//					}
		//					
		//				}
		//					
		//				
		//				
		//				////
		//				// In the second step all columns are summed.
		//				// Each entry then is divided by the summed current column.
		//				// The resulting value is then multiplied by the default probability 
		//				// associated with the current column.
		//				////
		//				
		//				
		//				// We do not allocate more threads the twice the number of processors.
		//				int numberOfThreads = Math.min(Math.max(2 * Runtime.getRuntime().availableProcessors(),1),numberOfColumns);
		//				
		//				ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		//				
		//				Vector<Future<Double>> columnSums = new Vector<Future<Double>>(numberOfColumns);
		//				columnSums.setSize(numberOfColumns);
		//				
		//				for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
		//					
		//					final double[][] fixedMatrix = nextStepMatrix;
		//					
		//					final int fixedColumnIndex = columnIndex;
		//					
		//						Future<Double> columnSum = executor.submit( new Callable<Double>() {
		//							public Double call() throws SolverException {
		//								
		//								double runningColumnSum = 0;
		//								double helperNextSum = 0.0;
		//								
		//								double negativLostDigitsStorage = 0.0;
		//								double nextToAdd = 0.0;
		//							
		//								for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
			//
		//									nextToAdd = fixedMatrix[rowIndex][fixedColumnIndex] - negativLostDigitsStorage;
		//									helperNextSum += nextToAdd;
		//									negativLostDigitsStorage = ( helperNextSum - runningColumnSum ) - nextToAdd;
		//									runningColumnSum = helperNextSum;
		//									
		//								}
		//								
		//								return runningColumnSum;
		//							}
		//						}); 
		//				
		//					columnSums.set(columnIndex, columnSum);
		//					
		//				}
		//				
		//				
		//				
		//				double currentDefaultProbability = 0.0;
		//				
		//				double currentColumnSum = 0.0;
		//			
		//				for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
		//				
		//					currentColumnSum = columnSums.get(columnIndex).get().doubleValue();
		//					
		//					currentDefaultProbability = defaultProbabilities[columnIndex];
		//					
		//					for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//					
		//						firstStepMatrix[rowIndex][columnIndex] = nextStepMatrix[rowIndex][columnIndex] / currentColumnSum * currentDefaultProbability;
		//						
		//					}
		//						
		//				}
		//				
		//				// Divide each entry of column by sum and multiply by q(columnIndex).
		//				
		//				try {
		//					executor.shutdown();
		//				}
		//				catch(SecurityException e) {
		//					// TODO: Improve exception handling here
		//				}
		//				
		//			}
		//				
		//			
			//	
		//			double worstCaseCVAUnderConstraint = 0.0;
		//			double helperNextSum = 0.0;
		//			
		//			double nextToAdd = 0.0;
		//			double negativLostDigitsStorage = 0.0;
		//			
		//			for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//			
		//				for(int columnIndex = 0; columnIndex < numberOfColumns - 1; columnIndex++) {
		//					
		//					nextToAdd = discountedFlooredNPV[columnIndex].getRealizations()[rowIndex] 
		//							* firstStepMatrix[rowIndex][columnIndex] - negativLostDigitsStorage;
		//					helperNextSum = worstCaseCVAUnderConstraint + nextToAdd;
		//					negativLostDigitsStorage = ( helperNextSum - worstCaseCVAUnderConstraint ) - nextToAdd;
		//					worstCaseCVAUnderConstraint = helperNextSum;
		//					
		//				}
		//				
		//			}
		//			
		//			return worstCaseCVAUnderConstraint;
		//			
		//		}
			//
			//	
		//		/**
		//		 * 
		//		 * This method uses the iterartive proportional fitting procedure (IPFP) to calculate the worst case CVA under a constraint.
		//		 * The constraint is that the eligible distributions must not differ too much from the following target distribution. The target
		//		 * distribution is the independent distribution of the discountedFlooredNPV and the default.
		//		 * 
		//		 * @param penaltyFactor This factor determines how much the worst case distribution is allowed to differ from the independent distribution. 
		//		 * @param discountedFlooredNPV (Numeraire(0)/Numeraire(t_i) * (NPV(t_i))<sup> + </sup>)<sub> i = 1, ... , d </sub>
		//		 * @param numberOfSimulationPaths The number of simulated paths.
		//		 * @param defaultProbabilities The default probabilities in the time intervalls  (t_0, t_{1}], ... , (t_{d-1}, t_d], (t_d, infinity).
		//		 * @return
		//		 * @throws ExecutionException 
		//		 * @throws InterruptedException 
		//		 */
		//		public double getWorstCaseCVANotToFarFromIndependence1(final double penaltyFactor, final RandomVariableInterface[] discountedFlooredNPV, final int numberOfSimulationPaths, final double[] defaultProbabilities ) throws InterruptedException, ExecutionException {
		//			
		//			////
		//			// Calculating the Initial Matrix for the IPFP.
		//			////
		//			
		//			// The number of columns and the number of rows are fetched.
		//			final int numberOfColumns = defaultProbabilities.length;
		//			final int numberOfRows = numberOfSimulationPaths;
		//			
		//			// The initial matrix is allocated.
		//			final double[][] matrixA = new double[numberOfRows][numberOfColumns];
		//			// The matrix used as next step in the iteration is allocated.
		//			final double[][] matrixB = new double[numberOfRows][numberOfColumns];
		//			
		//			// The probability of the occurrence of a particular path.
		//			// A uniform distribution is assumed.
		//			final double pathProbability = 1.0 / numberOfRows;
		//			
		//			
		//			////
		//			// Multi-Threading Preparations
		//			////
		//			
		//			// We do not allocate more threads the twice the number of processors.
		//			int numberOfThreads = Math.min(Math.max(2 * Runtime.getRuntime().availableProcessors(),1),defaultProbabilities.length);
		//			
		//			ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		//			
		//			// Using Callable instead of runnable such that invokeAll can be invoked.
		//			Set<Callable<Double>> callables = new HashSet<Callable<Double>>();
		//			
		//			
		//			////
		//			// First the initial matrix is assigned as follows. Where F_{i,j} := q_{j} * (1 / numberOfRows)
		//			// matrix[i][j] = exp( penaltyFactor * discountedFlooredNPV[i][j] ) * F_{i,j}.
		//			////
		//			
		//			// The last column is assigned.
		//			callables.add( new Callable<Double>() { 
		//				
		//				public Double call() {
		//					
		//					// This is the value that would be lost if the default occurred at this time index.
		//					// The last time index encodes that no default occurs in the observed time horizon.
		//					// Therefore no loss occurs.
		//					double currentEntry = 0.0;
		//					
		//					double currentIndependentCommonDistributionWeight = 0.0;
		//					
		//					// Iterating over each row in the current column.
		//					for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//						
		//						// This is q_{fixedColumnIndex} * (1 / numberOfRows) =: F_{rowIndex, fixedColumnINdex}. 
		//						// The probability of the occurrence of the fixedColumnIndex and the rowIndex assuming 
		//						// an independent distribution of the marginals.
		//						currentIndependentCommonDistributionWeight = pathProbability * defaultProbabilities[ numberOfColumns - 1 ]; 
		//						
		//						// Multiply the current entry by the penalty factor and apply exp then multiply with F_{rowIndex, fixedColumnIndex}.
		//						matrixA[rowIndex][ numberOfColumns - 1 ] = Math.exp( penaltyFactor * currentEntry ) * currentIndependentCommonDistributionWeight;
		//						
		//					}
		//					
		//					return 0.0;
		//				} 
		//				
		//			});
		//			
		//			// Second all columns except the last one of the matrix are assigned.
		//			for(int columnIndex = 0; columnIndex < numberOfColumns - 1; columnIndex++) {
		//			
		//				final int fixedColumnIndex = columnIndex;
		//				
		//				callables.add( new Callable<Double>() { 
		//					public Double call() {
		//						
		//						double currentEntry = 0.0;
		//						
		//						double currentIndependentCommonDistributionWeight = 0.0;
		//						
		//						// Speeds up performance significantly.
		//						double[] currentColumn = discountedFlooredNPV[fixedColumnIndex + 1].getRealizations();
		//						
		//						// Iterating over each row in the current column.
		//						for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//							
		//							// First the value of the process at a given time index and realization is fetched.
		//							// This is the value that would be lost if the default occurred at this time index on 
		//							// this path with a loss given default of 1.
		//							currentEntry = currentColumn[rowIndex];
		//							
		//							// This is q_{fixedColumnIndex} * (1 / numberOfRows) =: F_{rowIndex, fixedColumnINdex}. 
		//							// The probability of the occurrence of the fixedColumnIndex and the rowIndex assuming 
		//							// an independent distribution of the marginals.
		//							currentIndependentCommonDistributionWeight = pathProbability * defaultProbabilities[fixedColumnIndex]; 
		//							
		//							// Multiply the current entry by the penalty factor and apply exp then multiply with F_{rowIndex, fixedColumnINdex}.
		//							matrixA[rowIndex][fixedColumnIndex] = Math.exp( penaltyFactor * currentEntry ) * currentIndependentCommonDistributionWeight;
		//							
		//						}
		//						
		//						return 0.0;}
		//				});
		//			
		//			}
			//	
			//	
		//			// Waits until each thread executed the call code. TODO: Error handling
		//			executor.invokeAll(callables);
		//			
		//			
		//			////
		//			// Setup for the row-wise iteration step.
		//			////
		//			
		//			// The number of blocks into which the matrix is split. When each block has at most rowPerBlock rows.
		//			// If the number of rows is not divisible by the number of threads one additional block is needed
		//			// to cover the whole matrix.
		//			final int numberOfMatrixBlocks = (numberOfRows % numberOfThreads) == 0 ? numberOfThreads: numberOfThreads + 1;
		//			
		//			// The number of rows each block has.
		//			final int rowsPerBlock = numberOfRows / numberOfThreads;
		//			
		//			
		//			////
		//			// Preparations for Multithreading
		//			////
		//			
		//			callables = new HashSet<Callable<Double>>();
			//
		//			
		//			////
		//			// Performing the Iteration Steps
		//			////
		//			
		//			
		//			//TODO: Add abort criterion if the marginal distributions reached the wanted distribution. Is that the right criterion?
		//			
		//			for(int iterationIndex = 0; iterationIndex < 10; iterationIndex++) {
		//				
		//				////
		//				// Row-wise Renormalization Step
		//				////
		//				
		//				// Calculating the row operations block-wise.
		//				// Each block is treated by a thread.
		//				
		//				callables = new HashSet<Callable<Double>>();
		//				
		//				// Iterating over all blocks. 
		//				for(int blockIndex = 0; blockIndex < numberOfMatrixBlocks; blockIndex++) {
		//				
		//					// The matrix-index of the first row in the current block.
		//					final int fixedIndexFirstRowOfBlock = blockIndex * rowsPerBlock;
		//					// If we are in the last block the rows only iterate to the final row of the
		//					// whole matrix.
		//					final int fixedIndexLastRowOfBlockPlusOne = ( blockIndex != (numberOfMatrixBlocks - 1) ) ? (blockIndex+1) * rowsPerBlock : numberOfRows;
		//					
		//					callables.add(new Callable<Double>() {
		//						
		//						public Double call() {
		//							
		//							////
		//							// In the first step all rows are summed. 
		//							// And each entry of the current row is divided by the sum of the current row.
		//							////
		//							
		//							// Variables for Kahan summation
		//							double currentRowSum = 0.0;
		//							double helperNextSum = 0.0;
		//							
		//							double nextToAdd = 0.0;
		//							double negativLostDigitsStorage = 0.0;
		//							
		//							// Iterating over each row in the current block
		//							for(int rowIndex = fixedIndexFirstRowOfBlock; rowIndex < fixedIndexLastRowOfBlockPlusOne; rowIndex++ ) {
		//								
		//								currentRowSum = 0.0;
		//								
		//								nextToAdd = 0.0;
		//								negativLostDigitsStorage = 0.0;
		//								helperNextSum = 0.0;
		//								
		//								for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
		//									
		//									nextToAdd = matrixA[rowIndex][columnIndex] - negativLostDigitsStorage;
		//									helperNextSum += nextToAdd;
		//									negativLostDigitsStorage = (helperNextSum - currentRowSum) - nextToAdd;
		//									currentRowSum = helperNextSum;
		//									
		//								}
		//								
			//// Debug-Code 
		////								if(rowIndex == 0) {
		////									System.out.println(" Row : " + rowIndex + "has running weight sum: " + currentRowSum);
		////								}
		//								
		//								for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
		//									matrixB[rowIndex][columnIndex] = matrixA[rowIndex][columnIndex] / currentRowSum * pathProbability;
		//								}
		//										
		//							}
		//							
		//							return currentRowSum;
		//						}
		//					});
		//				
		//				}
		//				
		//				// Waits until each thread executed the call code. TODO: Error handling
		//				executor.invokeAll(callables);
		//				
			//
		//				////
		//				// Column-wise Renormalization Step
		//				// All columns are summed.
		//				// Each column entry is divided by the summed current column.
		//				// The resulting value is then multiplied by the default probability 
		//				// associated with the current column.
		//				////
		//				
		//				// The set of Callables is reset.
		//				callables = new HashSet<Callable<Double>>();
		//				
		//				for(int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
		//					
		//					final double currentDefaultProbability = defaultProbabilities[columnIndex];
		//					
		//					final double[][] fixedMatrix = matrixB;
		//					
		//					final int fixedColumnIndex = columnIndex;
		//					
		//						callables.add( new Callable<Double>() {
		//							public Double call() throws SolverException {
		//								
		//								double runningColumnSum = 0;
		//								double helperNextSum = 0.0;
		//								
		//								double negativLostDigitsStorage = 0.0;
		//								double nextToAdd = 0.0;
		//							
		//								for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
			//
		//									nextToAdd = fixedMatrix[rowIndex][fixedColumnIndex] - negativLostDigitsStorage;
		//									helperNextSum += nextToAdd;
		//									negativLostDigitsStorage = ( helperNextSum - runningColumnSum ) - nextToAdd;
		//									runningColumnSum = helperNextSum;
		//									
		//								}
		//								
		//								for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//								
		//									matrixA[rowIndex][fixedColumnIndex] = matrixB[rowIndex][fixedColumnIndex] / runningColumnSum * currentDefaultProbability;
		//									
		//								}
		//								// TODO: Remove after debugging.
		//								System.out.println("Columnindex" + fixedColumnIndex + ", running probability weight sum: " + runningColumnSum);
		//								return 0.0;
		//							}
		//						}); 
			//
		//				}
		//				
		//				
		//				// Waits until each thread executed the call code. TODO: Error handling
		//				executor.invokeAll(callables);
			//
		//			}
		//			
		//			
		//			////
		//			// Calculating the CVA with respect to the calculated worst case distribution under constraint.
		//			////
		//			
		//			// The set of Callables is reset.
		//			callables = new HashSet<Callable<Double>>();
			//
		//			for(int columnIndex = 0; columnIndex < numberOfColumns - 1; columnIndex++) {
		//			
		//				final int fixedColumnIndex = columnIndex;
		//				
		//				callables.add(new Callable<Double>() {
		//					public Double call() {
		//						
		//						System.out.println("Only column index: " + fixedColumnIndex);
		//						
		//						double runningSum = 0.0;
		//						double helperNextSum = 0.0;
		//						
		//						double nextToAdd = 0.0;
		//						double negativLostDigitsStorage = 0.0;
		//						
		//						double[] currentColumn = discountedFlooredNPV[fixedColumnIndex + 1].getRealizations();
		//						
		//						for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
		//					
		//							nextToAdd = currentColumn[rowIndex] * matrixA[rowIndex][fixedColumnIndex] 
		//									- negativLostDigitsStorage;
		//							helperNextSum = runningSum + nextToAdd;
		//							negativLostDigitsStorage = ( helperNextSum - runningSum ) - nextToAdd;
		//							runningSum = helperNextSum;
		//							
		//						}
		//						System.out.println("Column " + fixedColumnIndex + " has running cva sum: " + runningSum);
		//						return runningSum;
		//						
		//					}
		//				});
			//
		//			}
		//			
		//			// The partial sums are initialized and fetched. 
		//			List<Future<Double>> partialSums = new ArrayList<Future<Double>>();
		//			partialSums = executor.invokeAll(callables);
		//			
		//			
		//			////
		//			// The partial sums are added together.
		//			////
			//	
		//			double worstCaseCVAUnderConstraint = 0.0;
			//	
		//			for(int columnIndex = 0; columnIndex < numberOfColumns - 1 /* The last column is the zero column */; columnIndex++) {
		//				
		//				worstCaseCVAUnderConstraint += partialSums.get(columnIndex).get();
		//				
		//			}
		//			
		//			
		//			////
		//			// The executor has to be shutdown before leaving the method.
		//			// Otherwise the threads will continue to run.
		//			////
		//			
		//			try {
		//				executor.shutdown();
		//			}
		//			catch(SecurityException e) {
		//				// TODO: Improve exception handling here
		//			}
		//			
		//			return worstCaseCVAUnderConstraint;
			//	
		//		}
	
	
}
