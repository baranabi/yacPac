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
  public static void main(String[] args)
  {
    String command;
    if (args.length < 1)
    {
      System.err.println("ERROR: please specify a command! \"help\" for a list of commands");
      System.exit(1);
    }
    command = args[0];

  }
}
