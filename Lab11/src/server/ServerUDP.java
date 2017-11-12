package server;


import ui.Chat;
import ui.Interface;

import java.net.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;


public class ServerUDP implements Runnable, Chat {
	private DatagramSocket socket;
	private InetAddress address;
	private Integer port;
	private String receivedData;
	private byte[] dataBuffer;
	private Integer clientPort;
	private String name = "unknown";
	private Interface ui;
	private boolean exit = false;

	public ServerUDP(int socketNumber, Interface ui) throws Exception {
		socket = new DatagramSocket(socketNumber);		
		address = null;
		port = socketNumber;
		clientPort = null;
		receivedData = null;		
		dataBuffer = new byte[1024];
		this.ui = ui;
		ui.setChat(this);
	}


	public void run() {

		try {
			DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);

			while (true) {
				if (exit) {
					closeSocket();
				}

				socket.receive(packet);

				address = packet.getAddress();
				clientPort = packet.getPort();

				receivedData = new String(packet.getData(), packet.getOffset(), packet.getLength());

				ui.writeText(receivedData);
				
			}


		} catch (Exception e) {
			if (socket != null) {
				socket.close();
			} 

			e.printStackTrace();
		}
	}


	public void setName(String name) {
		this.name = name;
	}



	public String getName() {
		return name;
	}


	public synchronized void  setExit(boolean b) {

	}


	public void send(String message) throws Exception {	
		if (address == null || clientPort == null) {
			ui.writeText(">Please await connection...");
			return;
		}

		message = name + ": " + message;
		byte[] sendData = message.getBytes();
		DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, clientPort);
		socket.send(packet);
	}


	public void closeSocket() {
		if (!socket.isClosed()) {
			socket.close();
			System.exit(0);
		}
	}


	public static void main(String[] args) {
		try {
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				Enumeration<InetAddress> f = e.nextElement().getInetAddresses();
				while (f.hasMoreElements()) {
					System.out.println(f.nextElement());
				}
			}
		} catch (Exception e){

		}

		if (args.length < 1) {
			System.out.println("Error: invalid arguments\nPlease specify port you'd like to use as first argument");
			System.exit(0);

		}


		Interface ui = new Interface();

		try {


			ServerUDP serverObject = new ServerUDP(Integer.parseInt(args[0]), ui);
			new Thread(serverObject).start();

			ui.writeText("Use \"@name %yourname%\" to define your username.\nType message and press enter to send it after someone connects\nUse @end to exit programm ");

			/*while (true) {
				String userInput = scanner.nextLine();

				if (userInput.equals("@end")) {
					
					serverObject.closeSocket();
					System.exit(0);
				} else if(userInput.startsWith("@name")) {
					serverObject.setName(userInput.substring(6, userInput.length()));
					continue;
				}

				serverObject.send(userInput);*/
		} catch (Exception e){
			e.printStackTrace();
		}

		
	}
}