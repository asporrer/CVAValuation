package main.net.finmath.antonsporrer.masterthesis.function;

import net.finmath.montecarlo.RandomVariable;
import net.finmath.stochastic.RandomVariableInterface;

public class IntensityFunctionSwitchShiftFloor implements FunctionInterface {

	double shift;
	
	public IntensityFunctionSwitchShiftFloor(double shift) {
		this.shift = shift;
	}
	
	public RandomVariableInterface getValue(RandomVariableInterface input) {
	
		RandomVariableInterface output = null;
		
		output = (new RandomVariable(shift)).addProduct(input, -1.0).floor(0);
		
		return output;
		
	}

	
	
}
