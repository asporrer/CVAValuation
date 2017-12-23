//package test.net.finmath.antonsporrer.masterthesis.montecarlo.optimizer.linearprogramm;
//
//import java.util.Random;
//
//import lpsolve.LpSolveException;
//
//public class WorstCaseCVATestDrive {
//
//	public static void main(String[] args) throws LpSolveException {
//	
//		int numberOfFairValuePaths = 3;
//		int numberOfDefaultTimeIntervals = 3;
//		
//		double[] defaultProbabilites = new double[numberOfDefaultTimeIntervals];
//		double[][] fairValuePaths = new double[numberOfFairValuePaths][numberOfDefaultTimeIntervals];  
//		
//		for(int columnIndex = 0; columnIndex < numberOfDefaultTimeIntervals; columnIndex++) {
//			defaultProbabilites[columnIndex] = 1.0/numberOfDefaultTimeIntervals;
//		}
//		
//		for(int rowIndex = 0; rowIndex < numberOfFairValuePaths; rowIndex++) {
//			for(int columnIndex = 0; columnIndex < numberOfDefaultTimeIntervals; columnIndex++) {
//				fairValuePaths[rowIndex][columnIndex] = 1.0;
//			}
//		}
//		
//		WorstCaseCVA worstCaseCVA = new WorstCaseCVA(fairValuePaths, defaultProbabilites);
//		
//		System.out.println( worstCaseCVA.getWorstCaseCVA()  );
//		
//	}
//
//}
