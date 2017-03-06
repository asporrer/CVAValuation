package main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.interestrate.LIBORModelInterface;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * All model implementing this interface should provide the fair value of a
 * zero coupon bond maturing at maturity conditional on path-wise states of 
 * the short-rate at evaluation time. 
 * 
 * @author Anton Sporrer
 *
 */
public interface ConditionalBondFormulaModelInterface extends LIBORModelInterface {
	
	public RandomVariableInterface getZeroCouponBond(double evaluationTime, double maturity ) throws CalculationException;
	
}
