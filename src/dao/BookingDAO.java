package dao;

import db.DBConnection;
import models.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    public boolean create(Booking b) {
        String sql = "insert into Bookings(id, customerName, phone, tableId, bookingTime, partySize, status) values(?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.id);
            ps.setString(2, b.customerName);
            ps.setString(3, b.phone);
            ps.setInt(4, b.tableId);
            ps.setTimestamp(5, new Timestamp(b.bookingTime.getTime()));
            ps.setInt(6, b.partySize);
            ps.setString(7, b.status);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public List<Booking> getAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "select * from Bookings order by bookingTime desc";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Booking(
                        rs.getString("id"),
                        rs.getString("customerName"),
                        rs.getString("phone"),
                        rs.getInt("tableId"),
                        rs.getTimestamp("bookingTime"),
                        rs.getInt("partySize"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
