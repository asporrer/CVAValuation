package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.AbstractProductConditionalFairValueProcess;


/**
 * 
 * Function and intensity model have to be compatible!
 * 
 * @author Anton Sporrer
 *
 */
public abstract class AbstractNPVAndDefaultIntensityFunctionSimulation extends AbstractNPVAndDefaultIntensitySimulation{

	//TODO: Create Class Default Intensity Function and add to constructor.
	
	public AbstractNPVAndDefaultIntensityFunctionSimulation(
			ProductConditionalFairValue_ModelInterface underlyingModel,
			AbstractProductConditionalFairValueProcess productProcess) {
		super(underlyingModel, productProcess);
	}

	


	
	
}
