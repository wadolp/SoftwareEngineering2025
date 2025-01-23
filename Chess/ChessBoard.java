package Chess;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class ChessBoard extends JPanel {
	
	boolean switchVar;
	public ChessBoard () {
		
		this.setLayout(new GridLayout(8,8));
		JPanel test[][] = new JPanel[8][8];
		
		this.setVisible(true);
		this.setBackground(Color.WHITE);
		
		//Generate JPanels for black and white 
		for (int i = 0; i < 8; i++) {
			
			if (i % 2 == 0) {
				int colorVal = 0;
				for (int u = 0; u < 8; u++) {
					test[i][u] = new JPanel();
					test[i][u].setSize(new Dimension(50,50));
					if (colorVal == 0 ) {
						test[i][u].setBackground(Color.WHITE);
						colorVal = 1;
					} else {
						test[i][u].setBackground(Color.BLACK);
						colorVal = 0;
					}
					//test[i][u].setLocation(u*50, i*50);
					test[i][u].setVisible(true);
					this.add(test[i][u]);
					this.repaint();
				}
			} else {
				int colorVal = 1;
				for (int u = 0; u < 8; u++) {
					test[i][u] = new JPanel();
					test[i][u].setSize(new Dimension(50,50));
					if (colorVal == 0 ) {
						test[i][u].setBackground(Color.WHITE);
						colorVal = 1;
					} else {
						test[i][u].setBackground(Color.BLACK);
						colorVal = 0;
					}
					test[i][u].setLocation(u*50, i*50);
					test[i][u].setVisible(true);
					this.add(test[i][u]);
					this.repaint();
				}
			}
			
		}
		
		
		
		
		
	}
	
	public static void main(String args[]) {
		new ChessBoard();
	}
}
