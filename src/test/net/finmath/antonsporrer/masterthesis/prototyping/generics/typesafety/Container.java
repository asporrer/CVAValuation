package test.net.finmath.antonsporrer.masterthesis.prototyping.generics.typesafety;

import test.net.finmath.antonsporrer.masterthesis.prototyping.generics.AbstractRestrictionClass;

public class Container<T extends AbstractRestrictionClass> extends AbstractContainer<T>{

	public Container(T restrictionClass, AbstractContent<T> content) {
		super(restrictionClass, content);
		// TODO Auto-generated constructor stub
	}

}
