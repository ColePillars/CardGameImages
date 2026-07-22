public class Card {
    CardType type;
    String name;
    String points;
    String slots;
    String text;

    public Card(CardType type, String name, String points, String slots, String text) {
        this.type = type;
        this.name = name;
        this.points = points;
        this.slots = slots;
        this.text = text;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

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

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    enum CardType {BAG, CHARM}
}
