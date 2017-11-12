package ui;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Interface extends JPanel implements KeyListener {
    JFrame mainFrame;
    JTextPane chatBox;
    JTextField messageBox;
    JButton sendButton;
    Chat chat;

    public static final int HEIGHT = 300;
    public static final int WIDTH = 400;

    public Interface() {
        mainFrame = new JFrame("UDP-based chat");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setContentPane(this);

        mainFrame.setSize(WIDTH, HEIGHT);

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
    }


    private void sendMessage() throws Exception {
        if (!messageBox.getText().equals("")) {

            if (messageBox.getText().startsWith("@name")) {
                chat.setName(messageBox.getText().substring(6, messageBox.getText().length()));
                writeText(">Your name is now " + chat.getName());
            } else if (messageBox.getText().equals("@end")) {
                chat.setExit(true);
                System.exit(0);

            } else {
                chat.send(messageBox.getText());
                writeText(chat.getName() + ":" + messageBox.getText());
            }
        }

        messageBox.setText("");
    }


    public void setChat(Chat chat) {
        this.chat = chat;
    }


    public void writeText(String text) {
        chatBox.setText(chatBox.getText() + text + "\n");
    }


    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }


    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                sendMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }


}
