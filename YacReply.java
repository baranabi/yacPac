
import java.io.*;

public class YacReply extends Serializable
{
  private int status;
  private byte[] data;

  public YacReply(int status, byte[] data)
  {
    this.status = status;
    this.data   = data;
  }

  public int getStatus()
  {
    return this.status;
  }

  public byte[] getData()
  {
    return this.data;
  }
}
