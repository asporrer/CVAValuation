package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ZCBond_ProductConditionalFairValue_ModelInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

public class SwapConditionalFairValueProcess extends AbstractProductConditionalFairValueProcess<ZCBond_ProductConditionalFairValue_ModelInterface>{

	
	
	
	
	public SwapConditionalFairValueProcess(
			ZCBond_ProductConditionalFairValue_ModelInterface underlyingModel) {
		super(underlyingModel);
		// TODO Auto-generated constructor stub
	}

	public RandomVariableInterface getFairValue(int timeIndex)
			throws CalculationException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
