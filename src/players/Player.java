package players;

import cards.Card;
import cards.Rank;
import cards.Suit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * класс описывает игрока-простейшего бота
 */

public class Player {

    public static final int DEFAULT_CARDS_NUMBER_PER_PLAYER = 6; //у игрока должно быть 6 карт
    private final List<Card> cards = new ArrayList<>(DEFAULT_CARDS_NUMBER_PER_PLAYER); //список карт
    private final String userIdentifier;


    /**
     * @param userIdentifier никнейм игрока
     */
    public Player(java.lang.String userIdentifier) {
        //при создании игрока имеется возможность присвоить пользовательское имя, под которым он будет действовать
        this.userIdentifier = userIdentifier;
    }

    public void giveCard(Card card) {
        cards.add(card);
    }

    public void giveCards(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public void removeCard(int index) {
        cards.remove(index - 1);
    }
    /**
     * узнать, сколько карт будет подкинуто
     * @param defenderHandSize размер руки отбивающегося
     * @param attackCount номер захода
     * @return случайное число
     */
    public static int howManyToDiscard(int defenderHandSize, int attackCount) {
        int n;
        if (attackCount == 0) {
            //если это первый заход, то подброшенных карт не может быть более 5
            n = new Random().nextInt(1, 5 + 1);
        } else {
            //иначе может быть подкинуто максимум 6 карт, и не более, чем карт в руке отбивающегося
            n = new Random().nextInt(1, defenderHandSize + 1);
            if (defenderHandSize > 6) {
                n = new Random().nextInt(1, 6 + 1);
            }
        }
        return n;
    }

    /**
     * узнать номер карты для сброса
     * @param validIndices номера карт, доступных для сброса
     * @return случайное число
     */
    public static int whatToDiscard(List<Integer> validIndices) {
        if (validIndices.size() == 0) {
            return -1; //если доступных карт нет, то игрок ничего не подкидывает
        }
        int n = new Random().nextInt(0, validIndices.size());
        return validIndices.get(n);
    }

    /**
     * вернет минимальное достоинство среди всех карт игрока с переданной мастью
     *
     * @param suit масть
     * @return низшее достоинство
     */
    public Rank getLowestRankOfSuit(Suit suit) {
        Function<Card, Rank> getRank = Card::getRank;
        Comparator<Card> comparator = Comparator.comparing(getRank.andThen(Rank::ordinal));

        Card c = cards.stream().filter(x -> x.getSuit().equals(suit)).min(comparator).orElse(null);
        return c == null ? null : c.getRank(); //карта может быть null, если у игрока в принципе нет карт с такой мастью
    }

    public List<Card> getCards() {
        return cards;
    }

    public Card getCard(int index) {
        return cards.get(index - 1);
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }
}
