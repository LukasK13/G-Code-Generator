package lk1311;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Toolkit;

public class Befehle extends JDialog{
	
	private JScrollPane BefehlstabellePanel;
	private JTable Befehlstabelle;
	
	public Befehle (JFrame parent) {
		super(parent,"G-Code Befehle", false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Befehle.class.getResource("/resources/Logo.jpg")));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				BefehlstabellePanel.setSize(getWidth()-16, getHeight()-38);
				Befehlstabelle.setSize(getWidth()-16, getHeight()-38);
				BefehlstabellePanel.repaint();
				repaint();
			}
		});
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 465);	
		getContentPane().setLayout(null);
		
		BefehlstabellePanel = new JScrollPane();
		BefehlstabellePanel.setBounds(0, 0, getWidth()-16, getHeight()-38);		
		getContentPane().add(BefehlstabellePanel);
		
		Befehlstabelle = new JTable();
		Befehlstabelle.setModel(new DefaultTableModel(
			new Object[][] {
				{"G00", "X, Y, Z", "Lineare Interpolation zu (X|Y) / (Z) im Eilgang"},
				{"G01", "X, Y, Z", "Lineare Interpolation zu (X|Y) / (Z) in normaler Geschwindigkeit"},
				{null, null, null},
				{"G02", "X, Y, I, J", "Kreisbogen vom (I|J) zu (X|Y) im Uhrzeigersinn"},
				{"G03", "X, Y, I, J", "Kreisbogen vom (I|J) zu (X|Y) im Gegenuhrzeigersinn"},
				{"G04", "I, J, W", "Kreisbogen um (I|J) mit dem Winkel R im Uhrzeigersinn"},
				{"G05", "I, J, W", "Kreisbogen um (I|J) mit dem Winkel R im Gegenuhrzeigersinn"},
				{null, null, null},
				{"G40", "-", "Keine Werkzeugbahnkorrektur"},
				{"G41", "-", "Werkzeugbahnkorrektur in Vorschubrichtung links"},
				{"G42", "-", "Werkzeugbahnkorrektur in Vorschubrichtung rechts"},
				{null, null, null},
				{"G72", "N, Anzahl Wdh.", "Zyklus von ab N bis Aufrufer mit Anzahl Wdh."},
				{"", null, null},
				{"G90", "-", "Setzt die Bema\u00DFung auf Absolutma\u00DF"},
				{"G91", "-", "Setzt die Bema\u00DFung auf Relativma\u00DF"},
				{null, null, null},
				{"M00", "-", "Programmhalt"},
				{"M02", "-", "Programmende"},
				{"M03", "-", "Spindel ein"},
				{"M05", "-", "Spindel aus"},
				{null, null, null},
				{"F", "Vorschubgeschwindigkeit", "Setzt die Vorschubgeschwindigkeit"},
				{null, null, null},
				{"S", "Spindeldrehzahl", "Setzt die Spindeldrehzahl"},
			},
			new String[] {
				"Befehl", "Argumente", "Beschreibung"
			}
		));
		Befehlstabelle.getColumnModel().getColumn(0).setPreferredWidth(50);
		Befehlstabelle.getColumnModel().getColumn(0).setMinWidth(25);
		Befehlstabelle.getColumnModel().getColumn(0).setMaxWidth(50);
		Befehlstabelle.getColumnModel().getColumn(1).setPreferredWidth(125);
		Befehlstabelle.getColumnModel().getColumn(1).setMinWidth(125);
		Befehlstabelle.getColumnModel().getColumn(1).setMaxWidth(140);
		Befehlstabelle.getColumnModel().getColumn(2).setPreferredWidth(311);
		Befehlstabelle.getColumnModel().getColumn(2).setMinWidth(300);
		BefehlstabellePanel.setViewportView(Befehlstabelle);
			
	}
}