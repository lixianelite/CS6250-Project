import infra.UserObj;

import java.io.*;
import java.net.Socket;


public class ServerThread extends Thread {
    // user obj, has all the information of this user
    private UserObj user = null;
    private BufferedReader is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    // thread active or not
    private boolean active = false;

    // server in the main thread
    private final MultiThreadServer server;

    public ServerThread(Socket clientSocket, MultiThreadServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void exitThread() {
        try {
            active = false;
            is.close();
            os.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("ServeThread.exitThread() " + e);
        }
    }

    // The user tried to set up a connection with the server
    // so it needs to validate the user's account and password first.
    public String connect() {
        String msg = "failed";
        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
            msg = is.readLine().trim();
            synchronized (this) {
                UserObj tmp = new UserObj();
                tmp.deparse(msg);
                msg = server.checkLogin(tmp);
                sendMsg(msg);
                if (!msg.equals("success")) {
                    exitThread();
                } else {
                    user = server.getUserObjByAccount(tmp.getAccount());
                }
            }
        } catch (IOException e) {
            System.out.println("ServeThread.connect() " + e);
        }
        return msg;
    }

    public UserObj getUser() {
        return user;
    }

    public boolean isActive() { return active; }

    // send a message to server.
    public void sendMsg(String msg) {
        os.println(msg);
    }

    // debug purpose: send message received in server
    private void debugConnection(String msg) {
        sendMsg("server received: " + msg);
    }


    // main function of ServerThread.
    // We need to parse the command(action, message) from user here.
    @Override
    public void run() {
        active = true;
        try {
            while (true) {
                String msg = is.readLine();
                System.out.println("thread " + user.getAccount() + ": " + msg);
                debugConnection(msg);
                if (msg.startsWith("/quit")) {
                    break;
                }
            }
            exitThread();
        } catch (Exception e) {
            System.out.println("ServeThread.run() " + e);
        }
    }
}
