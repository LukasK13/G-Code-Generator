package lk1311;

import gnu.io.SerialPort;

import java.io.BufferedInputStream;
import java.io.IOException;

public class RS232_Empfangen implements Runnable{
	
	private static final double Pi = 3.14;
	private SerialPort SeriellerPort;
	private int N=0, G=0;
	private double X=0, Y=0, I=0, J=0;
	private Fr�ser Fr�ser;
	private BufferedInputStream Eingangsdaten = null;
	private byte[] temp = new byte[17];
	private int[] tempInt = new int[17];
	private int n=0;
	private double Pr�fsummeDbl = 0;
	private int Pr�fsummeInt = 0;
	
	public RS232_Empfangen(Fr�ser Fr�serNeu, SerialPort SeriellerPortNeu) {
		Fr�ser = Fr�serNeu;
		SeriellerPort = SeriellerPortNeu;
	}
	
	public void run() {
		try {	//InputStream �ffnen
	    	Eingangsdaten = new BufferedInputStream(SeriellerPort.getInputStream(), 30);
	    } catch (IOException e) {
	    	System.err.println(e.getClass() + " InputStream konnte nicht ge�ffnet werden.");
	    	e.printStackTrace();
	    }
		
		if (Eingangsdaten != null) {	
	    	try {
				if (Eingangsdaten.available() != 0) { 
					
					try {	//warten auf alle Bytes
						Thread.currentThread();
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
					}
					
					//Anzahl der neuen Bytes feststellen
					n=0;
					try {
						n = Eingangsdaten.available();
					} catch (IOException e) {
						System.err.println(e.getClass() + " Anzahl der neuen Daten konnte nicht �berpr�ft werden.");
					}
					
					//neue Bytes einlesen
					try {
						Eingangsdaten.read(temp);
					} catch (IOException e) {
						System.err.println(e.getClass() + " Bytes konnten nicht eingelesen werden.");
						e.printStackTrace();			
					}
					
					if (n==17) {	//korrekte Anzahl an Datenbytes empfangen
						//neue Bytes zu Integer konvertieren
						for (int i=0; i<temp.length; i++) {
							tempInt[i] = temp[i];
						}
						
						//Pr�fsumme berechnen
						Pr�fsummeDbl = 0;
						for (int i=0; i<16; i++) {
							Pr�fsummeDbl = Pr�fsummeDbl + tempInt[i] * Math.pow(Pi, (i / 10.0) - 1.0);
						}

						Pr�fsummeInt = (int) (Pr�fsummeDbl / 6.0 - 128.0);
						if (Pr�fsummeInt<0) {
							Pr�fsummeInt = -Pr�fsummeInt;
						}
						
						
						if (Pr�fsummeInt == tempInt[16]) {	//Pr�fsummen vergleichen
							//N, G, X, Y, I, J berechnen
				    		
				    		N=(int) (tempInt[0]*10000.0 + tempInt[1]*100.0 + tempInt[2]);
				            G=tempInt[3];
				            if (tempInt[4] >= 0) {
				                X=tempInt[4]*100.0 + tempInt[5] + tempInt[6]/100.0;
				            } else {
				                X=tempInt[4]*100.0 - tempInt[5] - tempInt[6]/100.0;
				            }
				            if (tempInt[7] >= 0) {
				                Y=tempInt[7]*100.0 + tempInt[8] + tempInt[9]/100.0;
				            } else {
				                Y=tempInt[7]*100.0 - tempInt[8] - tempInt[9]/100.0;
				            }
				            if (tempInt[10] >= 0) {
				                I=tempInt[10]*100.0 + tempInt[11] + tempInt[12]/100.0;
				            } else {
				                I=tempInt[10]*100.0 - tempInt[11] - tempInt[12]/100.0;
				            }
				            if (tempInt[13] >= 0) {
				                J=tempInt[13]*100.0 + tempInt[14] + tempInt[15]/100.0;
				            } else {
				                J=tempInt[13]*100.0 - tempInt[14] - tempInt[15]/100.0;
				            }
				            if (G==99) {
				            	if (n==0) {
				            		Fr�ser.X = X;
						            Fr�ser.Y = Y;
						            Fr�ser.I = I;
						            Fr�ser.J = J;					            
				            	} else if (0<N & N<=20){
				            		System.err.println("CNC Fehler E" + N);
				            		Fr�ser.NotAusBtn.doClick();
				            	} else if (N>20) {
				            		Fr�ser.CNCStatusInt = N;
				            		Fr�ser.NeueDatenFlag = true;
				            	}
				            }
				            
						} else {	//Daten fehlerhaft
							Fr�ser.DatenFehlerhaftFlag = true;
						}
							    		
					} else if(n==1 && temp[0]==-28) {	//�berpr�fen auf Initialisierungsbyte von RS232_Init_IRQ()
						System.out.println("RS232 erfolgreich Initilisiert");
						
				    } else {	//Daten fehlerhaft
				    	Fr�ser.DatenFehlerhaftFlag = true;
				    }						    	
				}
			} catch (IOException e) {
				System.err.println(e.getClass() + " Anzahl der neuen Daten konnte nicht �berpr�ft werden.");
			}
	    	
		    try {	//InputStream schlie�en
				Eingangsdaten.close();
			} catch (IOException e) {
				System.err.println(e.getClass() + " InputStream konnte nicht geschlossen werden.");
				e.printStackTrace();
			}
	    }	    
	}
}