package dao;

import db.DBConnection;
import models.Order;
//import models.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderDAO {

    // create order and items in one transaction
    public String createOrder(int tableId, List<Order.OrderItem> items) {
        String orderId = UUID.randomUUID().toString();
        String sqlOrder = "insert into Orders(id, tableId, total, status, createdAt, paid) values(?,?,?,?,getdate(),0)";
        String sqlItem = "insert into OrderItems(orderId, menuId, quantity, unitPrice) values(?,?,?,?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int total = 0;
            for (Order.OrderItem it : items) total += it.quantity * it.unitPrice;

            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder)) {
                psOrder.setString(1, orderId);
                psOrder.setInt(2, tableId);
                psOrder.setInt(3, total);
                psOrder.setString(4, "New");
                psOrder.executeUpdate();
            }

            try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                for (Order.OrderItem it : items) {
                    psItem.setString(1, orderId);
                    psItem.setInt(2, it.menuId);
                    psItem.setInt(3, it.quantity);
                    psItem.setInt(4, it.unitPrice);
                    psItem.executeUpdate();
                }
            }

            conn.commit();
            return orderId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Order> getOrdersByTable(int tableId) {
        List<Order> list = new ArrayList<>();
        String sql = "select * from Orders where tableId=? order by createdAt desc";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = new Order();
                    o.id = rs.getString("id");
                    o.tableId = rs.getInt("tableId");
                    o.total = rs.getInt("total");
                    o.status = rs.getString("status");
                    o.createdAt = rs.getTimestamp("createdAt");
                    o.paid = rs.getBoolean("paid");
                    // load items
                    o.items = getItemsByOrder(o.id);
                    list.add(o);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Order.OrderItem> getItemsByOrder(String orderId) {
        List<Order.OrderItem> items = new ArrayList<>();
        String sql = "select oi.*, m.name from OrderItems oi left join Menu m on oi.menuId = m.id where oi.orderId=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order.OrderItem it = new Order.OrderItem(
                            rs.getInt("menuId"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getInt("unitPrice")
                    );
                    it.orderId = orderId;
                    items.add(it);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return items;
    }

    public boolean updateStatus(String orderId, String newStatus) {
        String sql = "update Orders set status=? where id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean markPaid(String orderId) {
        String sql = "update Orders set paid=1 where id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
