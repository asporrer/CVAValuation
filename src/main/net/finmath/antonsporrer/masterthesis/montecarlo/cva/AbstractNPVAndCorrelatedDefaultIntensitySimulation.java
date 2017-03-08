package main.net.finmath.antonsporrer.masterthesis.montecarlo.cva;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.AbstractIntensityModel;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intensitymodel.IntensityModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.intermodelbmcorrelation.CorrelationInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ConditionalValueProductInterface;

public abstract class AbstractNPVAndCorrelatedDefaultIntensitySimulation extends AbstractNPVAndDefaultIntensitySimulation{

	IntensityModelInterface intensityModel;

	CorrelationInterface underlyingIntensityCorrelation;
	
	// TODO do implement correlation of B.m. here. Such that there is no duplicate code.
	
}
