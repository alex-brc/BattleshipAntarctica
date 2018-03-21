package test.org.alien8.drops;

import org.alien8.core.Entity;
import org.alien8.core.ServerModelManager;
import org.alien8.drops.Pickup;
import org.alien8.drops.Torpedo;
import org.alien8.drops.TorpedoItem;
import org.alien8.physics.Position;
import org.alien8.rendering.Sprite;
import org.alien8.ship.Ship;
import org.junit.Test;

public class TorpedoItemTest {
	@Test
	public void testUse() {
		// Make 2 items
		TorpedoItem item = new TorpedoItem();
		// Check the item
		assert(item.getSprite() == Sprite.item_torpedo);
		assert(item.getItemType() == Pickup.TORPEDO);
		// Make a ship
		Ship ship = new Ship(new Position(0,0), 0, 0);
		// Set a ship for the item
		item.setShip(ship);
		// Use it
		item.use();
		// Check a torpedo was made
		boolean check = false;
		for(Entity e : ServerModelManager.getInstance().getEntities())
			if(e instanceof Torpedo)
				check = true;
		assert(check);
	}
}
