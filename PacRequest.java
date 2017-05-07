
import java.io.*;

public class PacRequest implements Serializable
{

  private PacOp op;
  private String name;
  private byte[] data;

  public PacRequest(PacOp op, String name, byte[] data)
  {

    this.op = op;
    this.name = name;
    this.data = data;

  } // pacrequest

  public   PacOp   getOp() { return this.op;   }
  public  String getName() { return this.name; }
  public  byte[] getData() { return this.data; }

} // PacRequest
