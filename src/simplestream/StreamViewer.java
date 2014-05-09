package simplestream;


import javax.swing.JFrame;


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
		
	}

	
	/**
	 * Add a frame which is displayed by the viewer
	 * This data should be de-compressed etc and ready to be viewed.
	 * */
	public void addImage(byte[] imageData) {
		myViewer.ViewerInput(imageData);
		frame.repaint();
	}
	

}
