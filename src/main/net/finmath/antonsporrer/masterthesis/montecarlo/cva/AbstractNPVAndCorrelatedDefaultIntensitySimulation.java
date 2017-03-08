package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.AbstractIntensityModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.IntensityModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.AbstractProductConditionalFairValueProcess;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;



/**
 * 
 * 
 * 
 * @author Anton Sporrer
 *
 */
public abstract class AbstractNPVAndCorrelatedDefaultIntensitySimulation extends AbstractNPVAndDefaultIntensitySimulation{


	IntensityModelInterface intensityModel;

	CorrelationInterface underlyingIntensityCorrelation;
	

	// TODO do implement correlation of B.m. here. Such that there is no duplicate code.
		
	public AbstractNPVAndCorrelatedDefaultIntensitySimulation(
			ProductConditionalFairValue_ModelInterface underlyingModel,
			AbstractProductConditionalFairValueProcess productProcess, IntensityModelInterface intensityModel, CorrelationInterface correlation) {
		super(underlyingModel, productProcess);
		// TODO: Correlate intensity model and underlyingModel.
	}
	
	
}
