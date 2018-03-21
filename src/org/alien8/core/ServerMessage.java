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
  
  /**
   * Constructor for message type 0 or 2
   * @param type Type of the message
   */
  public ServerMessage(int type) {
    this.type = type;
  }
  
  /**
   * Constructor for message type 1
   * @param type Type of the message
   * @param timeBeforeExiting Time before exiting the match
   */
  public ServerMessage(int type, int timeBeforeExiting) {
    this.type = type;
    this.timeBeforeExiting = timeBeforeExiting;
  }

  /**
   * Get the type of the message
   * @return The type of the message
   */
  public int getType() {
    return this.type;
  }
  
  /**
   * Get the time before exiting the match
   * @return The time before exiting the match
   */
  public int getTimeBeforeExiting() {
    return this.timeBeforeExiting;
  }

}
