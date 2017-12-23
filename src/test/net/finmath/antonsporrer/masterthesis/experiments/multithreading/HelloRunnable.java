package test.net.finmath.antonsporrer.masterthesis.experiments.multithreading;

public class HelloRunnable implements Runnable {

	public void run() {
		System.out.println("Hello from a thread!");
	}
	
	public static void main(String[] args) {
		
		(new Thread(new HelloRunnable())).start();
		
		
	}

}
