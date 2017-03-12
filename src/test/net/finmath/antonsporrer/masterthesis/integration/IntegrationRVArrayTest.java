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
public class IntegrationRVArrayTest {


	TimeDiscretizationInterface timeDiscretization;
	IntegrationMethod integrationMethod;

	
	private RandomVariableInterface[] functionValuesRV;
	RandomVariableInterface resultRV;
	
	
	@Before 
	public void initialize() {
	}
	

	
	public IntegrationRVArrayTest(RandomVariableInterface[] functionValuesRV, TimeDiscretizationInterface timeDiscretization, IntegrationMethod integrationMethod, RandomVariableInterface resultRV) {
		this.functionValuesRV = functionValuesRV;
		this.timeDiscretization = timeDiscretization;
		this.integrationMethod = integrationMethod;
		this.resultRV = resultRV;
	}
	
	
	@Parameterized.Parameters 
	public static Collection integrationParameters() {
		return Arrays.asList(new Object[][] {
				
				//Test cases for the RandomVariableInterface version of the getIntegral method.
				{ new RandomVariableInterface[] {new RandomVariable(1.0),new RandomVariable(1.0),new RandomVariable(1.0)}, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0}), IntegrationMethod.LeftPoints, new RandomVariable(2.0)},
				{ new RandomVariableInterface[] {new RandomVariable(1.0),new RandomVariable(2.0),new RandomVariable(3.0),new RandomVariable(4.0),new RandomVariable(5.0) }, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0, 3.0, 4.0}), IntegrationMethod.LeftPoints, new RandomVariable(10.0) },
				{ new RandomVariableInterface[] {new RandomVariable(2.0),new RandomVariable(4.0),new RandomVariable(6.0)}, new TimeDiscretization(new Double[] {0.0, 1.0, 4.0}), IntegrationMethod.LeftPoints, new RandomVariable(14.0)}, 
				{ new RandomVariableInterface[] {new RandomVariable(1.0),new RandomVariable(1.0),new RandomVariable(1.0)}, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0}), IntegrationMethod.RightPoints, new RandomVariable(2.0)},
				{ new RandomVariableInterface[] {new RandomVariable(1.0),new RandomVariable(2.0),new RandomVariable(3.0),new RandomVariable(4.0),new RandomVariable(5.0) }, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0, 3.0, 4.0}), IntegrationMethod.RightPoints, new RandomVariable(14.0) },
				{ new RandomVariableInterface[] {new RandomVariable(2.0),new RandomVariable(4.0),new RandomVariable(6.0)}, new TimeDiscretization(new Double[] {0.0, 1.0, 4.0}), IntegrationMethod.RightPoints, new RandomVariable(22.0)}, 
				{ new RandomVariableInterface[] {new RandomVariable(1.0),new RandomVariable(1.0),new RandomVariable(1.0)},  new TimeDiscretization(new Double[] {0.0, 1.0, 2.0}), IntegrationMethod.Trapezoidal, new RandomVariable(2.0)},
				{ new RandomVariableInterface[] {new RandomVariable(1.0),new RandomVariable(2.0),new RandomVariable(3.0),new RandomVariable(4.0),new RandomVariable(5.0) }, new TimeDiscretization(new Double[] {0.0, 1.0, 2.0, 3.0, 4.0}), IntegrationMethod.Trapezoidal, new RandomVariable(12.0) },
				{ new RandomVariableInterface[] {new RandomVariable(2.0),new RandomVariable(4.0),new RandomVariable(6.0)}, new TimeDiscretization(new Double[] {0.0, 1.0, 4.0}), IntegrationMethod.Trapezoidal, new RandomVariable(18.0)}, 
		});
	}
	
	@Test
	public void testIntegration() throws CalculationException {
		
		System.out.println("The result is: " + resultRV.getAverage());
		System.out.println("The time discretization is: " + Arrays.toString(timeDiscretization.getAsDoubleArray()));
		System.out.println("The method is: " + integrationMethod);
		
			assertEquals(resultRV.getAverage(), 
		    		  Integration.getIntegral(functionValuesRV, timeDiscretization, integrationMethod).getAverage()	, 0.0001);
		
	}
	
	
}
