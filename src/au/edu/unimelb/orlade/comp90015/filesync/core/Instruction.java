package au.edu.unimelb.orlade.comp90015.filesync.core;

/**
 * @author aaron
 * @date 7th April 2013
 */

import org.json.simple.parser.JSONParser;

/*
 * All instructions have a Type which is a String name. All instructions can produce a JSON String
 * for network communication. Use the InstructionFactory class to convert a JSON String back to an
 * Instruction class.
 */

public abstract class Instruction {

  protected static final JSONParser parser = new JSONParser();

  public abstract String Type();

  public abstract String ToJSON();

  public abstract void FromJSON(String jst);
}
