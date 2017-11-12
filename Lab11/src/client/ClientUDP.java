package client;

import java.net.*;
import java.util.Scanner;
import ui.Interface;
import ui.Chat;

public class ClientUDP implements Runnable, Chat {
	private DatagramSocket socket;
	private InetAddress address;
	private Integer port;
	private String receivedData;
	private byte[] dataBuffer;
	private String name = "unknown";
	private Interface ui;
	private boolean exit = false;


	public ClientUDP(String address, int socketNumber, Interface ui) throws Exception {
		this.address = InetAddress.getByName(address);
		socket = new DatagramSocket();	
		port = socketNumber;
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


	public synchronized void  setExit(boolean b) {

	}


	public void send(String message) throws Exception {
		message = name + ": " + message;
		byte[] sendData = message.getBytes();
		DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, port);
		socket.send(packet);
	}



	public void closeSocket() {
		if (!socket.isClosed()) {
			socket.close();
			System.exit(0);
		}
	}


	public String getName() {
		return name;
	}


	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Error: invalid arguments\nPlease specify IP address and port you'd like to use as first and second argument");
			System.exit(0);
		}

		byte[] sendData = new byte[1024];

		Interface ui = new Interface();

		try {
			ClientUDP clientObject = new ClientUDP(args[0], Integer.parseInt(args[1]), ui);
			new Thread(clientObject).start();

			ui.writeText("e \"@name %yourname%\" to define your username.\nType message and press enter to send it\nUse @end to exit programm ");

			/*while (true) {Us
				Scanner scanner = new Scanner(System.in);
				String userInput = scanner.nextLine();


				if (userInput.equals("@end")) {
					clientObject.closeSocket();
					System.exit(0);
				} else if(userInput.startsWith("@name")) {
					clientObject.setName(userInput.substring(6, userInput.length()));
					continue;
				}

				clientObject.send(userInput);
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}