import javax.swing.*;  
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.*;  



import java.awt.GridBagConstraints;  
import java.awt.GridBagLayout;  

public class J_ChatServer extends JFrame
{
	private ObjectInputStream m_input;
	private ObjectOutputStream m_output;
	private JTextField m_enter;
	private JTextArea m_display;
	private int m_clientNumber = 0;
	private JFrame f;
	

	public J_ChatServer()
	{
		super("chat program server");

		// JFrame f =new JFrame();  
		// 

		Container c = getContentPane();

		m_display = new JTextArea();
		// c.add(new JScrollPane(m_display), BorderLayout.CENTER);
		c.add(new JScrollPane(m_display));
		// f.add(new JScrollPane(m_display), BorderLayout.CENTER); 
		

		DefaultListModel<String> l1 = new DefaultListModel<>();  
        l1.addElement("Friend List");  
        l1.addElement("Item2");  
        l1.addElement("Item3");  
        l1.addElement("Item4");  
        JList<String> friendList = new JList<>(l1); 
        friendList.setBounds(100,100, 75,75); 
        c.add(friendList);   



		m_enter = new JTextField(20);
		m_enter.setBounds(50,50, 150,120);  
		// m_enter.setEnabled(false);
		m_enter.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)	
				{
					try
					{
						String s = event.getActionCommand();	
						m_output.writeObject(s);
						m_output.flush();
						mb_displayAppend("server: " + s);
						m_enter.setText("");
					}
					catch(Exception e)
					{
						System.err.println("error! " + e);	
						e.printStackTrace();
					}
				}
			}
		);

		JPanel panel = new JPanel();
        JLabel label = new JLabel("Type sentenses here:");  
        panel.add(label); 
        panel.add(m_enter); 
        c.add(panel);
		// c.add(m_enter, BorderLayout.SOUTH);	
		// f.add(m_enter,BorderLayout.SOUTH);
		// 
		
		DefaultListModel<String> l2 = new DefaultListModel<>();  
        l2.addElement("Block List:");  
        l2.addElement("Item2");  
        l2.addElement("Item3");  
        l2.addElement("Item4");  
        JList<String> blockList = new JList<>(l2); 
        blockList.setBounds(100,100, 75,75); 
        c.add(blockList); 



		
		c.setLayout(new GridLayout(2,2));  
		// f.setSize(300,300);  
		// f.setVisible(true);  
		
	}

	public void mb_displayAppend(String s)
	{
		m_display.append(s + "\n");	
		m_display.setCaretPosition(m_display.getText().length());
		m_enter.requestFocusInWindow();
	}

	public boolean mb_isEndSession(String m)
	{
		if(m.equalsIgnoreCase("q"))	
		{
			return(true);	
		}
		if(m.equalsIgnoreCase("quit"))	
		{
			return(true);	
		}
		if(m.equalsIgnoreCase("exit"))	
		{
			return(true);	
		}
		if(m.equalsIgnoreCase("end"))	
		{
			return(true);	
		}
		return false;
	}

	public void mb_run()
	{
		try
		{
			ServerSocket server = new ServerSocket(5000);	
			String m;
			while(true)
			{
				m_clientNumber++;	
				mb_displayAppend("waiting for connection [" + m_clientNumber + "]");
				Socket s = server.accept();
				mb_displayAppend("receives connection from client [" + m_clientNumber + "]");
				m_output = new ObjectOutputStream(s.getOutputStream());
				m_input = new ObjectInputStream(s.getInputStream());
				m_output.writeObject("connect!");
				m_output.flush();
				m_enter.setEnabled(true);
				do
				{
					m = (String)m_input.readObject();
					mb_displayAppend("Client: " + m);
				}
				while(!mb_isEndSession(m));
				m_output.writeObject("q");
				m_output.flush();
				m_enter.setEnabled(false);
				m_output.close();
				m_input.close();
				s.close();
				mb_displayAppend("connection from client [" + m_clientNumber + "] ends");
			}
		}
		catch(Exception e)
		{
			System.err.println("error! " + e);	
			e.printStackTrace();
			mb_displayAppend("connection error!");
		}
	}
	
	public static void main(String [] args)
	{
		J_ChatServer app = new J_ChatServer();	

		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setSize(350,150);
		app.setVisible(true);
		app.mb_run();
	}
}
