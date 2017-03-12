package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

public abstract class AbstractCVA implements CVAInterface {

	private double lossGivenDefault;
	
	public AbstractCVA(double lossGivenDefault) {
		this.lossGivenDefault = lossGivenDefault;
	}
	
	public double getLGD() {
		return this.lossGivenDefault;
	}
	
}
