import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Main interfacing point between the user and the infrastructure. 
 * Spawns thread for each connection request. 
 */


public class Yac
{
  public static final String ADDRESS  = "localhost";
  public static final int YAC_PORT = 4000;
  public static final int PAC_PORT = 5000;
  public static final int CAT_PORT = 6000;
  public static final int _BACKLOG =    5;
  
  private List<YacThread> clients = 
    Collections.synchronizedList(new ArrayList<YacThread>());
  private List<PacEntry>  pacs = 
    Collections.synchronizedList(new ArrayList<PacEntry>());
  
  private  ServerSocket    catListen;
  private  ServerSocket clientListen;
  private  ServerSocket    pacListen; 

  private Socket catSock; 
  private ObjectInputStream fromCat;
  private ObjectOutputStream toCat;
  public static void main(String[] args) 
  {
    try 
    {
      new Yac().start();
    }
    catch (IOException e)
    {
      System.err.println(e);
    }
  } // main
  
  private void start() throws IOException
  {

    try
    {
      clientListen  = new ServerSocket(YAC_PORT,_BACKLOG);
      pacListen     = new ServerSocket(PAC_PORT,_BACKLOG);
      catListen     = new ServerSocket(CAT_PORT,_BACKLOG);
      System.out.println("Yac: waiting for catalog server on port " + CAT_PORT);
      catSock = catListen.accept();
      toCat   = new ObjectOutputStream(catSock.getOutputStream());
      fromCat = new ObjectInputStream(catSock.getInputStream());
      
      System.out.println("Yac: listening for clients on " + YAC_PORT);
      System.out.println("Yac: listening for pacs on " + PAC_PORT);

      (new Thread()
      {
         public void run() 
         {
           while (true)
           {
             try
             {
             System.out.println();
             System.out.println("Yac: waiting for client connection");
             Socket clientSock = clientListen.accept();
             System.out.println("Yac: creating thread for client request");
             YacThread yacThr = new YacThread(clientSock);
             yacThr.start();
             clients.add(yacThr);
             }
             catch (Exception e)
             {
               System.err.println("Yac: " + e);
             }
           }
         }
      }).start(); // this thread listens for client requests.
      (new Thread()
       {
         public void run()
         {
           while (true)
           {
             try
             {
               System.out.println();
               System.out.println("Yac: waiting for pac registration");
               Socket pacSock = pacListen.accept();
               System.out.println("Yac: creating pac thread");
               ObjectInputStream pacRegIn = new ObjectInputStream(pacSock.getInputStream());
               PacRegistration   pacReg   = (PacRegistration) pacRegIn.readObject();
               PacEntry newPac = new PacEntry(pacSock, pacReg.getName());
               pacRegIn.close();
             }
             catch (Exception e) { System.err.print(e); }
           } 
         }
       }).start(); // this thread listens for pac registrations. 

    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  } // start
  
  public class PacEntry  // our pacs list is a collection of these entries. 
  {
    private Socket s;
    private String name;
    
    public PacEntry(Socket sock, String pacName)
    {
      this.s = sock;
      this.name = pacName;
    } // entry constructor
    // getters
    public Socket getSocket()  { return s; }
    public String getName() { return name; }
  }

  public class YacThread extends Thread
  {
    private Socket yacSock;
    private String   owner;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    private ObjectInputStream fromCat;
    private ObjectOutputStream  toCat;
  
    public YacThread(Socket s)
    {
      this.yacSock = s;
      try 
      {
        this.input   = new   ObjectInputStream(this.yacSock.getInputStream());
        this.output  = new ObjectOutputStream(this.yacSock.getOutputStream());
        this.fromCat = new        ObjectInputStream(catSock.getInputStream());
        this.toCat   = new      ObjectOutputStream(catSock.getOutputStream());
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    } // constructor
  
    private CatReply catMessage(CatOp c, String name, String owner, int size)
    {
      try
      {
        CatRequest catReq  = new CatRequest( c, name, owner, size); 
        toCat.writeObject(catReq);
        return (CatReply) fromCat.readObject();
      }
      catch (Exception e)
      {
        return new CatReply(-1, e.toString());
      }
    }
    public void run()
    {
      // handle yac Requests.
      try
      {
        YacRequest yacReq = (YacRequest) input.readObject();
        YacOp op = yacReq.getOp();
        CatReply   catRep;
        if (op == YacOp.PUT) // PUT //////////////////////////////////////////////////
        {
          catRep = catMessage(CatOp.CAT_PUT, yacReq.getFileName(),
            yacReq.getOwner(), yacReq.getSize());
        }
        else if (op == YacOp.GET) // GET /////////////////////////////////////////////
        {
          catRep = catMessage(CatOp.CAT_GET, yacReq.getFileName(), 
            yacReq.getOwner(), 0);
        }
        else if (op == YacOp.LS)  // LS //////////////////////////////////////////////
        {
          catRep = catMessage(CatOp.CAT_LS, null, yacReq.getOwner(), 0);
        }
        else if (op == YacOp.RM)  // RM //////////////////////////////////////////////
        {
          catRep = catMessage(CatOp.CAT_RM, yacReq.getFileName(),
            yacReq.getOwner(), 0);
        }
        else
        {
          System.err.println("Yac: received unknown operation request from client!");
        } 
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  } // YacThread
} // Yac
