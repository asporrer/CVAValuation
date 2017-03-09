package main.net.finmath.antonsporrer.masterthesis.montecarlo;

import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * 
 * @author Anton Sporrer
 *
 */
public interface ZCBond_ProductConditionalFairValue_ModelInterface extends ProductConditionalFairValue_ModelInterface{

	public RandomVariableInterface getZeroCouponBond(double evaluationTime, double maturity);
	
}
