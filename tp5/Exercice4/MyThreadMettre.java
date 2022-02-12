package package3;

import java.util.concurrent.LinkedBlockingDeque;


public class MyThreadMettre extends Thread{
	public ThreadID tID;
	private boolean etat = false;
	private LinkedBlockingDeque<Integer> file;
	
	public boolean nonVide() {
		return etat;
	}
	
	public MyThreadMettre(LinkedBlockingDeque<Integer> file) {
		tID = new ThreadID();
		etat = true;
		this.file = file;
	}
		
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			try {
				System.out.println("La thread " +tID.get() + " met    " + i);
				file.put(i);
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.out.println("Erreur !!!"); 
			}
		}
		etat = false; 
		}
}