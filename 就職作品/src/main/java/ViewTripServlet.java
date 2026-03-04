

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ViewTripServlet
 */
@WebServlet("/ViewTripServlet")
public class ViewTripServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DATABASE_NAME = "sd16_works";//使用するDB名
		private static final String PROPATIES = "?characterEncoding=utf-8";
		private static final String URL = "jdbc:mysql://localhost:3306/"+DATABASE_NAME+PROPATIES;
		private static final String USER = "root";
		private static final String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewTripServlet() {
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

		    String travelIdStr = request.getParameter("travelId");
		    if (travelIdStr == null || travelIdStr.isEmpty()) {
		      response.sendRedirect("TripListServlet");
		      return;
		    }
		    int travelId = Integer.parseInt(travelIdStr);

		    try {
		      Class.forName("com.mysql.cj.jdbc.Driver");
		    } catch (ClassNotFoundException e) {
		      throw new ServletException(e);
		    }

		    try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {

		      // 旅行情報
		      String sql = "SELECT id, destination, start_date, end_date, members FROM travel WHERE id = ?";
		      String destination, startDate, endDate;
		      int members;

		      try (PreparedStatement ps = con.prepareStatement(sql)) {
		        ps.setInt(1, travelId);
		        try (ResultSet rs = ps.executeQuery()) {
		          if (!rs.next()) {
		            response.sendRedirect("TripListServlet");
		            return;
		          }
		          destination = rs.getString("destination");
		          startDate = rs.getDate("start_date").toString();
		          endDate = rs.getDate("end_date").toString();
		          members = rs.getInt("members");
		        }
		      }

		      // nights/days/dateList
		      LocalDate s = LocalDate.parse(startDate);
		      LocalDate e = LocalDate.parse(endDate);
		      int nights = (int) ChronoUnit.DAYS.between(s, e);
		      int days = nights + 1;

		      List<Map<String, String>> dateList = new ArrayList<>();
		      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd (E)", Locale.JAPANESE);
		      LocalDate cur = s;
		      while (!cur.isAfter(e)) {
		        Map<String, String> d = new HashMap<>();
		        d.put("date", cur.toString());
		        d.put("formatted", cur.format(fmt));
		        d.put("year", String.valueOf(cur.getYear()));
		        dateList.add(d);
		        cur = cur.plusDays(1);
		      }

		      // 予定一覧
		      List<Map<String, String>> scheduleList = new ArrayList<>();
		      String sql2 = "SELECT schedule_date, start_time, end_time, title, description " +
		                    "FROM travel_schedule_blocks WHERE travel_id = ? ORDER BY schedule_date, start_time";
		      try (PreparedStatement ps2 = con.prepareStatement(sql2)) {
		        ps2.setInt(1, travelId);
		        try (ResultSet rs2 = ps2.executeQuery()) {
		          while (rs2.next()) {
		            Map<String, String> m = new HashMap<>();
		            m.put("date", rs2.getDate("schedule_date").toString());
		            m.put("startTime", rs2.getTime("start_time").toString().substring(0,5));
		            m.put("endTime", rs2.getTime("end_time").toString().substring(0,5));
		            m.put("title", rs2.getString("title"));
		            m.put("description", rs2.getString("description"));
		            scheduleList.add(m);
		          }
		        }
		      }

		      // JSPへ
		      request.setAttribute("TRAVEL_ID", travelId);
		      request.setAttribute("PLACE", destination);
		      request.setAttribute("START", startDate);
		      request.setAttribute("LAST", endDate);
		      request.setAttribute("MEMBERS", String.valueOf(members));
		      request.setAttribute("NIGHTS", nights);
		      request.setAttribute("DAYS", days);
		      request.setAttribute("DATE_LIST", dateList);
		      request.setAttribute("SCHEDULE_LIST", scheduleList);

		      RequestDispatcher rd = request.getRequestDispatcher("/create.jsp");
		      rd.forward(request, response);

		    } catch (SQLException e1) {
		      e1.printStackTrace();
		      response.sendRedirect("TripListServlet?error=database");
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
