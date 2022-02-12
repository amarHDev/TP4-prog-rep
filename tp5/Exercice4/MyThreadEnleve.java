package package3;

import java.util.concurrent.LinkedBlockingDeque;


public class MyThreadEnleve extends Thread {
	public ThreadID tID;
	private MyThreadMettre ajoutF;
	private LinkedBlockingDeque<Integer> file;
	
	
	public MyThreadEnleve(MyThreadMettre ajoutF,LinkedBlockingDeque<Integer> file) {
		tID = new ThreadID();
		this.ajoutF = ajoutF;
		this.file = file;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		while (ajoutF.nonVide()) {
			try {
				System.out.println("La thread " +tID.get() + " enlève "+ file.take());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("Erreur !!!");
			}
		}
		System.out.println(file);
	}
}