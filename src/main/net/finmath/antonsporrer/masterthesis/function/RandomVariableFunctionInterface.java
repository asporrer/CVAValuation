/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.function;

import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * An interface for the implementation of a random variable function. A random variable function
 * gets an argument implementing RandomVariableInterface and returns a result of type RandomVariableInterface.
 * 
 * TODO: Could be generalized to a multidimensional function.
 * 
 * @author Anton Sporrer
 *
 */
public interface RandomVariableFunctionInterface {
	
	public RandomVariableInterface getValue(RandomVariableInterface input);
	
}
