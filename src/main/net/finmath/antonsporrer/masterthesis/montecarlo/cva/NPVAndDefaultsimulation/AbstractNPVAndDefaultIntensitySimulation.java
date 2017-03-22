package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.integration.Integration;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * The default probability is modeled as follows in the intensity based approach.
 * Let U be an uniform distributed random variable independent of the default intensity.
 * Then the default stopping time &tau; is defined as follows. 
 * <br> &tau; := inf(t >= 0 | U >= exp( - int<sub>0</sub><sup>t</sup> intensity(s) ds )).
 * 
 * @author Anton Sporrer
 *
 * @param <T>
 */

public abstract class AbstractNPVAndDefaultIntensitySimulation<T extends ProductConditionalFairValue_ModelInterface> extends AbstractNPVAndDefaultSimulation<T> implements NPVAndDefaultIntensitySimulationInterface<T> {
	
	// TODO: Change to ConcurrentHashMap and implement tests in function / correlated intensity sub-classes.
	// These tests should secure that the underlying determining the expOfIntegratedIntensity did not change
	// This array contains at index i is the pathwise approximation of exp(int_0^t_i intensity(s) ds).
	private RandomVariableInterface[] expOfIntegratedIntensity;
	
	public AbstractNPVAndDefaultIntensitySimulation(
			T underlyingModel,
			ProductConditionalFairValueProcessInterface<T> productProcess) {
		super(underlyingModel, productProcess);
	}

	
	
	public double getDefaultProbability( int timeIndex ) throws CalculationException {
		
		// In case the passed time index is strictly bigger than the number of times of the underlying time discretization an error is thrown.
		if( timeIndex > this.getTimeDiscretization().getNumberOfTimes() ) {
			throw new IllegalArgumentException("The timeIndex is not allowed to be strictly bigger then numberOfTimes.");
		}
		
		// Check if the requested default probability has already been calculated.
		// In this case it is just returned.
		if( this.defaultProbabilities.get(timeIndex) != null ) {
			return defaultProbabilities.get(timeIndex);
		}
		
		////
		// The default probability of tau in ( 0 , t_{timeIndex} ] is calculated. 
		//  The default stopping time is modeled by tau := inf(t >= 0 | U >= exp( - int_0^t intensity(s) ds )).
		// Therefore the probability P( tau in ( 0 , t_{timeIndex} ] ) = E[ 1 - exp(- int_0^t intensity(s) ds ) ].  
		////
		
		// Storing the current default probability to the default probabilities hash map.
		defaultProbabilities.put(  timeIndex , 1.0 - ( ( new RandomVariable(1.0) ).div( this.getExpOfIntegratedIntensity(timeIndex) ) ).getAverage()  );
		
		return  defaultProbabilities.get(timeIndex);
		
	}
	
	
	
	/**
	 * 
	 * @param timeIndex The time index up to which is integrated.
	 * @return The path-wise approximation of exp(int_0^t_{timeIndex} intensity(s) ds)
	 * 
	 */
	public RandomVariableInterface getExpOfIntegratedIntensity(int timeIndex) throws CalculationException {
		
		if( expOfIntegratedIntensity == null ) {
			doGenerateExpOfIntegratedIntensity();
		}
		return expOfIntegratedIntensity[timeIndex];
		
	}

	private void doGenerateExpOfIntegratedIntensity() throws CalculationException {
		
		// TODO: Write general integration scheme and use it here and in the CVA calculation.
		// 		 Maybe first fetch the intensity array and store it in an auxiliary intensityArray 
		// 		 which can be passed into an integral approximation function.
		
		// The number of time discretization points of the underlying model with respect to the product.
		int numberOfTimes = this.getTimeDiscretization().getNumberOfTimes();
		
		// The dimension of the exp integrated intensity array is set. 
		expOfIntegratedIntensity = new RandomVariableInterface[numberOfTimes];
				
		expOfIntegratedIntensity[0] = new RandomVariable(1.0);
		
		// t.
		double currentTime = 0.0;
		
		// t_(i+1) - t_(i).
		RandomVariableInterface currentDeltaT = null;
		
		// Default intensity.
		RandomVariableInterface currentIntensity = null;
		
		// Approximation of exp(\int_0^tn intensity(s) ds ) by exp( sum_(i = 0)^n (t_(i+1) - t_(i)) intensity(t_i) ).
		RandomVariableInterface currentExponentialFunctionOfIntensityIntegral = new RandomVariable(1.0);
		
		
		// TODO: Improve integral approximation scheme. Splines; common distribution of delta lambda  and delta exp( int lambda ).
		for(int timeIndex = 0; timeIndex < numberOfTimes - 1; timeIndex++) {
			
			currentTime = this.getTimeDiscretization().getTime(timeIndex);
			
			currentDeltaT = new RandomVariable(this.getTimeDiscretization().getTime(timeIndex+1) - currentTime);
			
			// intensity(t_timeIndex)
			currentIntensity = this.getIntensity(timeIndex);
			
			// Multiplying the new intensity step for the next iteration.
			// Calculating exp( sum_(k = 0)^(i-1) (t_(k+1) - t_(k)) intensity(t_k)  ) *  exp(  (t_(i+1) - t_(i)) intensity(t_i) ).
			currentExponentialFunctionOfIntensityIntegral = currentExponentialFunctionOfIntensityIntegral.mult( currentIntensity.mult(currentDeltaT).exp() );
			
			expOfIntegratedIntensity[timeIndex + 1] = currentExponentialFunctionOfIntensityIntegral; 
			
		}
	
	}

	
}
