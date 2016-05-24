import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;

import org.sqlite.*;

public class DataReader {
	private Connection connection;
	private String dbFileName;
	private boolean isOpened = false;	
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch(Exception e) { e.printStackTrace(); }
	}

	public DataReader(String databaseFileName) {
		this.dbFileName = databaseFileName;
	}
	
	public boolean open() {
		try {
			// 읽기 전용
			SQLiteConfig config = new SQLiteConfig();
			config.setReadOnly(true);  // db 를 열 때 속성들을 지정해줄 수 있다
			this.connection = DriverManager.getConnection("jdbc:sqlite:/" + this.dbFileName, config.toProperties());
		} catch(SQLException e) { e.printStackTrace();  return false; }

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
	
	public HashMap<String, String> getAccount(int idx) throws SQLException{
		String query = "SELECT * FROM Accounts WHERE idx=?;";
		PreparedStatement prep = this.connection.prepareStatement(query); // 데이터베이스 전용 언어로 만드는 것, sql injection을 막는 용도로도 사용
		prep.setInt(1, idx);
		ResultSet row = prep.executeQuery();
		if(row.next()){
			HashMap<String, String> hash = new HashMap<String, String>();
			String id = row.getString(2); // 필드 순서로 구하기 
			String pw = row.getString("pw"); // 필드명으로 구하기
			hash.put("id", id); 
			hash.put("pw", pw);
			return hash;
		}else{
			return null;
		}
	}
	
	public boolean LoginCheck(String id, String pw) throws SQLException{
		String query = "SELECT * FROM Accounts WHERE id='"+id+"' AND pw='"+pw+"';";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		if(rs.getLong(1) == 0)
			return false;
		else
			return true;
	}
	
	public HashMap<String, String> ReceivedCheck(String id) throws SQLException{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * from Content");
		String chat = null;
		int idx = 0;
		HashMap<String, String> hash = new HashMap<String, String>();
		
		while(rs.next()){
			if(!rs.getString("id").contains(id)){
				chat = rs.getString("chat");
				idx = rs.getInt("idx");			
				
				hash.put("chat", chat); 
				hash.put("idx", Integer.toString(idx));
				
				return hash;
			}
		}
		
		return null;
	}
	
	public boolean RegistCheck(String id) throws SQLException{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM Accounts");
		
		while(rs.next()){
			if(id.equals(rs.getString("id"))){
				return false;
			}
		}
		return true;
	}
	
	public int NewChatCheck() throws SQLException{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * from Content ORDER BY idx DESC");
		
		if(rs.getLong(1) == 0)
			return 0;
		else
			return rs.getInt("idx");
	}
	
	public String SynNewChat(int idx) throws SQLException{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT chat from Content WHERE idx='"+idx+"'");
		
		return rs.getString(1);
	}

}
