package org.alien8.client;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.alien8.core.Entity;
import org.alien8.core.Parameters;
import org.alien8.managers.ModelManager;
import org.alien8.physics.Position;
import org.alien8.rendering.Renderer;
import org.alien8.ship.Ship;

public class Client implements Runnable{
	/**
	 *  Volatile "running" boolean to avoid internal caching. 
	 *	Thread should stop when set to false.
	 */
	private boolean running = false;
	private Thread thread;
	private Renderer renderer;
	private ModelManager model;
	private int FPS = 0;
	private DatagramSocket socket = null;
	private InetAddress serverIP = null;
	private ClientInputSampleSender ciss = null;
	private ClientGameStateReceiver cgsr = null;
	
	public static void main(String[] args){
		
		Client game = new Client();
		game.start();
	}
	
	public Client(){
		renderer = new Renderer(new Dimension(800, 600));
		model = ModelManager.getInstance();
		Entity ship = new Ship(new Position(1,1), 0);
		// Add the first ship to the map, this will be the player for now
		model.addEntity(ship);
		model.addEntity(new Ship(new Position(100,100), 0)); //temporary reference point
	}
	
	/**
	 * Starts the main loop of the game.
	 */
	public void start(){
		// Do nothing if the game is already running
			if (running)
				return;
			running = true;
			
			thread = new Thread(this, "Battleship Antarctica");
			thread.start();
			// Start the loop
	}
	
	public void stop(){
		running = false;
		try{
			thread.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	/**
	 * The main loop of the game. A common way to implement it. 
	 * This implementation
	 * basically allows the renderer to do it's job separately from the update()
	 * method. If a certain computer tends to be slower on the render() side, then
	 * it can perform more fixed time updates in between frames to compensate.
	 * Faster computers wouldn't see any improvement.
	 */
	@Override
	public void run() {
		//Game loop goes here
		while(running){
			long lastTime = getTime();
			long currentTime = 0;
			double catchUp = 0;

			int frameRate = 0;
			long frameTimer = getTime();
			int tickRate = 0;
			long tickTimer = getTime();

			while (running) {
				currentTime = getTime();

				// Get the amount of update()s the model needs to catch up
				catchUp += (currentTime - lastTime) / (Parameters.N_SECOND / Parameters.TICKS_PER_SECOND);

				// Call update() as many times as needed to compensate before rendering
				while (catchUp >= 1) {
					model.update();
					catchUp--;
					tickRate++;
					// Update last time
					lastTime = getTime();
				}

				// Call the renderer
				renderer.render(model);
				frameRate++;

				// Update the FPS timer every FPS_FREQ^-1 seconds
				if (getTime() - frameTimer > Parameters.N_SECOND / Parameters.FPS_FREQ) {
					frameTimer += Parameters.N_SECOND / Parameters.FPS_FREQ;
					FPS = frameRate * Parameters.FPS_FREQ;
					frameRate = 0;
				}
				if(getTime() - tickTimer > Parameters.N_SECOND) {
					System.out.println(tickRate);
					tickTimer = getTime();
					tickRate = 0;
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
	 * @return current time in nanoseconds
	 */
	private long getTime() {
		return System.nanoTime();
	}
	
	/**
	 * Should be called when the client clicks the 'Connect' button after entering an server IP.
	 */
	public void connect(String serverIPStr) {
		if (socket == null && serverIP == null && ciss == null && cgsr == null) {
			try {
				socket = new DatagramSocket(4446);
				serverIP = InetAddress.getByName(serverIPStr);
				
				// Serialize a TRUE Boolean object (representing connect request) into byte array 
				Boolean connectRequest = new Boolean(true);
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
				objOut.writeObject(connectRequest);
				byte[] connectRequestByte = byteOut.toByteArray();
				
				DatagramPacket packet = new DatagramPacket(connectRequestByte, connectRequestByte.length, serverIP, 4446);
				
				// Send the connect request packet to the server
				socket.send(packet);
				
				// Start a thread responsible for sending client input sample regularly
				ciss = new ClientInputSampleSender(serverIP, socket);
				ciss.start();
				
				// Start a thread for handling game state snapshot received from the server
				cgsr = new ClientGameStateReceiver(socket);
				cgsr.start();
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
	
	/**
	 * Should be called when the client clicks the 'Exit' button from the in-game menu.
	 */
	public void disconnect() {
		if (socket != null && serverIP != null && ciss != null && cgsr != null) {
			try {
				// Serialize a FALSE Boolean object (representing disconnect request) into byte array 
				Boolean disconnectRequest = new Boolean(false);
				ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
				objOut.writeObject(disconnectRequest);
				byte[] disconnectRequestByte = byteOut.toByteArray();
				
				DatagramPacket packet = new DatagramPacket(disconnectRequestByte, disconnectRequestByte.length, serverIP, 4446);
				
				// Send the disconnect request packet to the server
				socket.send(packet);
				
				// Stop all the client-side threads and socket
				ciss.end();
				cgsr.end();
				socket.close();
				
				// Reset the socket, serverIP and client-side threads after disconnecting
				socket = null;
				serverIP = null;
				ciss = null;
				cgsr = null;
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
