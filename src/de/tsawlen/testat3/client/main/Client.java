package de.tsawlen.testat3.client.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Client extends Thread {
	//In Client müssten die Kommandos gar nicht von der Tastatur kommen, sondern über dreifaches Senden
	//danach dreifaches Empfangen
	public int type;
	
	public static DatagramSocket client = null;
	public static BufferedReader clientIn = null;
	public static InetAddress dest = null;
	public static String name = null;
	
	public Client(int type) {
		this.type = type;
	}
	
	public static void init() {
		try {
			dest = InetAddress.getByName("localhost");
			client = new DatagramSocket();
		}
		catch (Exception e) {
			
		}
	}
	
	@Override
	public void run() {
		if(type == 0) {
			
			while(true) {
				try {
					
					clientIn = new BufferedReader(new InputStreamReader(System.in));
					String message = clientIn.readLine();
					if(message.equals(".")) {
						break;
					}
					if(message != null) {
						byte[] messageByte = message.getBytes();
						
						DatagramPacket datagramPacket = new DatagramPacket(messageByte, messageByte.length, dest, 5999);
						client.send(datagramPacket);
					}
									
				}catch(Exception e) {
					e.printStackTrace();
					
				}
			}
			try {
				if(client != null) {
					client.close();
				}
				if(clientIn != null) {
					clientIn.close();
				}
			}catch(Exception e) {
				
			}
			
			
		}
		else if(type == 1) {
			
			while(true) {
				try {
					byte[] byteBuffer = new byte[65536];
					DatagramPacket buffer = new DatagramPacket(byteBuffer, byteBuffer.length);
					client.receive(buffer);
					processDatagram(buffer);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public static void main(String[] args) {
		
		init();
		
		List<Client> threads = new ArrayList<>();
		
		for(int i = 0; i < 2; i++) {
			Client thread = new Client(i);
			threads.add(thread);
			threads.get(i).start();
		}
	}
	
	public void processDatagram(DatagramPacket msg) {
		
		byte[] msgBuffer = msg.getData();
		String message = new String(msgBuffer, 0, msg.getLength());
		System.out.println(message);
		
		
	}

}
