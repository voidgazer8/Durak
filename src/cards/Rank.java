package cards;

/**
 * enum-описание достоинства отдельной карты (9 возможных)
 */
public enum Rank {

    SIX("6"), //шестерка
    SEVEN("7"), //семерка
    EIGHT("8"), //восьмерка
    NINE("9"), //девятка
    TEN("10"), //десятка
    JACK("J"), //валет
    QUEEN("Q"), //дама
    KING("K"), //король
    ACE("A"); //туз

    private final String info; //строковый параметр ранга карты

    Rank(String info) {
        this.info = info;
    }

    /**
     * @param rankToCompare достоинство карты для сравнения
     * @return является ли достоинство текущей карты ниже достоинства переданной
     */
    public boolean isLowerThan(Rank rankToCompare) {
        return this.ordinal() < rankToCompare.ordinal();
    }

    /**
     * @param rankToCompare достоинство карты для сравнения
     * @return является ли достоинство текущей карты выше достоинства переданной
     */
    public boolean isHigherThan(Rank rankToCompare) {
        return this.ordinal() > rankToCompare.ordinal();
    }

    public String getInfo() {
        return info;
    }
}
