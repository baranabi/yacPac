import java.io.*;

public class CatReply implements Serializable
{
  private int status;
  private int message;

  public CatReply( int status, int message)
  {
    this.status = status;
    this.message = message;
  } // constructor

  // getters
  public int getStatus() { return this.status; }
  public String getMessage() { return this.message;}
} // catReply
