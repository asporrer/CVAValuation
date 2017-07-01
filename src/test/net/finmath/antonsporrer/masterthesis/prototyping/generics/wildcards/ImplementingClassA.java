package test.net.finmath.antonsporrer.masterthesis.prototyping.generics.wildcards;

public class ImplementingClassA<T  extends SubBoundInterface> implements AInterface<T> {

	T parameter;
	
	public T getParameter() {
		return parameter;
	}

}
