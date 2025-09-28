package ui;

import dao.MenuDAO;
import dao.OrderDAO;
import dao.TableDAO;
import models.MenuItem;
import models.Order;
import models.TableEntity;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class OrderWindow extends JFrame {
    private final User user;
    private final TableEntity table;
    private final MenuDAO menuDAO = new MenuDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final TableDAO tableDAO = new TableDAO();

    private JList<String> menuList;
    private DefaultListModel<String> listModel;
    private List<MenuItem> menuItems;
   // selected: menuId -> qty
    private final Map<Integer, Integer> cart = new LinkedHashMap<>();

    public OrderWindow(User user, TableEntity table) {
        this.user = user;
        this.table = table;
        setTitle("Đặt món - Bàn " + table.id);
        setSize(600, 420);
        setLocationRelativeTo(null);
        build();
    }

    private void build() {
        menuItems = menuDAO.getAll();
        listModel = new DefaultListModel<>();
        for (MenuItem m : menuItems) listModel.addElement(m.toString());
        menuList = new JList<>(listModel);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel left = new JPanel(new BorderLayout());
        left.add(new JLabel("Thực đơn:"), BorderLayout.NORTH);
        left.add(new JScrollPane(menuList), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        DefaultListModel<String> cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        right.add(new JLabel("Giỏ hàng:"), BorderLayout.NORTH);
        right.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(4,1,6,6));
        JButton btnAdd = new JButton("Thêm vào giỏ");
        btnAdd.addActionListener(e -> {
            int idx = menuList.getSelectedIndex();
            if (idx < 0) { JOptionPane.showMessageDialog(this, "Chọn món trước"); return; }
            MenuItem m = menuItems.get(idx);
            String s = JOptionPane.showInputDialog(this, "Số lượng cho " + m.name + ":", "1");
            if (s == null) return;
            int q;
            try { q = Integer.parseInt(s); if (q <= 0) throw new NumberFormatException(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ"); return; }
            cart.put(m.id, cart.getOrDefault(m.id,0) + q);
            refreshCartModel(cartModel);
        });

        JButton btnRemove = new JButton("Xóa mục");
        btnRemove.addActionListener(e -> {
            int sel = cartList.getSelectedIndex();
            if (sel < 0) return;
            Integer key = new ArrayList<>(cart.keySet()).get(sel);
            cart.remove(key);
            refreshCartModel(cartModel);
        });

        JButton btnPlace = new JButton("Đặt món");
        btnPlace.addActionListener(evt -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng");
                return;
            }
            List<Order.OrderItem> items = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int menuId = entry.getKey();
                int qty = entry.getValue();
                MenuItem m = menuItems.stream().filter(mi -> mi.id == menuId).findFirst().orElse(null);
                if (m != null) {
                    items.add(new Order.OrderItem(m.id, m.name, qty, m.price));
                }
            }
            String createdId = orderDAO.createOrder(table.id, items);
            if (createdId != null) {
                JOptionPane.showMessageDialog(this, "Đặt món thành công. Mã đơn: " + createdId);
                tableDAO.updateStatus(table.id, "occupied", user.username);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đặt món thất bại");
            }
        });


        JButton btnCancel = new JButton("Hủy & Trả bàn");
        btnCancel.addActionListener(e -> {
            int conf = JOptionPane.showConfirmDialog(this, "Xác nhận hủy và giải phóng bàn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                tableDAO.updateStatus(table.id, "empty", null);
                JOptionPane.showMessageDialog(this, "Đã trả bàn " + table.id);
                dispose();
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnRemove);
        btnPanel.add(btnPlace);
        btnPanel.add(btnCancel);

        JPanel center = new JPanel(new BorderLayout(8,8));
        center.add(left, BorderLayout.WEST);
        center.add(right, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.EAST);

        add(center);
    }

    private void refreshCartModel(DefaultListModel<String> cartModel) {
        cartModel.clear();
        MenuDAO mdao = new MenuDAO();
        int total = 0;
        for (Map.Entry<Integer,Integer> e : cart.entrySet()) {
            MenuItem m = mdao.getById(e.getKey());
            if (m != null) {
                cartModel.addElement(m.name + " x" + e.getValue() + " = " + (m.price * e.getValue()) + " VND");
                total += m.price * e.getValue();
            }
        }
        cartModel.addElement("--------------");
        cartModel.addElement("Tổng: " + total + " VND");
    }
}
