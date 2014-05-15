package au.edu.unimelb.orlade.comp90015.filesync.server;

import java.io.IOException;
import java.net.Socket;

import lombok.extern.log4j.Log4j;
import au.edu.unimelb.orlade.comp90015.filesync.SyncManager;
import au.edu.unimelb.orlade.comp90015.filesync.core.SynchronisedFile;

/**
 * Responsible for receiving changes and persisting them to a file. May be used by client or server.
 */
@Log4j
public class SyncDestination extends SyncManager {

  private DestinationChangeHandler handler;

  public SyncDestination(final String filename, final Socket socket, final int blockSize) {
    super(filename, socket, blockSize);
  }

  @Override
  public void init() {
    log.debug("Initiailising destination manager for " + getFilename() + "...");
    final SynchronisedFile file = openFile();
    try {
      handler = new DestinationChangeHandler(file, getSocket());
      handler.start();
    } catch (IOException e) {
      log.error("Failed to setup change listener", e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void stop() throws IOException {
    log.debug("Stopping server...");
    handler.close();
  }

}
