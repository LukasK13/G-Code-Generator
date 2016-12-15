package lk1311;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Fr‰soptionen extends JDialog{
	
	private JTable Werkzeugtabelle;
	private JNumberField ZOffsetNum;
	private JNumberField XYOffsetNum;
	private String[][] WerkzeugdatenNeu = new String[5][3];
	private Double ZOffset = 0.0;
	private Double XYOffset = 0.0;

	
	public Fr‰soptionen (JFrame parent, String[][] WerkzeugdatenAlt, double ZOffsetAlt, double XYOffsetAlt) {
		super(parent,"Fr‰soptionen", true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(400,400);		
		getContentPane().setLayout(null);
		
		JScrollPane WerkzeugtabellePanel = new JScrollPane();
		WerkzeugtabellePanel.setBounds(0, 30, 394, 107);
		getContentPane().add(WerkzeugtabellePanel);
		
		
		Werkzeugtabelle = new JTable();
		Werkzeugtabelle.setModel(new DefaultTableModel(WerkzeugdatenAlt,
			new String[] {"Werkzeug", "Durchmesser", "L‰nge"}
		) {
			boolean[] columnEditables = new boolean[] {
				false, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		Werkzeugtabelle.getColumnModel().getColumn(0).setResizable(false);
		Werkzeugtabelle.getColumnModel().getColumn(1).setResizable(false);
		Werkzeugtabelle.getColumnModel().getColumn(2).setResizable(false);
		WerkzeugtabellePanel.setViewportView(Werkzeugtabelle);
		
		JLabel lblWerkzeugtabelle = new JLabel("Werkzeugtabelle:");
		lblWerkzeugtabelle.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblWerkzeugtabelle.setBounds(10, 5, 110, 20);
		getContentPane().add(lblWerkzeugtabelle);
		
		JLabel lblZoffset = new JLabel("Werkst\u00FCckh\u00F6he:");
		lblZoffset.setBounds(10, 170, 100, 14);
		getContentPane().add(lblZoffset);
		
		ZOffsetNum = new JNumberField();
		ZOffsetNum.setBounds(120, 167, 86, 20);
		ZOffsetNum.setDouble(ZOffsetAlt);
		getContentPane().add(ZOffsetNum);
		ZOffsetNum.setColumns(10);
		
		JLabel lblXyoffset = new JLabel("X/Y-Offset:");
		lblXyoffset.setBounds(10, 210, 100, 14);
		getContentPane().add(lblXyoffset);
		
		XYOffsetNum = new JNumberField();
		XYOffsetNum.setBounds(120, 207, 86, 20);
		XYOffsetNum.setDouble(XYOffsetAlt);
		getContentPane().add(XYOffsetNum);
		XYOffsetNum.setColumns(10);
		
		WerkzeugdatenNeu=WerkzeugdatenAlt;
		ZOffset = ZOffsetAlt;
		XYOffset = XYOffsetAlt;
		
		JButton btn‹bernehmen = new JButton("‹bernehmen");
		btn‹bernehmen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i=0; i<=4; i++) {
					for (int j=0; j<=2; j++) {
						WerkzeugdatenNeu[i][j]=Werkzeugtabelle.getValueAt(i,j).toString();
						ZOffset = ZOffsetNum.getDouble();
						XYOffset = XYOffsetNum.getDouble();
					}
				}
				dispose();
			}
		});
		btn‹bernehmen.setBounds(30, 338, 110, 23);
		getContentPane().add(btn‹bernehmen);
		
		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnAbbrechen.setBounds(244, 338, 110, 23);
		getContentPane().add(btnAbbrechen);
	}
	
	public String[][] getTableContent() {
		return WerkzeugdatenNeu;
	}
	
	public double getZOffset() {
		return ZOffset;
	}
	
	public double getXYOffset() {
		return XYOffset;
	}
}
