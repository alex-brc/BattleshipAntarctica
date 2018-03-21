package org.alien8.physics;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.core.Entity;
import net.jafama.FastMath;

/**
 * This class is used for checking for Collisions between Entities in the game.
 *
 */
public class CollisionDetector {
  /**
   * Checks for Collisions between a set of Entities.
   * 
   * @param entities a List of Entities which are being checked for collisions
   * @return
   * @return a List of Collisions
   */
  public ArrayList<Collision> findCollisions(ConcurrentLinkedQueue<Entity> entities) {
    // /*
    // * BROAD PHASE: In this phase, we do some rough spatial examination of the Entities to rule
    // out
    // * collisions between objects that are very far away. We end up with a list of potential
    // * collisions which are verified in the narrow phase
    // */
    // // Create an Axis-Aligned Bounding Box (AABB) for each entity
    // // AABBs are a rough approximation of an object's shape
    // ArrayList<AABB> aabbs = createAabbs(entities);
    //
    // // Sort and sweep algorithm
    // // Rules out collisions between objects that are far away from each other
    // ArrayList<IntervalValue> intervalValues = sort(aabbs);
    // ArrayList<Collision> potentialCollisions = sweep(intervalValues);

    // Just check for collisions between all objects. There is no noticeable change in performance
    ArrayList<Collision> potentialCollisions = new ArrayList<Collision>();
    for (Entity e1 : entities) {
      for (Entity e2 : entities) {
        if (e1.getSerial() != e2.getSerial() && !collisionInList(potentialCollisions, e2, e1)) {
          potentialCollisions.add(new Collision(e1, e2));
        }
      }
    }

    System.out.println(potentialCollisions.size());

    /*
     * NARROW PHASE: In this phase, we inspect each of our potential collisions to determine which
     * ones are real
     */
    ArrayList<Collision> verifiedCollisions = new ArrayList<>();
    // Verify each of our potential collisions
    for (Collision c : potentialCollisions) {
      MTV vector = verifyCollision(c);
      if (vector != null) {
        verifiedCollisions.add(new Collision(c.getEntity1(), c.getEntity2(), vector));
        // Collision col = new Collision(c.getEntity1(), c.getEntity2(), vector);
        // col.resolveCollision();
      }
      // if (verifyCollision(c)) {
      // verifiedCollisions.add(new Collision(c.getEntity1(), c.getEntity2()));
      // }
    }
    System.out.println("Verified: " + verifiedCollisions.size());

    // Return the collisions that we have found
    return verifiedCollisions;
  }

