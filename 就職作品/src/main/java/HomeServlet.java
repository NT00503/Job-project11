

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HomeServlet
 */
@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//定数宣言
	private static final String DATABASE_NAME = "sd16_works";//使用するDB名
	private static final String PROPATIES = "?characterEncoding=utf-8";
	private static final String URL = "jdbc:mysql://localhost:3306/"+DATABASE_NAME+PROPATIES;
	private static final String USER = "root";
	private static final String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		
		
		
		// 文字化け対策
		request.setCharacterEncoding("UTF-8");
		
		String strPlace = request.getParameter("place");
String strStart = request.getParameter("start");
String strLast = request.getParameter("last");
String strMembers = request.getParameter("members");

if (strPlace == null || strPlace.trim().isEmpty() ||
    strStart == null || strStart.trim().isEmpty() ||
    strLast == null || strLast.trim().isEmpty() ||
    strMembers == null || strMembers.trim().isEmpty()) {
    response.sendRedirect("home.jsp?error=required");
    return;
}

Connection con = null;
PreparedStatement ps = null;
ResultSet keys = null;

try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    con = DriverManager.getConnection(URL, USER, PASS);

    String sql = "INSERT INTO travel (destination, start_date, end_date, members) VALUES (?, ?, ?, ?)";
    ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);

    ps.setString(1, strPlace);
    ps.setString(2, strStart);
    ps.setString(3, strLast);
    ps.setString(4, strMembers);

    int result = ps.executeUpdate(); // ← 挿入件数
    System.out.println("挿入件数: " + result);

    keys = ps.getGeneratedKeys();
    if (keys.next()) {
        int travelId = keys.getInt(1);
        response.sendRedirect("ViewTripServlet?travelId=" + travelId);
        return;
    }

    // 万が一ID取れなかった場合
    response.sendRedirect("TripListServlet");

} catch (Exception e) {
    e.printStackTrace();
    response.sendRedirect("home.jsp?error=database");

} finally {
    try { if (keys != null) keys.close(); } catch (Exception ignore) {}
    try { if (ps != null) ps.close(); } catch (Exception ignore) {}
    try { if (con != null) con.close(); } catch (Exception ignore) {}
}

    }
		
		
		
		
		
		
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
