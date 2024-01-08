package packs;

import cards.Card;
import cards.Rank;
import cards.Suit;

import java.util.*;

/**
 * класс описывает основную колоду, которая тасуется и с которой сдаются карты игрокам
 */
public class BasicPack {
    private final LinkedList<Card> deckOfCards = new LinkedList<>();
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
            Card upperOne = deckOfCards.pollLast(); //снимаем верхнюю карту
            deckOfCards.addFirst(upperOne); //и перемещаем в низ стопки (начало списка = низ)
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
        Card card = deckOfCards.pollLast();
        lastDealtCard = card;
        return card;
    }

    public int getSize() {
        return deckOfCards.size();
    }

    public Card getTrumpCard() {
        return trumpCard;
    }
}
