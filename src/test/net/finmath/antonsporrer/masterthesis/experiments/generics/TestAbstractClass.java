package test.net.finmath.antonsporrer.masterthesis.experiments.generics;

public abstract class TestAbstractClass<T extends RestrictionInterface> implements TestInterface<T> {

	
	
	public T testMethod(T testParameter) {
		System.out.println("TestAbstractClass wird durchlaufen");
		testParameter.restrictionMethod();
		return null;
	}
	
	
}
