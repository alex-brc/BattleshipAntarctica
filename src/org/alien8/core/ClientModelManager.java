package org.alien8.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.ai.AIController;
import org.alien8.client.ClientInputSample;
import org.alien8.client.InputManager;
import org.alien8.drops.Effect;
import org.alien8.drops.HealthItem;
import org.alien8.drops.HealthPickup;
import org.alien8.drops.InvulnerableItem;
import org.alien8.drops.InvulnerablePickup;
import org.alien8.drops.Mine;
import org.alien8.drops.MineItem;
import org.alien8.drops.MinePickup;
import org.alien8.drops.NoCooldownItem;
import org.alien8.drops.NoCooldownPickup;
import org.alien8.drops.Pickup;
import org.alien8.drops.PlaneDropper;
import org.alien8.drops.SpeedItem;
import org.alien8.drops.SpeedPickup;
import org.alien8.drops.Torpedo;
import org.alien8.drops.TorpedoItem;
import org.alien8.drops.TorpedoPickup;
import org.alien8.mapgeneration.Map;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;


/**
 * This class implements the model at the core of the game itself. It keeps the record of game
 * entities in a ConcurrentLinkedQueue<Entity>.
 */
public class ClientModelManager {

  private long lastSerial = 0;
  private static ClientModelManager instance;
  private ConcurrentLinkedQueue<Entity> entities = new ConcurrentLinkedQueue<Entity>();
  private Map map;
  private Ship player;

  /**
   * Private constructor to prevent global instantiation
   */
  private ClientModelManager() {

  }

  /**
   * A standard getInstance() in accordance with the singleton pattern. Create and return a
   * ClientModelManager instance the first time being called, only return the instance afterwards
   * 
   * @return a ClientModelManager instance
   */
  public static ClientModelManager getInstance() {
    if (instance == null)
      instance = new ClientModelManager();
    return instance;
  }

  /**
   * Reset game state related fields
   */
  public void reset() {
    lastSerial = 0;
    entities = new ConcurrentLinkedQueue<Entity>();
    map = null;
    player = null;
  }

  /**
   * Make a map from a random seed
   * 
   * @param seed A random long
   */
  public void makeMap(long seed) {
    map = new Map(Parameters.MAP_HEIGHT, Parameters.MAP_WIDTH, 8, 8, seed);
  }

