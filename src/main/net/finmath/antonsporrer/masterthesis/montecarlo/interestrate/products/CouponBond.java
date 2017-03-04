package main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.montecarlo.interestrate.LIBORModelMonteCarloSimulationInterface;
import net.finmath.montecarlo.interestrate.products.AbstractLIBORMonteCarloProduct;
import net.finmath.montecarlo.interestrate.products.Bond;
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

public class CouponBond extends AbstractLIBORMonteCarloProduct{
	
	// T_2, ... , T_n
	double[] paymentDates;
	// C_1, ... , C_{n-1}
	double[] coupons;
	// T_2 - T_1, ... , T_n - T_{n-1}
	double[] periodFactors;
	
	
	public CouponBond(double[] paymentDates, double[] periodFactors, double[] coupons) {
		
		if (!(paymentDates.length == periodFactors.length && paymentDates.length == coupons.length) ) {
			throw new IllegalArgumentException("The length of the payment date, period factors and coupons array has to be equal.");
		}
		
		this.paymentDates = paymentDates; 
		this.periodFactors = periodFactors;
		this.coupons = coupons;
		
	};
	
	@Override
	public RandomVariableInterface getValue(double evaluationTime,
			LIBORModelMonteCarloSimulationInterface model)
			throws CalculationException {
		
		// The first payment date greater or equal to the 
		// evaluation time is determined.
		int firstOutstandingPaymentIndex = 0;
		
		while(evaluationTime > paymentDates[firstOutstandingPaymentIndex]) {
			++firstOutstandingPaymentIndex;
		}
		
		RandomVariableInterface outstandingPayments = new RandomVariable(0.0);
		AbstractLIBORMonteCarloProduct currentBond = null;
		RandomVariableInterface currentBondFairValue = null;
		
		// Calculating and summing the fair value at evaluation time of all outstanding payments.
		for(int index = firstOutstandingPaymentIndex; index < paymentDates.length; index++) {
			
			// Initializing the Bond class with the current payment date.
			currentBond = new Bond( paymentDates[index]);
			// Assigning this auxiliary random variable.
			currentBondFairValue = currentBond.getValue(evaluationTime, model);
			
			// Summing the fair values of all outstanding payments.
			outstandingPayments = outstandingPayments.add( currentBondFairValue.mult( coupons[index] * periodFactors[index]) );
			// The fair value at evaluation time of the payment of 1 at maturity.
			if(index == paymentDates.length-1) { outstandingPayments = outstandingPayments.add(currentBondFairValue);  }
		}
		
		return outstandingPayments;
	}
	
}
