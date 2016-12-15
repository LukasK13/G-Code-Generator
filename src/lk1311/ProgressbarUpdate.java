package lk1311;

import javax.swing.*;

public class ProgressbarUpdate implements Runnable{
	
	public JProgressBar FortschrittBar;
	public int Fortschritt = 0;
	
	public void run() {
		Thread.currentThread().setName("PbUpdateThread");
		while (!Thread.interrupted()) {
			if (Fortschritt < 100) {
				FortschrittBar.setValue(Fortschritt);
			} else {
				FortschrittBar.setValue(Fortschritt);
				Thread.currentThread().interrupt();
				break;				
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				//e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}
}
