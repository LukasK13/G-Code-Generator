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
	private JMenuBar Men�;
	private JMenuItem R�ckg�ngigMenu;
	private JSplitPane splitPane;
	private JProgressBar Fortschritt;
	private JLabel FortschrittLbl;
	private JTextArea Editor;
	private JTextArea Fehler;
	private JTabbedPane Konsole;
	
	//Datei �ffnen-Dialog
	private JFileChooser Browser;
	private FileFilter Filter;
	private File Datei;	
	
	//Subklassen
	private Thread SimulatorThread;
	private Thread PbUpdateThread;
	private Thread Pr�fenThread;
	private Simulator Simulator;
	private ProgressbarUpdate PbUpdate;
	private Pr�fen Pr�fen;
	private Fr�ser Fr�ser;
	private Thread Fr�serThread;
	
	//private Variablen
	private boolean gespeichert = false;
	private boolean DateinameVorhanden = false;
	private  double ZOffset = 0.0;
	private  double XYOffset = 0.0;
	private String Code;
	private String[] R�ckg�ngig = new String[100];
	private String[] CodeSplit;	
	private boolean gepr�ft=false;
	private boolean SimulatorInitialisiert = false;
	private boolean TextWirdVer�ndert=false;
	private int k=0;
	private boolean Fr�serInitialisiert = false;
	private  String[][] Werkzeugdaten = new String[][] {	{"T1", "1", "10"},
			{"T2", "2", "15"},
			{"T3", "3", "20"},
			{"T4", "0", "0"},
			{"T5", "0", "0"},
		};
	private JFrame thisVar = this;
	
	//�ffentliche Variablen
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
					int r�ckgabe = JOptionPane.showOptionDialog(null, "Programm beenden?","Best�tigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, new String[]{"Ja", "Nein", "speichern und beenden"}, "Nein");
					if (r�ckgabe==JOptionPane.YES_OPTION) {
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
						while (Fr�serThread.isAlive()) {
							Fr�serThread.interrupt();
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
					} else if (r�ckgabe==JOptionPane.CANCEL_OPTION) {
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
							while (Fr�serThread.isAlive()) {
								Fr�serThread.interrupt();
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
				Men�.setSize(Startseite.getWidth(), 21);
				splitPane.setBounds(0, 20, Startseite.getWidth(), Startseite.getHeight()-Men�.getHeight()-40);
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
		
		Men� = new JMenuBar();
		Men�.setToolTipText("Datei");
		Men�.setBounds(0, 0, Startseite.getWidth(), 21);
		Startseite.add(Men�);	
		JMenu Datei = new JMenu("Datei");
		Men�.add(Datei);
		
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
		
		JMenuItem Datei_�ffnen = new JMenuItem("\u00D6ffnen...");
		Datei_�ffnen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Open.png")));
		Datei_�ffnen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Datei_�ffnen();
			}
		});
		Datei_�ffnen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		Datei.add(Datei_�ffnen);
		
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
		Men�.add(Projekt);
		
		JMenuItem Projekt_Fr�soptionen = new JMenuItem("Fr\u00E4soptionen");
		Projekt_Fr�soptionen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Settings.png")));
		Projekt_Fr�soptionen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		Projekt_Fr�soptionen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Fr�soptionen();
			}
		});
		
		R�ckg�ngigMenu = new JMenuItem("R\u00FCckg\u00E4ngig");
		R�ckg�ngigMenu.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Undo.png")));
		R�ckg�ngigMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				R�ckg�ngig();
			}
		});
		R�ckg�ngigMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		R�ckg�ngigMenu.setEnabled(false);
		Projekt.add(R�ckg�ngigMenu);
		Projekt.add(Projekt_Fr�soptionen);
		
		JMenuItem Projekt_Pr�fen = new JMenuItem("Pr\u00FCfen...");
		Projekt_Pr�fen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Test.png")));
		Projekt_Pr�fen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Pr�fen();
			}
		});
		Projekt_Pr�fen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		Projekt.add(Projekt_Pr�fen);
		
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
		Men�.add(RS232);
		
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
		Men�.add(Starten);
		
		JMenuItem Start_Fr�sen = new JMenuItem("Fr�sen");
		Start_Fr�sen.setIcon(new ImageIcon(CNC_Steuerung.class.getResource("/resources/Go.png")));
		Start_Fr�sen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Fr�sen();
			}
		});
		Start_Fr�sen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
		Starten.add(Start_Fr�sen);
		
		JMenu Hilfe = new JMenu("Hilfe");
		Men�.add(Hilfe);
		
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
		splitPane.setBounds(0, 20, Startseite.getWidth(), Startseite.getHeight()-Men�.getHeight()-40);
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
		
		JMenuItem mntmLschen = new JMenuItem("L�schen");
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
				gepr�ft=false;
				if (!TextWirdVer�ndert) {
					R�ckg�ngigUpdate();
				}
			}
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				gespeichert=false;
				gepr�ft=false;
				if (!TextWirdVer�ndert) {
					R�ckg�ngigUpdate();
				}
			}
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				gespeichert=false;
				gepr�ft=false;
				if (!TextWirdVer�ndert) {
					R�ckg�ngigUpdate();
				}
			}
		});

		Editor_Scroll.setViewportView(Editor);
		
		for (int i=0; i<R�ckg�ngig.length; i++) {
			R�ckg�ngig[i] = "";
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
				
		Fr�ser = new Fr�ser();		
		Fr�ser.parent = this;
		Fr�serThread = new Thread(Fr�ser);
		Fr�serThread.setName("Fr�serThread");
		
		CommPortIdentifier SeriellerPort_IDTemp = null;
	    @SuppressWarnings("rawtypes")
		Enumeration CommPortEnum; //Alle bekannten Ports werden aufgez�hlt
	    CommPortEnum = CommPortIdentifier.getPortIdentifiers(); //Jede Portkennung wird �berpr�ft
	    while (CommPortEnum.hasMoreElements()) {
	    	SeriellerPort_IDTemp = (CommPortIdentifier) CommPortEnum.nextElement();
	     	if(SeriellerPort_IDTemp.getPortType() == CommPortIdentifier.PORT_SERIAL && SeriellerPort_IDTemp.getName().equals(SeriellerPort_Favorit_Name)) { //ist es ein serieller Port?
	    		SeriellerPort_ID=SeriellerPort_IDTemp;
	    		SeriellerPort_Name = SeriellerPort_ID.getName();
	    	}
	    }
	}
	
	public void Reset() {
		for (int i=0; i<R�ckg�ngig.length; i++) {
			R�ckg�ngig[i] = "";
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
		int r�ckgabe = JOptionPane.showOptionDialog(null, "Neue Datei erstellen?","Best�tigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, new String[]{"Ja", "Nein", "speichern und schlie�en"}, "Nein");
		if (r�ckgabe==JOptionPane.YES_OPTION) {		//Bei Ja wird das Programm zur�ckgesetzt
			Reset();
		}
		else if (r�ckgabe==JOptionPane.CANCEL_OPTION) {		//Bei Speichern und schlie�en wird das Programm gespeichert und zur�ckgesetzt
			if (Datei_Speichern() == true) {
				Reset();
			}			
		}
	} //End Datei_Neu()
	
	public void Datei_�ffnen() {
		if (Editor.getDocument().getLength() != 0 && !gespeichert) {
			int r�ckgabe = JOptionPane.showOptionDialog(null, "Neue Datei �ffnen?","Best�tigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, new String[]{"Ja", "Nein", "speichern und �ffnen"}, "speichern und �ffnen");
			if (r�ckgabe==JOptionPane.YES_OPTION) {
				Reset();
			}
			else if (r�ckgabe==JOptionPane.NO_OPTION | r�ckgabe==JOptionPane.CLOSED_OPTION) {
				return;
			}
			else if (r�ckgabe==JOptionPane.CANCEL_OPTION) {
				if (Datei_Speichern() == true) {
					Reset();
				} else {
					return;
				}
			}
		}
		Browser = new JFileChooser();
		Filter = new FileNameExtensionFilter("MF70 Fr�sprogramm", "mf70");
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
	  	        TextWirdVer�ndert = true;
	  	      
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
	  	        TextWirdVer�ndert = false;
	  	        br.close();
	  	    } catch (IOException ex) {
	  	        	ex.printStackTrace();
	  	    }		    
		} 
	} //End Datei_�ffnen()

	public boolean Datei_Speichern() {
		boolean erfolg = false;
		if (DateinameVorhanden==false) {
			Browser = new JFileChooser();
			Filter = new FileNameExtensionFilter("MF70 Fr�sprogramm", "mf70");
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
	        writer.write("----------MF70 Fr�sdaten erstellt mit MF70 CNC by Lukas Klass----------");
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
	
	public void Fr�soptionen() {
		Fr�soptionen Dialog = new Fr�soptionen(this, Werkzeugdaten, ZOffset, XYOffset);
		Dialog.setVisible(true);
		Werkzeugdaten = Dialog.getTableContent();
		ZOffset = Dialog.getZOffset();
		XYOffset = Dialog.getXYOffset();	
	}
	
	public void Verbindungseinstellungen() {
		Verbindungseinstellungen Dialog = new Verbindungseinstellungen(this, SeriellerPort_ID, SeriellerPort_Name, Baudrate, DatenBits, StopBits, Flusskontrolle);
		Dialog.setVisible(true);
		if (Dialog.getPortGew�hlt()) {
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
	
	public void Pr�fen() {
		PbUpdateThread = new Thread(PbUpdate);
		PbUpdate.FortschrittBar = Fortschritt;
		PbUpdate.Fortschritt = 0;
		PbUpdateThread.setName("PbUpdateThread");
		PbUpdateThread.start();
		
		Konsole.setSelectedIndex(1);
		Code = Editor.getText();
		CodeSplit = Code.split("\n");
		Fehler.setText("");
		Pr�fen = new Pr�fen();
		Pr�fen.Code = CodeSplit;
		Pr�fen.Fortschritt = Fortschritt;
		Pr�fen.PbUpdate = PbUpdate;		
		Pr�fen.ZOffset = ZOffset;
		Pr�fenThread = new Thread (Pr�fen);
		Pr�fenThread.setName("Pr�fenThread");
		Pr�fenThread.start();
		
		try {
			Pr�fenThread.join();
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
		while (Pr�fenThread.isAlive()) {
			Pr�fenThread.interrupt();
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
		
		CodeSplit = Pr�fen.Code;
		
		Editor.setText("");
		for (int i=0;i<CodeSplit.length ;i++ ) {
			try {
				Editor.getDocument().insertString(Editor.getDocument().getLength(), CodeSplit[i] + "\n", null);       
		 	} catch (Exception ex) {
		 		ex.printStackTrace();
		 	}	
		} // end of for	
		
		if (Pr�fen.FehlerInt==0) {
			gepr�ft=true;
		}
	}
	
	public void Simulieren() {
		if (gepr�ft) {
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
			Pr�fen();
		}
	}
	
	public void Fr�sen() {
		if (gepr�ft) {
			Code = Editor.getText();
			Fr�ser.Code = Code;
			CodeSplit = Code.split("\n");
			Fr�ser.CodeSplit=CodeSplit;
			Fr�ser.Werkzeugdaten = Werkzeugdaten;
			if (SeriellerPort_ID != null) {
				Fr�ser.SeriellerPort_ID = SeriellerPort_ID;
				Fr�ser.Baudrate = Baudrate;
				Fr�ser.DatenBits = DatenBits;
				Fr�ser.StopBits = StopBits;
				Fr�ser.Flusskontrolle = Flusskontrolle;
				Fr�ser.setVisible(true);
				if (!Fr�serInitialisiert) {			
					Fr�serThread.start();
					Fr�serInitialisiert = true;
				}
				try {
					Fr�serThread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					//e.printStackTrace();
				}
			} else {
				System.err.println(getClass() + " Kein Serieller Port gew�hlt. Bitte Port w�hlen und erneut Versuchen.");
				Konsole.setSelectedIndex(1);
			}			
			
		} else {
			Pr�fen();
		}
	}
	
	private void R�ckg�ngigUpdate() {
		for (int i=R�ckg�ngig.length-1; i>0; i--) {
			R�ckg�ngig[i] = R�ckg�ngig[i-1];
		}
		R�ckg�ngig[0] = Editor.getText();
		if (R�ckg�ngig[1] == "") {
			R�ckg�ngigMenu.setEnabled(false);
		} else {
			R�ckg�ngigMenu.setEnabled(true);
		}
		//System.out.println(R�ckg�ngig[0]);
	}
	
	private void R�ckg�ngig() {
		if (R�ckg�ngig[1] != "") {
			TextWirdVer�ndert=true;
			Editor.setText(R�ckg�ngig[1]);	
			TextWirdVer�ndert=false;
		}
		
		for (int i=1; i<R�ckg�ngig.length; i++) {
			R�ckg�ngig[i-1] = R�ckg�ngig[i];
			R�ckg�ngig[R�ckg�ngig.length-1] = "";
		}
		if (R�ckg�ngig[1] == "") {
			R�ckg�ngigMenu.setEnabled(false);
		} else {
			R�ckg�ngigMenu.setEnabled(true);
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
