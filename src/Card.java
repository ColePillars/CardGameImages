public class Card {
    private CardType type;
    private String points;
    private String symbols;
    private String artName;
    private String cardName;
    private String nameFontSize;
    private String text;
    private String wrapLength;
    private String fontSize;

    public Card(
            CardType type,
            String points,
            String symbols,
            String artName,
            String cardName,
            String nameFontSize,
            String text,
            String wrapLength,
            String fontSize) {
        this.type = type;
        this.points = points;
        this.symbols = symbols;
        this.artName = artName;
        this.cardName = cardName;
        this.nameFontSize = nameFontSize;
        this.text = text;
        this.wrapLength = wrapLength;
        this.fontSize = fontSize;
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

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getNameFontSize() {
        return nameFontSize;
    }

    public void setNameFontSize(String nameFontSize) {
        this.nameFontSize = nameFontSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getWrapLength() {
        return wrapLength;
    }

    public void setWrapLength(String wrapLength) {
        this.wrapLength = wrapLength;
    }

    public String getTypeString() {
        if (type.equals(CardType.BAG)) {
            return "B";
        } else if (type.equals(CardType.CHARM)) {
            return "C";
        } else {
            return "R";
        }
    }

    public static CardType getTypeFromString(String string) {
        if (string.equals("B")) {
            return CardType.BAG;
        } else if (string.equals("C")) {
            return CardType.CHARM;
        } else if (string.equals("O")) {
            return CardType.OTHER;
        } else {
            return CardType.UNKNOWN;
        }
    }

    enum CardType {BAG, CHARM, OTHER, UNKNOWN}
}
