package BackEnd;

import java.io.IOException;

/**
 * Created by Allam on 2017/12/9.
 */
public class Server {
    private static LoginServer loginSystem;
    private static ServerEnd chatSystem;
    private static RoomServer roomSystem;
    public static int loginPort = 2000;
    public static int chatPort = 2001;
    public static int roomPort = 2002;

    public static void main(String args[]) {
        loginSystem = new LoginServer(loginPort);
        chatSystem = new ServerEnd(chatPort);
        roomSystem = new RoomServer(roomPort);

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

        Thread thread3 = new Thread() {
            @Override
            public void run() {
                System.out.println("start rooms server");
                roomSystem.runServer();
            }
        };

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        }
        catch (Exception e) {
            System.out.println("error in servers: " + e);
        }
    }
}
