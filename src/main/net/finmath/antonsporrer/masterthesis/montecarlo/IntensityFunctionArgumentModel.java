package main.net.finmath.antonsporrer.masterthesis.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

public interface IntensityFunctionArgumentModel {

	public RandomVariableInterface getIntensityFunctionArgument(int timeIndex, int componentIndex) throws CalculationException;
	
}
