package au.edu.unimelb.orlade.comp90015.filesync.core;

import lombok.Getter;
import lombok.Setter;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@Getter
@Setter
public class ConfigInstruction extends Instruction {

  private String direction;
  private int blockSize;

  public ConfigInstruction() {}

  public ConfigInstruction(final String direction, final int blockSize) {
    this.direction = direction;
    this.blockSize = blockSize;
  }

  @Override
  public String Type() {
    return "Config";
  }

  @SuppressWarnings("unchecked")
  @Override
  public String ToJSON() {
    JSONObject obj = new JSONObject();
    obj.put("Type", Type());
    obj.put("direction", direction);
    obj.put("blocksize", blockSize);
    return obj.toJSONString();
  }

  @Override
  public void FromJSON(String jst) {
    JSONObject obj = null;

    try {
      obj = (JSONObject) parser.parse(jst);
    } catch (ParseException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    if (obj != null) {
      setDirection((String) obj.get("direction"));
      setBlockSize(Integer.valueOf(obj.get("blocksize").toString()));
    }
  }

}
