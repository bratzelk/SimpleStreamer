package simplestream.client;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import simplestream.webcam.Webcam;

/**
 * Displays images from a {@link Webcam} in a Java Swing GUI, using a
 * {@link Viewer}.
 */
public class StreamViewer {

	private static final Logger log = Logger.getLogger(StreamViewer.class);

	private final Viewer myViewer;
	private final JFrame frame;

	/** A callback to invoke when the user exit the viewer. */
	private final Runnable exitCallback;

	// Listen for a shutdown action, then invoke the exit callback.
	@SuppressWarnings("serial")
	Action shutdownAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			if (exitCallback != null) {
				exitCallback.run();
			} else {
				log.warn("No exit callback to run on " + this);
			}
			close();
		}
	};

	/**
	 * Creates a new {@link StreamViewer} instance to display webcam images.
	 *
	 * @param exitCallback
	 *            A callback to run when the user exits the program.
	 */
	public StreamViewer(Runnable exitCallback) {

		this.myViewer = new Viewer();
		this.frame = new JFrame("Simple Stream Viewer");
		this.exitCallback = exitCallback;

		frame.setVisible(true);
		frame.setSize(320, 240);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(myViewer);

		// Attach a listener for the Enter key to shutdown the program.
		myViewer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "shutdownAction");
		myViewer.getActionMap().put("shutdownAction", shutdownAction);
	}

	/**
	 * Add a frame which is displayed by the viewer. This data should be
	 * de-compressed and ready to be viewed.
	 */
	public void addImage(byte[] imageData) {
		myViewer.ViewerInput(imageData);
		frame.repaint();
	}

	/**
	 * Closes the viewer window.
	 */
	public void close() {
		frame.dispose();
	}

}
