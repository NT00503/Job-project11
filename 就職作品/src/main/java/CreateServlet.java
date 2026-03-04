

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CreateServlet
 */
@WebServlet("/CreateServlet")
public class CreateServlet extends HttpServlet {
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
    public CreateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		//文字化け
		request.setCharacterEncoding("UTF8");
		
        
        String planIdStr = request.getParameter("planId");
        System.out.println("受け取った planId = " + planIdStr);

        if (planIdStr == null || planIdStr.isEmpty()) {
            response.sendRedirect("HomeServlet");
            return;
        }
        
        // パラメータ取得
        String strPlace = request.getParameter("place");
        String strStart = request.getParameter("start");
        String strLast = request.getParameter("last");
        String strMembers = request.getParameter("members");

        // デバッグ出力
        System.out.println("place: " + strPlace);
        System.out.println("start: " + strStart);
        System.out.println("last: " + strLast);
        System.out.println("members: " + strMembers);

        // 属性設定
        request.setAttribute("PLACE", strPlace);
        request.setAttribute("START", strStart);
        request.setAttribute("LAST", strLast);
        request.setAttribute("MEMBERS", strMembers);

        String sql = "INSERT INTO travel(destination, start_date, end_date, members) VALUES (?,?,?,?)";
        
        Connection con = null;
        PreparedStatement ps = null;
        
     // 宿泊数と日数を計算
        int nights = 0;
        int days = 0;
        List<String> dateList = new ArrayList<>();
        
        if (strStart != null && strLast != null) {
            try {
                LocalDate startDate = LocalDate.parse(strStart);
                LocalDate endDate = LocalDate.parse(strLast);
                
                // 宿泊数 = 終了日 - 開始日
                nights = (int) ChronoUnit.DAYS.between(startDate, endDate);
                // 日数 = 宿泊数 + 1
                days = nights + 1;
                
                // 各日付をリストに追加
                LocalDate currentDate = startDate;
                while (!currentDate.isAfter(endDate)) {
                    dateList.add(currentDate.toString());
                    currentDate = currentDate.plusDays(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
        try {
            // JDBCドライバ読み込み
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // DB接続
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("DB接続成功");
            
            // PreparedStatement作成
            ps = con.prepareStatement(sql);
            
            // ✅ 修正: ps（psを使う）に値をセット
            ps.setString(1, strPlace);
            ps.setString(2, strStart);
            ps.setString(3, strLast);
            ps.setString(4, strMembers);
            
            System.out.println("SQL実行: " + ps.toString());
            
            // INSERT実行
            ps.executeUpdate();
            System.out.println("データ挿入成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            // エラー時の処理を追加することを推奨
            throw new ServletException("データベースエラーが発生しました", e);
            
        } finally {
            // リソース解放
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Create.jsp に遷移
        RequestDispatcher rd = request.getRequestDispatcher("/create.jsp");
        rd.forward(request, response);
    }

	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
