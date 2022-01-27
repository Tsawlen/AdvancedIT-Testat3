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
			try {
				reader.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
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
	
	public void automaticTestSequentiellWritingInSameDocument() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum schreiben ins gleiche Dokument");
		List<String> commands = new ArrayList();
		commands.add("WRITE secondTest.txt,3,Neue Zeile 1");
		commands.add("WRITE secondTest.txt,5,Neue Zeile in Zeile 5");
		sendTest(commands);
		
	}
	
	public void automaticTestParallelWritingInDifferentDocument() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum schreiben in unterschiedliche Dokumente");
		List<String> commands = new ArrayList();
		commands.add("WRITE secondTest.txt,3,Neue Zeile 1");
		commands.add("WRITE thirdTest.txt,5,Neue Zeile in Zeile 5");
		sendTest(commands);
	}
	
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
	
	public void automaticTestReadFromDifferentFiles() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum lesen aus verschiedenen Datein");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,2");
		commands.add("READ thirdTest.txt,3");
		commands.add("READ secondTest.txt,5");
		sendTest(commands);
	}
	
	public void automaticReadAndWriteDifferentFiles() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum parallelen Lesen aus einer Datei und schreiben in eine andere!");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,2");
		commands.add("WRITE thirdTest.txt,3,Paralleler Zugriff");
		commands.add("READ secondTest.txt,5");
		sendTest(commands);	
	}
	
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
	
	public void automaticTestReadOutOfBounds() {
		System.out.println("====================================================");
		System.out.println("Starte Test zum lesen einer nicht vorhandenen Zeile!");
		List<String> commands = new ArrayList();
		commands.add("READ secondTest.txt,50");
		sendTest(commands);	
	}
	
	public void sendTest(List<String> commands) {
		try {
			for(String command:commands) {
				System.out.println("Sende: " + command);
				byte[] commandBytes = command.getBytes();
				DatagramPacket datagramPacket = new DatagramPacket(commandBytes, commandBytes.length, dest, 5999);
				client.send(datagramPacket);
			}
			
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
		
		int mode = getWorkingMode();
		
		switch(mode) {
		case 1:
			List<Client> threads = new ArrayList<>();
			
			for(int i = 0; i < 2; i++) {
				Client thread = new Client(i);
				threads.add(thread);
				threads.get(i).start();
			}
			break;
		case 2:
			Client autoTest = new Client(3);
			autoTest.automaticTestParallelReading();
			autoTest.automaticTestSequentiellWritingInSameDocument();
			autoTest.automaticTestParallelWritingInDifferentDocument();
			autoTest.automaticTestWriterPriority();
			autoTest.automaticReadAndWriteDifferentFiles();
			autoTest.automaticTestMoreRequestsThanWorker();
			autoTest.automaticTestReadFromDifferentFiles();
			autoTest.automaticTestReadOutOfBounds();
			System.out.println("====================================================");
			System.out.println("Alle Tests erfolgreich abgeschlossen!");
			break;
		}
		
		
	}
	
	public void processDatagram(DatagramPacket msg) {
		
		byte[] msgBuffer = msg.getData();
		String message = new String(msgBuffer, 0, msg.getLength());
		System.out.println("Empfangen: " + message);
		
		
	}

}
