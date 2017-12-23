package test.net.finmath.antonsporrer.masterthesis.experiments.generics.typesafety;

import test.net.finmath.antonsporrer.masterthesis.experiments.generics.AbstractRestrictionClass;

public abstract class AbstractContainer<T extends AbstractRestrictionClass> {

	AbstractContent<T> content;
	T restrictionClass;
	
	public AbstractContainer(T restrictionClass , AbstractContent<T> content   ) {
		
	}
	
	
}
