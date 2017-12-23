package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cern.colt.function.IntIntDoubleFunction;
import main.net.finmath.antonsporrer.masterthesis.integration.Integration.IntegrationMethod;
import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.ConstrainedWorstCaseCVA;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.IntensityBasedCVA;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndCorrelatedDefaultIntensitySimulation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensitySimulationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.IntensityModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.Correlation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.CouponBondConditionalFairValueProcess;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.SwapConditionalFairValueProcess;
import net.finmath.exception.CalculationException;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

public class IntensityBasedCVAvsCWCCVAPlot {

	
	private class HWParameter {
		
		private double[] meanReversion;
		
		private double[] volatilities;
		
		private double[] forwardRates;

		
		public HWParameter(double[] meanReversion, double[] volatilities, double[] forwardRates) {
			super();
			this.meanReversion = meanReversion;
			this.volatilities = volatilities;
			this.forwardRates = forwardRates;
		}

		
		public double[] getMeanReversion() {
			return meanReversion;
		}

		public double[] getVolatilities() {
			return volatilities;
		}

		public double[] getForwardRates() {
			return forwardRates;
		}

		
	}
	
	
	private class CIRParameter {
		
		// The Initial Value
		private double initialValue;
		// The Adjustment Speed,
		private double kappa;
		// the Mean
		private double mu;
		// and the Volatility.
		private double nu;
		
		public CIRParameter(double initialValue, double kappa, double mu, double nu) {
			super();
			this.initialValue = initialValue;
			this.kappa = kappa;
			this.mu = mu;
			this.nu = nu;
		}
		
		
		public double getInitialValue() {
			return initialValue;
		}



		public double getKappa() {
			return kappa;
		}


		public double getMu() {
			return mu;
		}


		public double getNu() {
			return nu;
		}

		
		@Override
		public String toString() {
			return "CIRParameter [initialValue=" + initialValue + ", kappa=" + kappa + ", mu=" + mu + ", nu=" + nu
					+ "]";
		}

	}
	
	
	private class BondParameter {
		
		private double[] paymentDates;
		
		private double[] periodFactors;
		
		private double[] coupons;

		public BondParameter(double[] paymentDates, double[] periodFactors, double[] coupons) {
			super();
			this.paymentDates = paymentDates;
			this.periodFactors = periodFactors;
			this.coupons = coupons;
		}

		
		public double[] getPaymentDates() {
			return paymentDates;
		}

		public double[] getPeriodFactors() {
			return periodFactors;
		}

		public double[] getCoupons() {
			return coupons;
		}
		
	}
	
	
	private class SwapParameter {
		
		double[] paymentDatesFixingDates;
		
		double swapRate;

		public SwapParameter(double[] paymentDatesFixingDates, double swapRate) {
			super();
			this.paymentDatesFixingDates = paymentDatesFixingDates;
			this.swapRate = swapRate;
		}

		public double[] getPaymentDatesFixingDates() {
			return paymentDatesFixingDates;
		}

