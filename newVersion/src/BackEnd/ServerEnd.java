package BackEnd;

import Model.DataManagement;
import Model.UserObject;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Created by bravado on 11/18/17.
 */

public class ServerEnd {
    public static final String NO_USER = "No such user";
    public static final String USER_ONLINE = "Account online";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String SUCCESS = "Success";
    private ServerSocket serverSocket = null;

    private final int c_maxClientNum = 10;
    private Hashtable<String, UserObject> userTable;
    private final ClientThread[] threads = new ClientThread[c_maxClientNum];
    private int portNum = 1234;

    public ServerEnd() {
        userTable = DataManagement.INSTANCE.getMap();

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

                String connectMsg = "Server is full.";

                for (int i = 0; i < c_maxClientNum; ++i) {
                    if (threads[i] == null) {
                        ClientThread tmpThread = new ClientThread(clientSocket, threads, this);

                        boolean success = tmpThread.authenticate();

                        if (success) {
                            threads[i] = tmpThread;
                            threads[i].start();
                            break;
                        }
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

    public static void main(String args[]){
        ServerEnd serverEnd = new ServerEnd();
        System.out.println("server runs");
        serverEnd.runServer();
    }
}
