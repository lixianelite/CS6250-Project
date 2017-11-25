package FrontEnd;

import Model.UserInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
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

        /*m_enter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    String s = event.getActionCommand();
                    m_output.writeObject(s);
                    m_output.flush();
                    mb_displayAppend("server: " + s);
                    m_enter.setText("");
                } catch(Exception e) {
                    System.err.println("error! " + e);
                    e.printStackTrace();
                }
            }
        });*/

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Type sentenses here:");
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

    public void mb_displayAppend(String s)
    {
        m_display.append(s + "\n");
        m_display.setCaretPosition(m_display.getText().length());
        m_enter.requestFocusInWindow();
    }

    public boolean mb_isEndSession(String m)
    {
        if(m.equalsIgnoreCase("q"))
        {
            return(true);
        }
        if(m.equalsIgnoreCase("quit"))
        {
            return(true);
        }
        if(m.equalsIgnoreCase("exit"))
        {
            return(true);
        }
        if(m.equalsIgnoreCase("end"))
        {
            return(true);
        }
        return false;
    }

    /*public void mb_run()
    {
        try
        {
            ServerSocket server = new ServerSocket(5000);
            String m;
            while(true)
            {
                m_clientNumber++;
                mb_displayAppend("waiting for connection [" + m_clientNumber + "]");
                Socket s = server.accept();
                mb_displayAppend("receives connection from client [" + m_clientNumber + "]");
                m_output = new ObjectOutputStream(s.getOutputStream());
                m_input = new ObjectInputStream(s.getInputStream());
                m_output.writeObject("connect!");
                m_output.flush();
                m_enter.setEnabled(true);
                do
                {
                    m = (String)m_input.readObject();
                    mb_displayAppend("Client: " + m);
                }
                while(!mb_isEndSession(m));
                m_output.writeObject("q");
                m_output.flush();
                m_enter.setEnabled(false);
                m_output.close();
                m_input.close();
                s.close();
                mb_displayAppend("connection from client [" + m_clientNumber + "] ends");
            }
        }
        catch(Exception e)
        {
            System.err.println("error! " + e);
            e.printStackTrace();
            mb_displayAppend("connection error!");
        }
    }*/

    /*public static void main(String [] args)
    {
        UIDesign app = new UIDesign();

        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setSize(350,150);
        app.setVisible(true);
        app.mb_run();
    }*/
}