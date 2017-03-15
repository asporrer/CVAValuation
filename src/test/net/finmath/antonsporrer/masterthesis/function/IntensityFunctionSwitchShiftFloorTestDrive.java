package test.net.finmath.antonsporrer.masterthesis.function;

import net.finmath.montecarlo.RandomVariable;
import main.net.finmath.antonsporrer.masterthesis.function.IntensityFunctionSwitchShiftFloor;

public class IntensityFunctionSwitchShiftFloorTestDrive {

	public static void main(String[] args) {
		
		IntensityFunctionSwitchShiftFloor intensityFunctionSwitchShiftFloor = new IntensityFunctionSwitchShiftFloor(1);
		System.out.println(intensityFunctionSwitchShiftFloor.getValue(new RandomVariable(1.1)));
		
	}
}