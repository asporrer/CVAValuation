package test.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import java.util.List;
import java.util.concurrent.ExecutionException;
import main.net.finmath.antonsporrer.masterthesis.function.FunctionInterface;
import main.net.finmath.antonsporrer.masterthesis.integration.Integration.IntegrationMethod;
import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.ConstrainedWorstCaseCVA;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.IntensityBasedCVA;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndCorrelatedDefaultIntensitySimulation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation.NPVAndDefaultIntensityFunctionSimulation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.Correlation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.CouponBondConditionalFairValueProcess;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.SwapConditionalFairValueProcess;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.stochastic.RandomVariableInterface;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;

public class IntBasedCWCCVAforDifCor {
	
	
	// Timediscretization 
	private double initialTime = 0.0; 
	private int numberOfTimeSteps; 
	private double timeStepSize = 0.1; 
	
	
	// Random Number Parameters
	private int numberOfPaths = 10000; 
	private int seed = 1337;
	
	
	// Hull White Parameters
	double[] meanReversion;
	double[] volatilities; 
	double[] forwardRates;
	
	
	// CIR Parameters
	private double initialValue = 0.02;
	private double kappa = 0.03;
	private double mu = 0.02; 
	private double nu = 0.02;
	
	
	// Coupon Bond Parameters
	private double[] paymentDates;
	private double[] periodFactors;
	private double[] coupons;
	
	
	// Swap Parameters
	private double[] paymentDatesFixingDates;
	private double swapRate = 0.01;
	
	
	// Correlation Parameters
	private double[] correlations;
	
	
	// Lando Function
	private List<FunctionInterface<RandomVariableInterface,RandomVariableInterface>> landoFunctions;
	

	
	//
	// CVA
	//
	private IntensityBasedCVA intensityBasedCVA = new IntensityBasedCVA(1.0);
	 
	private double penaltyFactor = 60.0;	
	private ConstrainedWorstCaseCVA constrainedWorstCaseCVA = new ConstrainedWorstCaseCVA(1.0);
	
	
	public IntBasedCWCCVAforDifCor( int numberOfTimeSteps, double[] meanReversion, double[] volatilities, double[] forwardRates, double[] correlations, double[] paymentDatesFixingDates, double[] paymentDates, double[] periodFactors, double[] coupons ) {
		
		this.numberOfTimeSteps = numberOfTimeSteps;
		this.meanReversion = meanReversion;
		this.volatilities = volatilities;
		this.forwardRates = forwardRates;
		this.correlations = correlations;
		this.paymentDatesFixingDates = paymentDatesFixingDates;
		this.paymentDates = paymentDates;
		this.periodFactors = periodFactors;
		this.coupons = coupons;
		
	}
	
	
	public void setInitialTime(double initialTime) {
		this.initialTime = initialTime;
	}
	
	
	public void setTimeStepSize(double timeStepSize) {
		this.timeStepSize = timeStepSize;
	}
	
	
	public void setNumberOfPaths(int numberOfPaths) {
		this.numberOfPaths = numberOfPaths;
	}
	
	
	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	
	public void setInitialValue(double initialValue) {
		this.initialValue = initialValue;
	}
	
	
	public void setKappa(double kappa) {
		this.kappa = kappa;
	}
	
	
	public void setMu(double mu) {
		this.mu = mu;
	}
	
	
	public void setNu(double nu) {
		this.nu = nu;
	}
	
	
	public void setSwapRate(double swapRate) {
		this.swapRate = swapRate;
	}
	
	
	public void setPenaltyFactor(double penaltyFactor) {
		this.penaltyFactor = penaltyFactor;
	}
	
	
	public void setLandoFunction(List<FunctionInterface<RandomVariableInterface, RandomVariableInterface>> landoFunctions) {
		this.landoFunctions = landoFunctions;
	}
	

