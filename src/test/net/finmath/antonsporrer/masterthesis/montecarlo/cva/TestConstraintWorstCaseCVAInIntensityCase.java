package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import main.net.finmath.antonsporrer.masterthesis.integration.Integration.IntegrationMethod;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ZCBond_ProductConditionalFairValue_ModelInterface;
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
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

public class TestConstraintWorstCaseCVAInIntensityCase {

	public static void main(String[] args) throws InterruptedException, ExecutionException, CalculationException {
		
		double lossGivenDefault = 1.0;
		IntensityBasedCVA intensityBasedCVA = new IntensityBasedCVA(lossGivenDefault);
		
		int numberOfPaths = 1000000;
		
		ZCBond_ProductConditionalFairValue_ModelInterface underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5, numberOfPaths); 
		@SuppressWarnings("rawtypes")
		ProductConditionalFairValueProcessInterface productProcess = new CouponBondConditionalFairValueProcess(underlyingModel, new double[] {10.0}, new double[] {1.0}, new double[] {1.0});
		
		IntensityModelInterface intensityModel = new CIRModel(0.03, 0.7 , 0.03, 0.07);
		
		
		CorrelationInterface correlation = new Correlation(new double[][]{{-0.9},{0.0}});
		
		
		NPVAndDefaultIntensitySimulationInterface npvAndDefaultIntensitySimulation = new NPVAndCorrelatedDefaultIntensitySimulation<ZCBond_ProductConditionalFairValue_ModelInterface>(underlyingModel, productProcess, intensityModel , correlation , 3142);
		
		System.out.println( "The CVA with LGD of " + lossGivenDefault + " is: "  + intensityBasedCVA.getCVA( npvAndDefaultIntensitySimulation, IntegrationMethod.LeftPoints ).getAverage() );
		
		System.out.println("The Product Value is: " + productProcess.getFairValueNonMultiCurve(0).getAverage());
		
		
		ConstrainedWorstCaseCVA worstCaseCVA = new ConstrainedWorstCaseCVA(1.0, 0.00000001, 0.000000001, 0.00000000000001);
		
		
		double startTime = System.currentTimeMillis();
		
		double result = worstCaseCVA.getConstrainedWorstCaseCVA( npvAndDefaultIntensitySimulation, 0.0001);
		
		double endTime = System.currentTimeMillis();
		
		System.out.println("Time passed: " + (endTime - startTime));
		System.out.println("Result: " + result);
	
	}

}
