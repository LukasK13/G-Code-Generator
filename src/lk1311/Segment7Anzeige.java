package lk1311;

import java.awt.*;

public class Segment7Anzeige extends Canvas {
	private double Wert;
	private Color Ziffer = Color.yellow;
	private Color Hintergrund = Color.red;
	private int VieleckX[][] = {
		{ 1, 2, 8, 9, 8, 2},    //Segment 0		oben
		{ 9,10,10, 9, 8, 8},    //Segment 1		rechts-oben
		{ 9,10,10, 9, 8, 8},    //Segment 2		rechts-unten
		{ 1, 2, 8, 9, 8, 2},    //Segment 3		unten
		{ 1, 2, 2, 1, 0, 0},    //Segment 4		links-unten
		{ 1, 2, 2, 1, 0, 0},    //Segment 5		links-oben
		{ 1, 2, 8, 9, 8, 2},    //Segment 6		mitte
	};
	private int VieleckY[][] = {
		{ 1, 0, 0, 1, 2, 2},    //Segment 0
		{ 1, 2, 8, 9, 8, 2},    //Segment 1
		{ 9,10,16,17,16,10},    //Segment 2
		{17,16,16,17,18,18},    //Segment 3
		{ 9,10,16,17,16,10},    //Segment 4
		{ 1, 2, 8, 9, 8, 2},    //Segment 5
		{ 9, 8, 8, 9,10,10},    //Segment 6
	};
	private int digits[][] = {
		{1,1,1,1,1,1,0},         //Ziffer 0
		{0,1,1,0,0,0,0},         //Ziffer 1
		{1,1,0,1,1,0,1},         //Ziffer 2
		{1,1,1,1,0,0,1},         //Ziffer 3
		{0,1,1,0,0,1,1},         //Ziffer 4
		{1,0,1,1,0,1,1},         //Ziffer 5
		{1,0,1,1,1,1,1},         //Ziffer 6
		{1,1,1,0,0,0,0},         //Ziffer 7
		{1,1,1,1,1,1,1},         //Ziffer 8
		{1,1,1,1,0,1,1},         //Ziffer 9
		{1,0,0,1,1,1,1}			 //Error
	};

	public Segment7Anzeige(double WertNeu) {
		Wert = WertNeu;
	}
	
	public Segment7Anzeige() {
		Wert = 0;
	}

