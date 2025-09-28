package models;

public class MenuItem {
    public int id;
    public String name;
    public int price;
    public String category;
    public String description;

    public MenuItem(int id, String name, int price, String category, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " - " + price + " VND";
    }
}
