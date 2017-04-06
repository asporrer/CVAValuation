/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

/**
 * 
 * The loss given default is stored and returned.
 * 
 * @author Anton Sporrer
 *
 */
public abstract class AbstractCVA implements CVAInterface {

	private double lossGivenDefault;
	
	public AbstractCVA(double lossGivenDefault) {
		this.lossGivenDefault = lossGivenDefault;
	}
	
	public double getLGD() {
		return this.lossGivenDefault;
	}
	
}
