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
	private double sidePanelRatio = .15; //How much space of the window does the side panel take up
	JPanel east = new JPanel();
	JPanel west = new JPanel();
	JPanel south = new JPanel();
	JPanel north = new JPanel();
	
	public ChessGame() {
		
		ChessBoard chessBoard = new ChessBoard();
		
		//Set layout for JFrame
		setVisible(true);
		setSize(new Dimension(windowSize, windowSize));
		setLayout(new BorderLayout());
		add(chessBoard, BorderLayout.CENTER);
		
		//Change in the future, just test
		addSidePanels();
		addWindowResizeSensor();
		
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
		east.setPreferredSize(new Dimension((int)(sidePanelRatio*windowSize), 0));
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
	
	public static void main(String args[]) {
		new ChessGame();
	}
}