  /**
   * Checks if a Collision between two Entities already exists in a list.
   * 
   * @param list the list to check
   * @param e1 the first Entity involved in the Collision
   * @param e2 the second Entity involved in the Collision
   * @return
   */
  private boolean collisionInList(ArrayList<Collision> list, Entity e1, Entity e2) {
    for (Collision c : list) {
      if (c.getEntity1().getSerial() == e1.getSerial()
          && c.getEntity2().getSerial() == e2.getSerial()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates an Axis-Aligned Bounding Box (AABB) for each Entity given.
   * 
   * @param entities a List of Entities
   * @return a list of AABB's. Each one represents one Entity
   */
  @Deprecated
  private ArrayList<AABB> createAabbs(ConcurrentLinkedQueue<Entity> entities) {
    ArrayList<AABB> aabbs = new ArrayList<AABB>();
    for (Entity e : entities) {
      // Get position and length from the Entity
      Position pos = e.getPosition();
      double x = pos.getX();
      double y = pos.getY();
      double length = e.getLength();

      double dir = PhysicsManager.shiftAngle(e.getDirection());
      double hypotenuse = length / 2;
      Position max;
      Position min;

      if (dir >= 0 && dir < Math.PI / 2) {
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      } else if (dir >= Math.PI / 2 && dir < Math.PI) {
        dir = Math.PI - dir;
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      } else if (dir >= Math.PI && dir < 3 * Math.PI / 2) {
        dir = (3 * Math.PI / 2) - dir;
        max = new Position(x + hypotenuse * FastMath.sin(dir), y - hypotenuse * FastMath.cos(dir));
        min = new Position(x - hypotenuse * FastMath.sin(dir), y + hypotenuse * FastMath.cos(dir));
      } else {
        dir = (2 * Math.PI) - dir;
        max = new Position(x + hypotenuse * FastMath.cos(dir), y - hypotenuse * FastMath.sin(dir));
        min = new Position(x - hypotenuse * FastMath.cos(dir), y + hypotenuse * FastMath.sin(dir));
      }

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
  @Deprecated
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
  @Deprecated
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
  @Deprecated
  private ArrayList<Collision> sweep(ArrayList<IntervalValue> intervalValues) {
    ArrayList<Collision> potentialCollisions = new ArrayList<>();
    // Creates a list to store active intervals
    // As each interval has a beginning and end point, an active interval is one which has begun
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

  /**
   * Verifies that two Entities included in a Collision are colliding. This method uses the
   * Separating Axis Theorem to determine intersection between the Entities' Oriented Bounding Boxes
   * (OBBs).
   * 
   * @param c the Collision which is being checked
   * @return <code>true</code> if the Entities are colliding, <code>false</code> if they are not
   */
  public MTV verifyCollision(Collision c) {
    // Set overlap to be very large
    MTV minTranslationVector = new MTV(1000, null);

    // Get the two Entities' bounding boxes
    Position[] box1 = c.getEntity1().getObb();
    Position[] box2 = c.getEntity2().getObb();
    // Get the two sets of axes to test
    AxisVector[] axes1 = getAxes(box1);
    AxisVector[] axes2 = getAxes(box2);
    // Check against first set of axes
    for (AxisVector axis : axes1) {
      // Project both boxes onto a single axis
      Projection p1 = project(box1, axis);
      Projection p2 = project(box2, axis);
      // Check for overlap in the projections
      // If no overlap is found, there must be a gap between the Entities, meaning they are not
      // colliding so the method returns false
      if (!overlap(p1, p2)) {
        return null;
      } else {
        if (getOverlap(p1, p2) < minTranslationVector.getDistance()) {
          minTranslationVector = new MTV(getOverlap(p1, p2), axis);
        }
      }
    }
    // Check against second set of axes
    for (AxisVector axis : axes2) {
      Projection p1 = project(box1, axis);
      Projection p2 = project(box2, axis);
      if (!overlap(p1, p2)) {
        return null;
      }
    }

    // Control flow reaches this point if no gap has been found between the two Entities, so they
    // must collide
    return minTranslationVector;
  }

  // private boolean verifyCollision(Collision c) {
  // // Get the two Entities' bounding boxes
  // Position[] box1 = c.getEntity1().getObb();
  // Position[] box2 = c.getEntity2().getObb();
  // // Get the two sets of axes to test
  // AxisVector[] axes1 = getAxes(box1);
  // AxisVector[] axes2 = getAxes(box2);
  // // Check against first set of axes
  // for (AxisVector axis : axes1) {
  // // Project both boxes onto a single axis
  // Projection p1 = project(box1, axis);
  // Projection p2 = project(box2, axis);
  // // Check for overlap in the projections
  // // If no overlap is found, there must be a gap between the Entities, meaning they are not
  // // colliding so the method returns false
  // if (!overlap(p1, p2)) {
  // return false;
  // }
  // }
  // // Check against second set of axes
  // for (AxisVector axis : axes2) {
  // Projection p1 = project(box1, axis);
  // Projection p2 = project(box2, axis);
  // if (!overlap(p1, p2)) {
  // return false;
  // }
  // }
  //
  // // Control flow reaches this point if no gap has been found between the two Entities, so they
  // // must collide
  // return true;
  // }


  /**
   * Gets a set of axes to test against when given an Oriented Bounding Box (OBB).
   * 
   * @param box an OBB to get axes from
   * @return a set of AxisVectors
   */
  private AxisVector[] getAxes(Position[] box) {
    AxisVector[] axes = new AxisVector[box.length];
    for (int i = 0; i < box.length; i++) {
      Position p1 = box[i];
      // If the i is the last corner, set the next corner to 0 to find the last edge
      // Else, use the i+1th corner
      Position p2 = box[i + 1 == box.length ? 0 : i + 1];
      // Calculate the edge vector between the two points
      AxisVector edge = new AxisVector(p1.getX() - p2.getX(), p1.getY() - p2.getY());
      // Calculate the normal of the edge vector
      AxisVector normal = new AxisVector(-edge.getY(), edge.getX());
      // Since the normal is an axis we want, add it to the array
      axes[i] = normal;
    }
    return axes;
  }

  /**
   * Calculates the projection of an Oriented Bounding Box (OBB) onto an axis.
   * 
   * @param box the OBB being projected
   * @param axis the axis to project onto
   * @return the Projection of the OBB onto the axis
   */
  private Projection project(Position[] box, AxisVector axis) {
    // Set up max and min values of the Projection
    // Each value is the dot product of a corner of the box with the axis
    double min = dotProduct(box[0], axis);
    double max = min;
    // Test the value from each corner against the max and min to find the max and min of the
    // Projection
    for (int i = 1; i < box.length; i++) {
      double value = dotProduct(box[i], axis);
      if (value < min) {
        min = value;
      } else if (value > max) {
        max = value;
      }
    }
    return new Projection(min, max);
  }

  /**
   * Performs the dot product (scalar product) operation between a Position and AxisVector.
   * 
   * @param position a Position
   * @param axis an AxisVector
   * @return the dot product between the Position and the AxisVector
   */
  private double dotProduct(Position position, AxisVector axis) {
    double dot = position.getX() * axis.getX() + position.getY() * axis.getY();
    return dot;
  }

  /**
   * Tests if there is an overlap between two Projections.
   * 
   * @param p1 the first Projection
   * @param p2 the second Projection
   * @return <code>true</code> if there is an overlap, <code>false</code> if there is not
   */
  private boolean overlap(Projection p1, Projection p2) {
    if (p2.getMin() < p1.getMax()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the amount of overlap between two Projections.
   * 
   * @param p1 the first Projection
   * @param p2 the second Projection
   * @return the amount of overlap between two Projections
   */
  private double getOverlap(Projection p1, Projection p2) {
    double length1 = p2.getMin() - p1.getMax();
    double length2 = p1.getMin() - p2.getMax();
    double abs1 = FastMath.abs(length1);
    double abs2 = FastMath.abs(length2);
    double res = FastMath.min(abs1, abs2);
    // Divide by 1000 to ensure that the numbers involved are not so huge
    return res / 1000;
  }
}

