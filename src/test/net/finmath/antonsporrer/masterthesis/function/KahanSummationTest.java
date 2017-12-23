/* 
 * Contact: anton.sporrer@yahoo.com
 */

package test.net.finmath.antonsporrer.masterthesis.function;

import org.junit.Assert;
import org.junit.Test;


import main.net.finmath.antonsporrer.masterthesis.function.KahanSummation;


/**
 * 
 * Unit test for {@link main.net.finmath.antonsporrer.masterthesis.function.KahanSummation KahanSummation}
 * 
 * @author Anton Sporrer
 *
 */
public class KahanSummationTest {

	
	@Test
	public void testBoundryCaseSingleSummand() {
		Assert.assertEquals(1.0, KahanSummation.getValue(new double[] {1.0}), 1E-7);
	}
	
	@Test
	public void testSum() {
		double[] summands = new double[] {1.1, 2.4, 3.0, -1.3};
		Assert.assertEquals(5.2, KahanSummation.getValue(summands), 1E-7);
	}
	
	
}
