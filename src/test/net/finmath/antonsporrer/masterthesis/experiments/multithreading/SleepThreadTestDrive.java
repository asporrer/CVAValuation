package test.net.finmath.antonsporrer.masterthesis.experiments.multithreading;

public class SleepThreadTestDrive {

	public static void main(String[] args) throws InterruptedException {
		
		Thread testThread1 = new Thread(new HelloRunnable());
		Thread testThread2 = new Thread(new HelloRunnable());
		
		testThread1.sleep(4000);
		testThread2.start();
		testThread1.start();
		
	}

}
