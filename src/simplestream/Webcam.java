package simplestream;

import org.bridj.Pointer;

import com.github.sarxos.webcam.ds.buildin.natives.Device;
import com.github.sarxos.webcam.ds.buildin.natives.DeviceList;
import com.github.sarxos.webcam.ds.buildin.natives.OpenIMAJGrabber;
import common.Out;

	public class Webcam {
		
		OpenIMAJGrabber grabber;
		Device device;
		
		public Webcam() {
			grabber = new OpenIMAJGrabber();
			
			device = null;
			
			//Use the first device.
			Pointer<DeviceList> devices = grabber.getVideoDevices();
			for (Device d : devices.get().asArrayList()) {
				device = d;
				break;
			}
		
			boolean started = grabber.startSession(320, 240, 30, Pointer.pointerTo(device));
			
			if (!started) {
				throw new RuntimeException("Not able to start native grabber!");
			}			
		}
		
		
		//TODO: Finish this and return an image byte array.
		public byte[] getImage() {

			grabber.nextFrame();
			byte[] rawImageData = grabber.getImage().getBytes(320 * 240 * 3);
			
			//TODO: I don't know when you're supposed to call this. I think it is when you shut down.
			//grabber.stopSession();
			
			Out.print("" + rawImageData);
			
			return rawImageData;
		}
		

	

		
	}
