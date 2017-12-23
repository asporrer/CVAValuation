package test.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;

public class CIRModelSmallCheck {

	public static void main(String[] args) throws CalculationException {
		
		
		
		double initialValue = 0.3;
	     double kappa = 0.01;
	    double mu = 0.05;
	   double nu = 0.3;

	
			    double initialTime = 0;
			    int numberOfSteps = 80;
			    double stepSize = 0.125;
			TimeDiscretizationInterface timeDiscretization = new TimeDiscretization(initialTime, numberOfSteps, stepSize);;
			 int numberOfFactors = 1;
			int numberOfPaths = 50;
			 int seed = 3141;
	BrownianMotionInterface brownianMotion = new BrownianMotion(timeDiscretization, numberOfFactors, numberOfPaths, seed);;
	 ProcessEulerScheme process  = new ProcessEulerScheme(brownianMotion);;
	
	 CIRModel cirModel = new CIRModel(initialValue, kappa, mu, nu, process);
	
	System.out.println(cirModel.getProcessValue(5, 0).getAverage());
	System.out.println();
		
	}
	
}
