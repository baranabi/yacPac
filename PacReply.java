import java.io.*;

public class PacReply implements Serializable
{
  private int status;
  private byte[] data;
  
  public PacReply(int status, byte[] data)
  {
    this.status = status;
    this.data   = data;
  }

  public int getStatus  { return this.status; }
  public byte[] getData { return   this.data; }
}
