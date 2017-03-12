package main.net.finmath.antonsporrer.masterthesis.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * All model implementing this interface should provide the fair value of a
 * zero coupon bond maturing at maturity conditional on path-wise states of 
 * the underlying at evaluation time. 
 * 
 * @author Anton Sporrer
 *
 */
public interface ZCBond_ProductConditionalFairValue_ModelInterface extends ProductConditionalFairValue_ModelInterface{

	public RandomVariableInterface getZeroCouponBond(double evaluationTime, double maturity) throws CalculationException;
	
}
