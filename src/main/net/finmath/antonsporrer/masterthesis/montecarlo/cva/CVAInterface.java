/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;


/**
 * 
 * Interface for the implementation of a CVA.
 * 
 * TODO: Add getCVA, parameters?
 * 
 * @author Anton Sporrer
 *
 */
public interface CVAInterface {
	
	/**
	 * 
	 * @return LGD The loss given default.
	 */
	public double getLGD();
	
}
