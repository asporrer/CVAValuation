package test.net.finmath.antonsporrer.masterthesis.montecarlo.products;


import java.util.Arrays;
import java.util.Collection;


import static org.junit.Assert.*; // Notice the use of "static" here
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

import org.junit.Before;
import org.junit.Test;


import org.junit.runners.Parameterized;

import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.SwapConditionalFairValueProcess;

import org.junit.runner.RunWith;



@RunWith(Parameterized.class)
public class SwapTest {

	double[] paymentDatesFixingDates;
	double swapRate;

	HullWhiteModel hullWhiteModel;
	HullWhiteCreationHelper hullWhiteCreationHelper;
	
	SwapConditionalFairValueProcess swapConditionalFairValueProcess;
	
	double result;
	
   @Before
   public void initialize() {
      
	   hullWhiteModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5, 100);
	   swapConditionalFairValueProcess = new SwapConditionalFairValueProcess(hullWhiteModel, paymentDatesFixingDates, swapRate);
	   
   }

   // Each parameter should be placed as an argument here
   // Every time runner triggers, it will pass the arguments
   // from parameters we defined in primeNumbers() method
	
   public SwapTest(double[] paymentDatesFixingDates, double swapRate, double result ) {
	   this.paymentDatesFixingDates = paymentDatesFixingDates;
	   this.swapRate = swapRate;
	   hullWhiteCreationHelper = new HullWhiteCreationHelper();
	   this.result = result;
   }

   @Parameterized.Parameters
   public static Collection swapParameters() {
	   return Arrays.asList(new Object[][] {
         { new double[] { 1.0, 1.5 }, 0.05, 0.0 },
         { new double[] { 1.0, 1.5, 2.0 }, 0.05, 0.0 }, 
         { new double[] { 8.5, 9.0, 9.5, 10.0 } , 0.05, 0.0 }, 
         { new double[] { 8.5, 9.0, 9.5, 10.0, 10.5, 11.0 } , 0.05, 0.0 } // Asking for a swap with a swap date later than maturity is due to the analytic bond formula possible.
      });
   }

   // This test will run 4 times since we have 5 parameters defined
@Test
   public void testSwap() throws CalculationException {
//      System.out.println("Parameters are: " + initialValue + kappa+ mu+ nu );
//      System.out.println("The initial value is: " + cirModel.getProcessValue(10, 0).getAverage()  );
//      assertEquals(1.0, 
//      cirModel.getInitialValue()[0].get(0)	, 0.3	  
//      );
	
	System.out.println("The ZCB at 1.0: " + hullWhiteModel.getZeroCouponBond(0, 1.0)); 
	System.out.println("The ZCB at 2.0: " + hullWhiteModel.getZeroCouponBond(0, 2.0));
	System.out.println("Difference between the ZCBs: " + hullWhiteModel.getZeroCouponBond(0, 1.0).addProduct( hullWhiteModel.getZeroCouponBond(0, 2.0), -1.0 ) );
	System.out.println("The L(1.0,2.0;0.0) by ZCB-Mehtod: " + hullWhiteModel.getZeroCouponBond(0, 1.0).addProduct( hullWhiteModel.getZeroCouponBond(0, 2.0), -1.0 ).div(hullWhiteModel.getZeroCouponBond(0, 2.0)) );
	System.out.println( "The LIBORs by LIBOR-Method: " + hullWhiteModel.getLIBOR(0.0, 1.0, 2.0));
	System.out.println( "The LIBORs by LIBOR-Method: " + hullWhiteModel.getLIBOR(0.0, 2.0, 3.5));
	
	System.out.println("The ZCB at 1.0: " + hullWhiteModel.getZeroCouponBond(0, 1.0)); 
	System.out.println("The ZCB at 1.5: " + hullWhiteModel.getZeroCouponBond(0, 1.5));
	System.out.println("Difference between the ZCBs: " + hullWhiteModel.getZeroCouponBond(0, 1.0).addProduct( hullWhiteModel.getZeroCouponBond(0, 1.5), -1.0 ) );
	System.out.println("The L(1.0,1.5;0.0) by ZCB-Mehtod: " + hullWhiteModel.getZeroCouponBond(0, 1.0).addProduct( hullWhiteModel.getZeroCouponBond(0, 1.5), -1.0 ).div(hullWhiteModel.getZeroCouponBond(0, 1.5)).div(0.5) );
	System.out.println( "The LIBORs by LIBOR-Method: " + hullWhiteModel.getLIBOR(0.0, 1.0, 1.5));
	
	System.out.println("The L(1.5,2.0;0.0) by ZCB-Mehtod: " + hullWhiteModel.getZeroCouponBond(0, 1.5).addProduct( hullWhiteModel.getZeroCouponBond(0, 2.0), -1.0 ).div(hullWhiteModel.getZeroCouponBond(0, 2.0)).div(0.5) );
	

	
	
	System.out.println("The Swap Value: " + swapConditionalFairValueProcess.getFairValue(0));
	assertEquals( result, swapConditionalFairValueProcess.getFairValue(0).getRealizations()[0], 0.0000001 );
	
	
	
	
   }
}