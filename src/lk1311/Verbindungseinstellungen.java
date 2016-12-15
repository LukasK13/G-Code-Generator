package lk1311;

import gnu.io.CommPortIdentifier;

import java.awt.*;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

public class Verbindungseinstellungen extends JDialog {
	
	private CommPortIdentifier[] SeriellePorts_ID = new CommPortIdentifier[10];
	private CommPortIdentifier SeriellerPort_IDNeu;
	private String SeriellerPort_NameNeu;
	private int BaudrateNeu = 115200;
	private int DatenBitsNeu = 8;
	private int StopBitsNeu = 1;
	private int FlusskontrolleNeu = 0;
	private boolean PortGewählt = false;
	private JDialog thisVar = this;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Verbindungseinstellungen (JFrame parent, CommPortIdentifier SeriellerPort_ID, String SeriellerPort_Name, int Baudrate, int DatenBits, int StopBits, int Flusskontrolle) {
		super(parent,"RS232 Verbindungseinstellungen", true);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setSize(400,400);
		
		JPanel RS232_Panel = new JPanel();
		RS232_Panel.setBounds(0, 0, 10, 10);
		getContentPane().add(RS232_Panel);
		RS232_Panel.setLayout(null);
		
		JLabel lblRsVerbindungseinstellungen = new JLabel("RS232 Verbindungseinstellungen");
		lblRsVerbindungseinstellungen.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblRsVerbindungseinstellungen.setBounds(10, 11, 374, 14);
		RS232_Panel.add(lblRsVerbindungseinstellungen);
		
		JLabel lblRsport = new JLabel("RS232-Port:");
		lblRsport.setBounds(10, 45, 80, 14);
		RS232_Panel.add(lblRsport);
		
		SeriellePorts_ID=RS232_Ports();
		
		int i=0;
		while (SeriellePorts_ID[i] != null) {
			i++;
		}
		
		String[] SeriellePorts_Name = new String[i];
		
		int j=0;
		int k=-1;
		while (SeriellePorts_ID[j] != null) {
			SeriellePorts_Name[j] = SeriellePorts_ID[j].getName();
			if (SeriellePorts_ID[j].getName().equals(SeriellerPort_Name)) {
				k=j;
			}
			j++;
		}
		
		JComboBox PortBox = new JComboBox();
		PortBox.setModel(new DefaultComboBoxModel(SeriellePorts_Name));
		PortBox.setSelectedIndex(k);
		PortBox.setBounds(100, 42, 90, 20);
		PortBox.setMaximumRowCount(10);
		
		PortBox.setToolTipText("Schnittstelle");
		RS232_Panel.add(PortBox);
		
		JLabel lblBaudrate = new JLabel("Baudrate:");
		lblBaudrate.setBounds(10, 75, 80, 14);
		RS232_Panel.add(lblBaudrate);
		
		JComboBox BaudBox = new JComboBox();
		BaudBox.setModel(new DefaultComboBoxModel(new String[] {"1200", "2400", "9600", "19200", "38400", "57600", "115200"}));
		BaudBox.setSelectedItem(String.valueOf(Baudrate));
		BaudBox.setBounds(100, 73, 90, 20);
		RS232_Panel.add(BaudBox);
		
		JLabel lblDatenBits = new JLabel("Daten Bits:");
		lblDatenBits.setBounds(10, 105, 80, 14);
		RS232_Panel.add(lblDatenBits);
		
		JComboBox DatenBox = new JComboBox();
		DatenBox.setModel(new DefaultComboBoxModel(new String[] {"7", "8"}));
		DatenBox.setSelectedItem(String.valueOf(DatenBits));
		DatenBox.setBounds(100, 104, 90, 20);
		RS232_Panel.add(DatenBox);
		
		JLabel lblStopBits = new JLabel("Stop Bits:");
		lblStopBits.setBounds(10, 135, 80, 14);
		RS232_Panel.add(lblStopBits);
		
		JComboBox StopBox = new JComboBox();
		StopBox.setModel(new DefaultComboBoxModel(new String[] {"1", "2"}));
		StopBox.setSelectedItem(String.valueOf(StopBits));
		StopBox.setBounds(100, 132, 90, 20);
		RS232_Panel.add(StopBox);
		
		JLabel lblFlusskontrolle = new JLabel("Flusskontrolle:");
		lblFlusskontrolle.setBounds(10, 165, 80, 14);
		RS232_Panel.add(lblFlusskontrolle);
		
		JComboBox FlussBox = new JComboBox();
		FlussBox.setModel(new DefaultComboBoxModel(new String[] {"None", "RTS/CTS", "Xon/Xoff"}));
		if (Flusskontrolle == 0) {
			FlussBox.setSelectedIndex(0);
		} else if (Flusskontrolle == 1) {
			FlussBox.setSelectedIndex(1);
		} else if (Flusskontrolle == 4) {
			FlussBox.setSelectedIndex(2);
		}
		FlussBox.setBounds(100, 162, 90, 20);
		RS232_Panel.add(FlussBox);
	
		JButton abbrechenBtn = new JButton("abbrechen");
		abbrechenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BaudrateNeu = Baudrate;
				DatenBitsNeu = DatenBits;
				StopBitsNeu = StopBits;
				FlusskontrolleNeu = Flusskontrolle;
				dispose();
			}
		});
		abbrechenBtn.setBounds(220, 269, 164, 23);
		RS232_Panel.add(abbrechenBtn);
		
		JButton StandardBtn = new JButton("Standard wiederherstellen");
		StandardBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BaudBox.setSelectedIndex(6);
				DatenBox.setSelectedIndex(1);
				StopBox.setSelectedIndex(0);
				FlussBox.setSelectedIndex(0);
			}
		});
		StandardBtn.setBounds(220, 235, 164, 23);
		RS232_Panel.add(StandardBtn);
		
		JButton VerbindungstestBtn = new JButton("MF70 Verbindungstest");
		VerbindungstestBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CommPortIdentifier SeriellerPort_IDTest = SeriellePorts_ID[PortBox.getSelectedIndex()];
				int BaudrateTest = Integer.parseInt(BaudBox.getSelectedItem().toString());
				int DatenBitsTest = Integer.parseInt(DatenBox.getSelectedItem().toString());
				int StopBitsTest = Integer.parseInt(StopBox.getSelectedItem().toString());
				int FlusskontrolleTest = 0;
				if (FlussBox.getSelectedIndex() == 0) {
					FlusskontrolleTest = 0;
				} else if (FlussBox.getSelectedIndex() == 1) {
					FlusskontrolleTest = 1;
				} else if (FlussBox.getSelectedIndex() == 2) {
					FlusskontrolleTest = 4;
				}
				
				Verbindungstest Dialog = new Verbindungstest(thisVar, SeriellerPort_IDTest, BaudrateTest, DatenBitsTest, StopBitsTest, FlusskontrolleTest);
				Dialog.setVisible(true);
				if (Dialog.VerbindungErfolgreich) {
					VerbindungstestBtn.setForeground(Color.GREEN);
				}
			}
		});
		VerbindungstestBtn.setForeground(Color.RED);
		VerbindungstestBtn.setBounds(10, 235, 164, 23);
		RS232_Panel.add(VerbindungstestBtn);
		
		JButton zurückBtn = new JButton("sichern und zurück");
		zurückBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SeriellerPort_IDNeu = SeriellePorts_ID[PortBox.getSelectedIndex()];
				SeriellerPort_NameNeu = SeriellerPort_IDNeu.getName();
				BaudrateNeu = Integer.parseInt(BaudBox.getSelectedItem().toString());
				DatenBitsNeu = Integer.parseInt(DatenBox.getSelectedItem().toString());
				StopBitsNeu = Integer.parseInt(StopBox.getSelectedItem().toString());
				if (FlussBox.getSelectedIndex() == 0) {
					FlusskontrolleNeu = 0;
				} else if (FlussBox.getSelectedIndex() == 1) {
					FlusskontrolleNeu = 1;
				} else if (FlussBox.getSelectedIndex() == 2) {
					FlusskontrolleNeu = 4;
				}
				PortGewählt = true;
				dispose();
			}
		});
		zurückBtn.setBounds(10, 269, 164, 23);
		RS232_Panel.add(zurückBtn);
		PortGewählt = false;
	}
	
	private CommPortIdentifier[] RS232_Ports() {
		CommPortIdentifier[] SeriellePorts_IDTemp = new CommPortIdentifier[10];
		CommPortIdentifier CommPort_ID = null;
	    @SuppressWarnings("rawtypes")
		Enumeration CommPortEnum = CommPortIdentifier.getPortIdentifiers(); //Alle bekannten Ports werden aufgezählt
	    int i = 0;
	    while (CommPortEnum.hasMoreElements()) {	    	
	    	CommPort_ID = (CommPortIdentifier) CommPortEnum.nextElement();
	     	if(CommPort_ID.getPortType() == CommPortIdentifier.PORT_SERIAL) { //ist es ein serieller Port?
	     		if (i>=9) {
	     			break;
	     		}
	     		SeriellePorts_IDTemp[i] = CommPort_ID;
	     		i++;
	    	}
	    }
	    return SeriellePorts_IDTemp;
	}
	
	public boolean getPortGewählt() {
		return PortGewählt;
	}
	
	public CommPortIdentifier getPort() {
		return SeriellerPort_IDNeu;
	}
	
	public String getPortName() {
		return SeriellerPort_NameNeu;
	}
	
	public int getBaudrate() {
		return BaudrateNeu;
	}
	
	public int getDatenBits() {
		return DatenBitsNeu;
	}
	
	public int getStopBits() {
		return StopBitsNeu;
	}
	
	public int getFlusskontrolle() {
		return FlusskontrolleNeu;
	}
	
}	

