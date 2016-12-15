package lk1311;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Fehlercodes extends JDialog{
	
	private JScrollPane FehlertabellePanel;
	private JTable Fehlertabelle;
	
	public Fehlercodes (JFrame parent) {
		super(parent,"G-Code Befehle", false);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				FehlertabellePanel.setSize(getWidth()-16, getHeight()-38);
				Fehlertabelle.setSize(getWidth()-16, getHeight()-38);
				FehlertabellePanel.repaint();
				repaint();
			}
		});
		setResizable(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);	
		getContentPane().setLayout(null);
		
		FehlertabellePanel = new JScrollPane();
		FehlertabellePanel.setBounds(0, 0, getWidth()-16, getHeight()-38);		
		getContentPane().add(FehlertabellePanel);
		
		Fehlertabelle = new JTable();
		Fehlertabelle.setModel(new DefaultTableModel(
			new Object[][] {
				{"E04", "Notaus gedr\u00FCckt"},
				{"E05", "X+ Endschalter"},
				{"E06", "X- Endschalter"},
				{"E07", "Y+ Endschalter"},
				{"E08", "Y- Endschalter"},
				{"E09", "Z+ Endschalter"},
				{"E10", "Z- Endschalter"},
				{"E11", "A+ Endschalter"},
				{"E12", "A- Endschalter"},
				{"E14", "Sonstiger Fehler"},
				{"", ""},
				{null, null},
				{"", ""},
				{"", ""},
				{"", ""},
				{"", ""},
				{null, null},
				{"", ""},
				{null, null},
				{"", ""},
			},
			new String[] {
				"Fehlercode", "Beschreibung"
			}
		));
		Fehlertabelle.getColumnModel().getColumn(0).setPreferredWidth(100);
		Fehlertabelle.getColumnModel().getColumn(0).setMinWidth(50);
		Fehlertabelle.getColumnModel().getColumn(0).setMaxWidth(150);
		Fehlertabelle.getColumnModel().getColumn(1).setPreferredWidth(300);
		Fehlertabelle.getColumnModel().getColumn(1).setMinWidth(100);
		Fehlertabelle.getColumnModel().getColumn(1).setMaxWidth(600);
		FehlertabellePanel.setViewportView(Fehlertabelle);
			
	}
}