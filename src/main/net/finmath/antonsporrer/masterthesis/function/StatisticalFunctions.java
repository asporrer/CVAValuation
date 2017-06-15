/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.function;

import org.apache.commons.math3.exception.NotANumberException;

/**
 * 
 * A set of statistical functions.
 * 
 * @author Anton Sporrer
 *
 */
public class StatisticalFunctions {

	
	public enum EmpiricalVarianceVersion {
		/* The empirical variance is calculated using 1/(n-1). */
		Unbiased,
		/* The empirical variance is calculated using 1/n. */
		Biased
	}
	
	
	/**
	 * 
	 * A set of values x<sub>1</sub>, x<sub>2</sub>, ... , x<sub>n</sub>  is passed to this function and the function
	 * returns the mean &mu;.
	 * <br>
	 * <br> &mu; := ( x<sub>1</sub> + x<sub>2</sub> + ... + x<sub>n</sub> ) / n
	 *  
	 * 
	 * @param values (x<sub>1</sub>, x<sub>2</sub>, ... , x<sub>n</sub>)
	 * @return The mean of the values.
	 */
	public static double getArithmeticMean(double[] values) {
		if(values == null) {throw new IllegalArgumentException("The parameter is not allowed to be null.");}
		return KahanSummation.getValue(values)/((double) values.length);
	}
	
	
	/**
	 * 
	 * A set of values x<sub>1</sub>, x<sub>2</sub>, ... , x<sub>n</sub>  is passed to this function and the function
	 * returns the empirical variance &sigma;. The calculation is performed using the factor 1/n in the biased version and
	 * using factor 1/(n-1) in the biased version. The arithmetic mean &mu; {@link #getArithmeticMean} is used.
	 * <br> 
	 * <br> &sigma; := factor * ( (x<sub>1</sub> - &mu;)^2 + (x<sub>2</sub> - &mu;)^2 + ... + (x<sub>n</sub> - &mu;)^2 )
	 *  
	 * 
	 * @param values (x<sub>1</sub>, x<sub>2</sub>, ... , x<sub>n</sub>)
	 * @param version The version used to calculate the empirical variance.
	 * @return The mean of the values.
	 */
	public static double getEmpiricalVariance(double[] values, EmpiricalVarianceVersion version) {
		
		if( values == null ) {throw new IllegalArgumentException("The parameter is not allowed to be null.");}
		
		int numberOfValues = values.length;
		
		if( version == EmpiricalVarianceVersion.Unbiased && (numberOfValues == 1)) {throw new IllegalArgumentException("Not allowed to input an array of one value and Unbiased. This would result in (1.0/0.0 = NaN).");}
		
		// The arithmetic mean is calculated.
		double arithmeticMean = getArithmeticMean( values );
		
		// Allocating and assigning array for squared differences between each value and the arithmetic mean of all values.
		//
		double[] varianceSummands = new double[numberOfValues];
		
		for(int index = 0; index<numberOfValues; index++) {
			varianceSummands[index] = (values[index] - arithmeticMean)*(values[index] - arithmeticMean);
		}
		
		// Allocating and assigning factor based on the version input parameter
		//
		double factor = 0.0;
		
		if(version == EmpiricalVarianceVersion.Unbiased) {factor = 1.0/(numberOfValues-1);}
		else {factor = 1.0/numberOfValues;}
		
		// Using Kahan summation to calculate the empirical variance.
		return factor * KahanSummation.getValue(varianceSummands);
		
	}

	
	/**
	 * This method gets an array a = (a<sub>1</sub>, a<sub>2</sub>, ... , a<sub>n</sub>) and first calculates 
	 * the arithmetic mean &mu;<sup>a</sup> of this array. Second the array b = (|a<sub>1</sub> - &mu;<sup>a</sup>|, |a<sub>2</sub> - &mu;<sup>a</sup>|, ... , |a<sub>n</sub> - &mu;<sup>a</sup>|)
	 * is calculated and returned
	 * 
	 * @param values (a<sub>1</sub>, a<sub>2</sub>, ... , a<sub></sub>)
	 * @return (|a<sub>1</sub> - &mu;<sup>a</sup>|, |a<sub>2</sub> - &mu;<sup>a</sup>|, ... , |a<sub>n</sub> - &mu;<sup>a</sup>|)
	 */
	public static double[] getDeviationsFromMean(double[] values) {
		
		if(values == null) {throw new IllegalArgumentException("Not allowed to pass a double array of value null to this function.");}
		
		double[] deviationsFromMean = new double[values.length];
		double mean = getArithmeticMean(values);
		
		for( int i = 0; i < values.length; i++ ) {
			deviationsFromMean[i] = Math.abs(values[i] - mean);
		}
		
		return deviationsFromMean;
	}
	
	
	/**
	 * This method gets an array a = (a<sub>1</sub>, a<sub>2</sub>, ... , a<sub>n</sub>) and first calculates 
	 * the arithmetic mean &mu;<sup>a</sup> of this array. Second the array b = (|a<sub>1</sub> - &mu;<sup>a</sup>| / |&mu;<sup>a</sup>| , |a<sub>2</sub> - &mu;<sup>a</sup>| / |&mu;<sup>a</sup>|, ... , |a<sub>n</sub> - &mu;<sup>a</sup>| / |&mu;<sup>a</sup>|)
	 * is calculated and returned
	 * 
	 * @param values (a<sub>1</sub>, a<sub>2</sub>, ... , a<sub>n</sub>)
	 * @return (|a<sub>1</sub> - &mu;<sup>a</sup>| / |&mu;<sup>a</sup>| , |a<sub>2</sub> - &mu;<sup>a</sup>| / |&mu;<sup>a</sup>|, ... , |a<sub>n</sub> - &mu;<sup>a</sup>| / |&mu;<sup>a</sup>|)
	 */
	public static double[] getRelativeDeviationsWRTMean(double[] values) {
		
		if(values == null) {throw new IllegalArgumentException("Not allowed to pass a double array of value null to this function.");}
		
		
		double[] realtiveDevationsWRTMean = new double[values.length];
		double[] deviationsFromMean = getDeviationsFromMean(values);
		double absOfMean = Math.abs( getArithmeticMean(values) );
		if(absOfMean == 0.0) {throw new ArithmeticException("The arithmetic mean of the argument is zero. It is not allowed to divide by zero.");}
		
		for(int i = 0; i < values.length; i++) {
			realtiveDevationsWRTMean[i] = deviationsFromMean[i]/absOfMean;
		}
		
		return realtiveDevationsWRTMean;
	}
	
	
	
}
