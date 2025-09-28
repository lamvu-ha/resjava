package dao;

import db.DBConnection;
import models.TableEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {
    public List<TableEntity> getAll() {
        List<TableEntity> list = new ArrayList<>();
        String sql = "select * from Tables order by id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TableEntity(
                        rs.getInt("id"),
                        rs.getString("status"),
                        rs.getString("occupiedBy")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public TableEntity getById(int id) {
        String sql = "select * from Tables where id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TableEntity(rs.getInt("id"), rs.getString("status"), rs.getString("occupiedBy"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void updateStatus(int id, String status, String occupiedBy) {
        String sql = "update Tables set status=?, occupiedBy=? where id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, occupiedBy);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