	public void paint(Graphics g) {
		/* Ein Segment besteht aus 10 horizontalen Punkten und 18 vertikalen Punkten.
		 * Die Zählung beginnt unten links.
		 */
		
		boolean Negativ = false;
		int Int1=0, Int2=0, Int3=0, Int4=0, Int5=0, Int6=0;
		
		if (Wert <= -0.01) {
			Wert = -Wert;
			Negativ = true;
		}
		if (Wert <= 9999.99) {
			Int1 = (int) (Wert / 1000.0);
			Int2 = (int) ((Wert - Int1 * 1000.0) / 100.0);
			Int3 = (int) ((Wert - Int1 * 1000.0 - Int2 * 100.0) / 10.0);
			Int4 = (int) (Wert - Int1 * 1000.0 - Int2 * 100.0 - Int3 * 10.0);
			Int5 = (int) (Wert * 10.0 - Int1 * 10000.0 - Int2 * 1000.0 - Int3 * 100.0 - Int4 * 10.0); 
			Int6 = (int) (Wert * 100.0 - Int1 * 100000.0 - Int2 * 10000.0 - Int3 * 1000.0 - Int4 * 100.0 - Int5 * 10.0); 
			
			//dx und dy berechnen	
			int dx = getSize().width / 86;	
			int dy = getSize().height / 18;
			
			//Hintergrund
			g.setColor(Hintergrund);
			g.fillRect(0, 0, getSize().width, getSize().height);
			
			if(Negativ) {	//Minus
				g.setColor(Ziffer);
				Polygon poly = new Polygon();
				for (int j = 0; j < 6; ++j) { //alle Eckpunkte
					poly.addPoint(dx*VieleckX[6][j], dy*VieleckY[6][j]);
				}
				g.fillPolygon(poly);
			}
			
			//Vorkommastellen
			Segment(g, dx, dy, 1 * dx * 12, Int1);
			Segment(g, dx, dy, 2 * dx * 12, Int2);
			Segment(g, dx, dy, 3 * dx * 12, Int3);
			Segment(g, dx, dy, 4 * dx * 12, Int4);
			
			//Komma
			g.setColor(Ziffer);
			g.fillRect(5 * dx * 12, dy * 18 - dx * 2, dx * 2, dx * 2);
			
			//Nachkommastellen
			Segment(g, dx, dy, 5 * dx * 12 + dx * 4, Int5);
			Segment(g, dx, dy, 6 * dx * 12 + dx * 4, Int6);
		} else {
			System.err.println("Wert zu groß für 7-Segmentanzeige");
			
			//dx und dy berechnen	
			int dx = getSize().width / 86;		
			int dy = getSize().height / 18;
			
			//Hintergrund
			g.setColor(Hintergrund);
			g.fillRect(0,0,getSize().width,getSize().height);
			
			//Minus
			g.setColor(Ziffer);
			Polygon poly = new Polygon();
			for (int j = 0; j < 6; ++j) { //alle Eckpunkte
				poly.addPoint(dx*VieleckX[6][j], dy*VieleckY[6][j]);
			}
			g.fillPolygon(poly);
			
			//Vorkommastellen
			Segment(g, dx, dy, 1 * dx * 12, 10);
			Segment(g, dx, dy, 2 * dx * 12, 10);
			Segment(g, dx, dy, 3 * dx * 12, 10);
			Segment(g, dx, dy, 4 * dx * 12, 10);
			
			//Komma
			g.setColor(Ziffer);
			g.fillRect(5 * dx * 12, dy * 18 - dx * 2, dx * 2, dx * 2);
			
			//Nachkommastellen
			Segment(g, dx, dy, 5 * dx * 12 + dx * 4, 10);
			Segment(g, dx, dy, 6 * dx * 12 + dx * 4, 10);
		}		
	}
	
	private void Segment(Graphics g, int dx, int dy, int xOffset, int Zahl) {		
		//Segmente
		g.setColor(Ziffer);
		for (int i=0; i < 7; i++) { //alle Segmente
			//System.out.println(Wert + "   " + Zahl);
			if (digits[Zahl][i] == 1) {
				Polygon poly = new Polygon();
				for (int j = 0; j < 6; ++j) { //alle Eckpunkte
					poly.addPoint(dx*VieleckX[i][j] + xOffset,dy*VieleckY[i][j]);
				}
				g.fillPolygon(poly);
			}
		}
		//Trennlinien
		g.setColor(Hintergrund);
		g.drawLine(0 + xOffset,0,dx*10 + xOffset,dy*10);
		g.drawLine(0 + xOffset,8*dy,dx*10 + xOffset,18*dy);
		g.drawLine(0 + xOffset,10*dy,dx*10 + xOffset,0);
		g.drawLine(0 + xOffset,18*dy,dx*10 + xOffset,8*dy);
	}
	
	public double getValue() {
		return Wert;
	}

	public void setValue(double value) {
		Wert = value;
		repaint();
	}

	public void setForeground(Color ZifferNeu) {
		Ziffer = ZifferNeu;
	}
	
	public Color getForeground() {
		return Ziffer;
	}
	
	public void setBackground (Color HintergrundNeu) {
		Hintergrund = HintergrundNeu;
	}
	
	public Color getBackground() {
		return Hintergrund;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(4*86,4*18);
	}

	public Dimension getMinimumSize() {
		return new Dimension(86,18);
	}
}

