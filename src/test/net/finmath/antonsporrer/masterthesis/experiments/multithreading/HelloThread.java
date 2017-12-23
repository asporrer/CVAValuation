package test.net.finmath.antonsporrer.masterthesis.experiments.multithreading;

public class HelloThread extends Thread {

	public void run() {
		System.out.println("Hello from a thread!");
	}
	
	public static void main(String[] args) {
		
		(new HelloThread()).start();

	}

}
