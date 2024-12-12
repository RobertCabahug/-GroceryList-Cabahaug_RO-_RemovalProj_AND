package Model;

public class GroceryItem {
    private String name;
    private String description;
    private String id;

    public GroceryItem(String name, String description, String id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }


    public String getDescription() {
        return description;
    }
}

