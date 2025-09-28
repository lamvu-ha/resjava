package models;

public class TableEntity {
    public int id;
    public String status; // empty, occupied, needs_service
    public String occupiedBy;

    public TableEntity(int id, String status, String occupiedBy) {
        this.id = id;
        this.status = status;
        this.occupiedBy = occupiedBy;
    }

    @Override
    public String toString() {
        return "Bàn " + id + " (" + status + ")";
    }
}
