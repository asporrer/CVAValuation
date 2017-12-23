/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * An interface for a product providing its path-wise conditional fair value with 
 * respect to an underlying model {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface}.
 * <br> (E.g. in case of a short rate model: for each path &omega;, (E[V|r(t<sub>i</sub>)=r(t<sub>i</sub>,&omega;)]))<sub>i = 1, ... , n</sub> 
 * is provided. Where V is the discounted payoff of the product.
 * 
 * 
 * @author Anton Sporrer
 * 
 * @param <T> This interface is parameterized such that some methods return or get T type variables which provide certain methods. Further {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.product.AbstractProductConditionalFairValueProcess} implements this interface
 * and has an instance variable of type T.
 * 
 */
public interface ProductConditionalFairValueProcessInterface<T extends ProductConditionalFairValue_ModelInterface> {

	/**
	 * 
	 * @param timeIndex
	 * @return The numéraire value at the time index.
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException;
	
	/**
	 * 
	 * getFairValue version in the non-MultiCurve setting.
	 * 
	 * @param timeIndex
	 * @return The fair values of the product conditioned at the current time with respect to the path-wise current states of the underlying (e.g. in case of a short rate model: for each path &omega;, E[V|r(t<sub>timeIndex</sub>)=r(<sub>timeIndex</sub>,&omega;)]) is returned. Where V is the discounted payoff of the product).
	 * @throws CalculationException 
	 */
	@Deprecated
	public RandomVariableInterface getFairValueNonMultiCurve(int timeIndex) throws CalculationException;
	
	
	/**
	 * 
	 * @param timeIndex
	 * @return The fair values of the product conditioned at the current time with respect to the path-wise current states of the underlying (e.g. in case of a short rate model: for each path &omega;, E[V|r(t<sub>timeIndex</sub>)=r(<sub>timeIndex</sub>,&omega;)]) is returned. Where V is the discounted payoff of the product).
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getFairValue(int timeIndex) throws CalculationException;
	
	
	/**
	 * 
	 * @param timeIndex 
	 * @param componentIndex 
	 * @return The specified component of underlying at the specified time is returned.
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getUnderlying(int timeIndex, int componentIndex) throws CalculationException;

	/**
	 * 
	 * It should be pointed out that in most implementations of this method 
	 * referenced model is not cloned. Therefore the provided model itself 
	 * is assigned to the product.
	 *
	 * @param underlyingModel The underlying model which will be assigned to the product.
	 */
	public void setUnderlyingModel(T underlyingModel);
	
	/**
	 * Not a copy but the model itself is returned.
	 * 
	 * @return The underlying model which respect to which the fair price of the product is calculated.
	 */
	public T getUnderlyingModel();
	
	/**
	 * 
	 * @return The number of simulation paths of the underlying.
	 */
	public int getNumberOfPaths();
	
	/**
	 * 
	 * @return The time discretization of the underlying.
	 */
	public TimeDiscretizationInterface getTimeDiscretization();
	
}
