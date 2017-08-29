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
 * This class implements the valuation of a swap.
 * It provides at each time the fair value of the 
 * swap conditioned on the value of the underlying 
 * on the current path. E.g. if the underlying model is a short rate model:
 * <br> E[  DiscountedCashflows(t) | r<sub>t</sub> = r<sup>*</sup><sub>t</sub>( &omega; ) ] 
 * <br> is provided. 
 * <br> - Where t is the evaluation time.
 * <br> - r<sub>t</sub> is the short rate and r<sup>*</sup><sub>t</sub>( &omega; ) is the simulation of the short rate at time t and path &omega; .
 * <br> - DiscountedCashflows(t) are the discounted cashflows of the swap made at t or later. 
 * <br>
 * <br> This class implements a swap on the defaultable ("defaultable" is only relevant in the multi-curve setting) Libor rate L<sup>d</sup>. In case the discounting curve 
 * of the underlying model is equal to the forward curve then L<sup>d</sup> = L and the swap can be seen as a swap on the non-defaultable Libor L. It has the following payoff profile.
 * <br> For i = 1, ..., n-1
 * <br> (L<sup>d,i</sup>(T<sub>i</sub>) - K) (T<sub>i+1</sub> - T<sub>i</sub> ) is payed in T<sub>i+1</sub> 
 * <br> Where K is the swap rate and L<sup>d,i</sup>(t) = L<sup>d</sup>( T<sub>i</sub> , T<sub>i + 1</sub> ; t ) is the defaultable forward rate from T<sub>i</sub> to T<sub>i + 1</sub> 
 * at time t. 
 * 
 * @author Anton Sporrer
 * 
 */
public class SwapConditionalFairValueProcess<T extends ZCBond_ProductConditionalFairValue_ModelInterface> extends AbstractProductConditionalFairValueProcess<T>{
	
	// T_1 < T_2 < ... < T_n. T_1 is just the first fixing date. No payments are made at T_1. All other dates are the consecutive fixing and payoff dates.   
	private double[] paymentDatesFixingDates;
	// Swap Rate
	private double swapRate;
	
	
	/**
	 * 
	 * @param underlyingModel The underlying model with respect to which the fair value of the coupon bond is evaluated.
	 * @param paymentDatesFixingDates This array contains the fixing dates and the payment dates. It is assumed that the first index is the first fixing date and is strictly greater than zero. The last index is the last payment date. All other indices are payment date of the previous period and fixing date of the next period at the same time.
	 * @param swapRate The swap rate of the swap. 
	 */
	public SwapConditionalFairValueProcess(
			T underlyingModel, double[] paymentDatesFixingDates, double swapRate) {
		super(underlyingModel);
		this.paymentDatesFixingDates = paymentDatesFixingDates;
		this.swapRate = swapRate;
	}
	
	
	/**
	 * 
	 * In this constructor the swap rate is not passed as parameter. Instead the swap rate for which the swap has 
	 * fair value zero at time zero is calculated and set.
	 * 
	 * @param underlyingModel The underlying model with respect to which the fair value of the coupon bond is evaluated.
	 * @param paymentDatesFixingDates This array contains the fixing dates and the payment dates. It is assumed that the first index is the first fixing date and is strictly greater than zero. The last index is the last payment date. All other indices are payment date of the previous period and fixing date of the next period at the same time. 
	 * @throws CalculationException 
	 */
	public SwapConditionalFairValueProcess(
			T underlyingModel, double[] paymentDatesFixingDates) throws CalculationException {
		super(underlyingModel);
		this.paymentDatesFixingDates = paymentDatesFixingDates;
		parSwapRateAtZeroCalculation();
	}
	

