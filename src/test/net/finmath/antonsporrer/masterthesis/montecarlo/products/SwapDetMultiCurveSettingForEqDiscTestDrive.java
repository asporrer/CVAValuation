package test.net.finmath.antonsporrer.masterthesis.montecarlo.products;

import java.util.HashSet;

import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.SwapConditionalFairValueProcess;
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

public class SwapDetMultiCurveSettingForEqDiscTestDrive {

	public static void main(String[] args) throws CalculationException {
		
		//////
		////
		// Test 1 : If the discount curve = forward curve do the following statements hold?
		// 			- Is the fair value of the swap with the par swap rate as rate zero?
		// 			  That is to say is the average over the discounted fair values of a swap at a future time point 
		// 			  equal to 0?
		// 			
		////
		//////

		//////
		// Preparation of input paramters 
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
				new double[] { 0.05, 0.05, 0.05, 0.05 }	/* forwards */,
				1.0	/* tenor / period length */
				);
		
		DiscountCurveInterface discountCurve = new DiscountCurveFromForwardCurve(forwardCurveForDisco);
		
		HullWhiteModel hullWhiteModel = new HullWhiteModel(liborPeriodDiscretization, null, forwardCurve, discountCurve, shortRateVolatilityModel, null); 
		
		
		////
		// Declaring and Initializing the Process.
		////
		
		int numberOfFactors = 2;
		int seed = 1342;
		
		BrownianMotionInterface brownianMotionShortRateModel = new BrownianMotion(liborPeriodDiscretization, numberOfFactors, 10000, seed);
			
		AbstractProcess processShortRateModel = new ProcessEulerScheme(brownianMotionShortRateModel);
			
		
		////
		// Relating Model and Process.
		////

		hullWhiteModel.setProcess(processShortRateModel);
		processShortRateModel.setModel(hullWhiteModel);
		

		HashSet<double[]> paymentDatesFixingDatesParameters = new HashSet<double[]>();
//		paymentDatesFixingDatesParameters.add(new double[] {1.0, 2.0});
//		paymentDatesFixingDatesParameters.add(new double[] {1.0, 2.0, 3.0});
		paymentDatesFixingDatesParameters.add(new double[] { 1.0, 2.0});
		
		//////
		// Declaring and initializing the class which is tested.
		//////
		
		SwapConditionalFairValueProcess<HullWhiteModel> swapConditionalFairValueProcess = null;

		
		for(double[] paymentDatesFixingDatesParameter: paymentDatesFixingDatesParameters) {
	
			swapConditionalFairValueProcess = new SwapConditionalFairValueProcess<HullWhiteModel>(hullWhiteModel, paymentDatesFixingDatesParameter);
			System.out.println(" The par swap rate is: " + swapConditionalFairValueProcess.getSwapRate());
			for(int timeIndex = 0; timeIndex < hullWhiteModel.getTimeDiscretization().getNumberOfTimes(); timeIndex++) {
				
				System.out.println( " The fair value of of the swap with par swap rate at index " + timeIndex +
						" : " + swapConditionalFairValueProcess.getFairValue(timeIndex).
						div(swapConditionalFairValueProcess.getNumeraire(timeIndex)).
						mult(swapConditionalFairValueProcess.getNumeraire(0)).getAverage()  );
			}
			
		}
		

		
	}

}
