package FrontEnd;


import Model.UserInfo;
import Model.UserObject;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;

public class Login_system {
    private JFrame frame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private Socket socket;
    private BufferedReader is = null;
    private PrintStream os = null;

    public Login_system() {
        initialize();
    }

    private void initialize() {

        frame = new JFrame();
        frame.setBounds(200, 200, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblLogin = new JLabel("Please Log In ");
        lblLogin.setBounds(166, 39, 105, 16);
        frame.getContentPane().add(lblLogin);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setBounds(70, 93, 73, 16);
        frame.getContentPane().add(lblUsername);

        JLabel lblPssword = new JLabel("Password");
        lblPssword.setBounds(70, 142, 73, 16);
        frame.getContentPane().add(lblPssword);

        txtUsername = new JTextField();
        txtUsername.setBounds(143, 88, 119, 26);
        frame.getContentPane().add(txtUsername);
        txtUsername.setColumns(10);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(143, 137, 120, 26);
        frame.getContentPane().add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String password = String.valueOf(txtPassword.getPassword());
                String username = txtUsername.getText();

                boolean succ = login(username, password);

                if (succ){
                    System.out.println("lixian succeed!");
                }

                /*if (map.containsKey(username) && map.get(username).equals(password)) {
                    txtPassword.setText(null);
                    txtUsername.setText(null);
                    // jump to next info & need to import this package

                    //Login_system2 newWindow = new Login_system2();
                    //newWindow.main(null);

                    frame.setVisible(false); // make current frame disappear
                    UIDesign app = new UIDesign();
                    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    app.setSize(500,400); // change the frame size
                    app.setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(null, "Please provide valid login information","Login Error", JOptionPane.ERROR_MESSAGE);
                    txtPassword.setText(null);
                    txtUsername.setText(null);
                }*/
            }
        });
        btnLogin.setBounds(226, 224, 117, 29);
        frame.getContentPane().add(btnLogin);
    }

    public void initSocket() {
        try {
            socket = new Socket("localhost", 1234);
            os = new PrintStream(socket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Successfully connect to server");
        } catch (IOException e) {
            System.out.println("ClientApp.initSocket() " + e);
        }

    }

    private boolean login(String username, String password) {
        try {
            initSocket();
            UserObject tmp = new UserObject(username, password);
            sendMsg(tmp.parse());

            String response1 = is.readLine();
            System.out.println("response1: " + response1);

            if (response1.equals("Success")) {
                String response2 = is.readLine();
                System.out.println("response2: " + response2);
                List<UserInfo> friendList = getList(response2);

                String response3 = is.readLine();
                List<UserInfo> blockList = getList(response3);
                System.out.println("response3: " + response3);
                UIDesign app = new UIDesign(friendList, blockList, socket, is, os);
                app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                app.setSize(500,400);
                app.setVisible(true);
                frame.setVisible(false);

                return true;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }

    private void sendMsg(String msg) {
        os.println(msg);
    }

    private List<UserInfo> getList(String response){
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

    public static void main(String[] args) {
        Login_system window = new Login_system();
        window.frame.setVisible(true);
    }
}