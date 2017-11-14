import com.sun.corba.se.spi.activation.Server;
import com.sun.jmx.snmp.tasks.ThreadService;
import infra.UserObj;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Hashtable;

/**
 * Created by Allam on 2017/11/13.
 */
public class MultiThreadServer {
    private ServerSocket serverSocket = null;

    private final int c_maxClientNum = 10;

    private Hashtable<String, UserObj> users = new Hashtable<String, UserObj>();

    private final ServerThread[] threads = new ServerThread[c_maxClientNum];

    private int portNum = 1234;

    public MultiThreadServer() {
        UserObj test1 = new UserObj("123", "123", "abcd");
        UserObj test2 = new UserObj("abc", "", "9876");
        users.put(test1.getAccount(), test1);
        users.put(test2.getAccount(), test2);

        System.out.println(test1.parse());
        System.out.println(test2.parse());
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

                String connectMsg = "Server is full.";
                for (int i = 0; i < c_maxClientNum; ++i) {
                    if (threads[i] == null) {
                        ServerThread tmpThread = new ServerThread(clientSocket, this);
                        connectMsg = tmpThread.connect();
                        if (connectMsg.equals("success")) {
                            threads[i] = tmpThread;
                            threads[i].start();
                            break;
                        }
                    }
                }

                if (!connectMsg.equals("success")) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println(connectMsg);
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public String checkLogin(UserObj loginUser) {
        UserObj user = getUserObjByAccount(loginUser.getAccount());
        if (user == null) return "No such account name";
        if (getThreadByAccount(user.getAccount()) != null) return "Account online";
        if (user.getPassword().equals(loginUser.getPassword())) return "success";
        return "Wrong password";
    }

    public UserObj getUserObjByAccount(String account) {
        return users.get(account);
    }

    public ServerThread getThreadByAccount(String account) {
        for (ServerThread t : threads){
            if (t != null && account.equals(t.getUser().getAccount())) return t;
        }
        return null;
    }

    public static void main(String args[]) {
        MultiThreadServer server = new MultiThreadServer();
        server.runServer();
    }

}
