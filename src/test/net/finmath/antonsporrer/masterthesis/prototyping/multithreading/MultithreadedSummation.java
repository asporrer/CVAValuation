package test.net.finmath.antonsporrer.masterthesis.prototyping.multithreading;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultithreadedSummation {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		 
		 ExecutorService pool = Executors.newFixedThreadPool(3);
		 
		 Set<Future<Double>> summationResults = new HashSet<Future<Double>>();
		 
		 for(int i = 1; i < 6; i++) {
			 final double summand1 = i;
			 final double summand2 = 2*i;
			 
			 Future<Double> result = pool.submit( new Callable<Double>() {
				 public Double call() {
					 return new Double(summand1 + summand2);
				 }
			 } );
			 
			 summationResults.add(result);
			 
		 }
		 
		 Double sum = new Double(0.0);
		 
		 for(Future<Double> entry : summationResults) {
			 sum += entry.get();
		 }

		 System.out.println(sum);
		 
	}

}
