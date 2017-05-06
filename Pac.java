/**
 * Pac.java
 * pac server, resource server that holds the files allotted by yac server. 
 */

import java.io.*;
import java.net.*;




public class Pac
{
  private ObjectInputStream fromYac;
  private ObjectOutputStream  toYac;

  public static void main(String[] args)
  {
    String pacName;
    if (args.length < 1)
    {
      System.err.println("Pac: ERROR: please specify server name!");
      System.exit(1);
    }
    pacName = args[0];
    File pacDir = new File(pacName);
    if (!pacDir.mkdirs())
    {
      System.err.println("Pac: ERROR: could not create pac directory for " + pacName);
      System.exit(1);
    }
    ServerSocket listen;
    Socket socket;

    try
    {
      listen = new ServerSocket(Yac.PAC_PORT, Yac._BACKLOG);
     
      // register pac w/ yac

      

      // enter server loop
      while (true)
      {
        System.out.println();
        System.out.println("Pac: waiting for connection");

        socket = listen.accept();

        System.out.println("Pac: accepted connection, processing request.");
      } // serverLoop
    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  } // main
}//Pac
