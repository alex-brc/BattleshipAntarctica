package org.alien8.physics;

import org.alien8.core.Entity;

public class AABB {
  private Position min;
  private Position max;
  private Entity entity;

  public AABB(Position min, Position max, Entity entity) {
    this.setMin(min);
    this.setMax(max);
    this.setEntity(entity);
  }

  public Position getMin() {
    return min;
  }

  public void setMin(Position min) {
    this.min = min;
  }

  public Position getMax() {
    return max;
  }

  public void setMax(Position max) {
    this.max = max;
  }

  public Entity getEntity() {
    return entity;
  }

  public void setEntity(Entity entity) {
    this.entity = entity;
  }
}
