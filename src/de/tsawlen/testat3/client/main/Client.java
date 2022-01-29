package de.tsawlen.testat3.client.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Client extends Thread {
	public int type;
	
	public static DatagramSocket client = null;
	public static BufferedReader clientIn = null;
	public static InetAddress dest = null;
	public static String name = null;
	
	/**
	 * Constructor for manual client
	 * @param type
	 */
	public Client(int type) {
		this.type = type;
	}
	
	/**
	 * This method is responsible for initiating the client
	 */
	public static void init() {
		try {
			//set the destination
			dest = InetAddress.getByName("localhost");
			//create a DatagramSocket
			client = new DatagramSocket();
		}
		catch (Exception e) {
			
		}
	}
	
	/**
	 * This method is responsible for getting the working mode of the client
	 * @return
	 */
	public static int getWorkingMode() {
		BufferedReader reader = null;
		try {
			System.out.println("Verfügbare Modi: 1 - Manuell; 2 - Automatisch");
			System.out.print("Bitte wählen sie einen Modus > ");
			reader = new BufferedReader(new InputStreamReader(System.in));
			String modeString = reader.readLine();
			int mode = Integer.parseInt(modeString.trim());
			return mode;
			
		}catch(IOException iOE) {
			iOE.printStackTrace();
			
		}finally {
			/*try {
				reader.close();
			}catch(IOException e) {
				e.printStackTrace();
			}*/
		}
		return -1;
	}
	
	/**
	 * This method is responsible for testing parallel reading in the same file
	 */
	public void automaticTestParallelReading() {
		System.out.println("====================================================");
		System.out.println("Starte parallelen Lese-Test");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,2");
		commands.add("READ secondTest.txt,3");
		commands.add("READ secondTest.txt,4");
		commands.add("READ secondTest.txt,5");
		
		sendTest(commands);	
	}
	
	/**
	 * This method is responsible for testing parallel writing to the same file
	 */
	public void automaticTestSequentiellWritingInSameDocument() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum schreiben ins gleiche Dokument");
		List<String> commands = new ArrayList();
		commands.add("WRITE secondTest.txt,3,Neue Zeile 1");
		commands.add("WRITE secondTest.txt,5,Neue Zeile in Zeile 5");
		sendTest(commands);
		
	}
	
	/**
	 * This method is responsible for testing parallel writing to different files
	 */
	public void automaticTestParallelWritingInDifferentDocument() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum schreiben in unterschiedliche Dokumente");
		List<String> commands = new ArrayList();
		commands.add("WRITE secondTest.txt,3,Neue Zeile 1");
		commands.add("WRITE thirdTest.txt,5,Neue Zeile in Zeile 5");
		sendTest(commands);
	}
	
	/**
	 * This method is responsible for testing if the writer-priority is active in the server
	 */
	public void automaticTestWriterPriority() {
		System.out.println("====================================================");
		System.out.println("Starte Schreiber-Prioritäts-Test");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,2");
		commands.add("WRITE secondTest.txt,3,Neue Zeile 1");
		commands.add("READ secondTest.txt,3");
		commands.add("READ secondTest.txt,4");
		commands.add("WRITE secondTest.txt,5,Neue Zeile in Zeile 5");
		commands.add("READ secondTest.txt,5");
		sendTest(commands);
	}
	
	/**
	 * This method is responsible for testing parallel reading from different files
	 */
	public void automaticTestReadFromDifferentFiles() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum lesen aus verschiedenen Datein");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,2");
		commands.add("READ thirdTest.txt,3");
		commands.add("READ secondTest.txt,5");
		sendTest(commands);
	}
	
	/**
	 * This method is responsible for testing parallel reading and writing to different files
	 */
	public void automaticReadAndWriteDifferentFiles() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum parallelen Lesen aus einer Datei und schreiben in eine andere!");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,2");
		commands.add("WRITE thirdTest.txt,3,Paralleler Zugriff");
		commands.add("READ secondTest.txt,5");
		sendTest(commands);	
	}
	
	/**
	 * This method is responsible for testing the activity of the server when more commands are send than there are worker in the pool
	 */
	public void automaticTestMoreRequestsThanWorker() {
		System.out.println("====================================================");
		System.out.println("Starte Test mit mehr Anfragen als aktive Worker");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,2");
		commands.add("READ secondTest.txt,3");
		commands.add("READ secondTest.txt,4");
		commands.add("READ secondTest.txt,5");
		commands.add("READ secondTest.txt,6");
		commands.add("READ secondTest.txt,7");
		commands.add("READ secondTest.txt,8");
		commands.add("READ secondTest.txt,9");
		commands.add("READ secondTest.txt,10");
		commands.add("READ secondTest.txt,11");
		commands.add("READ secondTest.txt,12");
		commands.add("READ secondTest.txt,13");
		sendTest(commands);	
	}
	
	/**
	 * This method is responsible for testing the server when a line is requested which is outside of the documents bounds
	 */
	public void automaticTestReadOutOfBounds() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum lesen einer nicht vorhandenen Zeile!");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,50");
		sendTest(commands);	
	}
	
	/**
	 * This method is responsible for sending all tests and receiving their answers
	 * @param commands
	 */
	public void sendTest(List<String> commands) {
		try {
			//create and send datagrams for all requests
			for(String command:commands) {
				System.out.println("Sende: " + command);
				byte[] commandBytes = command.getBytes();
				DatagramPacket datagramPacket = new DatagramPacket(commandBytes, commandBytes.length, dest, 5999);
				client.send(datagramPacket);
			}
			//receive all answers to the commands
			for(String command:commands) {
				byte[] byteBuffer = new byte[65536];
				DatagramPacket buffer = new DatagramPacket(byteBuffer, byteBuffer.length);
				client.receive(buffer);
				processDatagram(buffer);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is responsible for the manual client
	 */
	@Override
	public void run() {
		//thread for writing commands to the server
		if(type == 0) {
			
			while(true) {
				try {
					//create a input stream for the user
					clientIn = new BufferedReader(new InputStreamReader(System.in));
					//read a command
					String message = clientIn.readLine();
					if(message.equals(".")) {
						break;
					}
					if(message != null) {
						//create a byte array from the command
						byte[] messageByte = message.getBytes();
						//create a datagram packet to send
						DatagramPacket datagramPacket = new DatagramPacket(messageByte, messageByte.length, dest, 5999);
						//send the command to the server
						client.send(datagramPacket);
					}
									
				}catch(Exception e) {
					e.printStackTrace();
					
				}
			}
			
			try {
				if(client != null) {
					//close the client if open
					client.close();
				}
				if(clientIn != null) {
					//close the input Stream if open
					clientIn.close();
				}
			}catch(Exception e) {
				
			}
			
			
		}
		//Thread for receiving messages 
		else if(type == 1) {
			
			while(true) {
				try {
					//create a byte buffer for incoming messages
					byte[] byteBuffer = new byte[65536];
					//create a buffer Datagrampacket
					DatagramPacket buffer = new DatagramPacket(byteBuffer, byteBuffer.length);
					//Receive a packet
					client.receive(buffer);
					//process the packet
					processDatagram(buffer);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	/**
	 * The main method of the client
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Initialize the client
		init();
		
		//get the running mode
		int mode = getWorkingMode();
		
		switch(mode) {
		//if running mode is manual
		case 1:
			//create send and receive thread
			List<Client> threads = new ArrayList<>();
			
			//create and start them
			for(int i = 0; i < 2; i++) {
				Client thread = new Client(i);
				threads.add(thread);
				threads.get(i).start();
			}
			break;
		//if the running mode is automatic start all tests
		case 2:
			Client autoTest = new Client(3);
			//autoTest.automaticTestParallelReading();
			//autoTest.automaticTestSequentiellWritingInSameDocument();
			//autoTest.automaticTestParallelWritingInDifferentDocument();
			autoTest.automaticTestWriterPriority();
			//autoTest.automaticReadAndWriteDifferentFiles();
			//autoTest.automaticTestMoreRequestsThanWorker();
			//autoTest.automaticTestReadFromDifferentFiles();
			//autoTest.automaticTestReadOutOfBounds();
			System.out.println("====================================================");
			System.out.println("Alle Tests erfolgreich abgeschlossen!");
			break;
		}
		
		
	}
	
	/**
	 * Method to analyze the content of a packet
	 * @param msg
	 */
	public void processDatagram(DatagramPacket msg) {
		//read the data from the package
		byte[] msgBuffer = msg.getData();
		//create a String from the data
		String message = new String(msgBuffer, 0, msg.getLength());
		//print the Data
		System.out.println("Empfangen: " + message);
		
		
	}

}
