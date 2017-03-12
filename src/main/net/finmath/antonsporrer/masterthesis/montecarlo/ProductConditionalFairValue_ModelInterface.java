package main.net.finmath.antonsporrer.masterthesis.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.model.AbstractModelInterface;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * 
 * 
 * @author Anton Sporrer
 *
 */
public interface ProductConditionalFairValue_ModelInterface extends AbstractModelInterface{

	/**
	 * 
	 * @param timeIndex
	 * @return The Numeraire
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException;
	
	/**
	 * 
	 * @param timeIndex
	 * @param componentIndex
	 * @return The value 
	 */
	public RandomVariableInterface getProcessValue(int timeIndex, int componentIndex) throws CalculationException;
	
}
