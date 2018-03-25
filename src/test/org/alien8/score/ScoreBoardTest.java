package test.org.alien8.score;

import org.alien8.core.Parameters;
import org.alien8.physics.Position;
import org.alien8.score.ClientScoreBoard;
import org.alien8.score.Score;
import org.alien8.score.ScoreEvent;
import org.alien8.score.ServerScoreBoard;
import org.alien8.server.Player;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.junit.Test;

public class ScoreBoardTest {
	
	@Test
	public void testScore() {
		Ship ship = new Ship(
				new Position(0,0), 0, 0
				);
		ship.setSerial(0);
		// Test player constructor
		Score score = new Score(new Player(
				"test", null, 0, ship));
		assert(score.getName() == "test");
		assert(score.getShipSerial() == 0);
		assert(score.getColour() == 0);
		// Test scoreevent constructor
		score = new Score(
				new ScoreEvent(0, "test", 0, 0, 0, true
						));
		// Test give score
		score.giveScore(50);
		assert(score.getScore() == 50);
		// Test givehit
		score.giveHit(new Bullet(new Position(0,0),0, 0, 0));
		assert(score.getScore() == 65);
		// Test giveKill
		score.giveKill();
		assert(score.getKills() == 1);
		assert(score.getScore() == 65 + Parameters.SCORE_PER_KILL);
		// Test toString
		score.toString();
		// Test export to event
		ScoreEvent se =  score.exportToEvent();
		assert(se.getScore() == 65 + Parameters.SCORE_PER_KILL);
		assert(se.getKills() == 1);
		assert(se.getAlive() == true);
		assert(se.getColour() == 0);
		assert(se.getName() == "test");
		assert(se.getShipSerial() == 0);
	}
	
	@Test
	public void testServerScoreBoard() {
		// Get a scoreboard
		ServerScoreBoard sb = ServerScoreBoard.getInstance();
		sb = ServerScoreBoard.getInstance();
		// Make two players
		Ship s1 = new Ship(new Position(0,0), 0, 0);
		s1.setSerial(0);
		Player p1 = new Player("test1", null, 0, s1);
		Ship s2 = new Ship(new Position(0,0), 0, 0);
		s2.setSerial(1);
		Player p2 = new Player("test2", null, 0, s2);
		// Populate it
		sb.add(p1);
		assert(sb.getScore(p2) == null);
		sb.add(p2);
		// Check 
		assert(sb.getScores().size() == 2);
		assert(sb.getScore(p1) != null);
		// Test giveScore
		sb.giveScore(p2, 10);
		System.out.println(sb.getScore(p1).getScore());
		assert(sb.getScore(p2).getScore() == 10);
		// Test giveHit
		sb.giveHit(p1, new Bullet(new Position(0,0), 0, 0, 0));
		assert(sb.getScore(p1).getScore() == 15);
		// Test giveKill
		sb.giveKill(p2);
		assert(sb.getScore(p2).getScore() == Parameters.SCORE_PER_KILL + 10);
		assert(sb.getScore(p2).getKills() == 1);
		// Test kill
		sb.kill(p1.getShip());
		assert(sb.getScore(p1).getAlive() == false);
		// Test remove
		sb.remove(p2);
		assert(sb.getScore(p2) == null);
		// Test clean
		sb.reset();
		assert(sb.getScores().size() == 0);
		sb.giveScore(p1, 0);
		sb.giveHit(p1, new Bullet(new Position(0,0), 0, 0, 0));
		sb.giveKill(p1);
		sb.kill(p1.getShip());
	}

	@Test
	public void testClientScoreBoard() {
		// Get a scoreboard
		ClientScoreBoard sb = ClientScoreBoard.getInstance();
		sb = ClientScoreBoard.getInstance();
		// Populate it
		Score score = new Score(
				new ScoreEvent(0, "test", 0, 0, 2, true
						));
		sb.update(score);
		score = new Score(
				new ScoreEvent(2, "test", 0, 0, 2, true
						));
		sb.update(score);
		// Check it's there
		assert(sb.getScore(0) != null);
		assert(sb.getScore(1) == null);
		// Update it
		assert(sb.getScore(0).getAlive() == true);
		score.kill();
		sb.update(score);
		assert(score.getAlive() == false);
		// Check it's updated
		assert(sb.getScore(2).getAlive() == false);
		// Test render
		sb.render();
		// Clean it
		sb.reset();
		assert(sb.getScore(0) == null);
	}
	
	
}
