package test.net.finmath.antonsporrer.masterthesis.experiments.generics.wildcards;

public interface AInterface<T extends SuperBoundInterface> {

	public T getParameter();
	
}
