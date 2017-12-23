package test.net.finmath.antonsporrer.masterthesis.experiments.somesmalltests;

import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;

public class TestDriveRandomVariableGetRealization {

	public static void main(String[] args) {
	
		RandomVariableInterface testRV = new RandomVariable(1.0);
		
		System.out.println( testRV.get(1) );

	}

}
