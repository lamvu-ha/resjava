package ui;

import dao.OrderDAO;
import models.Order;

import javax.swing.*;
import java.awt.*;

public class OrderDetailWindow extends JFrame {
    private final Order order;
    private final StaffTableWindow parent;

    public OrderDetailWindow(Order order, StaffTableWindow parent) {
        this.order = order;
        this.parent = parent;
        setTitle("Chi tiết đơn " + order.id);
        setSize(420, 360);
        setLocationRelativeTo(null);
        build();
    }

    private void build() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        for (Order.OrderItem it : order.items) {
            sb.append(it.name).append(" x").append(it.quantity).append(" = ")
              .append(it.unitPrice * it.quantity).append(" VND\n");
        }
        sb.append("\nTổng: ").append(order.total).append(" VND\n");
        sb.append("Trạng thái: ").append(order.status).append("\n");
        sb.append("Đã thanh toán: ").append(order.paid).append("\n");
        area.setText(sb.toString());

        JButton btnMarkPaid = new JButton("Đánh dấu đã thanh toán");
        btnMarkPaid.addActionListener(e -> {
            OrderDAO dao = new OrderDAO();
            boolean ok = dao.markPaid(order.id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã đánh dấu đã thanh toán");
                dispose();
                parent.refreshOrders();
                parent.parent.refreshTables();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi");
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(btnMarkPaid);

        add(new JScrollPane(area), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}
