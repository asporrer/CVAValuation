package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.AbstractProductConditionalFairValue_Model;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.AbstractProductConditionalFairValueProcess;

public abstract class AbstractNPVAndDefaultIntensitySimulation<T extends AbstractProductConditionalFairValue_Model> extends AbstractNPVAndDefaultSimulation<T> implements NPVAndDefaultIntensitySimulationInterface {
	
	private RandomVariableInterface[] expOfIntegratedIntensity;
	
	public AbstractNPVAndDefaultIntensitySimulation(
			T underlyingModel,
			AbstractProductConditionalFairValueProcess<T> productProcess) {
		super(underlyingModel, productProcess);
	}

	public double getDefaultProbability(int timeIndex) {
		// TODO: Implement here.
		// Lazy init for defaultProbability. 
		return -1;
	}
	
	
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
		for(int timeIndex = 0; timeIndex < numberOfTimes; timeIndex++) {
			
			currentTime = this.getTimeDiscretization().getTime(timeIndex);
			
			currentDeltaT = new RandomVariable(this.getTimeDiscretization().getTime(timeIndex+1) -  currentTime);
			
			// intensity(t_timeIndex)
			currentIntensity = this.getIntensity(timeIndex);
			
			// Multiplying the new intensity step for the next iteration.
			// Calculating exp( sum_(k = 0)^(i-1) (t_(k+1) - t_(k)) intensity(t_k)  ) *  exp(  (t_(i+1) - t_(i)) intensity(t_i) ).
			currentExponentialFunctionOfIntensityIntegral = currentExponentialFunctionOfIntensityIntegral.mult( currentIntensity.mult(currentDeltaT).exp() );
			
			expOfIntegratedIntensity[timeIndex + 1] = currentExponentialFunctionOfIntensityIntegral; 
			
		}
	
	}

	
}
