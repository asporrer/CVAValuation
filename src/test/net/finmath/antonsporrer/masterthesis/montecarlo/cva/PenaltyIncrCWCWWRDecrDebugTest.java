package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

public class PenaltyIncrCWCWWRDecrDebugTest {

	public static void main(String[] args) {
		
		// Test Division by NaN.
		double denominator = Math.exp(709);
		
		double significantDigitsA = 1.0;
		double significantDigitsB = 1.0E-20;

		System.out.println(" Denominator: "+ denominator);
		System.out.println(" 1 / Denominator: " + 0.000000000000000001/denominator);
		
		System.out.println(" Significant Digits Test: " + (significantDigitsA - significantDigitsB) );
		
	}

}
