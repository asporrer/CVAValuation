package test.net.finmath.antonsporrer.masterthesis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.net.finmath.antonsporrer.masterthesis.function.KahanSummationTest;
import test.net.finmath.antonsporrer.masterthesis.function.statisticalfunctions.ArithmeticMeanTest;
import test.net.finmath.antonsporrer.masterthesis.function.statisticalfunctions.EmpiricalVarianceTest;
import test.net.finmath.antonsporrer.masterthesis.function.statisticalfunctions.GetDeviationFromMeanUT;
import test.net.finmath.antonsporrer.masterthesis.function.statisticalfunctions.GetRelativeDeviationsWRTMeanUT;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.ConstrainedWorstCaseWWRUT;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.convergencetests.ConvergenceTestGetArithmeticMeansUT;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.convergencetests.GetIncreasingSampleSizeMeanArrayUT;
import test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.npvanddefaultsimulation.AbstractNPVAndDefaultSimulation.PlugInProductProcessUT;


@RunWith(Suite.class)

@Suite.SuiteClasses({
	KahanSummationTest.class,
	ArithmeticMeanTest.class,
	EmpiricalVarianceTest.class,
	ConvergenceTestGetArithmeticMeansUT.class,
	GetDeviationFromMeanUT.class,
	GetRelativeDeviationsWRTMeanUT.class,
	GetIncreasingSampleSizeMeanArrayUT.class, 
	PlugInProductProcessUT.class,
	ConstrainedWorstCaseWWRUT.class
})

public class JUnitTestSuite {   
}  	