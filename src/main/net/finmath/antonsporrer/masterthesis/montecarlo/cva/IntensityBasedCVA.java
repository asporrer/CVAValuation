package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import main.net.finmath.antonsporrer.masterthesis.integration.Integration;
import main.net.finmath.antonsporrer.masterthesis.integration.Integration.IntegrationMethod;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensitySimulationInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

public class IntensityBasedCVA extends AbstractCVA{

	public IntensityBasedCVA(double lossGivenDefault) {
		super(lossGivenDefault);
	}
	
	@SuppressWarnings("rawtypes")
	public RandomVariableInterface getCVA(NPVAndDefaultIntensitySimulationInterface npvAndDefaultIntensitySimulation, IntegrationMethod integrationMethod) throws CalculationException {
		// TODO: Integration schemes. First approach: just simple integration code here.
		
		int numberOfFunctionValues = npvAndDefaultIntensitySimulation.getProductProcess().getTimeDiscretization().getNumberOfTimes();
		
		RandomVariableInterface[] functionValues = new RandomVariableInterface[numberOfFunctionValues];
		
		for(int timeIndex = 0; timeIndex < numberOfFunctionValues ; timeIndex++) {
			functionValues[timeIndex] = npvAndDefaultIntensitySimulation.getDiscountedNPV(timeIndex, 0)
					.mult( npvAndDefaultIntensitySimulation.getIntensity(timeIndex) )
					.mult( npvAndDefaultIntensitySimulation.getExpOfIntegratedIntensity(timeIndex) );
		}
		
		return Integration.getIntegral(functionValues, npvAndDefaultIntensitySimulation.getProductProcess().getTimeDiscretization(), integrationMethod).mult(this.getLGD());

	}
	
}
