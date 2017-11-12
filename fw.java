// Basic Obj class that can be parsed into packet
// or deparse from packet.
// It could also be written into file and read from file.
interface BaseObj {
	String parse();

	public void deparse(String packet);

	public void writeFile();

	public void readFile(String path);	
}

public class User implements BaseObj {
	//unique
	String m_account;
	//changeable
	String m_userName;
	String m_password;

	ListObj m_friendList;
	ListObj m_blockList;

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

class ListObj implements BaseObj {
	ArrayList<String> m_list;

	public void addObj(String st) {

	}

	public void removeObj(String st) {

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

public class Server {
	Hashtable<String, UserObj> m_users;

	public void readUsersFile() {

	}

	public void writeUsersFile() {

	}
}