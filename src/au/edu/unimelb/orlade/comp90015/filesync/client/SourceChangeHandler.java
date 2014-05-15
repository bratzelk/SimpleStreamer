package au.edu.unimelb.orlade.comp90015.filesync.client;

import java.io.IOException;
import java.net.Socket;

import lombok.extern.log4j.Log4j;
import au.edu.unimelb.orlade.comp90015.filesync.ConnectionBuffer;
import au.edu.unimelb.orlade.comp90015.filesync.core.CopyBlockInstruction;
import au.edu.unimelb.orlade.comp90015.filesync.core.Instruction;
import au.edu.unimelb.orlade.comp90015.filesync.core.NewBlockInstruction;
import au.edu.unimelb.orlade.comp90015.filesync.core.SynchronisedFile;

/**
 * Sends change requests to the server.
 */
@Log4j
public class SourceChangeHandler extends ConnectionBuffer {

  private final SynchronisedFile file;

  public SourceChangeHandler(SynchronisedFile file, Socket socket) throws IOException {
    super(socket);
    this.file = file;
  }

  @Override
  public void run() {

    while (!isInterrupted()) {
      Instruction instruction = file.NextInstruction();
      if (instruction == null) {
        continue;
      }

      String message = instruction.ToJSON();
      log.debug("New instruction: " + message);
      Response response = send(message);
      log.debug("Received response: " + response);

      if (response == Response.NEED_BLOCK) {
        Instruction newBlock = new NewBlockInstruction((CopyBlockInstruction) instruction);
        String blockInstruction = newBlock.ToJSON();
        Response newResponse = send(blockInstruction);
        if (newResponse != Response.ACKNOWLEDGED) {
          throw new RuntimeException("Server failed to handle message:" + message);
        }
      }
    }
    log.debug("Closing client change handler...");
  }

}
