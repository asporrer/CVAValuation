/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.IntensityModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.BrownianMotionView;
import net.finmath.montecarlo.CorrelatedBrownianMotion;
import net.finmath.montecarlo.process.AbstractProcess;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * 
 * This class implements a simulation of correlated underlying process (e.g. a short rate or a LIBOR Market Model) specified by 
 * {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface} and
 * a default intensity specified by {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.IntensityModelInterface}.
 * This is simply done by correlating the Brownian motions used to simulate the underlying model and the intensity model. 
 * 
 * 
 * @author Anton Sporrer
 *
 */
public class NPVAndCorrelatedDefaultIntensitySimulation<T extends ProductConditionalFairValue_ModelInterface> extends AbstractNPVAndDefaultIntensitySimulation<T>{
	
	private IntensityModelInterface intensityModel;
	// The calculated default probabilities are cached. The correctness of these stored probabilities is
	// only guaranteed if the intensity model unchanged. Therefore the model is also stored in this
	// variable for a later check before providing the stored default probabilities. 
	// TODO: This test is not sufficient and should be extended.
	private IntensityModelInterface intensityModelForDefaultProbabilityConsistencyCheck;
	
	// The correlation of the Brownian motions B_1, B_1. Where B_1 is used to simulate the underlying and 
	// and B_2 is used to simulate the intensity.
	private CorrelationInterface underlyingIntensityCorrelation;
	
	// The seed for the brownian motion used in this simulation. 
	private int seed;

	
	/**
	 * 
	 * It is assumed that the underlyingModel has a not null instance variable of type AbstractProcess.
	 * Such that getNumberOfPaths() and getTimeDiscretization() return proper values.
	 * 
	 * @param underlyingModel The underlying model for example a short rate model.
	 * @param productProcess 
	 * @param intensityModel
	 * @param correlation The intercorrelations of the Brownian motions B_1, B_2. B_1 is used to simulate the underlying, B_2 is used to simulate the intensity. 
	 * @param seed The seed of (B_1,B_2) the Brownian motion.
	 */
	public NPVAndCorrelatedDefaultIntensitySimulation(
			T underlyingModel,
			ProductConditionalFairValueProcessInterface<T> productProcess, IntensityModelInterface intensityModel, CorrelationInterface correlation, int seed) {
		
		super(underlyingModel, productProcess);
		// TODO: Check if the underlying model has a not null process as instance variable.
		
		this.intensityModel = intensityModel;
		
		this.underlyingIntensityCorrelation = correlation;
		this.seed = seed;
		
		correlateUnderylingAndIntensity(this.getTimeDiscretization(), seed, this.getNumberOfPaths() );
	}
	
	
	@Override
	public double getDefaultProbability(int timeIndex) throws CalculationException {
		// TODO: Improve! This does not necessarily guarantee that the default probabilities are still correct. 
		// Check if the intensityModel has changed. If so the default probabilities have 
		// to be reset to guarantee consistent default probabilities.
		if(intensityModelForDefaultProbabilityConsistencyCheck != intensityModel) {
			defaultProbabilities.clear();
			intensityModelForDefaultProbabilityConsistencyCheck = intensityModel;
		}
		
		return super.getDefaultProbability(timeIndex);
	}
	
	
	/**
	 * 
	 * @param timeDiscretization
	 * @param seed
	 * @param numberOfPaths
	 */
	private void correlateUnderylingAndIntensity(TimeDiscretizationInterface timeDiscretization, int seed, int numberOfPaths) {
		
		// The dimension or in other words the number of factors of the Brownian motion driving the underlying and the intensity model
		// is set.
		int numberOfFactors = underlyingIntensityCorrelation.getNumberOfRows();
	
		// First an uncorrelated Brownian motion is needed. 
		// This Brownian motion has the time discretization format of the underlying model.
		BrownianMotionInterface uncorrelatedBrownianMotion = new BrownianMotion(timeDiscretization, numberOfFactors, numberOfPaths, seed);
		
		// An correlated Brownian motion is created from an uncorrelated Brownian motion and factor loadings
		// provided by the correlation.
		BrownianMotionInterface correlatedBrownianMotion = new CorrelatedBrownianMotion(uncorrelatedBrownianMotion, underlyingIntensityCorrelation.getCorrelationFactorMatrix());
					
	
		////
		// Now the correlated Brownian motion will be split into two parts.
		////
		
		// Getting the number of underlying respectively intensity model factors in order to
		// assign the Integer arrays accordingly.
		int numberOfUnderlyingModelFactors = underlyingIntensityCorrelation.getNumberOfInterCorrelationRows();
		int numberOfItensityModelFactors = underlyingIntensityCorrelation.getNumberOfInterCorrelationColumns();

		// Parameters used for BrownianMotionView
		Integer[] bmFactorsForUnderlyingModel = new Integer[numberOfUnderlyingModelFactors]; 
		Integer[] bmFactorsForIntensity = new Integer[numberOfItensityModelFactors];
		
		for(int index = 0; index < numberOfUnderlyingModelFactors; index++) {
			bmFactorsForUnderlyingModel[index] = index;
		}
		
		for(int index = 0; index < numberOfItensityModelFactors; index++) {
			bmFactorsForIntensity[index] = index + numberOfUnderlyingModelFactors;
		}

		// Creating two correlated Brownian motions.
		BrownianMotionInterface brownianMotionUnderlyingModel = new BrownianMotionView( correlatedBrownianMotion, bmFactorsForUnderlyingModel);
		BrownianMotionInterface brownianMotionIntensityModel = new BrownianMotionView( correlatedBrownianMotion, bmFactorsForIntensity);
		
		
		////
		// Each Brownian motion will be passed to a different process.
		// Thereby the processes will be correlated. One process will be 
		// passed to the underlyingModel and the other one to the 
		// intensityModel. 
		////
		
		AbstractProcess processUnderlyingModel = new ProcessEulerScheme(brownianMotionUnderlyingModel);
		AbstractProcess processIntensityModel = new ProcessEulerScheme(brownianMotionIntensityModel);

		////
		// Linking the processes and the models.
		////
		
		this.getProductProcess().getUnderlyingModel().setProcess(processUnderlyingModel);
		processUnderlyingModel.setModel(this.getProductProcess().getUnderlyingModel());
		
		intensityModel.setProcess(processIntensityModel);
		processIntensityModel.setModel(intensityModel);
		
	}
	
	
	public RandomVariableInterface getIntensity(int timeIndex) throws CalculationException {
		return this.intensityModel.getIntensity(timeIndex);
	}


	public RandomVariableInterface getExpOfIntegratedIntensity(int timeIndex) throws CalculationException { 
		return super.getExpOfIntegratedIntensity(timeIndex);
	}

	
	public IntensityModelInterface getIntensityModel() {
		return this.intensityModel;
	}
	
	
	/**
	 * 
	 * @return seed The seed of the correlated Brownian motion used to simulate the underlying and the intensity
	 */
	public int getSeed() {
		return this.seed;
	}
		
	
	
}
