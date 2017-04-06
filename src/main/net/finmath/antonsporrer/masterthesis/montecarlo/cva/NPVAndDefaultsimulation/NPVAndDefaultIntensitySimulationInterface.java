/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * This subinterface specifies required methods for joint product and intensity simulation.
 * 
 * @author Anton Sporrer
 *
 */
public interface NPVAndDefaultIntensitySimulationInterface<T extends ProductConditionalFairValue_ModelInterface> extends NPVAndDefaultSimulationInterface<T> {
	
	/**
	 * 
	 * @param timeIndex
	 * @return &lambda; The default intensity at the current time is returned.
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getIntensity(int timeIndex) throws CalculationException;
	
	
	/**
	 * 
	 * @param timeIndex The time index up to which is integrated.
	 * @return The path-wise approximation (Riemann-Integral-wise) of exp(int_0^t_{timeIndex} intensity(s) ds)
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getExpOfIntegratedIntensity(int timeIndex) throws CalculationException;
	
}
