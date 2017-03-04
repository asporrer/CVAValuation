package main.net.finmath.antonsporrer.test;

import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;

public class TimeDiscretizationTestDrive {

	public static void main(String[] args) {
		
		// Testing the return value of the method getTimeIndex of the class TimeDiscretization in case 
		// the time is not in the dicretization.
		double initial = 0;
		int numberOfTimeSteps = 10;
		double deltaT = 0.5;
		TimeDiscretizationInterface timeDiscretization = new TimeDiscretization(initial, numberOfTimeSteps, deltaT);

		double testExcludedTime = 0.3;
		System.out.println(timeDiscretization.getTimeIndex(testExcludedTime));
		
	}

}
