package main.net.finmath.antonsporrer.masterthesis.integration;

import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * 
 * This class provides static methods to approximate an Lebesgue integral. 
 * Whereas the integrand is given only on discrete time points. Two Versions 
 * are provided in that class. One where the function values are given as an
 * double array and one where the function values are given as an RandomVariable
 * Interface array.
 * 
 * @author Anton Sporrer
 *
 */
public class Integration {

	public enum IntegrationMethod {LeftPoints, RightPoints, Trapezoidal }
	
	/**
	 * This function gets a time discretization and corresponding function values. 
	 * 
	 * @param functionValues The discrete function values corresponding to the time discretization.
	 * @param timeDiscretization
	 * @param integrationMethod The integration method can be chosen (e.g. Trapezoidal, ... ).
	 * @return The Lebesgue integral of the specified function.
	 */
	public static double getIntegral(double[] functionValues, TimeDiscretizationInterface timeDiscretization, IntegrationMethod integrationMethod) {
		// TODO: Implement weights. Maybe check if the length of the function values array is equal to the time discretization length. 
		
		if(functionValues.length != timeDiscretization.getNumberOfTimes()) {
			throw new IllegalArgumentException("The number of function values has to be equal to the number of time points");
		}
		
		// The number of all discretization points.
		int numberOfDiscretizationPoints = functionValues.length;
		
		
		////
		// Allocating the array holding the function approximation. 
		// This is done according to the IntegrationMethod Enum. 
		////
		
		double[] functionApproximations = new double[numberOfDiscretizationPoints - 1];		
		
		switch( integrationMethod ) {
		case LeftPoints: 
			for(int index = 0; index < numberOfDiscretizationPoints - 1; index++) {
			functionApproximations[index] = functionValues[index];
			}
			break;
		case RightPoints:
			for(int index = 0; index < numberOfDiscretizationPoints - 1; index++) {
				functionApproximations[index] = functionValues[index + 1];
			}
			break;
		case Trapezoidal:
			for(int index = 0; index < numberOfDiscretizationPoints - 1; index++) {
				functionApproximations[index] = 0.5 * ( functionValues[index] + functionValues[index + 1] );
			}
			break;
		}
		
		
		////
		// Using Kahan summation to calculate the integral approximation sum.
		////
		
		double integral = 0.0;
		double auxiliaryNextIntegral = 0.0;
		double negativErrorContainer = 0.0;
		double summand = 0.0;
		
		for(int summationIndex = 0; summationIndex < functionApproximations.length; summationIndex++ ) {
			
			summand = functionApproximations[summationIndex] * ( timeDiscretization.getTime(summationIndex + 1) - timeDiscretization.getTime( summationIndex ) )
					- negativErrorContainer;
			auxiliaryNextIntegral += summand;
			negativErrorContainer = (auxiliaryNextIntegral - integral) - summand;
			integral += summand;
			
		}
				
		
		return integral;
		
	}
	
	
	/**
	 * This function gets a time discretization and corresponding function values. 
	 * 
	 * @param functionValues The discrete function values corresponding to the time discretization.
	 * @param timeDiscretization
	 * @param integrationMethod The integration method can be chosen (e.g. Trapezoidal, ... ).
	 * @return The Lebesgue integral of the specified function.
	 */
	public static RandomVariableInterface getIntegral(RandomVariableInterface[] functionValues, TimeDiscretizationInterface timeDiscretization, IntegrationMethod integrationMethod) {
		// TODO: Implement weights. Maybe check if the length of the function values array is equal to the time discretization length. 
		
		if(functionValues.length != timeDiscretization.getNumberOfTimes()) {
			throw new IllegalArgumentException("The number of function values has to be equal to the number of time points");
		}
		
		// The number of all discretization points.
		int numberOfDiscretizationPoints = functionValues.length;
		
		
		////
		// Allocating the array holding the function approximation. 
		// This is done according to the IntegrationMethod Enum. 
		////
		
		RandomVariableInterface[] functionApproximations = new RandomVariableInterface[numberOfDiscretizationPoints - 1];		
		
		switch( integrationMethod ) {
		case LeftPoints: 
			for(int index = 0; index < numberOfDiscretizationPoints - 1; index++) {
			functionApproximations[index] = functionValues[index];
			}
			break;
		case RightPoints:
			for(int index = 0; index < numberOfDiscretizationPoints - 1; index++) {
				functionApproximations[index] = functionValues[index + 1];
			}
			break;
		case Trapezoidal:
			for(int index = 0; index < numberOfDiscretizationPoints - 1; index++) {
				functionApproximations[index] = ( functionValues[index].add( functionValues[index + 1] ) ).mult(0.5);
			}
			break;
		}
		
		
		////
		// Using Kahan summation to calculate the integral approximation sum.
		////
		
		RandomVariableInterface integral = new RandomVariable(0.0);
		RandomVariableInterface auxiliaryNextIntegral = new RandomVariable(0.0);
		RandomVariableInterface negativErrorContainer = new RandomVariable(0.0);
		RandomVariableInterface summand = new RandomVariable(0.0);
		
		for(int summationIndex = 0; summationIndex < functionApproximations.length; summationIndex++ ) {
			
			summand = functionApproximations[summationIndex]
					.mult( ( timeDiscretization.getTime(summationIndex + 1) - timeDiscretization.getTime( summationIndex ) ) )
					.addProduct( negativErrorContainer, -1.0);
			auxiliaryNextIntegral = auxiliaryNextIntegral.add( summand );
			negativErrorContainer = auxiliaryNextIntegral
					.addProduct( integral, -1.0 ).addProduct( summand, -1.0 );
			integral = integral.add( summand );
			
		}
				
		
		return integral;
		
	}
	
	
}
