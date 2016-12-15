package lk1311;

import gnu.io.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.TooManyListenersException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;

import com.jgoodies.forms.layout.*;
import com.jgoodies.forms.factories.*;

public class Fr�ser extends JFrame implements Runnable{

	//sichtbare Komponenten
	private JPanel contentPane, TurtlePanel, ParameterPanel;
	private JSplitPane splitPaneGanzGro�;
	private JSplitPane splitPaneGro�;
	private Turtle t;
	private JCheckBox KoordinatenachsenChk;
	private JLabel lblObenLinksX;
	private JNumberField ObenLinksXNum;
	private JLabel lblObenLinksY;
	private JNumberField ObenLinksYNum;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblL�ngeDx;
	private JLabel lblBreiteDy;
	private JLabel lblH�heDz;
	private JNumberField L�ngeNum;
	private JNumberField BreiteNum;
	private JNumberField H�heNum;
	private JButton �bernehmenBtn;
	private JSeparator separator_1;
	private JLabel lblAnzeige;
	private JSplitPane splitPaneKlein;
	private JScrollPane CNCStatusScroll;
	private JSlider Ma�stabSld;
	private JLabel lblMa�stab;
	private JCheckBox FahrwegAu�enChk;
	private JSeparator separator;
	private JLabel lblX;
	private JLabel lblY;
	private JLabel lblZ;
	private JLabel lblCncPosition;
	private JSeparator separator_2;
	private JLabel lblFr�sen;	
	private JScrollPane FehlerScroll;	
	private JPopupMenu popupMenu;
	private JMenuItem L�schenItem;
	private JScrollPane EditorScroll;
	
	public JTextArea Editor;	
	public JButton Fr�senStartenBtn;
	public JButton N�chsterBtn;
	public JCheckBox BisNChk;
	public JNumberField BisN;	
	public JCheckBox AbNChk;
	public JNumberField AbN;
	public JTabbedPane Konsole;
	public JTextArea CNCStatus;	
	public JTextArea Fehler;
	public JProgressBar FortschrittBar;
	public Segment7Anzeige xSegment;
	public Segment7Anzeige ySegment;
	public Segment7Anzeige zSegment;
	public JButton NotAusBtn;
	
	//Subklassen
	private Fr�serSimulationsEngine Fr�serEngine;
	private Fr�serUpdate Fr�serUpdate;
	private RS232_Empfangen RS232_Empfangen;
	private Fr�skoordinator Fr�skoordinator;
	private Thread Fr�serEngineThread;
	private Thread Fr�serUpdateThread;
	private Thread RS232_EmpfangenThread;
	private Thread Fr�skoordinatorThread;
	
	//private Variablen
	private double ma�stab=1;
	private final double Pixelbreite = 0.24;	
	private static final double Pi = 3.14;	
	private int UrsprungAbsolutX=0;		//Koordinaten des Turtle-Ursprungs
	private int UrsprungAbsolutY=0;		//Koordinaten des Turtle-Ursprungs	
	private int k;	//Z�hlvariable f�r Beenden eines Threads	
	private boolean Fr�senGestartet = false;
	
	//�ffentliche Variablen
	public CNC_Steuerung parent;
	public String Code;
	public String[] CodeSplit;
	public String[][] Werkzeugdaten;	
	public boolean NeueDatenFlag = false;
	public boolean DatenFehlerhaftFlag = false;
	public boolean EinzelschrittFr�sen = false;
	public boolean N�chsterClicked = false;
	public boolean NotAusBet�tigt = false;
	public boolean SpindelAn=false;
	public boolean AbbruchDialogAnzeigen = true;
	/**
	 * CNC Status
	 * 90: CNC Empfangsbereit
	 * 91: CNC nicht Empfangsbereit
	 * 92: CNC Daten fehlerhaft
	 * 93: CNC Schritt beendet
	 * 94: CNC Error - Fr�svorgang beenden
	 */
	public int CNCStatusInt;
	public int AnzahlWiederholungen = 0;
	public int Fr�seError = 0;
	public int N=0, G=0;
	public int Tool=1;
	public int Korrektur=0; //0=keine Radiuskorrektur 1=links 2=rechts
	public int Ma�=0; //0=Absolutma� 1=Relativma�
	public int Fortschritt = 0;
	public double X=0, Y=0, I=0, J=0;
	public double XPos=0, YPos=0, ZPos=0, APos=0;
	public double Vorschub=0;
	public double Drehzahl=0;	
	
