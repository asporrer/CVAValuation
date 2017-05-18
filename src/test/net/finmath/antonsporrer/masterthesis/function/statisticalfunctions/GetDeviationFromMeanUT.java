/* 
 * Contact: anton.sporrer@yahoo.com
 */

package test.net.finmath.antonsporrer.masterthesis.function.statisticalfunctions;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions;

/**
 * 
 * Unit test for {@link main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions #getDeviationsFromMean(double[])}
 * 
 * @author Anton Sporrer
 *
 */
public class GetDeviationFromMeanUT {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test 
	public void throwsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		StatisticalFunctions.getDeviationsFromMean( null );
	}
	
	@Test
	public void testBoundrySingleInput() {
		Assert.assertArrayEquals(new double[] {0.0}, StatisticalFunctions.getDeviationsFromMean( new double[] {1.0} ), 1E-7);
	}
	
	@Test
	public void testExample1() {
		Assert.assertArrayEquals(new double[] {0.7, 0.7, 0.0}, StatisticalFunctions.getDeviationsFromMean(new double[] {1.7, 0.3, 1.0} ), 1E-7);
		Assert.assertArrayEquals(new double[] {0.8, 0.2, 1.0}, StatisticalFunctions.getDeviationsFromMean(new double[] {2.2, 2.8, 4.0 } ), 1E-7);
	}
	
}
