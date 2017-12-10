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
    private ServerSocket serverSocket = null;

    private final int c_maxClientNum = 10;
    private Hashtable<String, UserObject> userTable;
    private final ClientThread[] threads = new ClientThread[c_maxClientNum];
    private int portNum = 1234;

    public ServerEnd(int portNum) {
        userTable = DataManagement.INSTANCE.getMap();
        this.portNum = portNum;
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

                for (int i = 0; i < c_maxClientNum; ++i) {
                    if (threads[i] == null) {
                        ClientThread tmpThread = new ClientThread(clientSocket, threads, this);

                        System.out.println("ServerEnd Success");
                        threads[i] = tmpThread;
                        threads[i].start();
                        System.out.println("thread" + i + " started");
                        break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Server.runServer() " + e);
            }
        }
    }

    public static void main(String args[]){
        ServerEnd serverEnd = new ServerEnd(1001);
        System.out.println("server runs");
        serverEnd.runServer();
    }
}
