package Chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class ChessGame extends JFrame {
	private int windowSize = 1000;
	private double sidePanelRatio = .15; //How much space of the window does the side panel take up
	JPanel east = new JPanel();
	JPanel west = new JPanel();
	JPanel south = new JPanel();
	JPanel north = new JPanel();
	ChessBoard chessBoard;
	Rectangle[][] squareChessBounds;
	
	//Variables for mouse listener
	int[] centerPanelCoordinates = new int[4]; //stores the corners starting from top left clockwise
	
	public ChessGame() {
		
		chessBoard = new ChessBoard();
		squareChessBounds = chessBoard.getSquareBounds();
		//Set layout for JFrame
		setVisible(true);
		setSize(new Dimension(windowSize, windowSize));
		setLayout(new BorderLayout());
		add(chessBoard, BorderLayout.CENTER);
		mouseListenerAdd();
		
		//Change in the future, just test
		addSidePanels();
		addWindowResizeSensor();
		chessBoard.setSquareBorders();
	}
	
	
	/*
	* Method name: whenWindowResized
	* Description: Automatically adjusts anything that needs to be readjusted when the window is resized
	* Input: None
	* Output: None
	*/
	private void whenWindowResized() {
		if (getWidth() > getHeight()) {
        	setSize(new Dimension(getWidth(), getWidth()));
        } else {
        	setSize(new Dimension(getHeight(), getHeight()));
        }
        
        //Resize borders
        north.setPreferredSize(new Dimension(0,(int)(sidePanelRatio*getWidth())));
		west.setPreferredSize(new Dimension((int)(sidePanelRatio*getWidth()), 0));
		south.setPreferredSize(new Dimension(0, (int)(sidePanelRatio*getWidth())));
		east.setPreferredSize(new Dimension((int)(sidePanelRatio*getWidth()), 0));
		
		chessBoard.generateSquareBounds();
		squareChessBounds = chessBoard.generateSquareBounds();
        
	}
	
	/*
	* Method name: northPanelDesign
	* Description: Holds the design properties for the north panel (intermediary method)
	* Input: None
	* Output: None
	*/
	private void northPanelDesign() {
		north.setVisible(true);
		north.setBackground(Color.GRAY);
		north.setPreferredSize(new Dimension(0,(int)(sidePanelRatio*windowSize)));
		
		JLabel tempText = new JLabel("Will store timer");
		tempText.setVisible(true);
		north.add(tempText);
		
	}
	
	/*
	* Method name: westPanelDesign
	* Description: Holds the design properties for the west panel (intermediary method)
	* Input: None
	* Output: None
	*/
	private void westPanelDesign() {
		west.setVisible(true);
		west.setBackground(Color.GRAY);
		west.setPreferredSize(new Dimension((int)(sidePanelRatio*windowSize), 0));
		JLabel tempText = new JLabel("Will store captured white pieces");
		tempText.setVisible(true);
		west.add(tempText);
	}
	
	/*
	* Method name: southPanelDesign
	* Description: Holds the design properties for the south panel (intermediary method)
	* Input: None
	* Output: None
	*/
	private void southPanelDesign() {
		south.setVisible(true);
		south.setBackground(Color.GRAY);
		south.setPreferredSize(new Dimension(0, (int)(sidePanelRatio*windowSize)));
		JLabel tempText = new JLabel("Will store notation or other things");
		tempText.setVisible(true);
		south.add(tempText);
	}
	
	/*
	* Method name: eastPanelDesign
	* Description: Holds the design properties for the east panel (intermediary method)
	* Input: None
	* Output: None
	*/
	private void eastPanelDesign() {
		east.setVisible(true);
		east.setBackground(Color.GRAY);
		east.setPreferredSize(new Dimension((int)(sidePanelRatio*windowSize), 100));
		JLabel tempText = new JLabel("Will store captured black pieces");
		tempText.setVisible(true);
		east.add(tempText);
	}
	
	/*
	* Method name: addSidePanels
	* Description: Function to handle calling intermediary build methods, and adding them to the frame.
	* Input: None
	* Output: None
	*/
	private void addSidePanels() {
		northPanelDesign();
		eastPanelDesign();
		southPanelDesign();
		westPanelDesign();
		add(north, BorderLayout.NORTH);
		add(south, BorderLayout.SOUTH);
		add(east, BorderLayout.EAST);
		add(west, BorderLayout.WEST);
		revalidate();
	}
	
	/*
	* Method name: addWindowResizeSensor
	* Description: Only needs to be called once. Adds the resize listener (ADD ANYTHING THAT NEEDS TO BE CHANGED WHEN RESIZED TO whenWindowResized)
	* Input: None
	* Output: None
	*/
	private void addWindowResizeSensor() {
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
                            whenWindowResized();
                        });
                    }
                };

                // Schedule the task to run after a delay (e.g., 300 ms)
                resizeTimer.schedule(resizeTask, 300);
            }
        });
	}
	
	private void mouseListenerAdd() {
		//Initialize the corner coordinates
		centerPanelCoordinates[0] = chessBoard.getLocation().x; //Stores left wall
		centerPanelCoordinates[1] = chessBoard.getLocation().y; //Stores top wall
		centerPanelCoordinates[2] = chessBoard.getLocation().x + chessBoard.getWidth(); //Stores right wall
		centerPanelCoordinates[3] = chessBoard.getLocation().y + chessBoard.getHeight(); //Stores bottom wall
		
				
		addMouseListener(new MouseAdapter() {
			@Override
            public void mousePressed(MouseEvent e) {
                // Get the position of the mouse
                //int x = e.getX();
                //int y = e.getY();
            	Point p = e.getPoint();
                
                for (int i = 0; i < 8; i++) {
                	for (int u = 0; u < 8; u++) {
                		if (squareChessBounds[i][u].contains(p)) {
                			System.out.println("X: " + u + " Y: " + i);
                		}
                	}
                }
                
            }
        });

	}
	
	public static void main(String args[]) {
		new ChessGame();
	}
}
