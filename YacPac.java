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
  private static final String ADDRESS = "localhost";
  private boolean waitingForResponse = false;

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
    }
    else if (command.equals("rm"))
    {
      if (args.length < 2)
      {
        System.err.println("ERROR: please specify a filename");
        System.exit(1);
      }
      sessionInit();
    }
    else
    {
      System.err.println("ERROR: unrecognized command! \"help\" for a list of commands");
      System.exit(1);
    }
    String retMsg = rc == 0 ? null : "client exiting with error!";
    cleanUpAndQuit(retMsg, rc);
  } // main
  
  
  private int yacPut(String name)
  {
    return 0;
  } //yacPut
  
  private int yacGet(String name)
  {
    return 0;
  } //yacGet
  
  private int yacRm(String name)
  {
    return 0;
  } //yacRm
  
  private int yacLs()
  {
    return 0;
  }
  
  private int yacHelp()
  {
    System.out.println("YAC COMMANDS:");
    System.out.println("put <filename>: put a file on the yacPac");
    System.out.println("get <filename>: get a file from the yacPac");
    System.out.println("ls : list your files.");
    System.out.println("rm  <filename> : delete a file off the yacPac");
    System.out.println("help : list commands.");
    return 0;
  } //yacHelp

  private void sessionInit()
  {
    try
    {
      socket = new Socket(ADDRESS, Yac.YAC_PORT);
      output = new ObjectOutputStream(socket.getOutputStream());
      input = new ObjectInputStream(socket.getInputStream());
    }
    catch (IOException e)
    {
      cleanUpAndQuit("ERROR: Couldn't connect to the server!",1);
    }
  }
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
  }
  
}
