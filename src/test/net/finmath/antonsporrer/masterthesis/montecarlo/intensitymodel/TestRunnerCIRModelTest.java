package test.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel;


import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunnerCIRModelTest {
   public static void main(String[] args) {
      Result result = JUnitCore.runClasses(CIRModelTest.class);

      for (Failure failure : result.getFailures()) {
         System.out.println(failure.toString());
      }
		
      System.out.println(result.wasSuccessful());
   }
}  	


	

