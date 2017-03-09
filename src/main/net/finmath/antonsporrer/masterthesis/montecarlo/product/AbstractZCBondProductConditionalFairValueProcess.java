package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.AbstractZCBond_ProductConditionalFairValue_Model;

public abstract class AbstractZCBondProductConditionalFairValueProcess< T extends AbstractZCBond_ProductConditionalFairValue_Model> extends AbstractProductConditionalFairValueProcess<T> {

	
	public AbstractZCBondProductConditionalFairValueProcess(
			T underlyingModel) {
		super(underlyingModel);

	}

	
}