	//Serieller Port
	public CommPortIdentifier SeriellerPort_ID = null;
	public SerialPort SeriellerPort = null;
	public int Baudrate;
	public int DatenBits;
	public int StopBits;
	public int Flusskontrolle;
			
	public void run() {
		setMaximumSize(new Dimension(1500, 830));
		setMinimumSize(new Dimension(1100, 830));
		setBackground(new Color(240, 240, 240));
		setIconImage(Toolkit.getDefaultToolkit().getImage(Simulator.class.getResource("/resources/Logo.jpg")));
		setBounds(parent.getLocationOnScreen().x, parent.getLocationOnScreen().y, 1500, 830);
		setTitle("MF70 CNC - Fr�svorgang starten");
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				ThreadBeenden(Fr�serEngineThread);
				ThreadBeenden(Fr�serUpdateThread);
				ThreadBeenden(Fr�skoordinatorThread);
				
				System.setOut(parent.TextStream);
				System.setErr(parent.ErrorStream);
				dispose();
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
		    public void componentResized(ComponentEvent e) {
				splitPaneGanzGro�.setBounds(0, 0, getWidth() - 16, getHeight() - 38);
		    	splitPaneGanzGro�.setDividerLocation(getWidth() - 1100);
		    	splitPaneGro�.setBounds(0, 0, 1094, getHeight() - 38);
				splitPaneGro�.setDividerLocation(800);
		    }
			@Override
			public void componentShown(ComponentEvent arg0) {
				if (VerbindungTesten(SeriellerPort_ID, Baudrate, DatenBits, StopBits, Flusskontrolle)) {
					System.out.println("Verbindung zu MF70 erfolgreich hergestellt.");
					Aufbau();
				} else {
					System.err.println(getClass() + " Verbindung zur MF70 konnte nicht hergestellt werden. Bitte Verbindungseinstellungen �berpr�fen.");
					setVisible(false);
				}
			}
		});
		
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setSize(getWidth()-6, getHeight()-28);
		setContentPane(contentPane);
		
		splitPaneGanzGro� = new JSplitPane();
		splitPaneGanzGro�.setEnabled(false);
		splitPaneGanzGro�.setBounds(0, 0, 1484, 792);
		splitPaneGanzGro�.setDividerLocation(400);
		contentPane.add(splitPaneGanzGro�);
		
		splitPaneGro� = new JSplitPane();
		splitPaneGro�.setEnabled(false);
		splitPaneGro�.setBounds(0, 0, 1094, 762);
		splitPaneGro�.setDividerLocation(800);
		splitPaneGanzGro�.setRightComponent(splitPaneGro�);
		
		
		ParameterPanel = new JPanel();
		splitPaneGro�.setRightComponent(ParameterPanel);
		ParameterPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("36px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("36px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("36px"),
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.UNRELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		lblCncPosition = new JLabel("CNC Position");
		lblCncPosition.setFont(new Font("Tahoma", Font.PLAIN, 14));
		ParameterPanel.add(lblCncPosition, "2, 2, 11, 1, center, center");
		
		lblX = new JLabel("X:");
		lblX.setFont(new Font("Tahoma", Font.PLAIN, 30));
		ParameterPanel.add(lblX, "2, 4, center, center");
		
		xSegment = new Segment7Anzeige();
		xSegment.setValue(-1234.56);
		xSegment.setForeground(Color.GREEN);
		xSegment.setBackground(Color.DARK_GRAY);
		ParameterPanel.add(xSegment, "4, 4, 9, 1, fill, center");
		
		lblY = new JLabel("Y:");
		lblY.setFont(new Font("Tahoma", Font.PLAIN, 30));
		ParameterPanel.add(lblY, "2, 6, center, center");
		
		ySegment = new Segment7Anzeige();
		ySegment.setValue(-1234.56);
		ySegment.setForeground(Color.GREEN);
		ySegment.setBackground(Color.DARK_GRAY);
		ParameterPanel.add(ySegment, "4, 6, 9, 1, fill, center");
		
		lblZ = new JLabel("Z:");
		lblZ.setFont(new Font("Tahoma", Font.PLAIN, 30));
		ParameterPanel.add(lblZ, "2, 8, center, center");
		
		zSegment = new Segment7Anzeige();
		zSegment.setValue(-1234.56);
		zSegment.setForeground(Color.GREEN);
		zSegment.setBackground(Color.DARK_GRAY);
		ParameterPanel.add(zSegment, "4, 8, 9, 1, fill, fill");
		
		separator_2 = new JSeparator();
		ParameterPanel.add(separator_2, "2, 10, 11, 1");
		
		lblNewLabel_2 = new JLabel("Werkst�ckdimensionen");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		ParameterPanel.add(lblNewLabel_2, "2, 12, 11, 1, center, default");
		
		lblNewLabel_3 = new JLabel("Kante oben links");
		ParameterPanel.add(lblNewLabel_3, "2, 14, 3, 1");
		
		lblObenLinksX = new JLabel("X:");
		ParameterPanel.add(lblObenLinksX, "2, 16, right, default");
		
		ObenLinksXNum = new JNumberField();
		ObenLinksXNum.setText("0");
		ParameterPanel.add(ObenLinksXNum, "4, 16, fill, default");
		ObenLinksXNum.setColumns(10);
		
		lblObenLinksY = new JLabel("Y:");
		ParameterPanel.add(lblObenLinksY, "8, 16, right, default");
		
		ObenLinksYNum = new JNumberField();
		ObenLinksYNum.setText("100");
		ParameterPanel.add(ObenLinksYNum, "10, 16, fill, default");
		ObenLinksYNum.setColumns(10);
		
		lblL�ngeDx = new JLabel("L\u00E4nge dX:");
		ParameterPanel.add(lblL�ngeDx, "2, 18, right, default");
		
		L�ngeNum = new JNumberField();
		L�ngeNum.setText("160");
		ParameterPanel.add(L�ngeNum, "4, 18, fill, default");
		L�ngeNum.setColumns(10);
		
		lblBreiteDy = new JLabel("Breite dY:");
		ParameterPanel.add(lblBreiteDy, "2, 20, right, default");
		
		BreiteNum = new JNumberField();
		BreiteNum.setText("100");
		ParameterPanel.add(BreiteNum, "4, 20, fill, default");
		BreiteNum.setColumns(10);
		
		lblH�heDz = new JLabel("H�he dZ:");
		ParameterPanel.add(lblH�heDz, "2, 22, right, default");
		
		H�heNum = new JNumberField();
		H�heNum.setText("20");
		ParameterPanel.add(H�heNum, "4, 22, fill, default");
		H�heNum.setColumns(10);
		
		�bernehmenBtn = new JButton("�bernehmen");
		�bernehmenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				KomponentenZeichnen();
				NeuZeichnen();
			}
		});
		ParameterPanel.add(�bernehmenBtn, "7, 24, 6, 1");
		
		separator_1 = new JSeparator();
		ParameterPanel.add(separator_1, "2, 26, 11, 1");
		
		lblAnzeige = new JLabel("Anzeige");
		lblAnzeige.setFont(new Font("Tahoma", Font.PLAIN, 14));
		ParameterPanel.add(lblAnzeige, "2, 28, 11, 1, center, default");
		
		KoordinatenachsenChk = new JCheckBox("Koordinatenachsen anzeigen");
		KoordinatenachsenChk.setSelected(true);
		KoordinatenachsenChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				NeuZeichnen();
			}
		});		
		ParameterPanel.add(KoordinatenachsenChk, "2, 30, 9, 1, left, default");
		
		FahrwegAu�enChk = new JCheckBox("Fahrweg au�en anzeigen");		
		FahrwegAu�enChk.setSelected(true);
		FahrwegAu�enChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				NeuZeichnen();
			}
		});	
		ParameterPanel.add(FahrwegAu�enChk, "2, 32, 9, 1");
		
		lblMa�stab = new JLabel("Ma\u00DFstab: 1:1");
		ParameterPanel.add(lblMa�stab, "2, 34, 9, 1");
		
		Ma�stabSld = new JSlider();
		Ma�stabSld.setSnapToTicks(true);
		Ma�stabSld.setPaintTicks(true);
		Ma�stabSld.setMinimum(5);
		Ma�stabSld.setMaximum(95);
		Ma�stabSld.setMajorTickSpacing(5);
		Ma�stabSld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {				
				if (Ma�stabSld.getValue() == 50.0) {
					ma�stab = 1.0;				
				} else if (Ma�stabSld.getValue() > 50.0) {
					ma�stab = (Ma�stabSld.getValue() - 45.0) / 20.0 + 0.75;
				} else if (Ma�stabSld.getValue() < 50.0) {
					ma�stab = Ma�stabSld.getValue()	/ 50.0;			
				}				
				lblMa�stab.setText("Ma�stab: 1:" + ma�stab);
				KomponentenZeichnen();
				if (!Ma�stabSld.getValueIsAdjusting()) {
					NeuZeichnen();
				}
			}
		});
		ParameterPanel.add(Ma�stabSld, "2, 36, 11, 1");
		
		separator = new JSeparator();
		ParameterPanel.add(separator, "2, 40, 11, 1");
		
		lblFr�sen = new JLabel("Fr\u00E4sen");
		lblFr�sen.setFont(new Font("Tahoma", Font.PLAIN, 14));
		ParameterPanel.add(lblFr�sen, "2, 42, 11, 1, center, default");
		
		NotAusBtn = new JButton("");
		NotAusBtn.setIcon(new ImageIcon(Fr�ser.class.getResource("/resources/Notaus_Gr\u00FCn_klein.png")));
		NotAusBtn.setForeground(Color.BLACK);
		NotAusBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (NotAusBet�tigt) {
					NotAusBtn.setIcon(new ImageIcon(Fr�ser.class.getResource("/resources/Notaus_Gr�n_klein.png")));
					NotAusBet�tigt = false;
					Pause(false);
				} else {
					NotAusBtn.setIcon(new ImageIcon(Fr�ser.class.getResource("/resources/Notaus_Rot_klein.png")));
					NotAusBet�tigt = true;
					Pause(true);
				}
			}
		});
		ParameterPanel.add(NotAusBtn, "10, 44, 3, 6, fill, center");
		
		AbNChk = new JCheckBox("Ab N");
		AbNChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				Fr�sabbruch(true);
			}
		});
		ParameterPanel.add(AbNChk, "2, 44, 2, 1");
		
		AbN = new JNumberField();
		ParameterPanel.add(AbN, "4, 44, fill, default");
		AbN.setColumns(10);

		Fr�senStartenBtn = new JButton("Fr�sen starten");
		Fr�senStartenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Fr�senGestartet) {
					Fr�sabbruch(false);
					Fr�senStartenBtn.setText("Fr�sen starten");
					N�chsterBtn.setText("Einzelschritte");
					
				} else {
					Fr�senGestartet = true;
					Fr�senStartenBtn.setText("Fr�svorgang beenden");
					Start();
				}
				
			}
		});
		ParameterPanel.add(Fr�senStartenBtn, "2, 48, 3, 1");
		
		BisNChk = new JCheckBox("bis N");
		BisNChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				Fr�sabbruch(true);
			}
		});
		ParameterPanel.add(BisNChk, "2, 46, 2, 1, left, default");
		
		BisN = new JNumberField();
		ParameterPanel.add(BisN, "4, 46, fill, default");
		BisN.setColumns(10);
				
		N�chsterBtn = new JButton("Einzelschritte");
		N�chsterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Fr�senGestartet) {
					if (N�chsterBtn.getForeground() == Color.green) {
						N�chsterClicked = true;
					}
				} else {
					Fr�senGestartet = true;
					EinzelschrittFr�sen = true;
					Fr�senStartenBtn.setText("Fr�sen beenden");
					N�chsterBtn.setText("N�chster Schritt");
					Start();
				}
			}
		});
		ParameterPanel.add(N�chsterBtn, "2, 50, 3, 1");
		
		FortschrittBar = new JProgressBar();
		FortschrittBar.setStringPainted(true);
		ParameterPanel.add(FortschrittBar, "2, 52, 11, 1");
		
		splitPaneKlein = new JSplitPane();
		splitPaneKlein.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneKlein.setEnabled(false);
		splitPaneKlein.setBounds(0, 0, contentPane.getWidth() * 2 / 3,contentPane.getHeight() - 3);
		splitPaneKlein.setDividerLocation(600);
		splitPaneGro�.setLeftComponent(splitPaneKlein);		
		
		TurtlePanel = new JPanel();
		FlowLayout TurtlePanelLayout = (FlowLayout) TurtlePanel.getLayout();
		TurtlePanelLayout.setAlignOnBaseline(true);
		TurtlePanelLayout.setAlignment(FlowLayout.LEFT);
		TurtlePanelLayout.setVgap(0);
		TurtlePanelLayout.setHgap(0);
		splitPaneKlein.setLeftComponent(TurtlePanel);
		
		Konsole = new JTabbedPane(JTabbedPane.TOP);
		splitPaneKlein.setRightComponent(Konsole);
		
		CNCStatusScroll = new JScrollPane();
		CNCStatusScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Konsole.addTab("CNC", null, CNCStatusScroll, null);
		
		CNCStatus = new JTextArea();
		CNCStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
		CNCStatusScroll.setViewportView(CNCStatus);
		
		t = new Turtle(796,598);
		t.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				Fr�serEngine.Pause = true;
				try {
					Thread.currentThread();
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				t.setOrigin(arg0.getX(), arg0.getY());
				UrsprungAbsolutX = arg0.getX();
				UrsprungAbsolutY = arg0.getY();
				KomponentenZeichnen();
			}
		});
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Fr�serEngine.Pause = true;
				try {
					Thread.currentThread();
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				t.setOrigin(arg0.getX(), arg0.getY());
				UrsprungAbsolutX = arg0.getX();
				UrsprungAbsolutY = arg0.getY();
				NeuZeichnen();
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				NeuZeichnen();
			}
		});
		t.setBackground(Color.white);
		t.setForeground(Color.black);
		TurtlePanel.add(t);
		t.setOrigin(t.getWidth()/2, t.getHeight()/2);
		UrsprungAbsolutX = t.getWidth()/2;
		UrsprungAbsolutY = t.getHeight()/2;
		
		FehlerScroll = new JScrollPane();
		Konsole.addTab("Fehler", null, FehlerScroll, null);
		
		Fehler = new JTextArea();
		Fehler.setForeground(Color.RED);
		FehlerScroll.setViewportView(Fehler);
		
		popupMenu = new JPopupMenu();
		addPopup(Fehler, popupMenu);
		
		L�schenItem = new JMenuItem("L�schen");
		L�schenItem.setIcon(new ImageIcon(Fr�ser.class.getResource("/resources/Delete.png")));
		L�schenItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Fehler.setText("");
			}
		});
		popupMenu.add(L�schenItem);
		
		EditorScroll = new JScrollPane();
		splitPaneGanzGro�.setLeftComponent(EditorScroll);
		
		Editor = new JTextArea();
		Editor.setText(Code);
		EditorScroll.setViewportView(Editor); 
		
		if (VerbindungTesten(SeriellerPort_ID, Baudrate, DatenBits, StopBits, Flusskontrolle)) {
			System.out.println("Verbindung zu MF70 erfolgreich hergestellt.");
			Aufbau();
			
		} else {
			System.err.println(getClass() + " Verbindung zur MF70 konnte nicht hergestellt werden. Bitte Verbindungseinstellungen �berpr�fen.");
			setVisible(false);
		}
	}
		
	private void Aufbau() {
		//�ffentliche Variablen resetten
		NeueDatenFlag = false;
		DatenFehlerhaftFlag = false;
		EinzelschrittFr�sen = false;
		N�chsterClicked = false;
		NotAusBet�tigt = false;
		SpindelAn=false;
		AbbruchDialogAnzeigen = true;
		CNCStatusInt = 0;
		AnzahlWiederholungen = 0;
		Fr�seError = 0;
		N=0; G=0;
		Tool=1;
		Korrektur=0; //0=keine Radiuskorrektur 1=links 2=rechts
		Ma�=0; //0=Absolutma� 1=Relativma�
		Fortschritt = 0;		
		X=0; Y=0; I=0; J=0;
		XPos=0; YPos=0; ZPos=0; APos=0;
		Vorschub=0;
		Drehzahl=0;	
		
		Fr�serUpdate = new Fr�serUpdate(this);
		Fr�serUpdateThread = new Thread(Fr�serUpdate);
		Fr�serUpdateThread.setName("Fr�serUpdateThread");
				
		Fr�serEngine = new Fr�serSimulationsEngine(CodeSplit, t, ma�stab, FahrwegAu�enChk.isSelected());
		Fr�serEngineThread = new Thread(Fr�serEngine);
		Fr�serEngineThread.setName("Fr�serEngineThread");
		
		Fr�skoordinator = new Fr�skoordinator(this, Fr�serEngine, Fr�serEngineThread, Fr�serUpdateThread);
		Fr�skoordinatorThread = new Thread(Fr�skoordinator);
		Fr�skoordinatorThread.setName("Fr�skoordinatorThread");

		//Seriellen Port initialisieren
		if (SeriellerPort_ID != null) {
	    	try {	//Seriellen Port �ffnen
		    	SeriellerPort = (SerialPort) SeriellerPort_ID.open("Java RX/TX RS232", 10000);
		    } catch(PortInUseException e) {
		        System.err.println(e.getClass() + " Port " + SeriellerPort_ID.getName() + " wird bereits von einem anderen Programm verwendet");
		    }
		    
		    try {	//Port-Parameter setzen
		    	SeriellerPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				System.err.println(e.getClass() + " Portparameter konnten nicht gesetzt werden.");
			}
	    } else {
	    	System.err.println(getClass() + "kein Serieller Port gew�hlt.");
	    }
		
		if (SeriellerPort != null) {
			RS232_Empfangen = new RS232_Empfangen(this, SeriellerPort);
			RS232_EmpfangenThread = new Thread(RS232_Empfangen);
			
			//Seriellen Listener hinzuf�gen
			try {
				SeriellerPort.addEventListener(new SerialPortEventListener() {
					public void serialEvent(SerialPortEvent e) {
						if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
							RS232_EmpfangenThread.start();
						}					
					}
				});
			} catch (TooManyListenersException e) {
				e.printStackTrace();
			}			
			SeriellerPort.notifyOnDataAvailable(true);			
		}
		
		
		//TextStream und ErrorStream umleiten
		PrintStream TextStream = new PrintStream(System.out) {
			public void print(String s) {
				try {
					Fehler.getDocument().insertString(Fehler.getDocument().getLength(), s + "\n", StyleConstant.BLACK);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
		    }		     
		};
		
		PrintStream ErrorStream = new PrintStream(System.out) {
			public void print(String s) {
				try {
					Konsole.setSelectedIndex(1);
					Fehler.getDocument().insertString(Fehler.getDocument().getLength(), s + "\n", StyleConstant.RED);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
		    }		     
		};
		System.setOut(TextStream);
		System.setErr(ErrorStream);
		
		KomponentenZeichnen();
	}
	
	private void Fr�sabbruch(boolean ParameterGe�ndert) {
		if (Fr�senGestartet) {
			if (AbbruchDialogAnzeigen) {
				if (JOptionPane.showOptionDialog(null, "Fr�svorgang abbrechen?","Best�tigen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
						null, new String[]{"Ja", "Nein"}, "Nein") == JOptionPane.YES_OPTION) {
					ThreadBeenden(Fr�skoordinatorThread);
					ThreadBeenden(Fr�serEngineThread);
					ThreadBeenden(Fr�serUpdateThread);
					N�chsterBtn.setForeground(Color.black);
					N�chsterBtn.setText("Einzelschritte");
					Fr�senStartenBtn.setText("Fr�senStarten");
					CNCStatus.append("Fr�svorgang abgebrochen" + "\n");
				}
			} else {
				ThreadBeenden(Fr�skoordinatorThread);
				ThreadBeenden(Fr�serEngineThread);
				ThreadBeenden(Fr�serUpdateThread);
				N�chsterBtn.setForeground(Color.black);
				N�chsterBtn.setText("Einzelschritte");
				Fr�senStartenBtn.setText("Fr�senStarten");
				CNCStatus.append("Fr�svorgang abgebrochen" + "\n");
				AbbruchDialogAnzeigen = true;
			}
						
			if (ParameterGe�ndert) {			
				Aufbau();
			}
			Fr�senGestartet=false;
		}		
	}
		
	private void Koordinatenachsen() {	
		if (KoordinatenachsenChk.isSelected()) {
			t.setForeground(Color.black);
			t.moveto(0, t.getHeight()*(-1));
			t.drawto(0, t.getHeight());
			t.moveto(t.getWidth()*(-1), 0);
			t.drawto(t.getWidth(), 0);
		}		
	}
		
	private void Werkst�ck() {
		t.setForeground(Color.LIGHT_GRAY);
		t.myBufferedGraphics.fillRect((int) Pixel(ObenLinksXNum.getDouble()) + UrsprungAbsolutX, (int) -Pixel(ObenLinksYNum.getDouble()) + UrsprungAbsolutY, (int) Pixel(L�ngeNum.getDouble()), (int) Pixel(BreiteNum.getDouble()));
	}
		
	private double Pixel(double Strecke) {
		double pixel = Strecke * ma�stab / Pixelbreite;
		return pixel;
	}
		
	private void KomponentenZeichnen() {	
		if (!Fr�serEngine.Pause) {
			Fr�serEngine.Pause = true;
			try {
				Thread.currentThread();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		t.drawDynamic=false;
		t.clear();
		Werkst�ck();
		Koordinatenachsen();
		
		Fr�serEngine.Pause = false;
	}
		
	private void NeuZeichnen() {
		if (!Fr�serEngine.Pause) {
			Fr�serEngine.Pause = true;	
			try {
				Thread.currentThread();
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		t.drawDynamic=false;
		t.clear();
		Werkst�ck();
		Koordinatenachsen();
		
		Fr�serEngine.Code = CodeSplit;
		Fr�serEngine.ma�stab = ma�stab;	
		Fr�serEngine.FahrwegAu�en = FahrwegAu�enChk.isSelected();		
		Fr�serEngine.AktuellesN=0;
		if (AbNChk.isSelected()) {
			Fr�serEngine.AbNZeichnen=AbN.getInt();
		} else {
			Fr�serEngine.AbNZeichnen = 0;
		}
		
		Fr�serEngine.Pause = false;
	}
		
	private void Start() {
		if (!Fr�skoordinatorThread.isAlive() && !Fr�serEngineThread.isAlive() && !Fr�serUpdateThread.isAlive()) {
			Fr�skoordinatorThread.start();
			Fr�serEngineThread.start();
			Fr�serUpdateThread.start();
			if (BisNChk.isSelected()) {
				EinzelschrittFr�sen = true;
			}
		} else {
			System.err.println(getClass() + "Fr�svorgang konnte nicht gestartet werden, da die Threads noch aktiv sind.");
		}		
	}
	
	private void Pause(boolean Stop) {
		Fr�skoordinator.Pause = Stop;
		Fr�serEngine.Pause = Stop;
	}

	private boolean VerbindungTesten(CommPortIdentifier SeriellerPort_ID, int Baudrate, int DatenBits, int StopBits, int Flusskontrolle) {		
		SerialPort SeriellerPort = null;
		boolean Erfolg = false;
		
		if (SeriellerPort_ID != null) {
	    	try {	//Seriellen Port �ffnen
		    	SeriellerPort = (SerialPort) SeriellerPort_ID.open("Java RX/TX RS232", 10000);
		    } catch(PortInUseException e) {
		        System.err.println(e.getClass() + " Port " + SeriellerPort_ID.getName() + " wird bereits von einem anderen Programm verwendet");
		    }
		    
		    try {	//Port-Parameter setzen
		    	SeriellerPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				System.err.println(e.getClass() + " Portparameter konnten nicht gesetzt werden.");
			}
	    }
		
		if (SeriellerPort != null) {	//Bytes senden, empfangen & Seriellen Port schlie�en
			RS232_SendenPr�f(SeriellerPort, 123456,99,1234.56,1234.56,1234.56,1234.56);
			try {
				Thread.currentThread();
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			if (RS232_Empfangen(SeriellerPort)) {
				Erfolg = true;
			} else {
				Erfolg = false;				
			}			
			
			SeriellerPort.close();			
		}	
		return Erfolg;
	}
		
	private boolean RS232_Empfangen(SerialPort SeriellerPort) {
		boolean Erfolg = false;
		
		BufferedInputStream Eingangsdaten = null;
		byte[] temp = new byte[17];
		int[] tempInt = new int[17];
	    
	    try {
	    	Eingangsdaten = new BufferedInputStream(SeriellerPort.getInputStream(), 30);
	    } catch (IOException e) {
	    	System.err.println(e.getClass() + " InputStream konnte nicht ge�ffnet werden.");
	    }
	    if (Eingangsdaten != null) { 
	    	//Anzahl der neuen Bytes feststellen
	    	int n=0;
			try {
				n = Eingangsdaten.available();
			} catch (IOException e) {
				System.err.println(e.getClass() + " Anzahl der neuen Bytes konnte nicht �berpr�ft werden.");
			}
			
			if (n!=0) {
				
				//neue Bytes einlesen
				try {
					Eingangsdaten.read(temp);
				} catch (IOException e) {
					System.err.println(e.getClass() + " Neue Bytes konnten nicht eingelesen werden.");
				}
				
		    	if (n==17) {	//korrekte Anzahl an Datenbytes empfangen
		    		//neues Bytes zu Integer konvertieren
		    		for (int i=0; i<temp.length; i++) {
		    			tempInt[i] = temp[i];
		    		}
		    		
		    		//Pr�fsumme berechnen
		    		double Pr�fsummeDbl = 0;
		    		for (int i=0; i<16; i++) {
		    			Pr�fsummeDbl = Pr�fsummeDbl + tempInt[i] * Math.pow(Pi, (i / 10.0) - 1.0);
		    		}

		    		int Pr�fsummeInt = (int) (Pr�fsummeDbl / 6.0 - 128.0);
		    		if (Pr�fsummeInt<0) {
		    			Pr�fsummeInt = -Pr�fsummeInt;
		    		}
		    		
		    		
		    		if (Pr�fsummeInt == tempInt[16]) {	//Pr�fsummen vergleichen
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
			            	System.err.println("Verbindungstest: Empfangene Daten stimmen nicht mit den gesendeten �berein. Bitte Verbindungseinstellungen �berpr�fen.");
			            }
			            
		    		}
		    	}
			}
	    		    	
	    	try {	//InputStream schlie�en
				Eingangsdaten.close();
			} catch (IOException e) {
				System.err.println(e.getClass() + " InputStream konnte nicht geschlossen werden.");
			}	    	
	    }
	    return Erfolg;
	}
	
	private void RS232_SendenPr�f(SerialPort Port, int nInt, int gInt, double xDbl, double yDbl, double iDbl, double jDbl) {
		OutputStream Ausgangsdaten = null;
		
		int nInt1=0, nInt2=0, nInt3=0, xInt1=0, xInt2=0, xInt3=0, yInt1=0, yInt2=0, yInt3=0, iInt1=0, iInt2=0, iInt3=0, jInt1=0, jInt2=0, jInt3=0;
		boolean ZahlenOK = true;
		
		if (nInt <=999999 && nInt > 0) {
			nInt1 = (int) (nInt / 10000);
			nInt2 = (int) ((nInt - nInt1 * 10000) / 100);
			nInt3 = nInt - nInt1 * 10000 - nInt2 * 100;
		} else {
			System.err.println(getClass() + " N: falsches Zahlenformat");
			ZahlenOK = false;
		}
		
		if (gInt > 255 && gInt >= 0) {
			System.err.println(getClass() + " G: falsches Zahlenformat");
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
			System.err.println(getClass() + " X: falsches Zahlenformat");
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
			System.err.println(getClass() + " Y: falsches Zahlenformat");
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
			System.err.println(getClass() + " I: falsches Zahlenformat");
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
			System.err.println(getClass() + " J: falsches Zahlenformat");
			ZahlenOK = false;
		}
		
		if (ZahlenOK) {
			int[] Zahlen = {nInt1, nInt2, nInt3, gInt, xInt1, xInt2, xInt3, yInt1, yInt2, yInt3, iInt1, iInt2, iInt3, jInt1, jInt2, jInt3};
			
			int [] ZahlenPr�f = Pr�fsumme(Zahlen);
			
			try {
				Ausgangsdaten = Port.getOutputStream();
			} catch (IOException e) {
				System.err.println(e.getClass() + " OutputStream konnte nicht ge�ffnet werden.");
			}
			try {
				for (int i=0; i<ZahlenPr�f.length; i++) {
					Ausgangsdaten.write(ZahlenPr�f[i]);
				}
			} catch (IOException e) {
				System.err.println(e.getClass() + " Bytes konnten nicht gesendet werden.");
			}
			try {
				Ausgangsdaten.flush();
			} catch (IOException e) {
				System.err.println(e.getClass() + " OutputStream konnte nicht geflusht werden.");
			}
			try {
				Ausgangsdaten.close();
			} catch (IOException e) {
				System.err.println(e.getClass() + " OutputStream konnte nicht geschlossen werden.");
			}
		}		
	}
	
	private int[] Pr�fsumme (int[] Zahlen) {
		double Pr�fsummeDbl = 0;
		for (int i=0; i<16; i++) {
			Pr�fsummeDbl = Pr�fsummeDbl + Zahlen[i] * Math.pow(Pi, (i / 10.0) - 1.0);
		}

		int [] ZahlenPr�f = new int[17];
		for (int i =0; i<Zahlen.length; i++) {
			ZahlenPr�f[i] = Zahlen[i];
		}
		ZahlenPr�f[16] = (int) (Pr�fsummeDbl / 6.0 - 128.0);
		if (ZahlenPr�f[16]<0) {
			ZahlenPr�f[16] = -ZahlenPr�f[16];
		}
		return ZahlenPr�f;
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

	private void ThreadBeenden(Thread ThreadToKill) {
		k=0;
		while (ThreadToKill.isAlive()) {
			ThreadToKill.interrupt();
			k++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			if (k==5) {
				System.out.println("ThreadInterrupt " + k + " fehlgeschlagen");
				break;
			}
		}
	}

}
