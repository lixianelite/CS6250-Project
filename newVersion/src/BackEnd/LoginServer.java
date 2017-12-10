package BackEnd;

import Model.DataManagement;
import Model.UserInfo;
import Model.UserObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allam on 2017/12/9.
 */
public class LoginServer {
    public static final String NO_USER = "No such user";
    public static final String USER_ONLINE = "Account online";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String SUCCESS = "Success";
    public static final String ALREADY_EXIST = "Already Exist";

    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private int portNum = -1;
    private BufferedReader is = null;
    private PrintStream os = null;

    // online users to validate users' status
    private List<String> onlineUsers = new ArrayList<String>();

    public LoginServer(int portNum) {
        this.portNum = portNum;
    }

    public void closeClientSocket() {
        try {
            is.close();
            os.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("LoginServer.closeClientSocket() " + e);
        }
    }

    public void runServer() {
        try {
            serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
            System.out.println(e);
        }
        Socket clientSocket;

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                os = new PrintStream(clientSocket.getOutputStream());
                System.out.println("connect login system.");
                parseMessage();
                closeClientSocket();
            } catch (IOException e) {
                System.out.println("LoginServer.runServer() " + e);
            }
        }
    }

    public void parseMessage() {
        try {
            String msg = is.readLine();
            System.out.println("Login Server's msg: " + msg);
            String[] options = msg.split("\\s", 2);
            System.out.println("options[0]: " + options[0]);
            System.out.println("options[1]: " + options[1]);
            if (options[0].equals("Login")) {
                authenticate(options[1]);
            } else if (options[0].equals("Registration")) {
                Register(options[1]);
            } else if (options[0].equals("Logout")) {
                removeUser(options[1]);
            }
        }
        catch (Exception e){
            System.out.println("LoginServer.parseMessage()" + e);
        }
    }

    public void removeUser(String name) {
        int selected = -1;
        for (int i = 0; i < onlineUsers.size(); ++i) {
            if (onlineUsers.get(i).equals(name)) {
                selected = i;
                break;
            }
        }
        if (selected != -1) onlineUsers.remove(selected);
    }

    public void Register(String msg) {
        UserObject userObject = new UserObject();
        userObject.deParse(msg);
        UserObject user = DataManagement.INSTANCE.findUserByUserName(userObject.getUserName());
        if (user == null) {
            DataManagement.INSTANCE.addUser(userObject);
            System.out.println("add success!");
            DataManagement.INSTANCE.printUserList();
            os.println(SUCCESS);
        } else {
            os.println(ALREADY_EXIST);
        }
    }

    public String checkLogin(UserObject loginUser) {
        UserObject user = DataManagement.INSTANCE.findUserByUserName(loginUser.getUserName());
        if (user == null) return NO_USER;
        for (String st : onlineUsers){
            if (st.equals(loginUser.getUserName())) return USER_ONLINE;
        }
        if (user.getPassword().equals(loginUser.getPassword())) return SUCCESS;
        return WRONG_PASSWORD;
    }

    public boolean authenticate(String msg){
        boolean success = false;
        try {
            synchronized (this) {
                UserObject user = new UserObject();
                user.deParse(msg);
                msg = checkLogin(user);
                if (!msg.equals(SUCCESS)) {
                    os.println(msg);
                } else {
                    user = DataManagement.INSTANCE.findUserByUserName(user.getUserName());
                    List<UserInfo> friendList = user.getFriendList();
                    List<UserInfo> blockList = user.getBlockList();
                    StringBuilder friendInfo = new StringBuilder();
                    StringBuilder blockInfo = new StringBuilder();
                    for (int i = 0; i < friendList.size(); i++){
                        friendInfo.append(friendList.get(i).getUserName() + "#");
                    }
                    for (int i = 0; i < blockList.size(); i++){
                        blockInfo.append(blockList.get(i).getUserName() + "#");
                    }
                    os.println(SUCCESS);
                    os.println(friendInfo.toString());
                    os.println(blockInfo.toString());
                    success = true;

                    // add online users.
                    onlineUsers.add(user.getUserName());
                }
            }
        } catch (Exception e) {
            System.out.println("ClientThread " + e);
        }
        return success;
    }
}
