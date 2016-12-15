package lk1311;

import gnu.io.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.BadLocationException;

import java.io.File;
import java.util.Enumeration;

import javax.swing.border.EmptyBorder;

public class CNC_Steuerung extends JFrame {
	//sichtbare Komponenten
	private JPanel Startseite;
	private JMenuBar Menü;
	private JMenuItem RückgängigMenu;
	private JSplitPane splitPane;
	private JProgressBar Fortschritt;
	private JLabel FortschrittLbl;
	private JTextArea Editor;
	private JTextArea Fehler;
	private JTabbedPane Konsole;
	
	//Datei öffnen-Dialog
	private JFileChooser Browser;
	private FileFilter Filter;
	private File Datei;	
	
	//Subklassen
	private Thread SimulatorThread;
	private Thread PbUpdateThread;
	private Thread PrüfenThread;
	private Simulator Simulator;
	private ProgressbarUpdate PbUpdate;
	private Prüfen Prüfen;
	private Fräser Fräser;
	private Thread FräserThread;
	
	//private Variablen
	private boolean gespeichert = false;
	private boolean DateinameVorhanden = false;
	private  double ZOffset = 0.0;
	private  double XYOffset = 0.0;
	private String Code;
	private String[] Rückgängig = new String[100];
	private String[] CodeSplit;	
	private boolean geprüft=false;
	private boolean SimulatorInitialisiert = false;
	private boolean TextWirdVerändert=false;
	private int k=0;
	private boolean FräserInitialisiert = false;
	private  String[][] Werkzeugdaten = new String[][] {	{"T1", "1", "10"},
			{"T2", "2", "15"},
			{"T3", "3", "20"},
			{"T4", "0", "0"},
			{"T5", "0", "0"},
		};
	private JFrame thisVar = this;
	
	//öffentliche Variablen
	public PrintStream TextStream;
	public PrintStream ErrorStream;
	
