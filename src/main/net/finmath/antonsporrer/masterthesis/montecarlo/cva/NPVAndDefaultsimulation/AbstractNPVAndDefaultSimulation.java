package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import java.util.concurrent.ConcurrentHashMap;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;
import net.finmath.exception.CalculationException;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;

/**
 * 
 * Underlying model and product have to be compatible.
 * 
 * @author Anton Sporrer
 *
 */

public abstract class AbstractNPVAndDefaultSimulation<T extends ProductConditionalFairValue_ModelInterface> implements NPVAndDefaultSimulationInterface<T>{
		
		private ProductConditionalFairValueProcessInterface<T> productProcess;
	
		// This hash map contains the default probabilities. More precisely the value associated with with key i 
		// is the probability of default in the interval (0, t_{i}].
		final protected ConcurrentHashMap<Integer, Double> defaultProbabilities;

		public AbstractNPVAndDefaultSimulation(T underlyingModel, ProductConditionalFairValueProcessInterface<T> productProcess) {
			productProcess.setUnderlyingModel(underlyingModel);
			this.productProcess = productProcess;
			defaultProbabilities = new ConcurrentHashMap<Integer, Double>();
			
		}
		
		public RandomVariableInterface getNumeraire(int timeIndex) throws CalculationException {
			return productProcess.getNumeraire(timeIndex);
		}
		
		public RandomVariableInterface getDiscountedNPV(int timeIndex, int discountBackToIndex) throws CalculationException {
			return productProcess.getFairValue(timeIndex).div(productProcess.getNumeraire(timeIndex).mult(productProcess.getNumeraire(discountBackToIndex)));
		}
		
		/**
		 * 
		 * @param 
		 * @return The probability of default in the interval (0, t_{timeIndex}]
		 * @throws CalculationException 
		 * 
		 */
		public double getDefaultProbability(int timeIndex) throws CalculationException {
			return defaultProbabilities.get(timeIndex);
		}
		
		
		public TimeDiscretizationInterface getTimeDiscretization() {
			return this.productProcess.getTimeDiscretization();
		}
		
		public int getNumberOfPaths() {
			return this.productProcess.getNumberOfPaths();
		}
		
		
		public void setProductProcess(ProductConditionalFairValueProcessInterface<T> productProcess) {
			this.productProcess = productProcess;
		}
		
		public ProductConditionalFairValueProcessInterface<T> getProductProcess() {
			return this.productProcess;
		}
		
		
		
		
}
