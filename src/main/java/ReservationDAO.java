

import java.sql.*;
import java.util.Random;

public class ReservationDAO {

	public String addReservation(String passengerName, String trainNumber, String trainName,
            String classType, String from, String to, Date journeyDate) {
String pnr = "PNR" + new Random().nextInt(999999);
String sql = "INSERT INTO reservations (pnr, passenger_name, train_number, train_name, class_type, from_place, to_place, date_of_journey) VALUES (?,?,?,?,?,?,?,?)";

try (Connection conn = DBConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

ps.setString(1, pnr);
ps.setString(2, passengerName);
ps.setString(3, trainNumber);
ps.setString(4, trainName);
ps.setString(5, classType);
ps.setString(6, from);
ps.setString(7, to);
ps.setDate(8, journeyDate);

int rows = ps.executeUpdate();

System.out.println("Rows inserted: " + rows);
return pnr;

} catch (Exception e) {
e.printStackTrace(); // ✅ This will print full error in the console
System.out.println("Error message: " + e.getMessage()); // ✅ Log it
}

return null;
}

    public ResultSet searchByPNR(String pnr) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE pnr = ?";
        Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pnr);
        return ps.executeQuery();
    }

    public boolean cancelReservation(String pnr) {
        String sql = "DELETE FROM reservations WHERE pnr = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
