import java.io.*;

public class CatRequest implements Serializable
{
  private CatOp        op;
  private String filename;
  private String    owner;
  private    int     size;
  
  public CatRequest(CatOp op, String filename, String owner, int size)
  {
    this.op = op;
    this.filename = filename;
    this.owner = owner;
    this.size = size;
  }// COnstructor
  
  // getters
  public CatOp getOp()
  {
    return this.op;
  }
  
  public String getName()
  {
    return this.filename;
  }
  
  public String getOwner()
  {
    return this.owner;
  }
  
  public     int  getSize()
  {
    return this.size;
  }
}// CatRequest
