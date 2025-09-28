package models;

import java.util.Date;

public class Booking {
    public String id;
    public String customerName;
    public String phone;
    public int tableId;
    public Date bookingTime;
    public int partySize;
    public String status;

    public Booking(String id, String customerName, String phone, int tableId, Date bookingTime, int partySize, String status) {
        this.id = id;
        this.customerName = customerName;
        this.phone = phone;
        this.tableId = tableId;
        this.bookingTime = bookingTime;
        this.partySize = partySize;
        this.status = status;
    }
}
