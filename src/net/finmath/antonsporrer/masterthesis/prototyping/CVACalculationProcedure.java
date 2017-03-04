package net.finmath.antonsporrer.masterthesis.prototyping;

import net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;
import net.finmath.montecarlo.assetderivativevaluation.BlackScholesModel;
import net.finmath.exception.CalculationException;
import net.finmath.functions.LinearAlgebra;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.curves.DiscountCurveFromForwardCurve;
import net.finmath.marketdata.model.curves.DiscountCurveInterface;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.model.curves.ForwardCurveInterface;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.BrownianMotionView;
import net.finmath.montecarlo.CorrelatedBrownianMotion;
import net.finmath.montecarlo.IndependentIncrementsInterface;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.montecarlo.interestrate.modelplugins.AbstractLIBORCovarianceModelParametric;
import net.finmath.montecarlo.interestrate.modelplugins.HullWhiteLocalVolatilityModel;
import net.finmath.montecarlo.interestrate.modelplugins.ShortRateVolailityModelInterface;
import net.finmath.montecarlo.interestrate.modelplugins.ShortRateVolatilityModel;
import net.finmath.montecarlo.interestrate.modelplugins.ShortRateVolatilityModelHoLee;
import net.finmath.montecarlo.process.AbstractProcess;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.stochastic.RandomVariableMutableClone;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationInterface;

public class CVACalculationProcedure {

