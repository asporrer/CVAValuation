package test.net.finmath.antonsporrer.masterthesis.prototyping.proofofconcept;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.Correlation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.BrownianMotionView;
import net.finmath.montecarlo.CorrelatedBrownianMotion;
import net.finmath.montecarlo.model.AbstractModelInterface;
import net.finmath.montecarlo.process.AbstractProcessInterface;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.time.TimeDiscretizationInterface;

public class CVA {

	// Mode
	AbstractModelInterface underlyingModel; // Creating new underlying interface compatible with an intensity.
	AbstractModelInterface intensityModel; // Creating new intensity interface could be helpful.
	
	// ValueProcess
	
	
	// Product
	
	
	// Correlation
	CorrelationInterface correlation;
	
	int numberOfPaths;
	int seed;
	
	
	public CVA( AbstractModelInterface underlyingModel, AbstractModelInterface intensityModel, double[][] interCorrelationMatrix, int numberOfPaths, int seed ) {
		
		this(underlyingModel, intensityModel, new Correlation(interCorrelationMatrix), numberOfPaths, seed);
	
	}
	
	
	public CVA( AbstractModelInterface underlyingModel, AbstractModelInterface intensityModel, CorrelationInterface correlation, int numberOfPaths, int seed) {
		
		//ToDo: throw exception if matrix formats are inconsistent or if discretizations are not equal.
		this.correlation = correlation;
		this.numberOfPaths = numberOfPaths;
		this.seed = seed;
		correlateUnderlyingAndIntensity(underlyingModel, intensityModel, correlation, numberOfPaths, seed);
		
	}
	
	
	//The following methods are used to correlate the underlying and the intensity model.
	private void correlateUnderlyingAndIntensity(AbstractModelInterface underlyingModelParameter, AbstractModelInterface intensityModelParameter, CorrelationInterface correlation, int numberOfPaths, int seed) {
		
		// First an uncorrelated Brownian motion is needed. 
		// This Brownian motion has the time discretization format of the underlying model.
		BrownianMotionInterface uncorrelatedBrownianMotion = new BrownianMotion(underlyingModelParameter.getTimeDiscretization(),correlation.getNumberOfRows(), numberOfPaths, seed);
		
		// From this uncorrelated Brownian motion and the factor matrix with respect to the 
		// correlation matrix a correlated Brownian motion is created.
		BrownianMotionInterface correlatedBrownianMotion = new CorrelatedBrownianMotion(uncorrelatedBrownianMotion, correlation.getCorrelationFactorMatrix() );
		
		// Getting the number of underlying respectively intensity model factors in order to
		// assign the Integer arrays accordingly.
		int numberOfUnderlyingFactors = correlation.getNumberOfInterCorrelationRows();
		int numberOfIntensityFactors = correlation.getNumberOfInterCorrelationColumns();
		
		// Parameters used for BrownianMotionView
		Integer[] factorsUnderlying = new Integer[numberOfUnderlyingFactors] ;
		Integer[] factorsIntensity = new Integer[numberOfIntensityFactors];
		
		for(int i = 0; i < numberOfUnderlyingFactors; i++) {
			factorsUnderlying[i] = i;
		}
		
		for(int i = numberOfUnderlyingFactors; i< numberOfUnderlyingFactors + numberOfIntensityFactors; i++) {
			factorsIntensity[i-numberOfUnderlyingFactors] = i;
		}
	
		// Creating Brownian motions which are correlated with each other.
		BrownianMotionInterface underlyingModelBrownianMotion = new BrownianMotionView(correlatedBrownianMotion, factorsUnderlying );
		BrownianMotionInterface intensityModelBrownianMotion = new BrownianMotionView(correlatedBrownianMotion, factorsIntensity );
		
		// Using the above Brownian motions to create processes.
		AbstractProcessInterface processUnderlying = new ProcessEulerScheme(underlyingModelBrownianMotion);
		AbstractProcessInterface processIntensity = new ProcessEulerScheme(intensityModelBrownianMotion);
		
		// Linking the processes and the models.
		underlyingModelParameter.setProcess(processUnderlying);
		processUnderlying.setModel(underlyingModelParameter);
		
		intensityModelParameter.setProcess(processIntensity);
		processIntensity.setModel(intensityModelParameter);
		
		// Assigning the correlated models.
		this.underlyingModel = underlyingModelParameter;
		this.intensityModel = intensityModelParameter;
		
	}
	
	
	public int getSeed() {
		return seed;
	}
	
	public int getNumberOfPaths() {
		return numberOfPaths;
	}

	public AbstractModelInterface getUnderlyingModel() {
		return underlyingModel;
	}
	
	public AbstractModelInterface getIntensityModel() {
		return intensityModel;
	}
	
}
