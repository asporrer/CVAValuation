package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import net.finmath.stochastic.RandomVariableInterface;

/**
 * An interface for the coupling of a fair value process of an underlying product and a its default time. 
 * 
 * @author Anton Sporrer
 * 
 */
public interface NPVAndDefaultSimulationInterface {
	
	/**
	 * @param timeIndex
	 * @return NPV The Net Present Value at the timeIndex discounted back to time zero is returned.
	 */
	public RandomVariableInterface getDiscountedNPV(int timeIndex);
	
	/**
	 * @param timeIndex
	 * @return Default Probability of default occuring in the intervall (timeIndex, timeIndex + 1]. If the timeIndex is the last discretization point of the underlying time discretization than the probability of default in (timeIndex, infinity) is returned.
	 */
	public double getDefaultProbability(int timeIndex);
	
}
