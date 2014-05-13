package simplestream;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


/**
 * I made this very quickly by copying the code from Aaron's LocalView class.
 * It should work the same way but instead of reading from the webcam it should be given an image to display.
 * It may require some changes...
 * */

public class StreamViewer {
	
	Viewer myViewer;
	JFrame frame;
	
	public StreamViewer() {
		
		myViewer = new Viewer();
		frame = new JFrame("Simple Stream Viewer");
		
		frame.setVisible(true);
		frame.setSize(320, 240);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(myViewer);
		

		//Attach a listener for the Enter key to shutdown the program.
		myViewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "shutdownAction");
		myViewer.getActionMap().put("shutdownAction", shutdownAction);
	}

	
	//TODO: Actually send the shutdown message
	//This action is run when the user inputs the shutdown command (enter key).
	@SuppressWarnings("serial")
	Action shutdownAction = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Sending shutdown message...");
            //TODO: Actually send the shutdown message
        }
    };
    
	
	/**
	 * Add a frame which is displayed by the viewer
	 * This data should be de-compressed etc and ready to be viewed.
	 * */
	public void addImage(byte[] imageData) {
		myViewer.ViewerInput(imageData);
		frame.repaint();
	}
	

	
}
