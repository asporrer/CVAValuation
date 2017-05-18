/* 
 * Contact: anton.sporrer@yahoo.com
 */
package test.net.finmath.antonsporrer.masterthesis.function.statisticalfunctions;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions;
import main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions.EmpiricalVarianceVersion;

/**
 * 
 * Unit test for {@link main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions #getEmpiricalVariance(double[] , EmpiricalVarianceVersion )}
 * 
 * @author Anton Sporrer
 *
 */
public class EmpiricalVarianceTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test 
	public void throwsIllegalArgumentExceptionIfOneValueAndUnbiased() {
		exception.expect(IllegalArgumentException.class);
		StatisticalFunctions.getEmpiricalVariance( new double[] {1.0}, EmpiricalVarianceVersion.Unbiased );
	}
	
	@Test
	public void testBoundrySingleInput() {
		Assert.assertEquals(0.0, StatisticalFunctions.getEmpiricalVariance( new double[] {1.0}, EmpiricalVarianceVersion.Biased ), 1E-7);
	}
	
	@Test
	public void testExample1() {
		Assert.assertEquals(20.996, StatisticalFunctions.getEmpiricalVariance(new double[] {-5.5, 1.0, 3.3, 7.2, 6.5}, EmpiricalVarianceVersion.Biased ), 1E-7);
		Assert.assertEquals(26.245, StatisticalFunctions.getEmpiricalVariance(new double[] {-5.5, 1.0, 3.3, 7.2, 6.5}, EmpiricalVarianceVersion.Unbiased ), 1E-7);
	}
	
}
