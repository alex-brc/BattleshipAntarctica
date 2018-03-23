package org.alien8.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.alien8.client.ClientInputSample;
import org.alien8.core.Entity;
import org.alien8.core.EntityLite;
import org.alien8.core.Parameters;
import org.alien8.core.ServerModelManager;
import org.alien8.drops.Mine;
import org.alien8.drops.Pickup;
import org.alien8.drops.PlaneDropper;
import org.alien8.drops.Torpedo;
import org.alien8.ship.Bullet;
import org.alien8.ship.Ship;
import org.alien8.util.LogManager;

public class ServerGameHandler extends Thread {

  private InetAddress multiCastIP;
  private DatagramSocket udpSocket;
  private ConcurrentLinkedQueue<Entity> entities;
  private ConcurrentHashMap<Player, ClientInputSample> latestCIS;
  private ArrayList<Player> playerList;
  private byte[] buf = new byte[65536];
  private byte[] receivedByte;
  private byte[] sendingByte;
  private volatile boolean gameRunning = true;
  private int currentMatchTime;

  /**
   * Constructor
   * 
   * @param ds Server's UDP socket
   * @param ip Server's IP address
   * @param entities List of entity
   * @param latestCIS Table of the latest client input sample
   * @param playerList List of player
   */
  public ServerGameHandler(DatagramSocket ds, InetAddress ip,
      ConcurrentLinkedQueue<Entity> entities,
      ConcurrentHashMap<Player, ClientInputSample> latestCIS, ArrayList<Player> playerList) {
    udpSocket = ds;
    multiCastIP = ip;
    this.entities = entities;
    this.latestCIS = latestCIS;
    this.playerList = playerList;
  }

  /**
   * Main loop of the game
   */
  @Override
  public void run() {
    // Setup up variables for calculating time elapsed and ticks
    long lastTime = getNanoTime();
    long timer = getNanoTime();
    currentMatchTime = Parameters.MATCH_LENGTH;
    long currentTime = 0;
    double tick = 0;

    while (gameRunning) {
      // Get current time
      currentTime = getNanoTime();

      tick += (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

      while (tick >= 1) {
        this.readInputSample();
        this.updateGameStateByCIS();
        this.sendGameState();
        tick--;
        // Update last time
        lastTime = getNanoTime();
      }

      if (getNanoTime() - timer > Parameters.N_SECOND) {
        timer += Parameters.N_SECOND;
        currentMatchTime--;
        if (currentMatchTime % 10 == 0)
          ServerModelManager.getInstance().addEntity(new PlaneDropper());
        if (ServerModelManager.getInstance().countShips() == 1)
          gameRunning = false;
        updateServerTimer();
      }

      if (currentMatchTime <= 0) {
        gameRunning = false;
      }

    }

    // Notice all clients game has ended
    for (ClientHandler ch : Server.getInstance().getCHList()) {
      ch.sendEndGameMessage();
    }

    long timeWhenGameEnds = getNanoTime();

    // Wait 10 seconds before the server shut down (for players to read the leaderboard)
    while (getNanoTime() - timeWhenGameEnds < Parameters.TIME_BEFORE_SERVER_END
        * (long) Parameters.N_SECOND) {
      int timeBeforeExiting = Parameters.TIME_BEFORE_SERVER_END
          - (int) ((getNanoTime() - timeWhenGameEnds) / Parameters.N_SECOND);

      // Send info about how much time before exiting for client's renderer to update the time
      for (ClientHandler ch : Server.getInstance().getCHList()) {
        ch.sendTimeBeforeExiting(timeBeforeExiting);
      }
    }

    // Notice all clients that server has been shut down
    for (ClientHandler ch : Server.getInstance().getCHList()) {
      ch.sendServerStoppedMessage();
      ch.end();
    }

    // Stop the server
    Server.getInstance().stop();
    System.out.println("Server game handler stopped");
  }
  
  /**
   * @return true if the game is running, false otherwise
   */
  public boolean running() {
	  return gameRunning;
  }

  /**
   * Read input sample from client, number of input sample read for each call depends on the number
   * of player
   */
  private void readInputSample() {
    try {
      for (int i = 0; i < playerList.size(); i++) {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        // Receive an input sample packet and obtain its byte data
        udpSocket.receive(packet);
        InetAddress clientIP = packet.getAddress();
        int clientPort = packet.getPort();
        receivedByte = packet.getData();

        // Deserialize the input sample byte data into object
        ByteArrayInputStream byteIn = new ByteArrayInputStream(receivedByte);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        ClientInputSample cis = (ClientInputSample) objIn.readObject();

        // Identify which Player the CIS belongs to
        Player p = Server.getInstance().getPlayerByIpAndPort(clientIP, clientPort);

        // Put the received input sample in the CIS table
        if (p != null && cis != null)
          if (latestCIS.containsKey(p))
            latestCIS.replace(p, cis);
          else
            latestCIS.put(p, cis);
      }
    } catch (SocketTimeoutException ste) {
      // Do nothing, just proceed
    } catch (IOException ioe) {
      LogManager.getInstance().log("ServerGameHandler", LogManager.Scope.CRITICAL,
          "Something wrong when deserializing input sample byte");
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      LogManager.getInstance().log("ServerGameHandler", LogManager.Scope.CRITICAL,
          "Cannot find the class ClientInputSample");
      cnfe.printStackTrace();
    }
  }

  /**
   * Update the game state by Client's input sample
   */
  private void updateGameStateByCIS() {
    ServerModelManager.getInstance().updateServer(latestCIS);
  }

  /*
   * Create a compressed set of entities (ArrayList of EntityLite) from the original set of entities
   */
  private static ArrayList<EntityLite> calculateEntitiesLite(ConcurrentLinkedQueue<Entity> ents) {
    ArrayList<EntityLite> EntitiesLite = new ArrayList<EntityLite>();

    for (Entity e : ents) {
      if (e instanceof Ship) {
        Ship s = (Ship) e;
        Player p = Server.getInstance().getPlayerByShip(s);
        if (p != null) { // Player ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 0, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getRearTurretDirection(), s.getFrontTurretCharge(), s.getRearTurretCharge(),
              s.getColour(), s.getItemType(), s.getEffectType(), p.getIP(), p.getPort()));
        } else { // AI ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 1, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getRearTurretDirection(), s.getEffectType(), s.getColour()));
        }

      } else if (e instanceof Bullet) {
        Bullet b = (Bullet) e;
        EntitiesLite.add(new EntityLite(b.getSerial(), 2, b.getPosition(), b.isToBeDeleted(),
            b.getDirection(), b.getSpeed(), b.getDistance(), b.getTravelled(), b.getSource()));
      } else if (e instanceof Pickup) {
        Pickup p = (Pickup) e;
        EntitiesLite.add(new EntityLite(3, p.getPosition(), p.getPickupType(), p.isToBeDeleted()));
      } else if (e instanceof PlaneDropper) {
        PlaneDropper pd = (PlaneDropper) e;
        EntitiesLite
            .add(new EntityLite(4, pd.getPosition(), pd.isToBeDeleted(), pd.getDirection()));
      } else if (e instanceof Mine) {
        Mine m = (Mine) e;
        EntitiesLite.add(new EntityLite(5, m.getPosition(), m.isToBeDeleted(), m.getDirection()));
      } else if (e instanceof Torpedo) {
        Torpedo t = (Torpedo) e;
        EntitiesLite.add(new EntityLite(6, t.getPosition(), t.isToBeDeleted(), t.getDirection()));
      }
    }

