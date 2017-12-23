/* 
 * Contact: anton.sporrer@yahoo.com
 */

package main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation;

import net.finmath.functions.LinearAlgebra;

/**
 * 
 * The two purposes of this class are. 
 * First to build a matrix from a given block according to 
 * {@link #createCorrelationMatrix(double[][]) }.
 * Secondly, to provide useful methods for the generated matrix.
 * <br> The generated matrix can be interpreted as a correlation matrix of 
 * two random vectors. Where the intercorrelations are specified by the 
 * constructor parameter interCorrelations. The intracorrelations of the
 * two random vectors are zero that is to say that each of the two random vectors 
 * has the identity matrix as correlation matrix.
 * 
 * 
 * @author Anton Sporrer
 *
 */
public class Correlation implements CorrelationInterface {
	
	private double[][] correlationFactor;
	private double[][] correlation;
	
	////
	// The dimensions of the intercorrelation matrix are stored.
	////
	int numberOfInterCorrelationsRows;
	int numberOfInterCorrelationColumns;
	
	/**
	 *  
	 *  This constructor passes the parameter to 
	 *  {@link #createCorrelationMatrix(double[][])}
	 * 	which builds a matrix according to its documentation.
	 * 
	 * @param interCorrelations 
	 */
	public Correlation(double[][] interCorrelations) {
		
		createCorrelationMatrix(interCorrelations);
		numberOfInterCorrelationsRows = interCorrelations.length;
		numberOfInterCorrelationColumns = interCorrelations[0].length;
		
	}
	

	public double[][] getCorrelationMatrix() {
		
		return correlation;
		
	}
	
	
	public double getCorrelation(int rowIndex, int columnIndex) {
		
		return correlation[rowIndex][columnIndex];
	}
	
	
	public double[][] getCorrelationFactorMatrix(){	
		if(correlationFactor == null) {
			correlationFactor =  LinearAlgebra.getFactorMatrix(correlation, correlation.length);
		}
		return correlationFactor;
	}
	
	
	public double getCorrelationFactor(int rowIndex, int columnIndex) {
		if(correlationFactor == null) {
			correlationFactor = LinearAlgebra.getFactorMatrix(correlation, correlation.length);
		}
		return correlationFactor[rowIndex][columnIndex];
	}
	
	
	public int getNumberOfRows() {
		return correlation.length;
	}
	
	
	public int getNumberOfColumns() {
		return correlation[0].length;
	}
	
	
	/**
	 * 
	 * @return numberOfInterCorrelationsRows The row dimension of the intercorrelation matrix passed as constructor parameter.
	 */
	public int getNumberOfInterCorrelationRows() {
		return numberOfInterCorrelationsRows;
	}
	
	/**
	 * 
	 * @return numberOfInterCorrelationsColumns The column dimension of the intercorrelation matrix passed as constructor parameter.
	 */
	public int getNumberOfInterCorrelationColumns() {
		return numberOfInterCorrelationColumns;
	}
	
	
	/**
	 * Let interCorrelation be of dimension m x n. 
	 * This method returns a symmetric and quadratic matrix A of dimension m+n.
	 * The quadratic matrix from index (0,0) to index (m-1, m-1) of A is the identity matrix. 
	 * The same holds for the quadratic matrix from index (m,m) to index (m + n - 1, m + n - 1) of A. 
	 * The matrix from (0, m) to (m - 1, m + n - 1) of A is the passed parameter. This is the 
	 * intercorrelation matrix.  
	 * 
	 * @param interCorrelations 
	 */
	private void createCorrelationMatrix(double[][] interCorrelations) {
		
		int numberOfFactorsFirstModel = interCorrelations.length;
		int numberOfFactorsSecondModel = interCorrelations[0].length;
		
		int numberOfFactorsBothModels = numberOfFactorsFirstModel + numberOfFactorsSecondModel;
		
		double[][] correlationMatrix = new double[numberOfFactorsBothModels][numberOfFactorsBothModels];
		
		
		// Assigning the identity matrix to the diagonal of the matrix.
		for(int index = 0; index < numberOfFactorsFirstModel + numberOfFactorsSecondModel; index++) {
			correlationMatrix[index][index] = 1.0;
		}
		
		// Assigning the inter-correlations to the correlation matrix of the combined models.
		for(int rowIndexInterCorrelationMatrix = 0; rowIndexInterCorrelationMatrix < numberOfFactorsFirstModel; rowIndexInterCorrelationMatrix++ ) {
			for(int columnIndexInterCorrelationMatrix = numberOfFactorsFirstModel; columnIndexInterCorrelationMatrix < numberOfFactorsBothModels; columnIndexInterCorrelationMatrix++ ) {
				// Assigning the upper right intercorrelation part.
				correlationMatrix[rowIndexInterCorrelationMatrix][columnIndexInterCorrelationMatrix] = 
						interCorrelations[rowIndexInterCorrelationMatrix][columnIndexInterCorrelationMatrix - numberOfFactorsFirstModel];
				// Assigning the lower left part of the intercorrelations.
				correlationMatrix[columnIndexInterCorrelationMatrix][rowIndexInterCorrelationMatrix] =
						correlationMatrix[rowIndexInterCorrelationMatrix][columnIndexInterCorrelationMatrix];
			}
		}
		correlation = correlationMatrix;
	}
	
	
	
	
}
