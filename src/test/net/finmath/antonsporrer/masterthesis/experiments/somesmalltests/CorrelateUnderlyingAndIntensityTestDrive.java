package test.net.finmath.antonsporrer.masterthesis.experiments.somesmalltests;

import test.net.finmath.antonsporrer.masterthesis.experiments.proofofconcept.CVA;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.HullWhiteCreationHelper;
import net.finmath.montecarlo.interestrate.HullWhiteModel;
import net.finmath.montecarlo.model.AbstractModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.CIRModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.Correlation;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;

public class CorrelateUnderlyingAndIntensityTestDrive {

	public static void main(String[] args) {

		HullWhiteCreationHelper hullWhiteCreationHelper = new HullWhiteCreationHelper();
		AbstractModelInterface hullWhiteModel = hullWhiteCreationHelper.createHullWhiteModel(0, 20, 0.5, 100);
		
		AbstractModelInterface cirModel = new CIRModel(0, 1, 0.03, 0.12);
		
		double[][] interCorrelations = new double[2][1];
		
		interCorrelations[0] = new double[] {0.7};
		interCorrelations[1] = new double[] {0.7};
		
		CorrelationInterface correlation = new Correlation(interCorrelations);
		
		CVA testCVA = new CVA(hullWhiteModel, cirModel, correlation,50, 3141);
		
	}

}
