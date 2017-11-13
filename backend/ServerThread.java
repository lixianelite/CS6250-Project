import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
	private UserObj m_user = null;
	private BufferedReader m_is = null;
  private PrintStream m_os = null;
  private Socket m_clientSocket = null;
  private final MultiThreadServer m_server;

  public clientThread(Socket clientSocket, MultiThreadServer server) {
  	this.m_clientSocket = clientSocket;
  	this.m_server = server;
  }

  public void exitThread() {
  	m_is.close();
  	m_os.close();
  	m_clientSocket.close();
  }

  public String connect() {
  	try {
  		m_is = new BufferedReader(new InputStreamReader(m_clientSocket.getInputStream()));
  		m_os = new PrintStream(clientSocket.getOutputStream());
      String msg = m_is.readLine().trim();
      synchronized (this) {
      	msg = m_server.checkLogin(msg);
      	if (!msg.equals("success")) {
      		exitThread();
      	}
      	else {
      		m_user = m_server.getUserObj(msg);
      	}
      }
  	} catch (IOException e) {
  		System.out.println(e);
  	}
  }

  public void run() {
  	try {
  		while (true) {
  			String msg = is.readLine();
  			System.out.println("thread " + m_user.getUserName() + ": " + msg);
  			if (msg.startsWith("/quit") {
  				break;
  			}
  		}
  		exitThread();
  	} catch (IOException e) {
  		System.out.println(e);
  	}
  }


}