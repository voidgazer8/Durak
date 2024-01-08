package cards;

/**
 * класс описывает карту
 */
public class Card {
    private final Rank rank; //достоинство
    private final Suit suit; //масть

    public Card(Suit suit, Rank rank) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * узнать, является ли карта козырной
     *
     * @param trumpSuit масть козырной карты
     * @return да/нет
     */
    public boolean isTrump(Suit trumpSuit) {
        return suit.equals(trumpSuit);
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }
}
