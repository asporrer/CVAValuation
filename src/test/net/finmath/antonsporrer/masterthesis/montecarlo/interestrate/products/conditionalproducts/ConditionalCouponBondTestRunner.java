package test.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products.conditionalproducts;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

@Deprecated
public class ConditionalCouponBondTestRunner {

	public static void main(String[] args) {
		
		Result result = JUnitCore.runClasses(ConditionalCouponBondTest.class);

	      for (Failure failure : result.getFailures()) {
	         System.out.println(failure.toString());
	      }
			
	      System.out.println(result.wasSuccessful());

	}

}
