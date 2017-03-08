package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import net.finmath.stochastic.RandomVariableInterface;

/**
 * An interface for a product providing its path-wise conditional fair value 
 * whereby we assume that the underlying is a stochastic process having the Markov Property.
 * <br> (E.g. in case of a short rate model: for each path &omega;, (E[V|r(t<sub>i</sub>)=r(t<sub>i</sub>,&omega;)]))<sub>i = 1, ... , n</sub> 
 * is provided. Where V is the discounted payoff of the product.
 * 
 * @author Anton Sporrer
 *
 */
public interface ConditionalValueProductInterface {

	/**
	 * 
	 * @param timeIndex
	 * @return
	 */
	public RandomVariableInterface getNumeraire(int timeIndex);
	
	/**
	 * 
	 * @param timeIndex
	 * @return The fair values of the product conditioned at the current time with respect to the path-wise current states of the underlying (e.g. in case of a short rate model: for each path &omega;, E[V|r=r(&omega;)]) is returned. Where V is the discounted payoff of the product).
	 */
	public RandomVariableInterface getFairValue(int timeIndex);
	
	
	/**
	 * 
	 * @param timeIndex
	 * @return The underlying with respect to which the product is evaluated is returned.
	 */
	public RandomVariableInterface getUnderlying(int timeIndex, int componentIndex);
	
	
}