  /**
   * Sync the client with the server
   * 
   * @param entitiesLite An array-list of EntityLite
   * @param clientIP This client's IP address
   * @param clientUdpPort This client's UDP port number
   */
  public void sync(ArrayList<EntityLite> entitiesLite, InetAddress clientIP,
      Integer clientUdpPort) {
    // Remove all entities
    for (Entity e : entities) {
      entities.remove(e);
      this.lastSerial = 0;
    }

    // Add updated entities
    for (EntityLite el : entitiesLite) {
      if (el.entityType == 0) { // Player Ship
        Ship s = new Ship(el.position, el.direction, el.colour);
        s.setSpeed(el.speed);
        s.setHealth(el.health);
        s.getFrontTurret().setDirection(el.frontTurretDirection);
        s.getRearTurret().setDirection(el.rearTurretDirection);
        s.getFrontTurret().setDistance(el.frontTurretCharge);
        s.getRearTurret().setDistance(el.rearTurretCharge);

        // Give item
        switch (el.itemType) {
          case Pickup.HEALTH:
            s.giveItem(new HealthItem());
            break;
          case Pickup.MINE:
            s.giveItem(new MineItem());
            break;
          case Pickup.INVULNERABLE:
            s.giveItem(new InvulnerableItem());
            break;
          case Pickup.SPEED:
            s.giveItem(new SpeedItem());
            break;
          case Pickup.NO_COOLDOWN:
            s.giveItem(new NoCooldownItem());
            break;
          case Pickup.TORPEDO:
            s.giveItem(new TorpedoItem());
            break;
          default:
            // Don't give an item
            break;
        }

        // Apply effect
        switch (el.effectType + 1) {
          case Pickup.INVULNERABLE:
            s.applyEffect(new Effect(999999999, Effect.INVULNERABLE));
            break;
          case Pickup.SPEED:
            s.applyEffect(new Effect(999999999, Effect.SPEED));
            break;
          case Pickup.NO_COOLDOWN:
            s.applyEffect(new Effect(999999999, Effect.NO_COOLDOWN));
            break;
          default:
            // Don't apply effect
            break;
        }

        if (el.toBeDeleted) {
          s.delete();
        }

        if (el.clientIP.equals(clientIP) && el.clientUdpPort == clientUdpPort) { // Client's ship
          this.setPlayer(s);
        }

        this.addEntity(s);
        s.setSerial(el.serial);
      } else if (el.entityType == 1) { // AI Ship
        Ship s = new Ship(el.position, el.direction, el.colour);
        s.setSpeed(el.speed);
        s.setHealth(el.health);
        s.getFrontTurret().setDirection(el.frontTurretDirection);
        s.getRearTurret().setDirection(el.rearTurretDirection);

        // Apply effect
        switch (el.effectType + 1) {
          case Pickup.INVULNERABLE:
            s.applyEffect(new Effect(999999999, Effect.INVULNERABLE));
            break;
          case Pickup.SPEED:
            s.applyEffect(new Effect(999999999, Effect.SPEED));
            break;
          case Pickup.NO_COOLDOWN:
            s.applyEffect(new Effect(999999999, Effect.NO_COOLDOWN));
            break;
          default:
            // Don't apply effect
            break;
        }

        if (el.toBeDeleted) {
          s.delete();
        }

        this.addEntity(s);
      } else if (el.entityType == 2) { // Bullet
        Bullet b = new Bullet(el.position, el.direction, el.distance, el.source);
        b.setSpeed(el.speed);
        b.setTravelled(el.travelled);

        if (el.toBeDeleted) {
          b.delete();
        }

        this.addEntity(b);
      } else if (el.entityType == 3) { // Pickup
        Pickup p = null;
        switch (el.pickupType) {
          case Pickup.HEALTH:
            p = new HealthPickup(el.position);
            break;
          case Pickup.MINE:
            p = new MinePickup(el.position);
            break;
          case Pickup.INVULNERABLE:
            p = new InvulnerablePickup(el.position);
            break;
          case Pickup.SPEED:
            p = new SpeedPickup(el.position);
            break;
          case Pickup.NO_COOLDOWN:
            p = new NoCooldownPickup(el.position);
            break;
          case Pickup.TORPEDO:
            p = new TorpedoPickup(el.position);
            break;
        }

        if (el.toBeDeleted) {
          p.delete();
        }

        this.addEntity(p);
      } else if (el.entityType == 4) { // Plane
        PlaneDropper pd = new PlaneDropper(el.position, el.direction);
        if (el.toBeDeleted) {
          pd.delete();
        }

        this.addEntity(pd);
      } else if (el.entityType == 5) { // Mine
        Mine m = new Mine(el.position, 0);
        if (el.toBeDeleted) {
          m.delete();
        }

        this.addEntity(m);
      } else if (el.entityType == 6) { // Torpedo
        Torpedo t = new Torpedo(el.position, 0, el.direction);
        if (el.toBeDeleted) {
          t.delete();
        }

        this.addEntity(t);
      }
    }
  }

  /**
   * Creates a serial number for it then adds the Entity to the entity queue
   * 
   * @param entity the Entity to add to the queue
   * @return true if the Entity was added successfully, false otherwise
   */
  public boolean addEntity(Entity entity) {
    // Give it a serial number
    lastSerial++;
    entity.setSerial(lastSerial);

    return entities.add(entity);
  }

  /**
   * Get the current Entity queue
   * 
   * @return An Entity queue
   */
  public ConcurrentLinkedQueue<Entity> getEntities() {
    return entities;
  }

  /**
   * Sets the designated ship as player.
   * 
   * @param player ship to set as the player
   */
  public void setPlayer(Ship player) {
    this.player = player;
  }

  /**
   * Get this player's designated ship
   * 
   * @return
   */
  public Ship getPlayer() {
    return player;
  }

  /**
   * Get the map
   * 
   * @return The map
   */
  public Map getMap() {
    return this.map;
  }

}