	public static void main(String[] args) throws CalculationException {
		////
		// Declaring and initializing the time discretization.
		////
			// Declaring and initializing the simulation time discretization.
			double initial = 0;
			int numberOfTimeSteps = 20 * 7;
			double deltaT = 0.05; 
			
			TimeDiscretizationInterface timeDiscretization = new TimeDiscretization(initial, numberOfTimeSteps, deltaT);
			
			
			
		////
		// Declaring a Correlated Brownian Motion.
		////
		
			////
			// Declaring and Initializing an Uncorrelated Brownian Motion.
			////
			
			int numberOfFactors = 3;
			int numberOfPaths = 50000;
			int seed = 1337;
			
			// Will be passed as parameter to the correlated Brownian motion.
			BrownianMotionInterface uncorrelatedBrownianMotion = new BrownianMotion(timeDiscretization, numberOfFactors, numberOfPaths, seed);
		
			////
			// Calculating the Factor Loadings. 
			////
			
			double rho0_2 = 0.3;
			double rho1_2 = -0.3;
			
			double[][] interCorrelationMatrix = new double[2][1];
			
			
			interCorrelationMatrix[0][0] = rho0_2;
			interCorrelationMatrix[1][0] = rho1_2;

			double[][] correlationMatrix = createCorrelationMatrixTwoModelSetting(interCorrelationMatrix);
			
			double[][] factorOfCorrelationMatrix = LinearAlgebra.getFactorMatrix(correlationMatrix, numberOfFactors);
			
			double[][] testResult = multiplyMatrices(factorOfCorrelationMatrix, transpose(factorOfCorrelationMatrix)); 
			
	
			BrownianMotionInterface correlatedBrownianMotion = new CorrelatedBrownianMotion(uncorrelatedBrownianMotion, factorOfCorrelationMatrix);
		
		// Now the correlated Brownian motion is calculated. It will be split in to parts. Each part will be passed to a different process.
		// These processes will be passed to different models. One model for the short rate one model for the intensity. 
		// The models are then used to simulate the intensity and short rate and subsequently the LIBOR and Bond prices.
		// This is the setup we need to evaluate the CVA of simple products. And experiment with the effect of rho on the 
		// CVA.
			
		////
		// Creating the short rate model and the intensity model with the respective processes. 
		// The Processes hold the inter-correlations between the models.
		////
		
			// Creating the Brownian motions needed for the intensity model and the short rate model from the correlated Brownian motion.
			
			Integer[] bmFactorsForShortRateModel = {new Integer(0), new Integer(1)}; 
			Integer[] bmFactorsForIntensity = {new Integer(2)};
			
			BrownianMotionInterface brownianMotionShortRateModel = new BrownianMotionView( correlatedBrownianMotion, bmFactorsForShortRateModel);
			BrownianMotionInterface brownianMotionIntensityModel = new BrownianMotionView( correlatedBrownianMotion, bmFactorsForIntensity);
			
			AbstractProcess processShortRateModel = new ProcessEulerScheme(brownianMotionShortRateModel);
			AbstractProcess processIntensityModel = new ProcessEulerScheme(brownianMotionIntensityModel);
			
			
			////
			// Declaring and initializing the intensity model.
			////
			
			BlackScholesModel intensityModel = new BlackScholesModel(0.03, 0.03, 0.03);
			intensityModel.setProcess(processIntensityModel);
			processIntensityModel.setModel(intensityModel);
			
			////
			// Declaring and initializing the short rate model.
			////
			
			// Declaring the LIBOR discretization.
			
			double initialLIBOR = 0.0; 
			int numberOfTimeStepsLIBOR = 14; 
			double deltaTLIBOR = 0.5;
			
			TimeDiscretizationInterface liborPeriodDiscretization = new TimeDiscretization(initialLIBOR, numberOfTimeStepsLIBOR, deltaTLIBOR);
			
			ForwardCurve forwardCurve = ForwardCurve.createForwardCurveFromForwards(
					"forwardCurve"								/* name of the curve */,
					new double[] {0.5 , 1.0 , 2.0 , 5.0 , 40.0}	/* fixings of the forward */,
					new double[] {0.05, 0.05, 0.05, 0.05, 0.05}	/* forwards */,
					0.5							/* tenor / period length */
					);
			
			
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
			
			
			// Declaring and initializing the short rate model.
			HullWhiteModel shortRateModel = new HullWhiteModel(liborPeriodDiscretization, null, forwardCurve, new DiscountCurveFromForwardCurve(forwardCurve), shortRateVolatilityModel, null);
			shortRateModel.setProcess(processShortRateModel);
			processShortRateModel.setModel(shortRateModel);
			
			// Calculating the CVA of a zero coupon bond with maturity 6;
			double bondMaturity = 6.5;
			
			System.out.println( "Zero Coupon Bond CVA: " + calculateZeroCouponBondCVA(shortRateModel, intensityModel, bondMaturity) );
			
			System.out.println("Intensity: " + intensityModel.getProcessValue(140, 0).getAverage()); 	
			
			System.out.println("Numéraire at Zero: " + shortRateModel.getNumeraire(0).getAverage());
			System.out.println("E[1/Numéraire]*N(0): " + new RandomVariable(1.0).div(shortRateModel.getNumeraire(bondMaturity)).getAverage());
			System.out.println("Zero Coupon Bond Price: " + shortRateModel.getZeroCouponBond(0.0, bondMaturity).getAverage());
			
			

			
	}

	
	/**
	 * Let interCorrelation be of dimension m x n. 
	 * This method returns a symmetric and quadratic matrix A of dimension m+n.
	 * The quadratic matrix from (0,0) to (m-1, m-1) of A is the identity matrix. 
	 * The same holds for the quadratic matrix from (m,m) to (m + n - 1, m + n - 1) of A. 
	 * The matrix from (0, m) to (m - 1, m + n - 1) of A is passed parameter. This is the 
	 * intercorrelation matrix.  
	 * 
	 * @param interCorrelations 
	 * @return The correlation matrix for both models.
	 */
	public static double[][] createCorrelationMatrixTwoModelSetting(double[][] interCorrelations) {
		
		int numberOfFactorsFirstModel = interCorrelations.length;
		int numberOfFactorsSecondModel = interCorrelations[0].length;
		
		int numberOfFactorsBothModels = numberOfFactorsFirstModel + numberOfFactorsSecondModel;
		
		double[][] correlationMatrixBothModels = new double[numberOfFactorsBothModels][numberOfFactorsBothModels];
		
		
		// Assigning the identity matrix to the the first quadratic diagonal matrix.
		for(int index = 0; index < numberOfFactorsFirstModel + numberOfFactorsSecondModel; index++) {
			correlationMatrixBothModels[index][index] = 1.0;
		}
		
		// Assigning the inter-correlations to the two model correlation matrix.
		for(int rowIndexInterCorrelationMatrix = 0; rowIndexInterCorrelationMatrix < numberOfFactorsFirstModel; rowIndexInterCorrelationMatrix++ ) {
			for(int columnIndexInterCorrelationMatrix = numberOfFactorsFirstModel; columnIndexInterCorrelationMatrix < numberOfFactorsBothModels; columnIndexInterCorrelationMatrix++ ) {
				// Assigning the upper right intercorrelation part.
				correlationMatrixBothModels[rowIndexInterCorrelationMatrix][columnIndexInterCorrelationMatrix] = 
						interCorrelations[rowIndexInterCorrelationMatrix][columnIndexInterCorrelationMatrix - numberOfFactorsFirstModel];
				// Assigning the lower left part of the intercorrelations.
				correlationMatrixBothModels[columnIndexInterCorrelationMatrix][rowIndexInterCorrelationMatrix] =
						correlationMatrixBothModels[rowIndexInterCorrelationMatrix][columnIndexInterCorrelationMatrix];
			}
		}
			
		return correlationMatrixBothModels;
	}
	
	
	/**
	 * Returns the product C = A*B.
	 * 
	 * @param A
	 * @param B
	 * @return C = A*B 
	 */
	public static double[][] multiplyMatrices(double[][] A, double[][] B) throws IllegalArgumentException  {
		
		int numberOfRowsA = A.length;
		int numberOfColumnsA = A[0].length;
		
		int numberOfRowsB = B.length;
		int numberOfColumnsB = B[0].length;
		
		if(numberOfColumnsA != numberOfRowsB ) {
			throw new IllegalArgumentException();
		}

		// Declaring the calculation result.
		double[][] C = new double[numberOfRowsA][numberOfColumnsB];
		
		////
		// Calculating the matrix product.
		////
		
		double summationHelper = 0.0;
		
		// Looping over all rows.
		for(int rowIndex = 0; rowIndex < numberOfColumnsB; rowIndex++ ) {
			// Looping over all columns.
			for(int columnIndex = 0; columnIndex < numberOfRowsA; columnIndex++) {
				
				summationHelper = 0.0;
				// Calculating the scalar product of column times row.
				for(int summationIndex = 0; summationIndex<numberOfRowsB; summationIndex++) {
					summationHelper = A[rowIndex][summationIndex]* B[summationIndex][columnIndex] + summationHelper;
				}
				
				C[rowIndex][columnIndex] = summationHelper;
				
			}
		}
		
		return C;
	}
	
	
	/**
	 * Returns the transposed matrix.
	 *
	 * @param A
	 * @return A^T
	 */
	public static double[][] transpose(double[][] A) {
		
		int numberOfRowsA = A.length;
		int numberOfColumnsA = A[0].length;
		
		double[][] AT = new double[numberOfColumnsA][numberOfRowsA];
		
		for(int rowIndexA = 0; rowIndexA < numberOfRowsA; rowIndexA++) {
			for(int columnIndexA= 0 ; columnIndexA < numberOfColumnsA; columnIndexA++) {
				AT[columnIndexA][rowIndexA] = A[rowIndexA][columnIndexA];
			}
		}

		return AT;
	}
	
