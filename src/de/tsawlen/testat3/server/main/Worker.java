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
	
	public Worker(DatagramSocket serverSocket, String path, MonitorVault monitorVault, QueueMonitor queue) {
		this.serverSocket = serverSocket;
		this.path = path;
		this.monitorVault = monitorVault;
		this.queue = queue;
		
	}
	
	@Override
	public void run() {
		
		while(true) {
			DatagramPacket message = queue.getJob();
			String response = "";
			String msg = processDatagram(message);
			MessageType msgType = decodeCommand(msg);
			
			if(msgType.equals(MessageType.READ)){
				try {
					this.sleep(5000);
				}catch(Exception e) {
					e.printStackTrace();
				}
				response = read(msg, path);
			}
			else if(msgType.equals(MessageType.WRITE)) {
				try {
					
					this.sleep(5000);
				}catch(Exception e) {
					e.printStackTrace();
				}
				response = write(msg, path);
			}else {
				response = "Error";
			}
			if(response != null) {
				byte[] responseByte = response.getBytes();
				DatagramPacket responsePacket = new DatagramPacket(responseByte, responseByte.length, message.getAddress(), message.getPort());
				try {
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
	
	public String read(String message, String path) {
		
		String command = message.substring(5);
		String commandSections[] = command.split(",");
		String toReturn = "";
		
		if(commandSections[0] != null) {
			String toRead = path + "/" + commandSections[0];
			
			try {
				if(commandSections[1] != null) {
					int lineNo = Integer.parseInt(commandSections[1].trim());
					monitorVault.getAccess(toRead.trim()).enterRead();
					System.out.println("Worker (Lesend) " + this + " fängt an zu arbeiten!");
					toReturn = reader.readLine(toRead, lineNo);
					System.out.println("Worker (Lesend) " + this + " hört auf zu arbeiten!");
					monitorVault.getAccess(toRead.trim()).leaveReader();
					return toReturn;
				}
				else {
					return "Invalid command, line Number missing!";
				}
			}catch (Exception e) {
				return "Internal Server Error!";
			}
		}else {
			return "Invalid command, filename missing!";
		}
	}
	
	public String write(String message, String path) {
		String command = message.substring(6);
		String commandSections[] = command.split(",");
		String toReturn = "";
		
		if(commandSections[0] != null) {
			String toRead = path + "/" + commandSections[0];
			
			try {
				if(commandSections[1] != null) {
					int lineNo = Integer.parseInt(commandSections[1].trim());
					if(commandSections[2] != null) {
						monitorVault.getAccess(toRead.trim()).enterWriter();
						System.out.println("Worker (Schreibend) " + this + " fängt an zu arbeiten!");
						toReturn = reader.writeLine(toRead.trim(), lineNo, commandSections[2].trim());
						System.out.println("Worker (Schreibend) " + this + " hört auf zu arbeiten!");
						monitorVault.getAccess(toRead.trim()).leaveWriter();
						return toReturn;
					}
					else {
						return "Invalid command, the Data is missing!";
					}
				}
				else {
					return "Invalid command, line Number missing!";
				}
				
			}catch (Exception e) {
				return "Internal Server Error!";
			}
		}else {
			return "Invalid command, filename missing!";
		}
	}
	
	
	public String processDatagram(DatagramPacket msg) {
		
		byte[] msgBuffer = msg.getData();
		//Wegen Umlauten besser msgBuffer.length
		String message = new String(msgBuffer, 0, msgBuffer.length);
		
		return message;
		
	}

}
