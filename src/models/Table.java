// models/Table.java
package models;

public class Table {
    public int id;
    public String status;
    public String occupiedBy;

    public Table(int id, String status, String occupiedBy) {
        this.id = id;
        this.status = status;
        this.occupiedBy = occupiedBy;
    }
}
