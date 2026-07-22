public class Card {
    String name;
    String slots;
    Integer points;
    String text;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public Card(String name, String slots) {
        this.name = name;
        this.slots = slots;
    }

}
