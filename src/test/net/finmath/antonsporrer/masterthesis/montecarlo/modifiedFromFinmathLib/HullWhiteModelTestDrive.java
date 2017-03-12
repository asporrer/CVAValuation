package test.net.finmath.antonsporrer.masterthesis.montecarlo.modifiedFromFinmathLib;

import net.finmath.exception.CalculationException;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;
import main.net.finmath.antonsporrer.masterthesis.modifiedFromFinmathLib.HullWhiteModel;

public class HullWhiteModelTestDrive {

	public static void main(String[] args) throws CalculationException {
		
		HullWhiteModel hullWhiteModel = HullWhiteCreationHelper.createHullWhiteModel(0.0, 20, 0.5);

		System.out.println( hullWhiteModel.getLIBOR(0, 19) );
		
	}

}
