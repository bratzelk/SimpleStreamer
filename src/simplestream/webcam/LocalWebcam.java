package simplestream.webcam;

import org.apache.log4j.Logger;
import org.bridj.Pointer;

import com.github.sarxos.webcam.ds.buildin.natives.Device;
import com.github.sarxos.webcam.ds.buildin.natives.DeviceList;
import com.github.sarxos.webcam.ds.buildin.natives.OpenIMAJGrabber;

/**
 * Accesses the local machine's webcam to grab images from.
 */
public class LocalWebcam implements Webcam {

	private final Logger log = Logger.getLogger(getClass());

	/** The local machine's webcam. */
	private Device device;

	/** Retrieves image frames from the local machine's webcam. */
	private final OpenIMAJGrabber grabber;

	public LocalWebcam() {
		grabber = new OpenIMAJGrabber();

		// Use the first device.
		Pointer<DeviceList> devices = grabber.getVideoDevices();
		for (Device d : devices.get().asArrayList()) {
			device = d;
			break;
		}
		// Ensure a webcam was found.
		if (device == null) {
			throw new IllegalStateException("No local webcam device found");
		}

		boolean started = grabber.startSession(320, 240, 30,
				Pointer.pointerTo(device));

		if (!started) {
			throw new RuntimeException("Not able to start native grabber!");
		}
	}

	/**
	 * Grabs the next image frame from the local webcam and return it as a byte
	 * array.
	 */
	public byte[] getImage() {
		grabber.nextFrame();
		return grabber.getImage().getBytes(320 * 240 * 3);
	}

	@Override
	public void kill() {
		log.debug("Shutting down " + this + "...");
		// grabber.stopSession(); // This causes things to crash.
		log.debug(this + " shut down successfully");
	}

}
