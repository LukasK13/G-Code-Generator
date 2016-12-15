package lk1311;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class Turtle extends Canvas {

	private BufferedImage myBufferedImage;
	private int originX;
	private int originY;
	private Color foreground;
	private Color background;
	private static final double piDurch180 = Math.PI / 180;
	
	/**
   	* myBufferedGraphics ist die Graphik der Turtle.
   	* Beispiel: myTurtle.myBufferedGraphics.drawRect(X, Y, Länge, Breite);
   	*/
	public Graphics myBufferedGraphics;  
  
	/**
   	* turtleX ist die X-Koordinate der Turtle.
   	* Beispiel: myTurtle.turtleX = 100;
   	*/
	public double turtleX;

	/**
   	* turtleY ist die Y-Koordinate der Turtle.
   	* Beispiel: myTurtle.turtleY = 200;
   	*/
	public double turtleY;

	/**
   	* turtleW ist der aktuelle Winkel der Turtle im Bereich 0 bis 360 Grad.
   	* Beispiel: myTurtle.turtleW = 180;
   	*/
	public double turtleW;

	/**
   	* Ist drawDynamic true, so kann der Zeichenvorgang beobachtet werden.
   	* Beispiel: myTurtle.drawDynamic = true;
   	*/
	public boolean drawDynamic;

	/**
   	* Ist drawDynamic true, so wird um 'sleepTime' Millisekunden verzögert.
   	* Beispiel: myTurtle.sleepTime = 500;
   	*/
	public int sleepTime = 100;
  
	// --- Konstruktor -----------------------------------------------------------

	/**
	 * Erzeugt eine Turtle mit einer Zeichenfläche, die Breite x Höhe groß ist.
	 * Die Turtle wird anfangs in die Mitte der Zeichenfläche gesetzt.
	 * Der Anfangswinkel ist 0 Grad, was Blickrichtung nach rechts entspricht.
	 * Die Hintergrundfarbe ist Weiß, die Zeichenfarbe ist Schwarz.
	 * Die Turtleposition kann interaktiv durch Anklicken der Zeichenfläche
	 * festgelegt werden.
	 * Die Turtlegröße wird automatisch an das Programmfenster angepasst.
	 * Beispiel: Turtle myTurtle = new Turtle(640, 480);
	 */

	public Turtle(int width, int height) {
		setSize(width, height);

		myBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		myBufferedGraphics = myBufferedImage.getGraphics();

		setForeground(Color.black);
		setBackground(Color.white);
		drawDynamic = false;

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				turtleMouseClicked(evt);
			}
		});
		
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				turtleResized(evt);
			}
		});
	}

	private void turtleMouseClicked(MouseEvent evt) {
		turtleX = evt.getX() - originX;
		turtleY = originY - evt.getY();
		turtleW = 0;
	}

	private void turtleResized(ComponentEvent evt) {
		int width = getWidth();
		int height = getHeight();

		BufferedImage newBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics newGraphics = newBufferedImage.getGraphics();
		newGraphics.setColor(background);
		newGraphics.fillRect(0, 0, width, height);
		newGraphics.setColor(foreground);
		newGraphics.drawImage(myBufferedImage, 0, 0, this);

		turtleX = 0;
		turtleY = 0;
		turtleW = 0;
		setOrigin(width / 2, height / 2);
		System.out.println("hier");
		myBufferedImage = newBufferedImage;
		myBufferedGraphics = newGraphics;
  }

	public boolean isDoubleBuffered() {
		return true;
	}

	// --- Winkel und Drehungen --------------------------------------------------

	private void wTurtleMod360() {
		while (turtleW >= 360) {
			turtleW = turtleW - 360;
		}			
		while (turtleW < 0) {
			turtleW = turtleW + 360;
		}			
	}

	/**
	 * Dreht den Richtungswinkel der Turtle relativ um den Winkel angle.
	 * Positive Werte bedeuten eine Rechtsdrehung, negative eine Linksdrehung.
	 * Beispiel: myTurtle.turn(-90);
	 */
	public void turn(double angle) {
		turtleW = turtleW + angle;
		wTurtleMod360();
	}

	/**
	 * Setzt den Richtungswinkel der Turtle absolut auf den Winkel angle.
	 * Der Richtungswinkel nimmt gegen den Uhrzeigersinn zu, also gilt
	 * Rechts = 0, Oben = 90, Links = 180, Unten = 270.
	 * Beispiel: myTurtle.turnto(270);
	 */
	public void turnto(double angle) {
		turtleW = angle;
		wTurtleMod360();
	}

	// --- Zeichnen --------------------------------------------------------------

	/**
	 * Die Turtle zeichnet eine Linie der angegebenen Länge in die aktuelle
	 * Richtung.
	 * Beispiel: myTurtle.draw(100);
	 */
	public void draw(double laenge) {
		drawto(turtleX + laenge * Math.cos(turtleW * piDurch180), turtleY + laenge * Math.sin(turtleW * piDurch180));
	}

	/**
	 * Die Turtle zeichnet eine Linie von der aktuellen Position (turtleX, turtleY)
	 * zur Position (x, y).
	 * Beispiel: myTurtle.drawto(200, 300);
	 */
	public void drawto(double x, double y) {
		int x1 = originX + (int) turtleX;
		int x2 = originX + (int) x;
		int y1 = originY - (int) turtleY;
		int y2 = originY - (int) y;

		myBufferedGraphics.drawLine(x1, y1, x2, y2);
		if (drawDynamic){
			getGraphics().drawLine(x1, y1, x2, y2);
			try {
				Thread.sleep(sleepTime);
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} else {
			repaint();
		}      
      
		turtleX = x;
		turtleY = y;
	}

	/**
	 * Die Turtle bewegt sich ohne Zeichnen in der aktuellen Richtung
	 * um eine Strecke der angegebenen Länge.
	 * Beispiel: myTurtle.move(100);
	 */
	public void move(double length) {
		turtleX = turtleX + length * Math.cos (turtleW * piDurch180);
		turtleY = turtleY + length * Math.sin (turtleW * piDurch180);
	}

	// --- Bewegen ohne Zeichnen -------------------------------------------------

	/**
	 * Die Turtle bewegt sich ohne Zeichnen zur Position (x/y).
	 * Beispiel: myTurtle.moveto(100, 200);
	 */
	public void moveto(double x, double y) {
		turtleX = x;
		turtleY = y;
	}

	// --- Ursprung setzen -------------------------------------------------------

	/**
	 * Setzt den Ursprung der Turtle auf die Position (x/y) der Grafikfläche.
	 * Beispiel: myTurtle.setOrigin(100, 200);
	 */
	public void setOrigin(double x, double y) {
		originX = (int) x;
		originY = (int) y;
	}
  
	// --- Vorder- und Hintergrundfarbe ------------------------------------------

	/**
	 * Setzt die Zeichenfarbe der Turtle auf die Farbe c.
	 * Beispiel: myTurtle.setForeground(Color.red);
	 */
	public void setForeground(Color c) {
		foreground = c;
		myBufferedGraphics.setColor(foreground);
		super.setForeground(foreground);
	}

	/**
	 * Setzt die Farbe der Zeichenfläche auf die Farbe c.
	 * Beispiel: myTurtle.setBackground(Color.blue);
	 */
	public void setBackground(Color c) {
		background = c;
		myBufferedGraphics.setColor(background);
		myBufferedGraphics.fillRect(0, 0, getWidth(), getHeight());
		myBufferedGraphics.setColor(foreground);
		repaint();
	}

	/**
	 * Löscht die Zeichenfläche der Turtle mit der aktuellen
	 * Hintergrundfarbe.
	 * Beispiel: myTurtle.clear();
	 */
	public void clear() {
		setBackground(background);
		getGraphics().drawImage(myBufferedImage, 0, 0, this);
		repaint();
	}

	// --- Anzeigen --------------------------------------------------------------

	/**
	 * Stellt die Zeichenfläche der Turtle dar.
	 */
	public void paint(Graphics g) {
		g.drawImage(myBufferedImage, 0, 0, this);
	}

	/**
	 * Aktualisiert die Zeichenfläche der Turtle.
	 */
	public void update(Graphics g) {
		paint(g);
	}
 
}
