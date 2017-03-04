package main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel;

import java.util.Map;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.montecarlo.model.AbstractModel;
import net.finmath.montecarlo.model.AbstractModelInterface;
import net.finmath.montecarlo.process.ProcessEulerScheme;
import net.finmath.stochastic.RandomVariableInterface;



/**
 * 
 * This class implements a CIR model. 
 * It is assumed that &nu;<sup>2</sup> < &kappa; &mu;. 
 * This implies that &lambda;<sub>t</sub> > 0, for all t in the simulation horizont.
 * Basically an Euler scheme is used to approximate this process. Although after
 * simulating one step the absolute value function is applied to the result to guarantee the non-negativity.
 * The convergence of this scheme is proved in (Berkaoui, Bossy, Diop, 2006) [https://hal.archives-ouvertes.fr/inria-00000176v2/document].  
 * 
 * 
 * The getDrift und getFactorLoadings method
 * return the drift and the factor loadings according to the following 
 * SDE and an Euler scheme. The method applyStateSpaceTransform will apply the modulus to its parameter of type random variable.
 * 
 * <br>
 * <i>d&lambda;<sub>t</sub> = &kappa; ( &mu; - &lambda;<sub>t</sub> )dt + &nu; (&lambda;<sub>t</sub>)<sup>1/2</sup> dB<sub>t</sub> </i>
 * <br>
 * Where B<sub>t</sub> is a standard Brownian motion. 
 * 
 * 
 * 
 * @author Anton Sporrer
 */

public class CIRModel extends AbstractModel{

	// The start value of the intensity model.
	private double initialValue;

	// The Adjustmen Speed.
	private double kappa;
	
	// The Mean.
	private double mu;
	// The volatility
	private double nu;
	
	
	public CIRModel(double initialValue, double kappa, double mu, double nu) {
		
		super();
		this.initialValue = initialValue;
		this.kappa = kappa;
		this.mu = mu;
		this.nu = nu;
	
		if(nu*nu >= 2*kappa*mu) {throw new IllegalArgumentException("The following inequality has to be satisfied nu*nu < 2*kappa*mu. Otherwise the CIR Model can obtain negative values.");}
	}
	
	public CIRModel(double initialValue, double kappa, double mu, double nu, ProcessEulerScheme process) {
		
		this(initialValue, kappa, mu, nu);
		this.setProcess(process);
		process.setModel(this);
	}
	
	/**
	 * @return 1 One is returned. This model has only one component.
	 */
	public int getNumberOfComponents() {
		return 1;
	}

	/**
	 * The modulus is applied to preserve the non-negativity of the CIR model.
	 * @param componentIndex Has to be zero since this class only has one component
	 * @param randomVariable 
	 * @return randomVariable The modulus is applied to the random variable passed as argument. 
	 */
	public RandomVariableInterface applyStateSpaceTransform(int componentIndex,
			RandomVariableInterface randomVariable) {
		if(componentIndex != 0) {throw new IllegalArgumentException("The CIR model only has one compomenent. Therefore the parameter componentIndex has to be zero.");}
		// The modulus is applied.
		return randomVariable.abs();
	}

	public RandomVariableInterface[] getInitialState() {
		
		// Since no state space transformation is performed the initial value is returned.
		return new RandomVariableInterface[]  {new RandomVariable(initialValue)};
		
	}

	
	/**
	 * Not supported. 
	 * @throws UnsupportedOperationException
	 */
	// Not supported. Not needed. Maybe write new AbstractIntensityModel.
	public RandomVariableInterface getNumeraire(double time)
			throws CalculationException {
		throw new UnsupportedOperationException("This method is not supported");
	}

	public RandomVariableInterface[] getDrift(int timeIndex,
			RandomVariableInterface[] realizationAtTimeIndex,
			RandomVariableInterface[] realizationPredictor) {
		
		return new RandomVariableInterface[] {realizationAtTimeIndex[0].mult(-kappa).add(kappa*mu)};
		
	}

	public RandomVariableInterface[] getFactorLoading(int timeIndex,
			int componentIndex, RandomVariableInterface[] realizationAtTimeIndex) {
		return   new RandomVariableInterface[] { realizationAtTimeIndex[0].abs().sqrt().mult(nu) };
	}
	
	// Not implemented yet. 
	public AbstractModelInterface getCloneWithModifiedData(
			Map<String, Object> dataModified) throws CalculationException {
		
		throw new UnsupportedOperationException("This method is not implemented yet");
	
	}

}
