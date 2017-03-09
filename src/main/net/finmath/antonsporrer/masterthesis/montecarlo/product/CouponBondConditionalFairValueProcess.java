package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.AbstractZCBond_ProductConditionalFairValue_Model;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * This class implements the valuation of a coupon bond.
 * 
 * A coupon bond has the following payoff profile.
 * <br> For i = 1, ..., n-1
 * <br> C<sub>i</sub> (T<sub>i+1</sub> - T<sub>i</sub> ) is payed in T<sub>i+1</sub> 
 * <br> 1 is payed in addition at time T<sub>n</sub> . 
 * 
 * @author Anton Sporrer
 */
public class CouponBondConditionalFairValueProcess extends AbstractZCBondProductConditionalFairValueProcess<AbstractZCBond_ProductConditionalFairValue_Model> {

	// T_2, ... , T_n
	double[] paymentDates;
	// C_1, ... , C_{n-1}
	double[] coupons;
	// T_2 - T_1, ... , T_n - T_{n-1}
	double[] periodFactors;
	
	
	
	public CouponBondConditionalFairValueProcess(
			AbstractZCBond_ProductConditionalFairValue_Model underlyingModel, double[] paymentDates, double[] periodFactors, double[] coupons) {
		super(underlyingModel);
		
		if (!(paymentDates.length == periodFactors.length && paymentDates.length == coupons.length) ) {
			throw new IllegalArgumentException("The length of the payment date, period factors and coupons array has to be equal.");
		}
		
		this.paymentDates = paymentDates; 
		this.periodFactors = periodFactors;
		this.coupons = coupons;
		
	}

	
	public RandomVariableInterface getFairValue(int timeIndex)
			throws CalculationException {
		
		// The first payment date greater or equal to the 
		// evaluation time is determined.
		int firstOutstandingPaymentIndex = 0;
		
		double evaluationTime = underlyingModel.getTimeDiscretization().getTime(timeIndex);
		
		while(evaluationTime > paymentDates[firstOutstandingPaymentIndex]) {
			++firstOutstandingPaymentIndex;
		}
		
		RandomVariableInterface outstandingPayments = new RandomVariable(0.0);
		RandomVariableInterface currentBondFairValue = null;
		
		// Calculating and summing the fair value at evaluation time of all outstanding payments.
		for(int index = firstOutstandingPaymentIndex; index < paymentDates.length; index++) {
			
			// Assigning this auxiliary random variable.
			// The fair value of a zero coupon bond maturing at paymentDates[index] at evaluation time is fetched.
			currentBondFairValue = underlyingModel.getZeroCouponBond(evaluationTime, paymentDates[index]);
					
			// Summing the fair values of all outstanding payments.
			outstandingPayments = outstandingPayments.add( currentBondFairValue.mult( coupons[index] * periodFactors[index]) );
			// The fair value at evaluation time of the payment of 1 at maturity.
			if(index == paymentDates.length-1) { outstandingPayments = outstandingPayments.add(currentBondFairValue);  }
		}
		
		return outstandingPayments;
	}

}
