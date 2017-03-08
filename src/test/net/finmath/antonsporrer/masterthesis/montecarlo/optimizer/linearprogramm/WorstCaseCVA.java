package test.net.finmath.antonsporrer.masterthesis.montecarlo.optimizer.linearprogramm;

import java.util.function.ToDoubleBiFunction;

import net.finmath.stochastic.RandomVariableInterface;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class WorstCaseCVA {

	// Has dimension numberOfFairValuePaths times numberOfDefaultTimeIntervals.
	double[][] fairValuePaths;
	double[] defaultProbabilities;
	
	// The number of simulation paths of the fair value of the product of interest.
	int numberOfFairValuePaths;
	
	// The number of intervals used to discretize the default probability 
	// including the interval to infinity.
	int numberOfDefaultTimeIntervals;
	
	// Auxiliary array necessary for the call of the lp solve method.
	double[] fairValuesIncludingZero;
	
	// ToDo: Also store the worst case distribution.
	double[] worstCaseDistribution;
	double worstCaseCVA;
	boolean isWorstCaseCVACalculated;
	
	public WorstCaseCVA(double[][] fairValuePaths, double[] defaultProbabilites) {
		isWorstCaseCVACalculated = false;
		
		this.fairValuePaths= fairValuePaths;
		this.defaultProbabilities = defaultProbabilites;
		this.numberOfFairValuePaths = fairValuePaths.length;
		this.numberOfDefaultTimeIntervals = fairValuePaths[0].length;
		
	}
	
	
	/**
	 * 
	 * 
	 * @return The worst case CVA.
	 * @throws LpSolveException
	 */
	public double getWorstCaseCVA() throws LpSolveException {
		
		if(isWorstCaseCVACalculated == false) { 
			calculateWorstCaseCVA();
		}
		
		return worstCaseCVA;
	}
	
	private void calculateWorstCaseCVA() throws LpSolveException {
	
		fairValuesIncludingZero = simulationPathsToOneBigLPRow(fairValuePaths, numberOfDefaultTimeIntervals, numberOfFairValuePaths);
		
		// What if very small?
		double weightOnOnePath = 1.0/numberOfFairValuePaths;

		int[][] indicesOfWholeCurrentRow = new int[numberOfFairValuePaths][numberOfDefaultTimeIntervals];
		
		for(int rowIndex = 0; rowIndex < numberOfFairValuePaths; rowIndex++) {
			for(int columnIndex = 0; columnIndex < numberOfDefaultTimeIntervals; columnIndex++) {
				indicesOfWholeCurrentRow[rowIndex][columnIndex] = rowIndex*numberOfDefaultTimeIntervals + columnIndex + 1;
			}
		}
		
		double[] onesForRow = new double[numberOfDefaultTimeIntervals];
		
		for(int index = 0; index < numberOfDefaultTimeIntervals; index++) {
			onesForRow[index] = 1.0;
		}
		
		int[][] indicesOfWholeCurrentColumn = new int[numberOfDefaultTimeIntervals][numberOfFairValuePaths];
		
		for(int rowIndex = 0; rowIndex < numberOfFairValuePaths; rowIndex++) {
			for(int columnIndex = 0; columnIndex < numberOfDefaultTimeIntervals; columnIndex++) {
				indicesOfWholeCurrentColumn[columnIndex][rowIndex] = rowIndex*numberOfDefaultTimeIntervals + columnIndex + 1;
			}
		}
		
		double[] onesForColumn = new double[numberOfFairValuePaths];
		
		for(int index = 0; index < numberOfFairValuePaths; index++) {
			onesForColumn[index] = 1.0;
		}
		
		LpSolve problem = LpSolve.makeLp(0, numberOfDefaultTimeIntervals * numberOfFairValuePaths);
		
		problem.setAddRowmode(true);
		
		for(int indexPath = 0; indexPath < numberOfFairValuePaths; indexPath++) {
			problem.addConstraintex(numberOfDefaultTimeIntervals, onesForRow, indicesOfWholeCurrentRow[indexPath], LpSolve.EQ, weightOnOnePath);
		}
		
		for(int indexDefaultEvent = 0; indexDefaultEvent < numberOfDefaultTimeIntervals; indexDefaultEvent++){
			problem.addConstraintex(numberOfFairValuePaths, onesForColumn, indicesOfWholeCurrentColumn[indexDefaultEvent], LpSolve.EQ, defaultProbabilities[indexDefaultEvent]);
		}
		
		problem.setAddRowmode(false);
		
		
		// Setting up the objective function.
		
		int[] completeMatrix = new int[numberOfFairValuePaths*numberOfDefaultTimeIntervals];
		
		for(int index = 0; index < numberOfFairValuePaths*numberOfDefaultTimeIntervals; index++) {
			completeMatrix[index] = index + 1;
		}

		problem.setObjFnex(numberOfFairValuePaths*numberOfDefaultTimeIntervals, fairValuesIncludingZero, completeMatrix);
		
		/* set the object direction to maximize */
        problem.setMaxim();
		
        
        /* ToDo:  */
        problem.setVerbose(LpSolve.IMPORTANT);
		
                
        /* ToDo: ret. Now let lpsolve calculate a solution */
        int ret = problem.solve();
        
        
        worstCaseCVA = problem.getObjective();
        
        System.out.println("Objective value: " + worstCaseCVA);
        
        
        /* variable values */
        worstCaseDistribution = new double[ numberOfFairValuePaths * numberOfDefaultTimeIntervals ];
        problem.getVariables(worstCaseDistribution);
        for(int index = 0; index < worstCaseDistribution.length; index++) {
        	System.out.println("Variable Index "+ index + " : " + worstCaseDistribution[index]);
        }
		
        
        // ToDo
        if(problem.getLp() != 0) {
        	problem.deleteLp();
        }
        
        
        isWorstCaseCVACalculated = true;
		
	}
	
	

	/**
	 * 
	 * @param fairValuePaths 
	 * @param numberOfDefaultTimeIntervals Including infinity
	 * @param numberOfFairValuePaths
	 * @return
	 */
	public static double[] simulationPathsToOneBigLPRow(double[][] fairValuePaths, int numberOfDefaultTimeIntervals, int numberOfFairValuePaths) {
		
		double[] oneBigLPRow = new double[numberOfDefaultTimeIntervals*numberOfFairValuePaths];
		
		for(int timeIndex = 0; timeIndex < numberOfDefaultTimeIntervals-1; timeIndex++) {
			
			for(int pathIndex = 0; pathIndex < numberOfFairValuePaths; pathIndex++) {
				
				oneBigLPRow[ timeIndex + pathIndex*numberOfDefaultTimeIntervals ] = fairValuePaths[pathIndex][timeIndex];
				
			}
			
		}
		
		for(int pathIndex = 0; pathIndex < numberOfFairValuePaths; pathIndex++) {
			
			oneBigLPRow[pathIndex*numberOfDefaultTimeIntervals + numberOfDefaultTimeIntervals-1] = 0.0;
			
		}
		
		return oneBigLPRow;
		
	}
	
	
	
}
