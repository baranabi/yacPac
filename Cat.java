import java.io.*;
import java.net.*;
import java.util.*;
public class Cat
{
  private ObjectInputStream fromYac;
  private ObjectOutputStream toYac;

  private CatRequest catReq;
  private CatReply   catRep;
  private CatOp          op;

  private List<PacEntry> pacs =
    Collections.synchronizedList(new ArrayList<PacEntry>()); 
  private List<OwnerEntry> owners = 
    Collections.synchronizedList(new ArrayList<OwnerEntry>());

  public static void main(String args[])
  {
    new Cat().start();
  }

  public void start()
  {
    Socket socket;
    
    try
    {
      System.out.println("Cat: registering w/ Yac...");
      socket = new Socket(Yac.ADDRESS, Yac.CAT_PORT);

      fromYac = new ObjectInputStream(socket.getInputStream());
      toYac   = new ObjectOutputStream(socket.getOutputStream());
      
      while (true)
      {
        try
        {
          System.out.println();
          System.out.println("Cat: waiting for request...");
          catReq = (CatRequest) fromYac.readObject();
          this.op = catReq.getOp();

          System.out.println("Cat: got request!");
          
          if (catReq.getOp() == CatOp.CAT_REGPAC) 
          { 
            System.out.println("Cat: registering pac server " + catReq.getName());
            addPac();        
          }
          else
          {
            System.out.println("Cat: doing operation");

            catRep = doOp();
            toYac.writeObject(catRep);
          }
        }
        catch (Exception e)
        {
          System.err.println("Cat: " + e.toString()); 
        }
      }
    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  } // main


  private OwnerEntry currOwner;
  private   PacEntry nextPac;
  private CatReply doOp()
  {
    if (this.op == CatOp.CAT_PUT)      { return catPut(); }
    else if (this.op == CatOp.CAT_GET) { return catGet(); }
    else if  (this.op == CatOp.CAT_RM) { return  catRm(); }
    else if  (this.op == CatOp.CAT_LS) { return  catLs(); }
    else { return new CatReply(-1, "OP NOT RECOGNIZED"); } // this shouldn't happen!
  }

  private CatReply catPut()
  {
    System.out.println("....PUT");
    currOwner = findOwner();
    if (currOwner == null) 
    { 
      System.out.println("Cat: creating new owner: " + catReq.getOwner());
      currOwner = new OwnerEntry(catReq.getOwner()); 
      owners.add(currOwner);
    }
    System.out.println("Cat: finding location for file...");
    nextPac = getNextPac();

    System.out.println("Cat: adding file to cataloc...");
    int rc = currOwner.addFile(
        catReq.getName(), nextPac.getName(), catReq.getSize());
    if (rc != 0) 
    { return new CatReply( -1 ,  "Cat: put: file already exists!");}
    nextPac.addBytes(catReq.getSize());
    return new CatReply( 0, nextPac.getName());
  }

  private CatReply catGet()
  {
    System.out.println("....GET");
    currOwner = findOwner();
    if (currOwner == null)
    {
      return new CatReply( -1, "Cat: OWNER DOESN'T EXIST! unable to get.");
    }
    FileEntry target = currOwner.getFile(catReq.getName());
    if (target == null)
    {
      return new CatReply( -1, "Cat: FILE DOESN'T EXIST! unable to get.");
    }
    return new CatReply( 0, target.getLocation());   
  }

  private CatReply catLs()
  {
    System.out.println("....LS");
    currOwner = findOwner();
    if (currOwner == null)
    {
      return new CatReply( -1, "Cat: OWNER DOESN'T EXIST! unable to ls.");
    }
    return new CatReply(0, currOwner.listFiles());
  }

  private CatReply catRm()
  {
    System.out.println("....RM");
    currOwner = findOwner();
    if (currOwner == null)
    {
      return new CatReply( -1, "Cat: OWNER DOESN'T EXIST! unable to rm.");
    }
    FileEntry target = currOwner.getFile(catReq.getName());
    if ( target == null)
    {
      return new CatReply( -1, "Cat: file doesn't exist! unable to rm.");
    }
    String location = target.getLocation();
    System.out.println("Deleting file " + catReq.getName() + " on " + location);
    int rc = currOwner.delFile(catReq.getName());
    if (rc != 0) 
    { return new CatReply(0, "Cat: unable to delete entry from cat"); }
    return new CatReply(0, location);
  }

  private void addPac()
  {
    PacEntry newPac = new PacEntry(catReq.getName());
    pacs.add(newPac);
  }

  private OwnerEntry findOwner()
  {
    for (OwnerEntry o : owners) 
    { 
      if (catReq.getOwner().equals(o.getName())) { return o; }
    }
    return null;
  }
  
  private PacEntry  getNextPac()
  {
    PacEntry nextPac = pacs.get(0);
    for (PacEntry p : pacs) { nextPac = nextPac.compareTo(p) < 0 ? nextPac : p; }
    return nextPac;
  }


  public class OwnerEntry
  {
    private String name;
    private List<FileEntry> files;

    public OwnerEntry(String name)
    {
      this.name = name;
      this.files = Collections.synchronizedList(new ArrayList<FileEntry>());
    }

    public String getName()
    {
      return this.name;
    }

    private boolean hasFile(String name)
    {
      for (FileEntry f : files)
      {
        if (f.getName().equals(name)) { return true;}
      }
      return false;
    }

    public FileEntry getFile(String name)
    {
      for (FileEntry f : files)
      {
        if (f.getName().equals(name)) { return f;}
      }
      return null;
    }

    public String listFiles()
    {
      String retStr = this.name +"'s files\n";
      for (FileEntry f : this.files) { retStr += f.toString(); }
      return retStr;
    }

    public int addFile(String filename, String location, int size)
    {
      if (hasFile(filename)) { return -1;}
      FileEntry newFileEntry = new FileEntry(this.name, filename, location, size);
      this.files.add(newFileEntry);
      return 0;
    }

    public int delFile(String filename)
    {
      if (!hasFile(filename)) { return -1;}
      for (FileEntry f : files)
      {
        if (f.getName().equals(filename)) { this.files.remove(f); return 0; }
      }
      return -1; // this shouldn't Happen!
    }
  }

  public class PacEntry implements Comparable<PacEntry>
  {
    private String name;
    private    int held;

    public PacEntry(String name) 
    {
      this.name = name;
      this.held  = 0;
    }
    public String getName()       { return this.name; }
    public    int getHeld()       { return this.held; }
    public   void addBytes(int n) {   this.held += n; }

    @Override
    public int compareTo(PacEntry p)
    {
      return this.getHeld() - p.getHeld();
    }
  }

  public class FileEntry
  {
    private String    owner;
    private String     name;
    private String location;
    private    int     size;

    public FileEntry(
        String owner, String name,  String location, int size
        )
    {
      this.owner = owner;
      this.name = name;
      this.location = location;
      this.size = size;
    }

    public String getName() { return this.name; }
    public String getLocation()  { return this.location; }

    @Override
    public String toString()
    {
      return this.name + " :: " + Integer.toString(this.size) + "bytes :: " + 
       this.location + "\n"; 
    }
  }  
} // Cat