	//Serieller Port
	private CommPortIdentifier SeriellerPort_ID = null;;
	private String SeriellerPort_Favorit_Name = "COM5";
	private String SeriellerPort_Name;
	private int Baudrate = 115200;
	private int DatenBits = 8;
	private int StopBits = 1;
	private int Flusskontrolle = 0;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CNC_Steuerung steuerung = new CNC_Steuerung();
					steuerung.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public CNC_Steuerung() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {				
				if (Editor.getDocument().getLength() != 0 && !gespeichert) {
					int rückgabe = JOptionPane.showOptionDialog(null, "Programm beenden?","Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, new String[]{"Ja", "Nein", "speichern und beenden"}, "Nein");
					if (rückgabe==JOptionPane.YES_OPTION) {
						k=0;
						while (PbUpdateThread.isAlive()) {
							PbUpdateThread.interrupt();
							k++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								//e.printStackTrace();
							}
							if (k==5) {
								System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
								break;
							}
						}
						k=0;
						while (SimulatorThread.isAlive()) {
							SimulatorThread.interrupt();
							k++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								//e.printStackTrace();
							}
							if (k==5) {
								System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
								break;
							}
						}
						k=0;
						while (FräserThread.isAlive()) {
							FräserThread.interrupt();
							k++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								//e.printStackTrace();
							}
							if (k==5) {
								System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
								break;
							}
						}
						dispose();
						System.exit(0);
					} else if (rückgabe==JOptionPane.CANCEL_OPTION) {
						if (Datei_Speichern() == true) {
							k=0;
							while (PbUpdateThread.isAlive()) {
								PbUpdateThread.interrupt();
								k++;
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
									//e.printStackTrace();
								}
								if (k==5) {
									System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
									break;
								}
							}
							k=0;
							while (SimulatorThread.isAlive()) {
								SimulatorThread.interrupt();
								k++;
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
									//e.printStackTrace();
								}
								if (k==5) {
									System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
									break;
								}
							}
							k=0;
							while (FräserThread.isAlive()) {
								FräserThread.interrupt();
								k++;
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									Thread.currentThread().interrupt();
									//e.printStackTrace();
								}
								if (k==5) {
									System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
									break;
								}
							}
							dispose();
							System.exit(0);
						}				
					}
				} else {
					dispose();
					System.exit(0);
				}				
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				Startseite.setSize(getWidth()-16, getHeight()-16);
				Menü.setSize(Startseite.getWidth(), 21);
				splitPane.setBounds(0, 20, Startseite.getWidth(), Startseite.getHeight()-Menü.getHeight()-40);
				splitPane.setDividerLocation(Startseite.getHeight()-200);
				Fortschritt.setBounds(70,Startseite.getHeight()-39,Startseite.getWidth()-84,14);
				FortschrittLbl.setBounds(5, Startseite.getHeight()-39, 60, 14);
				Startseite.repaint();
				repaint();
			}
		});
		setTitle("MF70 CNC - Unbenannt.mf70");
		setResizable(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Simulator.class.getResource("/resources/Logo.jpg")));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(50, 50, 700, 800);
		Startseite = new JPanel();
		Startseite.setBorder(new EmptyBorder(5, 5, 5, 5));
		Startseite.setLayout(null);
		Startseite.setSize(getWidth()-16, getHeight()-16);
		setContentPane(Startseite);
		
		Menü = new JMenuBar();
		Menü.setToolTipText("Datei");
		Menü.setBounds(0, 0, Startseite.getWidth(), 21);
		Startseite.add(Menü);	
		JMenu Datei = new JMenu("Datei");
		Menü.add(Datei);
		
		JMenuItem Datei_Neu = new JMenuItem("Neu");
		Datei_Neu.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/New.png")));
		Datei_Neu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Editor.getDocument().getLength() != 0) {
					Datei_Neu();
				}				
			}
		});
		Datei_Neu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		Datei.add(Datei_Neu);
		
		JMenuItem Datei_Öffnen = new JMenuItem("\u00D6ffnen...");
		Datei_Öffnen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Open.png")));
		Datei_Öffnen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Datei_Öffnen();
			}
		});
		Datei_Öffnen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		Datei.add(Datei_Öffnen);
		
		JMenuItem Datei_Speichern = new JMenuItem("Speichern");
		Datei_Speichern.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Save.png")));
		Datei_Speichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Datei_Speichern();
			}
		});
		Datei_Speichern.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		Datei.add(Datei_Speichern);
		
		JMenuItem Datei_SpeichernUnter = new JMenuItem("Speichern unter...");
		Datei_SpeichernUnter.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Save.png")));
		Datei_SpeichernUnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DateinameVorhanden=false;
				Datei_Speichern();
			}
		});
		Datei.add(Datei_SpeichernUnter);
		
		JMenu Projekt = new JMenu("Projekt");
		Menü.add(Projekt);
		
		JMenuItem Projekt_Fräsoptionen = new JMenuItem("Fr\u00E4soptionen");
		Projekt_Fräsoptionen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Settings.png")));
		Projekt_Fräsoptionen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		Projekt_Fräsoptionen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Fräsoptionen();
			}
		});
		
		RückgängigMenu = new JMenuItem("R\u00FCckg\u00E4ngig");
		RückgängigMenu.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Undo.png")));
		RückgängigMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Rückgängig();
			}
		});
		RückgängigMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		RückgängigMenu.setEnabled(false);
		Projekt.add(RückgängigMenu);
		Projekt.add(Projekt_Fräsoptionen);
		
		JMenuItem Projekt_Prüfen = new JMenuItem("Pr\u00FCfen...");
		Projekt_Prüfen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Test.png")));
		Projekt_Prüfen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Prüfen();
			}
		});
		Projekt_Prüfen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		Projekt.add(Projekt_Prüfen);
		
		JMenuItem Simulieren = new JMenuItem("Simulieren...");
		Simulieren.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Go.png")));
		Simulieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Simulieren();
			}
		});
		Simulieren.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		Projekt.add(Simulieren);
		
		JMenu RS232 = new JMenu("RS232");
		Menü.add(RS232);
		
		JMenuItem RS232_Verbindungseinstellungen = new JMenuItem("Verbindungseinstellungen");
		RS232_Verbindungseinstellungen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Settings.png")));
		RS232_Verbindungseinstellungen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		RS232_Verbindungseinstellungen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Verbindungseinstellungen();
			}
		});
		RS232.add(RS232_Verbindungseinstellungen);
		
		JMenuItem RS232_VerbindungTesten = new JMenuItem("Verbindung testen");
		RS232_VerbindungTesten.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Connect.png")));
		RS232_VerbindungTesten.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		RS232_VerbindungTesten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Verbindungstest Dialog = new Verbindungstest(thisVar, SeriellerPort_ID, Baudrate, DatenBits, StopBits, Flusskontrolle);
				Dialog.setVisible(true);
			}
		});
		RS232.add(RS232_VerbindungTesten);
		
		JMenu Starten = new JMenu("Starten");
		Menü.add(Starten);
		
		JMenuItem Start_Fräsen = new JMenuItem("Fräsen");
		Start_Fräsen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Go.png")));
		Start_Fräsen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Fräsen();
			}
		});
		Start_Fräsen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		Starten.add(Start_Fräsen);
		
		JMenu Hilfe = new JMenu("Hilfe");
		Menü.add(Hilfe);
		
		JMenuItem Hilfe_Befehle = new JMenuItem("Befehle");
		Hilfe_Befehle.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Command.png")));
		Hilfe_Befehle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Befehle();
			}
		});
		Hilfe.add(Hilfe_Befehle);
		
		JMenuItem Hilfe_Fehlercodes = new JMenuItem("Fehlercodes");
		Hilfe_Fehlercodes.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Errors.png")));
		Hilfe_Fehlercodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Fehlercodes();
			}
		});
		Hilfe_Fehlercodes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		Hilfe.add(Hilfe_Fehlercodes);
		
		splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setBounds(0, 20, Startseite.getWidth(), Startseite.getHeight()-Menü.getHeight()-40);
		splitPane.setDividerLocation(Startseite.getHeight()-200);
		Startseite.add(splitPane);
		
		Konsole = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(Konsole);
		
		JTextPane CNC_Status = new JTextPane();
		CNC_Status.setEditable(false);
		Konsole.addTab("CNC-Status", null, CNC_Status, null);
		
		JScrollPane Fehler_Scroll = new JScrollPane();
		Konsole.addTab("Fehler", null, Fehler_Scroll, null);

		Fehler = new JTextArea();
		Fehler.setEditable(false);
		Fehler.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
			    if(ce.getDot() != ce.getMark()) {
			    	try {
			    		try {
			    			int Fehlerzeile = Integer.parseInt(Fehler.getText(Fehler.getLineStartOffset(Fehler.getLineOfOffset(Fehler.getSelectionStart())) + 12,
			    					Fehler.getLineEndOffset(Fehler.getLineOfOffset(Fehler.getSelectionStart())) - 
			    					Fehler.getLineStartOffset(Fehler.getLineOfOffset(Fehler.getSelectionStart())) - 13));

			    			Editor.select(Editor.getLineStartOffset(Fehlerzeile - 1), Editor.getLineEndOffset(Fehlerzeile - 1)-1);
			    			Editor.requestFocus();
			    		} catch (NumberFormatException e) {			    			
			    		}
			 						
					} catch (BadLocationException e) {
						System.err.println(getClass() + "Angeklickte Zeile konnte nicht ermittelt werden.");
					}
			    } 
			}
		});
		Fehler_Scroll.setViewportView(Fehler);
		
		JPopupMenu FehlerKontextMenu = new JPopupMenu();
		addPopup(Fehler, FehlerKontextMenu);
		
		JMenuItem mntmLschen = new JMenuItem("Löschen");
		mntmLschen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Delete.png")));
		mntmLschen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Fehler.setText("");
			}
		});
		FehlerKontextMenu.add(mntmLschen);
		
		TextStream = new PrintStream(System.out) {
			public void print(String s) {
				try {
					Fehler.getDocument().insertString(Fehler.getDocument().getLength(), s + "\n", StyleConstant.BLACK);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
		    }		     
		};
		
		ErrorStream = new PrintStream(System.out) {
			public void print(String s) {
				try {
					Fehler.getDocument().insertString(Fehler.getDocument().getLength(), s + "\n", StyleConstant.RED);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
		    }		     
		};
		System.setOut(TextStream);
		System.setErr(ErrorStream);
		
		JScrollPane Editor_Scroll = new JScrollPane();
		Editor_Scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.setLeftComponent(Editor_Scroll);
		
		Editor = new JTextArea();
		Editor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				gespeichert=false;
				geprüft=false;
				if (!TextWirdVerändert) {
					RückgängigUpdate();
				}
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				gespeichert=false;
				geprüft=false;
				if (!TextWirdVerändert) {
					RückgängigUpdate();
				}
			}
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				gespeichert=false;
				geprüft=false;
				if (!TextWirdVerändert) {
					RückgängigUpdate();
				}
			}
		});

		Editor_Scroll.setViewportView(Editor);
		
		for (int i=0; i<Rückgängig.length; i++) {
			Rückgängig[i] = "";
		}
		
		Fortschritt = new JProgressBar();
		Fortschritt.setStringPainted(true);
		Fortschritt.setBounds(70,Startseite.getWidth()-39,Startseite.getWidth()-84,14);
		Startseite.add(Fortschritt);
		
		FortschrittLbl = new JLabel("Fortschritt");
		FortschrittLbl.setBounds(5, Startseite.getHeight()-39, 60, 14);
		Startseite.add(FortschrittLbl);
		
		PbUpdate = new ProgressbarUpdate();
		PbUpdateThread = new Thread(PbUpdate);
		
		Simulator = new Simulator();		
		Simulator.parent = this;
		SimulatorThread = new Thread(Simulator);
		SimulatorThread.setName("SimulatorThread");
				
		Fräser = new Fräser();		
		Fräser.parent = this;
		FräserThread = new Thread(Fräser);
		FräserThread.setName("FräserThread");
		
		CommPortIdentifier SeriellerPort_IDTemp = null;
	    @SuppressWarnings("rawtypes")
		Enumeration CommPortEnum; //Alle bekannten Ports werden aufgezählt
	    CommPortEnum = CommPortIdentifier.getPortIdentifiers(); //Jede Portkennung wird überprüft
	    while (CommPortEnum.hasMoreElements()) {
	    	SeriellerPort_IDTemp = (CommPortIdentifier) CommPortEnum.nextElement();
	     	if(SeriellerPort_IDTemp.getPortType() == CommPortIdentifier.PORT_SERIAL && SeriellerPort_IDTemp.getName().equals(SeriellerPort_Favorit_Name)) { //ist es ein serieller Port?
	    		SeriellerPort_ID=SeriellerPort_IDTemp;
	    		SeriellerPort_Name = SeriellerPort_ID.getName();
	    	}
	    }
	}
	
	public void Reset() {
		for (int i=0; i<Rückgängig.length; i++) {
			Rückgängig[i] = "";
		}
		Editor.setText("");
		Werkzeugdaten[0][0]="T1";
		Werkzeugdaten[0][1]="1";
		Werkzeugdaten[0][2]="10";
		Werkzeugdaten[1][0]="T2";
		Werkzeugdaten[1][1]="2";
		Werkzeugdaten[1][2]="15";
		Werkzeugdaten[2][0]="T3";
		Werkzeugdaten[2][1]="3";
		Werkzeugdaten[2][2]="20";
		Werkzeugdaten[3][0]="T4";
		Werkzeugdaten[3][1]="0";
		Werkzeugdaten[3][2]="0";
		Werkzeugdaten[4][0]="T5";
		Werkzeugdaten[4][1]="0";
		Werkzeugdaten[4][2]="0";
		ZOffset=0.0;
		XYOffset=0.0;
		Fehler.setText("");
	}
	
	public void Datei_Neu() {
		int rückgabe = JOptionPane.showOptionDialog(null, "Neue Datei erstellen?","Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, new String[]{"Ja", "Nein", "speichern und schließen"}, "Nein");
		if (rückgabe==JOptionPane.YES_OPTION) {		//Bei Ja wird das Programm zurückgesetzt
			Reset();
		}
		else if (rückgabe==JOptionPane.CANCEL_OPTION) {		//Bei Speichern und schließen wird das Programm gespeichert und zurückgesetzt
			if (Datei_Speichern() == true) {
				Reset();
			}			
		}
	} //End Datei_Neu()
	
	public void Datei_Öffnen() {
		if (Editor.getDocument().getLength() != 0 && !gespeichert) {
			int rückgabe = JOptionPane.showOptionDialog(null, "Neue Datei öffnen?","Bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, new String[]{"Ja", "Nein", "speichern und öffnen"}, "speichern und öffnen");
			if (rückgabe==JOptionPane.YES_OPTION) {
				Reset();
			}
			else if (rückgabe==JOptionPane.NO_OPTION | rückgabe==JOptionPane.CLOSED_OPTION) {
				return;
			}
			else if (rückgabe==JOptionPane.CANCEL_OPTION) {
				if (Datei_Speichern() == true) {
					Reset();
				} else {
					return;
				}
			}
		}
		Browser = new JFileChooser();
		Filter = new FileNameExtensionFilter("MF70 Fräsprogramm", "mf70");
		Browser.setFileFilter(Filter);
		int rueckgabeWert = Browser.showOpenDialog(Startseite);
	    if(rueckgabeWert == JFileChooser.APPROVE_OPTION)	{
	    	Datei=Browser.getSelectedFile();
	    	setTitle("MF70 CNC - " + Datei.getName());
	    	try {
	  	        FileReader fr = new FileReader(Datei);
	  	        BufferedReader br = new BufferedReader(fr);
	  	        String temp = br.readLine();
	  	        temp = br.readLine();
	  	        temp = br.readLine();
	  	        Werkzeugdaten[0] = temp.split("\t");
	  	        temp = br.readLine();
	  	        Werkzeugdaten[1] = temp.split("\t");
	  	        temp = br.readLine();
	  	        Werkzeugdaten[2] = temp.split("\t");
	  	        temp = br.readLine();
	  	        Werkzeugdaten[3] = temp.split("\t");
	  	        temp = br.readLine();
	  	        Werkzeugdaten[4] = temp.split("\t");
	  	        temp = br.readLine();
	  	        String[] splitResult = temp.split("\t");
	  	        ZOffset = Double.parseDouble(splitResult[1]);
	  	        temp = br.readLine();
	  	        splitResult = temp.split("\t");
	  	        XYOffset = Double.parseDouble(splitResult[1]);
	  	        temp = br.readLine();
	  	        
	  	        String[] tempSplit;
	  	        TextWirdVerändert = true;
	  	      
	  	        for (int i=0;i<=Datei.length() ;i++ ) {
	  	        	temp = br.readLine();
	  	        	if (temp != null) {
	  	        		try {
	  	        			tempSplit = temp.split(System.getProperty("line.separator"));
	  	        			Editor.getDocument().insertString(Editor.getDocument().getLength(), tempSplit[0] + "\n", null);
	  	        		} catch (Exception ex) {
	  	   	  	        	ex.printStackTrace();
	  	   	  	    	}	  	        		
	  	        	}
	  	        } // end of for	  
	  	        TextWirdVerändert = false;
	  	        br.close();
	  	    } catch (IOException ex) {
	  	        	ex.printStackTrace();
	  	    }		    
		} 
	} //End Datei_Öffnen()

	public boolean Datei_Speichern() {
		boolean erfolg = false;
		if (DateinameVorhanden==false) {
			Browser = new JFileChooser();
			Filter = new FileNameExtensionFilter("MF70 Fräsprogramm", "mf70");
			Browser.setFileFilter(Filter);
			if(Browser.showSaveDialog(Startseite) == JFileChooser.APPROVE_OPTION) {
				String Pfad=Browser.getSelectedFile().getPath();
				if (Pfad.endsWith(".mf70") == false) {
					Pfad = Pfad + ".mf70";
				}	
				Datei = new File(Pfad);
			}
		}
		
		if (Datei != null) {
			if (Datei.exists() == true) {
				Datei.delete();
			}
			setTitle("MF70 CNC - " + Datei.getName());
			try {
	        FileWriter writer = new FileWriter(Datei ,true); 
	        writer.write("----------MF70 Fräsdaten erstellt mit MF70 CNC by Lukas Klass----------");
	        writer.write(System.getProperty("line.separator"));
	        writer.write(System.getProperty("line.separator"));
	        for (int i=0; i<=4; i++) {
	        	writer.write(Werkzeugdaten[i][0] + "\t" + Werkzeugdaten[i][1] + "\t" + Werkzeugdaten[i][2]);
		        writer.write(System.getProperty("line.separator"));
	        }
	        writer.write("Z_Offset" + "\t" + ZOffset);
	        writer.write(System.getProperty("line.separator"));
	        writer.write("XY-Offset" + "\t" + XYOffset);
	        writer.write(System.getProperty("line.separator"));
	        writer.write(System.getProperty("line.separator"));
	        String Puffer = Editor.getText();
	        String[] PufferSplit = Puffer.split("\n");
	        for (int i=0; i<PufferSplit.length; i++) {
	        	writer.write(PufferSplit[i]);
	        	writer.write(System.getProperty("line.separator"));
	        }
	        	//writer.write(System.getProperty("line.separator"));
	        
	        writer.flush();
	        writer.close();
	        } catch (IOException e) {
	        e.printStackTrace();
	        }
			DateinameVorhanden=true;
			gespeichert = true;
			erfolg = true;
		}
		return erfolg;
	} //End Datei_Speichern()
	
	public void Fräsoptionen() {
		Fräsoptionen Dialog = new Fräsoptionen(this, Werkzeugdaten, ZOffset, XYOffset);
		Dialog.setVisible(true);
		Werkzeugdaten = Dialog.getTableContent();
		ZOffset = Dialog.getZOffset();
		XYOffset = Dialog.getXYOffset();	
	}
	
	public void Verbindungseinstellungen() {
		Verbindungseinstellungen Dialog = new Verbindungseinstellungen(this, SeriellerPort_ID, SeriellerPort_Name, Baudrate, DatenBits, StopBits, Flusskontrolle);
		Dialog.setVisible(true);
		if (Dialog.getPortGewählt()) {
			SeriellerPort_ID = Dialog.getPort();
			SeriellerPort_Name = Dialog.getPortName();
		}		
		Baudrate = Dialog.getBaudrate();
		DatenBits = Dialog.getDatenBits();
		StopBits = Dialog.getStopBits();
		Flusskontrolle = Dialog.getFlusskontrolle();
	}
	
	public void Befehle() {
		Befehle GBefehle = new Befehle(this);
		GBefehle.setVisible(true);
	}
	
	public void Fehlercodes() {
		Fehlercodes Fehlercodes = new Fehlercodes(this);
		Fehlercodes.setVisible(true);
	}
	
	public void Prüfen() {
		PbUpdateThread = new Thread(PbUpdate);
		PbUpdate.FortschrittBar = Fortschritt;
		PbUpdate.Fortschritt = 0;
		PbUpdateThread.setName("PbUpdateThread");
		PbUpdateThread.start();
		
		Konsole.setSelectedIndex(1);
		Code = Editor.getText();
		CodeSplit = Code.split("\n");
		Fehler.setText("");
		Prüfen = new Prüfen();
		Prüfen.Code = CodeSplit;
		Prüfen.Fortschritt = Fortschritt;
		Prüfen.PbUpdate = PbUpdate;		
		Prüfen.ZOffset = ZOffset;
		PrüfenThread = new Thread (Prüfen);
		PrüfenThread.setName("PrüfenThread");
		PrüfenThread.start();
		
		try {
			PrüfenThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
		try { //Warten auf Update der Fortschrittsanzeige
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
		k=0;
		while (PbUpdateThread.isAlive()) {
			PbUpdateThread.interrupt();
			k++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				//e.printStackTrace();
			}
			if (k==5) {
				System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
				break;
			}
		}
		
		k=0;
		while (PrüfenThread.isAlive()) {
			PrüfenThread.interrupt();
			k++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				//e.printStackTrace();
			}
			if (k==5) {
				System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
				break;
			}
		}
		
		CodeSplit = Prüfen.Code;
		
		Editor.setText("");
		for (int i=0;i<CodeSplit.length ;i++ ) {
			try {
				Editor.getDocument().insertString(Editor.getDocument().getLength(), CodeSplit[i] + "\n", null);       
		 	} catch (Exception ex) {
		 		ex.printStackTrace();
		 	}	
		} // end of for	
		
		if (Prüfen.FehlerInt==0) {
			geprüft=true;
		}
	}
	
	public void Simulieren() {
		if (geprüft) {
			Code = Editor.getText();
			CodeSplit = Code.split("\n");
			Simulator.CodeSplit=CodeSplit;
			Simulator.Werkzeugdaten = Werkzeugdaten;
			Simulator.setVisible(true);
			if (!SimulatorInitialisiert) {			
				SimulatorThread.start();
				SimulatorInitialisiert = true;
			}
			try {
				SimulatorThread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				//e.printStackTrace();
			}
		} else {
			Prüfen();
		}
	}
	
	public void Fräsen() {
		if (geprüft) {
			Code = Editor.getText();
			Fräser.Code = Code;
			CodeSplit = Code.split("\n");
			Fräser.CodeSplit=CodeSplit;
			Fräser.Werkzeugdaten = Werkzeugdaten;
			if (SeriellerPort_ID != null) {
				Fräser.SeriellerPort_ID = SeriellerPort_ID;
				Fräser.Baudrate = Baudrate;
				Fräser.DatenBits = DatenBits;
				Fräser.StopBits = StopBits;
				Fräser.Flusskontrolle = Flusskontrolle;
				Fräser.setVisible(true);
				if (!FräserInitialisiert) {			
					FräserThread.start();
					FräserInitialisiert = true;
				}
				try {
					FräserThread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					//e.printStackTrace();
				}
			} else {
				System.err.println(getClass() + " Kein Serieller Port gewählt. Bitte Port wählen und erneut Versuchen.");
				Konsole.setSelectedIndex(1);
			}			
			
		} else {
			Prüfen();
		}
	}
	
	private void RückgängigUpdate() {
		for (int i=Rückgängig.length-1; i>0; i--) {
			Rückgängig[i] = Rückgängig[i-1];
		}
		Rückgängig[0] = Editor.getText();
		if (Rückgängig[1] == "") {
			RückgängigMenu.setEnabled(false);
		} else {
			RückgängigMenu.setEnabled(true);
		}
		//System.out.println(Rückgängig[0]);
	}
	
	private void Rückgängig() {
		if (Rückgängig[1] != "") {
			TextWirdVerändert=true;
			Editor.setText(Rückgängig[1]);	
			TextWirdVerändert=false;
		}
		
		for (int i=1; i<Rückgängig.length; i++) {
			Rückgängig[i-1] = Rückgängig[i];
			Rückgängig[Rückgängig.length-1] = "";
		}
		if (Rückgängig[1] == "") {
			RückgängigMenu.setEnabled(false);
		} else {
			RückgängigMenu.setEnabled(true);
		}
	}
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
