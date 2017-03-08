package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import net.finmath.stochastic.RandomVariableInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.AbstractProductConditionalFairValueProcess;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;

public abstract class AbstractNPVAndDefaultIntensitySimulation extends AbstractNPVAndDefaultSimulation {

	public AbstractNPVAndDefaultIntensitySimulation(
			ProductConditionalFairValue_ModelInterface underlyingModel,
			AbstractProductConditionalFairValueProcess productProcess) {
		super(underlyingModel, productProcess);
	}



	
	
	
	
}
