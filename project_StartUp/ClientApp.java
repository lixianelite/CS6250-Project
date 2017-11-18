import infra.UserObj;

import java.io.*;
import java.net.Socket;

/**
 * Created by allam on 11/16/17.
 */
public class ClientApp {
	// IP address of server
	private static final String IP_ADDR = "localhost";
	// port number of server's address
	private static final int PORT = 1234;

	private Socket socket;

	// stream to send msg to server.
	private PrintStream os;
	// stream to receive msg to server.
	private BufferedReader is;

	// input from system console
	private BufferedReader console;

	// Thread for receive msg from server.
	// Actually sending thread is in the main thread.
	private ReceiveThread receiveThread;


	private class ReceiveThread extends Thread {

		// main function of receive thread.
		@Override
		public void run () {
			try {
				String msg;
				// receive msg from server.
				while ((msg = is.readLine()) != null) {
					// here we need to do what we want to do for the message.
					debugOut(msg);
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

	public void initSocket() {
		try {
			// build connection with server.
			socket = new Socket(IP_ADDR, PORT);
			os = new PrintStream(socket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("Successfully connect to server");
		} catch (IOException e) {
			System.out.println("ClientApp.initSocket() " + e);
		}

	}

	// Initialization of backend.
	public ClientApp() {
		receiveThread = new ReceiveThread();
		console = new BufferedReader(new InputStreamReader(System.in));
	}

	// send a message to server.
	private void sendMsg(String msg) {
		os.println(msg);
	}

	// debug: print out msg from server.
	private void debugOut(String msg) {
		System.out.println("from server: " + msg);
	}

	// try to login and check the login message.
	private boolean login() {
		try {
			System.out.println("Please input your account:");
			String account = console.readLine();
			System.out.println("Please input your password:");
			String password = console.readLine();

			initSocket();
			// initialize a user obj
			UserObj tmp = new UserObj(account, "", password);
			// send this obj to server and check login.
			sendMsg(tmp.parse());

			// response from server.
			String response = is.readLine();
			debugOut(response);
			if (response.equals("success")) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return false;
	}

	private void connectingServer() {
		receiveThread.start();

		// I am using while(true) here because I am using console as input,
		// When applied UI, remove the while(true);
		try {
			while (true) {
				String text = console.readLine();
				// quit the client and close the server's responding thread.
				if (text.equals("/quit")) {
					sendMsg(text);
					receiveThread.join();
					System.out.println("disconnect from the server");
					break;
				}
				// here we need send some message (including action & message) to the server.
				sendMsg(text);
			}
		} catch (Exception e) {
			System.out.println("ClientApp.connectingServer() " + e);
		}
	}

	// Start to run the backend of client app
	public void run() {
		// I am using while(true) here because I am using console as input,
		// When applied UI, remove the while(true);
		while (true) {
			// if login successfully then create receiving thread and sending thread.
			if (login()) {
				connectingServer();
				break;
			}
			// other wise, continue login process.
		}
	}

	public static void main(String[] args) {

		ClientApp app = new ClientApp();

		app.run();

	}
}
