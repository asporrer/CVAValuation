/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * An interface for the coupling of a fair value process of an underlying product and a a default time. 
 * 
 * @author Anton Sporrer
 * 
 * @param <T> The parameter is utilized by {@link #getProductProcess()}. The return type provides methods returning T type variables.
 */
public interface NPVAndDefaultSimulationInterface<T extends ProductConditionalFairValue_ModelInterface> {
	

	/**
	 * 
	 * The path dependent discounted net present value of this product is returned.
	 * <br> That is to say first only the future or present payments after or at the time t_timeIndex are considered.
	 * These payments are discounted back to t_timeIndex and then the value of the factorized conditional expectation 
	 * path-wise applied to the value of the underlying at t_timeIndex (conditional refers to the underlying). 
	 * <br> Second the these path-wise values are discounted back to the time with index discountBackToIndex.
	 * The discounting is done with the numéraire of the underlying model. 
	 * 
	 * @param timeIndex
	 * @param discountBackToIndex The index of the time to which the net present value  should be discounted.
	 * @return NPV The Net Present Value at the timeIndex discounted back to time with index discountBackToIndex is returned.
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getDiscountedNPV(int timeIndex, int discountBackToIndex) throws CalculationException;
	
	
	/**
	 * 
	 * The numéraire in question is derived from the underlying model with respect to which the product is evaluated.
	 * 
	 * @param timeIndex 
	 * @return The numéraire at given time index. 
	 * @throws CalculationException 
	 */
	public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException;
	
	
	/**
	 * @param timeIndex
	 * @return Default Probability of default occurring in the interval (0, t<sub>timeIndex</sub>].
	 */
	public double getDefaultProbability(int timeIndex) throws CalculationException;
	
	/**
	 * 
	 * @return The time discretization of the underlying model with respect to which the product is evaluated.
	 */
	public TimeDiscretizationInterface getTimeDiscretization();
	
	/**
	 * 
	 * @return The number of paths used in this simulation.
	 */
	public int getNumberOfPaths();
	
	
	/**
	 * 
	 * @param productProcess The underyling product process is set.
	 */
	public void setProductProcess(ProductConditionalFairValueProcessInterface<T> productProcess);
	
	
	/**
	 * 
	 * In contrast to {@link #setProductProcess(ProductConditionalFairValueProcessInterface)} 
	 * an implementation of this method should not simply set the new parameter product process N as new 
	 * instance variable instead of the old product process O. But beforehand N should get the underlying 
	 * model of O. Thereby the short rate and if used the intensity can be reused.
	 * 
	 * @param productProcess
	 */
	public void plugInProductProcess(ProductConditionalFairValueProcessInterface<T> productProcess);
	
	/**
	 * 
	 * @return the underlying product process.
	 */
	public ProductConditionalFairValueProcessInterface<T> getProductProcess();
	

}
