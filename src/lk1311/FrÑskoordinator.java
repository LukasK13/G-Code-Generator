package lk1311;

import java.awt.*;
import java.io.*;
import javax.swing.text.*;

public class Fr‰skoordinator implements Runnable{

	private static final double Pi = 3.14;
	private Fr‰ser Fr‰ser;
	private Fr‰serSimulationsEngine Fr‰serEngine;
	private Thread Fr‰serEngineThread;
	private Thread Fr‰serUpdateThread;

	private int aktuellesN = 0;
	private int[] ZahlenPr¸fAlt;
	private double Xtemp=0, Ytemp=0, Ztemp=0, Atemp=0;
	private double VorschubTemp=0;
	private double DrehzahlTemp=0;
	private boolean SpindelAnTemp=false;
	private int ToolTemp=1;
	private int KorrekturTemp=0; //0=keine Radiuskorrektur 1=links 2=rechts
	private int MaﬂTemp=0; //0=Absolutmaﬂ 1=Relativmaﬂ

	private String[] temp;
	private String[] Split = {"","","","","",""};
	private String[] Code;

	private int SendeVersuch=0;
	private int AbN = 0;
	private int BisN = 0;

	public boolean Pause = false;

	public Fr‰skoordinator(Fr‰ser Fr‰serNeu, Fr‰serSimulationsEngine Fr‰serEngineNeu,Thread Fr‰serEngineThreadNeu, Thread Fr‰serUpdateThreadNeu) {
		Fr‰ser = Fr‰serNeu;
		Fr‰serEngine = Fr‰serEngineNeu;
		Fr‰serEngineThread = Fr‰serEngineThreadNeu;
		Fr‰serUpdateThread = Fr‰serUpdateThreadNeu;
	}

