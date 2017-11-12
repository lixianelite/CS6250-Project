import java.util.*;
import java.io.*;

public class User implements BaseObj{
	//unique
	String m_account;
	//changeable
	String m_userName;
	String m_password;

	ListObj m_friendList;
	ListObj m_blockList;

	public String parse() {
		
		StringBuilder sb=new StringBuilder();
		
		if(m_account.length()==0)
			sb.append("nil");
		else
			sb.append(m_account);
		
		sb.append("#");
		
		if(m_userName.length()==0)
			sb.append("nil");
		else
			sb.append(m_userName);
		
		sb.append("#");
		
		if(m_password.length()==0)
			sb.append("nil");
		else
			sb.append(m_password);
		
		return sb.toString();
	}

	public void deparse(String packet) {

		String[] arr=packet.split("#");
		
		if(arr[0]=="nil")
		   m_account=null;
		else
		   m_account=arr[0];
				
		if(arr[1]=="nil")
		   m_userName=null;
		else
		   m_userName=arr[1];
		
		if(arr[2]=="nil")
		   m_password=null;
		else
		   m_password=arr[2];		
	}

	public void writeFile()throws IOException {
		PrintWriter output;
		
		String output_file="outputFile";
		
		output = new PrintWriter(output_file, "UTF-8");
		
		output.println(m_account);
		
		output.println(m_userName);
		
		output.println(m_password);
		
		output.close();		
	}

	public void readFile(String path)throws IOException {
        
		
		BufferedReader br = new BufferedReader(new FileReader(path));
        		
		m_account = br.readLine();
		
		m_userName= br.readLine();
		
		m_password= br.readLine();
		
		br.close();
		
	}
	
	public static void main(String[] args){
		User newUser=new User();
		
		newUser.deparse("nil#John#9401");
		
		System.out.println(newUser.m_account);
		System.out.println(newUser.m_userName);
		System.out.println(newUser.m_password);
		
	}
}

class ListObj implements BaseObj {
	ArrayList<String> m_list;

	public void addObj(String st) {		
		if(!m_list.contains(st))			
		    m_list.add(st);
	}

	public void removeObj(String st) {
       if(m_list.contains(st))
    	   m_list.remove(st);
	}

	public String parse() {
		return "";
	}

	public void deparse(String packet) {

	}

	public void writeFile() {

	}

	public void readFile(String path) {

	}
}

class MsgObj implements BaseObj {

	public String parse() {
		return "";
	}

	public void deparse(String packet) {

	}

	public void writeFile() {

	}

	public void readFile(String path) {

	}
}