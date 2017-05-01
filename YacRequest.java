/**
 * YacRequest: 
 * structured request from YacPac client to Yac server. 
 * Includes the operation, and if applicable, the data payload. 
 *
*/

import java.io.*;

public class YacRequest
{
  private YacOp op;
  private String filename;
  private String owner;
  private FileInputStream fileInput;
  
  public YacRequest(YacOp op, String arg)
  {
    this.op = op;
    this.filename = arg;
    try
    {
      fileInput = new FileInputStream("yacpac.user");
      
    
  } // Constructor

} // YacRequest
