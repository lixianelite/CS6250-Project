package FrontEnd;

import Model.UserInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class UIDesign extends JFrame
{
    private List<UserInfo> friendList;
    private List<UserInfo> blockList;
    private JTextField m_enter;
    private JTextArea m_display;

    private Socket socket;
    private BufferedReader is = null;
    private PrintStream os = null;

    private class ReceiveThread extends Thread{
        @Override
        public void run() {
            try {
                String msg;
                System.out.println("run start");
                // receive msg from server.
                while ((msg = is.readLine()) != null) {
                    mb_displayAppend("Server: " + msg);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public UIDesign(List<UserInfo> friendList, List<UserInfo> blockList, Socket socket, BufferedReader is, PrintStream os) {
        super("Chat Program");

        this.socket = socket;
        this.is = is;
        this.os = os;

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
                    mb_displayAppend("client: " + s);
                    m_enter.setText("");
                    os.println(s);
                    String res = is.readLine();
                    mb_displayAppend(res);
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
        ReceiveThread trd = new ReceiveThread();
        trd.start();
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

    public void client_run(Socket s) {

        try {
            mb_displayAppend("try to connect");

            String m;
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            m_enter.setEnabled(true);
            do {
                m = is.readLine();
                mb_displayAppend("Server: " + m);
            }
            while(!mb_isEndSession(m));
            s.close();
            System.exit(0);
        }
        catch(Exception e) {
            System.err.println("error! " + e);
            e.printStackTrace();
            mb_displayAppend("error!");
        }
    }

    public static void main(String[] args){
        String friends = args[0];
        String blocks = args[1];
        String userName = args[2];
        String ipAddress = args[3];
        String port = args[4];

    }
}