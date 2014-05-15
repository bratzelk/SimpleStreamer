package au.edu.unimelb.orlade.comp90015.filesync.server;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.kohsuke.args4j.Option;

import au.edu.unimelb.orlade.comp90015.filesync.AbstractFileSync;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer.Response;
import au.edu.unimelb.orlade.comp90015.filesync.client.SyncSource;
import au.edu.unimelb.orlade.comp90015.filesync.core.ConfigInstruction;
import au.edu.unimelb.orlade.comp90015.filesync.core.InstructionFactory;
import au.edu.unimelb.orlade.comp90015.filesync.server.ConnectionListener.Callback;

/**
 * Listens for and responds to requests to synchronize files.
 */
public class SimpleStreamerServer extends AbstractFileSync {

  Logger log = Logger.getLogger(getClass());

  private static final int DEFAULT_PORT = 7899;

  public static SimpleStreamerServer instance;

  private final int port;

  private ConnectionListener listener;

  @Option(name = "-file", usage = "the name of the file to synchronize to")
  private String filename;

  /**
   * The callback to invoke when a new client connection is received.
   */
  private final Callback clientConnectionCallback = new Callback() {

    /**
     * Parses the {@link ConfigInstruction} from the client on the given socket.
     *
     * @throws IOException
     */
    private ConfigInstruction receiveConfig(final ConnectionBuffer directionBuffer)
        throws IOException {
      final String configJson = directionBuffer.receive();
      log.debug("Received configuration " + configJson);
      return (ConfigInstruction) new InstructionFactory().FromJSON(configJson);
    }

    @Override
    public void onRequest(final Socket clientSocket) {
      try {
        ConnectionBuffer directionBuffer = new ConnectionBuffer(clientSocket);
        ConfigInstruction config = receiveConfig(directionBuffer);
        if (config.getDirection().equals("push")) {
          setManager(new SyncSource(filename, clientSocket, config.getBlockSize()));
        } else if (config.getDirection().equals("pull")) {
          setManager(new SyncDestination(filename, clientSocket, config.getBlockSize()));
        } else {
          throw new IllegalArgumentException("Unknown direction: " + config.getDirection());
        }
        directionBuffer.respond(Response.ACKNOWLEDGED);
        getManager().init();
      } catch (IOException e) {
        throw new IllegalStateException("Failed to connect with client", e);
      }
    }
  };

  public SimpleStreamerServer(final int port) {
    this.port = port;
  }

  public SimpleStreamerServer() {
    this(DEFAULT_PORT);
  }

  /**
   * Initializes a file sync server that listens for and responds to client requests.
   */
  public static void main(String args[]) {
    instance = new SimpleStreamerServer();
    instance.parseCommandLine(args);
    instance.init();
  }

  public void init() {
    Logger root = Logger.getRootLogger();
    root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));

    try {
      listener = new ConnectionListener(port, clientConnectionCallback);
      listener.start();
    } catch (IOException e) {
      log.error("Listen socket error", e);
    }

  }

  public void stop() throws IOException {
    log.debug("Stopping server...");
    super.stop();
    listener.close();
  }

}
