package org.alien8.server;

public class TimerEvent extends GameEvent {
  private static final long serialVersionUID = -2221951913857155239L;
  private int seconds;

  /**
   * Constructor
   * @param seconds Current time in seconds
   */
  public TimerEvent(int seconds) {
    this.seconds = seconds;
  }

  /**
   * Get current time (seconds part)
   * 
   * @return Current time (seconds part)
   */
  public int getSeconds() {
    return this.seconds;
  }
}
