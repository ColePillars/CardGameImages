public class Card {
    CardType type;
    Integer number;
    String points;
    String slots;
    String text;

    public Card(CardType type, Integer number, String points, String slots, String text) {
        this.type = type;
        this.number = number;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    public String getTypeString() {
        return type.equals(CardType.BAG) ? "B" : "C";
    }

    enum CardType {BAG, CHARM}
}
