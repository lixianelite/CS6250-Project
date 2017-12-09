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

    public ClientThread(Socket clientSocket, ClientThread[] threads, String clientName) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.clientName = "@" + clientName;

        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());
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
                String line = is.readLine();
                System.out.println(clientName + ": " + line);

                if (line.startsWith("/quit")){
                    break;
                }
                parsePacket(line);
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

    // parse the message from clients
    public void parsePacket(String line) {
        System.out.println("line: " + line);
        String[] words = line.split("\\s", 2);
        if (words[0].equals("message")) {
            sendMessage(words[1]);
        }
        else if (words[0].equals("operation")) {
            // Do operation here.
        }
    }

    private void sendMessage(String msg) {
        String[] words = msg.split(":", 2);
        if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
                synchronized (this) {
                    for (int i = 0; i < threads.length; i++) {
                        if (threads[i] != null) System.out.println(threads[i].getUserName());
                        if (threads[i] != null && threads[i] != this
                                && threads[i].clientName != null
                                && threads[i].clientName.equals(words[0])) {
                            threads[i].os.println(clientName + ": " + words[1]);
                            break;
                        }
                    }
                }
            }
        }
    }
}
