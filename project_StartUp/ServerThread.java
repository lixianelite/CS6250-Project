import infra.UserObj;

import java.io.*;
import java.net.Socket;
/**
 * Created by Allam on 2017/11/13.
 */
public class ServerThread extends Thread {
    private UserObj user = null;
    private BufferedReader is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final MultiThreadServer server;

    public ServerThread(Socket clientSocket, MultiThreadServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void exitThread() {
        try {
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

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
                if (!msg.equals("success")) {
                    exitThread();
                } else {
                    user = server.getUserObjByAccount(tmp.getAccount());
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return msg;
    }

    public UserObj getUser() {
        return user;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = is.readLine();
                System.out.println("thread " + user.getAccount() + ": " + msg);
                if (msg.startsWith("/quit")) {
                    break;
                }
            }
            exitThread();
        } catch (IOException e) {
            System.out.println(e);
        }
    }


}
