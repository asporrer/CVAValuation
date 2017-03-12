package test.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products;


import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*; // Notice the use of "static" here
import main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products.CouponBond;
import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.interestrate.LIBORModelMonteCarloSimulationInterface;
import net.finmath.montecarlo.interestrate.TermStructureModelInterface;
import net.finmath.montecarlo.interestrate.TermStructureModelMonteCarloSimulation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;

import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;
@Deprecated
@RunWith(Parameterized.class)
public class CouponBondTest {

	private double evaluationTime;
	
	private double[] paymentDates;
	private double[] periodFactors;
	private double[] coupons;
	
	private CouponBond couponBond;
	
	
	private HullWhiteModel hullWhiteModel;
	TermStructureModelMonteCarloSimulation termStructureModelMonteCarloSimulation;
	
	@Before
	public void initialize() {
		this.couponBond = new CouponBond(paymentDates, periodFactors, coupons); 
		hullWhiteModel = HullWhiteCreationHelper.createHullWhiteModel(0, 20, 0.5);
		termStructureModelMonteCarloSimulation = new TermStructureModelMonteCarloSimulation((TermStructureModelInterface) hullWhiteModel);
	}
	
	public CouponBondTest(double evaluationTime, double[] paymentDates, double[] periodFactors, double[] coupons) {
		this.evaluationTime = evaluationTime;
		this.paymentDates = paymentDates;
		this.periodFactors = periodFactors;
		this.coupons = coupons;
	}
	
	@Parameterized.Parameters
	   public static Collection cirParameters() {
		   return Arrays.asList(new Object[][] {
	         {0.0, new double[] {4.0, 5.0},new double[] {1.0, 1.0}, new double[] {1.0, 2.0} },
	         {0.0, new double[] {5.0, 9.5},new double[] {1.0, 1.0}, new double[] {1.0, 0.0}},
	         {0.0, new double[] {2.0},new double[] {1.0}, new double[] {0.1} },
	         {0.0, new double[] {2.0},new double[] {1.0}, new double[] {0.1} },
	         {1.9, new double[] {2.0},new double[] {1.0}, new double[] {0.1} }
	      });
	   }
	
	@Test
	   public void testCIRModel() throws CalculationException {
	      System.out.println("Parameters are: ");
	      
	      System.out.println("The bond price is : " +  couponBond.getValue(evaluationTime,  termStructureModelMonteCarloSimulation).getAverage()   );
	      
	      assertEquals(1.0, 
	    		  couponBond.getValue(evaluationTime,  termStructureModelMonteCarloSimulation).getAverage()	, 10.0	  
	      );
	   }
	
	
}
