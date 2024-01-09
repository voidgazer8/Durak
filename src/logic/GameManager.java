package logic;

import cards.Card;
import cards.Rank;
import packs.BasicPack;
import players.Player;
import players.PlayerService;

import java.util.*;

/**
 * ����� ��������� �������� ������ ���� � ���������� ������
 */
public class GameManager {

    private BasicPack pack; //������� ������
    private int firstTurnPlayerIndex; //������ ������ � ������ ������� ����
    private int howManyPlayers;  //��������� ����� �������
    private String loser; //��� ������������
    private final List<String> winners = new ArrayList<>(); //����� ����������
    private List<Player> players; //������
    private int savedDefenderIndex;

    public GameManager(int randomPlayersNumber) {
        setUpBasicPack();
        setUpPlayers(randomPlayersNumber);
        findTrumpCard();
        dealCards();
        findFirstTurnPlayerIndex();
    }

    /**
     * ������������ ���������������� ������ � ������������
     */
    private void setUpBasicPack() {
        pack = new BasicPack();
        pack.shuffle();
    }

    /**
     * ���������������� ������� � ����������������-����������� ��������
     *
     * @param howManyPlayers ���-�� �������
     */
    private void setUpPlayers(int howManyPlayers) {
        this.howManyPlayers = howManyPlayers;

        players = new ArrayList<>(howManyPlayers);
        for (int i = 1; i <= howManyPlayers; i++) {
            players.add(new Player(String.valueOf(i)));
        }
    }

    /**
     * ������� �����
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
     * ����� �������� �����
     */
    private void findTrumpCard() {
        pack.findTrumpCard();
    }

    /**
     * ������, ��� ����� ������
     */
    private void findFirstTurnPlayerIndex() {
        Rank[] minRanks = GameService.collectMinRanksOfPlayers(pack.getTrumpCard().getSuit(), players);

        firstTurnPlayerIndex = GameService.getPlayerIndexWithGeneralMinRank(minRanks);
        //�� ���� ����� ���� ������� ���������� ����������� �������� ����� � ���������� ����� �
        //����� ������, ���� ��� (�� � ���� ��� ����� �����)

        if (firstTurnPlayerIndex == -1) { //2� ������
            firstTurnPlayerIndex = new Random().nextInt(1, howManyPlayers + 1);
        }
    }

    /**
     * ��������� ����
     *
     * @return ������ GameAction
     */
    public List<GameAction> run() {
        //������ totalActions �������� ��� GameAction, ������� ���� ������������ �� ���� ������ ����.
        //��� ������ ���� ��������������� � �������, � ������� �������� ���������� ���������� - �����,
        //���������� ��� ����. � ����������� �� ����� �������, ���-�� ������ ����� ������. ��������, ��� 2-�
        //������� ����� ������ 1 ����, ��� ��� ����� ���������� ���������� � �����������, ���� �����. ��� 3-� �������
        //����� 2 �����: �� 1-�� �������� 1-� ���������� (1 �����), ������� �������� �� ����, �� 2-�� - 2-�
        //���������� (2 �����) � �����������. ����� ������� ��������� ��� ������ ����������� ���-�� �������.
        List<GameAction> totalActions = new ArrayList<>();
        for (int i = 1; i <= howManyPlayers - 1; i++) {
            List<GameAction> currentActions = start(i);
            totalActions.addAll(currentActions);
        }

        findLoser();

        return totalActions;
    }

