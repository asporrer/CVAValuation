package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.integration.Integration;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * An interface for the coupling of a fair value process of an underlying product and a its default time. 
 * 
 * @author Anton Sporrer
 * 
 */
public interface NPVAndDefaultSimulationInterface<T extends ProductConditionalFairValue_ModelInterface> {
	
	/**
	 * @param timeIndex
	 * @return NPV The Net Present Value at the timeIndex discounted back to time zero is returned.
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getDiscountedNPV(int timeIndex, int discountBackToIndex) throws CalculationException;
	
	
	/**
	 * 
	 * @param timeIndex
	 * @return
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException;
	
	
	/**
	 * @param timeIndex
	 * @return Default Probability of default occuring in the intervall (timeIndex, timeIndex + 1]. If the timeIndex is the last discretization point of the underlying time discretization than the probability of default in (timeIndex, infinity) is returned.
	 */
	public double getDefaultProbability(int timeIndex) throws CalculationException;
	
	public TimeDiscretizationInterface getTimeDiscretization();
	
	public int getNumberOfPaths();
	
	
	public void setProductProcess(ProductConditionalFairValueProcessInterface<T> productProcess);
	
	
	public ProductConditionalFairValueProcessInterface<T> getProductProcess();
	
	
	
	
}
