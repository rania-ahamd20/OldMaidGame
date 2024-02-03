public class Card {
    private final String value;
    private final String cardType;
    private final String color;

    public Card(String value, String cardType, String color) {
        this.value = value;
        this.cardType = cardType;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public String getCardType() {
        return cardType;
    }

    public String getColor() {
        return color;
    }

    public boolean isMatchingPair(Card otherCard) {
        return this.value == otherCard.value && this.color.equals(otherCard.color);
    }
    @Override
    public String toString() {
        return value + " of " + cardType ;
    }
}