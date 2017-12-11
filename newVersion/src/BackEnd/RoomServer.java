package BackEnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by allam on 12/10/17.
 */
public class RoomServer {
    private int portNum;
    private ServerSocket serverSocket = null;

    private List<Room> rooms = new ArrayList<>();

    private class Room {
        private String roomName;
        private List<ChatThread> users = new ArrayList<>();

        private class ChatThread extends Thread {
            private String userName;
            private Socket socket;
            private BufferedReader is;
            private PrintStream os;
            private List<ChatThread> users;

            public ChatThread(String user, Socket socket, BufferedReader is, PrintStream os, List<ChatThread> users) {
                this.userName = user;
                this.socket = socket;
                this.is = is;
                this.os = os;
                this.users = users;
            }

            public void sendMsg(String msg) {
                os.println(msg);
            }

            public void exitThread() {
                try {
                    is.close();
                    os.close();
                    socket.close();
                }
                catch (Exception e) {
                    System.out.println("RoomServer.ChatThread.exitThread() " + e);
                }
            }

            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        String msg = is.readLine();

                        if (!msg.equals(null)) {
                            System.out.println(msg);

                            if (msg.equals("/quit")) {
                                break;
                            }

                            synchronized (this) {
                                for (ChatThread user : users) {
                                    user.sendMsg(msg);
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        System.out.println("RoomServer.ChatThread.run() " + e);
                        System.out.println(e.getStackTrace());
                    }
                }

                synchronized (this) {
                    exitThread();
                    users.remove(this);
                    for (ChatThread user : users) {
                        user.sendMsg(userName + " exit room.");
                    }
                }
            }
        }

        public Room(String roomName) {
            this.roomName = roomName;
        }

        public void addUser(String user, Socket socket, BufferedReader is, PrintStream os) {
            ChatThread newThread = new ChatThread(user, socket, is, os, this.users);
            synchronized (this) {
                users.add(newThread);
                for (ChatThread t : users) {
                    t.sendMsg(user + " enter room.");
                }
            }
            newThread.start();
        }
    }

    public RoomServer(int portNum) {
        this.portNum = portNum;
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
                BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintStream os = new PrintStream(clientSocket.getOutputStream());
                String userName = is.readLine();
                String roomName = is.readLine();

                System.out.println(userName + " enter room: " + roomName);

                boolean findRoom = false;
                for (Room room : rooms) {
                    if (room.roomName.equals(roomName)) {
                        room.addUser(userName, clientSocket, is, os);
                        findRoom = true;
                    }
                }

                if (!findRoom) {
                    Room newRoom = new Room(roomName);
                    newRoom.addUser(userName, clientSocket, is, os);
                    rooms.add(newRoom);
                }
            } catch (IOException e) {
                System.out.println("Server.runServer() " + e);
            }
        }
    }
}
