

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DeleteTripServlet
 */
@WebServlet("/DeleteTripServlet")
public class DeleteTripServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String DATABASE_NAME = "sd16_works";
    private static final String PROPATIES = "?characterEncoding=utf-8";
    private static final String URL = "jdbc:mysql://localhost:3306/"+DATABASE_NAME+PROPATIES;
    private static final String USER = "root";
    private static final String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteTripServlet() {
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

        int travelId;
        try {
            travelId = Integer.parseInt(request.getParameter("travelId"));
        } catch (Exception e) {
            response.sendRedirect("TripListServlet?error=bad_id");
            return;
        }

        // travel_schedule_blocks は travel_id で紐づいてる（あなたのDB構造）
        String delBlocksSql = "DELETE FROM travel_schedule_blocks WHERE travel_id = ?";
        String delTravelSql = "DELETE FROM travel WHERE id = ?";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(delBlocksSql);
                 PreparedStatement ps2 = con.prepareStatement(delTravelSql)) {

                ps1.setInt(1, travelId);
                ps1.executeUpdate();

                ps2.setInt(1, travelId);
                ps2.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("TripListServlet?error=delete_failed");
            return;
        }

        // 削除後は一覧へ戻す
        response.sendRedirect("TripListServlet?deleted=1");
    }
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
