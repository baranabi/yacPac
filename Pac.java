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
  private String pacName;
  private PacRequest pacReq;
  private PacReply   pacRep;
  private  Socket    yacSock;

  public static void main(String[] args)
  {
    new Pac().start(args);
  }
  public void start(String[] args)
  {
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

    try
    {
      System.out.println("Pac: connecting to Yac and creating streams");
      yacSock  = new Socket(Yac.ADDRESS, Yac.PAC_PORT);
      System.out.println("Pac: ... toYac");
      toYac   = new ObjectOutputStream(yacSock.getOutputStream());

      // register pac w/ yac
      System.out.println("Pac: registering w/ Yac");
      PacRegistration pacReg = new PacRegistration(pacName); 
      toYac.writeObject(pacReg);
      //toYac.close();

      System.out.println("Pac: entering main server loop...");
      // enter server loop
      fromYac = new ObjectInputStream(yacSock.getInputStream());
      while (true)
      {

        //ServerSocket yacListen = new ServerSocket(Yac.PAC_PORT, Yac._BACKLOG);
        System.out.println();
        System.out.println("Pac: waiting for request");
        pacReq = (PacRequest) fromYac.readObject();
        System.out.println("Pac: got request!" );
        pacRep = doOp();
        toYac.writeObject(pacRep);
      } // serverLoop
    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  } // main

  private PacReply doOp()
  {
    PacOp op = pacReq.getOp();
    if (op == PacOp.PUT)      { return pacPut();}
    else if (op == PacOp.GET) { return pacGet();}
    else if (op == PacOp.RM)  { return  pacRm();}
    else { return null;} //<- this shouldn't happen! 
  }

  private PacReply pacPut() 
  {
    System.out.println("putting " + pacReq.getName());
    try
    {
      FileOutputStream fos = new FileOutputStream(pacName + "/" + pacReq.getName());
      fos.write(pacReq.getData());
      fos.close();
      return new PacReply(0, null);
    }
    catch (IOException e)
    {
      System.out.println("put failed!");
      return new PacReply(-1, null);
    }
  }

  private PacReply pacGet()
  {
    System.out.println("getting " + pacReq.getName());
    try
    {
      File f = new File(pacName + "/" + pacReq.getName());
      int byteLength = (int) f.length();
      byte[] data = new byte[byteLength];
      FileInputStream fis = new FileInputStream(f);
      fis.read(data,0,byteLength);
      fis.close();
      return new PacReply(0, data);
    }
    catch (IOException e)
    {
      System.out.println("get failed!");
      return new PacReply(-1, null);
    }
  }

  private PacReply pacRm()
  {
    System.out.println("deleting " + pacReq.getName());
    try
    {
      File f = new File(pacName + "/" + pacReq.getName());
      f.delete();
      return new PacReply(0, null);
    }
    catch (SecurityException e)
    {
      System.out.println("rm failed!");
      return new PacReply(-1, null);
    }  
  } 
}//Pac
