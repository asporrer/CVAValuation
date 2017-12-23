package test.net.finmath.antonsporrer.masterthesis.experiments.proofofconcept;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.BrownianMotionView;
import net.finmath.montecarlo.CorrelatedBrownianMotion;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.montecarlo.assetderivativevaluation.BlackScholesModel;
import net.finmath.montecarlo.process.AbstractProcess;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;
import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.AbstractIntensityModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.IntensityModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;

public class CVACalculationIntensityVsWorstCase {
//
//	public enum CVAMethod { IndependentCVA, WorstCase, WorstCaseWithConstraint, CorrelatedIntensity, FunctionalIntensity}
//	
//	// TODO: SimulationInterfaces?
//	
//	ConditionalBondFormulaModelInterface bondModel;
//	AbstractIntensityModel intensityModel;
//	
//	CorrelationInterface correlation;
//	
//	
//	int seed;
//	int numberOfPaths;
//	TimeDiscretizationInterface timeDiscretization;
//	
//	
//	ConditionalFairValueProductInterface conditionalFairValueProduct; 
//	
//	double[] defaultProbabilites;
//	
//	double correlatedIntensityCVA = -1;
//	double worstCaseCVA = -1;
//	
//	
//	public CVACalculationIntensityVsWorstCase(ConditionalBondFormulaModelInterface conditionalBondFormulaModel, AbstractIntensityModel intensityModel, CorrelationInterface correlation, ConditionalFairValueProductInterface conditionalFairValueProduct, TimeDiscretizationInterface timeDiscretization, int numberOfPaths, int seed) {
//
//		super();
//		this.bondModel = conditionalBondFormulaModel;
//		this.intensityModel = intensityModel;
//		this.correlation = correlation;
//		
//		this.conditionalFairValueProduct = conditionalFairValueProduct;
//		
//		this.timeDiscretization = timeDiscretization;
//		this.numberOfPaths = numberOfPaths;
//		this.seed = seed;
//		
//		//TODO: Call this method only if getCVA() is called.
//		createCorrelatedModels();
//		
//	}
//	
//	/**
//	 * 
//	 */
//	private void createCorrelatedModels() {
//		
//		// The dimension or in other words the number of factors of the Brownian motion is calculated.
//		// TODO: Check if models provide desired numbers in any case.
//		int numberOfFactors = correlation.getNumberOfRows();
//		
//		// An correlated Brownian motion is created from an uncorrelated Brownian motion and factor loadings
//		// provided by the correlation.
//		BrownianMotionInterface uncorrelatedBrownianMotion = new BrownianMotion(timeDiscretization, numberOfFactors, numberOfPaths, seed);
//		BrownianMotionInterface correlatedBrownianMotion = new CorrelatedBrownianMotion(uncorrelatedBrownianMotion, correlation.getCorrelationFactorMatrix());
//					
//	
//		////
//		// Now the correlated Brownian motion will be split into two parts.
//		////
//		
//		int numberOfBondModelFactors = correlation.getNumberOfInterCorrelationRows();
//		int numberOfItensityModelFactors = correlation.getNumberOfInterCorrelationColumns();
//		Integer[] bmFactorsForBondModel = new Integer[numberOfBondModelFactors]; 
//		Integer[] bmFactorsForIntensity = new Integer[numberOfItensityModelFactors];
//		
//		for(int index = 0; index < numberOfBondModelFactors; index++) {
//			bmFactorsForBondModel[index] = index;
//		}
//		
//		for(int index = 0; index < numberOfItensityModelFactors; index++) {
//			bmFactorsForIntensity[index] = index + numberOfBondModelFactors;
//		}
//
//		BrownianMotionInterface brownianMotionBondModel = new BrownianMotionView( correlatedBrownianMotion, bmFactorsForBondModel);
//		BrownianMotionInterface brownianMotionIntensityModel = new BrownianMotionView( correlatedBrownianMotion, bmFactorsForIntensity);
//		
//		
//		////
//		// Each Brownian motion will be passed to a different process.
//		// Thereby the processes will be correlated. One process will be 
//		// passed to the bondModel and the other to the 
//		// intensityModel.
//		////
//		AbstractProcess processBondModel = new ProcessEulerScheme(brownianMotionBondModel);
//		AbstractProcess processIntensityModel = new ProcessEulerScheme(brownianMotionIntensityModel);
//
//		bondModel.setProcess(processBondModel);
//		processBondModel.setModel(bondModel);
//		
//		intensityModel.setProcess(processIntensityModel);
//		processIntensityModel.setModel(intensityModel);
//	}
//	
//	
//	/**
//	 * This method returns the CVA at time zero of the conditionalFairValueProduct.
//	 * The prices process is modeled by an conditionalBondFormulaModel.
//	 * The default is modeled by an intensity based approach using the intensityModel.
//	 * The formula stated in Credit Risk, Bielecki and Rutkoski page 144 is
//	 * used to calculate the CVA.
//	 * 
//	 * TODO: Maybe introduce a class providing different methods of integration.
//	 * 
//	 * @return CVA of the product underlying this class.
//	 * @throws CalculationException 
//	 */
//	private void calculateCorrelatedIntensityCVA() throws CalculationException {
//		
//		// The time discretization used for the simulation steps.
//		TimeDiscretizationInterface timeDiscretization = intensityModel.getTimeDiscretization();
//		
//		int numberOfTimeSteps = timeDiscretization.getNumberOfTimeSteps();
//		
//		// This variable will be calculated successively in the following for loop. 
//		RandomVariableInterface cva = new RandomVariable(0.0);
//		
//		// t.
//		double currentTime = 0.0;
//		
//		// t_(i+1) - t_(i).
//		RandomVariableInterface currentDeltaT = null;
//		
//		// Storing V^+(t)/N(t) where V^+  is the fair value of 
//		// the product floored at zero and N is the numéraire 
//		// of the conditional bond model. t is the current time 
//		// at the current time index with respect to the following
//		// for loop.
//		RandomVariableInterface currentFairValueOfProduct_DividedByCurrentNumeraire = null;
//		
//		// Default intensity.
//		RandomVariableInterface currentIntensity = null;
//		
//		// Approximation of exp(\int_0^tn intensity(s) ds ) by exp( sum_(i = 0)^n (t_(i+1) - t_(i)) intensity(t_i) ).
//		RandomVariableInterface currentExponentialFunctionOfIntensityIntegral = new RandomVariable(1.0);
//		
//		
//		// TODO: Improve integral approximation scheme. Splines; common distribution of delta lambda  and delta exp( int lambda ).
//		for(int timeIndex = 0; timeIndex < numberOfTimeSteps; timeIndex++) {
//			
//			currentTime = timeDiscretization.getTime(timeIndex);
//			
//			currentDeltaT = new RandomVariable(timeDiscretization.getTime(timeIndex+1) -  currentTime);
//			
//			// V^+(t_timeIndex)/N(t_timeIndex)
//			currentFairValueOfProduct_DividedByCurrentNumeraire = conditionalFairValueProduct.getFairValue(currentTime, bondModel).div(bondModel.getNumeraire(currentTime));
//
//			
//			// intensity(t_timeIndex)
//			currentIntensity = intensityModel.getIntensity(timeIndex);
//			
//			// Calculating P(bondMaturity ; t_timeIndex)/N(t_timeIndex)  *  exp( sum_(k = 0)^(i-1) (t_(k+1) - t_(k)) intensity(t_k)  )  *  intensity(t_i)  *  ( (t_(i+1) - t_(i)).
//			cva = cva.addProduct( currentFairValueOfProduct_DividedByCurrentNumeraire.mult(currentExponentialFunctionOfIntensityIntegral).mult(currentIntensity)  , currentDeltaT);
//			
//			// Multiplying the new intensity step for the next iteration.
//			// Calculating exp( sum_(k = 0)^(i-1) (t_(k+1) - t_(k)) intensity(t_k)  ) *  exp(  (t_(i+1) - t_(i)) intensity(t_i) ).
//			currentExponentialFunctionOfIntensityIntegral = currentExponentialFunctionOfIntensityIntegral.mult( currentIntensity.mult(currentDeltaT).exp() );
//			
//		}
//	
//		correlatedIntensityCVA = cva.getAverage();
//	}
//	
//	
//	public void calculateWorstCaseCVA() {
//		
//	}
//	
//	
//	public double getCVA(CVAMethod cvaMethod) throws CalculationException {
//		
//		double specifiedCVA = 0.0;
//		
//		switch(cvaMethod) {
//			case CorrelatedIntensity: 
//				if (correlatedIntensityCVA == -1) { calculateCorrelatedIntensityCVA(); } 
//				specifiedCVA = correlatedIntensityCVA;
//				break;
//			// TODO: Implement further methods.
//			default: throw new UnsupportedOperationException("The selected method is not supported yet.");
//		}
//		
//		return specifiedCVA;
//		
//	}
//	
//	
}
