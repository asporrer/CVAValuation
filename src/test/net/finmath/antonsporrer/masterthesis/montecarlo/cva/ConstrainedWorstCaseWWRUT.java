/* 
 * Contact: anton.sporrer@yahoo.com
 */

package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.ConstrainedWorstCaseCVA;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * Unit test for {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.ConstrainedWorstCaseCVA #getWorstCaseCVANotToFarFromIndependence}
 * 
 * @author Anton Sporrer
 *
 */
public class ConstrainedWorstCaseWWRUT {

	private int numberOfSimulationPaths;
	private RandomVariableInterface[] discountedFlooredNPV;
	private double[] defaultProbabilities;
	
	private double terminationCriterionRows;
	private double terminationCriterionColumns;
	private double terminationCriterionColumnsAbsolut;
	
	private double penaltyFactor;
	private ConstrainedWorstCaseCVA constrainedWorstCaseCVA = new ConstrainedWorstCaseCVA(1.0);
	
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	@Test 
	public void throwsIllegalArgumentExceptionIfOneValueAndUnbiased() {
//		exception.expect(IllegalArgumentException.class);
//		CVAHullWhiteCIRConvergenceTest.getArithmeticMeans( new double[] {1.0, 3.0, 3.0, 7.0}, 1, 4, 1 );
	}
	
	
	@Test
	public void testBoundrySingleInput() throws InterruptedException, ExecutionException {
		numberOfSimulationPaths = 2;
		discountedFlooredNPV = new RandomVariable[] { new RandomVariable(0.0, new double[] { 0, 10 }) };
		defaultProbabilities = new double[] {0.5, 0.5};
		
		terminationCriterionRows = 1.0E-10;
		terminationCriterionColumns = 1.0E-10;
		terminationCriterionColumnsAbsolut = 1.0E-10;
		
		penaltyFactor = 0.00001;
		double constrWorstCaseCVA = constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut);
		System.out.println("Boundry Case (only two paths and one barrier probability):");
		System.out.println("CWCWWR for the penalty factor " + penaltyFactor + " is: " + constrWorstCaseCVA );
		System.out.println("The desired CWCWWR value is close to the expectation under the independent joint distribution: 2.5 ");
		Assert.assertEquals(2.5, constrWorstCaseCVA, 0.001);
		
		penaltyFactor = 10;
		constrWorstCaseCVA = constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut);
		
		System.out.println("CWCWWR for the penalty factor " + penaltyFactor + " is: " + constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut) );
		System.out.println("The desired CWCWWR value is close to the expectation under the worst case joint distribution: 5 ");
		Assert.assertEquals(5.0, constrWorstCaseCVA, 0.03);
		
	}
	
	
	
	/**
	 * Simple Test for which the exact solution is known.
	 */
	@Test
	public void testExample1() throws InterruptedException, ExecutionException {
		
		numberOfSimulationPaths = 2;
		discountedFlooredNPV = new RandomVariable[] { new RandomVariable(0.0, new double[] {10, 0}) , new RandomVariable(0.0, new double[] {0, 10}) };
		defaultProbabilities = new double[] {0.5, 0.48, 0.02};
		
		terminationCriterionRows = 1.0E-10;
		terminationCriterionColumns = 1.0E-10;
		terminationCriterionColumnsAbsolut = 1.0E-10;
		
		penaltyFactor = 0.00001;
		double constrWorstCaseCVA = constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut);
		System.out.println("--- I.) Test Case row1 = (10,0), row2 = (0,10): ---");
		System.out.println("CWCWWR for the penalty factor " + penaltyFactor + " is: " + constrWorstCaseCVA );
		System.out.println("The desired CWCWWR value is close to the expectation under the independent joint distribution: 4.9 ");
		Assert.assertEquals(4.9, constrWorstCaseCVA, 0.001);
		
		
		penaltyFactor = 10;
		constrWorstCaseCVA = constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut);
		
		System.out.println("CWCWWR for the penalty factor " + penaltyFactor + " is: " + constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut) );
		System.out.println("The desired CWCWWR value is close to the expectation under the worst case joint distribution: 9.8 ");
		Assert.assertEquals(9.8, constrWorstCaseCVA, 0.001);

	}
	
	
	
	/**
	 * Simple Test for which the exact solution is known.
	 */
	@Test
	public void testExample2() throws InterruptedException, ExecutionException {
		
		numberOfSimulationPaths = 2;
		discountedFlooredNPV = new RandomVariable[] { new RandomVariable(0.0, new double[] {10, 0}) , new RandomVariable(0.0, new double[] {5, 10}) };
		defaultProbabilities = new double[] {0.2, 0.76, 0.04};
		
		terminationCriterionRows = 1.0E-10;
		terminationCriterionColumns = 1.0E-10;
		terminationCriterionColumnsAbsolut = 1.0E-10;
		
		penaltyFactor = 0.0001;
		double constrWorstCaseCVA = constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut);
		System.out.println("--- II.) Test Case row1 = (10,5), row2 = (0,10): ---");
		System.out.println("CWCWWR for the penalty factor " + penaltyFactor + " is: " + constrWorstCaseCVA );
		System.out.println("The desired CWCWWR value is close to the expectation under the independent joint distribution: 6.7" );
		Assert.assertEquals(6.7, constrWorstCaseCVA, 0.001);
		
		
		penaltyFactor = 10;
		constrWorstCaseCVA = constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut);
		
		System.out.println("CWCWWR for the penalty factor " + penaltyFactor + " is: " + constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, terminationCriterionRows, terminationCriterionColumns, terminationCriterionColumnsAbsolut) );
		System.out.println("The desired CWCWWR value is close to the expectation under the worst case joint distribution: 8.3 ");
		Assert.assertEquals(8.3, constrWorstCaseCVA, 0.001);

	}
	
	@Test
	public void testExample3() {

		numberOfSimulationPaths = 1000000;
		
		int numberOfDefaultProbabilities = 20;
		
		defaultProbabilities = new double[numberOfDefaultProbabilities];
		
		discountedFlooredNPV = new RandomVariable[numberOfDefaultProbabilities-1];
		
		defaultProbabilities[numberOfDefaultProbabilities - 1] = 1.0/numberOfDefaultProbabilities;
		
		Random randomNumberGenerator = new Random(1335);
		
		for(int defaultIndex = 0; defaultIndex < numberOfDefaultProbabilities - 1; defaultIndex++) {
			
			defaultProbabilities[defaultIndex] = 1.0/numberOfDefaultProbabilities;
			
			double time = defaultIndex + 1;
			
			double[] realisations = new double[numberOfSimulationPaths];
			
			for(int pathIndex = 0; pathIndex < numberOfSimulationPaths; pathIndex++) {
				
				realisations[pathIndex] = randomNumberGenerator.nextDouble()*10;
				
			}
			
			discountedFlooredNPV[defaultIndex] = new RandomVariable(time, realisations);
			
		}
		
		 
		try {
			
			
			System.out.println("--- III.) 1 Mio Random Pahts Test ---");
			double startTime = System.currentTimeMillis();
			penaltyFactor = 0.0001;
			double result = constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, 0.00000001, 0.000000001, 0.00000000000001 );
			System.out.println("The result should be very close to 4.75 but a bit greater. The result is: " + result);
			Assert.assertEquals(4.75, result, 0.01);
			
			penaltyFactor = 60;
			result = constrainedWorstCaseCVA.getWorstCaseCVANotToFarFromIndependence(penaltyFactor, discountedFlooredNPV, numberOfSimulationPaths, defaultProbabilities, 0.00000001, 0.000000001, 0.00000000000001 );
			System.out.println("The result should be smaller than 9.75. The result is: " + result);
			Assert.assertEquals(9.95, result, 1);
			
			
			double endTime = System.currentTimeMillis();
			
			System.out.println("Time passed: " + (endTime - startTime));
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

	
}
