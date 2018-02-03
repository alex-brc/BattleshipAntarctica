package org.alien8.physics;

import org.alien8.core.Entity;

public class Collision {
  private Entity entity1;
  private Entity entity2;

  public Collision(Entity entity1, Entity entity2) {
    this.setEntity1(entity1);
    this.setEntity2(entity2);
  }

  public Entity getEntity1() {
    return entity1;
  }

  public void setEntity1(Entity entity1) {
    this.entity1 = entity1;
  }

  public Entity getEntity2() {
    return entity2;
  }

  public void setEntity2(Entity entity2) {
    this.entity2 = entity2;
  }

  public void resolveCollision() {

  }
}
