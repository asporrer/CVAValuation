/* 
 * Contact: anton.sporrer@yahoo.com
 */

package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.convergencetests;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * Unit test for {@link test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.convergencetests.CVAHullWhiteCIRConvergencePlausibilityTest #getIncreasingSampleSizeMeanArray(double[]) }
 * 
 * @author Anton Sporrer
 *
 */
public class GetIncreasingSampleSizeMeanArrayUT {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test 
	public void throwsIllegalArgumentExceptionTest() {
		exception.expect(IllegalArgumentException.class);
		CVAHullWhiteCIRConvergencePlausibilityTest.getIncreasingSampleSizeMeanArray(null);
	}
	
	@Test 
	public void boundrySingleInputTest() {
		Assert.assertArrayEquals( new double[] {1.0} , CVAHullWhiteCIRConvergencePlausibilityTest.getIncreasingSampleSizeMeanArray(new double[] {1.0}), 1E-7);
	}
	
	@Test
	public void testExample1() {
		Assert.assertArrayEquals(new double[] {1.0, 1.5, 2.0, 2.0}, CVAHullWhiteCIRConvergencePlausibilityTest.getIncreasingSampleSizeMeanArray(new double[] {1.0, 2.0, 3.0, 2.0}) , 1E-7);
		Assert.assertArrayEquals(new double[] {1.0, 1.0, 1.0, 1.0}, CVAHullWhiteCIRConvergencePlausibilityTest.getIncreasingSampleSizeMeanArray(new double[] {1.0, 1.0, 1.0, 1.0}) , 1E-7);
	}
	
}
