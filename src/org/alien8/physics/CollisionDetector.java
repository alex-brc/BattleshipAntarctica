package org.alien8.physics;

import java.util.ArrayList;
import java.util.List;
import org.alien8.core.Entity;

public class CollisionDetector {
  /**
   * Checks for Collisions between a set of Entities.
   * 
   * @param entities a List of Entities which are being checked for collisions
   * @return a List of Collisions
   */
  public List<Collision> checkForCollisions(List<Entity> entities) {
    /*
     * BROAD PHASE: In this phase, we do some rough spatial examination of the Entities to rule out
     * collisions between objects that are very far away. We end up with a list of potential
     * collisions which are verified in the narrow phase
     */
    // Create an Axis-Aligned Bounding Box (AABB) for each entity
    // AABBs are a rough approximation of an object's shape
    ArrayList<AABB> aabbs = createAabbs(entities);

    // Sort and sweep algorithm
    // Rules out collisions between objects that are far away from each other
    ArrayList<IntervalValue> intervalValues = sort(aabbs);
    ArrayList<Collision> potentialCollisions = sweep(intervalValues);

    /*
     * NARROW PHASE: In this phase, we inspect each of our potential collisions to determine which
     * ones are real
     */
    ArrayList<Collision> verifiedCollisions = new ArrayList<>();
    // Verify each of our potential collisions
    for (Collision c : potentialCollisions) {
      if (verifyCollision(c) == true) {
        verifiedCollisions.add(c);
      }
    }

    // Return the collisions that we have found
    return verifiedCollisions;
  }

  /**
   * Creates an Axis-Aligned Bounding Box (AABB) for each Entity given.
   * 
   * @param entities a List of Entities
   * @return a list of AABB's. Each one represents one Entity
   */
  private ArrayList<AABB> createAabbs(List<Entity> entities) {
    ArrayList<AABB> aabbs = new ArrayList<AABB>();
    for (Entity e : entities) {
      // Get position and length from the Entity
      Position pos = e.getPosition();
      double length = e.getLength();
      // Calculate max and min points
      Position max = new Position((pos.getX() + 0.5 * length * Math.cos(e.getDirection())),
          (pos.getY() + 0.5 * length * Math.sin(e.getDirection())));
      Position min = new Position((pos.getX() - 0.5 * length * Math.cos(e.getDirection())),
          (pos.getY() - 0.5 * length * Math.sin(e.getDirection())));
      // Create new AABB
      AABB box = new AABB(max, min, e);
      aabbs.add(box);
    }
    return aabbs;
  }

  /**
   * The 'sort' part of the sort-and-sweep algorithm. Given a set of Axis-Aligned Bounding Boxes
   * (AABBs), produces a sorted list of beginning and end IntervalValues along the X-axis.
   * 
   * @param aabbs a List of AABBs
   * @return a sorted List of IntervalValues
   */
  private ArrayList<IntervalValue> sort(ArrayList<AABB> aabbs) {
    ArrayList<IntervalValue> intervalValues = new ArrayList<IntervalValue>();
    for (AABB aabb : aabbs) {
      // Generates the beginning and end IntervalValues for the AABB
      IntervalValue begin =
          new IntervalValue(IntervalValueType.b, aabb.getEntity(), aabb.getMin().getX());
      IntervalValue end =
          new IntervalValue(IntervalValueType.e, aabb.getEntity(), aabb.getMax().getX());
      // Inserts them in the correct place in the list
      insert(begin, intervalValues);
      insert(end, intervalValues);
    }
    return intervalValues;
  }

  /**
   * Inserts an IntervalValue in the correct place in a sorted list.
   * 
   * @param item the IntervalValue being inserted
   * @param intervalValues the List into which the IntervalValue is being inserted
   */
  private void insert(IntervalValue item, ArrayList<IntervalValue> intervalValues) {
    // If the list is empty, just add the item
    if (intervalValues.isEmpty()) {
      intervalValues.add(item);
    } else {
      // Loop through the list
      for (int i = 0; i < intervalValues.size(); i++) {
        // If the item we are inserting is less than or equal to the one in the list position,
        // then insert it
        if (item.getValue() <= intervalValues.get(i).getValue()) {
          intervalValues.add(i, item);
          return;
        }
      }
    }
  }

  /**
   * The 'sweep' part of the sort-and-sweep algorithm. Sweeps through a sorted list of
   * IntervalValues, determining where intervals overlap, and so which Entities might potentially be
   * colliding.
   * 
   * @param intervalValues a sorted List of IntervalValues
   * @return a List of potential Collisions
   */
  private ArrayList<Collision> sweep(ArrayList<IntervalValue> intervalValues) {
    ArrayList<Collision> potentialCollisions = new ArrayList<>();
    // Creates a list to store active intervals
    // As each interval has a beginnning and end point, an active interval is one which has begun
    // but not yet ended
    ArrayList<IntervalValue> activeIntervals = new ArrayList<>();
    for (IntervalValue i : intervalValues) {
      // If the interval is beginning
      if (i.getType() == IntervalValueType.b) {
        // Stores potential collisions between the beginning interval and those that have already
        // begun
        for (IntervalValue j : activeIntervals) {
          potentialCollisions.add(new Collision(i.getEntity(), j.getEntity()));
        }
        // Marks the interval as active
        activeIntervals.add(i);
      } else // Else, the interval must be ending
        // Loops through the active intervals to find the right beginning interval and remove it
        // from the active intervals
        for (IntervalValue j : activeIntervals) {
          if (j.getEntity() == i.getEntity()) {
            activeIntervals.remove(j);
            break;
          }
        }
    }
    return potentialCollisions;
  }

  private boolean verifyCollision(Collision c) {
    // use separating axis theorem
    return false;
  }

}

