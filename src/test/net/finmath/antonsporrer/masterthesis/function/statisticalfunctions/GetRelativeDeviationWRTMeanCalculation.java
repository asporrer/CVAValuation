package test.net.finmath.antonsporrer.masterthesis.function.statisticalfunctions;

import main.net.finmath.antonsporrer.masterthesis.function.StatisticalFunctions;

public class GetRelativeDeviationWRTMeanCalculation {

	public static void main(String[] args) {
		// Calculation Result Used for Testing an Plotting.
		System.out.println( StatisticalFunctions.getArithmeticMean( StatisticalFunctions.getRelativeDeviationsWRTMean(
				new double[] {0.010270808, 
				0.010276967,
				0.010264869,
				0.010271854,
				0.010271548,
				0.010274855,
				0.010277387,
				0.010276712,
				0.010273665,
				0.010271365  }) ) ); 

	}

}
