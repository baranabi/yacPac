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
               System.out.println("Yac: starting thread");
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
               ObjectInputStream pacRegIn = new ObjectInputStream(pacSock.getInputStream());
               System.out.println("Yac: waiting for  pac message");
               PacRegistration   pacReg   = (PacRegistration) pacRegIn.readObject();
               System.out.println("Yac: creating pacEntry");
               PacEntry newPac = new PacEntry(pacSock, pacRegIn , pacReg.getName());
               System.out.println("Yac: adding pac to our collection");
               pacs.add(newPac);
               System.out.println("Yac: messaging cat to register pac");
               toCat.writeObject(new CatRequest(CatOp.CAT_REGPAC, pacReg.getName(), null, 0));
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
    private ObjectInputStream fromPac;
    private ObjectOutputStream  toPac;
    private String name;
    
    public PacEntry(Socket sock, ObjectInputStream in, String pacName) throws IOException
    {
      this.s = sock;
      this.fromPac = in;
      this.toPac = new ObjectOutputStream(this.s.getOutputStream());

      this.name = pacName;
    } // entry constructor
    // getters
    public Socket getSocket()  { return this.s; }
    public String getName() { return this.name; }
    public ObjectInputStream getInput() { return this.fromPac; }
    public ObjectOutputStream getOutput() { return this.toPac;  }
  }

  public class YacThread extends Thread
  {
    private Socket yacSock;
    private String   owner;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    public YacThread(Socket s)
    {
      this.yacSock = s;
      try 
      {
        System.out.println("YacThread: creating i/o streams\n...from client");
        this.input   = new   ObjectInputStream(this.yacSock.getInputStream());
        System.out.println("... to client");
        this.output  = new ObjectOutputStream(this.yacSock.getOutputStream());
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    } // constructor
  
    private CatReply catMessage(CatOp c, String name, String owner, int size)
    {
      System.out.println("Yac: messaging cat");
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
        YacReply   yacRep;
        PacRequest pacReq;
        PacReply   pacRep;

        ObjectOutputStream toPac;
        ObjectInputStream fromPac;
        if (op == YacOp.PUT) // PUT //////////////////////////////////////////////////
        {
          System.out.println("Yac: PUT");
          catRep = catMessage(CatOp.CAT_PUT, yacReq.getFileName(),
            yacReq.getOwner(), yacReq.getSize());
          System.out.println("Yac: got reply from cat !");
          if (catRep.getStatus() != 0) 
          {
            System.out.println("Yac: cat reported an error! Messaging client..");
            yacRep =  new YacReply(catRep.getStatus(), catRep.getMessage().getBytes());
          }
          else
          {
            System.out.println("Yac: got cat info, messaging appropriate pac");
            pacReq = new PacRequest(PacOp.PUT, yacReq.getFileName(), yacReq.getData());
            PacEntry target = getPac(catRep.getMessage());
            toPac = target.getOutput();
            fromPac = target.getInput();
            System.out.println("Yac: writing request to pac " + catRep.getMessage());
            toPac.writeObject(pacReq);
            pacRep =  (PacReply) fromPac.readObject();
            System.out.println("Yac: packing client reply..");
            yacRep = new YacReply(pacRep.getStatus(), pacRep.getData());
          }
          output.writeObject(yacRep);
        }
        else if (op == YacOp.GET) // GET /////////////////////////////////////////////
        {
          System.out.println("Yac: GET");
          catRep = catMessage(CatOp.CAT_GET, yacReq.getFileName(), 
            yacReq.getOwner(), 0);
          if (catRep.getStatus() != 0) 
          {
            yacRep =  new YacReply(catRep.getStatus(), catRep.getMessage().getBytes());
          }
          else
          {
            System.out.println("Yac: got cat info, messaging appropriate pac");
            pacReq = new PacRequest(PacOp.GET, yacReq.getFileName(), null);
            PacEntry target = getPac(catRep.getMessage());
            toPac = target.getOutput();
            fromPac = target.getInput();
            toPac.writeObject(pacReq);
            pacRep = (PacReply) fromPac.readObject();
            yacRep = new YacReply(pacRep.getStatus(), pacRep.getData());
          }
          output.writeObject(yacRep);
        }
        else if (op == YacOp.LS)  // LS //////////////////////////////////////////////
        {
          System.out.println("Yac: LS");
          catRep = catMessage(CatOp.CAT_LS, null, yacReq.getOwner(), 0);
          yacRep = new YacReply(catRep.getStatus(), catRep.getMessage().getBytes());
          output.writeObject(yacRep);
        }
        else if (op == YacOp.RM)  // RM //////////////////////////////////////////////
        {
          System.out.println("Yac: RM");
          catRep = catMessage(CatOp.CAT_RM, yacReq.getFileName(),
            yacReq.getOwner(), 0);
          
          if (catRep.getStatus() != 0) 
          {
            yacRep =  new YacReply(catRep.getStatus(), catRep.getMessage().getBytes());
          }
          else
          {
            System.out.println("Yac: got cat info, messaging appropriate pac");
            pacReq = new PacRequest(PacOp.RM, yacReq.getFileName(), null);
            PacEntry target = getPac(catRep.getMessage());
            toPac = target.getOutput();
            fromPac = target.getInput();
            System.out.println("Yac: writing request to pac " + catRep.getMessage());
            toPac.writeObject(pacReq);
            pacRep = (PacReply) fromPac.readObject();
            yacRep = new YacReply(pacRep.getStatus(), pacRep.getData());
          }
          output.writeObject(yacRep);
        }
        else
        {
          System.err.println("Yac: received unknown operation request from client!");
        }
        System.out.println("Yac: Completed request! Closing streams and killing thread"); 
        this.input.close();
        this.output.close();
        clients.remove(this);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    private PacEntry getPac(String name)
    {
      for (PacEntry p : pacs)
      {
        if (p.getName().equals(name)) { return p; }
      }
      return null; // this shouldn't happen!
    }
  } // YacThread
} // Yac
