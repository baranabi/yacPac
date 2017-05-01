import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Main interfacing point between the user and the infrastructure. 
 * Spawns thread for each connection request. 
 */


public class Yac
{
  public static final int YAC_PORT = 4000;
  public static final int PAC_PORT = 5000;
  public static final int _BACKLOG =    5;
  
  private List<YacThread> clients = 
    Collections.synchronizedList(newArrayList<YacThread>());
  private List<PacThread>  pacs = 
    Collections.synchronizedList(newArrayList<PacThread>());
  
  
  
  public static void main(String args[])
  {
    
    ServerSocket clientSock;
    ServerSocket    pacSock; 
    Socket socket;
    YacThread yacThr;

    try
    {
      clientSock; = new ServerSocket(YAC_PORT,_BACKLOG);
      pacSock     = new ServerSocket(PAC_PORT,_BACKLOG); 
      System.out.println("Yac: listening for clients on " + YAC_PORT);
      System.out.println("Yac: listening for pacs on " + PAC_PORT);

      while (true)
      {
        System.out.println();
        System.out.println("server: waiting for connection");

        socket = listen.accept();

        System.out.println("server: creating thread for socket");

        yacThr = new YacThread(socket);
        yacThr.start();
        clients.add(yacThr);
      }
    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  } // Main
} // Yac
