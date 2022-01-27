package de.tsawlen.testat3.server.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;


public class ServerDispatcher {
	
	//The port
	public static final int PORT = 5999;

	/**
	 * The main method of the server
	 * @param args
	 */
	public static void main(String[] args) {
		//Get the file path
		String path = System.getProperty("user.home") + "/Desktop/Fileserver";
		//Create a map that identifies monitors by their filename
		MonitorVault monitorVault = new MonitorVault();
		//Create a Job queue
		QueueMonitor queue = new QueueMonitor();
		
		
		
		try {
			//Instantiate the socker
			DatagramSocket serverSocket = new DatagramSocket(PORT);
			System.out.println("Der Server ist gestartet!");
			
			//Create and start all Workers
			for(int i = 0; i <= 5; i++) {
				Worker worker = new Worker(serverSocket, path, monitorVault, queue);
				worker.start();
			}
			
			//Endless loop
			while(true) {
				//create a new recieve buffer
				byte[] byteBuffer = new byte[65536];
				//create the buffer on the array
				DatagramPacket buffer = new DatagramPacket(byteBuffer, byteBuffer.length);
				//let the socket wait or recieve 
				serverSocket.receive(buffer);
				System.out.println("Message recieved!");
				//add the queue to the new Job queue
				queue.addJob(buffer);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
