/**
 * YacRequest: 
 * structured request from YacPac client to Yac server. 
 * Includes the operation, and if applicable, the data payload. 
 *
*/

import java.io.*;

public class YacRequest implements Serializable 
{
  private YacOp op;
  private String filename;
  private String owner;
  private FileInputStream fileInput;
  private byte[] data;
  private int size;

  public YacRequest(YacOp op, String arg)
  {
    this.op = op;
    this.filename = arg;
    this.owner = YacPacOwner.OWNER;
    this.size  = 0;
    if (this.filename != null)
    {
      try
      {
        File inFile = new File(this.filename);
        fileInput = new FileInputStream(inFile);
        int byteLength = (int) inFile.length();
        this.size = byteLength;
        data = new byte[byteLength];
        fileInput.read(data,0,byteLength);
      }
      catch (Exception e)
      {
        System.err.println(e);
        System.exit(1);
        
      } // trycatch for reading in file data
    }    
  } // Constructor


  // just some getters
  public YacOp getOp()
  {
    return this.op;
  }

  public String getOwner()
  {
    return this.owner;
  }

  public String getFileName()
  {
    return this.filename;
  }

  public byte[] getData()
  {
    return this.data;
  }
  
  public int getSize()
  {
    return this.size;
  }

} // YacRequest
