package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import main.net.finmath.antonsporrer.masterthesis.integration.Integration.IntegrationMethod;
import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.ConstrainedWorstCaseCVA;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.IntensityBasedCVA;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndCorrelatedDefaultIntensitySimulation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.Correlation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.SwapConditionalFairValueProcess;
import net.finmath.exception.CalculationException;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

public class IntBasedCWCCVAforDifCor {
	
	
	// Timediscretization 
	private double initialTime = 0.0; 
	private int numberOfTimeSteps; 
	private double timeStepSize = 0.1; 
	
	
	// Random Number Parameters
	private int numberOfPaths = 10000; 
	private int seed = 1337;
	
	
	// Hull White Parameters
	double[] meanReversion;
	double[] volatilities; 
	double[] forwardRates;
	
	
	// CIR Parameters
	private double initialValue = 0.02;
	private double kappa = 0.03;
	private double mu = 0.02; 
	private double nu = 0.02;
	
	
	// Swap Parameters
	private double[] paymentDatesFixingDates;
	private double swapRate = 0.01;
	
	
	// Correlation Parameters
	private double[] correlations;
	
	
	//
	// CVA
	//
	private IntensityBasedCVA intensityBasedCVA = new IntensityBasedCVA(1.0);
	 
	private double penaltyFactor = 60.0;	
	private ConstrainedWorstCaseCVA constrainedWorstCaseCVA = new ConstrainedWorstCaseCVA(1.0);
	 
	
	public IntBasedCWCCVAforDifCor( int numberOfTimeSteps, double[] meanReversion, double[] volatilities, double[] forwardRates, double[] correlations, double[] paymentDatesFixingDates ) {
		
		this.numberOfTimeSteps = numberOfTimeSteps;
		this.meanReversion = meanReversion;
		this.volatilities = volatilities;
		this.forwardRates = forwardRates;
		this.correlations = correlations;
		this.paymentDatesFixingDates = paymentDatesFixingDates;
		
		
	}
	

	public void setInitialTime(double initialTime) {
		this.initialTime = initialTime;
	}


	public void setTimeStepSize(double timeStepSize) {
		this.timeStepSize = timeStepSize;
	}

	
	public void setNumberOfPaths(int numberOfPaths) {
		this.numberOfPaths = numberOfPaths;
	}


	public void setSeed(int seed) {
		this.seed = seed;
	}


	public void setInitialValue(double initialValue) {
		this.initialValue = initialValue;
	}


	public void setKappa(double kappa) {
		this.kappa = kappa;
	}


	public void setMu(double mu) {
		this.mu = mu;
	}


	public void setNu(double nu) {
		this.nu = nu;
	}


	public void setSwapRate(double swapRate) {
		this.swapRate = swapRate;
	}


	public void setPenaltyFactor(double penaltyFactor) {
		this.penaltyFactor = penaltyFactor;
	}


//	public static double testMethod(double one, double two, double three, double four, double five, double six, double seven, double eight) {
//		return 1.0;
//	} 
	
	public  double[][] getIntBasedCWCCVAforCor() {
		
		
		// Hull White Model
		HullWhiteModel underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(initialTime, numberOfTimeSteps, timeStepSize, meanReversion, volatilities, forwardRates, numberOfPaths ); 
		
		
		// CIR Model 
		CIRModel intensityModel = new CIRModel(initialValue, kappa,  mu,  nu);
		
		
		// Swap is set.
		SwapConditionalFairValueProcess<HullWhiteModel> productProcess = new SwapConditionalFairValueProcess<HullWhiteModel>(underlyingModel, paymentDatesFixingDates, swapRate);
		
		
		
		// Declaring the npv and the cva variables.
		
		NPVAndCorrelatedDefaultIntensitySimulation<HullWhiteModel> npvAndDefaultIntensitySimulation = null;
		

		
		
		// 2-dimensional array holding the results. The first row contains the intensity based CVA the second row contains the constrained worst case CVA.
		double[][] results = new double[2][correlations.length];
		
		
		for(int correlationIndex = 0 ; correlationIndex < correlations.length; correlationIndex++) {
			
			CorrelationInterface correlation = new Correlation(new double[][]{{correlations[correlationIndex]},{0.0}});;
			
			npvAndDefaultIntensitySimulation = new NPVAndCorrelatedDefaultIntensitySimulation<HullWhiteModel>(underlyingModel, productProcess, intensityModel , correlation , seed);
			
			try {
				results[0][correlationIndex] = intensityBasedCVA.getCVA(npvAndDefaultIntensitySimulation, IntegrationMethod.Trapezoidal).getAverage();
			} catch (CalculationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				results[1][correlationIndex] = constrainedWorstCaseCVA.getConstrainedWorstCaseCVA(npvAndDefaultIntensitySimulation, penaltyFactor);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CalculationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		
		return results;
		
	} 
	
	
}
