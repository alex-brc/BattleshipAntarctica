package org.alien8.client;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.alien8.core.EntityLite;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.ship.Ship;

public class Client implements Runnable {
  /**
   * Volatile "running" boolean to avoid internal caching. Thread should stop when set to false.
   */
  private volatile  boolean running = false;
  private Thread thread;
  private Renderer renderer;
  private ModelManager model;
  private int FPS = 0;
  private Socket server = null;
  private static DatagramSocket udpServer = null;
  private InetAddress serverIP = null;
  private ClientInputSampleSender ciss = null;
  // private ClientGameStateReceiver cgsr = null;


  public static void main(String[] args) {

    Client game = new Client();
    game.start();
  }

  public Client() {
    renderer = new Renderer(new Dimension(800, 600));
    model = ModelManager.getInstance();
    Ship ship = new Ship(new Position(200, 200), 0); // <-- Comment out this line to test networking
    model.setPlayer(ship); // <-- Comment out this line to test networking
    model.addEntity(ship); // <-- Comment out this line to test networking
    Ship notPlayer = new Ship(new Position(100, 100), 0); // <-- Comment out this line to test networking
    notPlayer.setSpeed(0.8); // <-- Comment out this line to test networking
    model.addEntity(notPlayer); // temporary reference point // <-- Comment out this line to test networking
  }

  /**
   * Starts the main loop of the game.
   */
  public void start() {
    // Do nothing if the game is already running
    if (running)
      return;
    running = true;

    thread = new Thread(this, "Battleship Antarctica");
    thread.start();
    // Start the loop
  }

  public void stop() {
    running = false;
    try {
      thread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * The main loop of the game. A common way to implement it. This implementation basically allows
   * the renderer to do it's job separately from the update() method. If a certain computer tends to
   * be slower on the render() side, then it can perform more fixed time updates in between frames
   * to compensate. Faster computers wouldn't see any improvement.
   */
  @Override
  public void run() {
    // Game loop goes here
    while (running) {
      long lastTime = getTime();
      long currentTime = 0;
      double catchUp = 0;

      int frameRate = 0;
      long frameTimer = getTime();
      
      // this.connect("192.168.0.15"); // <-- Uncomment this line to test networking
      while (running) {
        currentTime = getTime();

        // Get the amount of update()s the model needs to catch up
        catchUp += (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

        // Call update() as many times as needed to compensate before rendering
        while (catchUp >= 1) {
          model.update(); // <-- Comment out this line to test networking
          // this.receiveAndUpdate(); // <-- Uncomment this line to test networking
          catchUp--;
          // Update last time
          lastTime = getTime();
        }

        // Call the renderer
		//ai.update();
        renderer.render(model);
        frameRate++;

        // Update the FPS timer every FPS_FREQ^-1 seconds
        if (getTime() - frameTimer > Parameters.N_SECOND / Parameters.FPS_FREQ) {
          frameTimer += Parameters.N_SECOND / Parameters.FPS_FREQ;
          FPS = frameRate * Parameters.FPS_FREQ;
          frameRate = 0;
          System.out.println(FPS);
        }

      }
      System.out.println("stopped");
    }
  }

  /**
   * Getter for the latest FPS estimation.
   * 
   * @return
   */
  public int getFPS() {
    return FPS;
  }

  /**
   * Pauses the game.
   */
  public void pause() {
    running = false;
  }

  /**
   * Gets current time in nanoseconds from the JVM
   * 
   * @return current time in nanoseconds
   */
  private long getTime() {
    return System.nanoTime();
  }

  /**
   * Should be called when the client clicks the 'Connect' button after entering an server IP.
   */
  public void connect(String serverIPStr) {
	if (server == null && serverIP == null && ciss == null) {
	  try {
		serverIP = InetAddress.getByName(serverIPStr);
		server = new Socket(serverIP, 4446);
		udpServer = new DatagramSocket(4445, serverIP);
		
		// Serialize a TRUE Boolean object (representing connect request) into byte array 
		Boolean connectRequest = new Boolean(true);
		ObjectOutputStream toServer = new ObjectOutputStream(server.getOutputStream());
		ObjectInputStream fromServer = new ObjectInputStream(server.getInputStream());
		toServer.writeObject(connectRequest);
		
		// Wait for a full snapshot of current game state from server
		ArrayList<EntityLite> initialEntitiesLite = getFullSnapshot(fromServer);
		
		// Full sync the game state
		model.fullSync(initialEntitiesLite);
		
		// Client's Ship is stored at the end of the synced entities queue
		Object[] entitiesArr = model.getEntities().toArray();
		model.setPlayer((Ship) entitiesArr[entitiesArr.length - 1]);
		
		// Start a thread responsible for sending client input sample regularly
		ciss = new ClientInputSampleSender(serverIP, udpServer);
		ciss.start();
	}
	catch (SocketException se) {
		se.printStackTrace();
	}
	catch (UnknownHostException uhe) {
		uhe.printStackTrace();
	}
	catch (IOException ioe) {
		ioe.printStackTrace();
	}
  }
}

  private ArrayList<EntityLite> getFullSnapshot(ObjectInputStream fromServer) {
	  ArrayList<EntityLite> fullSnapShot = null;
	  
	  try {
		  fullSnapShot = (ArrayList<EntityLite>) fromServer.readObject();
	  }
	  catch (IOException ioe) {
		  ioe.printStackTrace();
	  }
	  catch (ClassNotFoundException cnfe) {
		  cnfe.printStackTrace();
	  }
	  
	  return fullSnapShot;
  }
  
  /*
   * Receive the game state difference from the server and sync the game state with the server
   */
  public void receiveAndUpdate() {
	  try {
			// Create a packet for receiving difference packet
		    byte[] buf = new byte[65536];
		    DatagramPacket packet = new DatagramPacket(buf, buf.length);
		    
		    // Receive a difference packet and obtain the byte data
		    udpServer.receive(packet);
		    byte[] differenceByte = packet.getData();
		    
		    // Deserialize the difference byte data into object
		    ByteArrayInputStream byteIn = new ByteArrayInputStream(differenceByte);
		    ObjectInputStream objIn = new ObjectInputStream(byteIn);
		    ArrayList<EntityLite> difference = (ArrayList<EntityLite>) objIn.readObject();
		    
		    // Sync the game state with server
		    ModelManager.getInstance().sync(difference);
		    System.out.println("Entities: " + model.getEntities().toString());
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
  }

  /**
   * Should be called when the client clicks the 'Exit' button from the in-game menu. TODO
   */
//  public void disconnect() {
//	if (server != null && serverIP != null && ciss != null) {
//      try {
//        // Serialize a FALSE Boolean object (representing disconnect request) into byte array
//        Boolean disconnectRequest = new Boolean(false);
//        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
//        objOut.writeObject(disconnectRequest);
//        byte[] disconnectRequestByte = byteOut.toByteArray();
//
//        DatagramPacket packet = new DatagramPacket(disconnectRequestByte, disconnectRequestByte.length, serverIP, 4446);
//
//        // Send the disconnect request packet to the server
//        //socket.send(packet);
//
//        // Stop all the client-side threads and socket
//        ciss.end();
//        //socket.close();
//
//        // Reset the socket, serverIP and client-side threads after disconnecting
//        //socket = null;
//        serverIP = null;
//        ciss = null;
//      } catch (IOException ioe) {
//        ioe.printStackTrace();
//      }
//    }
//  }
}
