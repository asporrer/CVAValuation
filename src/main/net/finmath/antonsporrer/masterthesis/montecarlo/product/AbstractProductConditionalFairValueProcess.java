package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;


public abstract class AbstractProductConditionalFairValueProcess<T extends  ProductConditionalFairValue_ModelInterface > implements ProductConditionalFairValueProcessInterface<T> {

	T underlyingModel;
	
	
	// TODO: Keep both constructor and setUnderlyingModel?
	public AbstractProductConditionalFairValueProcess(T underlyingModel) {
		this.underlyingModel = underlyingModel;
	}
	

	
	/**
	 * 
	 * @return The specified component of the underlying at the current time is returned.
	 * @throws CalculationException 
	 * 
	 */
	public RandomVariableInterface getUnderlying(int timeIndex, int componentIndex) throws CalculationException {
		return this.underlyingModel.getProcessValue(timeIndex, componentIndex);
	}
	
	public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException {
		return underlyingModel.getNumeraire(timeIndex);
	}
	
	
	
	// TODO: Assign clone of the underlying model?
	public void setUnderlyingModel(T underlyingModel) {
		this.underlyingModel = underlyingModel;
	}
	
	public T getUnderlyingModel() {
		return underlyingModel;
	}
	
	
	public int getNumberOfPaths() {
		return this.underlyingModel.getProcess().getNumberOfPaths();
	}
	
	public TimeDiscretizationInterface getTimeDiscretization() {
		return this.underlyingModel.getTimeDiscretization();
	}
	
	
	
	
	
	
}
