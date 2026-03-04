

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SaveScheduleServlet
 */
@WebServlet("/SaveScheduleServlet")
public class SaveScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String DATABASE_NAME = "sd16_works";
    private static final String PROPATIES = "?characterEncoding=utf-8";
    private static final String URL = "jdbc:mysql://localhost:3306/"+DATABASE_NAME+PROPATIES;
    private static final String USER = "root";
    private static final String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveScheduleServlet() {
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

		    // -------------------------
		    // ① パラメータ取得
		    // -------------------------
		    int travelId;
		    try {
		        travelId = Integer.parseInt(request.getParameter("travelId"));
		    } catch (Exception e) {
		        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "travelId 不正");
		        return;
		    }

		    String[] dates = request.getParameterValues("schedule_date");
		    String[] startTimes = request.getParameterValues("start_time");
		    String[] endTimes = request.getParameterValues("end_time");
		    String[] titles = request.getParameterValues("title");
		    String[] descriptions = request.getParameterValues("description");

		    // 予定0件でもOK（削除のみ）
		    if (dates == null || startTimes == null || endTimes == null || titles == null) {
		        dates = new String[0];
		        startTimes = new String[0];
		        endTimes = new String[0];
		        titles = new String[0];
		        descriptions = new String[0];
		    }

		    // -------------------------
		    // ② SQL文を定義（★ここに貼る）
		    // -------------------------
		    String delSql =
		        "DELETE FROM travel_schedule_blocks WHERE travel_id = ?";

		    String insSql =
		        "INSERT INTO travel_schedule_blocks " +
		        "(travel_id, schedule_date, start_time, end_time, title, description) " +
		        "VALUES (?,?,?,?,?,?)";

		    // -------------------------
		    // ③ DB処理
		    // -------------------------
		    try (
		        Connection con = DriverManager.getConnection(URL, USER, PASS);
		        PreparedStatement del = con.prepareStatement(delSql);
		        PreparedStatement ins = con.prepareStatement(insSql);
		    ) {
		        con.setAutoCommit(false); // トランザクション開始

		        // 既存予定を全削除
		        del.setInt(1, travelId);
		        del.executeUpdate();

		        // 新しい予定を登録
		        for (int i = 0; i < titles.length; i++) {
		            ins.setInt(1, travelId);
		            ins.setDate(2, java.sql.Date.valueOf(dates[i]));
		            ins.setTime(3, java.sql.Time.valueOf(startTimes[i] + ":00"));
		            ins.setTime(4, java.sql.Time.valueOf(endTimes[i] + ":00"));
		            ins.setString(5, titles[i]);
		            ins.setString(6,
		                (descriptions != null && descriptions.length > i)
		                    ? descriptions[i]
		                    : ""
		            );
		            ins.addBatch();
		        }

		        ins.executeBatch();
		        con.commit();

		    } catch (Exception e) {
		        e.printStackTrace();
		        throw new ServletException(e);
		    }

		    // -------------------------
		    // ④ 保存完了 → 一覧へ戻す（★ここに貼る）
		    // -------------------------
		    response.sendRedirect("TripListServlet?saved=1");
		}
		  


	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
