package test.net.finmath.antonsporrer.masterthesis.prototyping;

import net.finmath.exception.CalculationException;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;
import test.net.finmath.antonsporrer.masterthesis.prototyping.CVACalculationIntensityVsWorstCase.CVAMethod;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.AbstractIntensityModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.ConditionalBondFormulaModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products.conditionalproducts.ConditionalCouponBond;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.interestrate.products.conditionalproducts.ConditionalFairValueProductInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.Correlation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;

public class CVACalculationIntensityVsWorstCaseTestDrive {

	public static void main(String[] args) throws CalculationException {
		
		// Bond Model parameters. (Further parameters are hard coded in the static method.)
		double initialTime = 0;
		int numberOfTimeSteps = 20;
		double deltaT = 0.5;
		ConditionalBondFormulaModelInterface bondModel = HullWhiteCreationHelper.createHullWhiteModel(initialTime, numberOfTimeSteps, deltaT);

		// Intensity Model parameters.
		double initialValue = 0;
		double kappa = 0.7;
		double mu = 0.03;
		double nu = 0.04;
		
		AbstractIntensityModel intensityModel = new CIRModel(initialValue, kappa, mu, nu);

		// Product Parameters.
		double[] paymentDates = new double[]{(numberOfTimeSteps - 1)*deltaT};
		double[] periodFactors = new double[]{0.0};
		double[] coupons = new double[]{0.0};
	
		ConditionalFairValueProductInterface myProduct = new ConditionalCouponBond(paymentDates, periodFactors, coupons);
		
		// CVA Parameters.
		double[][] interCorrelations = new double[][]{{0.0},{0.0}};
		CorrelationInterface correlation = new Correlation(interCorrelations);
		TimeDiscretizationInterface timeDiscretization = new TimeDiscretization(initialTime, numberOfTimeSteps, deltaT);
		int seed = 3141;
		int numberOfPaths = 100;
		
		CVACalculationIntensityVsWorstCase cvaCalculationIntensityVsWorstCase = new CVACalculationIntensityVsWorstCase(bondModel, intensityModel, correlation, myProduct, timeDiscretization, numberOfPaths, seed);
		
		System.out.println("Product Value: " + myProduct.getFairValue(0.0, bondModel).getAverage());
		System.out.println("CVA of the Product: " + cvaCalculationIntensityVsWorstCase.getCVA(CVAMethod.CorrelatedIntensity));
		
		
		
	}

}
