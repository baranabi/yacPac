import java.io.*;

public class CatReply implements Serializable
{
  private int status;
  private String message;

  public CatReply( int status, String message)
  {
    this.status = status;
    this.message = message;
  } // constructor

  // getters
  public int getStatus() { return this.status; }
  public String getMessage() { return this.message;}
} // catReply
