package simplestream;


import javax.swing.JFrame;

import org.apache.commons.codec.binary.Base64;
import org.bridj.Pointer;

import com.github.sarxos.webcam.ds.buildin.natives.Device;
import com.github.sarxos.webcam.ds.buildin.natives.DeviceList;
import com.github.sarxos.webcam.ds.buildin.natives.OpenIMAJGrabber;

public class LocalView {
	
	Viewer myViewer;
	JFrame frame;
	
	byte[] rawImageData;
	
	public LocalView() {
		
		myViewer = new Viewer();
		frame = new JFrame("Simple Stream Viewer");
		
	}
	
	
	private void setImageData(byte[] rawImageData) {
		this.rawImageData = rawImageData;
	}
	
	public byte[] setImageData() {
		return this.rawImageData;
	}
	
	public void start() {

		frame.setVisible(true);
		frame.setSize(320, 240);
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(myViewer);
		
		/**
		 * This example show how to use native OpenIMAJ API to capture raw bytes
		 * data as byte[] array. It also calculates current FPS.
		 */

		OpenIMAJGrabber grabber = new OpenIMAJGrabber();

		Device device = null;
		Pointer<DeviceList> devices = grabber.getVideoDevices();
		for (Device d : devices.get().asArrayList()) {
			device = d;
			break;
		}

		boolean started = grabber.startSession(320, 240, 30, Pointer.pointerTo(device));
		if (!started) {
			throw new RuntimeException("Not able to start native grabber!");
		}

		int n = 1000;
		int i = 0;
		do {
			/* Get a frame from the webcam. */
			grabber.nextFrame();
			/* Get the raw bytes of the frame. */
			byte[] raw_image=grabber.getImage().getBytes(320 * 240 * 3);
			
			
			//Set the raw image data so that it is publicly available
			setImageData(raw_image);
			
			
			//TODO: Remove this and do the compression externally
			
			/* Apply a crude kind of image compression. */
			byte[] compressed_image = Compressor.compress(raw_image);
			/* Prepare the date to be sent in a text friendly format. */
			byte[] base64_image = Base64.encodeBase64(compressed_image);

			
			//TODO: Remove this and do the de-compression externally
			
			byte[] nobase64_image = Base64.decodeBase64(base64_image);
			/* Decompress the image */
			byte[] decompressed_image = Compressor.decompress(nobase64_image);
			
			
			/* Give the raw image bytes to the viewer. */
			myViewer.ViewerInput(decompressed_image);
			frame.repaint();
		} while (++i < n);

		grabber.stopSession();
		
	}
	

}
