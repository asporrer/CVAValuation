package test.net.finmath.antonsporrer.masterthesis.experiments.generics.wildcards;

public class WildCardsTestDrive {

	public static void main(String[] args) {
		
		AInterface<? super ImplementingBoundClass> abc = new ImplementingClassA<ImplementingBoundClass>();
		
//		AInterface< ImplementingBoundClass > bcd = abc;
		
	}

}
