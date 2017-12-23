package test.net.finmath.antonsporrer.masterthesis.experiments.somesmalltests;

import java.util.Map;
import java.util.TreeMap;

public class TestDriveTreeMap {

	public static void main(String[] args) {
		
		TreeMap<Integer, Double> testTreeMap = new TreeMap<Integer, Double>();
		testTreeMap.put(1, 2.0);
		testTreeMap.put(3, 6.0);
		testTreeMap.put(2, 4.0);
		
		for(Map.Entry<Integer, Double> entry: testTreeMap.entrySet()){
			
			System.out.println("The key: " + entry.getKey() + " the value: " + entry.getValue());
			
		}

	}

}
