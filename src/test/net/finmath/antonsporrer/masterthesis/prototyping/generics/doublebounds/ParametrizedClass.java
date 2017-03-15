package test.net.finmath.antonsporrer.masterthesis.prototyping.generics.doublebounds;

public class ParametrizedClass<T extends BoundInterfaceA & BoundInterfaceB> implements ParametrizedInterface<T>  {

	public T getT() {
		return null;
	}

	public static <T extends BoundInterfaceA & BoundInterfaceB> T unwrap(ParametrizedClass<T> parametrizedClass) {
		return parametrizedClass.getT();
	}
	
}
