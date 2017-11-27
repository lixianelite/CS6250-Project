package FrontEnd;

import Model.UserInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UIDesign extends JFrame
{
    private List<UserInfo> friendList;
    private List<UserInfo> blockList;
    private JTextField m_enter;
    private JTextArea m_display;
    private String userName;

    private Socket socket;
    private BufferedReader is = null;
    private PrintStream os = null;

    private ReceiveThread receiveThread = null;

    private class ReceiveThread extends Thread{
        @Override
        public void run() {
            try {
                String msg;
                msg = is.readLine();
                msg = is.readLine();
                msg = is.readLine();
                while (((msg = is.readLine()) != null) && (!mb_isEndSession(msg))) {
                    mb_displayAppend("Server: " + msg);
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
            exitServer();
        }
    }

    public void exitServer() {
        try {
            socket.close();
            receiveThread.join();
        } catch (Exception e) {
            System.err.println("error! " + e);
            e.printStackTrace();
        }
    }

    public UIDesign(List<UserInfo> friendList, List<UserInfo> blockList) {
        super("Chat Program");
        this.friendList = friendList;
        this.blockList = blockList;

        Container c = getContentPane();

        m_display = new JTextArea();
        c.add(new JScrollPane(m_display));

        DefaultListModel<String> l1 = new DefaultListModel<>();
        for (UserInfo object : this.friendList){
            l1.addElement(object.getUserName());
        }

        JList<String> fList = new JList<>(l1);
        fList.setBounds(100,100, 75,75);
        c.add(fList);

        m_enter = new JTextField(20);
        m_enter.setBounds(50,50, 150,120);

        m_enter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    String s = event.getActionCommand();
                    //s = "@" + userName + " " + s;
                    mb_displayAppend("@" + userName + ": " + s);
                    m_enter.setText("");

                    os.println(s);
                } catch(Exception e) {
                    System.err.println("error! " + e);
                    e.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Type sentences here:");

        panel.add(label);
        panel.add(m_enter);

        c.add(panel);

        DefaultListModel<String> l2 = new DefaultListModel<>();
        for (UserInfo object : this.blockList){
            l2.addElement(object.getUserName());
        }

        JList<String> bList = new JList<>(l2);
        bList.setBounds(100,100, 75,75);
        c.add(bList);
        c.setLayout(new GridLayout(2,2));

    }

    public void mb_displayAppend(String s) {
        m_display.append(s + "\n");
        m_display.setCaretPosition(m_display.getText().length());
        m_enter.requestFocusInWindow();
    }

    public boolean mb_isEndSession(String m)
    {
        if(m.equalsIgnoreCase("q")) {
            return(true);
        }
        if(m.equalsIgnoreCase("quit")) {
            return(true);
        }
        if(m.equalsIgnoreCase("exit")) {
            return(true);
        }
        if(m.equalsIgnoreCase("end")) {
            return(true);
        }
        return false;
    }

    private static List<UserInfo> getList(String response){
        String[] people = response.split("#");
        List<UserInfo> list = new ArrayList<>();
        for (int i = 0; i < people.length; i++){
            if (!people[i].equals("")){
                UserInfo friend = new UserInfo(people[i]);
                list.add(friend);
            }
        }
        return list;
    }

    public void mb_run(String host, int port, String UserInfo, String userName) {
        try {
            this.userName = userName;
            socket = new Socket(host, port);
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os = new PrintStream(socket.getOutputStream());
            os.println(UserInfo);
            m_enter.setEnabled(true);
            receiveThread = new ReceiveThread();
            receiveThread.start();
        }
        catch(Exception e) {
            System.err.println("error! " + e);
            e.printStackTrace();
            mb_displayAppend("error!");
        }
    }

    public static void main(String[] args){
        String ipAddress = args[0];
        String port = args[1];
        String userName = args[2];
        String friendList = args[3];
        String blockList = args[4];
        String userInfo = args[5];

        List<UserInfo> fList = getList(friendList);
        List<UserInfo> bList = getList(blockList);
        UIDesign app = new UIDesign(fList, bList);
        app.setSize(550,350);
        app.setVisible(true);
        app.mb_run(ipAddress, Integer.valueOf(port), userInfo, userName);

    }
}