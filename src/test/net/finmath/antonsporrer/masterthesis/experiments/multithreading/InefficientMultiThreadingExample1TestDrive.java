package test.net.finmath.antonsporrer.masterthesis.experiments.multithreading;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.ConstrainedWorstCaseCVA;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.optimizer.SolverException;
import net.finmath.stochastic.RandomVariableInterface;

public class InefficientMultiThreadingExample1TestDrive {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		ConstrainedWorstCaseCVA worstCaseCVA = new ConstrainedWorstCaseCVA(1.0);
		
		int numberOfPaths = 1000000;
		
		int numberOfDefaultProbabilities = 10;
		
		double[] defaultProbabilities = new double[numberOfDefaultProbabilities];
		
		RandomVariableInterface[] discountedFlooredNPV = new RandomVariable[numberOfDefaultProbabilities];
		
		defaultProbabilities[numberOfDefaultProbabilities - 1] = 1.0/numberOfDefaultProbabilities;
		
		for(int defaultIndex = 0; defaultIndex < numberOfDefaultProbabilities - 1; defaultIndex++) {
			defaultProbabilities[defaultIndex] = 1.0/numberOfDefaultProbabilities;
			
			double time = defaultIndex + 1;
			
			double[] realisations = new double[numberOfPaths];
			
			for(int pathIndex = 0; pathIndex < numberOfPaths; pathIndex++) {
				
				realisations[pathIndex] = 1.0;
				
			}
			
			discountedFlooredNPV[defaultIndex] = new RandomVariable(time, realisations);
			
		}
		
		// We do not allocate more threads the twice the number of processors.
		int numberOfThreads1 = Math.min(Math.max(2 * Runtime.getRuntime().availableProcessors(),1),defaultProbabilities.length);
		
		int numberOfThreads2 = 1;
		
		double startTime = System.currentTimeMillis();
		
		double[][] result = getWorstCaseCVANotToFarFromIndependence(0.5, discountedFlooredNPV, numberOfPaths, defaultProbabilities, numberOfThreads1);

		double endTime = System.currentTimeMillis();
		
		System.out.println("Calculation Time for Multithreading: " + (endTime - startTime));
		
		
		
		startTime = System.currentTimeMillis();
		
		double[][] result2 = getWorstCaseCVANotToFarFromIndependence(0.5, discountedFlooredNPV, numberOfPaths, defaultProbabilities, numberOfThreads2);

		endTime = System.currentTimeMillis();
		
		System.out.println("Calculation Time for one multithread: " + (endTime - startTime));
	}

	
	/**
	 * 
	 * This method uses the iterartive proportional fitting procedure (IPFP) to calculate the worst case CVA under a constraint.
	 * The constraint is that the eligible distributions must not differ too much from the following target distribution. The target
	 * distribution is the independent distribution of the discountedFlooredNPV and the default.
	 * 
	 * @param penaltyFactor This factor determines how much the worst case distribution is allowed to differ from the independent distribution. 
	 * @param discountedFlooredNPV (Numeraire(0)/Numeraire(t_i) * (NPV(t_i))<sup> + </sup>)<sub> i = 1, ... , d </sub>
	 * @param numberOfSimulationPaths The number of simulated paths.
	 * @param defaultProbabilities The default probabilities in the time intervalls  (t_0, t_{1}], ... , (t_{d-1}, t_d], (t_d, infinity).
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static double[][] getWorstCaseCVANotToFarFromIndependence(final double penaltyFactor, final RandomVariableInterface[] discountedFlooredNPV, final int numberOfSimulationPaths, final double[] defaultProbabilities, int numberOfThreads ) throws InterruptedException, ExecutionException {
		
		////
		// Calculating the Initial Matrix for the IPFP.
		////
		
		// The number of columns and the number of rows are fetched.
		final int numberOfColumns = defaultProbabilities.length;
		final int numberOfRows = numberOfSimulationPaths;
		
		// The initial matrix is allocated.
		final double[][] initialMatrix = new double[numberOfRows][numberOfColumns];
		
		// First the initial matrix is assigned as follows. Where F_{i,j} := q_{j} * (1 / numberOfRows)
		// initialMatrix[i][j] = exp( penaltyFactor * discountedFlooredNPV[i][j] ) * F_{i,j}.
//		 In a second step all column sums are summed up and every entry of the matrix is divided by this 
//		 resulting sum. Thereby the matrix is normed.
		
		

		
		// An ExecutorService is declared and initialized to handle the threads.
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		
		// A set of futures which is used to collect the different futures returned by the executor.
		// When invoking get on these futures (this is like join and some get method applied to a thread)
		// the main thread stops and waits until the corresponding thread is finished.
		Set<Future<Double>> columnSums = new HashSet<Future<Double>>();
		
		// The columns of the initial matrix are assigned and summed in parallel.
		// Whereas the last column of the initial matrix is the zero column.
		for(int columnIndex = 0; columnIndex < numberOfColumns - 1 /* the last column is a zero column */; columnIndex++) {
			
			// Has to be final to use multi-threading.
			final int fixedColumnIndex = columnIndex;
			
			// Shortcut for submitting an object implementing callable<Double> to the executor 
			// TODO: Use lambda notation. Use Runnable to implement.
			Future<Double> columnSum = executor.submit( new Callable<Double>() {
				public Double call() throws SolverException {
					
					double[] currentRow = discountedFlooredNPV[fixedColumnIndex].getRealizations();
					
					// Storing the partial sum results.
					double runningColumnSum = 0;
					double result = 0.0;
					// Iterating over each row in the current column.
					for(int rowIndex = 0; rowIndex < numberOfRows; rowIndex++) {
						
						// First the value of the process at a given time index and realization is fetched.
						// This is the value that would be lost if the default occured at this time index on 
						// this path with a loss given default of 1.
						double currentEntry = currentRow[rowIndex];// discountedFlooredNPV[fixedColumnIndex].getRealizations()[rowIndex]; 
						
						// This is q_{fixedColumnIndex} * (1 / numberOfRows) =: F_{rowIndex, fixedColumnINdex}. 
						// The probability of the occurrence of the fixedColumnIndex and the rowIndex assuming 
						// an independent distribution of the marginals.
						double currentIndependentCommonDistributionWeight = numberOfRows * defaultProbabilities[fixedColumnIndex]; 
						
						// Multiply the current entry by the penalty factor and apply exp then multiply with F_{rowIndex, fixedColumnINdex}.
					
						 initialMatrix[rowIndex][fixedColumnIndex] = Math.exp( penaltyFactor * currentEntry ) * currentIndependentCommonDistributionWeight;
						
//						double[][] assignmentTestMatrix = initialMatrix;
//						assignmentTestMatrix[1][1] = 0.0;
//						
//						 for(int index = 0; index < 1000; index++) {
//							 result += Math.exp(index) + initialMatrix[rowIndex][fixedColumnIndex];
//						 }
						   
					}
					
					// At the moment not supported. Runnable could be used.
					// Originally this was intended to return the sum of the 
					// current column. But this sum is not needed.
					return result;
					
				}
			});
			
			// TODO: Remove
			columnSums.add(columnSum);
			
		}
		
		// TODO:
		double testSum = 0.0;
		
		for(Future<Double> item: columnSums) {
			testSum += item.get();
		}
		
		
		
		try {
			executor.shutdown();
		}
		catch(SecurityException e) {
			// TODO: Improve exception handling here
		}
	
		// TODO: Test if useful!
		try {
			  executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
			  
			}
		
			
		// TODO: Calculate the CVA	
		return initialMatrix;
		
	}
	
	
}
