package main.net.finmath.antonsporrer.masterthesis.montecarlo.product;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ZCBond_ProductConditionalFairValue_ModelInterface;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * This class implements the valuation of a swap.
 * It provides at each time the fair value of the 
 * swap conditioned on the value of the underlying 
 * at the current path. E.g. if the underlying model is a short rate model:
 * <br> E[  DiscountedCashflows(t) | r<sub>t</sub> = r<sup>*</sup><sub>t</sub>( &omega; ) ] 
 * <br> is provided. 
 * <br> - Where t is the evaluation time,
 * <br> - r<sub>t</sub> is the short rate and r<sup>*</sup><sub>t</sub>( &omega; ) is the simulation of the short rate at time t and path &omega; .
 * <br> - DiscountedCashflows(t) are the discounted cashflows of the swap made at t or later. 
 * <br>
 * <br>A swap has the following payoff profile.
 * <br> For i = 1, ..., n-1
 * <br> (L<sup>i</sup>(T<sub>i</sub>) - K) (T<sub>i+1</sub> - T<sub>i</sub> ) is payed in T<sub>i+1</sub> 
 * <br> Where K is the swap rate and L<sup>i</sup>(t) = L( T<sub>i</sub> , T<sub>i + 1</sub> ; t ) is the forward rate from T<sub>i</sub> to T<sub>i + 1</sub> 
 * at time t. 
 * 
 * @author Anton Sporrer
 */
public class SwapConditionalFairValueProcess extends AbstractProductConditionalFairValueProcess<ZCBond_ProductConditionalFairValue_ModelInterface>{
	
	// T_1, T_2, ... , T_n
	double[] paymentDatesFixingDates;
	// Swap Rate
	double swapRate;
	
	/**
	 * 
	 * @param underlyingModel
	 * @param paymentDatesFixingDates This array contains the fixing dates and the payment dates. It is assumed that the first index is the first fixing date and is strictly greater than zero. The last index is the last payment date. All other indices are payment date of the previous period and fixing date of the next period at the same time.
	 * @param swapRate The swap rate of the swap. 
	 */
	public SwapConditionalFairValueProcess(
			ZCBond_ProductConditionalFairValue_ModelInterface underlyingModel, double[] paymentDatesFixingDates, double swapRate) {
		super(underlyingModel);
		this.paymentDatesFixingDates = paymentDatesFixingDates;
		this.swapRate = swapRate;
	}

	public RandomVariableInterface getFairValue(int timeIndex) throws CalculationException {
		
		int numberOfDates = paymentDatesFixingDates.length;
		
		////
		// The first date index greater or equal to the 
		// evaluation time is determined.
		////
		
		int nextDateIndex = 0;
		
		double evaluationTime = underlyingModel.getTimeDiscretization().getTime(timeIndex);
		
		while( paymentDatesFixingDates[nextDateIndex] < evaluationTime) {
			++nextDateIndex;
		}
		
		
		// The following two cases have to be considered
		if( nextDateIndex == 0 ) {
			
			////
			// Calculating the fair value of the Floating payments.
			////
			
			// The fair Zero Coupon Bond values P(T_1,t) ,P(T_n,t) are fetched.
			RandomVariableInterface bondT1 = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[nextDateIndex]);
			RandomVariableInterface bondTn = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[numberOfDates - 1]);
			
			// Calculating P(T_1,t) - P(T_n,t) this is the fair value of the floating payments.
			RandomVariableInterface fairValueFloatingRatePayments = bondT1.addProduct(bondTn, -1.0);
			
			
			////
			// Calculating the fair value of the fixed payments.
			// This is the fair value of a coupon bond.
			// The class used to calculate the fair value of a coupon bond is reused.
			////
			
			// The fair value of a coupon bond bond is calculated.
			
			// ( T_2, ... , T_n )
			double[] paymentDates = new double[ numberOfDates - 1 ];
			// ( swapRate, swapRate, ... , swapRate )
			double[] coupons = new double[ numberOfDates - 1 ];
			// ( T_2 - T_1, T_3 - T_2, ... , T_n - T_{n-1} )
			double[] periodFactors = new double[ numberOfDates - 1 ];
			
			for(int index = 0; index < numberOfDates - 1 ; index++) {
				paymentDates[index] = paymentDatesFixingDates[index + 1];
				coupons[index] = swapRate;
				periodFactors[index] = paymentDatesFixingDates[index + 1] - paymentDatesFixingDates[index];
			}
			
			// Declaring and initializing the coupon bond calculation class.
			CouponBondConditionalFairValueProcess couponBondConditionalFairValueProcess = new CouponBondConditionalFairValueProcess(this.getUnderlyingModel(), paymentDates, periodFactors, coupons);
			
			// Calculating the fair value of the fixed rate payments. The fair value of the coupon bond is calculated. 
			// In a second step the bond payment of the ZCBond has to be subtracted since this is not part of the swap payments.
			RandomVariableInterface fairValueFixedRatePayments = couponBondConditionalFairValueProcess.getFairValue(timeIndex).addProduct(bondTn, -1.0);
			