	/**
	 * 
	 * @return swapRate The swap rate assigned to the model. Not necessarily the par swap rate.
	 */
	public double getSwapRate() {
		return swapRate;
	}
	
	
	// Proofreading Notes: Master Thesis, Block 3, p.13)
	/**
	 * This method returns the fair value of a swap with respect to the defaultable ("defaultable" is only relevant in multi-curve setting) Libor L^d evaluated at the time with index timeIndex.
	 * ( In formulas: sum_{i= k_timeIndex}^{n-1} ( L<sup>d</sup>(T_i, T_i+1; t_timeIndex) - C ) (T_{i+1} - T_i) )
	 * Where k_timeIndex is the smallest i such that t_timeIndex <= T_i and C is the coupon payment.
	 * 
	 * @param timeIndex The index of the time (w.r.t. the time discretization of the underlying model) at which the product is evaluated.
	 * @return The fair value of a swap w.r.t. the defaultable Libor rate
	 * @throws CalculationException
	 */
	public RandomVariableInterface getFairValue(int timeIndex) throws CalculationException {
		
		// In this method let L^d and P^d denote the defaultable forward rate and the defaultable bond.
		
		int numberOfDates = paymentDatesFixingDates.length;
		
		
		////
		// The first date index greater or equal to the 
		// evaluation time is determined.
		////
		
		int nextDateIndex = 0;
		
		double evaluationTime = underlyingModel.getTimeDiscretization().getTime(timeIndex);
		
		while( paymentDatesFixingDates[nextDateIndex] < evaluationTime) {
			
			++nextDateIndex;
			
			// In case the evaluation time is strictly greater than the last payment date the value
			// of the product is 0.0.
			if(nextDateIndex == numberOfDates) {
				return new RandomVariable(0.0);
			}
			
		}


		////
		// Calculating the fair value of the floating payments ( Sum_{i=0}^{n-1} L^d(T_i,T_{i+1};T_i) * (T_{i+1}-T_i) ). 
		////
		
		// Holding the fair value of the floating leg.
		RandomVariableInterface fairValueFloatingRatePayments = new RandomVariable(0.0);
		
		
		// If the evaluation time t is not smaller or equal to the first fixing date T_1
		// then the fair value of the defaultable floating rate payment L^d(S,T;S)*(T-S) in T {S<t<=T} 
		// in the current period has to be calculated different from the later defaultable floating rates. 
		// Let L^d respectively P^d denote the defaultable forward rate respectively the defaultable bond 
		// then the fair value of L^d(S,T;S)*(T-S) payed in T is calculated as follows.
		// L^d(T_{nextDatesIndex - 1}, T_{nextDatesIndex}; T_{ nextDateIndex - 1 }) * (T_{nextDateIndex}) - T_{nextDateIndex - 1} ) * P(T_{nextDateIndex}; evaluationDate). 
		// This formula can be rearranged and thereby be expressed by defaultable bonds and discounting factors as follows.
		// ( 1 / P^d(T_{nextDatesIndex}; T_{nextDatesIndex - 1}) - 1 ) * P^d(T_{nextDatesIndex}; evaluationDate ) * exp( int_evaluationDate^{T_{nextDatesIndex}} lambda(s) ds ) (Master Thesis Block 3, page 4).
		if( nextDateIndex != 0 ) {
			
			// The bond from the previous to the next time period is fetched. (P^d(T_{nextDateIndex}; T_{nextDateIndex - 1}))
			RandomVariableInterface bondTNextDateIndexAtPreviousDateIndex = this.underlyingModel.getZeroCouponBond( paymentDatesFixingDates[ nextDateIndex - 1 ] , paymentDatesFixingDates[ nextDateIndex ]);
			
			// The bond P^d(T_{nextDateIndex}; evaluationTime) is fetched.
			RandomVariableInterface bondTNextDateIndexAtEvaluationTime = this.underlyingModel.getZeroCouponBond( evaluationTime, paymentDatesFixingDates[ nextDateIndex ]);
			
			// The discounting adjustment (multicurve) discountingAdjustment(evaluationTime, T_{nextDateIndex}) := exp( int_evaluationDate^{T_{nextDatesIndex}} lambda(s) ds ) 
 			double discountingAdjustmentEvalNext = this.underlyingModel.getDiscountingAdjustment(evaluationTime, paymentDatesFixingDates[ nextDateIndex ]);
			
			// ( P^d( T_{nextDateIndex}; evaluationDate) / P^d(T_{nextDateIndex}; T_{nextDateIndex - 1}) -  P^d( T_{nextDateIndex}; evaluationDate) ) * exp( int_evaluationDate^{T_{nextDatesIndex}} lambda(s) ds ) is calculated. 
			fairValueFloatingRatePayments = ( bondTNextDateIndexAtEvaluationTime.div(bondTNextDateIndexAtPreviousDateIndex)
					.addProduct(bondTNextDateIndexAtEvaluationTime, -1.0) ).mult(discountingAdjustmentEvalNext);
			
			// The payment associated with the interval in which the evaluation date lies has been valuated in this if-statement. 
 			// It remains to check for and evaluate later payments. 
			
		}
					
		
		// In case the next date index plus one is within the bound of the fixing and payment dates array further payments occur.
		// The fair value of these payments is calculated next. 
		// It should be mentioned again that in the following if-case the fixing dates happen after or at evaluation time.
		// Therefore an evaluation formula different to the above formula is applicable.
		if(nextDateIndex + 1 != numberOfDates) {
		
			////
			// Declaring variables for the following for-loop.
			////
 			
 			// The discounting adjustment (multicurve) discountingAdjustment(t, T_{indexFloatingLegFixingDate + 1}).
 			double currentDiscountingAdjustmentEvalPayment = 1.0;
			
 			// The bond P^d( T_{indexFloatingLegFixingDate}; evaluationTime) depending on the current iteration.
 			RandomVariableInterface currentBondEvaluationTimeFixingDate = null;
 			
 			// The bond P^d( T_{indexFloatingLegFixingDate + 1}; evaluationTime) depending on the current iteration.
 			RandomVariableInterface currentBondEvaluationTimePaymentDate = null;
			
 			// The fair value of the current payoff with fixing date indexFloatingLegFixingDate
 			RandomVariableInterface currentPaymentFairValue = null;
 			
			// All floating leg payments after the current interval are evaluated.
			for(int indexFloatingLegFixingDate = nextDateIndex; indexFloatingLegFixingDate < numberOfDates - 1; indexFloatingLegFixingDate++ ) {
				
	 			// The current discounting adjustment from evaluation date to payment date is assigned. 
	 			currentDiscountingAdjustmentEvalPayment = this.underlyingModel.getDiscountingAdjustment( evaluationTime, paymentDatesFixingDates[ indexFloatingLegFixingDate + 1 ] );
				
	 			// P^d( T_{indexFloatingLegFixingDate}; evaluationTime)
	 			currentBondEvaluationTimeFixingDate = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[ indexFloatingLegFixingDate ]);
	 			
	 			// P^d( T_{indexFloatingLegFixingDate + 1}; evaluationTime)
	 			currentBondEvaluationTimePaymentDate = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[ indexFloatingLegFixingDate + 1 ]);
				
	 			// Calculating the fair value of the current floating leg payment. The following formula is used.
	 			// exp(int_{T_i}^{T_{i+1}} lambda(s) ds ) * P(T_i;t) - P(T_{i+1};t) 
	 			// = [ exp(int_{t}^{T_{i+1}} lambda(s) ds ) * [ P^d(T_i;t) - P^d(T_{i+1}; t) ] (Master Thesis, Block 3, p.4).
	 			// Where i = indexFloatingLegFixingDate and t = evaluationTime.
	 			currentPaymentFairValue = (currentBondEvaluationTimeFixingDate.addProduct(currentBondEvaluationTimePaymentDate, -1.0)).mult(currentDiscountingAdjustmentEvalPayment);
	 			
				// Adding the current fair value to the fair value of the previous payments.
				fairValueFloatingRatePayments = fairValueFloatingRatePayments.add(currentPaymentFairValue);
				
			}
		
		}

		
		////
		// Calculating the fair value of the fixed payments (fixed leg payments).
		////
		
		// The fair value of a coupon bond is calculated.
		
		// The next payment date index is set.
		int nextPaymentDateIndex = nextDateIndex;
		
		// In case the next date index is the first fixing date the next payment date has to be incremented. 
		// Since the first fixed leg payment occurs in T_2.
		if(nextDateIndex == 0) { nextPaymentDateIndex++; }
		
		// P^d( T_n; evaluationTime)
		RandomVariableInterface defaultableBondTn = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[numberOfDates-1]);
		
		// The discounting adjustment (multi-curve) exp( int_evaluationTime^{T_n} lambda(s) ds ).
		double discountingAdjustmentEvalLastPayment = this.underlyingModel.getDiscountingAdjustment( evaluationTime, paymentDatesFixingDates[numberOfDates-1] );
		
		// P( T_n; evaluationTime) = P^d( T_n; evaluationTime) *  exp( int_evaluationTime^{T_n} lambda(s) ds ) 
		RandomVariableInterface nondefaultableBondTn = defaultableBondTn.mult( discountingAdjustmentEvalLastPayment );
		
		// The fair value of the fixed rate payments is calculated.
		RandomVariableInterface fairValueFixedRatePayments = getFairValueNonDefaultableCouponPaymentsMultiCurveSetting(timeIndex, nextPaymentDateIndex, swapRate, nondefaultableBondTn);
		

		////
		// Subtracting the fair value of the fixed payments from the fair value of the floating payments. 
		// This is the fair value of the swap.
		////
		
		return fairValueFloatingRatePayments.addProduct( fairValueFixedRatePayments, -1.0 );
				
	}
	
	
	
	/**
	 * 
	 * The fair value at evaluation time ( the time with index timeIndex ) 
	 * of the non-defaultable ("non-defaultable" is only relevant in multi-curve setting) coupon payments at time index nextPaymentDateIndex and following 
	 * is calculated. 
	 * 
	 * @param timeIndex The index of the time at which the future coupon payments are evaluated.
	 * @param nextPaymentDateIndex The index of the time at which the next coupon is payed.
	 * @param couponValue The value of all coupon payments. (Every coupon has the same value in this class).
	 * @param bondTn The fair value of a bond maturing at the last payment date (T_n) evaluated at the time with index timeIndex.
	 * @return The fair value of the future coupon payments evaluated at time with index timeIndex.
	 * @throws CalculationException
	 */
	private RandomVariableInterface getFairValueNonDefaultableCouponPaymentsMultiCurveSetting( int timeIndex, int nextPaymentDateIndex, double couponValue, RandomVariableInterface bondTn ) throws CalculationException {
		
		
			////
			// Calculating the fair value of the non-defaultable coupon payments .
			// This is the fair value of the non-defaultable coupon bonds P(T_{nextPaymentDateIndex+1},t_timeIndex), ..., P(T_{numberOfDates},t_timeIndex) .
			// The class used to calculate the fair value of a coupon bond is reused.
			////

			// The fair value of a coupon bond is calculated.

			// the number of payment and fixing dates.
			int numberOfDates = paymentDatesFixingDates.length;
			
			// ( T_{nextPaymentDateIndex+1}, ... , T_numberOfDates )
			double[] paymentDates = new double[ numberOfDates - nextPaymentDateIndex ];
			// ( swapRate, swapRate, ... , swapRate )
			double[] coupons = new double[ numberOfDates - nextPaymentDateIndex ];
			// ( T_{nextPaymentDateIndex + 1} - T_{nextPaymentDateIndex } , T_{nextPaymentDateIndex + 2} - T_{nextPaymentDateIndex + 1}, ... , T_n - T_{n-1} )
			double[] periodFactors = new double[ numberOfDates - nextPaymentDateIndex ];
			
			for( int index = 0; index < numberOfDates - nextPaymentDateIndex ; index++ ) {
				paymentDates[ index ] = paymentDatesFixingDates[ index + nextPaymentDateIndex ];
				coupons[ index ] = couponValue;
				periodFactors[ index ] = paymentDatesFixingDates[ index + nextPaymentDateIndex ] - paymentDatesFixingDates[ index + nextPaymentDateIndex - 1 ];
			}
			
			// Declaring and initializing the coupon bond calculation class.
			CouponBondConditionalFairValueProcess<T> couponBondConditionalFairValueProcess = new CouponBondConditionalFairValueProcess<T>(this.getUnderlyingModel(), paymentDates, periodFactors, coupons);
			
			// Calculating the fair value of the fixed rate payments. In other words the fair value of the non-defaultable coupon bond is calculated. 
			// In a second step the non-defaultable bond payment of the zero coupon bond has to be subtracted since this is not part of the fixed leg swap payments.
			return couponBondConditionalFairValueProcess.getFairValue(timeIndex).addProduct(bondTn, -1.0);
			
	}

	
	/**
	 *
	 * The par swap rate of a swap with respect to the 
	 * defaultable ("defaultable" is only relevant in multi-curve setting) Libor L<sup>d</sup> at time 0.0 is calculated. 
	 * And the swap rate is set to par swap rate.
	 * More precisely the swap rate that satisfies the next 
	 * equation is returned. 
	 * <br> 0 = sum_(i = 1)^{n-1} [L<sup>d</sup>(T_i, T_{i+1}; 0) - swapRate*P(T_{i+1};0) ] * (T_{i+1} - T_i) 
	 * 
	 * 
	 * 
	 * @return swapRate The swap rate for which the swap has fair value zero at time zero.
	 * @throws CalculationException 
	 * 
	 */
	private double parSwapRateAtZeroCalculation() throws CalculationException {
		
		////
		// Calculating the fair value of the floating payments. 
		////

		// Setting the internal swap rate to zero and calculating the fair value of the corresponding swap.
		// Thereby calculating just the fair value of the floating leg.
		this.swapRate = 0.0;
		RandomVariableInterface fairValueFloatingRatePayments = getFairValue(0);
		
		
		////
		// The fair value of the fixed rate payments is calculated.
		////
		
		// The number of payment and fixing dates is fetched.
		int numberOfDates = paymentDatesFixingDates.length;
		
		// The index of the first coupon payment.
		int firstFixingDateIndex = 0;
		
		// The evaluation time usually 0.0 is fetched.
		double evaluationTime = underlyingModel.getTimeDiscretization().getTime(0);
		
		//
		// The non-defaultable bond price P(T_n; 0) is calculated
		//
	
		RandomVariableInterface nonDefaultableBondTn = underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[numberOfDates-1]);
		
		double discountingAdjustmentZeroTn = underlyingModel.getDiscountingAdjustment(evaluationTime, paymentDatesFixingDates[numberOfDates-1]);
		
		nonDefaultableBondTn = nonDefaultableBondTn.mult(discountingAdjustmentZeroTn);
		
		// The fair value of the fixed leg is calculated.
		RandomVariableInterface fairValueFixedRatePayments = getFairValueNonDefaultableCouponPaymentsMultiCurveSetting(0, firstFixingDateIndex + 1, 1.0, nonDefaultableBondTn);
		
		
		////
		// Dividing the fair value of the fixed payments by the fair value of the floating payments. 
		// This is par swap value. Next the par swap rate is assigned to the instance variable holding the swap rate.
		////
		
		this.swapRate = fairValueFloatingRatePayments.div( fairValueFixedRatePayments ).getAverage();
		
		return swapRate;
						
	}
	
	
	/**
	 * 
	 * The fair value at evaluation time ( the time with index timeIndex ) 
	 * of the defaultable coupon payments at time index nextPaymentDateIndex or later 
	 * is calculated. 
	 * 
	 * @param timeIndex The index of the time at which the future coupon payments are evaluated.
	 * @param nextPaymentDateIndex The index of the time at which the next coupon is payed.
	 * @param couponValue The value of all coupon payments.
	 * @param bondTn The fair value of a bond maturing at the last payment date (T_n) evaluated at the time with index timeIndex.
	 * @return The fair value of the future coupon payments evaluated at time with index timeIndex.
	 * @throws CalculationException
	 */
	// TODO: Could be used to implement a defaultable coupon bond. But at the moment the payoff at maturity is the same as for a non-defaultable bond this among other things has to be changed.
	@Deprecated
	private RandomVariableInterface getFairValueCouponPayments( int timeIndex, int nextPaymentDateIndex, double couponValue, RandomVariableInterface bondTn ) throws CalculationException {
		
		
			////
			// Calculating the fair value of the coupon payments.
			// This is the fair value of coupon bonds.
			// The class used to calculate the fair value of a coupon bond is reused.
			////
			
			// The fair value of a coupon bond is calculated.

			// the number of payment and fixing dates.
			int numberOfDates = paymentDatesFixingDates.length;
			
			// ( T_nextPaymentDateIndex, ... , T_n )
			double[] paymentDates = new double[ numberOfDates - nextPaymentDateIndex ];
			// ( swapRate, swapRate, ... , swapRate )
			double[] coupons = new double[ numberOfDates - nextPaymentDateIndex ];
			// ( T_nextPaymentDateIndex - T_{nextPaymentDateIndex - 1} , T_{nextPaymentDateIndex + 1} - T_nextPaymentDateIndex, ... , T_n - T_{n-1} )
			double[] periodFactors = new double[ numberOfDates - nextPaymentDateIndex ];
			
			for( int index = 0; index < numberOfDates - nextPaymentDateIndex ; index++ ) {
				paymentDates[ index ] = paymentDatesFixingDates[ index + nextPaymentDateIndex ];
				coupons[ index ] = couponValue;
				periodFactors[ index ] = paymentDatesFixingDates[ index + nextPaymentDateIndex ] - paymentDatesFixingDates[ index + nextPaymentDateIndex - 1 ];
			}
			
			// Declaring and initializing the coupon bond calculation class.
			CouponBondConditionalFairValueProcess<T> couponBondConditionalFairValueProcess = new CouponBondConditionalFairValueProcess<T>(this.getUnderlyingModel(), paymentDates, periodFactors, coupons);
			
			// Calculating the fair value of the fixed rate payments. In other words the fair value of the coupon bond is calculated. 
			// In a second step the bond payment of the zero coupon bond has to be subtracted since this is not part of the swap payments.
			return couponBondConditionalFairValueProcess.getFairValueNonMultiCurve(timeIndex).addProduct(bondTn, -1.0);
				
	}
	
	
	@Deprecated
	public RandomVariableInterface getFairValueNonMultiCurve(int timeIndex) throws CalculationException {
		
		int numberOfDates = paymentDatesFixingDates.length;
		
		
		////
		// The first date index greater or equal to the 
		// evaluation time is determined.
		////
		
		int nextDateIndex = 0;
		
		double evaluationTime = underlyingModel.getTimeDiscretization().getTime(timeIndex);
		
		while( paymentDatesFixingDates[nextDateIndex] < evaluationTime) {
			
			++nextDateIndex;
			
			// In case the evaluation time is strictly after the last payment date the value
			// of the product is 0.0.
			if(nextDateIndex == numberOfDates) {
				return new RandomVariable(0.0);
			}
			
		}
		
		
		////
		// Calculating the fair value of the floating payments. 
		////
		
		// The fair Zero Coupon Bond values P(T_nextIndex,t) ,P(T_n,t) are fetched.
		RandomVariableInterface bondTNextDateIndex = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[nextDateIndex]);
		RandomVariableInterface bondTn = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[numberOfDates - 1]);
		
		// Calculating P(T_nextIndex,t) - P(T_n,t) this is the fair value of the floating payments 
		// for fixing dates greater or equal to nextIndex.
		// In case nextIndex = n the following value is zero thus no case distinction is necessary.
		RandomVariableInterface fairValueFloatingRatePayments = bondTNextDateIndex.addProduct(bondTn, -1.0);
		
		// In case the evaluation time is not smaller or equal to the first fixing date. 
		// The fair value of the floating rate in the current period has to be added. 
		// (E.g. L(T_{nextDatesIndex - 1}, T_{nextDatesIndex}, T_{ nextDateIndex - 1 }) * (T_{nextDateIndex}) - T_{nextDateIndex - 1} ) * P(T_{nextDateIndex}; evaluationDate).
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
		////
		
		// The fair value of a coupon bond is calculated.
		
		// The next payment date index is set.
		int nextPaymentDateIndex = nextDateIndex;
		
		// In case the next date index is the first fixing date the next payment date has to be incremented.
		if(nextDateIndex == 0) { nextPaymentDateIndex++; }
		
		// The fair value of the fixed rate payments is calculated.
		RandomVariableInterface fairValueFixedRatePayments = getFairValueCouponPayments(timeIndex, nextPaymentDateIndex, swapRate, bondTn);
		

		////
		// Subtracting the fair value of the fixed payments from the fair value of the floating payments. 
		// This is the fair value of the swap.
		////
		
		return fairValueFloatingRatePayments.addProduct( fairValueFixedRatePayments, -1.0 );
				
	}
	
	
	/**
	 *
	 * The par swap rate of a swap at time 0.0 is calculated.
	 * 
	 * @return swapRate The swap rate for which the swap has fair value zero at time zero.
	 * @throws CalculationException 
	 */
	@Deprecated
	private double parSwapRateAtZeroCalculationNonMultiCurveSetting() throws CalculationException {
		
		// The number of payment and fixing dates is fetched.
		int numberOfDates = paymentDatesFixingDates.length;
		
		// The index of the first coupon payment.
		int firstFixingDateIndex = 0;
		
		// The evaluation time usually 0.0 is fetched.
		double evaluationTime = underlyingModel.getTimeDiscretization().getTime(0);
		
		
		////
		// Calculating the fair value of the floating payments. 
		////
		
		// The fair Zero Coupon Bond values P(T_1,evaluationTime) ,P(T_n,evaluationTime) are fetched.
		RandomVariableInterface bondT1 = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[firstFixingDateIndex]);
		RandomVariableInterface bondTn = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[numberOfDates - 1]);
		
		// Calculating P(T_1,t) - P(T_n,t) this is the fair value of the floating payments.
		RandomVariableInterface fairValueFloatingRatePayments = bondT1.addProduct(bondTn, -1.0);
		
		
		////
		// The fair value of the fixed rate payments is calculated.
		////
		
		RandomVariableInterface fairValueFixedRatePayments = getFairValueCouponPayments(0, firstFixingDateIndex + 1, 1.0, bondTn);
		
		
		////
		// Subtracting the fair value of the fixed payments from the fair value of the floating payments. 
		// This is par swap value.
		////
		
		return fairValueFloatingRatePayments.div( fairValueFixedRatePayments ).getAverage();
						
	}
	
	
	// TODO: Delete when testing is completed.
	// Tested new getFairValue method. Thus this out commented code can be removed. 
		
		public RandomVariableInterface getFairValueTest(int timeIndex) throws CalculationException {
			
			int numberOfDates = paymentDatesFixingDates.length;
			
			
			////
			// The first date index greater or equal to the 
			// evaluation time is determined.
			////
			
			int nextDateIndex = 0;
			
			double evaluationTime = underlyingModel.getTimeDiscretization().getTime(timeIndex);
			
			while( paymentDatesFixingDates[nextDateIndex] < evaluationTime) {
				
				++nextDateIndex;
				
				// In case the evaluation time is strictly after the last payment date the value
				// of the product is 0.0.
				if(nextDateIndex == numberOfDates) {
					return new RandomVariable(0.0);
				}
			}
			
	
			////
			// Calculating the fair value of the floating payments. 
			////
			
			// The fair Zero Coupon Bond values P(T_nextIndex,t) ,P(T_n,t) are fetched.
			RandomVariableInterface bondTNextDateIndex = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[nextDateIndex]);
			RandomVariableInterface bondTn = this.underlyingModel.getZeroCouponBond(evaluationTime, paymentDatesFixingDates[numberOfDates - 1]);
			
			// Calculating P(T_1,t) - P(T_n,t) this is the fair value of the floating payments.
			RandomVariableInterface fairValueFloatingRatePayments = bondTNextDateIndex.addProduct(bondTn, -1.0);
			
			// In case the evaluation time is not smaller or equal to the first fixing date. 
			// The fair value of the floating rate in the current period has to be added. 
			// (E.g. L(T_{nextDatesIndex - 1}, T_{nextDatesIndex}, T_{ nextDateIndex - 1 }) * (T_{nextDateIndex}) - T_{nextDateIndex - 1} ) * P(T_{nextDateIndex}; evaluationDate).
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
			// This is the fair value of coupon bonds.
			// The class used to calculate the fair value of a coupon bond is reused.
			////
			
			// The fair value of a coupon bond is calculated.
			
			// The next payment date index is set.
			int nextPaymentDateIndex = nextDateIndex;
			
			// In case the next date index is the first fixing date the next payment date has to be incremented.
			if(nextDateIndex == 0) { nextPaymentDateIndex++; }
			
			
			// ( T_nextPaymentDateIndex, ... , T_n )
			double[] paymentDates = new double[ numberOfDates - nextPaymentDateIndex ];
			// ( swapRate, swapRate, ... , swapRate )
			double[] coupons = new double[ numberOfDates - nextPaymentDateIndex ];
			// ( T_nextPaymentDateIndex - T_{nextPaymentDateIndex - 1} , T_{nextPaymentDateIndex + 1} - T_nextPaymentDateIndex, ... , T_n - T_{n-1} )
			double[] periodFactors = new double[ numberOfDates - nextPaymentDateIndex ];
			
			for( int index = 0; index < numberOfDates - nextPaymentDateIndex ; index++ ) {
				paymentDates[ index ] = paymentDatesFixingDates[ index + nextPaymentDateIndex ];
				coupons[ index ] = swapRate;
				periodFactors[ index ] = paymentDatesFixingDates[ index + nextPaymentDateIndex ] - paymentDatesFixingDates[ index + nextPaymentDateIndex - 1 ];
			}
			
			// Declaring and initializing the coupon bond calculation class.
			CouponBondConditionalFairValueProcess<T> couponBondConditionalFairValueProcess = new CouponBondConditionalFairValueProcess<T>(this.getUnderlyingModel(), paymentDates, periodFactors, coupons);
			
			// Calculating the fair value of the fixed rate payments. In other words the fair value of the coupon bond is calculated. 
			// In a second step the bond payment of the zero coupon bond has to be subtracted since this is not part of the swap payments.
			RandomVariableInterface fairValueFixedRatePayments = couponBondConditionalFairValueProcess.getFairValue(timeIndex).addProduct(bondTn, -1.0);
	
			
			////
			// Subtracting the fair value of the fixed payments from the fair value of the floating payments. 
			// This is the fair value of the swap.
			////
			
			return fairValueFloatingRatePayments.addProduct( fairValueFixedRatePayments, -1.0 );
					
		}
	
}
