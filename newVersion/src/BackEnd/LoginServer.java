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

                authenticate();
                closeClientSocket();
            } catch (IOException e) {
                System.out.println("LoginServer.runServer() " + e);
            }
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

    public boolean authenticate(){
        boolean success = false;
        try {
            String msg = is.readLine();

            System.out.println("receive msg: " + msg);
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
        } catch (IOException e) {
            System.out.println("ClientThread " + e);
        }
        return success;
    }
}
