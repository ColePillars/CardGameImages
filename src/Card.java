public class Card {
    CardType type;
    String points;
    String slots;
    String artName;
    String text;

    public Card(CardType type, String points, String slots, String artName, String text) {
        this.type = type;
        this.points = points;
        this.slots = slots;
        this.artName = artName;
        this.text = text;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public String getArtName() {
        return artName;
    }

    public void setArtName(String artName) {
        this.artName = artName;
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
