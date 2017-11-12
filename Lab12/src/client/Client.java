/*
Написать текстовый многопользовательский чат.
Пользователь управляет клиентом. На сервере пользователя нет. Сервер занимается пересылкой сообщений между клиентами.
По умолчанию сообщение посылается всем участникам чата.
Есть команда послать сообщение конкретному пользователю (@senduser Vasya).
Программа работает по протоколу TCP.

 */
package client;

import ui.GraphicalInterface;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client implements Runnable {
    private Socket socket;
    private GraphicalInterface ui;
    private static Logger logger = Logger.getLogger(Client.class.getName());
    private static String exceptionLiteral = "Exception";

    private Client(String address, int port) {

        try {
            this.socket = new Socket(InetAddress.getByName(address), port);
        } catch (Exception e) {

            logger.log(Level.SEVERE, exceptionLiteral, e);
        }

    }


    @Override
    public void run() {
        InputHandeler client = new InputHandeler(socket);

        this.ui = new GraphicalInterface(client);
        ui.writeText("Use \"@name %yourname%\" to define your username.\nType message and press enter to send it\n" +
                    "Use @stop to exit programm");

        new Thread(client).start();

    }


    public class InputHandeler implements Runnable {
        private Socket socket;
        private PrintStream writer;

        InputHandeler(Socket socket) {

            this.socket = socket;

            try {
                writer = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {

                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }


        @Override
        public void run() {
            try (BufferedReader socketInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                while (true) {
                    String recievedString = receiveString(socketInputReader);


                    if (recievedString == null) {
                        close();
                        return;
                    }

                    ui.writeText(recievedString);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }


        public void send(String text) {
            writer.println(text);
        }


        void close() throws IOException {
            if (!socket.isClosed()) {
                socket.close();
            }

            System.exit(0);
        }


        private String receiveString(BufferedReader buff) throws IOException {
            String recievedString = null;

            try {
                recievedString = buff.readLine();
            } catch (SocketException e) {
                close();
                logger.log(Level.SEVERE, exceptionLiteral, e);
                System.exit(0);
            }

            return recievedString;
        }
    }





    public static void main(String[] args) {
        new Thread(new Client("localhost", 5000)).start();
    }
}
