package Chess;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.*;

public class ChessBoard extends JPanel {
	Rectangle[][] squareBorders;
	JPanel Squares[][];
	public ChessBoard () {
		
		this.setLayout(new GridLayout(8,8));
		this.setVisible(true);
		this.setBackground(Color.WHITE);
		Squares = generateBoard();
		
		/*
		BufferedImage myPicture = null;
		try {
			myPicture = ImageIO.read(new File(""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		add(picLabel);
		*/
		
	}
	
	public void setSquareBorders() {
		squareBorders = generateSquareBounds();
	}
	/*
	 * Method name: generateBoard: Creates the grid of panels, black and white
	 * Input: None
	 * Output: Double array of premade panels
	 */
	private JPanel[][] generateBoard() {
		JPanel temporary[][] = new JPanel[8][8];
		
		//Generate JPanels for black and white 
				for (int i = 0; i < 8; i++) {
					
					if (i % 2 == 0) {
						int colorVal = 0;
						for (int u = 0; u < 8; u++) {
							temporary[i][u] = new JPanel();
							temporary[i][u].setSize(new Dimension(50,50));
							if (colorVal == 0 ) {
								temporary[i][u].setBackground(Color.WHITE);
								colorVal = 1;
							} else {
								temporary[i][u].setBackground(Color.BLACK);
								colorVal = 0;
							}
							//test[i][u].setLocation(u*50, i*50);
							temporary[i][u].setVisible(true);
							this.add(temporary[i][u]);
							this.repaint();
						}
					} else {
						int colorVal = 1;
						for (int u = 0; u < 8; u++) {
							temporary[i][u] = new JPanel();
							temporary[i][u].setSize(new Dimension(50,50));
							if (colorVal == 0 ) {
								temporary[i][u].setBackground(Color.WHITE);
								colorVal = 1;
							} else {
								temporary[i][u].setBackground(Color.BLACK);
								colorVal = 0;
							}
							temporary[i][u].setLocation(u*50, i*50);
							temporary[i][u].setVisible(true);
							this.add(temporary[i][u]);
							this.repaint();
						}
					}
					
				}
		
		return temporary;
	}
	
	public Rectangle[][] generateSquareBounds() {
		Rectangle[][] temp = new Rectangle[8][8];
		for (int i = 0; i < 8; i++) {
			for (int u = 0; u < 8; u++) {
				Point location = Squares[i][u].getLocationOnScreen();
				temp[7-i][u] = new Rectangle(location.x, location.y, Squares[i][u].getWidth(), Squares[i][u].getHeight());
			}
		}
		return temp;
	}
	
	public Rectangle[][] getSquareBounds() {
		return squareBorders;
	}
	
}
