package FrontEnd;


import BackEnd.ServerEnd;
import Model.UserInfo;
import Model.UserObject;
import com.sun.org.apache.regexp.internal.RE;

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

public class Login_system implements Observer {
    private JFrame frame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel indicate;
    private Socket socket;

    private BufferedReader is = null;
    private PrintStream os = null;

    private String[] args;

    public Login_system() {
        initialize();
    }

    @Override
    public void update(Observable o, Object arg) {
        System.exit(0);
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

        indicate = new JLabel("");
        indicate.setBounds(70, 200, 200, 16);
        frame.getContentPane().add(indicate);

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
                args[2] = username;

                String responseMessage = login(username, password);

                if (responseMessage.equals(ServerEnd.SUCCESS)){
                    handleSucessLogin();
                    os.println("/quit");
                    socketClose();
                    UIDesign.main(args);
                    frame.setVisible(false);
                }else{
                    indicate.setText(responseMessage);
                }
            }
        });
        btnLogin.setBounds(226, 224, 117, 29);
        frame.getContentPane().add(btnLogin);
        args = new String[6];
    }

    public void initSocket() {
        try {
            socket = new Socket("localhost", 1234);
            args[0] = "localhost";
            args[1] = 1234 + "";
            os = new PrintStream(socket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Successfully connect to server");
        } catch (IOException e) {
            System.out.println("ClientApp.initSocket() " + e);
        }
    }

    public void socketClose(){
        try {
            os.close();
            is.close();
            socket.close();
            System.out.println("socket close Successfully");
        } catch (IOException e) {
            System.out.println("Close " + e);
        }
    }

    public void handleSucessLogin(){
        try{
            String response2 = is.readLine();
            args[3] = response2;
            System.out.println("response2: " + response2);
            String response3 = is.readLine();
            args[4] = response3;
            System.out.println("response3: " + response3);
        }catch (IOException e) {
            System.out.println("handleSucessLogin " + e);
        }
    }

    private String login(String username, String password) {
        String response1 = "";
        try {
            initSocket();
            UserObject tmp = new UserObject(username, password);
            String UserInfo = tmp.parse();
            args[5] = UserInfo;
            sendMsg(UserInfo);

            response1 = is.readLine();
            System.out.println("response1: " + response1);

            /*if (response1.equals("Success")) {
                String response2 = is.readLine();
                args[3] = response2;
                System.out.println("response2: " + response2);
                String response3 = is.readLine();
                args[4] = response3;
                System.out.println("response3: " + response3);
                return true;
            }*/
        } catch (IOException e) {
            System.out.println(e);
        }
        return response1;
    }

    private void sendMsg(String msg) {
        os.println(msg);
    }



    public static void main(String[] args) {
        Login_system window = new Login_system();
        window.frame.setVisible(true);
    }
}