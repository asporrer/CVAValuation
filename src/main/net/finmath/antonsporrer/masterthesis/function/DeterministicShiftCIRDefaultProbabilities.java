/*
 * Contact: anton.sporrer@yahoo.com
 */
package main.net.finmath.antonsporrer.masterthesis.function;

import java.util.Map;
import java.util.TreeMap;

import net.finmath.time.TimeDiscretizationInterface;

/**
 * 
 * This function is used by the {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRPlusPlusModel CIR++Model}.
 * The parameters of the CIR model are passed to this function. In addition a series of default probabilities 
 * (P( t<sub> k<sub>j</sub> </sub> < &tau; ))<sub> j= 0, ... , l </sub> is provided to this function.
 * In turn this function provides the deterministic shift function &psi; via {@link #getValue(Double)}. This 
 * deterministic shift function is, as mentioned, used by the {@link main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRPlusPlusModel CIR++Model}
 * and guarantees that the input default probabilities of this function are reproduced by the CIR++ model.
 * That is to say that E[exp(-int_0^t<sub> k<sub>j</sub> </sub> intensity(s) ds)] = P( t<sub> k<sub>j</sub> </sub> < &tau; ) for j= 0, ... , l. Where intensity() is the
 * intensity provided by the CIR++ model.  
 * 
 * <br>
 * <br> - At the moment this class assumes that the keys of the defaultProbabilities are elements of the time discretization points t<sub> 0 </sub> < t<sub> 1 </sub> < ... < t<sub> n </sub> and that
 * t<sub> n </sub> = t<sub> k<sub>l</sub> </sub>.  This could be generalized. 
 * <br> - There is some freedom of choice for psi. Different ways of calculating psi could be implemented. For now assuming psi as piece-wise constant is implemented.
 * 
 * @author Anton Sporrer
 * 
 */
public class DeterministicShiftCIRDefaultProbabilities implements FunctionInterface<Integer, Double> {
	
	// The model parameters used by the CIR++ model
	private double initialValue;
	private double kappa;
	private double mu;
	private double nu;
	
	// The time discretization
	private TimeDiscretizationInterface timeDiscretization;
	
	private double[] psi;
	
	// The default probabilities
	private TreeMap<Integer,Double> defaultProbabilities;
	
	// The integrals of the deterministic shift function psi over intervals [0,t_i] where i is the key.
	private TreeMap<Integer, Double> integratedDeterministicShiftValues;

	// TODO: Implement if needed.
	// This function specifies how the deterministic shift function is defined such that the 
	// integral values (specified by integratedDeterministicShiftValues) of this function are reproduced. 
	// Here is some freedom of choice.
	private FunctionInterface<Double,Double> integratedShiftToshiftFunction;
	
	
	
