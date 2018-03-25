package org.alien8.server;

import org.alien8.rendering.FontColor;
import org.alien8.rendering.Renderer;

public class Timer {
  public int minutes;
  public int seconds;

  /**
   * Constructor
   * @param event The TimeEvent object
   */
  public Timer(TimerEvent event) {
    this.minutes = event.getSeconds() / 60;
    this.seconds = event.getSeconds() % 60;
  }

  /**
   * Constructor
   * @param seconds Current time in seconds
   */
  public Timer(int seconds) {
    this.minutes = seconds / 60;
    this.seconds = seconds % 60;
  }

  /**
   * Get current time (minute part)
   * @return Current time (minute part)
   */
  public int getMinutes() {
    return this.minutes;
  }

  /**
   * Get current time (seconds part)
   * @return Current time (seconds part)
   */
  public int getSeconds() {
    return this.seconds;
  }

  /**
   * Render the timer of the HUD
   */
  public void render() {
    Renderer.getInstance().drawText("TIMER", 110, 16, true, FontColor.WHITE);
    Renderer.getInstance().drawText("" + minutes + "-" + seconds, 110, 40, true, FontColor.WHITE);
  }

}
