package org.alien8.audio;

import org.alien8.physics.Position;
import org.alien8.server.GameEvent;

public class AudioEvent extends GameEvent {

  private static final long serialVersionUID = 8047674799697266330L;

  public enum Type {
    SHOOT, ICE_CRASH, SHIP_CRASH;
  }

  private Type type;
  private Position position;

  public AudioEvent(Type type, Position position) {
    this.type = type;
    this.position = position;
  }

  public Type getType() {
    return type;
  }

  public Position getPosition() {
    return position;
  }

  @Override
  public String toString() {
    return type.name() + " " + position.toString();
  }
}
