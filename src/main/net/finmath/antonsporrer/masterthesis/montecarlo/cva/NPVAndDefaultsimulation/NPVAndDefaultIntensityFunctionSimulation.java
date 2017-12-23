/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.function.FunctionInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.IntensityFunctionArgumentModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;



/**
 * 
 * This class implements a simulation of an underlying process (e.g. a short rate or a LIBOR Market Model) specified by 
 * {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface} and
 * a default intensity which is implemented as a function of the underlying. More precisely the underlying has to implement {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.IntensityFunctionArgumentModelInterface}.
 * Therefore the underlying provides via {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.IntensityFunctionArgumentModelInterface#getIntensityFunctionArgument getIntensityFunctionArgument} 
 * the argument for a so called intensity function implementing {@link main.net.finmath.antonsporrer.masterthesis.function.RandomVariableFunctionInterface}. The function value is then the intensity.
 * 
 * 
 * 
 * @author Anton Sporrer
 *
 */
public class NPVAndDefaultIntensityFunctionSimulation<T extends  ProductConditionalFairValue_ModelInterface & IntensityFunctionArgumentModelInterface> extends AbstractNPVAndDefaultIntensitySimulation<T>{


	private FunctionInterface<RandomVariableInterface, RandomVariableInterface> intensityFunction;
	
	private T underlyingModelforDefaultProbabilityConsistencyCheck;
	private FunctionInterface<RandomVariableInterface, RandomVariableInterface> intensityFunctionforDefaultProbabilityConsistencyCheck;


	/**
	 * 
	 * 
	 * @param underlyingModel 
	 * @param productProcess 
	 * @param seed The seed of the Brownian motion used to simulate the underlying model (Not implemented!)
	 * @param intensityFunction The intensity function using arguments provided by the underlying model to calculate the default intensity.
	 */
	public NPVAndDefaultIntensityFunctionSimulation(
			T underlyingModel,
			ProductConditionalFairValueProcessInterface<T> productProcess, int seed, FunctionInterface<RandomVariableInterface, RandomVariableInterface> intensityFunction ) {
		super(underlyingModel, productProcess);
		this.intensityFunction = intensityFunction;
	}
	
	
	public RandomVariableInterface getIntensity(int timeIndex) throws CalculationException {
		// The intensity function is applied to the intensity function argument provided by the underlying model.
		return intensityFunction.getValue( this.getProductProcess().getUnderlyingModel().getIntensityFunctionArgument(timeIndex, 0 /* Could be extended if needed */ ) );
	}

	@Override
	public double getDefaultProbability(int timeIndex) throws CalculationException {
		
		// Check if the underlying model or the intensity function has changed. If so the default probabilities and the expOfIntegratedIntensity of the superclass have 
		// to be reset to guarantee consistent default probabilities and expOfIntegratedIntensity.
		if(underlyingModelforDefaultProbabilityConsistencyCheck != this.getProductProcess().getUnderlyingModel() || intensityFunctionforDefaultProbabilityConsistencyCheck != intensityFunction)
		{
			resetExpOfIntegratedIntensity();
			defaultProbabilities.clear();
			underlyingModelforDefaultProbabilityConsistencyCheck = this.getProductProcess().getUnderlyingModel() ;
			intensityFunctionforDefaultProbabilityConsistencyCheck = this.intensityFunction;
		}
		
		return super.getDefaultProbability(timeIndex);
		
	}
	
	public RandomVariableInterface getExpOfIntegratedIntensity(int timeIndex) throws CalculationException {
	
		// Check if the underlying model or the intensity function has changed. If so the default probabilities and the expOfIntegratedIntensity of the superclass have 
		// to be reset to guarantee consistent default probabilities and expOfIntegratedIntensity.
		if(underlyingModelforDefaultProbabilityConsistencyCheck != this.getProductProcess().getUnderlyingModel() || intensityFunctionforDefaultProbabilityConsistencyCheck != intensityFunction)
		{
			resetExpOfIntegratedIntensity();
			defaultProbabilities.clear();
			underlyingModelforDefaultProbabilityConsistencyCheck = this.getProductProcess().getUnderlyingModel() ;
			intensityFunctionforDefaultProbabilityConsistencyCheck = this.intensityFunction;
		}
		
		return super.getExpOfIntegratedIntensity(timeIndex);
	}
	
	/**
	 * 
	 * @param timeIndex The time index
	 * @param componentIndex The component index
	 * @return The component of the argument at the given time index provided to the intensity function. 
	 * @throws CalculationException
	 */
	public RandomVariableInterface getIntensityFunctionArgument(int timeIndex, int componentIndex) throws CalculationException {
		return this.getProductProcess().getUnderlyingModel().getIntensityFunctionArgument(timeIndex, componentIndex);
	}

	
}
