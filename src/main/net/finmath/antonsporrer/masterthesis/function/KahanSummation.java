package main.net.finmath.antonsporrer.masterthesis.function;

public class KahanSummation {

	/**
	 * This function provides the Kahan summation of the summands.
	 * 
	 * @summands The summands of the sum.
	 * @return The sum of the summands is returned.
	 * 
	 */
	public static double getValue(double[] summands) throws IllegalArgumentException {
		
		if(summands == null) {throw new IllegalArgumentException("The summands array is not allowed to be null.");}
		
		double nextToAdd = 0.0;
		double sum = 0.0;
		double helperSum = 0.0;
		double negativeLostDigitsStorage = 0.0;
		
		for(int index = 0; index < summands.length; index++) {
			nextToAdd = summands[index] + negativeLostDigitsStorage;
			helperSum += nextToAdd;
		    negativeLostDigitsStorage = (helperSum - sum) - nextToAdd;
		    sum = helperSum;
		}
		
		return sum;
	}

	
	
}
