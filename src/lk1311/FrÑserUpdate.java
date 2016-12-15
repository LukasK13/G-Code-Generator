package lk1311;

public class FräserUpdate implements Runnable{
	
	private Fräser Fräser;
	
	public FräserUpdate(Fräser FräserNeu) {
		Fräser = FräserNeu;
	}
	
	public void run() {		
		Thread.currentThread().setName("FräserUpdateThread");
		while (!Thread.interrupted()) {
			if (Fräser.Fortschritt < 100) {
				Fräser.FortschrittBar.setValue(Fräser.Fortschritt);
			} else {
				Fräser.FortschrittBar.setValue(Fräser.Fortschritt);
				Thread.currentThread().interrupt();
				break;				
			}
			
			Fräser.xSegment.setValue(Fräser.XPos);
			Fräser.ySegment.setValue(Fräser.YPos);
			Fräser.zSegment.setValue(Fräser.ZPos);
			
			Fräser.CNCStatus.setText("");
			Fräser.CNCStatus.append("Position:   X:" + Fräser.XPos + "mm   Y:" + Fräser.YPos + "mm   Z:" + Fräser.ZPos + "mm   +A:" + Fräser.APos + "°" + "\n");
			Fräser.CNCStatus.append("Werkzeug: T" + Fräser.Tool + "   Durchmesser: " + Fräser.Werkzeugdaten[Fräser.Tool-1][1] + "mm   Länge: " + Fräser.Werkzeugdaten[Fräser.Tool-1][2] + "mm" + "\n");
			Fräser.CNCStatus.append("Vorschub F: " + Fräser.Vorschub + "mm/min   Drehzahl: " + Fräser.Drehzahl + "U/min" + "\n");
			if (Fräser.Korrektur==0) {
				Fräser.CNCStatus.append("Werkzeugkorrektur: keine (G40)" + "\n");
			} else if (Fräser.Korrektur==1) {
				Fräser.CNCStatus.append("Werkzeugkorrektur: links (G41)" + "\n");
			} else if (Fräser.Korrektur==2) {
				Fräser.CNCStatus.append("Werkzeugkorrektur: rechts (G42)" + "\n");
			}
			
			if (Fräser.Maß==0) {
				Fräser.CNCStatus.append("Maß: Absolutmaß (G90)" + "\n");
			} else if (Fräser.Maß==1) {
				Fräser.CNCStatus.append("Maß: Relativmaß (G91)" + "\n");
			}
			
			if (Fräser.SpindelAn) {
				Fräser.CNCStatus.append("Spindel ist an" + "\n");
			} else {
				Fräser.CNCStatus.append("Spindel ist aus" + "\n");
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}

