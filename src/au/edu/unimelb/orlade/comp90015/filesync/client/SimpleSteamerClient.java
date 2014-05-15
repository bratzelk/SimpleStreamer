package au.edu.unimelb.orlade.comp90015.filesync.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.kohsuke.args4j.Option;

import au.edu.unimelb.orlade.comp90015.filesync.AbstractFileSync;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer.Response;
import au.edu.unimelb.orlade.comp90015.filesync.core.ConfigInstruction;
import au.edu.unimelb.orlade.comp90015.filesync.server.SyncDestination;

/**
 * Polls for changes to the specified file and synchronises them to the server.
 */
public class SimpleSteamerClient extends AbstractFileSync {

  Logger log = Logger.getLogger(getClass());

  private static final int DEFAULT_PORT = 7899;
  private static final String DEFAULT_HOSTNAME = "localhost";
  private static final String DEFAULT_DIRECTION = "push";
  private static final int DEFAULT_BLOCKSIZE = 1024; // 1 KB

  public static SimpleSteamerClient instance;

  private final int port;
  private Socket socket;

  @Option(name = "-file", usage = "the name of the file to synchronize to")
  private String filename;

  @Option(name = "-host", usage = "the hostname of the server to connect to")
  private String serverHostname;

  @Option(name = "-direction", usage = "whether this client is a source (push) or destination (pull) for changes")
  private String direction = DEFAULT_DIRECTION;

  @Option(name = "-blocksize", usage = "the number of bytes per block of file to sync")
  private int blockSize = DEFAULT_BLOCKSIZE;

  public SimpleSteamerClient(final int port, final String filename, final String serverHostname) {
    this.port = port;
    this.filename = filename;
    this.serverHostname = serverHostname;
  }

  public SimpleSteamerClient(final String filename, final String serverHostname) {
    this(DEFAULT_PORT, filename, serverHostname);
  }

  public SimpleSteamerClient(final String filename) {
    this(DEFAULT_PORT, filename, DEFAULT_HOSTNAME);
  }

  public SimpleSteamerClient(String[] commandLineArgs) {
    this.port = DEFAULT_PORT;
    parseCommandLine(commandLineArgs);
  }

  public static void main(String args[]) {
    instance = new SimpleSteamerClient(args);
    instance.init();
  }

  public void init() {
    Logger root = Logger.getRootLogger();
    root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));

    try {
      this.socket = bind(serverHostname, port);
    } catch (UnknownHostException e) {
      log.error("Failed to bind", e);
    } catch (IOException e) {
      log.error("Failed to bind", e);
    }

    if (direction.equals("push")) {
      setManager(new SyncSource(filename, socket, blockSize));
    } else {
      setManager(new SyncDestination(filename, socket, blockSize));
    }
    getManager().init();
  }

  /**
   * Create a socket to connect to the server.
   */
  protected Socket bind(final String serverHostname, final int port) throws UnknownHostException,
      IOException {
    Socket socket = new Socket(serverHostname, port);
    log.debug("Established connection to " + serverHostname + ":" + port);

    // Establish the direction of the connection.
    final String serverDirection = direction.equals("push") ? "pull" : "push";
    ConfigInstruction config = new ConfigInstruction(serverDirection, blockSize);
    Response response = new ConnectionBuffer(socket).send(config.ToJSON());
    if (response != Response.ACKNOWLEDGED) {
      throw new RuntimeException("Failed to negotiate direction");
    }
    return socket;
  }

  /**
   * Cleans up the running services.
   *
   * @throws IOException
   */
  public void stop() throws IOException {
    log.debug("Stopping client...");
    super.stop();
  }

}
