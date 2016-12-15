package lk1311;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Simulator extends JFrame implements Runnable{
	//sichtbare Komponenten
	public JFrame parent;
	private JPanel contentPane, TurtlePanel, ParameterPanel;
	private JSplitPane splitPaneGroﬂ;
	private Turtle t;
	private JCheckBox KoordinatenachsenChk;
	private JLabel lblObenLinksX;
	private JNumberField ObenLinksXNum;
	private JLabel lblObenLinksY;
	private JNumberField ObenLinksYNum;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblL‰ngeDx;
	private JLabel lblBreiteDy;
	private JLabel lblHˆheDz;
	private JNumberField L‰ngeNum;
	private JNumberField BreiteNum;
	private JNumberField HˆheNum;
	private JButton ¸bernehmenBtn;
	private JSeparator separator_1;
	private JLabel lblAnzeige;
	private JButton aktualisierenBtn;
	private JSlider VorschubSld;
	private JLabel lblVorschubgeschwindigkeit;
	private JSplitPane splitPaneKlein;
	private JScrollPane CNCStatusScroll;
	private JTextArea CNCStatus;	
	private JSlider MaﬂstabSld;
	private JLabel lblMaﬂstab;
	private JButton Fr‰senStartenBtn;
	private JCheckBox FahrwegAuﬂenChk;
	private JProgressBar Fortschritt;	
	private JButton StopBtn;
	private JSeparator separator;
	
	//Subklassen
	private SimulatorEngine SimulatorEngine;
	private ProgressbarUpdate PbUpdate;
	private Thread SimulatorEngineThread;
	private Thread PbUpdateThread;
	
	//private Variablen
	private double maﬂstab=1;
	private int UrsprungAbsolutX=0;
	private int UrsprungAbsolutY=0;
	private final double Pixelbreite = 0.24;
	private int k;
	
	//ˆffentliche Variablen
	public String[] CodeSplit;
	public  String[][] Werkzeugdaten;
	
	public void run() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Simulator.class.getResource("/resources/Logo.jpg")));
		this.setBounds(parent.getLocationOnScreen().x + 50, parent.getLocationOnScreen().y + 50, 1100, 700);
		setResizable(false);
		setVisible(true);
		setTitle("MF70 CNC - Simulator");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {	
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
				while (SimulatorEngineThread.isAlive()) {
					SimulatorEngineThread.interrupt();
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
				setVisible(false);
			}
		});
		
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setSize(getWidth()-6, getHeight()-28);
		setContentPane(contentPane);
		
		splitPaneGroﬂ = new JSplitPane();
		splitPaneGroﬂ.setEnabled(false);
		splitPaneGroﬂ.setBounds(0, 0, 1094, 672);
		splitPaneGroﬂ.setDividerLocation(850);
		contentPane.add(splitPaneGroﬂ);
		
		
		ParameterPanel = new JPanel();
		splitPaneGroﬂ.setRightComponent(ParameterPanel);
		ParameterPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.UNRELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
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
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		lblNewLabel_2 = new JLabel("Werkst\u00FCckdimensionen");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		ParameterPanel.add(lblNewLabel_2, "2, 2, 7, 1, center, default");
		
		lblNewLabel_3 = new JLabel("Kante oben links");
		ParameterPanel.add(lblNewLabel_3, "2, 4, 3, 1");
		
		lblObenLinksX = new JLabel("X:");
		ParameterPanel.add(lblObenLinksX, "2, 6, right, default");
		
		ObenLinksXNum = new JNumberField();
		ObenLinksXNum.setText("0");
		ParameterPanel.add(ObenLinksXNum, "4, 6, fill, default");
		ObenLinksXNum.setColumns(10);
		
		lblObenLinksY = new JLabel("Y:");
		ParameterPanel.add(lblObenLinksY, "6, 6, right, default");
		
		ObenLinksYNum = new JNumberField();
		ObenLinksYNum.setText("100");
		ParameterPanel.add(ObenLinksYNum, "8, 6, fill, default");
		ObenLinksYNum.setColumns(10);
		
		lblL‰ngeDx = new JLabel("L\u00E4nge dX:");
		ParameterPanel.add(lblL‰ngeDx, "2, 8, right, default");
		
		L‰ngeNum = new JNumberField();
		L‰ngeNum.setText("160");
		ParameterPanel.add(L‰ngeNum, "4, 8, fill, default");
		L‰ngeNum.setColumns(10);
		
		lblBreiteDy = new JLabel("Breite dY:");
		ParameterPanel.add(lblBreiteDy, "2, 10, right, default");
		
		BreiteNum = new JNumberField();
		BreiteNum.setText("100");
		ParameterPanel.add(BreiteNum, "4, 10, fill, default");
		BreiteNum.setColumns(10);
		
		lblHˆheDz = new JLabel("H\u00F6he dZ:");
		ParameterPanel.add(lblHˆheDz, "2, 12, right, default");
		
		HˆheNum = new JNumberField();
		HˆheNum.setText("20");
		ParameterPanel.add(HˆheNum, "4, 12, fill, default");
		HˆheNum.setColumns(10);
		
		¸bernehmenBtn = new JButton("¸bernehmen");
		¸bernehmenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				KomponentenZeichnen();
				SimulationStarten(false);
			}
		});
		ParameterPanel.add(¸bernehmenBtn, "4, 14, 5, 1");
		
		separator_1 = new JSeparator();
		ParameterPanel.add(separator_1, "2, 16, 7, 1");
		
		lblAnzeige = new JLabel("Anzeige");
		lblAnzeige.setFont(new Font("Tahoma", Font.PLAIN, 14));
		ParameterPanel.add(lblAnzeige, "2, 18, 7, 1, center, default");
		
		KoordinatenachsenChk = new JCheckBox("Koordinatenachsen anzeigen");
		KoordinatenachsenChk.setSelected(true);
		KoordinatenachsenChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				KomponentenZeichnen();
				SimulationStarten(false);
			}
		});		
		ParameterPanel.add(KoordinatenachsenChk, "2, 20, 7, 1, left, default");
		
		FahrwegAuﬂenChk = new JCheckBox("Fahrweg auﬂen anzeigen");		
		FahrwegAuﬂenChk.setSelected(true);
		FahrwegAuﬂenChk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				KomponentenZeichnen();
				SimulationStarten(false);
			}
		});	
		ParameterPanel.add(FahrwegAuﬂenChk, "2, 22, 7, 1");
		
		lblMaﬂstab = new JLabel("Ma\u00DFstab: 1:1");
		ParameterPanel.add(lblMaﬂstab, "2, 24, 7, 1");
		
		MaﬂstabSld = new JSlider();
		MaﬂstabSld.setSnapToTicks(true);
		MaﬂstabSld.setPaintTicks(true);
		MaﬂstabSld.setMinimum(5);
		MaﬂstabSld.setMaximum(95);
		MaﬂstabSld.setMajorTickSpacing(5);
		MaﬂstabSld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {				
				if (MaﬂstabSld.getValue() == 50.0) {
					maﬂstab = 1.0;				
				} else if (MaﬂstabSld.getValue() > 50.0) {
					maﬂstab = (MaﬂstabSld.getValue() - 45.0) / 20.0 + 0.75;
				} else if (MaﬂstabSld.getValue() < 50.0) {
					maﬂstab = MaﬂstabSld.getValue()	/ 50.0;			
				}				
				lblMaﬂstab.setText("Maﬂstab: 1:" + maﬂstab);
				KomponentenZeichnen();
				if (!MaﬂstabSld.getValueIsAdjusting()) {
					SimulationStarten(false);
				}
			}
		});
		ParameterPanel.add(MaﬂstabSld, "2, 26, 7, 1");
		
		lblVorschubgeschwindigkeit = new JLabel("Vorschubgeschwindigkeit: 50%");
		ParameterPanel.add(lblVorschubgeschwindigkeit, "2, 28, 7, 1");
		
		VorschubSld = new JSlider();
		VorschubSld.setSnapToTicks(true);
		VorschubSld.setPaintLabels(true);
		VorschubSld.setPaintTicks(true);
		VorschubSld.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblVorschubgeschwindigkeit.setText("Vorschubgeschwindigkeit: " + VorschubSld.getValue() + "%");
				t.sleepTime=201 - VorschubSld.getValue() * 2;
			}
		});		
		ParameterPanel.add(VorschubSld, "2, 30, 7, 1");
		
		aktualisierenBtn = new JButton("aktualisieren");
		aktualisierenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				KomponentenZeichnen();
				SimulationStarten(false);
			}
		});
		ParameterPanel.add(aktualisierenBtn, "4, 32, 5, 1");
		
		separator = new JSeparator();
		ParameterPanel.add(separator, "2, 34, 7, 1");
		
		Fr‰senStartenBtn = new JButton("Simulation starten");
		Fr‰senStartenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				KomponentenZeichnen();
				SimulationStarten(true);
			}
		});
		ParameterPanel.add(Fr‰senStartenBtn, "2, 37, 3, 3");
		
		StopBtn = new JButton("Stop");
		StopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Stop();
			}
		});
		ParameterPanel.add(StopBtn, "6, 38, 3, 1");
		
		Fortschritt = new JProgressBar();
		Fortschritt.setStringPainted(true);
		ParameterPanel.add(Fortschritt, "2, 46, 7, 1");
		
		splitPaneKlein = new JSplitPane();
		splitPaneKlein.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneKlein.setEnabled(false);
		splitPaneKlein.setBounds(0, 0, contentPane.getWidth() * 2 / 3,contentPane.getHeight() - 3);
		splitPaneKlein.setDividerLocation(500);
		splitPaneGroﬂ.setLeftComponent(splitPaneKlein);		
		
		TurtlePanel = new JPanel();
		FlowLayout TurtlePanelLayout = (FlowLayout) TurtlePanel.getLayout();
		TurtlePanelLayout.setAlignOnBaseline(true);
		TurtlePanelLayout.setAlignment(FlowLayout.LEFT);
		TurtlePanelLayout.setVgap(0);
		TurtlePanelLayout.setHgap(0);
		splitPaneKlein.setLeftComponent(TurtlePanel);
		
		JTabbedPane Konsole = new JTabbedPane(JTabbedPane.TOP);
		splitPaneKlein.setRightComponent(Konsole);
		
		CNCStatusScroll = new JScrollPane();
		CNCStatusScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Konsole.addTab("CNC", null, CNCStatusScroll, null);
		
		CNCStatus = new JTextArea();
		CNCStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
		CNCStatusScroll.setViewportView(CNCStatus);
		
		t = new Turtle(846,498);
		t.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
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
				while (SimulatorEngineThread.isAlive()) {
					SimulatorEngineThread.interrupt();
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
				t.setOrigin(arg0.getX(), arg0.getY());
				UrsprungAbsolutX = arg0.getX();
				UrsprungAbsolutY = arg0.getY();
				KomponentenZeichnen();
			}
		});
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
				while (SimulatorEngineThread.isAlive()) {
					SimulatorEngineThread.interrupt();
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
				t.setOrigin(arg0.getX(), arg0.getY());
				UrsprungAbsolutX = arg0.getX();
				UrsprungAbsolutY = arg0.getY();
				KomponentenZeichnen();
				SimulationStarten(false);
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				KomponentenZeichnen();
				SimulationStarten(false);
			}
		});
		t.setBackground(Color.white);
		t.setForeground(Color.black);
		TurtlePanel.add(t);
		t.setOrigin(t.getWidth()/2, t.getHeight()/2);
		UrsprungAbsolutX = t.getWidth()/2;
		UrsprungAbsolutY = t.getHeight()/2;
		t.sleepTime=VorschubSld.getValue();	
		
		SimulatorEngine = new SimulatorEngine();
		SimulatorEngineThread = new Thread (SimulatorEngine);
		SimulatorEngine.t = t;
		SimulatorEngine.CNCStatus = CNCStatus;
		
		PbUpdate = new ProgressbarUpdate();
		PbUpdateThread = new Thread(PbUpdate);		
		
		KomponentenZeichnen();
		SimulationStarten(false);
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
	
	private void Werkst¸ck() {
		t.setForeground(Color.LIGHT_GRAY);
		t.myBufferedGraphics.fillRect((int) Pixel(ObenLinksXNum.getDouble()) + UrsprungAbsolutX, (int) -Pixel(ObenLinksYNum.getDouble()) + UrsprungAbsolutY, (int) Pixel(L‰ngeNum.getDouble()), (int) Pixel(BreiteNum.getDouble()));
	}
	
	private double Pixel(double Strecke) {
		double pixel = Strecke * maﬂstab / Pixelbreite;
		return pixel;
	}
	
	@SuppressWarnings("deprecation")
	private void KomponentenZeichnen() {
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
		while (SimulatorEngineThread.isAlive()) {
			SimulatorEngineThread.interrupt();
			k++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				//e.printStackTrace();
			}
			if (k==5) {
				SimulatorEngineThread.stop();
				System.err.println("ThreadInterrupt " + k + " fehlgeschlagen. Thread.stop() wird ausgef¸hrt");
				break;
			}
		}
	
		t.drawDynamic=false;
		t.clear();
		Werkst¸ck();
		Koordinatenachsen();
	}
	
	@SuppressWarnings("deprecation")
	private void SimulationStarten(boolean Vorschub) {		
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
		while (SimulatorEngineThread.isAlive()) {
			SimulatorEngineThread.interrupt();
			k++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				//e.printStackTrace();
			}
			if (k==5) {
				SimulatorEngineThread.stop();
				System.err.println("ThreadInterrupt " + k + " fehlgeschlagen. Thread.stop() wird ausgef¸hrt");
				break;
			}
		}
		
		t.drawDynamic=Vorschub;
		SimulatorEngine.Pause=false;
		SimulatorEngine.StopBtn = StopBtn;
		StopBtn.setText("Stop");
		
		PbUpdate.FortschrittBar = Fortschritt;
		PbUpdateThread = new Thread(PbUpdate);
		PbUpdateThread.setName("PbUpdateThread");
		PbUpdateThread.start();
		
		SimulatorEngine.Werkzeugdaten = Werkzeugdaten;
		SimulatorEngine.CodeSplit = CodeSplit;
		SimulatorEngine.maﬂstab = maﬂstab;	
		SimulatorEngine.FahrwegAuﬂen = FahrwegAuﬂenChk.isSelected();
		SimulatorEngine.PbUpdate = PbUpdate;
		SimulatorEngine.PbUpdateThread = PbUpdateThread;
		SimulatorEngineThread = new Thread (SimulatorEngine);
		SimulatorEngineThread.setName("SimulatorEngineThread");
		SimulatorEngineThread.start();		
	}
	
	public void Stop() {
		if (!SimulatorEngine.Pause) {
			SimulatorEngine.Pause = true;
			StopBtn.setText("Fortsetzen");
		}
		else {
			SimulatorEngine.Pause = false;
			StopBtn.setText("Stop");
		}
	}
	
}


