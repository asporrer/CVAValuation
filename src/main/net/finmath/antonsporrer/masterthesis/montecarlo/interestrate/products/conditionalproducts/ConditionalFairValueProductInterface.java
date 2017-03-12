package main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products.conditionalproducts;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.ConditionalBondFormulaModelInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

@Deprecated
public interface ConditionalFairValueProductInterface {
	
	// TODO: Implement addional getFairValue method with parameters int evaluationTimeIndex, ... .
	
	public RandomVariableInterface getFairValue(double evaluationTime, ConditionalBondFormulaModelInterface conditionalBondFormulaModel) throws CalculationException ;
	
	
}
