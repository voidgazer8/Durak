package cards;

/**
 * enum-описание масти отдельной карты (4 возможных)
 */
public enum Suit {

    HEARTS('\u2665'), //черви
    CLUBS('\u2663'),  //трефы
    DIAMONDS('\u2666'), //бубны
    SPADES('\u2660');  //пики

    private final char symbol;  //в зависимости от масти имеются разные символы

    Suit(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
}
