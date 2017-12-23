package test.net.finmath.antonsporrer.masterthesis.experiments.generics;


public class TestDrive{

	public static void main(String[] args) {
	
	
	EligibleClass eligibleClass = new EligibleClass();
	
	TestClass<EligibleClass> testClass = new TestClass<EligibleClass>();
	
	testClass.testMethod(eligibleClass);
	
	}
	
}
