package main.net.finmath.antonsporrer.masterthesis.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.model.AbstractModelInterface;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * This model specifies some basic methods each model used for 
 * simulating the process of product payoffs conditioned on the 
 * current value of the underlying at earlier time points.
 * More requirements on the model are specified in sub-interfaces.
 * 
 * This model is consumed by the classes implementing the following interface.
 * {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface}
 * 
 * @author Anton Sporrer
 *
 */
public interface ProductConditionalFairValue_ModelInterface extends AbstractModelInterface{

	/**
	 * The numéraire at a given time index is returned.
	 * 
	 * @param timeIndex
	 * @return The Numeraire
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException;
	
	/**
	 * The process value of a component at a given time index is returned.
	 * 
	 * @param timeIndex
	 * @param componentIndex
	 * @return The process value 
	 */
	public RandomVariableInterface getProcessValue(int timeIndex, int componentIndex) throws CalculationException;
	
}
