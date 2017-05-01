import java.io.*;
import java.net.*;

public class YacThread extends Thread
{
  private Socket yacSock;
  private String   owner;
  private ObjectInputStream input;
  private ObjectOutputStream output;

  public YacThread(Socket s)
  {
    this.yacSock = s;
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