	/**
	 * 
	 * Based on the parameters provided to the constructor the constrained worst case CVA and the 
	 * intensity based CVA (using the correlation approach) of the Coupon bond and the swap is calculated.
	 * 
	 * <br> First:  Product-Dimesion [+][][]: [0][][] ~ Bond,  [1][][] ~ Swap.
	 * <br> Second: Method-Dimesion [][+][]: [][0][] ~ Intensity based via correlation,  [][1][] ~ constrained worst case.
	 * <br> Third:  Corr Parameter-Dimesion [][][+]: [][][i] ~ Correlation Parameter with index i.
	 * 
	 * @return Array of CVAs Product x Method x Parameter
	 */
	public  double[][][] getIntBasedCWCCVAforCor() {
		
		// Hull White Model
		HullWhiteModel underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(initialTime, numberOfTimeSteps, timeStepSize, meanReversion, volatilities, forwardRates, numberOfPaths ); 
		
		
		// CIR Model 
		CIRModel intensityModel = new CIRModel(initialValue, kappa,  mu,  nu);
		
		
		// Bond is set
		CouponBondConditionalFairValueProcess<HullWhiteModel> bondProductProcess = new CouponBondConditionalFairValueProcess<HullWhiteModel>(underlyingModel, paymentDates, periodFactors, coupons);
		
		
		// Swap is set
		SwapConditionalFairValueProcess<HullWhiteModel> swapProductProcess = new SwapConditionalFairValueProcess<HullWhiteModel>(underlyingModel, paymentDatesFixingDates, swapRate);
		
		
		// Declaring the npv and the cva variables.
		NPVAndCorrelatedDefaultIntensitySimulation<HullWhiteModel> npvAndCorrDefaultIntensitySimulation = null;
		
		
		// 2-dimensional array holding the results. The first row contains the intensity based CVA the second row contains the constrained worst case CVA.
		double[][][] results = new double[2][2][correlations.length];
		
		
		for(int correlationIndex = 0 ; correlationIndex < correlations.length; correlationIndex++) {
			
			CorrelationInterface correlation = new Correlation(new double[][]{{correlations[correlationIndex]},{0.0}});;
			
			npvAndCorrDefaultIntensitySimulation = new NPVAndCorrelatedDefaultIntensitySimulation<HullWhiteModel>(underlyingModel, bondProductProcess, intensityModel , correlation , seed);
			
			try {
				
				results[0][0][correlationIndex] = intensityBasedCVA.getCVA(npvAndCorrDefaultIntensitySimulation, IntegrationMethod.Trapezoidal).getAverage();
				results[0][1][correlationIndex] = constrainedWorstCaseCVA.getConstrainedWorstCaseCVA(npvAndCorrDefaultIntensitySimulation, penaltyFactor);
				
				npvAndCorrDefaultIntensitySimulation.plugInProductProcess(swapProductProcess);
				
				results[1][0][correlationIndex] = intensityBasedCVA.getCVA(npvAndCorrDefaultIntensitySimulation, IntegrationMethod.Trapezoidal).getAverage();
				results[1][1][correlationIndex] = constrainedWorstCaseCVA.getConstrainedWorstCaseCVA(npvAndCorrDefaultIntensitySimulation, penaltyFactor);
				
			} catch (CalculationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return results;

	} 
	
	
	/**
	 * 
	 * Based on the parameters provided to the constructor the constrained worst case CVA and the 
	 * intensity based CVA (using Lando's approach) of the Coupon bond and the swap is calculated.
	 * 
	 * <br> First:  Product-Dimesion [+][][]: [0][][] ~ Bond,  [1][][] ~ Swap.
	 * <br> Second: Method-Dimesion [][+][]: [][0][] ~ Intensity based via Lando function,  [][1][] ~ constrained worst case.
	 * <br> Third:  Lando-Function-Dimesion [][][+]: [][][i] ~ Lando function with index i.
	 * 
	 * @return Array of CVAs Product x Method x Parameter
	 */
	public  double[][][] getIntBasedCWCCVAforLandoPar() {
		
		
		// Short Rate Model
		HullWhiteModel underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(initialTime, numberOfTimeSteps, timeStepSize, meanReversion, volatilities, forwardRates, numberOfPaths );
		
		
		// Workaround to get Hull White Model with modified seed of the stochastic driver.
		
		ProcessEulerScheme processModifiedSeed = new ProcessEulerScheme( underlyingModel.getProcess().getStochasticDriver().getCloneWithModifiedSeed(seed) );	
		underlyingModel.setProcess(processModifiedSeed);
		processModifiedSeed.setModel(underlyingModel);
		
		
		// Bond for Lando is set
		CouponBondConditionalFairValueProcess<HullWhiteModel> bondProductProcessForLando = new CouponBondConditionalFairValueProcess<HullWhiteModel>(underlyingModel, paymentDates, periodFactors, coupons);
		
		// Swap for Lando is set
		SwapConditionalFairValueProcess<HullWhiteModel> swapProductProcessForLando = new SwapConditionalFairValueProcess<HullWhiteModel>(underlyingModel, paymentDatesFixingDates, swapRate);
		
		
		
		double[][][] results = new double[2][2][landoFunctions.size()];
		

		for(int indexLandoFunction = 0 ; indexLandoFunction < landoFunctions.size(); indexLandoFunction++) {
			
			// Product Coupon Bond is assigned.
			NPVAndDefaultIntensityFunctionSimulation<HullWhiteModel> npvAndLandoDefaultIntensitySimulation = new NPVAndDefaultIntensityFunctionSimulation<HullWhiteModel>( underlyingModel , bondProductProcessForLando, seed, landoFunctions.get(indexLandoFunction));
			
			try {
					
					results[0][0][indexLandoFunction] = intensityBasedCVA.getCVA(npvAndLandoDefaultIntensitySimulation, IntegrationMethod.Trapezoidal).getAverage();
					results[0][1][indexLandoFunction] = constrainedWorstCaseCVA.getConstrainedWorstCaseCVA(npvAndLandoDefaultIntensitySimulation, penaltyFactor);
					
// TODO: Remove debug code
System.out.println("intensity CVA Bond value : " + results[0][0][indexLandoFunction]);
System.out.println("CWC CVA Bond value : " + results[0][1][indexLandoFunction]);	

					// Product Swap is assigned.
					npvAndLandoDefaultIntensitySimulation.plugInProductProcess(swapProductProcessForLando);
					
					results[1][0][indexLandoFunction] = intensityBasedCVA.getCVA(npvAndLandoDefaultIntensitySimulation, IntegrationMethod.Trapezoidal).getAverage();
					results[1][1][indexLandoFunction] = constrainedWorstCaseCVA.getConstrainedWorstCaseCVA(npvAndLandoDefaultIntensitySimulation, penaltyFactor);
					
// TODO: Remove debug code
System.out.println("intensity CVA Bond value : " + results[1][0][indexLandoFunction]);
System.out.println("CWC CVA Bond value : " + results[1][1][indexLandoFunction]);		
					
				} catch (CalculationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			return results;
			
	}
	
	
	/**
	 * 
	 * Based on the parameters provided to the constructor the fair value at 0 of the Coupon bond is calculated.
	 * 
	 * @return Net Present Value at Zero of the Coupon Bond
	 * @throws CalculationException
	 */
	public double getNPVAtZeroOfCouponBond() throws CalculationException { 
		
		HullWhiteModel underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(initialTime, numberOfTimeSteps, timeStepSize, meanReversion, volatilities, forwardRates, numberOfPaths );
		
		// Coupon Bond
		CouponBondConditionalFairValueProcess<HullWhiteModel> bondProductProcessForLando = new CouponBondConditionalFairValueProcess<HullWhiteModel>(underlyingModel, paymentDates, periodFactors, coupons);
		
		return bondProductProcessForLando.getFairValue(0).getAverage();
	}
	
	
	/**
	 * 
	 * Based on the parameters provided to the constructor the fair value at 0 of the swap is calculated.
	 * 
	 * @return Net Present Value at Zero of the swap
	 * @throws CalculationException
	 */
	public double getNPVAtZeroOfSwap() throws CalculationException {
		
		HullWhiteModel underlyingModel = HullWhiteCreationHelper.createHullWhiteModel(initialTime, numberOfTimeSteps, timeStepSize, meanReversion, volatilities, forwardRates, numberOfPaths );
		
		// Swap 
		SwapConditionalFairValueProcess<HullWhiteModel> swapProductProcessForLando = new SwapConditionalFairValueProcess<HullWhiteModel>(underlyingModel, paymentDatesFixingDates  , swapRate );
		
		return swapProductProcessForLando.getFairValue(0).getAverage();
	}
	
	
}
