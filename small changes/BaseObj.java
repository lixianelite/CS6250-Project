import java.io.*;

public interface BaseObj {
	String parse();

	public void deparse(String packet);

	public void writeFile() throws IOException;

	public void readFile(String path) throws IOException;
}
