/*
Написать текстовый многопользовательский чат.
Пользователь управляет клиентом. На сервере пользователя нет. Сервер занимается пересылкой сообщений между клиентами.
По умолчанию сообщение посылается всем участникам чата.
Есть команда послать сообщение конкретному пользователю (@senduser Vasya).
Программа работает по протоколу TCP.

 */
package server;



import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static Logger logger = Logger.getLogger(Server.class.getName());
    private static String exceptionLiteral = "Exception";

    private ServerSocket socket;
    private int connectionsNumber = 0;
    private HashMap<String, Socket> names;
    private char[][] drawData;

    private Server(int port) {
        try {
            socket = new ServerSocket(port);

            names = new HashMap<>();

            drawData = new char[3][3];

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    drawData[i][j] = ' ';
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }
    }


    public void run() {
        while (true) {
            try {
                if (socket.isClosed()) {
                    break;
                }

                Socket tempSocket = socket.accept();
                PrintStream socketOutputWriter = new PrintStream(tempSocket.getOutputStream());

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        socketOutputWriter.println(String.format("@draw %d %d %c", i, j, drawData[i][j]));
                    }
                }

                names.put("unknown" + Integer.toString(connectionsNumber), tempSocket);

                new Thread(new ClientHandeler(tempSocket, connectionsNumber)).start();
                connectionsNumber++;

            } catch (IOException e) {
                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }
    }


    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }

        System.exit(0);
    }



    public class ClientHandeler implements Runnable {
        private Socket clientSocket;
        private String name;

        ClientHandeler(Socket sock, int num) {
            this.clientSocket = sock;
            this.name = "unknown" + Integer.toString(num);
            names.put(name, clientSocket);
        }


        private void removeSelf() throws IOException {
            clientSocket.close();
            names.remove(name);
        }


        @Override
        public void run() {
            try (BufferedReader socketInputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String recievedString;

                while (true) {
                    recievedString = socketInputReader.readLine();

                    if (recievedString ==  null) {
                        removeSelf();
                        break;
                    }

                    handleInput(recievedString);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }


        private void handleInput(String recievedString) throws IOException {
            if (recievedString.startsWith("@")) {
                command(recievedString);
            } else {

                for (Map.Entry<String, Socket> entry : names.entrySet()) {
                    send(entry.getValue(), name + ":" + recievedString);
                }
            }
        }


        private void command(String recievedString) throws IOException {
            if (recievedString.equals("@stop")) {
                removeSelf();
            } else if (!recievedString.contains(" ")) {
                send(clientSocket, ">>>Invalid command");
            } else if (recievedString.startsWith("@draw")) {
                String[] tokens = recievedString.split(" ");

                if (tokens.length != 4) {
                    send(clientSocket, ">>>Invalid command");
                } else {
                    draw(recievedString);
                }
            } else if (recievedString.startsWith("@name")) {
                String newName = recievedString.substring(6, recievedString.length());
                if (names.containsKey(newName)) {
                    send(clientSocket, ">>>Your desired name is taken");
                } else {

                    names.remove(name);
                    names.put(newName, clientSocket);
                    name = newName;
                    send(clientSocket, ">>>Your name is now " + name);
                }
            } else {

                int firstSpace = recievedString.indexOf(' ');
                String targetName = recievedString.substring(1, firstSpace);

                if (!names.containsKey(targetName)) {
                    send(clientSocket, ">>>No user with such name found");
                } else {
                    send(names.get(targetName), "[private]" + name + ":" + recievedString.substring(firstSpace + 1,
                            recievedString.length()));
                    send(clientSocket, String.format("[to %s] : %s", targetName, recievedString.substring(firstSpace + 1,
                            recievedString.length())));
                }
            }
        }


        private void draw(String recievedString) throws IOException {
            String[] tokens = recievedString.split(" ");
            int row = Integer.parseInt(tokens[1]);
            int col = Integer.parseInt(tokens[2]);
            String symb = tokens[3];

            if (symb.length() > 1) {
                send(clientSocket, ">>>Can only display one symbol per cell");
                return;
            } else if (row > 3 || col > 3) {
                send(clientSocket, ">>>Index out of bounds");
            }

            drawData[row][col] = symb.charAt(0);

            for (Map.Entry<String, Socket> entry : names.entrySet()) {
                send(entry.getValue(), String.format("@draw %d %d %c", row, col, drawData[row][col]));
            }
        }


        private void send(Socket socket, String string) throws IOException {
            if (!socket.isClosed()) {
                PrintStream socketOutputWriter = new PrintStream(socket.getOutputStream());

                socketOutputWriter.println(string);
            }
        }
    }



    public static void main(String[] args) {
        Server serverObj = new Server(5000);
        new Thread(serverObj).start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("@stop")) {
                serverObj.close();
                break;
            }
        }
    }
}