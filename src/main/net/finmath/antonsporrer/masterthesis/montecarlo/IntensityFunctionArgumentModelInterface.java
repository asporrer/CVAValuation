/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.model.AbstractModelInterface;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * All Models implementing this interface can be used to model an underlying 
 * and a default intensity. The default intensity at a given time is provided by applying an 
 * intensity function to the return value of the getIntensityFunctionArgument method of this 
 * interface.
 * Therefore the getIntensityFunctionArgument method should be implemented in a way such that
 * the return value is a meaningful argument for the intensity function. The combination of getIntensityFunctionArgument 
 * and intensity function should result in a reasonable intensity. This result can be used to calculate the CVA.
 * 
 * @author Anton Sporrer
 *
 */
public interface IntensityFunctionArgumentModelInterface extends AbstractModelInterface {

	/**
	 * @param timeIndex The time index referring to the time point at which the intensity argument is requested.
	 * @param argumentIndex The index of the function argument. Only used in case of a multidimensional intensity function otherwise 0.
	 * @return A random variable used as an argument to calculate an intensity associated to the current underlying.
	 * @throws CalculationException
	 */
	public RandomVariableInterface getIntensityFunctionArgument(int timeIndex, int argumentIndex) throws CalculationException;
	
}
