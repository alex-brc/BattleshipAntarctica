package org.alien8.physics;

import org.alien8.core.Entity;

/**
 * This class represents a value for the interval covered by an object. It is to be used as part of
 * the sort-and-sweep algorithm during collision detection.
 *
 */
public class IntervalValue {

  private IntervalValueType type;
  private Entity entity;
  private double value;

  /**
   * Constructor.
   * 
   * @param type the type of the value
   * @param entity the Entity the value corresponds to
   * @param value the value itself
   */
  public IntervalValue(IntervalValueType type, Entity entity, double value) {
    this.type = type;
    this.entity = entity;
    this.value = value;
  }

  /**
   * Returns the type of the value, signifying whether it is the beginning or end of an interval.
   * 
   * @return the type
   */
  public IntervalValueType getType() {
    return type;
  }

  /**
   * Returns the Entity the value corresponds to.
   * 
   * @return the Entity the value corresponds to
   */
  public Entity getEntity() {
    return entity;
  }

  /**
   * Returns the value of the IntervalValue.
   * 
   * @return the value itself
   */
  public double getValue() {
    return value;
  }
}
