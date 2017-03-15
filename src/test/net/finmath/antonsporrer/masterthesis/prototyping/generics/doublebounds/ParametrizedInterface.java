package test.net.finmath.antonsporrer.masterthesis.prototyping.generics.doublebounds;

public interface ParametrizedInterface<T extends BoundInterfaceA & BoundInterfaceB> {

	public T getT();
	
}
