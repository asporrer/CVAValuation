package main.net.finmath.antonsporrer.masterthesis.montecarlo;

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
	 */
	public RandomVariableInterface getNumeraire(int timeIndex);
	
	/**
	 * 
	 * @param timeIndex
	 * @param componentIndex
	 * @return The value 
	 */
	public RandomVariableInterface getProcessValue(int timeIndex, int componentIndex);
	
}
