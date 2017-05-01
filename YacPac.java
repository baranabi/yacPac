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
  
  public static void main(String[] args)
  {
    int rc;
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
    }
    else if (command.equals("put"))

  } // main
  
  
  private static int yacPut(String name)
  {
    return 0;
  } //yacPut
  
  private static int yacGet(String name)
  {
    return 0;
  } //yacGet
  
  private static int yacRm(String name)
  {
    return 0;
  } //yacRm
  
  private static int yacLs(String name)
  {
    return 0;
  }
  
  private static int yacHelp()
  {
    System.out.println("YAC COMMANDS:");
    System.out.println("put <filename>: put a file on the yacPac");
    System.out.println("get <filename>: get a file from the yacPac");
    System.out.println("ls : list your files.");
    System.out.println("rm  <filename> : delete a file off the yacPac");
    System.out.println("help : list commands.");
    return 0;
  } //yacHelp
  
}