    return EntitiesLite;
  }

  /**
   * Send a full snapshot of game state to players
   */
  private void sendGameState() {
    try {
      ArrayList<EntityLite> entsLite = calculateEntitiesLite(entities);

      // Serialize the entsLite arraylist into byte array
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
      objOut.writeObject(entsLite);
      sendingByte = byteOut.toByteArray();

      // Create a packet for holding the entsLite byte data
      DatagramPacket entsLitePacket = new DatagramPacket(sendingByte, sendingByte.length,
          multiCastIP, Parameters.MULTI_CAST_PORT);

      // Make the game event packet
      GameEvent event = Server.getInstance().getNextEvent();
      byteOut = new ByteArrayOutputStream();
      objOut = new ObjectOutputStream(byteOut);
      objOut.writeObject(event);
      sendingByte = byteOut.toByteArray();

      // Make packet
      DatagramPacket eventPacket = new DatagramPacket(sendingByte, sendingByte.length, multiCastIP,
          Parameters.MULTI_CAST_PORT);

      // Send the entsLite packet and the event packet to client
      udpSocket.send(entsLitePacket);
      udpSocket.send(eventPacket);
    } catch (IOException ioe) {
      LogManager.getInstance().log("ServerMulticastSender", LogManager.Scope.CRITICAL,
          "Packet error: " + ioe.toString());
      ioe.printStackTrace();
    }
  }

  /**
   * Update the server timer
   */
  private void updateServerTimer() {
    Server.getInstance().addEvent(new TimerEvent(currentMatchTime));
  }

  /**
   * Gets current time in nanoseconds from the JVM
   * 
   * @return current time in nanoseconds
   */
  private long getNanoTime() {
    return System.nanoTime();
  }

}

