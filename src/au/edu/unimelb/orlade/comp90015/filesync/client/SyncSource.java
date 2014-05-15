package au.edu.unimelb.orlade.comp90015.filesync.client;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import lombok.extern.log4j.Log4j;
import au.edu.unimelb.orlade.comp90015.filesync.SyncManager;
import au.edu.unimelb.orlade.comp90015.filesync.core.SynchronisedFile;

/**
 * Responsible for watching a local file and sending changes when they are detected. May be used by
 * client or server.
 */
public class SyncSource extends SyncManager {

  Logger log = Logger.getLogger(getClass());

  private SourceChangeHandler handler;

  public SyncSource(final String filename, final Socket socket, final int blockSize) {
    super(filename, socket, blockSize);
  }

  /**
   * Creates the necessary components and begins the file watching process.
   */
  public void init() {
    log.debug("Initiailising source manager for " + getFilename() + "...");
    final SynchronisedFile file = openFile();
    try {
      handler = new SourceChangeHandler(file, getSocket());
      handler.start();
    } catch (IOException e) {
      log.error("Failed to setup change listener", e);
      throw new IllegalStateException(e);
    }

    watcher = new FileWatcher(file);
    watcher.start();
  }

  /**
   * Clean up the running services.
   */
  public void stop() {
    log.debug("Stopping source...");
    watcher.interrupt();
    handler.interrupt();
  }

}
