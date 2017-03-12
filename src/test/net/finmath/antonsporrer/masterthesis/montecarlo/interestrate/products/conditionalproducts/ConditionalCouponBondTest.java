package test.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products.conditionalproducts;

import java.util.Arrays;
import java.util.Collection;








import static org.junit.Assert.*; // Notice the use of "static" here
import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.ConditionalBondFormulaModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products.conditionalproducts.ConditionalCouponBond;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;

import org.junit.Before;
import org.junit.Test;


import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;

import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

@Deprecated
@RunWith(Parameterized.class)
public class ConditionalCouponBondTest {

	double evaluationTime;
	double[] paymentDates;
	double[] periodFactors;
	double[] coupons;
	
	ConditionalBondFormulaModelInterface hullWhiteModel;
	ConditionalCouponBond conditionalCouponBond;

   @Before
   public void initialize() {
	   hullWhiteModel = (ConditionalBondFormulaModelInterface) HullWhiteCreationHelper.createHullWhiteModel(0.0, 30, 0.5);
	   conditionalCouponBond = new ConditionalCouponBond(paymentDates, periodFactors, coupons);
   }
   

   public ConditionalCouponBondTest( double evaluationTime, double[] paymentDates, double[] periodFactors, double[] coupons ) {
      this.evaluationTime = evaluationTime;
	  this.paymentDates = paymentDates;
      this.periodFactors = periodFactors;
      this.coupons = coupons;
   }

   @Parameterized.Parameters
   public static Collection cirParameters() {
	   return Arrays.asList(new Object[][] {
         { 0.0 ,new double[] { 14.5 }, new double[] { 0.0 }, new double[] { 0.0 } },
         { 0.5 ,new double[] { 14.5 }, new double[] { 0.0 }, new double[] { 0.0 } },
         { 1.0 ,new double[] { 7.5, 14.5 }, new double[] { 1.0, 1.0 }, new double[] { 1.0, 1.0 } }
      });
   }

   // This test will run 4 times since we have 5 parameters defined
@Test
	public void testCIRModel() throws CalculationException {
    	
		double[] auxiliaryConditionalCouponBondValues = conditionalCouponBond.getFairValue(evaluationTime, hullWhiteModel).getRealizations();
		
		System.out.println("Parameters are (evaluation time, payment dates, period factors, coupons ): " + evaluationTime + ", " + Arrays.toString(paymentDates) + ", " + Arrays.toString(periodFactors) + ", " + Arrays.toString(coupons) );
		System.out.println("Conditional Coupon Bond Values: " + Arrays.toString(auxiliaryConditionalCouponBondValues));
		
		System.out.println(" Number of Conditional Coupon Bond Realizations: " + auxiliaryConditionalCouponBondValues.length);
		System.out.println(" Number of Hull White Simulation Paths: " + ((HullWhiteModel) hullWhiteModel).getProcess().getNumberOfPaths() );
		
		RandomVariableInterface runningSum = new RandomVariable(0.0);
		
		for(int index = 0; index < paymentDates.length; index++) {
			runningSum = runningSum.add( hullWhiteModel.getZeroCouponBond(evaluationTime, paymentDates[index]).mult( periodFactors[index] * coupons[index] ) );
		}
		
		runningSum = runningSum.add( hullWhiteModel.getZeroCouponBond(evaluationTime, paymentDates[ paymentDates.length - 1 ]) );
		
		System.out.println("Calculated From Hand: " + Arrays.toString( runningSum.getRealizations()) );
		
		if(evaluationTime <= paymentDates[0]) {
			for(int index = 0; index < conditionalCouponBond.getFairValue(evaluationTime, hullWhiteModel).getRealizations().length; index++) {
				assertEquals( runningSum.getRealizations()[index] , auxiliaryConditionalCouponBondValues[index] , 0.0001);
			}
		}	
		
    }

}