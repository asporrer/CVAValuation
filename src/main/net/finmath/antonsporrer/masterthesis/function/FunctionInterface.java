/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.function;



/**
 * 
 * An interface for the implementation of a function mapping from objects of type T to objects of type T. 
 * 
 * @author Anton Sporrer
 *
 */
public interface FunctionInterface<S,T>  {

	public T getValue(S input) throws IllegalArgumentException;

}