	/**
	 * 
	 * @param initialValue The initial value of the CIR++ model
	 * @param kappa The kappa of the CIR++ model
	 * @param mu The mu of the CIR++ model
	 * @param nu The nu of the CIR++ model
	 * @param timeDiscretization The time discretization underlying the CIR++ model
	 * @param defaultProbabilities Probabilities (value of the map) that default doesn't occur up to the time point t_i (i is the key of the map) [i,P(t_i < &tau;)]. The highest index has to be equal to the last index of the time discretization.
	 */
	public DeterministicShiftCIRDefaultProbabilities(double initialValue, double kappa, double mu, double nu, TimeDiscretizationInterface timeDiscretization, TreeMap<Integer,Double> defaultProbabilities) {
		
		// TODO: Exception Handling if the HashMap is empty?
		this.initialValue = initialValue;
		this.kappa = kappa;
		this.mu = mu;
		this.nu = nu;
		this.timeDiscretization = timeDiscretization;
		this.defaultProbabilities = defaultProbabilities;
		
		if( timeDiscretization.getNumberOfTimeSteps() != defaultProbabilities.lastKey()) {
			throw new IllegalArgumentException("The highest index of the default probabilities map has to be equal to the last index of the time discretization.");
		}

	}
	
	
	/**
	 * {@link #DeterministicShiftCIRDefaultProbabilities}
	 * 
	 * @param integratedShiftToshiftFunction The convention used to define the deterministic shift. The integral of this function has to reproduce certain values. (Not implemented yet)
	 */
	public DeterministicShiftCIRDefaultProbabilities(double initialValue, double kappa, double mu, double nu, TimeDiscretizationInterface timeDiscretization, TreeMap<Integer,Double> defaultProbabilities, FunctionInterface<Double,Double> integratedShiftToshiftFunction) {
		
		this(initialValue, kappa, mu, nu, timeDiscretization, defaultProbabilities);
		this.integratedShiftToshiftFunction = integratedShiftToshiftFunction;
	}
	
	
	/**
	 * @param discretizationTimeIndex The index of the time discretization for which the deterministic shift value is desired.
	 * @return The deterministic shift value at the specified time discretization index is returned.
	 */
	public Double getValue(Integer discretizationTimeIndex) {
		
		// The deterministic shift psi is calculated in case this hasn't been done yet.
		if(psi == null) {
			calculateIntegratedShiftFromDefaultProbs();
			calculateDeterministicShiftFromDefaultProbabilities();
		}

		return psi[discretizationTimeIndex];
	}

	
	/**
	 * &Psi; is calculated for all given default probabilities (P( t<sub> k<sub>j</sub> </sub> < &tau; ))<sub> j= 0, ... , l </sub> 
	 * <br> &Psi;(t<sub> k<sub>j</sub> </sub>, &kappa;, &mu;, &nu;, initialValue ) = ln( P<sup>CIR</sup>(0,t<sub> k<sub>j</sub> </sub>, &kappa;, &mu;, &nu;, initialValue) / P( t<sub> k<sub>j</sub> </sub> < &tau; ))  , j= 0, ... , l.
	 * <br>
	 * <br> The CIR analytic bond price formula is used here to calculate 
	 * P<sup>CIR</sup>(0,t, &kappa;, &mu;, &nu;, initialValue). 
	 * <br>
	 * <br> (See Master thesis CIR++ chapter.)
	 * 
	 */
	private void calculateIntegratedShiftFromDefaultProbs() {
		
		for(Map.Entry<Integer, Double> entry: defaultProbabilities.entrySet()){
			
			int currentIndexKey = entry.getKey();
			double currentTime = timeDiscretization.getTime( currentIndexKey );
			double currentDefaultProbability = entry.getValue();

			double h = Math.sqrt(kappa*kappa + 2*nu*nu);
			double aBasis = (  2*h*Math.exp( (kappa+h)*currentTime*0.5 )  ) / (  2*h + (kappa+h)*(Math.exp(currentTime*h) - 1)  );
			double aExponent = (2*kappa*mu)/(nu*nu);
			double b = ( 2*(Math.exp(currentTime*h) -1) ) / (  2*h + (kappa + h)*( Math.exp(currentTime*h)-1 )  );
			
			integratedDeterministicShiftValues.put(currentIndexKey, aExponent * Math.log( aBasis ) + (-b * initialValue) - currentDefaultProbability);
		}
		
	}
	
	
	/**
	 * 
	 * <br> Calculating psi (deterministic shift) from Psi (integrated deterministic shift). 
	 * <br> psi is set such that integrating psi with the right point integral approximation
	 * rule results in Psi whereas in addition psi is stepwise constant.
	 * <br> More precisely this function calculates the function values of psi (the deterministic shift) on the time discretization
	 * grid t<sub>0</sub> < t<sub>1</sub> <...< t<sub>n</sub>. It takes the integrated deterministic shift values und sets psi as follows. The time discretization 
	 * points t<sub>k<sub>i</sub> + 1</sub> < ... < t<sub>k<sub>i+1</sub> - 1</sub> < t<sub>k<sub>i+1</sub></sub> lying in between two consecutive integrated deterministic shift indices t<sub>k<sub>i</sub></sub> < t<sub>k<sub>i+1</sub></sub> are set to the following  
	 * constant value V. The constant value V is the value such that multiplying with the time span t<sub>k<sub>i+1</sub></sub> - t<sub>k<sub>i</sub></sub> between the two 
	 * consecutive integrated deterministic shift indices yields the integrated deterministic shift value of the 
	 * higher index minus the integrated deterministic shift value of the 
	 * lower index (in formulas: V := (Psi(t<sub>k<sub>i+1</sub></sub>) - Psi(t<sub>k<sub>i</sub></sub>))/(t<sub>k<sub>i+1</sub></sub> - t<sub>k<sub>i</sub></sub>))
	 */
	private void calculateDeterministicShiftFromDefaultProbabilities() {
		
		////
		// Time Index Variables
		////
		
		// The index of the time being the lower bound of the current interval with respect to the time discretization.
		int indexLeftCurrentIntervalBound = 0;
		
		// The index of the time being the upper bound of the current interval with respect to the time discretization.
		int indexRightCurrentIntervalBound = 0;
		
		
		////
		// Time Variables
		////
		
		// The time being the lower bound of the current interval. 
		double timeLeftCurrentIntervalBound = timeDiscretization.getTime(0);
		
		// The time being the upper bound of the current interval. 
		double timeRightCurrentIntervalBound = 0.0;
		
		// The diameter of the current interval.
		double deltaTCurrentInterval = 0.0;
		
		
		////
		// Function Values of Psi (the integrated deterministic shift)
		////
		
		// Psi at the left interval bound
		double valueLeftCurrentIntervalBound = 0.0;
	
		// Psi at the right interval bound
		double valueRightCurrentIntervalBound = 0.0;
		
		
		// The calculated function value psi
		double currentCalculatedIntervalValue = 0.0;
		

		// In this loop the values of psi are iteratively calculated. The natural ordering of the class Integer used by TreeMap
		// guarantees that this loop goes through the entries in ascending order with respect to the map indices which are also the time indices.
		for(Map.Entry<Integer, Double> entry: integratedDeterministicShiftValues.entrySet()) {
		
			// Updating the index, the time and the value of the upper bound.
			indexRightCurrentIntervalBound = entry.getKey();
			timeRightCurrentIntervalBound = timeDiscretization.getTime(indexRightCurrentIntervalBound);
			valueRightCurrentIntervalBound = entry.getValue();
			
			// Calculating the diameter of the current interval.
			deltaTCurrentInterval = timeRightCurrentIntervalBound - timeLeftCurrentIntervalBound;
			
			// Calculating the values for the finer discretization. Such that integrating over the values  of the  finer integral reproduces Psi.
			currentCalculatedIntervalValue = (valueRightCurrentIntervalBound-valueLeftCurrentIntervalBound)/deltaTCurrentInterval;
		
			// Assigning the calculated value to psi
			for(int indexCurrentInterval = indexLeftCurrentIntervalBound + 1; indexCurrentInterval < indexRightCurrentIntervalBound + 1; indexCurrentInterval++) {
				
				psi[indexCurrentInterval] = currentCalculatedIntervalValue;
				
			}

			// Updating the lower bound values.
			indexLeftCurrentIntervalBound = indexRightCurrentIntervalBound;
			valueLeftCurrentIntervalBound = valueRightCurrentIntervalBound;
			timeLeftCurrentIntervalBound = timeRightCurrentIntervalBound;
			
		}
		
		// The index 0 has to be treated separately.
		psi[0] = psi[1];
		
	}
	
	
	
}
