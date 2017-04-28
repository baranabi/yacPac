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
  private 
  public YacRequest(YacOp op, String arg)
  {
    this.op = op;
  } // Constructor

} // YacRequest
