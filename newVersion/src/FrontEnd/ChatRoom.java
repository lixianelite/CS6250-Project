package FrontEnd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by allam on 12/10/17.
 */

public class ChatRoom extends JFrame{
    private JTextArea roomDisplay;
    private JTextField roomEnter;
    private String roomName;
    private String userName;

    private Thread receiveThread;

    private Socket socket;
    private BufferedReader is = null;
    private PrintStream os = null;

    private String host = "localhost";
    private int portNum = 2002;

    public ChatRoom(String name, String userName) {
        super(userName + " in chat room " + name);

        this.roomName = name;
        this.userName = userName;
        Container c = getContentPane();

        roomDisplay = new JTextArea();
        roomDisplay.setEditable(false);
        roomDisplay.setBounds(25, 0, 300, 220);

        roomEnter = new JTextField(20);
        roomEnter.setBounds(25,225, 300,50);

        roomEnter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    // changed here
                    // send message to target user.
                    String s = event.getActionCommand();
                    roomEnter.setText("");
                    roomEnter.requestFocusInWindow();
                    os.println(userName + ": " + s);
                } catch(Exception e) {
                    System.err.println("error! " + e);
                    e.printStackTrace();
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("leave chat room now");
                try {
                    os.println("/quit");
                    receiveThread.interrupt();
                    receiveThread.join();
                    os.close();
                    is.close();
                    socket.close();
                }
                catch (Exception error) {
                    System.out.println("ChatRoom.windowClosing() " + error);
                }

            }
        });

        JPanel panel = new JPanel();
        panel.add(roomEnter);
        panel.add(roomDisplay);

        panel.setLayout(null);
        c.add(panel);

        roomEnter.requestFocusInWindow();
        setSize(350,300);

        roomDisplay.append("connect to chat room: " + roomName + "\n");

        try {
            socket = new Socket(host, portNum);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintStream(socket.getOutputStream());
            os.println(userName);
            os.println(roomName);

            receiveThread = new Thread () {
                @Override
                public void run() {
                    try {
                        String msg;
                        while ((!isInterrupted()) && ((msg = is.readLine()) != null)) {
                            roomDisplay.append(msg + "\n");
                            roomDisplay.setCaretPosition(roomDisplay.getText().length());
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                    System.out.println("receive thread stopped");
                }
            };

            receiveThread.start();
        }
        catch (Exception e) {
            System.out.println("ChatRoom.settingTCPConnection: " + e);
        }
    }
}
