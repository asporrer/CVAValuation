package test.net.finmath.antonsporrer.masterthesis.montecarlo.optimizer.linearprogramm;

import net.finmath.stochastic.RandomVariableInterface;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class LPSolverProblemSetUp {

	public static void main(String[] args) throws LpSolveException {
		
		// Has to be provided.
		int numberOfDefaultEvents = 4;
		
		// Has to be provided.
		int numberOfFairValuePaths = 2;
		
		// Has to be provided.
		double[] defaultProbabilities = new double[numberOfDefaultEvents];
		
		// Has to be provided.
		double[] fairValuesIncludingZero = new double[numberOfFairValuePaths*numberOfDefaultEvents];
		
		// What if very small?
		double weightOnOnePath = 1.0/numberOfFairValuePaths;
		
		//Example
		defaultProbabilities = new double[] {0.1, 0.1, 0.7, 0.1};
		
		fairValuesIncludingZero = new double[]{ 2.0, 3.0, 4.0, 0.0 , 1.0, 3.0, 2.0, 0.0 };
		 
		int[][] indicesOfWholeCurrentRow = new int[numberOfFairValuePaths][numberOfDefaultEvents];
		
		for(int rowIndex = 0; rowIndex < numberOfFairValuePaths; rowIndex++) {
			for(int columnIndex = 0; columnIndex < numberOfDefaultEvents; columnIndex++) {
				indicesOfWholeCurrentRow[rowIndex][columnIndex] = rowIndex*numberOfDefaultEvents + columnIndex + 1;
			}
		}
		
		double[] onesForRow = new double[numberOfDefaultEvents];
		
		for(int index = 0; index < numberOfDefaultEvents; index++) {
			onesForRow[index] = 1.0;
		}
		
		int[][] indicesOfWholeCurrentColumn = new int[numberOfDefaultEvents][numberOfFairValuePaths];
		
		for(int rowIndex = 0; rowIndex < numberOfFairValuePaths; rowIndex++) {
			for(int columnIndex = 0; columnIndex < numberOfDefaultEvents; columnIndex++) {
				indicesOfWholeCurrentColumn[columnIndex][rowIndex] = rowIndex*numberOfDefaultEvents + columnIndex + 1;
			}
		}
		
		double[] onesForColumn = new double[numberOfFairValuePaths];
		
		for(int index = 0; index < numberOfFairValuePaths; index++) {
			onesForColumn[index] = 1.0;
		}
		
		LpSolve problem = LpSolve.makeLp(0, numberOfDefaultEvents * numberOfFairValuePaths);
		
		problem.setAddRowmode(true);
		
		for(int indexPath = 0; indexPath < numberOfFairValuePaths; indexPath++) {
			problem.addConstraintex(numberOfDefaultEvents, onesForRow, indicesOfWholeCurrentRow[indexPath], LpSolve.EQ, weightOnOnePath);
		}
		
		for(int indexDefaultEvent = 0; indexDefaultEvent < numberOfDefaultEvents; indexDefaultEvent++){
			problem.addConstraintex(numberOfFairValuePaths, onesForColumn, indicesOfWholeCurrentColumn[indexDefaultEvent], LpSolve.EQ, defaultProbabilities[indexDefaultEvent]);
		}
		
		problem.setAddRowmode(false);
		
		
		// Setting up the objective function.
		
		int[] completeMatrix = new int[numberOfFairValuePaths*numberOfDefaultEvents];
		
		for(int index = 0; index < numberOfFairValuePaths*numberOfDefaultEvents; index++) {
			completeMatrix[index] = index + 1;
		}

		problem.setObjFnex(numberOfFairValuePaths*numberOfDefaultEvents, fairValuesIncludingZero, completeMatrix);
		
		/* set the object direction to maximize */
        problem.setMaxim();
		
        problem.setVerbose(LpSolve.IMPORTANT);
		
                
        /* Now let lpsolve calculate a solution */
        int ret = problem.solve();
        
        
        System.out.println("Objective value: " + problem.getObjective());
        
        /* variable values */
        double[] variables = new double[ numberOfFairValuePaths * numberOfDefaultEvents ];
        problem.getVariables(variables);
        for(int index = 0; index < variables.length; index++) {
        	System.out.println("Variable Index "+ index + " : " + variables[index]);
        }
		
        if(problem.getLp() != 0) {
        	problem.deleteLp();
        }
         
	}

	
	/**
	 * 
	 * This class provides an double array of the form {a<sub>0</sub>, ... , a<sub>numberOfTimeSteps - 2</sub> , 0 , a<sub> numberOfTimeSteps </sub>, ... , a<sub>2*numberOfTimeSteps - 2</sub> , 0, ... a<sub> numberOfTimeSteps * (numberOfPaths-1)  </sub>, ... , a<sub>numberOfTimeSteps * numberOfPaths - 2</sub> , 0    }
	 * 
	 * @param simulationPath
	 * @param numberOfTimes Including infinity.
	 * @param numberOfPaths 
	 * @return
	 */
	public static double[] simulationPathsToOneBigLPRow(RandomVariableInterface[] simulationPath, int numberOfTimes, int numberOfPaths) {
		
		double[] oneBigLPRow = new double[numberOfTimes*numberOfPaths];
		
		double[] currentSimulation = new double[numberOfPaths];
		
		for(int timeIndex = 0; timeIndex < numberOfTimes-1; timeIndex++) {
			currentSimulation = simulationPath[timeIndex].getRealizations();
			
			for(int pathIndex = 0; pathIndex < numberOfPaths; pathIndex++) {
				
				oneBigLPRow[pathIndex*numberOfTimes + timeIndex] = currentSimulation[pathIndex];
				
			}
			
		}
		
		for(int pathIndex = 0; pathIndex < numberOfPaths; pathIndex++) {
			
			oneBigLPRow[pathIndex*numberOfTimes + numberOfTimes-1] = 0.0;
			
		}
		
		return oneBigLPRow;
		
	}
	
	/**
	 * 
	 * @param simulationPaths
	 * @param numberOfTimes Including infinity
	 * @param numberOfPaths
	 * @return
	 */
	public static double[] simulationPathsToOneBigLPRow(double[][] simulationPaths, int numberOfTimes, int numberOfPaths) {
		
		double[] oneBigLPRow = new double[numberOfTimes*numberOfPaths];
		
		for(int timeIndex = 0; timeIndex < numberOfTimes-1; timeIndex++) {
			
			for(int pathIndex = 0; pathIndex < numberOfPaths; pathIndex++) {
				
				oneBigLPRow[ timeIndex + pathIndex*numberOfTimes ] = simulationPaths[timeIndex][pathIndex];
				
			}
			
		}
		
		for(int pathIndex = 0; pathIndex < numberOfPaths; pathIndex++) {
			
			oneBigLPRow[pathIndex*numberOfTimes + numberOfTimes-1] = 0.0;
			
		}
		
		return oneBigLPRow;
		
	}
	
	
}
