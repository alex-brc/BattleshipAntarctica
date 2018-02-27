package org.alien8.client;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.alien8.ai.AIController;
import org.alien8.core.ClientRequest;
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
  private AIController aiPlayer;
  private int FPS = 0;
  private InetAddress serverIP = null;
  private InetAddress multiServerIP = null;
  private int clientMultiPort = 4445;
  private Socket tcpSocket = null;
  private DatagramSocket udpSocket = null;
  private MulticastSocket multiReceiver = null;
  private String groupIPStr = "224.0.0.5";
  private String serverIPstr = "192.168.0.15"; //<- change to the ip of the server to test


  public static void main(String[] args) {

    Client game = new Client();
    game.start();
  }

  public Client() {
    renderer = new Renderer(new Dimension(800, 600));
    model = ModelManager.getInstance();
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
      
      int tickRate = 0;
      long tickTimer = getTime();
      
      try {
		System.out.println("client connect to IP: " + InetAddress.getByName(serverIPstr));
	  } 
      catch (UnknownHostException e) {
		e.printStackTrace();
	  }
      this.connect(serverIPstr);
      while (running) {
        currentTime = getTime();

        // Get the amount of update()s the model needs to catch up
        // 
        //                  timeNow - timeLastUpdateWasDone    --> 
        // timeToCatchUp = ----------------------------------
        //							deltaTPerTick              --> how long a "tick" is
        // 
        catchUp += (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

        // Call update() as many times as needed to compensate before rendering
       while (catchUp >= 1) {
    	  this.sendInputSample();
          this.receiveAndUpdate();
          System.out.println("Entities: " + ModelManager.getInstance().getEntities());
          tickRate++;
          catchUp--;
          // Update last time
          lastTime = getTime();
       }

        // Call the renderer
		// aiPlayer.update();
        renderer.render(model);
      }
        frameRate++;

        // Update the FPS timer every FPS_FREQ^-1 seconds
        if (getTime() - frameTimer > Parameters.N_SECOND / Parameters.FPS_FREQ) {
          frameTimer += Parameters.N_SECOND / Parameters.FPS_FREQ;
          FPS = frameRate * Parameters.FPS_FREQ;
          frameRate = 0;
          System.out.println(FPS);
        }
        if(getTime() - tickTimer > Parameters.N_SECOND) {
        	tickTimer += Parameters.N_SECOND;
        	System.out.println(tickRate);
        	tickRate = 0;
        }
      }
      System.out.println("stopped");
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
	if (tcpSocket == null && serverIP == null) {
	  try {
		serverIP = InetAddress.getByName(serverIPStr);
		tcpSocket = new Socket(serverIP, 4446);
		udpSocket = new DatagramSocket();
		System.out.println(udpSocket.getLocalPort());
		
		// Serialize the ClientRequest object
		ClientRequest connectRequest = new ClientRequest(0, udpSocket.getLocalPort());
		ObjectOutputStream toServer = new ObjectOutputStream(tcpSocket.getOutputStream());
		ObjectInputStream fromServer = new ObjectInputStream(tcpSocket.getInputStream());
		toServer.writeObject(connectRequest);
		
		// Wait for a full snapshot of current game state from server
		ArrayList<EntityLite> initialEntitiesLite = getFullSnapshot(fromServer);
		
		// Full sync the game state
		model.fullSync(initialEntitiesLite);
		
		// Client's Ship is stored at the end of the synced entities queue
		Object[] entitiesArr = model.getEntities().toArray();
		model.setPlayer((Ship) entitiesArr[entitiesArr.length - 1]);
		
		// set up multicast socket client receiver
		multiServerIP = InetAddress.getByName(groupIPStr);
		multiReceiver = new MulticastSocket(clientMultiPort);
		multiReceiver.joinGroup(multiServerIP);
  	  }
	  catch (BindException be) {
		  be.printStackTrace();
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
  
  private void sendInputSample() {
	  try {
			// Serialize the input sample object into byte array 
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
			ClientInputSample cis = new ClientInputSample();
			objOut.writeObject(cis);
			byte[] clientInputSampleByte = byteOut.toByteArray();
			
			// Create a packet for holding the input sample byte data
			DatagramPacket packet = new DatagramPacket(clientInputSampleByte, clientInputSampleByte.length, serverIP, 4446);
			
			// Send the client input sample packet to the server
			udpSocket.send(packet);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
  }
  
  /*
   * Receive the game state difference from the server and sync the game state with the server
   */
  public void receiveAndUpdate() {
	  try {
			// Create a packet for receiving difference packet
		    byte[] buf = new byte[65536];
		    DatagramPacket packet = new DatagramPacket(buf, buf.length);
		    
		    // Receive a difference packet and obtain the byte data-
	        multiReceiver.receive(packet);
		    byte[] differenceByte = packet.getData();
		    
		    // Deserialize the difference byte data into object
		    ByteArrayInputStream byteIn = new ByteArrayInputStream(differenceByte);
		    ObjectInputStream objIn = new ObjectInputStream(byteIn);
		    ArrayList<EntityLite> difference = (ArrayList<EntityLite>) objIn.readObject();
		    
		    // Sync the game state with server
		    ModelManager.getInstance().sync(difference);
//		    System.out.println("Client receive Entities: " + model.getEntities());
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
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

  /**
   * Should be called when the client clicks the 'Exit' button from the in-game menu. TODO
   */
  public void disconnect() {
	if (tcpSocket != null && serverIP != null) {
      try {
        // Serialize a FALSE Boolean object (representing disconnect request) into byte array
  		Boolean disconnectRequest = new Boolean(false);
  		ObjectOutputStream toServer = new ObjectOutputStream(tcpSocket.getOutputStream());
  		ObjectInputStream fromServer = new ObjectInputStream(tcpSocket.getInputStream());
  		
  		// Send the disconnect request
  		toServer.writeObject(disconnectRequest);
  		
        // Reset the socket, serverIP and client-side threads after disconnecting
        tcpSocket = null;
        udpSocket = null;
        multiReceiver = null;
        serverIP = null;
      }
      catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }
}
