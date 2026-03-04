

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TripListServlet
 */
@WebServlet("/TripListServlet")
public class TripListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DATABASE_NAME = "sd16_works";//使用するDB名
		private static final String PROPATIES = "?characterEncoding=utf-8";
		private static final String URL = "jdbc:mysql://localhost:3306/"+DATABASE_NAME+PROPATIES;
		private static final String USER = "root";
		private static final String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TripListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());

		request.setCharacterEncoding("UTF-8");
	        
		 Connection con = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        try {
	            // JDBCドライバ読み込み
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            
	            // DB接続
	            con = DriverManager.getConnection(URL, USER, PASS);
	            System.out.println("DB接続成功");
	            
	            // 旅行一覧を取得（新しい順）
	            String sql = "SELECT id, destination, start_date, end_date, members, created_at " +
	                        "FROM travel ORDER BY created_at DESC;";
	            
	            pstmt = con.prepareStatement(sql);
	            rs = pstmt.executeQuery();
	            
	            // 旅行リストを作成
	            List<Map<String, Object>> tripList = new ArrayList<>();
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	            
	            while (rs.next()) {
	                Map<String, Object> trip = new HashMap<>();
	                trip.put("id", rs.getInt("id"));
	                trip.put("destination", rs.getString("destination"));
	                trip.put("startDate", rs.getString("start_date"));
	                trip.put("endDate", rs.getString("end_date"));
	                trip.put("members", rs.getString("members"));
	                
	                // 作成日時をフォーマット
	                Timestamp createdAt = rs.getTimestamp("created_at");
	                String formattedDate = sdf.format(createdAt);
	                trip.put("createdAtFormatted", formattedDate);
	                
	                tripList.add(trip);
	            }
	            
	            System.out.println("旅行一覧取得成功: " + tripList.size() + "件");
	            
	            // リクエストスコープに設定
	            request.setAttribute("tripList", tripList);
	            request.setAttribute("isEmpty", tripList.isEmpty());
	            
	            // TripList.jspに転送
	            RequestDispatcher rd = request.getRequestDispatcher("/triplist.jsp");
	            rd.forward(request, response);
	            
	        } catch (ClassNotFoundException | SQLException e) {
	            System.out.println("========== エラー発生 ==========");
	            e.printStackTrace();
	            System.out.println("===============================");
	            response.sendRedirect("home.jsp?error=database");
	            
	        } finally {
	            try {
	                if (rs != null) rs.close();
	                if (pstmt != null) pstmt.close();
	                if (con != null) con.close();
	                System.out.println("リソース解放完了");
	            } catch (SQLException e) {
	                e.printStackTrace();
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

}
