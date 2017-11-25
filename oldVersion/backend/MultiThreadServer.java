import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class MultiThreadServer {
	private static ServerSocket m_serverSocket = null;

	private static final int c_maxClientNum = 10;

	private HashTable<String, UserObj> m_users;

	private static final ServerThread[] m_threads = new ServerThread[c_maxClientNum];

	private int m_portNum = 20;

	public static void main(String args[]) {
    try {
		  m_serverSocket = new ServerSocket(m_portNum);
		} catch (IOException e) {
		  System.out.println(e);
		}

		Socket clientSocket = null;

		while (true) {
		  try {
				clientSocket = m_serverSocket.accept();

				String connectMsg = "Server is full.";
				for (int i = 0; i < c_maxClientNum; ++i) {
				  if (m_threads[i] == null) {
						ServerThread tmpThread = new ServerThread(clientSocket, this);
						connectMsg = tmpThread.connect();
						if (connectMsg.equals("success")) {
							m_threads[i] = tmpThread;
							m_threads[i].start();
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

}