package test.net.finmath.antonsporrer.masterthesis.montecarlo.products;

import java.util.Arrays;
import java.util.HashSet;

import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.SwapConditionalFairValueProcess;
import net.finmath.exception.CalculationException;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

public class ParRateSwapEvaluationTestDrive {

	public static void main(String[] args) throws CalculationException {
		
		
		////
		// Objects consumed by the swap are prepared.
		////
		
		HullWhiteModel hullWhiteModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5, 10000);

		boolean giveMethodsTheSameResults = true;
		

		
//		////
//		//Test New Swap Evaluation Method. 
//		////

//		
//		////
//		// Helper Objects are created
//		////
//		
//		double[] resultOldMethod = null;
//		double[] resultNewMethod = null;
//		
//		////
//		// Test parameters are generated.
//		////
//		
//		// Different payment dates
//		HashSet<double[]> paymentDatesFixingDatesParameters = new HashSet<double[]>();
//		
//		paymentDatesFixingDatesParameters.add(new double[] {1.0, 2.0}); 
//		paymentDatesFixingDatesParameters.add(new double[] {0.5, 1.0, 1.5, 5.0});
//		
//		// Different swap rates
//		double[] swapRatesParameters = {-1.0, 0.0, 1.0, 2.32 };
//		
//		
//		////
//		// Testing by iterating over all date parameters
//		////
//		for( double[] paymentDatesFixingDatesParameter: paymentDatesFixingDatesParameters  ) {
//		
//			// Iterating over all swap rate parameters
//			for(int indexSwapRateParameter = 0; indexSwapRateParameter< swapRatesParameters.length; indexSwapRateParameter++) {
//			    
//				System.out.println("Iterated!");
//				
//				double[] paymentDatesFixingDates = paymentDatesFixingDatesParameter;
//				double swapRate = 0.0;
//				
//				SwapConditionalFairValueProcess<HullWhiteModel> swapConditionalFairValueProcess = new SwapConditionalFairValueProcess<HullWhiteModel>(hullWhiteModel, paymentDatesFixingDates, swapRate);
//				
//				for(int timeIndex = 0; timeIndex < hullWhiteModel.getTimeDiscretization().getNumberOfTimes(); timeIndex++) {
//		
//					// Compare results of old and new method.
//					
//					resultOldMethod = swapConditionalFairValueProcess.getFairValue(timeIndex).getRealizations();
//					resultNewMethod = swapConditionalFairValueProcess.getFairValueTest(timeIndex).getRealizations();
//					
//					if ( ! Arrays.equals( resultOldMethod, resultNewMethod ) ) {
//						giveMethodsTheSameResults = false;
//						System.out.println("Old Result: " + Arrays.toString(resultOldMethod) );
//						System.out.println("New Result: " + Arrays.toString(resultNewMethod) );
//					}
//		
//				}
//			
//	
//			}
//			
//		}
//			
//		System.out.println("The methods give the same results is: " + giveMethodsTheSameResults );
// TODO: Previous test was successful above code could  be removed.
		
		
		
		////
		// Test Par Swap Rate calculation.
		////

		// Objects needed for test construction
		SwapConditionalFairValueProcess<HullWhiteModel> swapConditionalFairValueProcess = null;
		
		// Parameter Preparations
		
		// Different fixing and payment dates
		HashSet<double[]> paymentDatesFixingDatesParRateParameters = new HashSet<double[]>();
		
		paymentDatesFixingDatesParRateParameters.add(new double[] {0.5, 1.0}); 
		paymentDatesFixingDatesParRateParameters.add(new double[] {1.0, 2.0}); 
		paymentDatesFixingDatesParRateParameters.add(new double[] {0.5, 1.0, 1.5, 5.0});
		
		boolean parSwapRateGivesFairValueZero = true; 
		
		for(double[] paymentDatesFixingDatesParRate : paymentDatesFixingDatesParRateParameters) {
		
			swapConditionalFairValueProcess = new SwapConditionalFairValueProcess<HullWhiteModel>(hullWhiteModel, paymentDatesFixingDatesParRate);
		
			System.out.println( " The fixing and payment date array : " + Arrays.toString(paymentDatesFixingDatesParRate) + ". The associated par swap rate: " + swapConditionalFairValueProcess.getSwapRate() );
			
			System.out.println( "The fair value of the swap is: " + swapConditionalFairValueProcess.getFairValueNonMultiCurve(0).getAverage() );
			
			if(swapConditionalFairValueProcess.getFairValueNonMultiCurve(0).getAverage() != 0.0) {
				parSwapRateGivesFairValueZero = false;
			}
		}
		
		System.out.println("All swaps have fair value zero is: " + parSwapRateGivesFairValueZero);
		
		
		
		
		
	}

}
