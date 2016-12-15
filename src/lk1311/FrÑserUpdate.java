package lk1311;

public class Fr�serUpdate implements Runnable{
	
	private Fr�ser Fr�ser;
	
	public Fr�serUpdate(Fr�ser Fr�serNeu) {
		Fr�ser = Fr�serNeu;
	}
	
	public void run() {		
		Thread.currentThread().setName("Fr�serUpdateThread");
		while (!Thread.interrupted()) {
			if (Fr�ser.Fortschritt < 100) {
				Fr�ser.FortschrittBar.setValue(Fr�ser.Fortschritt);
			} else {
				Fr�ser.FortschrittBar.setValue(Fr�ser.Fortschritt);
				Thread.currentThread().interrupt();
				break;				
			}
			
			Fr�ser.xSegment.setValue(Fr�ser.XPos);
			Fr�ser.ySegment.setValue(Fr�ser.YPos);
			Fr�ser.zSegment.setValue(Fr�ser.ZPos);
			
			Fr�ser.CNCStatus.setText("");
			Fr�ser.CNCStatus.append("Position:   X:" + Fr�ser.XPos + "mm   Y:" + Fr�ser.YPos + "mm   Z:" + Fr�ser.ZPos + "mm   +A:" + Fr�ser.APos + "�" + "\n");
			Fr�ser.CNCStatus.append("Werkzeug: T" + Fr�ser.Tool + "   Durchmesser: " + Fr�ser.Werkzeugdaten[Fr�ser.Tool-1][1] + "mm   L�nge: " + Fr�ser.Werkzeugdaten[Fr�ser.Tool-1][2] + "mm" + "\n");
			Fr�ser.CNCStatus.append("Vorschub F: " + Fr�ser.Vorschub + "mm/min   Drehzahl: " + Fr�ser.Drehzahl + "U/min" + "\n");
			if (Fr�ser.Korrektur==0) {
				Fr�ser.CNCStatus.append("Werkzeugkorrektur: keine (G40)" + "\n");
			} else if (Fr�ser.Korrektur==1) {
				Fr�ser.CNCStatus.append("Werkzeugkorrektur: links (G41)" + "\n");
			} else if (Fr�ser.Korrektur==2) {
				Fr�ser.CNCStatus.append("Werkzeugkorrektur: rechts (G42)" + "\n");
			}
			
			if (Fr�ser.Ma�==0) {
				Fr�ser.CNCStatus.append("Ma�: Absolutma� (G90)" + "\n");
			} else if (Fr�ser.Ma�==1) {
				Fr�ser.CNCStatus.append("Ma�: Relativma� (G91)" + "\n");
			}
			
			if (Fr�ser.SpindelAn) {
				Fr�ser.CNCStatus.append("Spindel ist an" + "\n");
			} else {
				Fr�ser.CNCStatus.append("Spindel ist aus" + "\n");
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}

