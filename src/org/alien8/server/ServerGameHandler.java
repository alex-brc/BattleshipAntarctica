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
import org.alien8.core.ModelManager;
import org.alien8.core.Parameters;
import org.alien8.items.Pickup;
import org.alien8.items.PlaneDropper;
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
  private volatile boolean run = true;

  public ServerGameHandler(DatagramSocket ds, InetAddress ip,
      ConcurrentLinkedQueue<Entity> entities,
      ConcurrentHashMap<Player, ClientInputSample> latestCIS, ArrayList<Player> playerList) {
    udpSocket = ds;
    multiCastIP = ip;
    this.entities = entities;
    this.latestCIS = latestCIS;
    this.playerList = playerList;
  }

  public void run() {
    long lastTime = getTime();
    long currentTime = 0;
    double tick = 0;

    while (run) {
      currentTime = getTime();
      tick += 1.0 * (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);
      while (tick >= 1) {
        this.readInputSample();
        this.updateGameStateByCIS();
        this.sendGameState();
        tick--;
        // Update last time
        lastTime = getTime();
      }
    }
  }

  /**
   * Gets current time in nanoseconds from the JVM
   * 
   * @return current time in nanoseconds
   */
  private long getTime() {
    return System.nanoTime();
  }

  public void end() {
    run = false;
  }

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

        // Put the received input sample in the CIS hash map
        if (p != null && cis != null)
          if (latestCIS.containsKey(p))
            latestCIS.replace(p, cis);
          else
            latestCIS.put(p, cis);
      }
    } catch (SocketTimeoutException ste) {
      // Do nothing, just proceed
    } catch (IOException ioe) {
      LogManager.getInstance().log("ServerMulticastSender", LogManager.Scope.CRITICAL,
          "Something wrong when deserializing input sample byte");
      ioe.printStackTrace();
    } catch (ClassNotFoundException cnfe) {
      LogManager.getInstance().log("ServerMulticastSender", LogManager.Scope.CRITICAL,
          "Cannot find the class of the received serialized object");
      cnfe.printStackTrace();
    }
  }

  public void updateGameStateByCIS() {
    ModelManager.getInstance().updateServer(latestCIS);
  }

  /*
   * Create a compressed set of entities (game state) from the original set of entities
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
              s.getColour(), p.getIP(), p.getPort()));
        } else { // AI ship
          EntitiesLite.add(new EntityLite(s.getSerial(), 1, s.getPosition(), s.isToBeDeleted(),
              s.getDirection(), s.getSpeed(), s.getHealth(), s.getFrontTurretDirection(),
              s.getRearTurretDirection(), s.getColour()));
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
    	  System.out.println("Sending plane at " + pd.getPosition() );
    	  EntitiesLite.add(new EntityLite(4, pd.getPosition(), pd.isToBeDeleted(), pd.getDirection()));
      }
    }

    return EntitiesLite;
  }

  public void sendGameState() {
    try {
      ArrayList<EntityLite> entsLite = calculateEntitiesLite(entities);

      // Serialize the entsLite arraylist into byte array
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
      objOut.writeObject(entsLite);
      sendingByte = byteOut.toByteArray();

      // Create a packet for holding the entsLite byte data
      DatagramPacket entsLitePacket =
          new DatagramPacket(sendingByte, sendingByte.length, multiCastIP, Parameters.MULTI_CAST_PORT);

      // Make the game event packet
      GameEvent event = Server.getInstance().getNextEvent();
      byteOut = new ByteArrayOutputStream();
      objOut = new ObjectOutputStream(byteOut);
      objOut.writeObject(event);
      sendingByte = byteOut.toByteArray();

      // Make packet
      DatagramPacket eventPacket =
          new DatagramPacket(sendingByte, sendingByte.length, multiCastIP, Parameters.MULTI_CAST_PORT);

      // Send the entsLite packet and the event packet to client
      udpSocket.send(entsLitePacket);
      udpSocket.send(eventPacket);
    } catch (IOException ioe) {
      LogManager.getInstance().log("ServerMulticastSender", LogManager.Scope.CRITICAL,
          "Packet error: " + ioe.toString());
      ioe.printStackTrace();
    }
  }

}
