/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import main.net.finmath.antonsporrer.masterthesis.integration.Integration;
import main.net.finmath.antonsporrer.masterthesis.integration.Integration.IntegrationMethod;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensitySimulationInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;

/**
 * 
 * This class provides the credit valuation adjustment (CVA) 
 * in case of an intensity based model of default.  
 * The simulation {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensitySimulationInterface}
 * provides the necessary product and default information to apply the following formula.
 * <br> CVA = LGD * E[ int_0^T ( N<sub>0</sub> * NPV(u) / N(u) )<sup>+</sup> * &lambda;(u) * exp( - int_0^u &lambda;(s) ds ) du ]
 * <br >Where LGD is the loss given default N is the numéraire provided by the underlying model NPV is the net present value provided by the underlying model
 * and &lambda; is the default intensity.
 * <br> TODO: Provide source justifying this formula: Bielecki & Rutkowski, Credit Risk ... .
 * @author Anton Sporrer
 *
 * 
 *
 *
 */
public class IntensityBasedCVA extends AbstractCVA{

	public IntensityBasedCVA(double lossGivenDefault) {
		super(lossGivenDefault);
	}
	
	/**
	 * The CVA is calculated according to the following formula.
	 * 
	 * <br> CVA = LGD * E[ int_0^T ( N<sub>0</sub> * NPV(u) / N(u) )<sup>+</sup> * &lambda;(u) * exp( - int_0^u &lambda;(s) ds ) du ]
	 * <br >Where LGD is the loss given default N is the numéraire provided by the underlying model NPV is the net present value provided by the underlying model
	 * and &lambda; is the default intensity.
	 * 
	 * 
	 * @param npvAndDefaultIntensitySimulation The commen simulation of the fair product value and the default intensity.
	 * @param integrationMethod The integration method used to approximate the integral.
	 * @return CVA The credit value adjustment based on the input parameters.
	 * @throws CalculationException
	 */
	@SuppressWarnings("rawtypes")
	public RandomVariableInterface getCVA(NPVAndDefaultIntensitySimulationInterface npvAndDefaultIntensitySimulation, IntegrationMethod integrationMethod) throws CalculationException {
		
		int numberOfFunctionValues = npvAndDefaultIntensitySimulation.getProductProcess().getTimeDiscretization().getNumberOfTimes();
		
		RandomVariableInterface[] functionValues = new RandomVariableInterface[numberOfFunctionValues];
		
		for(int timeIndex = 0; timeIndex < numberOfFunctionValues ; timeIndex++) {
			functionValues[timeIndex] = npvAndDefaultIntensitySimulation.getDiscountedNPV(timeIndex, 0)
					.mult( npvAndDefaultIntensitySimulation.getIntensity(timeIndex) )
					.div( npvAndDefaultIntensitySimulation.getExpOfIntegratedIntensity(timeIndex) ); 
		}
	
		return Integration.getIntegral(functionValues, npvAndDefaultIntensitySimulation.getProductProcess().getTimeDiscretization(), integrationMethod).mult(this.getLGD());

	}
	
}
