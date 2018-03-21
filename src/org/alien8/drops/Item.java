package org.alien8.drops;

import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;

/**
 * This class represents a use item that can be held by a Ship and used affect the game in some way.
 *
 */
public abstract class Item {
  protected Ship ship;
  protected Sprite sprite;
  protected int itemType;

  /**
   * Constructor.
   * 
   * @param sprite the Sprite that represents the Item
   * @param itemType the type of the Item
   */
  public Item(Sprite sprite, int itemType) {
    this.sprite = sprite;
    this.itemType = itemType;
  }

  /**
   * @param ship the Ship to set
   */
  public void setShip(Ship ship) {
    this.ship = ship;
  }

  /**
   * @return the Sprite of this Item
   */
  public Sprite getSprite() {
    return sprite;
  }

  /**
   * @return the type of this Item
   */
  public int getItemType() {
    return itemType;
  }

  /**
   * Uses the Item, consuming it and producing some change to the game. Abstract to allow for
   * different effects when an Item is used.
   */
  public abstract void use();
}
