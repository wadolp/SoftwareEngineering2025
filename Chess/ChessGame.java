package Chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class ChessGame extends JFrame {
	private int windowSize = 500;
	public ChessGame() {
		
		ChessBoard chessBoard = new ChessBoard();
		
		//Set layout for JFrame
		setVisible(true);
		setSize(new Dimension(windowSize, windowSize));
		setLayout(new BorderLayout());
		add(chessBoard, BorderLayout.CENTER);
		
		//Change in the future, just test
		JPanel north = new JPanel();
		north.setVisible(true);
		north.setBackground(Color.BLUE);
		JPanel south = new JPanel();
		south.setVisible(true);
		south.setBackground(Color.BLUE);
		JPanel west = new JPanel();
		west.setVisible(true);
		west.setBackground(Color.BLUE);
		JPanel east = new JPanel();
		east.setVisible(true);
		east.setBackground(Color.BLUE);
		
		JButton testButton = new JButton("Test button");
		testButton.setVisible(true);
		west.add(testButton);
		
		JButton testTwo = new JButton ("Test space button");
		testTwo.setVisible(true);
		east.add(testTwo);
		north.setPreferredSize(new Dimension(0,(int)(.10*windowSize)));
		west.setPreferredSize(new Dimension((int)(.10*windowSize), 0));
		south.setPreferredSize(new Dimension(0, (int)(.10*windowSize)));
		east.setPreferredSize(new Dimension((int)(.10*windowSize), 0));
		
		add(north, BorderLayout.NORTH);
		add(south, BorderLayout.SOUTH);
		add(east, BorderLayout.EAST);
		add(west, BorderLayout.WEST);
		revalidate();
		
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
		                            
		                            //Resize borders
		                            north.setPreferredSize(new Dimension(0,(int)(.10*getWidth())));
		                    		west.setPreferredSize(new Dimension((int)(.10*getWidth()), 0));
		                    		south.setPreferredSize(new Dimension(0, (int)(.10*getWidth())));
		                    		east.setPreferredSize(new Dimension((int)(.10*getWidth()), 0));
		                            
		                        });
		                    }
		                };

		                // Schedule the task to run after a delay (e.g., 300 ms)
		                resizeTimer.schedule(resizeTask, 300);
		            }
		        });
		
	}
	
	public static void main(String args[]) {
		new ChessGame();
	}
}
