import java.io.*;
import java.net.*;

public class Cat
{
  private ObjectInputStream fromYac;
  private ObjectOutputStream toYac;

  public static void main(String args[])
  {
    Socket socket;
    
    try
    {
      socket = new Socket(Yac.ADDRESS, Yac.CAT_PORT);
      
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
