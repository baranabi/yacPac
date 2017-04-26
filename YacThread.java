import java.io.*;
import java.net.*;

public class YacThread extends Thread
{
  private Socket yacSock;

  public YacThread(Socket socket)
  {
    yacSock = socket;
  }

  public void run()
  {
    // handle yac Requests.
    try
    {
      inetAdr = yacSock.getInetAddress();
      System.out.println("YacThread: connection from " + inetAddr.toString());

      // creating i/o streams
      fromClient = new BufferedReader( new InputStreamReader(yacSock.getInputStream()));
      toClient   = new PrintWriter(mySocket.getOutputStream());
    }
  }
}
