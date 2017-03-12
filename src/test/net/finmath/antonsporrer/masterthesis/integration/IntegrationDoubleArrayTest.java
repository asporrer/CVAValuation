package test.net.finmath.antonsporrer.masterthesis.integration;

import static org.junit.Assert.assertEquals; // Notice the use of "static" here

import java.util.Arrays;
import java.util.Collection;

import main.net.finmath.antonsporrer.masterthesis.integration.Integration;
import main.net.finmath.antonsporrer.masterthesis.integration.Integration.IntegrationMethod;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;
import static org.junit.Assert.*; 

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class IntegrationDoubleArrayTest {

	private double[] functionValues;
	TimeDiscretizationInterface timeDiscretization;
	IntegrationMethod integrationMethod;
	double result;
	

	
	@Before 
	public void initialize() {
	}
	
	public IntegrationDoubleArrayTest(double[] functionValues, TimeDiscretizationInterface timeDiscretization, IntegrationMethod integrationMethod, double result) {
		this.functionValues = functionValues;
		this.timeDiscretization = timeDiscretization;
		this.integrationMethod = integrationMethod;
		this.result = result;
	}
	

	
	
	@Parameterized.Parameters 
	public static Collection integrationParameters() {
		return Arrays.asList(new Object[][] {
				// Test cases for the double array version of the getIntegral method.
				{ new double[] {1.0,1.0,1.0}, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0}), IntegrationMethod.LeftPoints, 2.0},
				{ new double[] {1.0, 2.0, 3.0, 4.0, 5.0 }, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0, 3.0, 4.0}), IntegrationMethod.LeftPoints, 10.0 },
				{ new double[] {2.0, 4.0, 6.0}, new TimeDiscretization(new Double[] {0.0, 1.0, 4.0}), IntegrationMethod.LeftPoints, 14.0}, 
				{ new double[] {1.0,1.0,1.0}, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0}), IntegrationMethod.RightPoints, 2.0},
				{ new double[] {1.0, 2.0, 3.0, 4.0, 5.0 }, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0, 3.0, 4.0}), IntegrationMethod.RightPoints, 14.0 },
				{ new double[] {2.0, 4.0, 6.0}, new TimeDiscretization(new Double[] {0.0, 1.0, 4.0}), IntegrationMethod.RightPoints, 22.0}, 
				{ new double[] {1.0,1.0,1.0}, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0}), IntegrationMethod.Trapezoidal, 2.0},
				{ new double[] {1.0, 2.0, 3.0, 4.0, 5.0 }, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0, 3.0, 4.0}), IntegrationMethod.Trapezoidal, 12.0 },
				{ new double[] {2.0, 4.0, 6.0}, new TimeDiscretization(new Double[] {0.0, 1.0, 4.0}), IntegrationMethod.Trapezoidal, 18.0},  
		});
	}
	
	@Test
	public void testIntegration() throws CalculationException {
		
		System.out.println("The result is: " + result);
		System.out.println("The function values are: " + Arrays.toString(functionValues));
		System.out.println("The time discretization is: " + Arrays.toString(timeDiscretization.getAsDoubleArray()));
		System.out.println("The method is: " + integrationMethod);
		

		assertEquals(result, 
	    		  Integration.getIntegral(functionValues, timeDiscretization, integrationMethod)	, 0.0001);

	}
	
	
}
