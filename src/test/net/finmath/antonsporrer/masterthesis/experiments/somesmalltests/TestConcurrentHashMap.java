package test.net.finmath.antonsporrer.masterthesis.experiments.somesmalltests;

import java.util.concurrent.ConcurrentHashMap;

public class TestConcurrentHashMap {

	public static void main(String[] args) {
		
		// Testing if a copy of the Double is returned. Test not successful.
		ConcurrentHashMap<Integer, Double> concurrentHashMap = new ConcurrentHashMap<Integer, Double>();
		concurrentHashMap.put(1, 1.333);
		Double receivedDouble = concurrentHashMap.get(1);
		System.out.println(receivedDouble);
		receivedDouble = new Double(1.0);
		System.out.println(concurrentHashMap.get(1));
		

	}

}
