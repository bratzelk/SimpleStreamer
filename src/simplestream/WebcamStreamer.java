package simplestream;

/**
 * Displays a webcam stream from the default camera.
 */
public class WebcamStreamer {

  private int streamingRate;

  public WebcamStreamer(int streamingRate) {
    this.streamingRate = streamingRate;
  }

  public void init() {
    // TODO: Show the local image viewer.
    // TODO: The StreamViewer currently listens for the enter key. It doesn't do anything when it
    // catches the event yet. This needs to be implemented.
    StreamViewer viewer = new StreamViewer();

    Webcam webcam = new Webcam();

    // TODO: nice exit from this loop.
    while (true) {
      byte[] imageData = webcam.getImage();
      viewer.addImage(imageData);
      try {
        Thread.sleep(streamingRate);
      } catch (InterruptedException e) {
        e.printStackTrace();
        throw new RuntimeException("Webcam streamer was interrupted.");
      }
    }
  }

  public int getStreamingRate() {
    return streamingRate;
  }

}
