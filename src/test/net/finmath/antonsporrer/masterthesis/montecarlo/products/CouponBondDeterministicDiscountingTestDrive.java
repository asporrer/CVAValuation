package test.net.finmath.antonsporrer.masterthesis.montecarlo.products;



import java.util.Arrays;
import java.util.HashSet;

import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.CouponBondConditionalFairValueProcess;
import net.finmath.exception.CalculationException;
import net.finmath.marketdata.model.curves.DiscountCurveFromForwardCurve;
import net.finmath.marketdata.model.curves.DiscountCurveInterface;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.interestrate.modelplugins.ShortRateVolatilityModel;
import net.finmath.montecarlo.process.AbstractProcess;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

public class CouponBondDeterministicDiscountingTestDrive {

	public static void main(String[] args) throws CalculationException {
		
		//////
		////
		// Test 1 : Does getFairValue method give the same result as getFairValueWithDeterministicDiscounting?
		////
		//////
		
		////
		// Objects consumed by the coupon bond and helper variables.
		////
	
		HullWhiteModel hullWhiteModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5, 10);

		// Where all tests successful?
		boolean giveMethodsTheSameResults = true;
		
		
		////
		// Test Parameters 
		//// 
		
		// Payment Date / Coupon / Period Factor Parameters
		// { { T_2, ... , T_n } , { C_1, ... , C_{n-1} }, { T_2 - T_1, ... , T_n - T_{n-1} } }
		// 
		HashSet<double[][]> paymentDatesCouponPeriodsParameters = new HashSet<double[][]>();
		
//		paymentDatesCouponPeriodsParameters.add(new double[][] {new double[] {0.5, 1.0}, {0.1, 0.1} , {1.0, 1.0}  }); 
//		paymentDatesCouponPeriodsParameters.add(new double[][]{ {1.0, 2.0, 3.0, 4.0}, {0.1, 0.1, 0.2, 1.1} , {1.0, 2.0, 1.0, 1.3}  });
		paymentDatesCouponPeriodsParameters.add(new double[][]{ {1.0}, {0.0}, {1.0} });
		paymentDatesCouponPeriodsParameters.add(new double[][]{ {2.0}, {0.0}, {1.0} });
		
		
		////
		// Object to test
		////
		
		CouponBondConditionalFairValueProcess<HullWhiteModel> couponBondConditionalFairValueProcess = null;
	
		////
		// Test Loops: does give getFairValue and getFairValueDeterministicDiscounting give the same values if discount curve is generated from forward curve?
		////
		
		// Looping over all parameters.
		for(double[][] paymentDatesCouponPeriodsParameter : paymentDatesCouponPeriodsParameters) {

			couponBondConditionalFairValueProcess = new CouponBondConditionalFairValueProcess<HullWhiteModel>(hullWhiteModel, paymentDatesCouponPeriodsParameter[0], paymentDatesCouponPeriodsParameter[1], paymentDatesCouponPeriodsParameter[2] );
			
			// Looping over all time discretization points.
			for(int timeIndex = 0; timeIndex < hullWhiteModel.getTimeDiscretization().getNumberOfTimes() ; timeIndex++) {
				System.out.println("Time Index: " + timeIndex);
				if( couponBondConditionalFairValueProcess.getFairValueNonMultiCurve(timeIndex).getAverage() != couponBondConditionalFairValueProcess.getFairValue(timeIndex).getAverage() ) {
					giveMethodsTheSameResults = false;
					System.out.println("Time Index: " + timeIndex);
				}
			
			}
			
			
			System.out.println( " The fair value in the non-multicurve setting :" + couponBondConditionalFairValueProcess.getFairValueNonMultiCurve(1) );
			System.out.println( " The fair value in the multicurve setting :" + couponBondConditionalFairValueProcess.getFairValue(1) );
			
			
			System.out.println(" All method calls return same results is : " + giveMethodsTheSameResults );
			
			
			
//			System.out.println( " The fixing and payment date array : " + Arrays.toString(paymentDatesFixingDatesParRate) + ". The associated par swap rate: " + swapConditionalFairValueProcess.getSwapRate() );
//			
//			System.out.println( "The fair value of the swap is: " + swapConditionalFairValueProcess.getFairValue(0).getAverage() );
//			
//			if(swapConditionalFairValueProcess.getFairValue(0).getAverage() != 0.0) {
//				parSwapRateGivesFairValueZero = false;
//			}
		}
		
		
		//////
		////
		// Test 2 : If the discount curve is generated from a separate forward curve do the following statements hold?
		// 			- Does the non-defaultable bond have a higher value than the defaultable bond?
		// 			- Does manuallly multipling the defaultable ZCB by the lambda-factor give the same price as a non-defaultable ZCB? (Master Thesis Block 3, page 12: for formulas)
		////
		//////

		
		////
		// The time discretization
		////
		