			////
			// Subtracting the fair value of the fixed payments from the fair value of the floating payments. 
			// This is the fair value of the swap.
			////
			
			return fairValueFloatingRatePayments.addProduct( fairValueFixedRatePayments, -1.0 );
		
		}
		else {
			
			
		}
		
		
		

		// The following two cases have to be considered
		 {
	
			 // TODO: Generalize form T_1 to T_nextIndex.
			 
			////
			// Calculating the fair value of the Floating payments. 
			////
			
			// The fair Zero Coupon Bond values P(T_nextIndex,t) ,P(T_n,t) are fetched.
			RandomVariableInterface bondTNextDateIndex = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[nextDateIndex]);
			RandomVariableInterface bondTn = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[numberOfDates - 1]);
			
			// Calculating P(T_1,t) - P(T_n,t) this is the fair value of the floating payments.
			RandomVariableInterface fairValueFloatingRatePayments = bondTNextDateIndex.addProduct(bondTn, -1.0);
			
			// In case the evaluation time is not smaller or equal to the first fixing date. 
			// The fair value of the floating rate in the current period as to be added. 
			// (E.g. L^nextDatesIndex(T_{ nextDateIndex - 1 })(T_{nextDateIndex}) - T_{nextDateIndex - 1} ) P(T_{nextDateIndex}; evaluationDate).
			if( nextDateIndex != 0 ) {
				
				RandomVariableInterface fairValueCurrentFloatingRatePayment = new RandomVariable(0.0);
				
				// The bond from the previous to the next time period is fetched. (P(T_{nextDateIndex}; T_{nextDateIndex - 1}))
				RandomVariableInterface bondTNextDateIndexAtPreviousTimeIndex = this.underlyingModel.getZeroCouponBond( paymentDatesFixingDates[ nextDateIndex - 1 ] , paymentDatesFixingDates[ nextDateIndex ]);
				
				// P(T_{nextDateIndex};evaluationTime) / P(T_{nextDateIndex}; T_{nextDateIndex - 1}) -  P(T_{nextDateIndex};evaluationTime) is calculated. 
				fairValueCurrentFloatingRatePayment = bondTNextDateIndex.div(bondTNextDateIndexAtPreviousTimeIndex).addProduct(bondTNextDateIndex, -1.0);
				
				// Adding the fair value of the current floating rate payments.
				fairValueFloatingRatePayments = fairValueFloatingRatePayments.add(fairValueCurrentFloatingRatePayment);
				
			}
			
			
			////
			// Calculating the fair value of the fixed payments.
			// This is the fair value of a coupon bond.
			// The class used to calculate the fair value of a coupon bond is reused.
			////
			
			// The fair value of a coupon bond bond is calculated.
			
			// ( T_2, ... , T_n )
			double[] paymentDates = new double[ numberOfDates - 1 ];
			// ( swapRate, swapRate, ... , swapRate )
			double[] coupons = new double[ numberOfDates - 1 ];
			// ( T_2 - T_1, T_3 - T_2, ... , T_n - T_{n-1} )
			double[] periodFactors = new double[ numberOfDates - 1 ];
			
			for(int index = 0; index < numberOfDates - 1 ; index++) {
				paymentDates[index] = paymentDatesFixingDates[index + 1];
				coupons[index] = swapRate;
				periodFactors[index] = paymentDatesFixingDates[index + 1] - paymentDatesFixingDates[index];
			}
			
			// Declaring and initializing the coupon bond calculation class.
			CouponBondConditionalFairValueProcess couponBondConditionalFairValueProcess = new CouponBondConditionalFairValueProcess(this.getUnderlyingModel(), paymentDates, periodFactors, coupons);
			
			// Calculating the fair value of the fixed rate payments. The fair value of the coupon bond is calculated. 
			// In a second step the bond payment of the ZCBond has to be subtracted since this is not part of the swap payments.
			RandomVariableInterface fairValueFixedRatePayments = couponBondConditionalFairValueProcess.getFairValue(timeIndex).addProduct(bondTn, -1.0);
			
	
			////
			// Subtracting the fair value of the fixed payments from the fair value of the floating payments. 
			// This is the fair value of the swap.
			////
			
			return fairValueFloatingRatePayments.addProduct( fairValueFixedRatePayments, -1.0 );
				
		}

		
		
		double[] auxiliaryCouponBondCoupons = new double[paymentDatesFixingDates.length];
		
		for(int index = 0; index<auxiliaryCouponBondCoupons.length; index++) {
			auxiliaryCouponBondCoupons[index] = 1
		}		
		
		// TODO: If parametrization is changed this coupon bond is parameterized with T.
		CouponBondConditionalFairValueProcess couponBondConditionalFairValueProcess = new CouponBondConditionalFairValueProcess(this.getUnderlyingModel() , paymentDates, periodFactors, coupons)
		
		return null;
	}

	
	
	
}
