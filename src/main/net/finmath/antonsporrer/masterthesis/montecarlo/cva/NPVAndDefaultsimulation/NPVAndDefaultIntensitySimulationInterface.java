package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * 
 * TODO: ?let getIntensity return a RandomVariableInterface vector? 
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
	 * @param timeIndex
	 * @return The exponential aplied to the integral of lambda from zero to the current time is returned.
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getExpOfIntegratedIntensity(int timeIndex) throws CalculationException;
	
}
