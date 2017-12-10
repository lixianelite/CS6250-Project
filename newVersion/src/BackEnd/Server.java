package BackEnd;

import java.io.IOException;

/**
 * Created by Allam on 2017/12/9.
 */
public class Server {
    private static LoginServer loginSystem;
    private static ServerEnd chatSystem;
    private static int loginPort = 2000;
    private static int chatPort = 2001;

    public static void main(String args[]) {
        loginSystem = new LoginServer(loginPort);
        chatSystem = new ServerEnd(chatPort);

        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("start login server");
                loginSystem.runServer();
            }
        };

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                System.out.println("start chatting server");
                chatSystem.runServer();
            }
        };

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        }
        catch (Exception e) {
            System.out.println("error in servers: " + e);
        }

    }
}
