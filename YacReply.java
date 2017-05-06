
import java.io.*;

public class YacReply extends Serializable
{
  private int status;
  private byte[] data;

  public YacReply(int status, byte[] data)
  {
    this.status = status;
    this.data   = data;
  } // constructor

  // getters

  public int getStatus()
  {
    return this.status;
  }

  public byte[] getData()
  {
    return this.data;
  }

  public String getMessage()
  {
    return new String(this.data);
  }
} //yacReply
