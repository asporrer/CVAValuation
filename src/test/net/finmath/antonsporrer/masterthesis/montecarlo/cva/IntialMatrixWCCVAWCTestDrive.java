package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.ConstrainedWorstCaseCVA;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;

public class IntialMatrixWCCVAWCTestDrive {


public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		ConstrainedWorstCaseCVA worstCaseCVA = new ConstrainedWorstCaseCVA(1.0);
		
		int numberOfPaths = 1000000;
		
		int numberOfDefaultProbabilities = 20;
		
		double[] defaultProbabilities = new double[numberOfDefaultProbabilities];
		
		RandomVariableInterface[] discountedFlooredNPV = new RandomVariable[numberOfDefaultProbabilities];
		
		defaultProbabilities[numberOfDefaultProbabilities - 1] = 1.0/numberOfDefaultProbabilities;
		
		Random randomNumberGenerator = new Random(1335);
		
		for(int defaultIndex = 0; defaultIndex < numberOfDefaultProbabilities - 1; defaultIndex++) {
			defaultProbabilities[defaultIndex] = 1.0/numberOfDefaultProbabilities;
			
			double time = defaultIndex + 1;
			
			double[] realisations = new double[numberOfPaths];
			
			for(int pathIndex = 0; pathIndex < numberOfPaths; pathIndex++) {
				
				realisations[pathIndex] = randomNumberGenerator.nextDouble()*10;
				
			}
			
			discountedFlooredNPV[defaultIndex + 1] = new RandomVariable(time, realisations);
			
		}
		
		System.out.println(randomNumberGenerator.nextDouble()*10);
		double startTime = System.currentTimeMillis();
		
		
		// For testing this method has to be set to public.
		double result = worstCaseCVA.getWorstCaseCVANotToFarFromIndependence(0.11, discountedFlooredNPV, numberOfPaths, defaultProbabilities, 0.00000001, 0.000000001, 0.00000000000001 );
		
		double endTime = System.currentTimeMillis();
		
		System.out.println("Time passed: " + (endTime - startTime));
		System.out.println("Result: " + result);
		
	}

}
