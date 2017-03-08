package main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation;

public interface CorrelationInterface {

	/**
	 * 
	 * @return correlationMatrix The whole correlation matrix is returned.
	 */
	public double[][] getCorrelationMatrix();
	
	/**
	 * 
	 * @param rowIndex The row index of the desired entry of the correlation matrix.
	 * @param columnIndex The column index of the desired entry of the correlation matrix.
	 * @return The correlation matrix entry with row index rowIndex and column index columnIndex.
	 */
	public double getCorrelation(int rowIndex, int columnIndex);
	
	
	/**
	 * @return correlationFactorMatrix The factor F of the correlation C. That is to say C = F*F<sup>T<sup>.
	 */
	public double[][] getCorrelationFactorMatrix();
	
	
	/**
	 * @param rowIndex The row index of the desired entry of the factor matrix.
	 * @param columnIndex The column index of the desired entry of the factor matrix.
	 * @return The factor correlation matrix entry with row index rowIndex and column index columnIndex.
	 */
	public double getCorrelationFactor(int rowIndex, int columnIndex);
	
	
	public int getNumberOfRows();
	
	public int getNumberOfColumns();
	
	/**
	 * 
	 * @return numberOfInterCorrelationsRows The row dimension of the intercorrelation matrix passed as constructor parameter.
	 */
	public int getNumberOfInterCorrelationRows();
	
	/**
	 * 
	 * @return numberOfInterCorrelationsColumns The column dimension of the intercorrelation matrix passed as constructor parameter.
	 */
	public int getNumberOfInterCorrelationColumns();
	
}
