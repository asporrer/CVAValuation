package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.npvanddefaultsimulation;

import net.finmath.exception.CalculationException;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;
import main.net.finmath.antonsporrer.masterthesis.function.IntensityFunctionSwitchShiftFloor;
import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.IntensityFunctionArgumentModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ZCBond_ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensityFunctionSimulation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensitySimulationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products.conditionalproducts.ConditionalFairValueProductInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.CouponBondConditionalFairValueProcess;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;

public class SimpleNPVAndDefaultIntensityFunctionSimulationTestDrive {

	public static void main(String[] args) throws CalculationException {
		
		HullWhiteCreationHelper hullWhiteCreationHelper = new HullWhiteCreationHelper();
		HullWhiteModel underlyingModel = hullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5, 10);
		
		ProductConditionalFairValueProcessInterface conditionalFairValueProduct = new CouponBondConditionalFairValueProcess(underlyingModel, new double[] {10.0}, new double[] {0.0}, new double[] {0.0});
		
		NPVAndDefaultIntensityFunctionSimulation abc = new NPVAndDefaultIntensityFunctionSimulation<HullWhiteModel>(underlyingModel, conditionalFairValueProduct, 3141, new IntensityFunctionSwitchShiftFloor(0.03));
		// abc.getProductProcess().getUnderlying(timeIndex, componentIndex);
		
		// System.out.println(abc.getI);
		
	}

}
