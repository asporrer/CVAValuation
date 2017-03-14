package test.net.finmath.antonsporrer.masterthesis.montecarlo;

import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import net.finmath.functions.LinearAlgebra;
import net.finmath.marketdata.model.curves.DiscountCurveFromForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.BrownianMotionView;
import net.finmath.montecarlo.CorrelatedBrownianMotion;
import net.finmath.montecarlo.assetderivativevaluation.BlackScholesModel;
import net.finmath.montecarlo.interestrate.modelplugins.ShortRateVolatilityModel;
import net.finmath.montecarlo.process.AbstractProcess;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;

public class HullWhiteCreationHelper {

	
	public static HullWhiteModel createHullWhiteModel(double initialTime, int numberOfTimeSteps, double deltaT, int numberOfPaths ) {
		
		// Declaring and initializing the simulation time discretization.
		TimeDiscretizationInterface timeDiscretization = new TimeDiscretization(initialTime, numberOfTimeSteps, deltaT);
		
		// Volatility array for the volatility model.
		double[] volatilities = new double[numberOfTimeSteps +1];
		
		// Mean reversion array for the volatility model.
		double[] meanReversions = new double[numberOfTimeSteps +1];
		
		for(int index = 0; index<numberOfTimeSteps +1; index++) {
			volatilities[index] = 0.3;
			meanReversions[index] = 0.03;
		}
		
		ShortRateVolatilityModel shortRateVolatilityModel = new ShortRateVolatilityModel(timeDiscretization, volatilities, meanReversions);
		// ShortRateVolatilityModelHoLee shortRateVolatilityModelHoLee = new ShortRateVolatilityModelHoLee(0.3);
		
		
		ForwardCurve forwardCurve = ForwardCurve.createForwardCurveFromForwards(
				"forwardCurve"								/* name of the curve */,
				new double[] {0.5 , 1.0 , 2.0 , 5.0 , 40.0}	/* fixings of the forward */,
				new double[] {0.05, 0.05, 0.05, 0.05, 0.05}	/* forwards */,
				0.5							/* tenor / period length */
				);
		
		return createHullWhiteModel(initialTime, numberOfTimeSteps, deltaT, shortRateVolatilityModel, forwardCurve, numberOfPaths);
			
	}
	
	
	
	
	public static HullWhiteModel createHullWhiteModel(double initialTime, int numberOfTimeSteps, double deltaT, ShortRateVolatilityModel shortRateVolatilityModel , ForwardCurve forwardCurve, int numberOfPaths ) {

		// Declaring and initializing the simulation time discretization.
		TimeDiscretizationInterface timeDiscretization = new TimeDiscretization(initialTime, numberOfTimeSteps, deltaT);

		////
		// Declaring and Initializing an Uncorrelated Brownian Motion.
		////
		int numberOfFactors = 2;
		int seed = 1337;
		
		BrownianMotionInterface brownianMotionShortRateModel = new BrownianMotion(timeDiscretization, numberOfFactors, numberOfPaths, seed);
			
		AbstractProcess processShortRateModel = new ProcessEulerScheme(brownianMotionShortRateModel);
			
		////
		// Declaring and initializing the short rate model.
		////
		
		// Declaring the LIBOR discretization.
		
		double initialLIBOR = 0.0; 
		int numberOfTimeStepsLIBOR = 20 /* 14 */; 
		double deltaTLIBOR = 0.5;
		
		TimeDiscretizationInterface liborPeriodDiscretization = new TimeDiscretization(initialLIBOR, numberOfTimeStepsLIBOR, deltaTLIBOR);
		
		// Declaring and initializing the short rate model.
		HullWhiteModel shortRateModel = new HullWhiteModel(liborPeriodDiscretization, null, forwardCurve, new DiscountCurveFromForwardCurve(forwardCurve), shortRateVolatilityModel, null);
		shortRateModel.setProcess(processShortRateModel);
		processShortRateModel.setModel(shortRateModel);
		
		return shortRateModel;
}
	

	/**
	 * @param initialTime
	 * @param numberOfTimeSteps
	 * @param deltaT
	 * @param meanReversionShortRate
	 * @param volatilityShortRate
	 * @param forwardRates Has to have length 5.
	 * @return
	 */
	public static HullWhiteModel createHullWhiteModel(double initialTime, int numberOfTimeSteps, double deltaT, double[] meanReversionShortRate , double[] volatilityShortRate, double[] forwardRates , int numberOfPaths) {
		
		if(numberOfTimeSteps + 1 != volatilityShortRate.length || numberOfTimeSteps + 1 != meanReversionShortRate.length) {throw new IllegalArgumentException("The number of times has to be equal to the length of the mean reversion and volatility array.");}
		if(5 != forwardRates.length) {throw new IllegalArgumentException("The length of forwardRates array has to be five.");}
		
		TimeDiscretizationInterface timeDiscretization = new TimeDiscretization(initialTime, numberOfTimeSteps, deltaT);
		
		ShortRateVolatilityModel shortRateVolatilityModel = new ShortRateVolatilityModel(timeDiscretization, volatilityShortRate, meanReversionShortRate);

		ForwardCurve forwardCurve = ForwardCurve.createForwardCurveFromForwards(
				"forwardCurve"								/* name of the curve */,
				new double[] {0.5 , 1.0 , 2.0 , 5.0 , 40.0}	/* fixings of the forward */,
				forwardRates	/* forwards */,
				0.5							/* tenor / period length */
				);
		
		return createHullWhiteModel(initialTime, numberOfTimeSteps, deltaT, shortRateVolatilityModel, forwardCurve, numberOfPaths);
	}
	
	
	
}
