package ui;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JRadioButton rbCust, rbStaff, rbAdmin;

    public LoginFrame() {
        setTitle("Đăng nhập - MyFood");
        setSize(420, 220);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        build();
    }

    private void build() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        JPanel center = new JPanel(new GridLayout(4,2,6,6));

        rbCust = new JRadioButton("Khách", true);
        rbStaff = new JRadioButton("Nhân viên");
        rbAdmin = new JRadioButton("Admin");
        ButtonGroup bg = new ButtonGroup(); bg.add(rbCust); bg.add(rbStaff); bg.add(rbAdmin);
        JPanel rolePanel = new JPanel(); rolePanel.add(new JLabel("Vai trò:")); rolePanel.add(rbCust); rolePanel.add(rbStaff); rolePanel.add(rbAdmin);
        p.add(rolePanel, BorderLayout.NORTH);

        center.add(new JLabel("Tên đăng nhập:"));
        txtUser = new JTextField(); center.add(txtUser);
        center.add(new JLabel("Mật khẩu:"));
        txtPass = new JPasswordField(); center.add(txtPass);

        p.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.addActionListener(e -> doLogin());
        JButton btnGuest = new JButton("Bỏ qua (Khách)"); 
        btnGuest.addActionListener(e -> skipGuest());
        south.add(btnLogin); south.add(btnGuest);
        p.add(south, BorderLayout.SOUTH);

        add(p);
    }

    private void doLogin() {
        String role = rbCust.isSelected() ? "customer" : rbStaff.isSelected() ? "staff" : "admin";
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (role.equals("customer") && (user.isEmpty() && pass.isEmpty())) {
            skipGuest();
            return;
        }

        UserDAO dao = new UserDAO();
        User u = dao.authenticate(user, pass);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!u.role.equals(role)) {
            JOptionPane.showMessageDialog(this, "Vai trò không đúng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        openMain(u);
    }

    private void skipGuest() {
        String phone = JOptionPane.showInputDialog(this, "Nhập số điện thoại (khách vãng lai):");
        if (phone == null || phone.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cần số điện thoại để đăng nhập với khách vãng lai", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        models.User guest = new models.User(-1, "guest_" + phone, "Khách vãng lai (" + phone + ")", "customer", null, phone);
        openMain(guest);
    }

    private void openMain(User u) {
        SwingUtilities.invokeLater(() -> {
            TableLayoutFrame w = new TableLayoutFrame(u);
            w.setVisible(true);
        });
        dispose();
    }
}
