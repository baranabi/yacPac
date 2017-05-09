/*
 * This is the yacPac client. 
 * This is how the user executes yacPac commands. 
 *
 * usage: 
 * ./YacPac <command> <arg>
 */

import java.io.*;
import java.net.*;

public class YacPac
{

  private ObjectInputStream input;
  private ObjectOutputStream output;
  private Socket socket;

  public static void main(String[] args)
  {
    new YacPac().start(args);
  }

  private void start(String[] args)
  {
    int rc = 0;
    String command;
    if (args.length < 1)
    {
      System.err.println("ERROR: please specify a command! \"help\" for a list of commands");
      System.exit(1);
    }
    command = args[0];
    
    if (command.equals("help"))
    {
      rc = yacHelp();
    }
 
    else if (command.equals("ls"))
    {
      // handle ls
      sessionInit();
      rc = yacLs();
    }
    else if (command.equals("put"))
    {
      if (args.length < 2)
      {
        System.err.println("ERROR: please specify a filename");
        System.exit(1);
      }
      sessionInit();
      rc = yacPut(args[1]);
    }
    else if (command.equals("get"))
    {
      if (args.length < 2)
      {
        System.err.println("ERROR: please specify a filename");
        System.exit(1);
      }
      sessionInit();
      rc = yacGet(args[1]);
    }
    else if (command.equals("rm"))
    {
      if (args.length < 2)
      {
        System.err.println("ERROR: please specify a filename");
        System.exit(1);
      }
      sessionInit();
      rc = yacRm(args[1]);
    }
    else
    {
      try
      {
        String owner  = command;
        FileWriter fw = new FileWriter("YacPacOwner.java");
        BufferedWriter bw = new BufferedWriter(fw);   // MARVEL AT THIS KLUDGE
        bw.write("public class YacPacOwner { public static final String OWNER = \"" + command + "\";}");
        System.out.println("SET CURRENT OWNER TO " + owner);
        bw.close();
        fw.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      rc = 0;
    }
    String retMsg = rc == 0 ? null : "client exiting with error!";
    cleanUpAndQuit(retMsg, rc);
  } // main
  
  private YacRequest yacReq;
  private YacReply   yacRep;
  private YacOp          op;
  private String    filename;
  private int yacPut(String name)
  {
    filename = name;
    op = YacOp.PUT;
    return doOp();
  } //yacPut
  
  private int yacGet(String name)
  {
    filename = name;
    op = YacOp.GET;
    int doStat = doOp();
    if (doStat != 0) { return doStat; }
    // get is a little different cos we are receiving a payload. 
    // that we write to file. 
    try
    {
      byte [] gotFile = yacRep.getData();
      FileOutputStream fos = new FileOutputStream(filename);
      fos.write(gotFile);
      fos.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return 0;
  } //yacGet
  
  private int yacRm(String name)
  {
    filename = name;
    op = YacOp.RM;
    return doOp();
  } //yacRm
  
  private int yacLs()
  {
    op = YacOp.LS; 
    return doOp();    
  } // yacLs

  private int doOp() 
  {
    yacReq = new YacRequest(op, filename);
    try 
    {
      output.writeObject(yacReq);
      yacRep = (YacReply) input.readObject();
    }
    catch (Exception e)
    {
      return -1;
    }
    if (yacRep.getStatus() != 0)
    {
      System.err.println("ERROR!");
      return yacRep.getStatus();
    }
    else
    {
      if(yacRep.getData() != null)
      {
        System.out.println(yacRep.getMessage());
      } 
      else { System.out.println("operation successful!");}
      return 0;
    }
  }
  
  private int yacHelp()
  {
    System.out.println("YAC COMMANDS:");
    System.out.println("put <filename>: put a file on the yacPac");
    System.out.println("get <filename>: get a file from the yacPac");
    System.out.println("ls : list your files.");
    System.out.println("rm  <filename> : delete a file off the yacPac");
    System.out.println("help : list commands.");
    System.out.println("<string> : sets current owner name to <string>");
    return 0;
  } //yacHelp

  private void sessionInit()
  {
    try
    {
      socket = new Socket(Yac.ADDRESS, Yac.YAC_PORT);
      output = new ObjectOutputStream(socket.getOutputStream());
      input = new ObjectInputStream(socket.getInputStream());
    }
    catch (IOException e)
    {
      cleanUpAndQuit("ERROR: Couldn't connect to the server!",1);
    }
  } // sessionInit
  private void cleanUpAndQuit(String message, int rc)
  {
    try
    {
      output.close();
      input.close();
      socket.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (message != null) System.err.println(message);
      System.exit(rc);
    }
  } // cleanUpAndQuit
} //yacPac client
