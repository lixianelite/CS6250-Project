package FrontEnd;

import BackEnd.ServerEnd;

import Model.UserObject;
import oracle.jvm.hotspot.jfr.JFR;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


/**
 * Created by bravado on 12/9/17.
 */
public class Registration{
    private JFrame frame;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtPasswordConfirm;
    private JLabel indicate;
    private Socket socket;

    private BufferedReader is = null;
    private PrintStream os = null;

    public Registration() {
        initialize();
    }

    public JFrame getFrame(){
        return frame;
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(200, 200, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel labelRegistration = new JLabel("Registration");
        labelRegistration.setBounds(180, 30, 100, 16);
        frame.getContentPane().add(labelRegistration);

        JLabel labelUsername = new JLabel("Username");
        labelUsername.setBounds(90, 75, 105, 16);
        frame.getContentPane().add(labelUsername);

        JLabel labelPassword = new JLabel("Password");
        labelPassword.setBounds(90, 105, 73, 16);
        frame.getContentPane().add(labelPassword);

        JLabel labelPasswordConfirm = new JLabel("Confirm");
        labelPasswordConfirm.setBounds(95, 135, 73, 16);
        frame.getContentPane().add(labelPasswordConfirm);

        txtUsername = new JTextField();
        txtUsername.setBounds(170, 70, 119, 26);
        frame.getContentPane().add(txtUsername);
        txtUsername.setColumns(10);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(170, 100, 120, 26);
        frame.getContentPane().add(txtPassword);

        txtPasswordConfirm = new JPasswordField();
        txtPasswordConfirm.setBounds(170, 130, 120, 26);
        frame.getContentPane().add(txtPasswordConfirm);

        indicate = new JLabel();
        indicate.setBounds(155, 140, 120, 26);
        frame.getContentPane().add(indicate);

        JButton btnLogin = new JButton("Register");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("register performed");
            }
        });
        btnLogin.setBounds(100, 180, 117, 29);
        frame.getContentPane().add(btnLogin);
        JButton btnRegister = new JButton("Exit");
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });
        btnRegister.setBounds(226, 180, 117, 29);
        frame.getContentPane().add(btnRegister);
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
            System.out.println("response2: " + response2);
            String response3 = is.readLine();

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
            sendMsg(UserInfo);

            response1 = is.readLine();
            System.out.println("response1: " + response1);

        } catch (IOException e) {
            System.out.println(e);
        }
        return response1;
    }

    private void sendMsg(String msg) {
        os.println(msg);
    }

    public static void main(String[] args) {
        Registration window = new Registration();
        window.frame.setVisible(true);
    }

}