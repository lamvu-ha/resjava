package ui;

import dao.TableDAO;
import models.TableEntity;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TableLayoutFrame extends JFrame {
    private final User user;
    private final TableDAO tableDAO = new TableDAO();
    private JPanel gridPanel;

    public TableLayoutFrame(User user) {
        this.user = user;
        setTitle("Bản đồ bàn - " + user.name + " (" + user.role + ")");
        setSize(1080, 1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        build();
    }

    private void build() {
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Xin chào: " + user.name + "  |  Vai trò: " + user.role), BorderLayout.WEST);
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        top.add(btnLogout, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(4,5,12,12));
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        refreshTables();
    }

    public void refreshTables() {
        gridPanel.removeAll();
        List<TableEntity> tables = tableDAO.getAll();
        for (TableEntity t : tables) {
            JButton b = new JButton("<html><center>Bàn " + t.id + "<br/>" + t.status + "</center></html>");
            b.setPreferredSize(new Dimension(120, 80));
            switch (t.status) {
                case "empty": b.setBackground(new Color(144, 238, 144)); break;
                case "occupied": b.setBackground(new Color(240, 128, 128)); break;
                case "needs_service": b.setBackground(new Color(255, 215, 0)); break;
                default: b.setBackground(Color.LIGHT_GRAY);
            }
            b.addActionListener(e -> onTableClicked(t));
            gridPanel.add(b);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void onTableClicked(TableEntity t) {
        if ("customer".equals(user.role)) {
            // if empty -> occupy then open ordering
            if ("empty".equalsIgnoreCase(t.status)) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có muốn chọn Bàn " + t.id + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tableDAO.updateStatus(t.id, "occupied", user.username);
                    refreshTables();
                    new OrderWindow(user, t).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Bàn đang không trống: " + t.status);
            }
        } else {
            // staff/admin view
            new StaffTableWindow(user, t, this).setVisible(true);
        }
    }
}
