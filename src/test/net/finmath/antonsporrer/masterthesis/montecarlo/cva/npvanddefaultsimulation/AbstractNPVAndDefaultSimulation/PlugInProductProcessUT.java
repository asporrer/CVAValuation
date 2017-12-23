/* 
 * Contact: anton.sporrer@yahoo.com
 */

package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.npvanddefaultsimulation.AbstractNPVAndDefaultSimulation;

import org.junit.Assert;
import org.junit.Test;

import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndCorrelatedDefaultIntensitySimulation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.Correlation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.CouponBondConditionalFairValueProcess;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;


/**
 * 
 * Unit test for {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation #plugInProductProcess }
 * 
 * @author Anton Sporrer
 *
 */
public class PlugInProductProcessUT {

	@Test
	public void testExample1() throws CalculationException {
		
		
		HullWhiteModel underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 100, 0.01, 100000);
		
		ProductConditionalFairValueProcessInterface<HullWhiteModel> productProcess1 = new CouponBondConditionalFairValueProcess<HullWhiteModel>(underlyingModel, new double[] {1.0}, new double[] {1.0}, new double[] {0.0});
		ProductConditionalFairValueProcessInterface<HullWhiteModel> productProcess2 = new CouponBondConditionalFairValueProcess<HullWhiteModel>(null, new double[] {1.0}, new double[] {1.0}, new double[] {0.0});
		CIRModel intensityModel = new CIRModel(0.01, 0.02, 0.03, 0.005);
		CorrelationInterface correlation = new Correlation(new double[][]{{0.9},{0.0}});
		
		double startTime = System.currentTimeMillis();
		NPVAndCorrelatedDefaultIntensitySimulation<HullWhiteModel> npvAndCorDefSimulation = new NPVAndCorrelatedDefaultIntensitySimulation<HullWhiteModel>(underlyingModel, productProcess1, intensityModel, correlation, 3142);
		
		npvAndCorDefSimulation.getDiscountedNPV(1, 1);
		npvAndCorDefSimulation.getIntensity(1);
		double endTime = System.currentTimeMillis();
		
		System.out.println("Calculation Time: " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		npvAndCorDefSimulation.plugInProductProcess(productProcess2);
		npvAndCorDefSimulation.getDiscountedNPV(1, 1);
		npvAndCorDefSimulation.getIntensity(1);
		endTime = System.currentTimeMillis();
		
		Assert.assertEquals(npvAndCorDefSimulation.getProductProcess(), productProcess2);
		System.out.println( "Is the new Product used? " + (npvAndCorDefSimulation.getProductProcess() == productProcess2) );
		
		Assert.assertEquals(npvAndCorDefSimulation.getProductProcess().getUnderlyingModel(), underlyingModel);
		System.out.println( "Is old underlying model used? " + (npvAndCorDefSimulation.getProductProcess().getUnderlyingModel() == underlyingModel) );
		
		System.out.println("Calculation Time: " + (endTime - startTime));

	}

}