	/**
	 * This method returns the CVA at time zero of a zero coupon bond.
	 * The bond prices process is modeled by an Hull White short rate model.
	 * The default is modeled by an intensity based approach.
	 * The formula stated in Credit Risk, Bielecki and Rutkoski page 144 is
	 * used to calculate the CVA.
	 * 
	 * @return CVA of a zero coupon bond.
	 * @throws CalculationException 
	 */
	public static double calculateZeroCouponBondCVA( HullWhiteModel shortRateModel, BlackScholesModel intensityModel, double bondMaturity ) throws CalculationException {
		
		// The time discretization used for the simulation steps.
		TimeDiscretizationInterface timeDiscretization = intensityModel.getTimeDiscretization();
		
		int numberOfTimeSteps = timeDiscretization.getNumberOfTimeSteps();
		
		
		// This variable will be calculated successively in the following for loop. 
		RandomVariableInterface cva = new RandomVariable(0.0);
		
		// t.
		double currentTime = 0.0;
		
		// t_(i+1) - t_(i).
		RandomVariableInterface currentDeltaT = null;
		
		// Storing P(T;t)/N(t) where P is the zero coupon bond with maturity T = bondMaturity and N is the numéraire of the short rate model. 
		// t is the current time at the current time index with respect to the for loop.
		RandomVariableInterface currentBondDividedByCurrentNumeraire = null;
		
		// Default intensity.
		RandomVariableInterface currentIntensity = null;
		
		// Approximation of exp(\int_0^tn intensity(s) ds ) by exp( sum_(i = 0)^n (t_(i+1) - t_(i)) intensity(t_i) ).
		RandomVariableInterface currentExponentialFunctionOfIntensityIntegral = new RandomVariable(1.0);
		


		for(int timeIndex = 0; timeIndex < numberOfTimeSteps; timeIndex++) {
			
			currentTime = timeDiscretization.getTime(timeIndex);
			
			currentDeltaT = new RandomVariable(timeDiscretization.getTime(timeIndex+1) -  currentTime);
			
			// P(bondMaturity ; t_timeIndex)/N(t_timeIndex)
			currentBondDividedByCurrentNumeraire = shortRateModel.getZeroCouponBond(currentTime , bondMaturity).div(shortRateModel.getNumeraire(currentTime));

			
			// intensity(t_timeIndex)
			currentIntensity = intensityModel.getProcessValue(timeIndex, 0);
			
			// Calculating P(bondMaturity ; t_timeIndex)/N(t_timeIndex)  *  exp( sum_(k = 0)^(i-1) (t_(k+1) - t_(k)) intensity(t_k)  )  *  intensity(t_i)  *  ( (t_(i+1) - t_(i)).
			cva = cva.addProduct( currentBondDividedByCurrentNumeraire.mult(currentExponentialFunctionOfIntensityIntegral).mult(currentIntensity)  , currentDeltaT);
			
			// Multiplying the new intensity step for the next iteration.
			// Calculating exp( sum_(k = 0)^(i-1) (t_(k+1) - t_(k)) intensity(t_k)  ) *  exp(  (t_(i+1) - t_(i)) intensity(t_i) ).
			currentExponentialFunctionOfIntensityIntegral = currentExponentialFunctionOfIntensityIntegral.mult( currentIntensity.mult(currentDeltaT).exp() );
				
		}
	
		// ToDo 
		return cva.getAverage();
	}
	
	
}
