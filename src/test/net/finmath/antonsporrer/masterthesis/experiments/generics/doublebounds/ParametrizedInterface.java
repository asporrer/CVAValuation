package test.net.finmath.antonsporrer.masterthesis.experiments.generics.doublebounds;

public interface ParametrizedInterface<T extends BoundInterfaceA & BoundInterfaceB> {

	public T getT();
	
}
