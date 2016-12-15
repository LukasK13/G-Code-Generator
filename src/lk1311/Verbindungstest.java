package lk1311;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import gnu.io.*;

public class Verbindungstest extends JDialog {
	
	private int FortschrittInt=0;
	private JButton OKBtn;
	private JLabel StatusLbl; 
	private static final double Pi = 3.14;
	public boolean VerbindungErfolgreich = false;
	
	public Verbindungstest (JDialog parent, CommPortIdentifier SeriellerPort_ID, int Baudrate, int DatenBits, int StopBits, int Flusskontrolle) {
		super(parent, "RS232 Verbindungseinstellungen", true);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				new Thread(
						new Runnable() {
							public void run() {
								VerbindungTesten(SeriellerPort_ID, Baudrate, DatenBits, StopBits, Flusskontrolle);													
					        }
					    }
				).start();
				
			}
		});
		setTitle("RS232 Verbindungstest");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(400,150);
		
		JPanel RS232_Panel = new JPanel();
		RS232_Panel.setBounds(0, 0, 10, 10);
		getContentPane().add(RS232_Panel);
		RS232_Panel.setLayout(null);
		
		JLabel lblRsVerbindungseinstellungen = new JLabel("RS232 Verbindungstest");
		lblRsVerbindungseinstellungen.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblRsVerbindungseinstellungen.setBounds(10, 11, 374, 20);
		RS232_Panel.add(lblRsVerbindungseinstellungen);
		
		StatusLbl = new JLabel();
		StatusLbl.setBounds(10, 42, 374, 14);
		RS232_Panel.add(StatusLbl);
		
		JProgressBar Fortschritt = new JProgressBar();
		Fortschritt.setBounds(10, 62, 374, 14);
		RS232_Panel.add(Fortschritt);
		
		OKBtn = new JButton("OK");
		OKBtn.setEnabled(false);
		OKBtn.setBounds(10, 87, 89, 23);
		OKBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		RS232_Panel.add(OKBtn);
		
		new Thread(	new Runnable() {
			public void run() {
				Thread.currentThread().setName("Verbindungstest");
				Fortschritt.setValue(0);
					while (true) {
						if (FortschrittInt != 100) {
							Fortschritt.setValue(FortschrittInt);
						} else {
							Fortschritt.setValue(FortschrittInt);
							break;
						}							
					}						
				}
		}).start();		
	}
	
	public Verbindungstest (JFrame parent, CommPortIdentifier SeriellerPort_ID, int Baudrate, int DatenBits, int StopBits, int Flusskontrolle) {
		super(parent, "RS232 Verbindungseinstellungen", true);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				new Thread(
						new Runnable() {
							public void run() {
								VerbindungTesten(SeriellerPort_ID, Baudrate, DatenBits, StopBits, Flusskontrolle);													
					        }
					    }
				).start();
				
			}
		});
		setTitle("RS232 Verbindungstest");
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(400,150);
		
		JPanel RS232_Panel = new JPanel();
		RS232_Panel.setBounds(0, 0, 10, 10);
		getContentPane().add(RS232_Panel);
		RS232_Panel.setLayout(null);
		
		JLabel lblRsVerbindungseinstellungen = new JLabel("RS232 Verbindungstest");
		lblRsVerbindungseinstellungen.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblRsVerbindungseinstellungen.setBounds(10, 11, 374, 20);
		RS232_Panel.add(lblRsVerbindungseinstellungen);
		
		StatusLbl = new JLabel();
		StatusLbl.setBounds(10, 42, 374, 14);
		RS232_Panel.add(StatusLbl);
		
		JProgressBar Fortschritt = new JProgressBar();
		Fortschritt.setBounds(10, 62, 374, 14);
		RS232_Panel.add(Fortschritt);
		
		OKBtn = new JButton("OK");
		OKBtn.setEnabled(false);
		OKBtn.setBounds(10, 87, 89, 23);
		OKBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		RS232_Panel.add(OKBtn);
		
		new Thread(	new Runnable() {
			public void run() {
				Fortschritt.setValue(0);
					while (true) {
						if (FortschrittInt != 100) {
							Fortschritt.setValue(FortschrittInt);
						} else {
							Fortschritt.setValue(FortschrittInt);
							break;
						}							
					}						
				}
		}).start();
		
	}
	
	public void VerbindungTesten(CommPortIdentifier SeriellerPort_ID, int Baudrate, int DatenBits, int StopBits, int Flusskontrolle) {
		if (SeriellerPort_ID != null) {
			StatusLbl.setForeground(Color.black);
			StatusLbl.setText("Serieller Port " + SeriellerPort_ID.getName() + " wird geöffnet...");
			SerialPort SeriellerPort = null;
			if (SeriellerPort_ID != null) {
		    	try {
			    	SeriellerPort = (SerialPort) SeriellerPort_ID.open("Java RX/TX RS232", 10000);
			    } catch(PortInUseException e) {
			        System.err.println("Port " + SeriellerPort_ID.getName() + " wird bereits von einem anderen Programm verwendet");
			    }
			    
			    try {
			    	SeriellerPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e) {
					e.printStackTrace();
				}
		    }
			
			if (SeriellerPort != null) {
				StatusLbl.setText("Sende Prüfbytes und warte auf Antwort...");
				RS232_SendenPrüf(SeriellerPort, 123456,99,1234.56,1234.56,1234.56,1234.56);
				for (int i=0; i<=100; i++) {
		    		FortschrittInt = i;
		    		try {
		    			Thread.currentThread();
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		    	}

				if (RS232_Empfangen(SeriellerPort)) {
					StatusLbl.setForeground(Color.green);
					StatusLbl.setText("Verbindungstest Erfolgreich!");
					VerbindungErfolgreich = true;
					OKBtn.setEnabled(true);
				} else {
					StatusLbl.setForeground(Color.red);
					StatusLbl.setText("Verbindungstest fehlgeschlagen!");
					OKBtn.setEnabled(true);
				}
				
				SeriellerPort.close();			
			}	
		} else {
			System.err.println("Bitte Seriellen Port wählen.");
			dispose();
		}
	}
		
	public boolean RS232_Empfangen(SerialPort SeriellerPort) {
		boolean Erfolg = false;
		
		BufferedInputStream Eingangsdaten = null;
		byte[] temp = new byte[17];
		int[] tempInt = new int[17];
	    
	    try {
	    	Eingangsdaten = new BufferedInputStream(SeriellerPort.getInputStream(), 30);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    
	    if (Eingangsdaten != null) { 
	    	//Anzahl der neuen Bytes feststellen
	    	int n=0;
			try {
				n = Eingangsdaten.available();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (n!=0) {
				//neue Bytes einlesen
				try {
					Eingangsdaten.read(temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		    	if (n==17) {	//korrekte Anzahl an Datenbytes empfangen
		    		//neues Bytes zu Integer konvertieren
		    		for (int i=0; i<temp.length; i++) {
		    			tempInt[i] = temp[i];
		    		}
		    		
		    		//Prüfsumme berechnen
		    		double PrüfsummeDbl = 0;
		    		for (int i=0; i<16; i++) {
		    			PrüfsummeDbl = PrüfsummeDbl + tempInt[i] * Math.pow(Pi, (i / 10.0) - 1.0);
		    		}

		    		int PrüfsummeInt = (int) (PrüfsummeDbl / 6.0 - 128.0);
		    		if (PrüfsummeInt<0) {
		    			PrüfsummeInt = -PrüfsummeInt;
		    		}
		    		
		    		
		    		if (PrüfsummeInt == tempInt[16]) {	//Prüfsummen vergleichen
		    			//N, G, X, Y, I, J berechnen
		    			double X=0, Y=0, I=0, J=0;
			    		int N=0, G=0;
			    		
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
			            
			            if (N==123456 && G==99 && X==1234.56 && Y==1234.56 && I==1234.56 && J==1234.56) {
			            	Erfolg = true;
			            } else {
			            	System.out.println(N + " " + G + " " + X + " " + Y + " " + I + " " + J);
			            }
			            
		    		}
		    	}
			}
	    		    	
	    	try {	//InputStream schließen
				Eingangsdaten.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	    	
	    }
	    return Erfolg;
	}
	
	public void RS232_SendenPrüf(SerialPort Port, int nInt, int gInt, double xDbl, double yDbl, double iDbl, double jDbl) {
		OutputStream Ausgangsdaten = null;
		
		int nInt1=0, nInt2=0, nInt3=0, xInt1=0, xInt2=0, xInt3=0, yInt1=0, yInt2=0, yInt3=0, iInt1=0, iInt2=0, iInt3=0, jInt1=0, jInt2=0, jInt3=0;
		boolean ZahlenOK = true;
		
		if (nInt <=999999 && nInt > 0) {
			nInt1 = (int) (nInt / 10000);
			nInt2 = (int) ((nInt - nInt1 * 10000) / 100);
			nInt3 = nInt - nInt1 * 10000 - nInt2 * 100;
		} else {
			System.out.println("N zu groß");
			ZahlenOK = false;
		}
		
		if (gInt > 255 && gInt >= 0) {
			System.out.println("G zu groß");
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
			System.out.println("X zu groß / klein");
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
			System.out.println("Y zu groß / klein");
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
			System.out.println("I zu groß / klein");
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
			System.out.println("J zu groß / klein");
			ZahlenOK = false;
		}
		
		if (ZahlenOK) {
			int[] Zahlen = {nInt1, nInt2, nInt3, gInt, xInt1, xInt2, xInt3, yInt1, yInt2, yInt3, iInt1, iInt2, iInt3, jInt1, jInt2, jInt3};
			
			int [] ZahlenPrüf = Prüfsumme(Zahlen);
			
			/*
			for (int i=0; i<ZahlenPrüf.length; i++) {
				System.out.println((i + 1) + ". Index:   " + ZahlenPrüf[i]);
			}
			*/
			
			try {
				Ausgangsdaten = Port.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				for (int i=0; i<ZahlenPrüf.length; i++) {
					Ausgangsdaten.write(ZahlenPrüf[i]);
				}
			} catch (IOException e) {
				System.err.println("Fehler beim Senden");
			}
			try {
				Ausgangsdaten.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Ausgangsdaten.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public int[] Prüfsumme (int[] Zahlen) {
		double PrüfsummeDbl = 0;
		for (int i=0; i<16; i++) {
			PrüfsummeDbl = PrüfsummeDbl + Zahlen[i] * Math.pow(Pi, (i / 10.0) - 1.0);
		}

		int [] ZahlenPrüf = new int[17];
		for (int i =0; i<Zahlen.length; i++) {
			ZahlenPrüf[i] = Zahlen[i];
		}
		ZahlenPrüf[16] = (int) (PrüfsummeDbl / 6.0 - 128.0);
		if (ZahlenPrüf[16]<0) {
			ZahlenPrüf[16] = -ZahlenPrüf[16];
		}
		return ZahlenPrüf;
	}

}	
