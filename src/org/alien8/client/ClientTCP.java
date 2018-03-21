package org.alien8.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.alien8.core.ServerMessage;

public class ClientTCP extends Thread {

  private ObjectInputStream fromServer;
  private volatile boolean run = true;

  /**
   * Constructor
   * 
   * @param fromServer Input Stream for reading data from server
   */
  public ClientTCP(ObjectInputStream fromServer) {
    this.fromServer = fromServer;
  }

  /**
   * Run loop to keep reading info from server through TCP channel
   */
  public void run() {
    while (run) {
      try {
        ServerMessage msg = (ServerMessage) fromServer.readObject();
        if (msg.getType() == 0) { // Game ended
          Client.getInstance().waitToExit();
        } else if (msg.getType() == 1) { // Time before exiting
          Client.getInstance().setTimeBeforeExiting(msg.getTimeBeforeExiting());
        } else if (msg.getType() == 2) { // Server stopped
          Client.getInstance().disconnect();
        } else if (msg.getType() == 3) { // Start game
          Client.getInstance().setState(Client.State.IN_GAME);
        }
      } catch (IOException ioe) { // Unexpected disconnection or other I/O errors
        Client.getInstance().disconnect();
      } catch (ClassNotFoundException cnfe) {
        cnfe.printStackTrace();
      }
    }
    System.out.println("Client TCP thread ended");
  }

  /**
   * Stop the thread
   */
  public void end() {
    this.run = false;
  }

}
