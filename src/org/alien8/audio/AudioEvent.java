package org.alien8.audio;

import org.alien8.physics.Position;
import org.alien8.server.GameEvent;

/**
 * This class represents an audio 'event' in the game, i.e. some event which happens, causing sound
 * to be played.
 *
 */
public class AudioEvent extends GameEvent {

  private static final long serialVersionUID = 8047674799697266330L;

  /**
   * This enum represents the type of event causing a sound to be played.
   *
   */
  public enum Type {
    SHOOT, HIT, PICKUP, MINE_EXPLODE;
  }

  private Type type;
  private Position position;

  /**
   * Constructor.
   * 
   * @param type the type of this event
   * @param position the Position at which this event occurs
   */
  public AudioEvent(Type type, Position position) {
    this.type = type;
    this.position = position;
  }

  /**
   * @return the type of this event
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the Position at which this event occurs in XY coordinates
   */
  public Position getPosition() {
    return position;
  }

  /**
   * @return a String representation of this AudioEvent
   */
  @Override
  public String toString() {
    return type.name() + " " + position.toString();
  }
}
