/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * This abstract class provides the following for its subclasses.
 * <br> First the implementation of {@link #getExpOfIntegratedIntensity(int))}. The return value 
 * is closely related to the default probability derived from the path-wise intensity simulation. 
 * <br> Secondly, the default probability up to a certain time point is stored and provided through 
 *  {@link #getDefaultProbability(int)}.
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
	
	
	// This array contains at index i the path-wise approximation of exp(int_0^t_i intensity(s) ds).
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
		//  The default stopping time is modeled by tau(omega) := inf(t >= 0 | U >= exp( - int_0^t intensity(s, omega) ds )).
		// Therefore the probability P( tau in ( 0 , t_{timeIndex} ] ) = E[ 1 - exp(- int_0^t intensity(s, omega) ds ) ].  
		////
		
		// Storing the current default probability to the default probabilities hash map.
		defaultProbabilities.put(  timeIndex , 1.0 - ( ( new RandomVariable(1.0) ).div( this.getExpOfIntegratedIntensity(timeIndex) ) ).getAverage()  );
		
		return  defaultProbabilities.get(timeIndex);
		
	}
	
	
	public RandomVariableInterface getExpOfIntegratedIntensity(int timeIndex) throws CalculationException {
		
		if( expOfIntegratedIntensity == null ) {
			doGenerateExpOfIntegratedIntensity();
		}
		return expOfIntegratedIntensity[timeIndex];
		
	}

	/**
	 * 
	 * exp(int_0^t_{timeIndex} intensity(s) ds) is approximated as follows.
	 * In each time step the average of the intensity at the interval bounds is used as
	 * an approximation for the intensity in this interval. These interval-wise approximation 
	 * are multiplied with their respective interval length then the exp-function is applied 
	 * to the result. The results are calculated successively each new step is multiplied to 
	 * the products of the previous steps. This is an approximation
	 * exp(int_0^t_{timeIndex} intensity(s) ds).
	 * 
	 * @throws CalculationException
	 */
	private void doGenerateExpOfIntegratedIntensity() throws CalculationException {
		
		// The number of time discretization points of the underlying model with respect to the product.
		int numberOfTimes = this.getTimeDiscretization().getNumberOfTimes();
		
		// The dimension of the exponential of integrated intensity array is set. 
		expOfIntegratedIntensity = new RandomVariableInterface[numberOfTimes];
				
		expOfIntegratedIntensity[0] = new RandomVariable(1.0);
		
		// t.
		double currentTime = 0.0;
		
		// t_(i+1) - t_(i).
		RandomVariableInterface currentDeltaT = null;
		
		// Default intensity at current time
		RandomVariableInterface currentIntensity = null;
		
		// Default intensity at the next time 
		RandomVariableInterface nextIntensity = null;
		
		// Approximation of exp(\int_0^tn intensity(s) ds ) by exp( sum_(i = 0)^n (t_(i+1) - t_(i)) (intensity(t_i) + intensity(t_(i+1))) * 0.5 ).
		RandomVariableInterface currentExponentialFunctionOfIntensityIntegral = new RandomVariable(1.0);
		
		
		for(int timeIndex = 0; timeIndex < numberOfTimes - 1; timeIndex++) {
			
			currentTime = this.getTimeDiscretization().getTime(timeIndex);
			
			currentDeltaT = new RandomVariable( this.getTimeDiscretization().getTime(timeIndex+1) - currentTime );
			
			// intensity(t_timeIndex)
			currentIntensity = this.getIntensity(timeIndex);
			
			// intensity(t_{timeIndex + 1})
			nextIntensity = this.getIntensity(timeIndex + 1);
			
			// Multiplying the new intensity step to the running exponential of the integral. Thereby adding the integral approximation summands.
			// Calculating exp( sum_(k = 0)^(i-1) (t_(k+1) - t_(k)) (intensity(t_k) + intensity(t_(k+1))) * 0.5  ) *  exp(  (t_(i+1) - t_(i)) * (intensity(t_i) + intensity(t_(i+1))) * 0.5 ).
			currentExponentialFunctionOfIntensityIntegral = currentExponentialFunctionOfIntensityIntegral.mult(  (currentIntensity.add(nextIntensity)).mult(0.5).mult(currentDeltaT).exp() );
			
			expOfIntegratedIntensity[timeIndex + 1] = currentExponentialFunctionOfIntensityIntegral; 
			
		}
	
	}

	
	/**
	 *
	 * This method enables us to reset the expOfIntegrated intensity. 
	 * For instance this is useful in the following case. Let us assume the 
	 * underlying intensity has changed in this case the expOfIntegratedIntensity
	 * should be recalculated. This can be done by first implementing test which 
	 * test whether the underlying intensity has changed. And second in case there 
	 * was a change then the expOfIntegratedIntensity is reset. 
	 * 
	 */
	public void resetExpOfIntegratedIntensity() {
		expOfIntegratedIntensity = null;
	}
	
	
}
