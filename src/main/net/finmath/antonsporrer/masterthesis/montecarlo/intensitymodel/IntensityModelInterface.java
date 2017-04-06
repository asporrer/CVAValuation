package main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.model.AbstractModelInterface;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * All a small subinterface for default intensity models.
 * 
 * 
 * 
 * @author Anton Sporrer
 *
 */
public interface IntensityModelInterface extends AbstractModelInterface {

	public RandomVariableInterface getIntensity(int timeIndex) throws CalculationException;
	
}
