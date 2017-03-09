package test.net.finmath.antonsporrer.masterthesis.prototyping.generics.typesafety;

import test.net.finmath.antonsporrer.masterthesis.prototyping.generics.AbstractRestrictionClass;

public abstract class AbstractContainer<T extends AbstractRestrictionClass> {

	AbstractContent<T> content;
	T restrictionClass;
	
	public AbstractContainer(T restrictionClass , AbstractContent<T> content   ) {
		
	}
	
	
}
