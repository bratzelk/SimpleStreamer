package simplestream;

import messages.MessageFactory;
import messages.MessageNotFoundException;
import messages.OverloadedResponseMessage;
import common.Out;
import common.Strings;

/**
 * Displays a webcam stream from a remote camera.
 */

public class RemoteWebcamStreamer extends WebcamStreamer {

  private final String remoteHostname;
  private final int remotePort;

  public RemoteWebcamStreamer(int streamingRate, String remoteHostname, int remotePort) {
    super(streamingRate);
    this.remoteHostname = remoteHostname;
    this.remotePort = remotePort;
  }

  public void init() {// This needs to be added to the overloadedMessage.
    Peer remoteServer = new Peer(remoteHostname, remotePort);

    // TODO: This is an example message
    try {
      OverloadedResponseMessage overloadedMessage =
          (OverloadedResponseMessage) MessageFactory
              .createMessage(Strings.OVERLOADED_RESPONSE_MESSAGE);
      overloadedMessage.addServer(remoteServer);
      // overloadedMessage.addClients(clients);
      Out.print(overloadedMessage.toJSON());
    } catch (MessageNotFoundException e) {
      throw new IllegalArgumentException("Overloaded message unknown", e);
    }

    // TODO: The messages themselves don't compress any byte arrays.
    // You need to do this explicitly before adding the data to a message.

    // TODO: display remote stream.
  }

}
