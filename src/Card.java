public class Card {
    CardType type;
    String points;
    String symbols;
    String artName;
    String text;

    public Card(CardType type, String points, String symbols, String artName, String text) {
        this.type = type;
        this.points = points;
        this.symbols = symbols;
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

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
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
