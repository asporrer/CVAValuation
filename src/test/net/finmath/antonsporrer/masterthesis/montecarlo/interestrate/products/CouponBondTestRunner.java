package test.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import test.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModelTest;

@Deprecated
public class CouponBondTestRunner {
   public static void main(String[] args) {
      Result result = JUnitCore.runClasses(CouponBondTest.class);

      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result.wasSuccessful());
   }
}  	


	