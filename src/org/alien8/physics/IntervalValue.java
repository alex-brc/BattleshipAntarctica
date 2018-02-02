package org.alien8.physics;

import org.alien8.core.Entity;

public class IntervalValue {

  private IntervalValueType type;
  private Entity entity;
  private double value;

  public IntervalValue(IntervalValueType type, Entity entity, double value) {
    this.setType(type);
    this.setEntity(entity);
    this.setValue(value);
  }

  public IntervalValueType getType() {
    return type;
  }

  public void setType(IntervalValueType type) {
    this.type = type;
  }

  public Entity getEntity() {
    return entity;
  }

  public void setEntity(Entity entity) {
    this.entity = entity;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
