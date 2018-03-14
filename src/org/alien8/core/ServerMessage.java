package org.alien8.core;

import java.io.Serializable;

public class ServerMessage implements Serializable {
  
  private static final long serialVersionUID = 2888364308186690819L;
  /*
   *  0 for game ended
   *  1 for time before exiting
   *  2 for server stopped
   */
  private int type;
  private int timeBeforeExiting;
  
  // For 0 or 2
  public ServerMessage(int type) {
    this.type = type;
  }
  
  // For 1
  public ServerMessage(int type, int timeBeforeExiting) {
    this.type = type;
    this.timeBeforeExiting = timeBeforeExiting;
  }

  public int getType() {
    return this.type;
  }
  
  public int getTimeBeforeExiting() {
    return this.timeBeforeExiting;
  }

}