		public double getSwapRate() {
			return swapRate;
		}
		
	}
	
	
	public static void main(String[] args) {
		
		
		IntensityBasedCVAvsCWCCVAPlot intensityBasedCVAvsCWCCVAPlot = new IntensityBasedCVAvsCWCCVAPlot();
		
		// Random Numbers
		int numberOfPaths = 10000;
		int seed = 186;
		
		// Time Discretization 
		double initialTime = 0.0;
		int numberOfTimeSteps = 100;
		double timeStepSize = 10.0 * 1.0 / numberOfTimeSteps;
		
		// CVAs
		IntensityBasedCVA intensityBasedCVA = new IntensityBasedCVA(1.0);
		double penaltyFactor = 60;
		ConstrainedWorstCaseCVA constrainedWorstCaseCVA = new ConstrainedWorstCaseCVA(1.0);
		
		// Model Parameters
		List<HWParameter> hwParameterList = new ArrayList<HWParameter>();
		List<CIRParameter> cirParameterList = new ArrayList<CIRParameter>();
		

		List<BondParameter> bondParameterList = new ArrayList<BondParameter>();
		List<SwapParameter> swapParameterList = new ArrayList<SwapParameter>();
		
		List<Double> correlationList = new ArrayList<Double>();
		
		////
		// Fill parameter lists.
		////
		
		// CIR Parameters
		cirParameterList.add( intensityBasedCVAvsCWCCVAPlot.new CIRParameter(0.01, 0.03, 0.02, 0.022) );
		
		
		// Hull White Parameters
		double[] meanReversion = new double[numberOfTimeSteps+1];
		double[] volatilities = new double[numberOfTimeSteps+1];
		double[] forwardRates = new double[] {0.02, 0.02, 0.01, 0.02, 0.01};
		
		for(int index = 0; index < numberOfTimeSteps + 1; index++) {
			meanReversion[index] = 0.05;
			volatilities[index] = 0.03;
		}
		
		hwParameterList.add(intensityBasedCVAvsCWCCVAPlot.new HWParameter(meanReversion, volatilities, forwardRates));
		
		
		// Correlation Parameters
		for(int index = 0; index < 19; index++ ) {
			correlationList.add(-0.9 + index * 0.1);
		}
		
		// Bond Parameters
		double[] paymentDates = new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
		double[] periodFactors = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		double[] coupons = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 };
		bondParameterList.add(intensityBasedCVAvsCWCCVAPlot.new BondParameter(paymentDates, periodFactors, coupons));
		
		// Swap Parameters
		double[] paymentDatesFixingDates = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0}; 
		double swapRate = 0.01;
		swapParameterList.add(intensityBasedCVAvsCWCCVAPlot.new SwapParameter(paymentDatesFixingDates, swapRate));
		
		