		TimeDiscretizationInterface liborPeriodDiscretization = new TimeDiscretization(0.0, 20, 0.5);

		
		////
		// Creating Short Rate Volatility Model
		////
		
		// Volatility array for the volatility model.
		double[] volatilities = new double[ liborPeriodDiscretization.getNumberOfTimes() ];
		
		// Mean reversion array for the volatility model.
		double[] meanReversions = new double[ liborPeriodDiscretization.getNumberOfTimes() ];
		
		for(int index = 0; index < liborPeriodDiscretization.getNumberOfTimes(); index++) {
			volatilities[index] = 0.03;
			meanReversions[index] = 0.05;
		}
		
		ShortRateVolatilityModel shortRateVolatilityModel = new ShortRateVolatilityModel(liborPeriodDiscretization, volatilities, meanReversions);
		
		
		////
		// Declaring and Initializing Forward and Discount Curve
		////

		ForwardCurve forwardCurve = ForwardCurve.createForwardCurveFromForwards(
				"forwardCurve"								/* name of the curve */,
				new double[] { 1.0, 2.0, 5.0, 10.0 }	/* fixings of the forward */,
				new double[] { 0.05, 0.05, 0.05, 0.05 }	/* forwards */,
				1.0	/* tenor / period length */
				);
		
		ForwardCurve forwardCurveForDisco = ForwardCurve.createForwardCurveFromForwards(
				"forwardCurve"								/* name of the curve */,
				new double[] { 1.0, 2.0, 5.0, 10.0 }	/* fixings of the forward */,
				new double[] { 0.03, 0.03, 0.03, 0.03 }	/* forwards */,
				1.0	/* tenor / period length */
				);
		
		DiscountCurveInterface discountCurve = new DiscountCurveFromForwardCurve(forwardCurveForDisco);
		
		hullWhiteModel = new HullWhiteModel(liborPeriodDiscretization, null, forwardCurve, discountCurve, shortRateVolatilityModel, null); 
		
		
		////
		// Declaring and Initializing the Process.
		////
		
		int numberOfFactors = 2;
		int seed = 1337;
		
		BrownianMotionInterface brownianMotionShortRateModel = new BrownianMotion(liborPeriodDiscretization, numberOfFactors, 10, seed);
			
		AbstractProcess processShortRateModel = new ProcessEulerScheme(brownianMotionShortRateModel);
			
		
		////
		// Relating Model and Process.
		////

		hullWhiteModel.setProcess(processShortRateModel);
		processShortRateModel.setModel(hullWhiteModel);
		
		
		
		// Looping over all parameters.
		for(double[][] paymentDatesCouponPeriodsParameter : paymentDatesCouponPeriodsParameters) {

			couponBondConditionalFairValueProcess = new CouponBondConditionalFairValueProcess<HullWhiteModel>(hullWhiteModel, paymentDatesCouponPeriodsParameter[0], paymentDatesCouponPeriodsParameter[1], paymentDatesCouponPeriodsParameter[2] );
			
			System.out.println( " The parameters are: " + Arrays.toString(paymentDatesCouponPeriodsParameter[0]));
			System.out.println( " The fair value of the defaultable bond: " + couponBondConditionalFairValueProcess.getFairValueNonMultiCurve(0).getAverage() );
			System.out.println( " The fair value of the non-defaultable bond: " + couponBondConditionalFairValueProcess.getFairValue(0).getAverage() );
			System.out.println( " Defaultable ZCB multiplied with lambda-factor: " + ( 1.05/1.03 * couponBondConditionalFairValueProcess.getFairValueNonMultiCurve(0).getAverage() ) );
						
			System.out.println( " The parameters are: " + Arrays.toString(paymentDatesCouponPeriodsParameter[0]));
			System.out.println( " The fair value of the defaultable bond: " + couponBondConditionalFairValueProcess.getFairValueNonMultiCurve(2).getAverage() );
			System.out.println( " The fair value of the non-defaultable bond: " + couponBondConditionalFairValueProcess.getFairValue(2).getAverage() );
			System.out.println( " Defaultable ZCB multiplied with lambda-factor: " + ( 1.05/1.03 * couponBondConditionalFairValueProcess.getFairValueNonMultiCurve(2).getAverage() ) );
						
			
			
			
		}

	}

}
