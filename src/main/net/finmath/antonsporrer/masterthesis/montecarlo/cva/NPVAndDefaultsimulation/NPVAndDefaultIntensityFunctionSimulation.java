package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.function.FunctionInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.IntensityFunctionArgumentModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;



/**
 * 
 * Function and intensity model have to be compatible!
 * 
 * @author Anton Sporrer
 *
 */
public class NPVAndDefaultIntensityFunctionSimulation<T extends  ProductConditionalFairValue_ModelInterface & IntensityFunctionArgumentModel> extends AbstractNPVAndDefaultIntensitySimulation<T>{


	private FunctionInterface intensityFunction;
	
	private T underlyingModelforDefaultProbabilityConsistencyCheck;
	private FunctionInterface intensityFunctionforDefaultProbabilityConsistencyCheck;
	//TODO: Store Intensity: private RandomVariableInterface[] intensityProcess; implement a wider class of functions not only "markovian" functions.
	
	//TODO: Create Class Default Intensity Function and add to constructor.
	
	public NPVAndDefaultIntensityFunctionSimulation(
			T underlyingModel,
			ProductConditionalFairValueProcessInterface<T> productProcess, int seed, FunctionInterface intensityFunction ) {
		super(underlyingModel, productProcess);
		this.intensityFunction = intensityFunction;
	}

	public RandomVariableInterface getIntensity(int timeIndex) throws CalculationException {
		return intensityFunction.getValue( this.getProductProcess().getUnderlyingModel().getIntensityFunctionArgument(timeIndex, 0 /* Could be extended if needed */ ) );
	}

	@Override
	public double getDefaultProbability(int timeIndex) throws CalculationException {
		
		// TODO: Check if this guarantees that the default probabilities are consistent!
		// Check if the underlying model or the intensity function has changed. If so the default probabilities have 
		// to be reset to guarantee consistent default probabilities.
		if(underlyingModelforDefaultProbabilityConsistencyCheck != this.getProductProcess().getUnderlyingModel() || intensityFunctionforDefaultProbabilityConsistencyCheck != intensityFunction) {
			defaultProbabilities.clear();
			underlyingModelforDefaultProbabilityConsistencyCheck = this.getProductProcess().getUnderlyingModel() ;
			intensityFunctionforDefaultProbabilityConsistencyCheck = this.intensityFunction;
		}
		
		return super.getDefaultProbability(timeIndex);
		
	}
	
	public RandomVariableInterface getExpOfIntegratedIntensity(int timeIndex) throws CalculationException {
		// TODO Use if-statement to treat the following case. The intensity model provides a getExpOfIntegratedIntensity function. 
		return super.getExpOfIntegratedIntensity(timeIndex);
	}
	
	public RandomVariableInterface getIntensityFunctionArgument(int timeIndex, int componentIndex) throws CalculationException {
		return this.getProductProcess().getUnderlyingModel().getIntensityFunctionArgument(timeIndex, componentIndex);
	}

	
}
