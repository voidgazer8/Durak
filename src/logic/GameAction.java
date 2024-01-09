package logic;

import cards.Card;
import cards.Suit;
import java.util.*;

/**
 * класс описывает участок игры, при котором один игрок отбивается, а остальные ему подкидывают
 */
public class GameAction {
    private final String defender; //отбивающийся игрок
    private boolean defenceResult; //результат - отбился или нет
    private final Map<Card, Card> defenceMap = new HashMap<>(); //какая карта какой была покрыта

    //подкинутые другими игроками карты. Для дальнейшей распечатки важно, чтобы сохранялся порядок вставки, поэтому LinkedMap
    private final Map<String, List<Card>> attackers = new LinkedHashMap<>();

    public GameAction(String defender) {
        this.defender = defender;
    }

    /**
     * отбился игрок или нет
     *
     * @param defenceResult результат
     */
    public void setDefenceResult(boolean defenceResult) {
        this.defenceResult = defenceResult;
    }

    /**
     * добавить игрока как атакующего
     * @param attacker имя игрока
     */
    public void addAttacker(String attacker) {
        attackers.put(attacker, new ArrayList<>());
    }

    /**
     * подкинуть карту
     *
     * @param attacker кто подкидывает
     * @param card что подкидывает
     */
    public void addCardForAttack(String attacker, Card card) {
        if (isCardValidForAttack(card)) { //если допускается походить данной картой, она добавляется
            List<Card> old = attackers.get(attacker);
            old.add(card);
            attackers.put(attacker, old);
        }
    }

    /**
     * отбить карту
     *
     * @param defenceCard  карта для защиты
     * @param attackerCard кроющаяся карта
     * @param trumpSuit    козырная масть
     */
    public void addCardForDefence(Card defenceCard, Card attackerCard, Suit trumpSuit) {
        //если допускается отбиться данной картой, то подкинутая карта кроется
        if (isCardValidForDefence(defenceCard, attackerCard, trumpSuit)) {
            defenceMap.put(defenceCard, attackerCard);
        }
    }

    /**
     * проверить, можно ли подкинуть данную карту
     *
     * @param card карта
     * @return результат
     */
    private boolean isCardValidForAttack(Card card) {
        List<Card> fullCards = getCardsOnTable(); //получаем все карты

        if (fullCards.size() == 0) { //если стол пуст, очевидно, карта валидна для захода
            return true;
        }
        for (Card c : fullCards) {
            //игрок может подкидывать карты того же достоинства, что уже лежат на столе, поэтому если
            //хотя бы 1 раз условие ниже выполнено, то карта также валидна
            if (c.getRank().equals(card.getRank())) {
                return true;
            }
        }
        return false;
    }

    /**
     * получить все карты на столе в данный момент
     *
     * @return список карт (подкинутых и отбивающихся)
     */
    private List<Card> getCardsOnTable() {
        List<Card> cardsOnTable = new ArrayList<>(getAttackersCards());
        cardsOnTable.addAll(defenceMap.keySet());
        return cardsOnTable;
    }

    /**
     * получить подкинутые карты
     *
     * @return список карт
     */
    public List<Card> getAttackersCards() {
        List<Card> cards = new ArrayList<>();
        for (List<Card> list : attackers.values()) {
            cards.addAll(list);
        }
        return cards;
    }

    /**
     * проверить, можно ли отбиться данной картой
     *
     * @param defenderCard отбивающаяся карта
     * @param attackerCard подкинутая карта
     * @param trumpSuit козырная масть
     * @return результат
     */
    public static boolean isCardValidForDefence(Card defenderCard, Card attackerCard, Suit trumpSuit) {
        //Побить карту можно картой той же масти, но большего достоинства, или козырной картой.
        //Картой козырной масти можно побить любую карту некозырной масти.
        //Отбить козырь можно только козырем большего номинала
        return defenderCard.getRank().isHigherThan(attackerCard.getRank())
                && defenderCard.getSuit().equals(attackerCard.getSuit())
                || (defenderCard.isTrump(trumpSuit) && !attackerCard.isTrump(trumpSuit))
                || (defenderCard.isTrump(trumpSuit) && attackerCard.isTrump(trumpSuit)
                && defenderCard.getRank().isHigherThan(attackerCard.getRank()));
    }

    /**
     * получить номера карт игрока, которые он может теоретически подкинуть
     *
     * @param cards список карт
     * @return список номеров
     */
    public List<Integer> getCardsIndicesValidForAttack(List<Card> cards) {
        List<Integer> validIndices = new ArrayList<>();
        int i = 1;

        for (Card c : cards) {
            if (isCardValidForAttack(c)) {
                validIndices.add(i);
            }
            i++;
        }
        return validIndices;
    }

    /**
     * получить номера карт, которыми игрок может теоретически отбиться
     *
     * @param defenderCards список возможных карт
     * @param attackerCard  кроющаяся карта
     * @param trumpSuit козырная масть
     * @return список номеров
     */
    public static List<Integer> getCardsIndicesValidForDefence(List<Card> defenderCards, Card attackerCard, Suit trumpSuit) {
        List<Integer> validIndices = new ArrayList<>();
        int i = 1;

        for (Card c : defenderCards) {
            if (isCardValidForDefence(c, attackerCard, trumpSuit)) {
                validIndices.add(i);
            }
            i++;
        }
        return validIndices;
    }

    public String getDefender() {
        return defender;
    }

    public Map<String, List<Card>> getAttackers() {
        return attackers;
    }

    public boolean isDefenceSuccessful() {
        return defenceResult;
    }

    public Map<Card, Card> getDefenceMap() {
        return defenceMap;
    }
}
