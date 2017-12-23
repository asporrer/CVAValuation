/* 
 * Contact: anton.sporrer@yahoo.com
 */

package test.net.finmath.antonsporrer.masterthesis.function.statisticalfunctions;

import org.junit.Assert;
import org.junit.Test;

import main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions;


/**
 * 
 * Unit test for {@link main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions #getArithmeticMean(double[])}
 * 
 * @author Anton Sporrer
 *
 */

public class ArithmeticMeanTest {

	@Test
	public void testBoundrySingleInput() {
		Assert.assertEquals( 0.0, StatisticalFunctions.getArithmeticMean(new double[] {0.0}),  1E-7 );
	}
	
	@Test
	public void testExample1() {
		Assert.assertEquals( 2.0, StatisticalFunctions.getArithmeticMean(new double[] {1.0, -1.0, 2.3, 2.7, 5.0}),  1E-7 );
	}
	
}
