package au.edu.unimelb.orlade.comp90015.filesync;

import java.io.IOException;
import java.net.Socket;

import lombok.Getter;
import au.edu.unimelb.orlade.comp90015.filesync.core.SynchronisedFile;

/**
 * Wraps logic for managing either source or destination of the file sync.
 */
@Getter
public abstract class SyncManager {

  private final String filename;
  private final Socket socket;
  private final int blockSize;

  public SyncManager(final String filename, final Socket socket, final int blockSize) {
    this.filename = filename;
    this.socket = socket;
    this.blockSize = blockSize;
  }

  /**
   * Creates a {@link SynchronisedFile} wrapper for the managed file.
   */
  protected SynchronisedFile openFile() {
    try {
      return new SynchronisedFile(filename, blockSize);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to connect to file " + filename, e);
    }
  }

  /**
   * Creates the necessary resources and runs the manager.
   */
  public abstract void init();

  /**
   * Clean up the running services.
   */
  public abstract void stop() throws IOException;

}
