package test.net.finmath.antonsporrer.masterthesis.prototyping.generics.wildcards;

public interface AInterface<T extends SuperBoundInterface> {

	public T getParameter();
	
}
