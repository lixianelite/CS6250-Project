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
import java.util.Hashtable;
import java.util.List;

/**
 * Created by bravado on 11/18/17.
 */

public class ServerEnd {
    public static final String NO_USER = "No such user";
    public static final String USER_ONLINE = "Account online";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String SUCCESS = "Success";
    public static final String ALREADY_EXIST = "Already Exist";
    private ServerSocket serverSocket = null;

    private final int c_maxClientNum = 10;
    private final ClientThread[] threads = new ClientThread[c_maxClientNum];
    private int portNum = 1234;
    private BufferedReader is;
    private PrintStream os;

    public ServerEnd() {
        for (int i = 0; i < c_maxClientNum; ++i) {
            threads[i] = null;
        }
    }

    public void runServer(){
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
                String msg = is.readLine();
                System.out.println("msg: " + msg);
                String[] options = msg.split("\\s", 2);
                System.out.println("options[0]: " + options[0]);
                System.out.println("options[1]: " + options[1]);
                if (options[0].equals("Login")){
                    boolean success = authenticate(options[1]);
                    if (success){
                        UserObject user = new UserObject();
                        user.deParse(msg);
                        for (int i = 0; i < c_maxClientNum; ++i) {
                            if (threads[i] == null) {
                                ClientThread tmpThread = new ClientThread(clientSocket, threads, user.getUserName());
                                threads[i] = tmpThread;
                                threads[i].start();
                                break;
                            }
                        }
                    }else{
                        is.close();
                        os.close();
                        clientSocket.close();
                    }
                }else if (options[0].equals("Registration")){
                    UserObject userObject = new UserObject();
                    userObject.deParse(options[1]);
                    UserObject user = DataManagement.INSTANCE.findUserByUserName(userObject.getUserName());
                    if (user == null){
                        DataManagement.INSTANCE.addUser(userObject);
                        System.out.println("add success!");
                        DataManagement.INSTANCE.printUserList();
                        os.println(SUCCESS);
                    }else {
                        os.println(ALREADY_EXIST);
                    }
                }
            } catch (IOException e) {
                System.out.println("Server.runServer() " + e);
            }
        }
    }

    public String checkLogin(UserObject loginUser) {
        UserObject user = DataManagement.INSTANCE.findUserByUserName(loginUser.getUserName());
        if (user == null) return NO_USER;
        for (int i = 0; i < threads.length; i++){
            if (threads[i] != null && threads[i].getUserName().equals("@" + user.getUserName())) return USER_ONLINE;
        }
        if (user.getPassword().equals(loginUser.getPassword())) return SUCCESS;
        return WRONG_PASSWORD;
    }

    public boolean authenticate(String message){
        boolean success = false;
        UserObject user = new UserObject();
        user.deParse(message);
        String response = checkLogin(user);
            if (!response.equals(SUCCESS)) {
                os.println(response);
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
                os.println(ServerEnd.SUCCESS);
                os.println(friendInfo.toString());
                os.println(blockInfo.toString());
                success = true;
            }
        return success;
    }

    public static void main(String args[]){
        ServerEnd serverEnd = new ServerEnd();
        System.out.println("server runs");
        serverEnd.runServer();
    }
}
