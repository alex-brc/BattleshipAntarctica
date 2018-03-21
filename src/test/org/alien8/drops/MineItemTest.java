package test.org.alien8.drops;

import org.alien8.core.Entity;
import org.alien8.core.ServerModelManager;
import org.alien8.drops.Mine;
import org.alien8.drops.MineItem;
import org.alien8.drops.Pickup;
import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;
import org.junit.Test;

public class MineItemTest {
	@Test
	public void testUse() {
		// Make an item
		MineItem item = new MineItem();
		// Check the item
		assert(item.getSprite() == Sprite.item_mine);
		assert(item.getItemType() == Pickup.MINE);
		// Make a ship
		Ship ship = new Ship(new Position(0,0), 0, 0);
		// Set a ship for the item
		item.setShip(ship);
		// Use it
		item.use();
		// Check a mine was made
		boolean check = false;
		for(Entity e : ServerModelManager.getInstance().getEntities())
			if(e instanceof Mine)
				check = true;
		assert(check);
	}
}
