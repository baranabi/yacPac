import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Main interfacing point between the user and the infrastructure. 
 * Spawns thread for each connection request. 
 */


public class Yac
{
  public static final int YAC_PORT = 4000;
  public static final int PAC_PORT = 5000;
  public static final int CAT_PORT = 6000;
  public static final int _BACKLOG =    5;
  
  private List<YacThread> clients = 
    Collections.synchronizedList(new ArrayList<YacThread>());
  private List<PacThread>  pacs = 
    Collections.synchronizedList(new ArrayList<PacThread>());
  

  private Socket catSock; 

  public static void main(String[] args) throws IOException
  {
    new Yac().start();
  }
  
  private void start()
  {
    ServerSocket    catListen;
    ServerSocket clientListen;
    ServerSocket    pacListen; 

    try
    {
      clientListen  = new ServerSocket(YAC_PORT,_BACKLOG);
      pacListen     = new ServerSocket(PAC_PORT,_BACKLOG);
      catListen     = new ServerSocket(CAT_PORT,_BACKLOG);
      System.out.println("Yac: waiting for catalog server on port " + CAT_PORT);
      catSock = catListen.accept();
      
      System.out.println("Yac: listening for clients on " + YAC_PORT);
      System.out.println("Yac: listening for pacs on " + PAC_PORT);


      (new Thread()
      {
         public void run()
         {
           while (true)
           {
             System.out.println();
             System.out.println("Yac: waiting for client connection");

             Socket clientSock = clientListen.accept();

             System.out.println("Yac: creating thread for client request");

             YacThread yacThr = new YacThread(clientSock);
             yacThr.start();
             clients.add(yacThr);
           }
         }
      }).start(); // this thread listens for client requests.
      (new Thread()
       {
         public void run()
         {
           System.out.println();
           System.out.println("Yac: waiting for pac registration");

           Socket pacSock = pacListen.accept();

           System.out.println("Yac: creating pac thread");
           PacThread pacThr = new PacThread(pacSock);
           pacThr.start();
           pacs.add(pacThr); 
         }
       }).start(); // this thread listens for pac registrations. 

    }
    catch (Exception e)
    {
      System.err.println(e);
    }
  } // Main


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
        this.fromCat = new   ObjectInputStream(this.catSock.getInputStream());
        this.toCat   = new ObjectOutputStream(this.catSock.getOutputStream());
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    } // constructor
  
    public void run()
    {
      // handle yac Requests.
      try
      {
        YacRequest yacReq = (YacRequest) input.readObject();
        YacOp op = yacReq.getOp();
        CatRequest catReq;
        CatReply   catRep;
        if (op == YacOp.PUT)
        {
          catReq = new CatRequest(CatOp.CAT_PUT, yacReq.getFileName(),
            yacReq.getOwner(), yacReq.getSize());
          
        }
        else if (op == YacOp.GET)
        {
          catReq = new CatRequest(CatOp.CAT_GET, yacReq.getFileName(), 
            yacReq.getOwner(), 0);
          toCat.writeObject(catReq); // send request to Cat
        }
        else if (op == YacOp.LS)
        {
          catReq = new CatRequest(CatOp.CAT_LS, null, yacReq.getOwner(), 0);
          toCat.writeObject(catReq);
        }
        else if (op == YacOp.RM)
        {
          catReq = new CatRequest(CatOp.CAT_RM, yacReq.getFileName(),
            yacReq.getOwner(), 0);
          toCat.writeObject(catReq);
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
  public class PacThread extends Thread
  {
    private Socket pacSock;
    public PacThread(Socket s)
    {
      this.pacSock = s;
    }

    public void run()
    {
    }
  }
} // Yac
