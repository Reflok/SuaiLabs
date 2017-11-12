/*
Написать текстовый многопользовательский чат.
Пользователь управляет клиентом. На сервере пользователя нет. Сервер занимается пересылкой сообщений между клиентами.
По умолчанию сообщение посылается всем участникам чата.
Есть команда послать сообщение конкретному пользователю (@senduser Vasya).
Программа работает по протоколу TCP.

 */
package ui;


import client.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GraphicalInterface extends JPanel implements KeyListener {
    private static Logger logger = Logger.getLogger(GraphicalInterface.class.getName());

    private JTextPane chatBox;
    private JTextField messageBox;
    private transient Client.InputHandeler client;
    private JTable drawTable;
    private static final int WINDOW_HEIGHT = 300;
    private static final int WINDOW_WIDTH = 400;

    public GraphicalInterface(Client.InputHandeler client) {
        this.client = client;

        JFrame mainFrame = new JFrame("TCP-based chat");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setContentPane(this);

        mainFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        this.setLayout(new BorderLayout());

        chatBox = new JTextPane();
        messageBox = new JTextField();
        messageBox.setSize(WIDTH, 30);

        this.add(chatBox, BorderLayout.CENTER);
        this.add(messageBox, BorderLayout.PAGE_END);
        messageBox.addKeyListener(this);
        chatBox.setEditable(false);

        mainFrame.setVisible(true);
        mainFrame.requestFocus();

        JFrame drawFrame = new JFrame("Draw");
        drawFrame.setSize(90, 90);

        drawTable = new JTable(3, 3);
        drawTable.setEnabled(false);
        drawTable.setTableHeader(null);
        drawTable.setRowHeight(drawFrame.getHeight() / 3);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < drawTable.getColumnCount(); i++) {
            drawTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        drawFrame.getContentPane().add(drawTable);


        for (int i = 0; i < drawTable.getColumnCount(); i++) {
            TableColumn col = drawTable.getColumnModel().getColumn(i);
            col.setWidth(30);
        }

        drawFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        drawFrame.setVisible(true);
    }


    private void sendMessage() {
        String text = messageBox.getText();

        if (!text.equals("")) {
            client.send(text);
        }

        messageBox.setText("");
    }



    public void writeText(String text) {
        if (text.startsWith("@draw")) {
            String[] tokens = text.split(" ");

            if (tokens.length < 4) {
                return;
            }

            setCell(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), tokens[3].charAt(0));

        } else {
            chatBox.setText(chatBox.getText() + text + "\n");
        }
    }


    @Override
    public void keyTyped(KeyEvent keyEvent) {
        //unnecessary
    }


    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                sendMessage();
            } catch (Exception e) {
                String exceptionLiteral = "Exception";
                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent keyEvent) {
        //unnecessary
    }


    private void setCell(int row, int col, char symb) {
        drawTable.setValueAt(symb, row, col);
    }


}