//		List<List<List<List<Double>>>> results = new ArrayList<List<List<List<Double>>>>();
//		Map<String, Double> results = new HashMap<String, Double>();
		
		
		// Result Containers
		// Little safety precaution (error check and handling)
		if(bondParameterList.size() < swapParameterList.size()) {throw new IllegalArgumentException("Number of different bond parameter settings has to be equal to the number of swap parameter settings.");}
		int productDimension = bondParameterList.size();
		double[][][][][] resultsInt = new double[2][productDimension][hwParameterList.size()][cirParameterList.size()][correlationList.size()];
		double[][][][][] resultsWC = new double[2][productDimension][hwParameterList.size()][cirParameterList.size()][correlationList.size()];
		

		HullWhiteModel underlyingModel;
		IntensityModelInterface intensityModel;
		ProductConditionalFairValueProcessInterface<HullWhiteModel> productProcess;
		CorrelationInterface correlation;
		NPVAndDefaultIntensitySimulationInterface<HullWhiteModel> npvAndDefaultIntensitySimulation;
		ProductConditionalFairValueProcessInterface<HullWhiteModel> productProcessBond;
		ProductConditionalFairValueProcessInterface<HullWhiteModel> productProcessSwap;
		
		
		HWParameter hwElement;
		CIRParameter cirElement;
		Double correlationElement;
		BondParameter bondElement;
		SwapParameter swapElement;
		
		for( int hwIndex = 0; hwIndex < hwParameterList.size(); hwIndex++) {
			
			
			
			for(int cirIndex = 0; cirIndex < cirParameterList.size(); cirIndex++) {
				
				
				
				
				for(int corIndex = 0; corIndex < correlationList.size(); corIndex++) {
					
					

					// Hull White Model is set.
					hwElement = hwParameterList.get(hwIndex);
					underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(initialTime, numberOfTimeSteps, timeStepSize, hwElement.getMeanReversion(), hwElement.getVolatilities(), hwElement.getForwardRates(), numberOfPaths ); 
					
					
					// CIR Model is set.
					cirElement = cirParameterList.get(cirIndex);
					intensityModel = new CIRModel(cirElement.getInitialValue(), cirElement.getKappa(), cirElement.getMu(), cirElement.getNu());
					
					
					// Standard Product, NPV and correlation is set.
					productProcess = new CouponBondConditionalFairValueProcess<HullWhiteModel>(underlyingModel, new double[] {10.0}, new double[] {1.0}, new double[] {1.0});
					
					correlationElement = correlationList.get(corIndex);
					correlation = new Correlation(new double[][]{{correlationElement},{0.0}});;
					
					npvAndDefaultIntensitySimulation = new NPVAndCorrelatedDefaultIntensitySimulation<HullWhiteModel>(underlyingModel, productProcess, intensityModel , correlation , seed);
					

					
					for(int bondIndex = 0; bondIndex < bondParameterList.size(); bondIndex++ ) {
					
						bondElement = bondParameterList.get(bondIndex);
						
						// Create bond
						productProcessBond = new CouponBondConditionalFairValueProcess<HullWhiteModel>(underlyingModel, bondElement.getPaymentDates(), bondElement.getPeriodFactors(), bondElement.getCoupons());
						
						// Plug bond in
						npvAndDefaultIntensitySimulation.plugInProductProcess(productProcessBond);
						
						try {
							
							double intensityBasedCVAValue = intensityBasedCVA.getCVA(npvAndDefaultIntensitySimulation, IntegrationMethod.Trapezoidal).getAverage();
							double cWCCVAValue = constrainedWorstCaseCVA.getConstrainedWorstCaseCVA(npvAndDefaultIntensitySimulation, penaltyFactor);
							System.out.println("Bond:");
							System.out.println("Correlation : " + correlationList.get(corIndex));
							System.out.println("Intensity based CVA: "+ intensityBasedCVAValue);
							System.out.println("Constrained WC CVA Value : " + cWCCVAValue);
							resultsInt[0][bondIndex][hwIndex][cirIndex][corIndex] = intensityBasedCVAValue;
							resultsWC[0][bondIndex][hwIndex][cirIndex][corIndex] = cWCCVAValue;

						} catch (CalculationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					
					for(int swapIndex = 0; swapIndex < swapParameterList.size(); swapIndex++) {
						
						swapElement = swapParameterList.get(swapIndex);
						
						// Create swap
						productProcessSwap = new SwapConditionalFairValueProcess<HullWhiteModel>(underlyingModel, swapElement.getPaymentDatesFixingDates(), swapElement.getSwapRate());
						
						// Plug swap in
						npvAndDefaultIntensitySimulation.plugInProductProcess(productProcessSwap);
						
						try {
							
							
							double intensityBasedCVAValue = intensityBasedCVA.getCVA(npvAndDefaultIntensitySimulation, IntegrationMethod.Trapezoidal).getAverage();
							double cWCCVAValue = constrainedWorstCaseCVA.getConstrainedWorstCaseCVA(npvAndDefaultIntensitySimulation, penaltyFactor);
							System.out.println("Swap:");
							System.out.println("Correlation : " + correlationList.get(corIndex));
							System.out.println("Intensity based CVA: "+ intensityBasedCVAValue);
							System.out.println("Constrained WC CVA Value : " + cWCCVAValue);
							resultsInt[1][swapIndex][hwIndex][cirIndex][corIndex] = intensityBasedCVAValue;
							resultsWC[1][swapIndex][hwIndex][cirIndex][corIndex] = cWCCVAValue;
							
							
							
							
//							resultsInt[1][swapIndex][hwIndex][cirIndex][corIndex] = intensityBasedCVA.getCVA(npvAndDefaultIntensitySimulation, IntegrationMethod.Trapezoidal).getAverage();
//							resultsWC[1][swapIndex][hwIndex][cirIndex][corIndex] = constrainedWorstCaseCVA.getConstrainedWorstCaseCVA(npvAndDefaultIntensitySimulation, penaltyFactor);

						} catch (CalculationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
					
					
				}
				
			}
		
		}
		
		System.out.println();
		
		
	}

}
