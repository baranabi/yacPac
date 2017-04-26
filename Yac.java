import java.io.*;
import java.net.*;

/*
 * Main interfacing point between the user and the infrastructure. 
 * Spawns thread for each connection request. 
 */


public class Yac
{
  public static void main(String args[])
  {
    ServerSocket listen;
    Socket socket;
    YacThread yacThr;

    try
    {
      listen = new ServerSocket(9999,5);

      while (true)
      {
        System.out.println();
        System.out.println("server: waiting for connection");

        socket = listen.accept();

        System.out.println("server: creating thread for socket");

        yacThr = new YacThread(socket);
        yacThr.start();
      }
    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  } // Main
} // Yac
