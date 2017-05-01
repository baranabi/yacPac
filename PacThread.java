import java.io.*;
import java.net.*;

public class PacThread extends Thread
{
  private Socket pacSock;
  private String   owner;
  private ObjectInputStream input;
  private ObjectOutputStream output;

  public PacThread(Socket s)
  {
    this.pacSock = s;
    try 
    {
      this.input  = new  ObjectInputStream(this.yacSock.getInputStream());
      this.output = new ObjectOutputStream(this.yacSock.getOutputStream());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  } // constructor

  public void run()
  {
    // handle yac Requests.
    try
    {
      YacRequest yacReq = (YacRequest) input.readObject();

    }
  }
}
