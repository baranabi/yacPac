/**
 * PacRegister.java
 * This is a message that is sent by a pac server to a yac server
 * when the pac server sets up. 
 * 
 * */

public class PacRegistration implements Serializable
{
  private String name; 
  public PacRegistration(String name) { this.name = name; }
  private getName() { return this.name; }
}
