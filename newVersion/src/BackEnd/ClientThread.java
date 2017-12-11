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

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;

        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            clientName = "@" + is.readLine();
            System.out.println("client thread initialized");

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
            while (!isInterrupted()) {
                String line = is.readLine();
                System.out.println(clientName + ": " + line);

                if (line.startsWith("/quit")){
                    break;
                }
                parsePacket(line);
            }

            synchronized (this) {
                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            exitThread();
        } catch (IOException e) {
            System.out.println("multiThreading error: " + e);
        }
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

    // parse the message from clients
    public void parsePacket(String line) {
        System.out.println("line: " + line);
        String[] words = line.split("\\s", 2);
        if (words[0].equals("message")) {
            sendMessage(words[1]);
        }
        else if (words[0].equals("operation")) {
            String result = processAdd(words[1]);
            System.out.println("result: " + result);
            this.os.println("@admin:" + result);
        }
    }

    private String processAdd(String operation){
        String[] contents = operation.split("\\s", 2);
        String name = contents[1];
        UserObject searchObject = DataManagement.INSTANCE.findUserByUserName(name);
        String cName = clientName.substring(1);
        UserObject currentUserObject = DataManagement.INSTANCE.findUserByUserName(cName);
        List<UserInfo> blockList = currentUserObject.getBlockList();
        List<UserInfo> friendList = currentUserObject.getFriendList();

        System.out.println("contents[1] " + contents[1]);

        if (searchObject != null){
            if (contents[0].equals("FRIEND")){
                System.out.println("NAME: " + name);
                removeUserFromList(blockList, name, "BLOCK");
                friendList.add(new UserInfo(searchObject.getUserName()));
                this.os.println("@admin:add FRIEND " + name);
                System.out.println("cName: " + cName);
            }else if (contents[0].equals("BLOCK")){
                removeUserFromList(friendList, name, "FRIEND");
                blockList.add(new UserInfo(searchObject.getUserName()));
                this.os.println("@admin:add BLOCK " + name);
            }
        }else {
            return "USER_NOT_EXIST";
        }
        return "SUCCESS";
    }

    private void removeUserFromList(List<UserInfo> list, String name, String listName){
        for (int i = 0; i < list.size(); i++){
            if (name.equals(list.get(i).getUserName())){
                list.remove(i);
                this.os.println("@admin:remove "+ listName + " " + name);
            }
        }
    }

    private void sendMessage(String msg) {
        String[] words = msg.split(":", 2);
        if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
                synchronized (this) {
                    for (int i = 0; i < threads.length; i++) {
                        if (threads[i] != null && threads[i] != this
                                && threads[i].clientName != null
                                && threads[i].clientName.equals(words[0])) {
                            String receiver = words[0].replaceFirst("@", "");
                            String sender = this.clientName.replace("@", "");
                            System.out.println("receiver: " + receiver);
                            System.out.println("sender: " + sender);

                            UserObject receiverObject = DataManagement.INSTANCE.findUserByUserName(receiver);
                            List<UserInfo> friendList = receiverObject.getFriendList();
                            List<UserInfo> blockList = receiverObject.getBlockList();
                            UserInfo senderFriend = searchPeople(friendList, sender);
                            UserInfo senderBlock = searchPeople(blockList, sender);
                            if (senderBlock != null){
                                this.os.println("extra:Send failed!!! The receiver you choose list you in the block list");
                            }else if(senderFriend != null){
                                threads[i].os.println(clientName + ": " + words[1]);
                            }else{
                                this.os.println("extra:Send failed!!! The receiver you choose doesn't know you!");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private UserInfo searchPeople(List<UserInfo> list, String name){
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUserName().equals(name)) return list.get(i);
        }
        return null;
    }
}
