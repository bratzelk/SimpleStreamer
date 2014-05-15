package au.edu.unimelb.orlade.comp90015.filesync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Argument parser for a client or server subclass that can be run from the command line.
 */
public abstract class AbstractFileSync {

  /**
   * The manager class that handles sending or receiving change requests, depending on whether this
   * is a source or destination.
   */
  @Getter
  @Setter
  private SyncManager manager;

  @Argument
  private List<String> arguments = new ArrayList<String>();

  public void parseCommandLine(String[] args) {
    CmdLineParser parser = new CmdLineParser(this);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      System.err.println("java FileSyncServer [options...] arguments...");
      parser.printUsage(System.err);
      System.err.println();

      // print option sample. This is useful some time
      System.err.println("  Example: java FileSyncServer");
      parser.printUsage(System.out);
    }
  }

  public void stop() throws IOException {
    manager.stop();
  }

}
