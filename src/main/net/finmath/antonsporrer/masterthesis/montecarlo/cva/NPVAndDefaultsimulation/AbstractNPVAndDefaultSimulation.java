package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva.NPVAndDefaultsimulation;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.AbstractProductConditionalFairValue_Model;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.AbstractProductConditionalFairValueProcess;
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

public abstract class AbstractNPVAndDefaultSimulation<T extends AbstractProductConditionalFairValue_Model> implements NPVAndDefaultSimulationInterface{
	
		private AbstractProductConditionalFairValueProcess<T> productProcess;
	
		private double[] defaultProbability;

		public AbstractNPVAndDefaultSimulation(T underlyingModel, AbstractProductConditionalFairValueProcess<T> productProcess) {
			productProcess.setUnderlyingModel(underlyingModel);
			this.productProcess = productProcess;
		}
		
		public RandomVariableInterface getNumeraire(int timeIndex) {
			return productProcess.getNumeraire(timeIndex);
		}
		
		public RandomVariableInterface getDiscountedNPV(int timeIndex, int discountBackToIndex) throws CalculationException {
			return productProcess.getFairValue(timeIndex).div(productProcess.getNumeraire(timeIndex).mult(productProcess.getNumeraire(discountBackToIndex)));
		}
		
		public double getDefaultProbability(int timeIndex) {
			return defaultProbability[timeIndex];
		}

		public TimeDiscretizationInterface getTimeDiscretization() {
			return this.productProcess.getTimeDiscretization();
		}
		
		public int getNumberOfPaths() {
			return this.productProcess.getNumberOfPaths();
		}
		
		
		public void setProductProcess(AbstractProductConditionalFairValueProcess<T> productProcess) {
			this.productProcess = productProcess;
		}
		
		public AbstractProductConditionalFairValueProcess<T> getProductProcess() {
			return this.productProcess;
		}
		
		
		
		
}
