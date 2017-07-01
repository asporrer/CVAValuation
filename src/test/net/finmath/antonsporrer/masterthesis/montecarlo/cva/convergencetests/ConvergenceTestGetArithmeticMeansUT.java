/* 
 * Contact: anton.sporrer@yahoo.com
 */

package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.convergencetests;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cern.colt.Arrays;
import main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions;
import main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions.EmpiricalVarianceVersion;


/**
 * 
 * Unit test for {@link test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.convergencetests.CVAHullWhiteCIRConvergencePlausibilityTest #getArithmeticMeans(double[] , int , int , int )}
 * 
 * @author Anton Sporrer
 *
 */
public class ConvergenceTestGetArithmeticMeansUT {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testBoundrySingleInput() {
		Assert.assertEquals(1337.0, CVAHullWhiteCIRConvergencePlausibilityTest.getArithmeticMeans( new double[] {1337.0}, 0, 1, 1 )[0], 1E-7);
	}
	
	@Test 
	public void throwsIllegalArgumentExceptionIfOneValueAndUnbiased() {
		exception.expect(IllegalArgumentException.class);
		CVAHullWhiteCIRConvergencePlausibilityTest.getArithmeticMeans( new double[] {1.0, 3.0, 3.0, 7.0}, 1, 4, 1 );
	}
	
	@Test
	public void testExample1() {
		
		double[] arithmeticmeans1 = CVAHullWhiteCIRConvergencePlausibilityTest.getArithmeticMeans(new double[] {-5.2, 1.0, 3.2, 7.2, 6.5}, 0, 2, 2 );
		Assert.assertArrayEquals(new double[] {-2.1, 5.2 }, arithmeticmeans1, 1E-7);
		System.out.println(Arrays.toString(arithmeticmeans1));
		
		double[] arithmeticmeans2 = CVAHullWhiteCIRConvergencePlausibilityTest.getArithmeticMeans(new double[] {-5.4, 1.0, 3.3, 7.2, 6.5}, 0, 1, 5 );
		Assert.assertArrayEquals(new double[] {2.52}, arithmeticmeans2, 1E-7);
		System.out.println(Arrays.toString(arithmeticmeans2));
	}
	
}
