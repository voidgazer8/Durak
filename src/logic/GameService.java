package logic;

import cards.Card;
import cards.Rank;
import cards.Suit;
import players.Player;

import java.util.*;

/**
 * класс содержит несколько дополнительных методов
 */
public class GameService {

    /**
     * получить минимальные номиналы с определенной мастью для игроков
     *
     * @param suit масть
     * @return массив номиналов
     */
    public static Rank[] collectMinRanksOfPlayers(Suit suit, List<Player> players) {
        return players.stream()
                .map(x -> x.getLowestRankOfSuit(suit))
                .toArray(Rank[]::new);
    }

    /**
     * получить индекс игрока, у которого имеется общий минимальный номинал карты
     *
     * @param ranksForPlayers минимальные номиналы для каждого из игроков
     * @return индекс
     */
    public static int getPlayerIndexWithGeneralMinRank(Rank[] ranksForPlayers) {
        Rank rank = Arrays.stream(ranksForPlayers)
                .filter(Objects::nonNull)
                .min(Comparator.comparing(Rank::ordinal))
                .orElse(null);

        int i = 1;
        for (Rank current : ranksForPlayers) {
            if (current != null && current.equals(rank)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * получить индексы подкидывающих игроков по часовой стрелке, учитывая, кто ходит первым
     *
     * @param defenderIndex индекс отбивающегося
     * @return список индексов
     */
    public static List<Integer> collectAttackersIndices(int defenderIndex, int playersNumber) {
        List<Integer> attackers = new ArrayList<>();
        for (int i = 1; i <= playersNumber; i++) {
            if (i == defenderIndex) {
                continue;
            }
            attackers.add(i);
        }
        int firstAttacker = defenderIndex - 1;

        if (defenderIndex == 1) {
            firstAttacker = playersNumber;
        }

        List<Integer> subList = new ArrayList<>(attackers.subList(attackers.indexOf(firstAttacker), attackers.size()));
        attackers.removeAll(subList);
        subList.addAll(attackers);

        return subList;
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
}
