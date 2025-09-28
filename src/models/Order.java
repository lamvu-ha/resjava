package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    public String id;
    public int tableId;
    public int total;
    public String status;
    public Date createdAt;
    public boolean paid;
    public List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public static class OrderItem {
        public int id; // orderItem id (optional)
        public String orderId;
        public int menuId;
        public String name;
        public int quantity;
        public int unitPrice;

        public OrderItem(int menuId, String name, int quantity, int unitPrice) {
            this.menuId = menuId;
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int subtotal() { return quantity * unitPrice; }
    }
}
