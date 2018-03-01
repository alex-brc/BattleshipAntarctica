package org.alien8.mapgeneration;

import java.io.Serializable;
import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.physics.Position;

public class Ice extends Entity implements Serializable {

  private static final long serialVersionUID = -3440599654141729832L;

  public Ice(Position pos) {
    super(pos, 0, 0, 0, Parameters.MAP_BOX_SIZE, Parameters.MAP_BOX_SIZE);
  }

  @Override
  public void dealWithOutOfBounds() {
    // TODO Auto-generated method stub

  }

  // @Override
  // public void render(Renderer r) {
  // // if (Parameters.RENDER_BOX)
  // // Adjust for the position being the center of the box
  // r.drawRect((int) this.position.getX() - Parameters.MAP_BOX_SIZE / 2 + 1,
  // (int) this.position.getY() - Parameters.MAP_BOX_SIZE / 2 + 1, 32, 32, 0xFF0000, false);
  // }

  @Override
  public void dealWithInIce(boolean[][] iceGrid) {
    // TODO Auto-generated method stub

  }

  @Override
  public void render() {
    // TODO Auto-generated method stub

  }
}
