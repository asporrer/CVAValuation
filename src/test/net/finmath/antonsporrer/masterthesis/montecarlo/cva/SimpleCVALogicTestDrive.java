package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import net.finmath.exception.CalculationException;
import main.net.finmath.antonsporrer.masterthesis.integration.Integration;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ZCBond_ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.IntensityBasedCVA;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndCorrelatedDefaultIntensitySimulation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensitySimulationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.IntensityModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.CouponBondConditionalFairValueProcess;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

public class SimpleCVALogicTestDrive {

	public static void main(String[] args) throws CalculationException {
		
		IntensityBasedCVA intensityBasedCVA = new IntensityBasedCVA(1.0);
		
		ZCBond_ProductConditionalFairValue_ModelInterface underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5); 
		/* AbstractProductConditionalFairValueProcess<AbstractZCBond_ProductConditionalFairValue_Model> */  CouponBondConditionalFairValueProcess productProcess = new CouponBondConditionalFairValueProcess(underlyingModel, new double[] {1.0}, new double[] {1.0}, new double[] {1.0});
		
		IntensityModelInterface intensityModel = new CIRModel(1.0, 1.0 , 1.0, 1.0);
		CorrelationInterface correlation = null;
		
		@SuppressWarnings("rawtypes")
		NPVAndDefaultIntensitySimulationInterface npvAndDefaultIntensitySimulation = new NPVAndCorrelatedDefaultIntensitySimulation<ZCBond_ProductConditionalFairValue_ModelInterface>(underlyingModel, productProcess, intensityModel , correlation , 3141);
		//NPVAndCorrelatedDefaultIntensitySimulation<AbstractZCBond_ProductConditionalFairValue_Model> npvAndDefaultIntensitySimulation = new NPVAndCorrelatedDefaultIntensitySimulation<AbstractZCBond_ProductConditionalFairValue_Model>(underlyingModel, productProcess, intensityModel, correlation, seed)
		
		intensityBasedCVA.getCVA( npvAndDefaultIntensitySimulation, Integration.IntegrationMethod.LeftPoints);
		
	}
	
}
