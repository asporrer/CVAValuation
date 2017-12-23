/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.function;

import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * This class implements a constant function operating on the input implementing RandomVariableInterface.
 * 
 * 
 * This function implements the following rule.
 * <br> x -> c
 * <br> Where c is a constant in the real numbers. 
 *
 * 
 * @author Anton Sporrer
 *
 */
public class ConstantFunction implements FunctionInterface<RandomVariableInterface,RandomVariableInterface> {

	// The constant function value
	private double constantFunctionValue;

	
	/**
	 * 
	 * 
	 * @param constantFunctionValue The constant value of this function.
	 */
	public ConstantFunction(double constantFunctionValue) {
		this.constantFunctionValue = constantFunctionValue;
	}
	
	
	/**
	 * The function is applied to the input.
	 */
	public RandomVariableInterface getValue(RandomVariableInterface input) {
	
		return new RandomVariable(constantFunctionValue);
		
	}

	
	
	
}
