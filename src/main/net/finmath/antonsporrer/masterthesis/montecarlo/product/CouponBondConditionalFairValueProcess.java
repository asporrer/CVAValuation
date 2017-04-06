/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ZCBond_ProductConditionalFairValue_ModelInterface;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * This class implements the valuation of a coupon bond.
 * It provides at each time the fair value of the 
 * coupon bond conditioned on the value of the underlying 
 * at the current path. E.g. if the underlying model is a short rate model:
 * <br> E[  DiscountedCashflows(t) | r<sub>t</sub> = r<sup>*</sup><sub>t</sub>( &omega; ) ] 
 * <br> is provided. 
 * <br> - Where t is the evaluation time,
 * <br> - r<sub>t</sub> is the short rate and r<sup>*</sup><sub>t</sub>( &omega; ) is the simulation of the short rate at time t and path &omega; .
 * <br> - DiscountedCashflows(t) are the discounted cashflows of the zero coupon bond made at t or later. 
 * <br>
 * <br>A coupon bond has the following payoff profile.
 * <br> For i = 1, ..., n-1
 * <br> C<sub>i</sub> (T<sub>i+1</sub> - T<sub>i</sub> ) is payed in T<sub>i+1</sub> 
 * <br> 1 is payed in addition at time T<sub>n</sub> . 
 * 
 * @author Anton Sporrer
 */
public class CouponBondConditionalFairValueProcess<T extends ZCBond_ProductConditionalFairValue_ModelInterface> extends AbstractProductConditionalFairValueProcess<T> {

	// T_2, ... , T_n
	double[] paymentDates;
	// C_1, ... , C_{n-1}
	double[] coupons;
	// T_2 - T_1, ... , T_n - T_{n-1}
	double[] periodFactors;
	
	
	/**
	 * 
	 * @param underlyingModel The underlying model with respect to which the fair value of the coupon bond is evaluated.
	 * @param paymentDates T<sub>2</sub>, ... , T<sub>n</sub>
	 * @param periodFactors T<sub>2</sub> - T<sub>1</sub>, ... , T<sub>n</sub> - T<sub>n-1</sub>
	 * @param coupons C<sub>1</sub>, ... , C<sub>n-1</sub>
	 */
	public CouponBondConditionalFairValueProcess(
			T underlyingModel, double[] paymentDates, double[] periodFactors, double[] coupons) {
		super(underlyingModel);
		
		// Small check if the arrays have at least the correct length.
		if (!(paymentDates.length == periodFactors.length && paymentDates.length == coupons.length) ) {
			throw new IllegalArgumentException("The length of the payment date, period factors and coupons array has to be equal.");
		}
		
		this.paymentDates = paymentDates; 
		this.periodFactors = periodFactors;
		this.coupons = coupons;
		
	}


	public RandomVariableInterface getFairValue(int timeIndex)
			throws CalculationException {
		
		////
		// The first payment date greater or equal to the 
		// evaluation time is determined.
		////
		
		int firstOutstandingPaymentIndex = 0;
		
		double evaluationTime = underlyingModel.getTimeDiscretization().getTime(timeIndex);
		
		while(evaluationTime > paymentDates[firstOutstandingPaymentIndex]) {
			++firstOutstandingPaymentIndex;
		}
		
		
		////
		// The fair values of the coupon and the zero coupon bond payments are summed up.
		////
		
		RandomVariableInterface outstandingPayments = new RandomVariable(0.0);
		RandomVariableInterface currentBondFairValue = null;
		
		// Calculating and summing the fair value at evaluation time of all outstanding payments.
		for(int index = firstOutstandingPaymentIndex; index < paymentDates.length; index++) {
			
			// Assigning this auxiliary random variable.
			// The fair value of a zero coupon bond maturing at paymentDates[index] at evaluation time is fetched.
			if( evaluationTime != paymentDates[index]) {
				currentBondFairValue = underlyingModel.getZeroCouponBond(evaluationTime, paymentDates[index]);
			}
			else {
				currentBondFairValue = new RandomVariable(1.0);
			}
			
			// Summing the fair values of all outstanding payments.
			outstandingPayments = outstandingPayments.add( currentBondFairValue.mult( coupons[index] * periodFactors[index]) );
			
		}
		
		// The fair value at evaluation time of the payment of 1 at maturity.
		outstandingPayments = outstandingPayments.add(currentBondFairValue); 
		
		return outstandingPayments;
	}

	
	/**
	 * 
	 * Not implemented yet!
	 * 
	 * TODO: Implement deterministic discounting.
	 * 
	 * @param timeIndex
	 * @return
	 * @throws CalculationException
	 */
	public RandomVariableInterface getFairValueWithDiscounting(int timeIndex)
			throws CalculationException {
		
		throw new UnsupportedOperationException("Is yet to be implemented.");
		
//		
//		// The first payment date greater or equal to the 
//		// evaluation time is determined.
//		int firstOutstandingPaymentIndex = 0;
//		
//		double evaluationTime = underlyingModel.getTimeDiscretization().getTime(timeIndex);
//		
//		while(evaluationTime > paymentDates[firstOutstandingPaymentIndex]) {
//			++firstOutstandingPaymentIndex;
//		}
//		
//		RandomVariableInterface outstandingPayments = new RandomVariable(0.0);
//		RandomVariableInterface currentBondFairValue = null;
//		
//		// Calculating and summing the fair value at evaluation time of all outstanding payments.
//		for(int index = firstOutstandingPaymentIndex; index < paymentDates.length; index++) {
//			
//			// Assigning this auxiliary random variable.
//			// The fair value of a zero coupon bond maturing at paymentDates[index] at evaluation time is fetched.
//			if( evaluationTime != paymentDates[index]) {
//				currentBondFairValue = underlyingModel.getZeroCouponBond(evaluationTime, paymentDates[index]);
//			}
//			else {
//				currentBondFairValue = new RandomVariable(1.0);
//			}
//			// Summing the fair values of all outstanding payments.
//			outstandingPayments = outstandingPayments.add( currentBondFairValue.mult( coupons[index] * periodFactors[index]) );
//			// The fair value at evaluation time of the payment of 1 at maturity.
//			if(index == paymentDates.length-1) { 
//				outstandingPayments = outstandingPayments.add(currentBondFairValue);  
//			}
//		}
//		
//		return outstandingPayments;
	}
	
	
	
	
	
}
