/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * All models implementing this interface should provide the fair value of a
 * zero coupon bond maturing at maturity conditional on path-wise states of 
 * the underlying at evaluation time. Therefore all models implementing 
 * this interface should fulfill the following. Knowing the value of the underlying
 * on a path &omega; at evaluation time implies the fair value of a zero coupon bond 
 * on this path &omega;.
 *
 * <br> E.g. in case of a short rate model: for each path &omega;,  N(t, &omega;) E[1/N(T)|r(t)=r(t,&omega;)] = P(T;t,&omega;)
 * is provided. Where N is the numéraire. T is the maturity, t is the evaluation time. 
 * 
 * <br> E.g. in case of a Libor Market Model for each path &omega;, 1/(1 + L(T<sub>k</sub>,T<sub>k + 1</sub>;T<sub>k</sub>) * (T<sub>k + 1</sub> - T<sub>k</sub>)) * ... * 1/(1 + L(T<sub>n-1</sub>,T<sub>n</sub>;T<sub>k</sub>) * (T<sub>n</sub> - T<sub>n-1</sub>)) = P(T<sub>n</sub>;T<sub>k</sub>,&omega;)
 * is provided. T<sub>n</sub> is the maturity, T<sub>k</sub> is the evaluation time. 
 * 
 * 
 * @author Anton Sporrer
 *
 */
public interface ZCBond_ProductConditionalFairValue_ModelInterface extends ProductConditionalFairValue_ModelInterface{

	/**
	 * 
	 * This function returns a random variable holding the fair prices of the zero coupon bond specified 
	 * by the parameters. More precisely the fair price of a ZCB on each path determined by the value of the underlying.
	 * In a multicurve setting this is the value of the defaultable bond.
	 * 
	 * @param evaluationTime The evaluation time of the fair price of the zero coupon bond.
	 * @param maturity The maturity of the zero coupon bond.
	 * @return The fair price of a zero coupon bond maturing at maturity at evaluation time. In a multicurve setting this is the value of the defaultable bond.
	 * @throws CalculationException
	 */
	public RandomVariableInterface getZeroCouponBond(double evaluationTime, double maturity) throws CalculationException;
	
	
	/**
	 * 
	 * 
	 * This method calculates the discounting adjustment. Thereby enables to implement multi-curve 
	 * evaluation.
	 * Let &lambda;(T) be the deterministic credit spread or the so called default intensity at time T.
	 * An implementation of this method calculates exp( int_initialTime^finalTime &lambda;(s) ds )
	 * using the information of the forwad curve (e.g. 3M LIBOR) and the discount curve (e.g. OIS).
	 * 
	 * @param initialTime
	 * @param finaltime
	 * @return The exponential function applied to the integrated deterministic credit spread. exp( int_initialTime^finalTime &lambda;(s) ds ) 
	 */
	public double getDiscountingAdjustment(double initialTime, double finaltime);

	
}
