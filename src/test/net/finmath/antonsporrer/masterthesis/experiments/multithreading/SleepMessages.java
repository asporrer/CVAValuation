package test.net.finmath.antonsporrer.masterthesis.experiments.multithreading;

public class SleepMessages {

	public static void main(String[] args) throws InterruptedException {
		
		String importantInfo[] = {
				"Mares eat cats",
				"Does eat oats",
				"Little lambs eat ivy",
				"A kid will eat ivy too"
		};

		for(int i = 0; i < importantInfo.length; i++) {
			// Pause for 4 seconds
			Thread.sleep(4000);
			// Print a message
			System.out.println(importantInfo[i]);
		}
		
	}

}
