import java.io.*;
import java.net.*;

public class Cat
{
  public static void main(String args[])
  {
    ServerSocket listen;
    Socket socket;
    
    try
    {
      listen = new ServerSocket(9999,5);

      while (true)
      {
      }
    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  } // main
} // Cat
