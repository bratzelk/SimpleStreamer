package au.edu.unimelb.orlade.comp90015.filesync.server;

import java.io.IOException;
import java.net.Socket;

import lombok.extern.log4j.Log4j;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer;
import au.edu.unimelb.orlade.comp90015.filesync.core.BlockUnavailableException;
import au.edu.unimelb.orlade.comp90015.filesync.core.Instruction;
import au.edu.unimelb.orlade.comp90015.filesync.core.InstructionFactory;
import au.edu.unimelb.orlade.comp90015.filesync.core.SynchronisedFile;

/**
 * Handles change requests sent by the client.
 */
@Log4j
public class DestinationChangeHandler extends ConnectionBuffer {

  private final SynchronisedFile file;

  public DestinationChangeHandler(SynchronisedFile file, Socket socket) throws IOException {
    super(socket);
    this.file = file;
  }

  /**
   * Listens for requests from the client indicating changes to the source file.
   */
  public void run() {
    while (!isInterrupted()) {
      String message;
      try {
        message = receive();
        respond(process(message));
      } catch (IOException e) {
        log.error("Failed to receive change request", e);
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Handles requests from the client to update the destination file.
   * 
   * @param message A serialized {@link Instruction}.
   */
  protected Response process(String message) {
    InstructionFactory instFact = new InstructionFactory();
    Instruction receivedInst = instFact.FromJSON(message);

    try {
      // Process the instruction.
      file.ProcessInstruction(receivedInst);
      return Response.ACKNOWLEDGED;
    } catch (BlockUnavailableException e) {
      // Request block information if necessary.
      return Response.NEED_BLOCK;
    } catch (IOException e) {
      log.error("Failed to process instruction", e);
      return Response.ERROR;
    }
  }

}
