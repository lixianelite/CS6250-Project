package BackEnd;

import Model.DataManagement;
import Model.UserInfo;
import Model.UserObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by bravado on 11/18/17.
 */

public class ClientThread extends Thread {

    private String clientName = null;
    private BufferedReader is = null;
    private PrintStream os = null;

    private Socket clientSocket = null;
    private ClientThread[] threads;
    private final ServerEnd server;

    public ClientThread(Socket clientSocket, ClientThread[] threads, ServerEnd server) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.server = server;

        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            System.out.println("initialized");

        } catch (IOException e) {
            System.out.println("ClientThread " + e);
        }
    }

    public String getUserName(){
        return clientName;
    }

    public void run() {
        ClientThread[] threads = this.threads;

        try {
            while (true) {
                System.out.println(clientName + "0: ");
                String line = is.readLine();
                System.out.println(clientName + ": " + line);

                os.println("From Server: " + line);

                System.out.println(clientName + "1: " + line);

                if (line.startsWith("/quit")){
                    System.out.println("prepare to break");
                    break;
                }

                String[] words = line.split("\\s", 2);
                if (words.length > 1 && words[1] != null) {
                    words[1] = words[1].trim();
                    if (!words[1].isEmpty()) {
                        synchronized (this) {
                            for (int i = 0; i < threads.length; i++) {
                                if (threads[i] != null && threads[i] != this
                                        && threads[i].clientName != null
                                        && threads[i].clientName.equals(words[0])) {
                                    threads[i].os.println(clientName + ">> " + words[1]);
                                    //this.os.println(">" + clientName + "> " + words[1]);
                                    break;
                                }
                            }
                        }
                    }
                }
                System.out.println(clientName + "2: " + line);
            }
            System.out.println("*** Bye " + clientName + " ***");

            synchronized (this) {
                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }

            is.close();
            os.close();
            clientSocket.close();
            System.out.println("clientSocket closed");
        } catch (IOException e) {
            System.out.println("multiThreading error: " + e);
        }
    }

    public boolean authenticate(){
        boolean success = false;
        try {
            String msg = is.readLine();

            System.out.println("receive msg: " + msg);
            synchronized (this) {
                UserObject user = new UserObject();
                user.deParse(msg);

                msg = server.checkLogin(user);
                if (!msg.equals(ServerEnd.SUCCESS)) {
                    os.println(msg);
                    exitThread();
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
                    clientName = "@" + user.getUserName();
                    os.println(ServerEnd.SUCCESS);
                    os.println(friendInfo.toString());
                    os.println(blockInfo.toString());
                    success = true;
                }
            }
        } catch (IOException e) {
            System.out.println("ClientThread " + e);
        }
        return success;
    }

    public void exitThread() {
        try {
            is.close();
            os.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("ServeThread.exitThread() " + e);
        }
    }
}
