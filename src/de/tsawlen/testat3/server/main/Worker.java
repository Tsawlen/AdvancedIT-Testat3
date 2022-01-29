package de.tsawlen.testat3.server.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class Worker extends Thread {
	
	private Manipulator reader = new Manipulator();
	private DatagramPacket clientData = null;
	private DatagramSocket serverSocket;
	private String path;
	private QueueMonitor queue;
	private MonitorVault monitorVault;
	
	/**
	 * Constructor Method for the worker thread
	 * @param serverSocket
	 * @param path
	 * @param monitorVault
	 * @param queue
	 */
	public Worker(DatagramSocket serverSocket, String path, MonitorVault monitorVault, QueueMonitor queue) {
		this.serverSocket = serverSocket;
		this.path = path;
		this.monitorVault = monitorVault;
		this.queue = queue;
		
	}
	
	/**
	 * This method is responsible for running the worker thread
	 */
	@Override
	public void run() {
		//endless loop
		while(true) {
			//get a job from the queue
			DatagramPacket message = queue.getJob();
			String response = "";
			//get the command from the datagram as a String
			String msg = processDatagram(message);
			//get the type of the message
			MessageType msgType = decodeCommand(msg);
			
			//if the message Type is read
			if(msgType.equals(MessageType.READ)){
				try {
					//sleep command to mock parallel access
					this.sleep(5000);
				}catch(Exception e) {
					e.printStackTrace();
				}
				//get the response by calling the read method
				response = read(msg, path);
			}
			//if the message type is write
			else if(msgType.equals(MessageType.WRITE)) {
				try {
					//sleep command to mock parallel access
					this.sleep(5000);
				}catch(Exception e) {
					e.printStackTrace();
				}
				//get the response by calling the write method
				response = write(msg, path);
			}else {
				//respond error if the command is unknown
				response = "STATUS 404: Command not found! Command was " + msg.split(" ")[0];
			}
			//if there is a response
			if(response != null) {
				//get the bytes of the response
				byte[] responseByte = response.getBytes();
				//create a datagram for the response
				DatagramPacket responsePacket = new DatagramPacket(responseByte, responseByte.length, message.getAddress(), message.getPort());
				try {
					//send the reponse
					serverSocket.send(responsePacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * This method is responsible for decoding the type of the message.
	 *
	 * @param msg - the message the user send
	 * @return MessageType - the type of the message
	 */
	public MessageType decodeCommand(String msg) {
		
		String[] components = msg.split(" ");
		
		switch (components[0].toUpperCase()) {
		//the first word of the Message is save
		case "READ": {
			return MessageType.READ;
		}
		//the first word of the Message is get
		case "WRITE": {
			return MessageType.WRITE;
		}
		//the first word of the Message does not equal a command
		default:
			return MessageType.UNKNOWN;
		}
		
	}
	
	/**
	 * The read method of the worker
	 * @param message - the message
	 * @param path - the path to the files folder
	 * @return - response String
	 */
	public String read(String message, String path) {
		
		String command = message.substring(5);
		String commandSections[] = command.split(",");
		String toReturn = "";
		
		//look if a filename is given
		if(commandSections[0] != null) {
			//build the the path to the file
			String toRead = path + "/" + commandSections[0];
			
			try {
				//look if a line number is given
				if(commandSections.length > 0) {
					//get the line number from the string
					int lineNo = Integer.parseInt(commandSections[1].trim());
					//get the monitor for the given file and then enter the Read section of that
					monitorVault.getAccess(toRead.trim()).enterRead();
					System.out.println("Worker (Lesend) " + this + " fängt an zu arbeiten!");
					//call the reader to read the line
					toReturn = reader.readLine(toRead, lineNo);
					System.out.println("Worker (Lesend) " + this + " hört auf zu arbeiten!");
					//get the monitor for the given file and exit the leave section of it
					monitorVault.getAccess(toRead.trim()).leaveReader();
					return toReturn;
				} else {
					//return exception
					return "Invalid command, line Number missing!";
				}
			}catch (Exception e) {
				//return exception
				return "Internal Server Error!";
			}
		}else {
			//return exception
			return "Invalid command, filename missing!";
		}
	}
	
	/**
	 * The write command of the worker
	 * @param message - the message to process
	 * @param path - the path to the file root
	 * @return - the response as String
	 */
	public String write(String message, String path) {
		String command = message.substring(6);
		String commandSections[] = command.split(",");
		String toReturn = "";
		
		//look if a filename is available
		if(commandSections.length >= 1) {
			//build the path to the requested file
			String toRead = path + "/" + commandSections[0];
			
			try {
				//look if a line number is available
				if(commandSections.length >= 2) {
					//get the line number as an integer
					int lineNo = Integer.parseInt(commandSections[1].trim());
					//look if data is available
					if(commandSections.length >= 3) {
						//get the monitor for the given file and enter the write section
						monitorVault.getAccess(toRead.trim()).enterWriter();
						System.out.println("Worker (Schreibend) " + this + " fängt an zu arbeiten!");
						//contact the reader to write a new line
						toReturn = reader.writeLine(toRead.trim(), lineNo, commandSections[2].trim());
						System.out.println("Worker (Schreibend) " + this + " hört auf zu arbeiten!");
						//get the monitor for the given file and exit the leave section
						monitorVault.getAccess(toRead.trim()).leaveWriter();
						return toReturn;
					}
					else {
						//return exception
						return "Invalid command, the Data is missing!";
					}
				}
				else {
					//return exception
					return "Invalid command, line Number missing!";
				}
				
			}catch (Exception e) {
				//return exception
				return "Internal Server Error!";
			}
		}else {
			//return exception
			return "Invalid command, filename missing!";
		}
	}
	
	/**
	 * This method is responsible for processing the datagram and returning a message string
	 * @param msg - the message package
	 * @return - response String
	 */
	public String processDatagram(DatagramPacket msg) {
		
		byte[] msgBuffer = msg.getData();
		//Wegen Umlauten besser msgBuffer.length
		String message = new String(msgBuffer, 0, msgBuffer.length);
		
		return message;
		
	}

}
