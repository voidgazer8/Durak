package packs;

import cards.Card;
import cards.Rank;
import cards.Suit;

import java.util.*;

/**
 * класс описывает основную колоду, которая тасуется и с которой сдаются карты игрокам
 */
public class BasicPack {
    private final ArrayList<Card> deckOfCards = new ArrayList<>();
    private Card trumpCard, lastDealtCard; //козырная и последняя сданная карты

    public BasicPack() {
        //при создании колоды происходит заполнение её 36 картами (4 * 9)
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deckOfCards.add(new Card(suit, rank));
            }
        }
    }

    /**
     * нахождение козырной карты
     */
    public void findTrumpCard() {
        try {
            Card upperOne = pollLast(); //снимаем верхнюю карту
            deckOfCards.add(0, upperOne); //и перемещаем в низ стопки (начало списка = низ)
            trumpCard = upperOne;
        } catch (NoSuchElementException ex) {
            //если все карты розданы и в колоде ничего не осталось, в козырные уходят параметры последней отданной карты
            trumpCard = lastDealtCard;
        }
    }

    /**
     * перетасовка колоды
     */
    public void shuffle() {
        Collections.shuffle(deckOfCards);
    }

    /**
     * съем верхней карты
     *
     * @return верхняя карта
     */
    public Card getUpperOne() {
        Card upperOne = pollLast();
        lastDealtCard = upperOne;
        return upperOne;
    }

    private Card pollLast() {
        int last = deckOfCards.size() - 1;
        Card upperOne = deckOfCards.get(last);
        deckOfCards.remove(last);
        return upperOne;
    }

    public int getSize() {
        return deckOfCards.size();
    }

    public Card getTrumpCard() {
        return trumpCard;
    }
}
