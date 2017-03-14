package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import main.net.finmath.antonsporrer.masterthesis.integration.Integration.IntegrationMethod;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ZCBond_ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.IntensityBasedCVA;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndCorrelatedDefaultIntensitySimulation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensitySimulationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.IntensityModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.Correlation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.CouponBondConditionalFairValueProcess;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

public class SimpleCVACostumHWTestDrive {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws CalculationException {

		double lossGivenDefault = 1.0;
		IntensityBasedCVA intensityBasedCVA = new IntensityBasedCVA(lossGivenDefault);
		
		int numberOfPaths = 1000;
		
		// ZCBond_ProductConditionalFairValue_ModelInterface underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5); 
		
		double[] meanReversion = new double[21];
		double[] volatilities = new double[21];
		double[] forwardRates = new double[] {0.02, 0.05, 0.01, 0.05, 0.01};
		
		for(int index = 0; index < 21; index++) {
			meanReversion[index] = 0.05;
			volatilities[index] = 0.03;
		}
		
		
		ZCBond_ProductConditionalFairValue_ModelInterface underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5, meanReversion, volatilities, forwardRates, numberOfPaths ); 
		
		@SuppressWarnings("rawtypes")
		ProductConditionalFairValueProcessInterface productProcess = new CouponBondConditionalFairValueProcess(underlyingModel, new double[] {10.0}, new double[] {1.0}, new double[] {1.0});
		
		IntensityModelInterface intensityModel = new CIRModel(0.02, 0.05 , 0.02, 0.03);
		
		
		CorrelationInterface correlation = new Correlation(new double[][]{{0.9},{0.0}});
		
		
		
		NPVAndDefaultIntensitySimulationInterface npvAndDefaultIntensitySimulation = new NPVAndCorrelatedDefaultIntensitySimulation<ZCBond_ProductConditionalFairValue_ModelInterface>(underlyingModel, productProcess, intensityModel , correlation , 3142);
		
		System.out.println( "The CVA with LGD of " + lossGivenDefault + " is: "  + intensityBasedCVA.getCVA( npvAndDefaultIntensitySimulation, IntegrationMethod.Trapezoidal ).getAverage() );
		
		System.out.println("The Product Value is: " + productProcess.getFairValue(0).getAverage());
		
	}

}
