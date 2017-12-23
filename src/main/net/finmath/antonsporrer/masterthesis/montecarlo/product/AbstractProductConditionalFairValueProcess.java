/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;


/**
 * This abstract class implements some methods common to all products. 
 * These methods are needed independent of whether a bond, cap, swaption etc. 
 * is implemented. And they are independent of whether there is an analytic
 * bond formula or an analytic formula for another payoff implemented in the
 * underlying model of type T. They are also independent of whether an American 
 * Monte Carlo simulation is used in the underlying model of type T to get 
 * the conditional fair price. 
 * 
 * 
 * @author Anton Sporrer
 *
 * @param <T> The getUnderlyingModel method provides a type T return value.
 */
public abstract class AbstractProductConditionalFairValueProcess<T extends  ProductConditionalFairValue_ModelInterface > implements ProductConditionalFairValueProcessInterface<T> {

	// The underlying model conditioned to which the fair value of the product is calculated.
	T underlyingModel;
	
	
	public AbstractProductConditionalFairValueProcess(T underlyingModel) {
		this.underlyingModel = underlyingModel;
	}

	
	/**
	 * 
	 * @return The specified component of the underlying at the current time is returned.
	 * @throws CalculationException  
	 */
	public RandomVariableInterface getUnderlying(int timeIndex, int componentIndex) throws CalculationException {
		return this.underlyingModel.getProcessValue(timeIndex, componentIndex);
	}
	
	public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException {
		return underlyingModel.getNumeraire(timeIndex);
	}
	
	public void setUnderlyingModel(T underlyingModel) {
		// The model is not cloned. This could be improved.
		this.underlyingModel = underlyingModel;
	}
	
	public T getUnderlyingModel() {
		// The model is not cloned. This could be improved.
		return underlyingModel;
	}
	
	public int getNumberOfPaths() {
		return this.underlyingModel.getProcess().getNumberOfPaths();
	}
	
	public TimeDiscretizationInterface getTimeDiscretization() {
		return this.underlyingModel.getTimeDiscretization();
	}
	
	
	
	
	
	
}
