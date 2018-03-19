package org.alien8.physics;

import org.alien8.core.Entity;

/**
 * This class is used to represent an Axis-Aligned Bounding Box (AABB) around an Entity. An AABB is
 * the smallest possible rectangle which encloses the Entity, whose edges are parallel to the x and
 * y axes.
 * <P>
 * The box is defined by a maximum point (the top-right corner), and a minimum point (the
 * bottom-left corner).
 */
public class AABB {
  private Position min;
  private Position max;
  private Entity entity;

  /**
   * Constructor.
   * 
   * @param min the minimum (bottom-left point) of the box
   * @param max the maximum (top-right point) of the box
   * @param entity the Entity that the box encloses
   */
  public AABB(Position min, Position max, Entity entity) {
    this.min = min;
    this.max = max;
    this.entity = entity;
  }

  /**
   * Returns the minimum value of the AABB (bottom-left point).
   * 
   * @return the Position of the minimum
   */
  public Position getMin() {
    return min;
  }

  /**
   * Returns the maximum value of the AABB (top-right point).
   * 
   * @return the Position of the maximum
   */
  public Position getMax() {
    return max;
  }

  /**
   * Returns the Entity that the AABB encloses.
   * 
   * @return the Entity that the AABB encloses
   */
  public Entity getEntity() {
    return entity;
  }
}