	public void run() { //Fr‰sparameter erfassen und an CNC ¸bermitteln
		Code = Fr‰ser.CodeSplit;
		AbN = Fr‰ser.AbN.getInt();
		BisN = Fr‰ser.BisN.getInt();
		Fr‰ser.N‰chsterBtn.setForeground(Color.red);


		//Fr‰sparameter erfassen
		if (Fr‰ser.AbNChk.isSelected()) {
			VorherigeBefehleErfassen();

			Fr‰ser.Vorschub = VorschubTemp;
			Fr‰ser.Drehzahl = DrehzahlTemp;
			Fr‰ser.SpindelAn = SpindelAnTemp;
			Fr‰ser.Tool = ToolTemp;
			Fr‰ser.Korrektur = KorrekturTemp;
			Fr‰ser.Maﬂ = MaﬂTemp;

			aktuellesN = AbN;
			Fr‰ser.N = AbN;
		} else {
			for (int i=0; i<=3; i++) {

				if (Thread.interrupted()) {	//wurde der Thread unterbrochen?
					break;
				} 

				temp = Code[i].split(" ");

				for (int j=0; j<temp.length; j++) {
					Split[j] = temp[j];
				}

				if (i==0) {
					if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
						if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
							Fr‰ser.Tool=Integer.parseInt(Split[1].substring(1));
						} 
					} 
				}

				if (i==1) {
					if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istDouble(Split[2].substring(1))) {
						if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
							Fr‰ser.Vorschub=Integer.parseInt(Split[1].substring(1));
							Fr‰ser.Drehzahl=Integer.parseInt(Split[2].substring(1));
						} 
					}
				}

				if (i==2) {
					if (Split[1].equals("G40")) {
						Fr‰ser.Korrektur=0;
					} else if (Split[1].equals("G41")) {
						Fr‰ser.Korrektur=1;
					} else if (Split[1].equals("G42")) {
						Fr‰ser.Korrektur=2;
					}
				}

				if (i==3) {
					if (Split[1].equals("G90")) {
						Fr‰ser.Maﬂ=0;
					} else if(Split[1].equals("G91")) {
						Fr‰ser.Maﬂ=1;
					}
				}

				for (int j=0; j<Split.length; j++) {	//String Split leeren
					Split[j] = "";
				}
			}
			aktuellesN = 4;
			Fr‰ser.N = 4;
		}

		//Fr‰sparameter an CNC ¸bermitteln
		RS232_SendenPr¸f(0, 62, Fr‰ser.Vorschub, Fr‰ser.Drehzahl, 0, 0);
		if (Fr‰ser.SpindelAn) {
			RS232_SendenPr¸f(0, 60, 0, 0, 0, 0);
		} else {
			RS232_SendenPr¸f(0, 61, 0, 0, 0, 0);
		}
		RS232_SendenPr¸f(0, 65, Double.parseDouble(Split[1].substring(1)), Double.parseDouble(Fr‰ser.Werkzeugdaten[Fr‰ser.Tool][1]), Double.parseDouble(Fr‰ser.Werkzeugdaten[Fr‰ser.Tool][2]), 0);

		RS232_SendenPr¸f(0, 1, Xtemp, Ytemp, 0, 0);
		RS232_SendenPr¸f(0, 1, 0, 0, Ztemp, 0);
		RS232_SendenPr¸f(0, 1, 0, 0, 0, Atemp);

		if (Fr‰ser.Korrektur == 0) {
			RS232_SendenPr¸f(0, 40, 0, 0, 0, 0);
		} else if (Fr‰ser.Korrektur == 1) {
			RS232_SendenPr¸f(0, 41, 0, 0, 0, 0);
		} else if (Fr‰ser.Korrektur == 2) {
			RS232_SendenPr¸f(0, 42, 0, 0, 0, 0);
		}
		if (Fr‰ser.Maﬂ == 0) {
			RS232_SendenPr¸f(0, 90, 0, 0, 0, 0);
		} else if (Fr‰ser.Maﬂ == 1) {
			RS232_SendenPr¸f(0, 91, 0, 0, 0, 0);
		}

		Fr‰sen();			
	}

	private void Fr‰sen() { //Fr‰sschleife
		while (!Thread.interrupted()) {
			while (!Pause && !Thread.interrupted()) {
				if (Fr‰ser.DatenFehlerhaftFlag) {
					RS232_SendenPr¸f(0, 92, 0, 0, 0, 0);
				}

				if (Fr‰ser.CNCStatusInt == 93) {

					if (Fr‰ser.EinzelschrittFr‰sen) {	//Einzelschrittfr‰sen
						if (Fr‰ser.N‰chsterClicked && Fr‰ser.BisNChk.isSelected() && Fr‰ser.N > BisN) {	//Fr‰svorgang ist bei BisN angekommen, manueller Durchlauf
							Fr‰ser.N‰chsterClicked = false;
							Fr‰ser.N‰chsterBtn.setForeground(Color.red);
							Fr‰ser.N++;
							Fr‰serEngine.BisNZeichnen = Fr‰ser.N;

						} else if (Fr‰ser.BisNChk.isSelected() && Fr‰ser.N <= BisN) {	//Fr‰svorgang ist noch nicht bei BisN angekommen, automatischer Durchlauf
							Fr‰ser.N++;
							Fr‰serEngine.BisNZeichnen = Fr‰ser.N;

						} else if (Fr‰ser.N‰chsterClicked && !Fr‰ser.BisNChk.isSelected()) {	//Fr‰svorgang ohne BisN, manueller Durchlauf
							Fr‰ser.N‰chsterClicked = false;
							Fr‰ser.N‰chsterBtn.setForeground(Color.red);
							Fr‰ser.N++;
							Fr‰serEngine.BisNZeichnen = Fr‰ser.N;

						} else {	//N‰chsterBtn f¸r Click freigeben
							Fr‰ser.N‰chsterBtn.setForeground(Color.green);
						}
					} else {	//automatischer Durchlauf
						Fr‰ser.N++;
						Fr‰serEngine.BisNZeichnen = Fr‰ser.N;
					} 					

					try {	//Codezeile im editor markieren
						Fr‰ser.Editor.select(Fr‰ser.Editor.getLineStartOffset(Fr‰ser.N), Fr‰ser.Editor.getLineEndOffset(Fr‰ser.N));
					} catch (BadLocationException e) {
						System.err.println(getClass() + "Codezeile konnte nicht markiert werden.");
					}

					if (aktuellesN < Fr‰ser.N) {	//Code ausf¸hren
						CodeInterpretieren(Fr‰ser.N);
						aktuellesN = Fr‰ser.N;
						Fr‰ser.Fortschritt = Fr‰ser.N / Code.length;
					}

				} else {	//Fr‰se besch‰ftigt, Thread zur Ressourcenschonung anhalten
					try {
						Thread.currentThread();
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}				
			}			
			try {	//Thread wurde Pausiert, Thread zur Ressourcenschonung anhalten
				Thread.currentThread();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void CodeInterpretieren(int Zeile) {
		temp = Code[Zeile].split(" ");

		for (int j=0; j<temp.length; j++) {
			Split[j] = temp[j];
		}

		if (Split[1].equals("G0") | Split[1].equals("G00")) {
			if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
				RS232_SendenPr¸f(Fr‰ser.N, 0, Double.parseDouble(Split[2].substring(1)), 0, 0, 0);
			} else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
				RS232_SendenPr¸f(Fr‰ser.N, 0, 0, Double.parseDouble(Split[2].substring(1)), 0, 0);
			} else if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
				RS232_SendenPr¸f(Fr‰ser.N, 0, 0, 0, Double.parseDouble(Split[2].substring(1)), 0);
			} else if ((Split[2].startsWith("A") | Split[2].startsWith("a")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
				RS232_SendenPr¸f(Fr‰ser.N, 0, 0, 0, 0, Double.parseDouble(Split[2].substring(1)));				
			} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {	
				RS232_SendenPr¸f(Fr‰ser.N, 0, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), 0, 0);
			} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {	
				RS232_SendenPr¸f(Fr‰ser.N, 0, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), 0, 0);
			} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {	
				RS232_SendenPr¸f(Fr‰ser.N, 0, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), 0, 0);
			} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) &&
					istDouble(Split[3].substring(1)) && (Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[4].substring(1))) {	
				RS232_SendenPr¸f(Fr‰ser.N, 0, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), 0);
			}
		}
		else if (Split[1].equals("G1") | Split[1].equals("G01")) {
			if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
				RS232_SendenPr¸f(Fr‰ser.N, 1, Double.parseDouble(Split[2].substring(1)), 0, 0, 0);
			} else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
				RS232_SendenPr¸f(Fr‰ser.N, 1, 0, Double.parseDouble(Split[2].substring(1)), 0, 0);
			} else if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
				RS232_SendenPr¸f(Fr‰ser.N, 1, 0, 0, Double.parseDouble(Split[2].substring(1)), 0);
			} else if ((Split[2].startsWith("A") | Split[2].startsWith("a")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
				RS232_SendenPr¸f(Fr‰ser.N, 1, 0, 0, 0, Double.parseDouble(Split[2].substring(1)));				
			} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {	
				RS232_SendenPr¸f(Fr‰ser.N, 1, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), 0, 0);
			} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {	
				RS232_SendenPr¸f(Fr‰ser.N, 1, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), 0, 0);
			} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {	
				RS232_SendenPr¸f(Fr‰ser.N, 1, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), 0, 0);
			} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) &&
					istDouble(Split[3].substring(1)) && (Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[4].substring(1))) {	
				RS232_SendenPr¸f(Fr‰ser.N, 1, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), 0);
			}
		}

		else if (Split[1].equals("G2") | Split[1].equals("G02")) {
			if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
					&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
					&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
					&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
				RS232_SendenPr¸f(Fr‰ser.N, 2, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), Double.parseDouble(Split[5].substring(1)));
			} 
		}		

		else if (Split[1].equals("G3") | Split[1].equals("G03")) {
			if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
					&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
					&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
					&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
				RS232_SendenPr¸f(Fr‰ser.N, 3, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), Double.parseDouble(Split[5].substring(1)));
			} 
		}		

		else if (Split[1].equals("G4") | Split[1].equals("G04")) {
			if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
					&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
				RS232_SendenPr¸f(Fr‰ser.N, 4, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), 0);
			} 
		}

		else if (Split[1].equals("G5") | Split[1].equals("G05")) {
			if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
					&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
				RS232_SendenPr¸f(Fr‰ser.N, 5, Double.parseDouble(Split[2].substring(1)), Double.parseDouble(Split[3].substring(1)), Double.parseDouble(Split[4].substring(1)), 0);
			} 
		}

		else if (Split[1].equals("G40")) {
			Fr‰ser.Korrektur=0;
			RS232_SendenPr¸f(Fr‰ser.N, 40, 0, 0, 0, 0);
		}

		else if (Split[1].equals("G41")) {
			Fr‰ser.Korrektur=1;
			RS232_SendenPr¸f(Fr‰ser.N, 41, 0, 0, 0, 0);
		}

		else if (Split[1].equals("G42")) {
			Fr‰ser.Korrektur=2;
			RS232_SendenPr¸f(Fr‰ser.N, 42, 0, 0, 0, 0);
		}

		else if (Split[1].equals("G90")) {
			Fr‰ser.Maﬂ=0;
			RS232_SendenPr¸f(Fr‰ser.N, 90, 0, 0, 0, 0);
		}

		else if (Split[1].equals("G91")) {
			Fr‰ser.Maﬂ=1;
			RS232_SendenPr¸f(Fr‰ser.N, 91, 0, 0, 0, 0);
		}

		else if (Split[1].equals("G72")) {
			if ((Split[2].startsWith("N") | Split[2].startsWith("n")) && istInteger(Split[2].substring(1)) && istInteger(Split[3])) {
				if (Integer.parseInt(Split[2].substring(1))<Fr‰ser.N) {
					if (Fr‰ser.AnzahlWiederholungen <= Integer.parseInt(Split[3])) {
						Fr‰ser.N = Integer.parseInt(Split[2].substring(1));
						Fr‰ser.AnzahlWiederholungen++;
					} else {
						Fr‰ser.AnzahlWiederholungen = 0;
					}
				} 
			}
		}

		else if (Split[1].equals("M00") | Split[1].equals("M0")) {
			RS232_SendenPr¸f(Fr‰ser.N, 63, 0, 0, 0, 0);
			Fr‰ser.NotAusBtn.doClick();			
		}

		else if (Split[1].equals("M02") | Split[1].equals("M2")) {
			RS232_SendenPr¸f(Fr‰ser.N, 64, 0, 0, 0, 0);
			Fr‰serUpdateThread.interrupt();
			Fr‰serEngineThread.interrupt();
			try {	//Warten auf beenden der anderen Threads, damit Ausgabe nicht gelˆscht wird.
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Fr‰ser.CNCStatus.append("Fr‰svorgang durch M02 beendet" + "\n");
			Fr‰ser.AbbruchDialogAnzeigen = false;
			Fr‰ser.Fr‰senStartenBtn.doClick();
		}

		else if (Split[1].equals("M03") | Split[1].equals("M3")) {
			Fr‰ser.SpindelAn=true;
			RS232_SendenPr¸f(Fr‰ser.N, 60, 0, 0, 0, 0);
		}

		else if (Split[1].equals("M05") | Split[1].equals("M5")) {
			Fr‰ser.SpindelAn=false;
			RS232_SendenPr¸f(Fr‰ser.N, 61, 0, 0, 0, 0);
		}

		else if ((Split[1].startsWith("S") | Split[1].startsWith("s")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
			if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 20000) {
				Fr‰ser.Drehzahl=Double.parseDouble(Split[1].substring(1));
				RS232_SendenPr¸f(Fr‰ser.N, 62, 0, Double.parseDouble(Split[1].substring(1)), 0, 0);
			} 
		}

		else if ((Split[1].startsWith("F") | Split[1].startsWith("S")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
			Fr‰ser.Vorschub=Double.parseDouble(Split[1].substring(1));
			RS232_SendenPr¸f(Fr‰ser.N, 62, Double.parseDouble(Split[1].substring(1)), 0, 0, 0);
		}

		else if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istInteger(Split[2].substring(1))) {
			if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
				Fr‰ser.Vorschub=Double.parseDouble(Split[1].substring(1));
				Fr‰ser.Drehzahl=Double.parseDouble(Split[2].substring(1));
				RS232_SendenPr¸f(Fr‰ser.N, 62, Double.parseDouble(Split[1].substring(1)), Double.parseDouble(Split[2].substring(1)), 0, 0);
			} 
		}

		else if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
			if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
				Fr‰ser.Tool=Integer.parseInt(Split[1].substring(1));
				RS232_SendenPr¸f(Fr‰ser.N, 65, Double.parseDouble(Split[1].substring(1)), Double.parseDouble(Fr‰ser.Werkzeugdaten[Fr‰ser.Tool][1]), Double.parseDouble(Fr‰ser.Werkzeugdaten[Fr‰ser.Tool][2]), 0);
			} 
		}
	}

	private void VorherigeBefehleErfassen() {
		for (int i=0; i<AbN; i++) {

			if (Thread.interrupted()) {	//wurde der Thread unterbrochen?
				break;
			} 

			temp = Code[i].split(" ");

			for (int j=0; j<temp.length; j++) {
				Split[j] = temp[j];
			}

			if (i==0) {
				if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
					if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
						ToolTemp=Integer.parseInt(Split[1].substring(1));
					} 
				} 
			}

			if (i==1) {
				if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istDouble(Split[2].substring(1))) {
					if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
						VorschubTemp=Integer.parseInt(Split[1].substring(1));
						DrehzahlTemp=Integer.parseInt(Split[2].substring(1));
					} 
				}
			}

			if (i==2) {
				if (Split[1].equals("G40")) {
					KorrekturTemp=0;
				} else if (Split[1].equals("G41")) {
					KorrekturTemp=1;
				} else if (Split[1].equals("G42")) {
					KorrekturTemp=2;
				}
			}

			if (i==3) {
				if (Split[1].equals("G90")) {
					MaﬂTemp=0;
				} else if(Split[1].equals("G91")) {
					MaﬂTemp=1;
				}
			}

			if (i>3) {
				if (Split[1].equals("G0") | Split[1].equals("G00") | Split[1].equals("G1") | Split[1].equals("G01")) {
					if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						if (MaﬂTemp==0) {
							Xtemp = Double.parseDouble(Split[2].substring(1));
						} else {
							Xtemp += Double.parseDouble(Split[2].substring(1));
						}
					} else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						if (MaﬂTemp==0) {
							Ytemp = Double.parseDouble(Split[2].substring(1));
						} else {
							Ytemp += Double.parseDouble(Split[2].substring(1));
						}
					} else if ((Split[2].startsWith("Z") | Split[2].startsWith("z")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						if (MaﬂTemp==0) {
							Ztemp = Double.parseDouble(Split[2].substring(1));
						} else {
							Ztemp += Double.parseDouble(Split[2].substring(1));
						}
					} else if ((Split[2].startsWith("A") | Split[2].startsWith("a")) && istDouble(Split[2].substring(1)) && Split[3].equals("") && Split[4].equals("")) {
						if (MaﬂTemp==0) {
							Atemp = Double.parseDouble(Split[2].substring(1));
						} else {
							Atemp += Double.parseDouble(Split[2].substring(1));
						}
					} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						if (MaﬂTemp==0) {
							Xtemp = Double.parseDouble(Split[2].substring(1));
							Ytemp = Double.parseDouble(Split[3].substring(1));
						} else {
							Xtemp += Double.parseDouble(Split[2].substring(1));
							Ytemp += Double.parseDouble(Split[3].substring(1));
						}
					} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						if (MaﬂTemp==0) {
							Xtemp = Double.parseDouble(Split[2].substring(1));
							Ztemp = Double.parseDouble(Split[3].substring(1));
						} else {
							Xtemp += Double.parseDouble(Split[2].substring(1));
							Ztemp += Double.parseDouble(Split[3].substring(1));
						}
					} else if ((Split[2].startsWith("Y") | Split[2].startsWith("y")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Z") | Split[3].startsWith("z")) && istDouble(Split[3].substring(1)) && Split[4].equals("")) {
						if (MaﬂTemp==0) {
							Ytemp = Double.parseDouble(Split[2].substring(1));
							Ztemp = Double.parseDouble(Split[3].substring(1));
						} else {
							Ytemp += Double.parseDouble(Split[2].substring(1));
							Ztemp += Double.parseDouble(Split[3].substring(1));
						}
					} else if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("Y") | Split[3].startsWith("y")) &&
							istDouble(Split[3].substring(1)) && (Split[4].startsWith("Z") | Split[4].startsWith("z")) && istDouble(Split[4].substring(1))) {
						if (MaﬂTemp==0) {
							Xtemp = Double.parseDouble(Split[2].substring(1));
							Ytemp = Double.parseDouble(Split[3].substring(1));
							Ztemp = Double.parseDouble(Split[3].substring(1));
						} else {
							Xtemp += Double.parseDouble(Split[2].substring(1));
							Ytemp += Double.parseDouble(Split[3].substring(1));
							Ztemp += Double.parseDouble(Split[3].substring(1));
						}
					}
				}				

				else if (Split[1].equals("G2") | Split[1].equals("G02")) {
					if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
							&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
							&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
							&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
						if (MaﬂTemp==1) {
							Xtemp += Double.parseDouble(Split[2].substring(1));
							Ytemp += Double.parseDouble(Split[3].substring(1));
						} 
					} 
				}		

				else if (Split[1].equals("G3") | Split[1].equals("G03")) {
					if ((Split[2].startsWith("X") | Split[2].startsWith("x")) && istDouble(Split[2].substring(1))
							&& (Split[3].startsWith("Y") | Split[3].startsWith("y")) && istDouble(Split[3].substring(1))
							&& (Split[4].startsWith("I") | Split[4].startsWith("i")) && istDouble(Split[4].substring(1)) 
							&& (Split[5].startsWith("J") | Split[5].startsWith("j")) && istDouble(Split[5].substring(1))) {
						if (MaﬂTemp==1) {
							Xtemp += Double.parseDouble(Split[2].substring(1));
							Ytemp += Double.parseDouble(Split[3].substring(1));
						} 
					} 
				}		

				else if (Split[1].equals("G4") | Split[1].equals("G04")) {
					if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
							&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
						if (MaﬂTemp == 1) {
							double radius, alpha, beta;

							if (Double.parseDouble(Split[3].substring(1)) <= 0) {
								radius = Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
										Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
								alpha = Math.acos(-Double.parseDouble(Split[2].substring(1)) / radius); 
							} else {
								radius = Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
										Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
								alpha = Math.acos(-Double.parseDouble(Split[2].substring(1)) / radius) + Math.PI; 
							}
							beta = alpha - (Double.parseDouble(Split[4].substring(1)) / 360 * (Math.PI * 2));
							if (beta < 0) {
								beta = Math.PI * 2 + beta;
							}		
							while (beta > 2 * Math.PI){
								beta = beta - 2 * Math.PI;
							}		

							Xtemp += Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1));
							Ytemp += Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1));
						}
					} 
				}

				else if (Split[1].equals("G5") | Split[1].equals("G05")) {
					if ((Split[2].startsWith("I") | Split[2].startsWith("i")) && istDouble(Split[2].substring(1)) && (Split[3].startsWith("J") | Split[3].startsWith("j"))
							&& istDouble(Split[3].substring(1)) && (Split[4].startsWith("W") | Split[4].startsWith("w")) && istDouble(Split[4].substring(1))) {
						if (MaﬂTemp == 1) {
							double radius, alpha, beta;

							if (Double.parseDouble(Split[3].substring(1)) <= 0) {
								radius = Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
										Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
								alpha = Math.acos(-Double.parseDouble(Split[2].substring(1)) / radius); 
							} else {
								radius = Math.sqrt(Double.parseDouble(Split[2].substring(1)) * Double.parseDouble(Split[2].substring(1)) + 
										Double.parseDouble(Split[3].substring(1)) * Double.parseDouble(Split[3].substring(1)));
								alpha = Math.acos(-Double.parseDouble(Split[2].substring(1)) / radius) + Math.PI; 
							}
							beta = alpha - (Double.parseDouble(Split[4].substring(1)) / 360 * (Math.PI * 2));
							if (beta < 0) {
								beta = Math.PI * 2 + beta;
							}		
							while (beta > 2 * Math.PI){
								beta = beta - 2 * Math.PI;
							}		

							Xtemp += Math.cos(beta) * radius + Double.parseDouble(Split[2].substring(1));
							Ytemp += Math.sin(beta) * radius + Double.parseDouble(Split[3].substring(1));
						}
					} 
				}

				else if (Split[1].equals("G40")) {
					KorrekturTemp=0;
				}

				else if (Split[1].equals("G41")) {
					KorrekturTemp=1;
				}

				else if (Split[1].equals("G42")) {
					KorrekturTemp=2;
				}

				else if (Split[1].equals("G90")) {
					MaﬂTemp=0;
				}

				else if (Split[1].equals("G91")) {
					MaﬂTemp=1;
				}

				else if (Split[1].equals("M03") | Split[1].equals("M3")) {
					SpindelAnTemp=true;
				}

				else if (Split[1].equals("M05") | Split[1].equals("M5")) {
					SpindelAnTemp=false;
				}

				else if ((Split[1].startsWith("S") | Split[1].startsWith("s")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
					if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 20000) {
						DrehzahlTemp=Double.parseDouble(Split[1].substring(1));
					} 
				}

				else if ((Split[1].startsWith("F") | Split[1].startsWith("S")) && istDouble(Split[1].substring(1)) && Split[2].equals("")) {
					VorschubTemp=Double.parseDouble(Split[1].substring(1));
				}

				else if ((Split[1].startsWith("F") | Split[1].startsWith("f")) && istDouble(Split[1].substring(1)) && (Split[2].startsWith("S") | Split[2].startsWith("s")) && istInteger(Split[2].substring(1))) {
					if (1 <= Integer.parseInt(Split[2].substring(1)) && Integer.parseInt(Split[2].substring(1)) <= 20000) {
						VorschubTemp=Double.parseDouble(Split[1].substring(1));
						DrehzahlTemp=Double.parseDouble(Split[2].substring(1));
					} 
				}

				else if ((Split[1].startsWith("T") | Split[1].startsWith("t")) && istInteger(Split[1].substring(1))) {
					if (1 <= Integer.parseInt(Split[1].substring(1)) && Integer.parseInt(Split[1].substring(1)) <= 5) {
						ToolTemp=Integer.parseInt(Split[1].substring(1));
					} 
				}
			}

			for (int j=0; j<Split.length; j++) {	//String Split leeren
				Split[j] = "";
			}	
		}
	}

	private boolean istDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean istInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}

	private void RS232_SendenPr¸f(int nInt, int gInt, double xDbl, double yDbl, double iDbl, double jDbl) {
		boolean Sendevorgang = true;
		int AnzahlWarten = 0;

		while (Sendevorgang && !Thread.interrupted()) {
			if (Fr‰ser.CNCStatusInt == 90 || Fr‰ser.CNCStatusInt == 93) {
				int nInt1=0, nInt2=0, nInt3=0, xInt1=0, xInt2=0, xInt3=0, yInt1=0, yInt2=0, yInt3=0, iInt1=0, iInt2=0, iInt3=0, jInt1=0, jInt2=0, jInt3=0;
				boolean ZahlenOK = true;
				boolean Erfolg = false;

				if (nInt <=999999 && nInt > 0) {
					nInt1 = (int) (nInt / 10000);
					nInt2 = (int) ((nInt - nInt1 * 10000) / 100);
					nInt3 = nInt - nInt1 * 10000 - nInt2 * 100;
				} else {
					System.out.println("N zu groﬂ");
					ZahlenOK = false;
				}

				if (gInt > 255 && gInt >= 0) {
					System.out.println("G zu groﬂ");
					ZahlenOK = false;
				}

				if (10000.0 > xDbl && xDbl>= 0.0) {
					xInt1 = (int) (xDbl / 100.0);
					xInt2 = (int) (xDbl - xInt1 * 100.0);
					xInt3 = (int) (xDbl * 100.0 - xInt1 * 10000.0 - xInt2 * 100.0);
				} else if (0.0 > xDbl && xDbl > -10000.0) {
					xDbl = -xDbl;
					xInt1 = (int) (xDbl / 100.0);
					xInt2 = (int) (xDbl - xInt1 * 100.0);
					xInt3 = (int) (xDbl * 100.0 - xInt1 * 10000.0 - xInt2 * 100.0);
					xInt1 = -xInt1;
				} else {
					System.out.println("X zu groﬂ / klein");
					ZahlenOK = false;
				}

				if (10000.0 > yDbl && yDbl>= 0.0) {
					yInt1 = (int) (yDbl / 100.0);
					yInt2 = (int) (yDbl - yInt1 * 100.0);
					yInt3 = (int) (yDbl * 100.0 - yInt1 * 10000.0 - yInt2 * 100.0);
				} else if (0.0 > yDbl && yDbl > -10000.0) {
					yDbl = -yDbl;
					yInt1 = (int) (yDbl / 100.0);
					yInt2 = (int) (yDbl - yInt1 * 100.0);
					yInt3 = (int) (yDbl * 100.0 - yInt1 * 10000.0 - yInt2 * 100.0);
					yInt1 = -yInt1;
				} else {
					System.out.println("Y zu groﬂ / klein");
					ZahlenOK = false;
				}

				if (10000.0 > iDbl && iDbl>= 0.0) {
					iInt1 = (int) (iDbl / 100.0);
					iInt2 = (int) (iDbl - iInt1 * 100.0);
					iInt3 = (int) (iDbl * 100.0 - iInt1 * 10000.0 - iInt2 * 100.0);
				} else if (0.0 > iDbl && iDbl > -10000.0) {
					iDbl = -iDbl;
					iInt1 = (int) (iDbl / 100.0);
					iInt2 = (int) (iDbl - iInt1 * 100.0);
					iInt3 = (int) (iDbl * 100.0 - iInt1 * 10000.0 - iInt2 * 100.0);
					iInt1 = -iInt1;
				} else {
					System.out.println("I zu groﬂ / klein");
					ZahlenOK = false;
				}

				if (10000.0 > jDbl && jDbl>= 0.0) {
					jInt1 = (int) (jDbl / 100.0);
					jInt2 = (int) (jDbl - jInt1 * 100.0);
					jInt3 = (int) (jDbl * 100.0 - jInt1 * 10000.0 - jInt2 * 100.0);
				} else if (0.0 > jDbl && jDbl > -10000.0) {
					jDbl = -jDbl;
					jInt1 = (int) (jDbl / 100.0);
					jInt2 = (int) (jDbl - jInt1 * 100.0);
					jInt3 = (int) (jDbl * 100.0 - jInt1 * 10000.0 - jInt2 * 100.0);
					jInt1 = -jInt1;
				} else {
					System.out.println("J zu groﬂ / klein");
					ZahlenOK = false;
				}

				if (ZahlenOK) {
					OutputStream Ausgangsdaten = null;
					Erfolg = true;

					int[] Zahlen = {nInt1, nInt2, nInt3, gInt, xInt1, xInt2, xInt3, yInt1, yInt2, yInt3, iInt1, iInt2, iInt3, jInt1, jInt2, jInt3};

					int [] ZahlenPr¸f = Pr¸fsumme(Zahlen);

					try {
						Ausgangsdaten = Fr‰ser.SeriellerPort.getOutputStream();
					} catch (IOException e) {
						Erfolg = false;
						System.err.println(getClass() + "OutputStream konnte nicht geˆffnet werden.");
					}
					try {
						for (int i=0; i<ZahlenPr¸f.length; i++) {
							Ausgangsdaten.write(ZahlenPr¸f[i]);
						}
					} catch (IOException e) {
						Erfolg = false;
						System.err.println(getClass() + "Fehler beim Senden");
					}
					try {
						Ausgangsdaten.flush();
					} catch (IOException e) {
						System.err.println(getClass() + "OutputStream konnte nicht geflusht werden.");
					}

					if(Erfolg) {
						Fr‰ser.CNCStatusInt = 91;
						ZahlenPr¸fAlt = ZahlenPr¸f;
					}
					Sendevorgang = false;
				}		
			} else if (Fr‰ser.CNCStatusInt == 92) {
				if (SendeVersuch == 0) {
					boolean Erfolg = false;
					OutputStream Ausgangsdaten = null;

					try {
						Ausgangsdaten = Fr‰ser.SeriellerPort.getOutputStream();
					} catch (IOException e) {
						Erfolg = false;
						System.err.println(getClass() + "OutputStream konnte nicht geˆffnet werden.");
					}
					try {
						for (int i=0; i<ZahlenPr¸fAlt.length; i++) {
							Ausgangsdaten.write(ZahlenPr¸fAlt[i]);
						}
					} catch (IOException e) {
						Erfolg = false;
						System.err.println(getClass() + "Fehler beim Senden");
					}
					try {
						Ausgangsdaten.flush();
					} catch (IOException e) {
						System.err.println(getClass() + "OutputStream konnte nicht geflusht werden.");
					}
					if (Erfolg) {
						SendeVersuch = 1;
						Fr‰ser.CNCStatusInt = 91;
					}
				} else {
					System.err.println(getClass() + "Zweiter Sendeversuch erneut fehlerhaft");
				}

			} else if (Fr‰ser.CNCStatusInt == 91) {
				if (AnzahlWarten <= 10) {
					try {
						Thread.currentThread();
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					AnzahlWarten++;
				} else {
					System.err.println(getClass() + "MF70 kann auch nach 20 Versuchen keine Daten entgegennehmen");
					Sendevorgang = false;
				}				
			}
		}		
	}

	private int[] Pr¸fsumme (int[] Zahlen) {
		double Pr¸fsummeDbl = 0;
		for (int i=0; i<16; i++) {
			Pr¸fsummeDbl = Pr¸fsummeDbl + Zahlen[i] * Math.pow(Pi, (i / 10.0) - 1.0);
		}

		int [] ZahlenPr¸f = new int[17];
		for (int i =0; i<Zahlen.length; i++) {
			ZahlenPr¸f[i] = Zahlen[i];
		}
		ZahlenPr¸f[16] = (int) (Pr¸fsummeDbl / 6.0 - 128.0);
		if (ZahlenPr¸f[16]<0) {
			ZahlenPr¸f[16] = -ZahlenPr¸f[16];
		}
		return ZahlenPr¸f;
	}
}
