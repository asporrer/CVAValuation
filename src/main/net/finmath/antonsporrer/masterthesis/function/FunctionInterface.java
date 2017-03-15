package main.net.finmath.antonsporrer.masterthesis.function;

import net.finmath.stochastic.RandomVariableInterface;

public interface FunctionInterface {

	public RandomVariableInterface getValue(RandomVariableInterface input);
	
}
