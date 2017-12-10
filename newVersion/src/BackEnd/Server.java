package BackEnd;

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
                loginSystem.runServer();
            }
        };

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                chatSystem.runServer();
            }
        };

        thread1.start();
        thread2.start();

    }
}
