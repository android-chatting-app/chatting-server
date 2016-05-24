

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetData
 */
@WebServlet("/GetData")
public class GetData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public GetData() {
    	System.out.println("Server");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = null, pw = null, act = null;
		if(request.getParameter("act").equals("login")){
			System.out.println("login process");
			id = request.getParameter("id");
			pw = request.getParameter("pw");
			System.out.println("login  id="+id+", pw="+pw);
			try {
				if(login(id,pw)){
					System.out.println("login success");
					response.getWriter().append("success");
				}
				else{
					System.out.println("login fail");
					response.getWriter().append("fail");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.getWriter().append("fail");
			}
		}
		else if(request.getParameter("act").equals("register")){
			System.out.println("register process");
			id = request.getParameter("id");
			pw = request.getParameter("pw");
			System.out.println("register  id="+id+", pw="+pw);
			try {
				if(regist(id,pw)){
					System.out.println("register success");
					response.getWriter().append("success");
				}
				else{
					System.out.println("register fail");
					response.getWriter().append("fail");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.getWriter().append("fail");
			}
		}
		else if(request.getParameter("act").equals("send")){
			String sendChat = request.getParameter("chat");
			System.out.println(sendChat);
			DataWriter writer = new DataWriter("C:/database/chat2.db");
			
			writer.open();
			try {
				writer.NewChat(sendChat);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				writer.close();
			}
			writer.close();
			
		}
		else if(request.getParameter("act").equals("syn")){
			String clienetID = request.getParameter("id");
			DataReader reader = new DataReader("C:/database/chat2.db");
			HashMap<String, String> chat;
			reader.open();
			try{
				if(reader.ReceivedCheck(clienetID) != null){
					chat = reader.ReceivedCheck(clienetID);
					reader.close();
					DataWriter writer = new DataWriter("C:/database/chat2.db");
					writer.open();
					writer.ReceivedAdd(clienetID, Integer.parseInt(chat.get("idx")));
					writer.close();
					response.getWriter().append(chat.get("chat"));
				}else
					response.getWriter().append(null);
			}catch(Exception ex){
				ex.printStackTrace();
				reader.close();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public boolean login(String lid, String lpw) throws SQLException{
		DataReader reader = new DataReader("C:/database/mydb.db");
		reader.open();
		boolean result = reader.LoginCheck(lid, lpw);
		reader.close();
		return result;
	}
	
	public boolean regist(String rid, String rpw) throws SQLException{
		DataReader reader = new DataReader("C:/database/mydb.db");
		reader.open();
		
		if(reader.RegistCheck(rid)){
			DataWriter writer = new DataWriter("C:/database/mydb.db");
			writer.open();
			writer.Regist(rid, rpw);
			reader.close();
			writer.close();
			return true;
		}
		reader.close();
		return false;
	}

}
