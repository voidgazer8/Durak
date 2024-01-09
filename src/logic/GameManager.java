package logic;

import cards.Card;
import cards.Rank;
import packs.BasicPack;
import players.Player;
import players.PlayerService;

import java.util.*;

/**
 * класс описывает основную логику игры в подкидного дурака
 */
public class GameManager {

    private BasicPack pack; //базовая колода
    private int firstTurnPlayerIndex; //индекс игрока с правом первого хода
    private int howManyPlayers;  //стартовое число игроков
    private String loser; //имя проигравшего
    private final List<String> winners = new ArrayList<>(); //имена победивших
    private List<Player> players; //игроки
    private int savedDefenderIndex;

    public GameManager(int randomPlayersNumber) {
        setUpBasicPack();
        setUpPlayers(randomPlayersNumber);
        findTrumpCard();
        dealCards();
        findFirstTurnPlayerIndex();
    }

    /**
     * сформировать первоначачальную колоду и перетасовать
     */
    private void setUpBasicPack() {
        pack = new BasicPack();
        pack.shuffle();
    }

    /**
     * зарегистрировать игроков с идентификаторами-порядковыми номерами
     *
     * @param howManyPlayers кол-во игроков
     */
    private void setUpPlayers(int howManyPlayers) {
        this.howManyPlayers = howManyPlayers;

        players = new ArrayList<>(howManyPlayers);
        for (int i = 1; i <= howManyPlayers; i++) {
            players.add(new Player(String.valueOf(i)));
        }
    }

    /**
     * раздать карты
     */
    private void dealCards() {
        for (int i = 0; i < Player.DEFAULT_CARDS_NUMBER_PER_PLAYER; i++) {
            for (int j = 1; j <= howManyPlayers; j++) {
                Player currentPlayer = players.get(j - 1);
                currentPlayer.giveCard(pack.getUpperOne());
            }
        }
    }

    /**
     * найти козырную карту
     */
    private void findTrumpCard() {
        pack.findTrumpCard();
    }

    /**
     * узнать, кто ходит первым
     */
    private void findFirstTurnPlayerIndex() {
        Rank[] minRanks = GameService.collectMinRanksOfPlayers(pack.getTrumpCard().getSuit(), players);

        firstTurnPlayerIndex = GameService.getPlayerIndexWithGeneralMinRank(minRanks);
        //на этом этапе либо найдено наименьшее достоинство козырной масти и существует игрок с
        //такой картой, либо нет (ни у кого нет такой масти)

        if (firstTurnPlayerIndex == -1) { //2й случай
            firstTurnPlayerIndex = new Random().nextInt(1, howManyPlayers + 1);
        }
    }

    /**
     * запустить игру
     *
     * @return список GameAction
     */
    public List<GameAction> run() {
        //список totalActions содержит все GameAction, которые были сформированы на всех этапах игры.
        //Под этапом игры подразумевается её отрезок, в течение которого выявляется победитель - игрок,
        //оставшийся без карт. В зависимости от числа игроков, кол-во этапов будет разным. Например, для 2-х
        //игроков будет только 1 этап, так как сразу выявляется победитель и проигравший, либо ничья. Для 3-х игроков
        //будет 2 этапа: на 1-ом выявится 1-й победитель (1 место), который выбывает из игры, на 2-ом - 2-й
        //победитель (2 место) и проигравший. Такой принцип действует для любого допустимого кол-ва игроков.
        List<GameAction> totalActions = new ArrayList<>();
        for (int i = 1; i <= howManyPlayers - 1; i++) {
            List<GameAction> currentActions = start(i);
            totalActions.addAll(currentActions);
        }

        findLoser();

        return totalActions;
    }

