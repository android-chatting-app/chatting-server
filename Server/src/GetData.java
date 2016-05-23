

import java.io.IOException;
import java.sql.SQLException;

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
		System.out.println("get process");
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
			System.out.println("send process");
			String sendChat = request.getParameter("chat");
			DataWriter writer = new DataWriter("C:/database/chat.db");
			
			writer.open();
			try {
				writer.NewChat(sendChat);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.close();
			
		}
		else if(request.getParameter("act").equals("syn")){
			System.out.println("syn process");
			int serverIdx = 0, clientIdx;
			DataReader reader = new DataReader("C:/database/chat.db");
			reader.open();
			try {
				serverIdx = reader.NewChatCheck();		
				clientIdx = Integer.parseInt(request.getParameter("idx"));
				System.out.println("serverIdx="+serverIdx+", clientIdx="+clientIdx);
				if(clientIdx == -1){
					System.out.println("Init syn process");
					System.out.println(serverIdx);
					response.getWriter().append(Integer.toString(serverIdx));
				}
				else if(clientIdx == serverIdx)
					response.getWriter().append("0");
				else{
					response.getWriter().append(Integer.toString(serverIdx-clientIdx));
					for(clientIdx++; clientIdx<=serverIdx; clientIdx++){
						response.getWriter().append(reader.SynNewChat(clientIdx));
					}
				}
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reader.close();
			
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
