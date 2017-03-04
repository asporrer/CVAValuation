package test.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel;

import java.util.Arrays;
import java.util.Collection;


import static org.junit.Assert.*; // Notice the use of "static" here
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;

import org.junit.Before;
import org.junit.Test;


import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;




@RunWith(Parameterized.class)
public class CIRModelTest {

		private	double initialValue;
	    private double kappa;
	    private double mu;
	    private double nu;
	private CIRModel cirModel;
	
			    private double initialTime;
			    private int numberOfSteps;
			    private double stepSize;
			private TimeDiscretizationInterface timeDiscretization;
			private int numberOfFactors;
			private int numberOfPaths;
			private int seed;
		private BrownianMotionInterface brownianMotion;
	private ProcessEulerScheme process;
	

	
	
   @Before
   public void initialize() {
      
      timeDiscretization = new TimeDiscretization(initialTime, numberOfSteps, stepSize);
      brownianMotion = new BrownianMotion(timeDiscretization, numberOfFactors, numberOfPaths, seed);
      process = new ProcessEulerScheme(brownianMotion);
      
      cirModel = new CIRModel(initialValue, kappa, mu, nu, process);
   }

   // Each parameter should be placed as an argument here
   // Every time runner triggers, it will pass the arguments
   // from parameters we defined in primeNumbers() method
	
   public CIRModelTest(double initialValue, double kappa, double mu, double nu, double initialTime, int numberOfSteps, double stepSize, int numberOfFactors, int numberOfPaths, int seed) {
      
	  this.initialValue = initialValue;
      this.kappa = kappa;
      this.mu = mu;
      this.nu = nu;
	  this.initialTime = initialTime;
	  this.numberOfSteps = numberOfSteps;
	  this.stepSize = stepSize ;
	  this.numberOfFactors = numberOfFactors;
	  this.numberOfPaths = numberOfPaths;
	  this.seed = seed;
      
   }

   @Parameterized.Parameters
   public static Collection cirParameters() {
	   return Arrays.asList(new Object[][] {
         { 1.0, 1.0, 0.5, 0.3, 0, 20, 0.5, 1, 50, 3141 },
         { 1.0, 1.0, 0.5, 0.25, 0, 20, 0.5, 1, 50, 3141 },
         { 1.0, 1.0, 0.5, 0.2, 0, 20, 0.5, 1, 50, 3141 },
         { 1.0, 1.0, 0.5, 0.15, 0, 20, 0.5, 1, 50, 3141 },
         { 1.0, 1.0, 0.5, 0.1, 0, 20, 0.5, 1, 50, 3141 }
      });
   }

   // This test will run 4 times since we have 5 parameters defined
@Test
   public void testCIRModel() throws CalculationException {
      System.out.println("Parameters are: " + initialValue + kappa+ mu+ nu );
      System.out.println("The initial value is: " + cirModel.getProcessValue(10, 0).getAverage()  );
      assertEquals(1.0, 
      cirModel.getInitialValue()[0].get(0)	, 0.3	  
      );
   }
}