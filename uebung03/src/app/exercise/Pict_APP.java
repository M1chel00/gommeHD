package app.exercise;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * A Java Class showing a Java Swing Applet whithin you can switch the view of an given image from 'original' to 'grayscale' or 'pattern'
 */
public class Pict_APP {
	private static BufferedImage img;
	//privates statischen BufferedImage

	/**
	 * main method of Class Pict_APP, that is called at startup
	 * @param args Programm arguments
	 */
	public static void main(String[] args){

		JFrame frame = new JFrame("Allgemeines Programmierpraktikum");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Anlegen eines neuen JFrame's, mit dem Titel 'APP' und das Setzen des Schlieszen des Fenster durch das X

		JPanel panel = new JPanel();
		frame.add(panel, BorderLayout.NORTH);
		//Anlegen eines neuen Panels, welches dem frame hinzugefuegt wird und im 'Norden' des Frames angeordnet wird

		JPanel panel2 = new JPanel();
		frame.add(panel2, BorderLayout.CENTER);
		//Anlegen eines neuen Panels, welches dem frame hinzugefuegt wird und im 'Center' des Frames angeordnet wird

		JPanel panel3 = new JPanel();
		frame.add(panel3, BorderLayout.SOUTH);
		//Anlegen eines neuen Panels, welches dem frame hinzugefuegt wird und im 'Sueden' des Frames angeordnet wird

		img = resize(loadImage("kandinsky.jpg"));
		//img wird ein Bild durch loadImage zugewiesen, welches vorher von resize noch vergroeszert wird
		JLabel picLabel = new JLabel(new ImageIcon(img));
		//Anlegen eines neuen JLabel's, welches als Parameter das Bild zugewiesen bekommt
		JButton original = new JButton("Original");
		JButton grayscale = new JButton("GrayScale");
		JButton pattern = new JButton("Pattern");
		JButton exit = new JButton("Exit");
		//Anlegen der 4 vorgegebenen Buttons mit dem jeweiligem Titel als Parameter

		ActionListener al = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JLabel pic;
				BufferedImage temp = null;
				if(e.getSource() == original) {
					temp = img;
					//dem temp Image wird das geladene Image zugewiesen
					pic = new JLabel(new ImageIcon(temp));
					//anlegen eines neuen JLabels mit dem temp Image
					panel2.removeAll();
					//alle Components vom 'CENTER' Panel entfernen
					panel2.add(pic);
					//hinzufuegen des JLabels zum 'CENTER' Panel
				}else if(e.getSource() == grayscale) {
					temp = setGray(img);
					//dem temp Image wird das return Image von 'setGray' mit dem geladenen Image zugewiesen
					pic = new JLabel(new ImageIcon(temp));
					//anlegen eines neuen JLabels mit dem temp Image
					panel2.removeAll();
					//alle Components vom 'CENTER' Panel entfernen
					panel2.add(pic);
					//hinzufuegen des JLabels zum 'CENTER' Panel
				}else if(e.getSource() == pattern) {
					temp = setPattern(img);
					//dem temp Image wird das return Image von 'setPattern' mit dem geladenen Image zugewiesen
					pic = new JLabel(new ImageIcon(temp));
					//anlegen eines neuen JLabels mit dem temp Image
					panel2.removeAll();
					//alle Components vom 'CENTER' Panel entfernen
					panel2.add(pic);
					//hinzufuegen des JLabels zum 'CENTER' Panel
				}else if(e.getSource() == exit) {
					System.exit(0);
					//Fenster/ Programm schlieszen
				}
				frame.pack();
				//Aufrufen der pack funktion fuer das Frame, welche die Groesze anpasst
				panel2.repaint();
				//das 'CENTER' Panel neuzeichnen
			}
		};
		//Anlegen des ActionListener, welcher fuer die Button zustaendig ist

		original.addActionListener(al);
		grayscale.addActionListener(al);
		pattern.addActionListener(al);
		exit.addActionListener(al);
		//Der ActionListener wird alles Buttons zugewiesen

		panel.add(original);
		panel.add(grayscale);
		panel.add(pattern);
		//dem 'NORTH' Panel werden die Buttons hinzugefuegt

		panel2.add(picLabel);
		//dem 'CENTER' Panel wird das Bild hinzugefuegt

		panel3.add(exit);
		//dem 'SOUTH' Panel wird der EXIT Button hinzugefuegt

		frame.pack();
		//Aufrufen der pack funktion fuer das Frame, welche die Groesze anpasst
		frame.setResizable(false);
		//deaktivieren des 'resize'n fuer dieses Frame
		frame.setVisible(true);
		//das Frame sichtbar machen
	}

	/**
	 * Load image name in current folder
	 * @param name An Image name
	 * @return a BufferedImage containing the given image
	 */
	private static BufferedImage loadImage(String name){
		try {
			String path = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
			//Path des Workspace
			File f = new File(path + "/" + name);
			//Path/File des zu ladenen Image
			return ImageIO.read(f);
			//laedt das Bild mit ImageIO und return'ed es
		} catch (IOException e) {
			System.err.println("Could not find the picture");
			System.exit(0);
			//wenn das Laden des Bildes scheitert, Fehler ausgeben und Programm beenden
			return null;
		}
	}

	/**
	 * Multiply the size of given image with 2
	 * @param img A BufferedImage to resize
	 * @return a new BufferedImage with the double size
	 */
	private static BufferedImage resize(BufferedImage img){
		BufferedImage fin = new BufferedImage(img.getWidth()*2, img.getWidth()*2, img.getType());
		//Anlegen einer Copy des uebergebenen Image
		for(int i = 0; i < img.getWidth(); i++) {
			for(int j = 0; j < img.getHeight(); j++) {
				//durch alle Pixel iterieren
				int c = img.getRGB(i, j);
				//Farbe des aktuellen Pixels lesen
				fin.setRGB(2*i, 2*j, c);
				fin.setRGB(2*i, 2*j+1, c);
				fin.setRGB(2*i+1, 2*j, c);
				fin.setRGB(2*i+1, 2*j+1, c);
				//neuzuweisen der Farben zu den passenden Pixels fuer die doppelte Groesze
			}
		}
		return fin;
		//zurueckgeben des vergroeszerten Bildes
	}

	/**
	 * Modify the given image to grayscale
	 * @param img A BufferedImage to recolor
	 * @return a new BufferedImage with a grayscale
	 */
	private static BufferedImage setGray(BufferedImage img){
		BufferedImage fin = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		//Anlegen einer Copy des uebergebenen Image
		for(int i = 0; i < fin.getWidth(); i++) {
			for(int j = 0; j < fin.getHeight(); j++) {
				//durch alle Pixel iterieren
				int c = img.getRGB(i, j);
				//aktuelle Farbe auslesen
				Color color = new Color(c);
				int d = (int) ((color.getBlue() + color.getGreen() + color.getRed())/3);
				//Mittelwert der RGB Werte bilden
				Color gray = new Color(d, d, d);
				//neue Farbe anlegen mit dem Mittelwert
				fin.setRGB(i, j, gray.getRGB());
				//Farbe der Pixel neusetzen
			}
		}
		return fin;
		//veraendertes Bild zurueckgeben
	}

	/**
	 * Modify the given image to a 'pattern'
	 * @param img A BufferedImage to recolor
	 * @return a new BufferedImage with a new 'pattern'
	 */
	private static BufferedImage setPattern(BufferedImage img){
		BufferedImage fin = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		//Anlegen einer Copy des uebergebenen Image
		for(int i = 0; i < fin.getWidth(); i+=2) {
			for(int j = 0; j < fin.getHeight(); j+=2) {
				//durch alle Pixel des nicht vergroeszerten Images iterieren
				int c = img.getRGB(i, j);
				//aktuelle Farbe des Pixels auslesen
				Color color = new Color(c);
				int d = (int) ((color.getBlue() + color.getGreen() + color.getRed())/3);
				//Mittelwert der RGB Werte berechnen
				Color c1, c2, c3, c4;
				//Anlegen von 4 Farben fuer das Pattern
				if(d < 51) {
					c1 = Color.BLACK;
					c2 = Color.BLACK;
					c3 = Color.BLACK;
					c4 = Color.BLACK;
				}else if(d < 102) {
					c1 = Color.WHITE;
					c2 = Color.BLACK;
					c3 = Color.BLACK;
					c4 = Color.BLACK;
				}else if(d < 153) {
					c1 = Color.BLACK;
					c2 = Color.WHITE;
					c3 = Color.WHITE;
					c4 = Color.BLACK;
				}else if(d < 204) {
					c1 = Color.BLACK;
					c2 = Color.WHITE;
					c3 = Color.WHITE;
					c4 = Color.WHITE;
				}else {
					c1 = Color.WHITE;
					c2 = Color.WHITE;
					c3 = Color.WHITE;
					c4 = Color.WHITE;
				}
				//zuweisen der 4 Farben anhand des gegebenen Pattern's
				fin.setRGB(i, j, c1.getRGB());
				fin.setRGB(i+1, j, c2.getRGB());
				fin.setRGB(i, j+1, c3.getRGB());
				fin.setRGB(i+1, j+1, c4.getRGB());
				//Farbe der Pixel anhand des Patterns setzen
			}
		}
		return fin;
		//veraendertes Bild zurueckgeben
	}

}
