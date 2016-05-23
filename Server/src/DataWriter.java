import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;

import org.sqlite.*;

public class DataWriter {
	private Connection connection;
	private String dbFileName;
	private boolean isOpened = false;	
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch(Exception e) { e.printStackTrace(); }
	}

	public DataWriter(String databaseFileName) {
		this.dbFileName = databaseFileName;
	}
	
	public boolean open() {
		try {
			SQLiteConfig config = new SQLiteConfig();
			this.connection = DriverManager.getConnection("jdbc:sqlite:/" + this.dbFileName, config.toProperties());
		} catch(SQLException e) { e.printStackTrace(); return false; }

		isOpened = true;
		return true;
	}
	public boolean close() {
		if(this.isOpened == false) { return true; }

		try {
			this.connection.close();
		} catch(SQLException e) { e.printStackTrace(); return false; }
		return true;
	}
	
	public int addAccount(String id, String pw) throws SQLException{
		String query = "insert into Accounts (id,pw) values (?,?);";
		PreparedStatement prep = this.connection.prepareStatement(query);
		prep.setString(1, id);
		prep.setString(2, pw);
		int res = prep.executeUpdate();
		if(res == 1){
			connection.commit();
		}
		return res;
	}
	
	public void Regist(String id, String pw) throws SQLException{
		Statement stmt = connection.createStatement();
		String query = "INSERT INTO Accounts (id,pw) VALUES ('"+id+"','"+pw+"');";		
		stmt.executeUpdate(query);		
	}
	
	public void NewChat(String chat) throws SQLException{
		Statement stmt = connection.createStatement();
		String query = "INSERT INTO Content (chat) VALUES ('"+chat+"');";		
		stmt.executeUpdate(query);	
	}
}
