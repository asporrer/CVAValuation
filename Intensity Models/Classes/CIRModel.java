

import java.util.Map;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.montecarlo.model.AbstractModel;
import net.finmath.montecarlo.model.AbstractModelInterface;
import net.finmath.stochastic.RandomVariableInterface;


/**
 * 
 * This class implements a CIR model. 
 * That is to say that the methods getDrift und getFactorLoadings 
 * return the drift and the factor loadings according to the following 
 * SDE. An Euler scheme is used to approximate this process.  The drift and the factor loadings are returned accordingly.
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
		
	}
	
	/**
	 * @return 1 One is returned. This model has only one component.
	 */
	public int getNumberOfComponents() {
		return 1;
	}

	/**
	 * @param componentIndex Has to be zero since this class only has one component
	 * @param randomVariable 
	 * @return randomVariable The random variable passed as argument is returned unchanged.
	 */
	public RandomVariableInterface applyStateSpaceTransform(int componentIndex,
			RandomVariableInterface randomVariable) {
		if(componentIndex != 0) {throw new IllegalArgumentException("The CIR model only has one compomenent. Therefore the parameter componentIndex has to be zero.");}
		// No state space transform is performed.
		return randomVariable;
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
		return   new RandomVariableInterface[] { realizationAtTimeIndex[0].sqrt().mult(nu) };
	}
	
	// Not implemented yet. 
	public AbstractModelInterface getCloneWithModifiedData(
			Map<String, Object> dataModified) throws CalculationException {
		
		throw new UnsupportedOperationException("This method is not implemented yet");
	
	}

}
