package ui;

import dao.OrderDAO;
import dao.TableDAO;
import models.Order;
import models.TableEntity;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StaffTableWindow extends JFrame {
    private final TableEntity table;
    private final User user;
    private final TableDAO tableDAO = new TableDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    final TableLayoutFrame parent;

    private DefaultListModel<String> listModel;
    private List<Order> orders;

    public StaffTableWindow(User user, TableEntity table, TableLayoutFrame parent) {
        this.user = user;
        this.table = table;
        this.parent = parent;
        setTitle("Staff - Bàn " + table.id);
        setSize(1080, 1080);
        setLocationRelativeTo(null);
        build();
    }

    private void build() {
        listModel = new DefaultListModel<>();
        JList<String> orderList = new JList<>(listModel);
        orderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        refreshOrders();

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshOrders());

        JButton btnDetail = new JButton("Mở chi tiết");
        btnDetail.addActionListener(e -> {
            int sel = orderList.getSelectedIndex();
            if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn đơn"); return; }
            Order o = orders.get(sel);
            new OrderDetailWindow(o, this).setVisible(true);
        });

        JButton btnPreparing = new JButton("Chuyển -> Preparing");
        btnPreparing.addActionListener(e -> changeStatus(orderList, "Preparing"));

        JButton btnReady = new JButton("Chuyển -> Ready");
        btnReady.addActionListener(e -> changeStatus(orderList, "Ready"));

        JButton btnServed = new JButton("Chuyển -> Served");
        btnServed.addActionListener(e -> changeStatus(orderList, "Served"));

        JButton btnPay = new JButton("Thanh toán (mock)");
        btnPay.addActionListener(e -> {
            int sel = orderList.getSelectedIndex();
            if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn đơn"); return; }
            Order o = orders.get(sel);
            boolean ok = orderDAO.markPaid(o.id);
            if (ok) {
                // free table when paid
                tableDAO.updateStatus(table.id, "empty", null);
                JOptionPane.showMessageDialog(this, "Đã thanh toán và giải phóng bàn");
                refreshOrders();
                parent.refreshTables();
            } else JOptionPane.showMessageDialog(this, "Lỗi thanh toán");
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(btnRefresh);
        top.add(btnDetail);
        top.add(btnPreparing);
        top.add(btnReady);
        top.add(btnServed);
        top.add(btnPay);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(orderList), BorderLayout.CENTER);
    }

    public void refreshOrders() {
        listModel.clear();
        orders = orderDAO.getOrdersByTable(table.id);
        for (Order o : orders) {
            listModel.addElement(o.id + " | Tổng: " + o.total + " VND | " + o.status + " | paid:" + o.paid);
        }
    }

    private void changeStatus(JList<String> orderList, String newStatus) {
        int sel = orderList.getSelectedIndex();
        if (sel < 0) { JOptionPane.showMessageDialog(this, "Chọn đơn"); return; }
        Order o = orders.get(sel);
        boolean ok = orderDAO.updateStatus(o.id, newStatus);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái -> " + newStatus);
            refreshOrders();
        } else JOptionPane.showMessageDialog(this, "Lỗi cập nhật");
    }
}