    /**
     * запустить игру для текущего этапа
     *
     * @param stepCounter счетчик
     * @return список GameAction текущего этапа
     */
    private List<GameAction> start(int stepCounter) {
        List<GameAction> actions = new ArrayList<>();

        //отбивающийся игрок будет на 1 позицию правее от того, кто ходит первым.
        //Считается, что игроков от 1 до n и 1 -> n является движением по часовой стрелке
        int defenderIndex = firstTurnPlayerIndex + 1;
        int attackCount = 0; //счетчик заходов

        if (stepCounter != 1) {
            defenderIndex = savedDefenderIndex;
        }

        while (true) {
            //обработка ситуаций, когда индекс отбивающегося вышел за границы
            if (defenderIndex == players.size() + 1) {
                defenderIndex = 1;
            }
            if (defenderIndex == players.size() + 2) {
                defenderIndex = 2;
            }

            //получение индексов подкидывающих игроков
            List<Integer> attackers = GameService.collectAttackersIndices(defenderIndex, players.size());

            //создание нового игрового события, передаем имя отбивающегося игрока
            GameAction action = new GameAction(players.get(defenderIndex - 1).getUserIdentifier());
            //результат защиты, в дальнейшем может измениться
            boolean isDefenceSuccessful = true;

            for (int attackerIndex : attackers) { //обход подкидывающих
                //получение копий для более удобной работы
                Player defender = players.get(defenderIndex - 1);
                Player attacker = players.get(attackerIndex - 1);

                //добавление в событие текущего игрока как подкидывающего
                action.addAttacker(attacker.getUserIdentifier());

                //проверка на ничью
                if (isDraw(defender, attacker)) {
                    return actions;
                }

                //проверка на то, избавился ли кто-нибудь от карт
                int[] ints = new int[]{defenderIndex, attackerIndex};
                for (int i : ints) {
                    if (isWinner(i)) {
                        winners.add(players.get(i - 1).getUserIdentifier());
                        players.remove(i - 1);
                        savedDefenderIndex = defenderIndex;
                        return actions;
                    }
                }

                //спрашиваем у игрока, сколько карт он хочет подкинуть, при этом передав номер захода и размер
                //руки отбивающегося, чтобы соблюсти правила
                int numberOfCardsToDiscard = PlayerService.howManyToDiscard(defender.getCards().size(), attackCount);
                attackCount++;

                for (int i = 0; i < numberOfCardsToDiscard; i++) {
                    Card attackerCard;

                    //спрашиваем у игрока, какую в данный момент карту он хочет подкинуть
                    int attackCardIndex = PlayerService.whatToDiscard(action.getCardsIndicesValidForAttack(attacker.getCards()));
                    if (attackCardIndex == -1) {
                        break; //не подкидывает
                    } else {
                        //иначе извлекаем подкинутую карту у игрока и добавляем на стол
                        attackerCard = attacker.getCard(attackCardIndex);
                        action.addCardForAttack(attacker.getUserIdentifier(), attackerCard);
                        players.get(attackerIndex - 1).removeCard(attackCardIndex);
                    }

                    //спрашиваем у отбивающегося, какой картой он планирует крыть подкинутую карту
                    int defenceCardIndex = PlayerService.whatToDiscard(
                            GameService.getCardsIndicesValidForDefence(defender.getCards(), attackerCard, pack.getTrumpCard().getSuit()));

                    if (defenceCardIndex == -1) {
                        isDefenceSuccessful = false; //отбиться не смог, переход к следующему подкидывающему - вдогонку
                        break;
                    } else {
                        //иначе извлекаем карту у игрока и добавляем на стол для защиты
                        action.addCardForDefence(defender.getCard(defenceCardIndex), attackerCard, pack.getTrumpCard().getSuit());
                        players.get(defenderIndex - 1).removeCard(defenceCardIndex);
                    }
                }
            }
            action.setDefenceResult(isDefenceSuccessful); //устанавливаем итог события - отбился игрок или нет
            actions.add(action); //добавляем в список событий

            if (isDefenceSuccessful) {
                //если игрок успешно отбился, то он ходит следующим. Тогда далее отбиваться будет тот, кто дальше него
                //по часовой стрелке, соответственно его индекс на 1 больше
                defenderIndex++;
                rechargePlayers(); //игроки добирают карты до полной руки
            } else {
                //если игрок отбиться не смог, то принимает все подкинутые карты, которые были сохранены.
                //Тогда ход переходит к тому, кто дальше него по часовой стрелке, и отбиваться, соответственно,
                //будет тот, чей индекс на 2 больше
                players.get(defenderIndex - 1).giveCards(action.getAttackersCards());
                defenderIndex += 2;
            }
        }
    }

    /**
     * выдать игрокам дополнительные карты
     */
    private void rechargePlayers() {
        block:
        for (Player player : players) {
            while (player.getCards().size() < 6) {
                if (pack.getSize() != 0) {
                    player.giveCard(pack.getUpperOne());
                } else {
                    break block;
                }
            }
        }
    }

    /**
     * проверить на ничью
     *
     * @param defender отбивающийся
     * @param attacker подкидывающий
     * @return результат
     */
    private boolean isDraw(Player defender, Player attacker) {
        return howManyPlayers == 2 && defender.getCards().size() == 0 && attacker.getCards().size() == 0;
    }

    /**
     * проверить, выиграл ли игрок
     *
     * @param playerIndex индекс
     * @return результат
     */
    private boolean isWinner(int playerIndex) {
        return players.get(playerIndex - 1).getCards().size() == 0;
    }

    /**
     * определить проигравшего
     */
    private void findLoser() {
        for (Player p : players) {
            if (!winners.contains(p.getUserIdentifier())) {
                loser = p.getUserIdentifier();
                break;
            }
        }
    }

    public Card getTrumpCard() {
        return pack.getTrumpCard();
    }

    public List<String> getWinners() {
        return winners;
    }

    public String getLoser() {
        return loser;
    }

    public int getFirstTurnPlayerIndex() {
        return firstTurnPlayerIndex;
    }

    public int getPlayersNumber() {
        return howManyPlayers;
    }
}
