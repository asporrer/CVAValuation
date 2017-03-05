package test.net.finmath.antonsporrer.masterthesis.montecarlo.optimizer.linearprogramm;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class LPSolverProblemSetUp {

	public static void main(String[] args) throws LpSolveException {
		
		double[] defaultProbabilities = new double[] {0.2, 0.3, 0.4, 0.1};
		double[][] fairValues = new double[][] {{1,2,3},{3,5,9}};
		
		LpSolve problem = LpSolve.makeLp(0, 8);
		
		
		int[][] allDefaultEventsForOnePathEvent = new int[][] {{1,2,3,4},{5,6,7,8}};
		int[][] allSimulationEventsForOneDefaultEvent = new int[][] {{1,5},{2,6}, {3,7}, {4,8}};
		double[] fourOnes = new double[] {1.0,1.0,1.0,1.0};
		double[] twoOnes = new double[] {1,1};
		
		for(int indexPath = 0; indexPath < fairValues.length; indexPath++) {
		problem.addConstraintex(4, fourOnes, allDefaultEventsForOnePathEvent[indexPath], LpSolve.EQ, 0.5);
		}
		
		
		for(int indexDefaultEvent = 0; indexDefaultEvent<defaultProbabilities.length; indexDefaultEvent++){
			problem.addConstraintex(2, twoOnes, allSimulationEventsForOneDefaultEvent[indexDefaultEvent], LpSolve.EQ, defaultProbabilities[indexDefaultEvent]);
		}
		
	}

}
