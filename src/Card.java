public class Card {
    private final String value;// From Ace,2,3,4,.....King
    private final String cardType;// Heart Diamond Culb Spade
    private final String color;// red black

    //Constructor to set attributes
    public Card(String value, String cardType, String color) {
        this.value = value;
        this.cardType = cardType;
        this.color = color;
    }
    //getters
    public String getValue() {
        return value;
    }

    public String getCardType() {
        return cardType;
    }

    public String getColor() {
        return color;
    }

    //method to help in checking matching pairs
    public boolean isMatchingPair(Card otherCard) {
        return this.value == otherCard.value && this.color.equals(otherCard.color);
    }
    //method to Print the Card
    @Override
    public String toString() {
        return value + " of " + cardType ;
    }
}