    /**
     * ��������� ���� ��� �������� �����
     *
     * @param stepCounter �������
     * @return ������ GameAction �������� �����
     */
    private List<GameAction> start(int stepCounter) {
        List<GameAction> actions = new ArrayList<>();

        //������������ ����� ����� �� 1 ������� ������ �� ����, ��� ����� ������.
        //���������, ��� ������� �� 1 �� n � 1 -> n �������� ��������� �� ������� �������
        int defenderIndex = firstTurnPlayerIndex + 1;
        int attackCount = 0; //������� �������

        if (stepCounter != 1) {
            defenderIndex = savedDefenderIndex;
        }

        while (true) {
            //��������� ��������, ����� ������ ������������� ����� �� �������
            if (defenderIndex == players.size() + 1) {
                defenderIndex = 1;
            }
            if (defenderIndex == players.size() + 2) {
                defenderIndex = 2;
            }

            //��������� �������� ������������� �������
            List<Integer> attackers = GameService.collectAttackersIndices(defenderIndex, players.size());

            //�������� ������ �������� �������, �������� ��� ������������� ������
            GameAction action = new GameAction(players.get(defenderIndex - 1).getUserIdentifier());
            //��������� ������, � ���������� ����� ����������
            boolean isDefenceSuccessful = true;

            for (int attackerIndex : attackers) { //����� �������������
                //��������� ����� ��� ����� ������� ������
                Player defender = players.get(defenderIndex - 1);
                Player attacker = players.get(attackerIndex - 1);

                //���������� � ������� �������� ������ ��� ��������������
                action.addAttacker(attacker.getUserIdentifier());

                //�������� �� �����
                if (isDraw(defender, attacker)) {
                    return actions;
                }

                //�������� �� ��, ��������� �� ���-������ �� ����
                int[] ints = new int[]{defenderIndex, attackerIndex};
                for (int i : ints) {
                    if (isWinner(i)) {
                        winners.add(players.get(i - 1).getUserIdentifier());
                        players.remove(i - 1);
                        savedDefenderIndex = defenderIndex;
                        return actions;
                    }
                }

                //���������� � ������, ������� ���� �� ����� ���������, ��� ���� ������� ����� ������ � ������
                //���� �������������, ����� �������� �������
                int numberOfCardsToDiscard = PlayerService.howManyToDiscard(defender.getCards().size(), attackCount);
                attackCount++;

                for (int i = 0; i < numberOfCardsToDiscard; i++) {
                    Card attackerCard;

                    //���������� � ������, ����� � ������ ������ ����� �� ����� ���������
                    int attackCardIndex = PlayerService.whatToDiscard(action.getCardsIndicesValidForAttack(attacker.getCards()));
                    if (attackCardIndex == -1) {
                        break; //�� �����������
                    } else {
                        //����� ��������� ���������� ����� � ������ � ��������� �� ����
                        attackerCard = attacker.getCard(attackCardIndex);
                        action.addCardForAttack(attacker.getUserIdentifier(), attackerCard);
                        players.get(attackerIndex - 1).removeCard(attackCardIndex);
                    }

                    //���������� � �������������, ����� ������ �� ��������� ����� ���������� �����
                    int defenceCardIndex = PlayerService.whatToDiscard(
                            GameService.getCardsIndicesValidForDefence(defender.getCards(), attackerCard, pack.getTrumpCard().getSuit()));

                    if (defenceCardIndex == -1) {
                        isDefenceSuccessful = false; //�������� �� ����, ������� � ���������� �������������� - ��������
                        break;
                    } else {
                        //����� ��������� ����� � ������ � ��������� �� ���� ��� ������
                        action.addCardForDefence(defender.getCard(defenceCardIndex), attackerCard, pack.getTrumpCard().getSuit());
                        players.get(defenderIndex - 1).removeCard(defenceCardIndex);
                    }
                }
            }
            action.setDefenceResult(isDefenceSuccessful); //������������� ���� ������� - ������� ����� ��� ���
            actions.add(action); //��������� � ������ �������

            if (isDefenceSuccessful) {
                //���� ����� ������� �������, �� �� ����� ���������. ����� ����� ���������� ����� ���, ��� ������ ����
                //�� ������� �������, �������������� ��� ������ �� 1 ������
                defenderIndex++;
                rechargePlayers(); //������ �������� ����� �� ������ ����
            } else {
                //���� ����� �������� �� ����, �� ��������� ��� ���������� �����, ������� ���� ���������.
                //����� ��� ��������� � ����, ��� ������ ���� �� ������� �������, � ����������, ��������������,
                //����� ���, ��� ������ �� 2 ������
                players.get(defenderIndex - 1).giveCards(action.getAttackersCards());
                defenderIndex += 2;
            }
        }
    }

    /**
     * ������ ������� �������������� �����
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
     * ��������� �� �����
     *
     * @param defender ������������
     * @param attacker �������������
     * @return ���������
     */
    private boolean isDraw(Player defender, Player attacker) {
        return howManyPlayers == 2 && defender.getCards().size() == 0 && attacker.getCards().size() == 0;
    }

    /**
     * ���������, ������� �� �����
     *
     * @param playerIndex ������
     * @return ���������
     */
    private boolean isWinner(int playerIndex) {
        return players.get(playerIndex - 1).getCards().size() == 0;
    }

    /**
     * ���������� ������������
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
