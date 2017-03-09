package test.net.finmath.antonsporrer.masterthesis.prototyping.generics;

public abstract class TestAbstractClass<T extends AbstractRestrictionClass> implements TestInterface<T> {

	
	
	public T testMethod(T testParameter) {
		System.out.println("TestAbstractClass wird durchlaufen");
		testParameter.restrictionMethod();
		return null;
	}
	
	
}
