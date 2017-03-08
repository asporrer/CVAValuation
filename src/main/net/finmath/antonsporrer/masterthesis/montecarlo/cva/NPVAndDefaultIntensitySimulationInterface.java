package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * 
 * TODO: ?let getIntensity return a RandomVariableInterface vector? 
 * @author Anton Sporrer
 *
 */
public interface NPVAndDefaultIntensitySimulationInterface extends NPVAndDefaultSimulationInterface {

	/**
	 * 
	 * @param timeIndex
	 * @return &lambda; The default intensity at the current time is returned.
	 */
	public RandomVariableInterface getIntensity(int timeIndex);
	
	
	/**
	 * 
	 * @param timeIndex
	 * @return The exponential aplied to the integral of lambda from zero to the current time is returned.
	 */
	public RandomVariableInterface getExpOfIntegratedIntensity(int timeIndex);
	
	
}
