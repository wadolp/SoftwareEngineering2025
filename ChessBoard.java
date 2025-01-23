package Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class ChessBoard extends JFrame{
	
	boolean switchVar;
	public ChessBoard () {
		
		this.setLayout(new GridLayout(8,8));
		JPanel test[][] = new JPanel[8][8];
		
		this.setVisible(true);
		this.setSize(new Dimension(500,500));
		this.setBackground(Color.WHITE);
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
		
		
		
		//-Change Later
		Timer resizeTimer = new Timer();

        addComponentListener(new ComponentAdapter() {
            private TimerTask resizeTask;

            @Override
            public void componentResized(ComponentEvent e) {
                if (resizeTask != null) {
                    resizeTask.cancel(); // Cancel the previous task
                }

                resizeTask = new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> {
                            if (getWidth() > getHeight()) {
                            	setSize(new Dimension(getWidth(), getWidth()));
                            } else {
                            	setSize(new Dimension(getHeight(), getHeight()));
                            }
                        });
                    }
                };

                // Schedule the task to run after a delay (e.g., 300 ms)
                resizeTimer.schedule(resizeTask, 300);
            }
        });
		
	}
	
	public static void main(String args[]) {
		new ChessBoard();
	}
}
