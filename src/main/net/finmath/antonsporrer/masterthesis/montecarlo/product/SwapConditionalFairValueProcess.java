package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.AbstractZCBond_ProductConditionalFairValue_Model;

public class SwapConditionalFairValueProcess extends AbstractZCBondProductConditionalFairValueProcess<AbstractZCBond_ProductConditionalFairValue_Model>{

	public SwapConditionalFairValueProcess(
			AbstractZCBond_ProductConditionalFairValue_Model underlyingModel) {
		super(underlyingModel);
		// TODO Auto-generated constructor stub
	}

	public RandomVariableInterface getFairValue(int timeIndex)
			throws CalculationException {
		// TODO Auto-generated method stub
		return null;
	}

}
