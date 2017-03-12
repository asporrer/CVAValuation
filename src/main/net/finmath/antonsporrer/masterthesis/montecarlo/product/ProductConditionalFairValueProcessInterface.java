package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * An interface for a product providing its path-wise conditional fair value 
 * whereby we assume that the underlying is a stochastic process having the Markov Property.
 * <br> (E.g. in case of a short rate model: for each path &omega;, (E[V|r(t<sub>i</sub>)=r(t<sub>i</sub>,&omega;)]))<sub>i = 1, ... , n</sub> 
 * is provided. Where V is the discounted payoff of the product.
 * 
 * @author Anton Sporrer
 *
 */
public interface ProductConditionalFairValueProcessInterface<T extends ProductConditionalFairValue_ModelInterface> {

	/**
	 * 
	 * @param timeIndex
	 * @return
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException;
	
	/**
	 * 
	 * @param timeIndex
	 * @return The fair values of the product conditioned at the current time with respect to the path-wise current states of the underlying (e.g. in case of a short rate model: for each path &omega;, E[V|r=r(&omega;)]) is returned. Where V is the discounted payoff of the product).
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getFairValue(int timeIndex) throws CalculationException;
	
	
	/**
	 * 
	 * @param timeIndex
	 * @return The underlying with respect to which the product is evaluated is returned.
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getUnderlying(int timeIndex, int componentIndex) throws CalculationException;
	
	
	// TODO: Assign clone of the underlying model?
	public void setUnderlyingModel(T underlyingModel);
	
	// TODO: Get a Clone?
	public T getUnderlyingModel();
	
	public int getNumberOfPaths();
	
	public TimeDiscretizationInterface getTimeDiscretization();
	
}
