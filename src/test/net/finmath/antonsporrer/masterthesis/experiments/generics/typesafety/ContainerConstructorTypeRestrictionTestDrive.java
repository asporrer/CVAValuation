package test.net.finmath.antonsporrer.masterthesis.experiments.generics.typesafety;

import test.net.finmath.antonsporrer.masterthesis.experiments.generics.AbstractRestrictionClass;
import test.net.finmath.antonsporrer.masterthesis.experiments.generics.EligibleClass;
import test.net.finmath.antonsporrer.masterthesis.experiments.generics.EligibleClassB;

public class ContainerConstructorTypeRestrictionTestDrive {

	public static void main(String[] args) {
		
		AbstractRestrictionClass eligibleClass = new EligibleClass();
		EligibleClassB eligibleClassB = new EligibleClassB();
		
		ContentA contentA = new ContentA();
		ContentB contentB = new ContentB();
		
		
		Container<AbstractRestrictionClass> container = new Container<AbstractRestrictionClass>(eligibleClass, contentA);
		
		//Container<EligibleClass> containerB = new Container<EligibleClass>(eligibleClass, contentB);
		
		

	}

}
