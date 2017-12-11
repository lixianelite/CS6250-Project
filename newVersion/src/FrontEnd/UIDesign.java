package FrontEnd;

import BackEnd.Server;
import Model.UserInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UIDesign extends JFrame {
    private List<UserInfo> friendList;
    private List<UserInfo> blockList;
    DefaultListModel<String> l1;
    DefaultListModel<String> l2;
    JList<String> fList;
    JList<String> bList;

    Container container;
    // Chat contents of different friends in friend list
    private List<StringBuilder> chats = new ArrayList<StringBuilder>();
    private JTextField m_enter;
    private JTextArea m_display;
    private String userName;

    private int selectedIndex = -1;

    private String host = "localhost";

    private Socket socket;
    private BufferedReader is = null;
    private PrintStream os = null;

    private ReceiveThread receiveThread = null;

    private class ReceiveThread extends Thread{
        @Override
        public void run() {
            try {
                String msg;
                while ((!isInterrupted()) && ((msg = is.readLine()) != null) && (!mb_isEndSession(msg))) {
                    synchronized (this) {
                        parseMessage(msg);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
            System.out.println("receive thread stopped");
        }

        private void parseMessage(String msg) {
            msg = msg.replaceFirst("@" ,"");
            System.out.println("msg: " + msg);
            String name = msg.split(":", 2)[0];
            String response = msg.split(":", 2)[1];
            if (name.equals("admin")){
                System.out.println("admin response: " + response);
                if (response.equals("SUCCESS")){
                    createDialog(response);
                    System.out.println("successful operation");
                }
                else {
                    String[] words = response.split(" ");
                    System.out.println(words[0] + " " + words[1] + " " + words[2]);
                    if (words[1].equals("FRIEND")) {
                        if (words[0].equals("add")) {
                            l1.addElement(words[2]);
                            friendList.add(new UserInfo(words[2]));
                            StringBuilder sb = new StringBuilder();
                            sb.append("chat with " + words[2] + ":\n");
                            chats.add(sb);
                        }
                        else if (words[0].equals("remove")) {
                            int index = -1;
                            for (int i = 0; i < friendList.size(); ++i) {
                                if (friendList.get(i).getUserName().equals(words[2])) {
                                    index = i;
                                    break;
                                }
                            }
                            System.out.println("index: " + index);
                            if (index != -1) {
                                l1.remove(index);
                                friendList.remove(index);
                                chats.remove(index);
                                selectedIndex = -1;
                                m_display.setText("");
                            }
                        }
                    }
                    else if (words[1].equals("BLOCK")) {
                        if (words[0].equals("add")) {
                            l2.addElement(words[2]);
                            blockList.add(new UserInfo(words[2]));
                        }
                        else if (words[0].equals("remove")) {
                            int index = -1;
                            for (int i = 0; i < blockList.size(); ++i) {
                                if (blockList.get(i).getUserName().equals(words[2])) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1) {
                                blockList.remove(index);
                                l2.remove(index);
                            }
                        }
                    }
                }
            }else if (name.equals("extra")){
                createDialog(response);
            } else{
                for (int i = 0; i < friendList.size(); ++i) {
                    if (name.equals(friendList.get(i).getUserName())) {
                        if (i == selectedIndex) {
                            mb_displayAppend(msg);
                        }
                        else {
                            chats.get(i).append(msg+"\n");
                        }
                    }
                }
            }

        }
    }

    // Friend selection list listener, to change current window and target receiver.
    private class FriendListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList<String> fList = (JList<String>)e.getSource();
            selectedIndex = fList.getSelectedIndex();
            if (selectedIndex != -1) {
                String st = chats.get(selectedIndex).toString();
                m_display.setText(st);
                m_display.setCaretPosition(m_display.getText().length());
            }
            else {
                m_display.setText("");
                m_display.setCaretPosition(m_display.getText().length());
            }
        }
    }

    private class BlockListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            //remove people from this list;
        }
    }

    public void exitServer() {
        try {
            receiveThread.interrupt();
            os.println("/quit");
            Thread.sleep(100);
            os.close();
            is.close();
            socket.close();
            // remove current user from login server
            socket = new Socket(this.host, Server.loginPort);
            os = new PrintStream(socket.getOutputStream());;
            os.println("Logout " + this.userName);
            Thread.sleep(100);
            os.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("error! " + e);
            e.printStackTrace();
        }
    }

    public UIDesign(List<UserInfo> friendList, List<UserInfo> blockList, String UserName) {
        super(UserName);
        this.friendList = friendList;
        this.blockList = blockList;

        container = getContentPane();

        m_display = new JTextArea();
        container.add(new JScrollPane(m_display));

        l1 = new DefaultListModel<>();
        for (UserInfo object : this.friendList){
            l1.addElement(object.getUserName());
            StringBuilder sb = new StringBuilder();
            sb.append("chat with " + object.getUserName() + ":\n");
            chats.add(sb);
        }

        fList = new JList<>(l1);
        fList.setBounds(100,100, 75,75);
        container.add(fList);

        fList.addListSelectionListener(new FriendListSelectionListener());

        m_enter = new JTextField(20);
        m_enter.setBounds(50,50, 150,120);

        m_enter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    String s = event.getActionCommand();
                    if (selectedIndex != -1) {
                        os.println("message @" + friendList.get(selectedIndex).getUserName() + ":" + s);
                        mb_displayAppend(userName + ": " + s);
                    }
                    m_enter.setText("");
                } catch(Exception e) {
                    System.err.println("error! " + e);
                    e.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        //JLabel label = new JLabel("Type sentences here:");
        JButton chooseRoom=new JButton("Choose a chat room");
        
        
        panel.setLayout(new GridLayout(3, 1));

        panel.add(chooseRoom);
        panel.add(m_enter);

        JPanel spanel = new JPanel();
        spanel.setLayout(new GridLayout(1, 2));
        JButton addFriend = new JButton("Add Friend List");
        JButton addBlock = new JButton("Add Block List");
        
        addFriend.setPreferredSize(new Dimension(20, 20));
        addBlock.setPreferredSize(new Dimension(20, 20));

        chooseRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog mydialog;
                mydialog= new JDialog();
                mydialog.setSize(new Dimension(400,100));
                mydialog.setTitle("Choose chatroom");

                JTextField tx1 = new JTextField();
                tx1.setBounds(10, 50, 100, 20);

                //JLabel label1 = new JLabel("Chatroom number: ");
                //label1.setBounds(20, 20, 150, 20);

                //JLabel indicate = new JLabel("indicate");
                //indicate.setBounds(20, 40, 150, 20);

                JButton Add = new JButton("Join");
                Add.setBounds(150, 50, 100, 20);

                JButton Cancel = new JButton("Cancel");
                Cancel.setBounds(275, 50, 100, 20);

                Add.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String name = tx1.getText();
                        System.out.println("name: " + name);
                        tx1.setText("");
                        tx1.requestFocus();
                        mydialog.setVisible(false);

                        ChatRoom chatRoom = new ChatRoom(name, userName);
                        chatRoom.setVisible(true);
                    }
                });

                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mydialog.setVisible(false);
                    }
                });

                JPanel pan = new JPanel();
                pan.setLayout(null);
                //pan.add(label1);
                //pan.add(indicate);

                pan.add(tx1);
                pan.add(Add);
                pan.add(Cancel);
                mydialog.add(pan);
                mydialog.setLocationRelativeTo(null);
                mydialog.setVisible(true);
            }

        });


        addFriend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog;
                dialog= new JDialog();
                dialog.setSize(new Dimension(400,100));
                dialog.setTitle("Add Friends");
                JTextField tx1 = new JTextField();
                tx1.setBounds(130, 20, 250, 20);

                JLabel label1 = new JLabel("Input Username: ");
                label1.setBounds(20, 20, 150, 20);

                JButton Add = new JButton("Add");
                Add.setBounds(150, 50, 100, 20);

                JButton Cancel = new JButton("Cancel");
                Cancel.setBounds(275, 50, 100, 20);

                Add.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String name = tx1.getText();
                        System.out.println("name: " + name);
                        os.println("operation FRIEND " + name);
                        dialog.setVisible(false);

                    }
                });

                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.setVisible(false);
                    }
                });

                JPanel pan = new JPanel();
                pan.setLayout(null);
                pan.add(label1);
                pan.add(tx1);
                pan.add(Add);
                pan.add(Cancel);
                dialog.add(pan);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });

        addBlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog;
                dialog= new JDialog();
                dialog.setSize(new Dimension(400,100));
                dialog.setTitle("Add Block");
                JTextField tx1 = new JTextField();
                tx1.setBounds(130, 20, 250, 20);

                JLabel label1 = new JLabel("Input Username: ");
                label1.setBounds(20, 20, 150, 20);

                JButton Add = new JButton("Add");
                Add.setBounds(150, 50, 100, 20);

                JButton Cancel = new JButton("Cancel");
                Cancel.setBounds(275, 50, 100, 20);

                Add.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String name = tx1.getText();
                        System.out.println("name: " + name);
                        os.println("operation BLOCK " + name);
                        tx1.setText("");
                        tx1.requestFocus();
                    }
                });

                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.setVisible(false);
                    }
                });

                JPanel pan = new JPanel();
                pan.setLayout(null);
                pan.add(label1);
                pan.add(tx1);
                pan.add(Add);
                pan.add(Cancel);
                dialog.add(pan);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });

        spanel.add(addFriend);
        spanel.add(addBlock);

        panel.add(spanel);

        container.add(panel);

        l2 = new DefaultListModel<>();
        for (UserInfo object : this.blockList){
            l2.addElement(object.getUserName());
        }

        bList = new JList<>(l2);
        bList.setBounds(100,100, 75,75);
        container.add(bList);

        bList.addListSelectionListener(new BlockListSelectionListener());

        container.setLayout(new GridLayout(2,2));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("close window now");
                exitServer();
                System.exit(0);
            }
        });
    }

    private void createDialog(String response){
        JDialog indicate = new JDialog();
        indicate.setSize(new Dimension(600,100));
        indicate.setTitle("Message");
        JLabel label1 = new JLabel(response);
        label1.setBounds(20, 20, 500, 20);
        JPanel pan = new JPanel();
        pan.setLayout(null);
        pan.add(label1);
        indicate.add(pan);
        indicate.setLocationRelativeTo(null);
        indicate.setVisible(true);
    }

    public void mb_displayAppend(String s) {
        // update current chat window contents
        if (selectedIndex != -1) {
            chats.get(selectedIndex).append(s + "\n");
        }
        m_display.append(s + "\n");
        m_display.setCaretPosition(m_display.getText().length());
        m_enter.requestFocusInWindow();
    }

    public boolean mb_isEndSession(String m) {
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
            os.println(userName);
            m_enter.setEnabled(true);
            receiveThread = new ReceiveThread();
            receiveThread.start();
        } catch(Exception e) {
            System.err.println("error! " + e);
            e.printStackTrace();
            mb_displayAppend("error!");
        }
    }

    public static void main(String[] args){
        String ipAddress = args[0];
        String port = args[1];
        String userName = args[2];
        String friendListInfo = args[3];
        String blockListInfo = args[4];
        String userInfo = args[5];

        List<UserInfo> FList = getList(friendListInfo);
        List<UserInfo> BList = getList(blockListInfo);
        UIDesign app = new UIDesign(FList, BList, userName);
        app.setSize(550,350);
        app.setLocation(450, 250);
        app.setVisible(true);
        app.mb_run(ipAddress, Integer.valueOf(port), userInfo, userName);
    }
}