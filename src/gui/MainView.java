package gui;

import cards.Card;
import logic.GameManager;
import logic.GameAction;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * ����� ��������� ������ �� ����������� ����������� ����
 */
public class MainView extends JFrame {
    private JTextArea main;
    private JPanel panel1;
    public static final String separator = System.lineSeparator();

    public MainView() {
        setContentPane(panel1);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        main.setEditable(false);
        main.setFont(new Font("Courier New", Font.BOLD, 20));
    }

    /**
     * ������� ������� ���� �� �����
     *
     * @param game ��������� ����
     */
    public void showGame(GameManager game) {
        displayPlayersNumber(game.getPlayersNumber()); //����� ���-�� �������
        displayFirstTurnPlayer(String.valueOf(game.getFirstTurnPlayerIndex())); //��� ����� ������
        displayTrumpCard(game.getTrumpCard().getRank().getInfo(), game.getTrumpCard().getSuit().getSymbol()); //�������� �����

        List<GameAction> actions = game.run();

        for (GameAction action : actions) {
            main.append("-----------------------------------------------------------------" + separator);

            displayAttackersInfo(action.getAttackers());
            displayDefenderInfo(action.getDefenceMap(), action.getDefender(), action.isDefenceSuccessful());

        }
        showUpResults(getConfiguredResults(game.getWinners(), game.getLoser()));
    }

    /**
     * ������� ���-��� � ��������� �������������
     *
     * @param defenceMap ������������� ���������� ����� � ������������ �����
     * @param defenderId  id �������������
     * @param attackResult ��������� - ������� ��� �������� �����
     */
    private void displayDefenderInfo(Map<Card, Card> defenceMap, String defenderId, boolean attackResult) {
        main.append("���������� �����: " + defenderId + separator);
        main.append("����� ���������� �����:" + separator);
        if (attackResult) {
            showUpCards(defenceMap.keySet().stream().toList()); //����� ���� �������������
            showUpCards(defenceMap.values().stream().toList()); //����� ����, ������� ��� �������� ���������
        }
        main.append(attackResult ? "�������" + separator : "�������� �� ����, �������� �����" + separator);
    }

    /**
     * ������� ���-��� � ��������� �������������
     *
     * @param attackers �������������
     */
    private void displayAttackersInfo(Map<String, List<Card>> attackers) {
        for (String attacker : attackers.keySet()) {
            //���� � ������ �� ������� ����, �� ���� �� ������ �� ���� �����������, ���� ��� ������ ���� �����������
            int state = (attackers.get(attacker).size() == 0) ? 0 : 1;
            String s = (state == 0) ? " ������ �� �����������" : " �����������:";
            main.append("����� " + attacker + s + separator);
            if (state != 0) {
                showUpCards(attackers.get(attacker));
            }
        }
    }

    /**
     * ������������ ��������� ��������� ������
     *
     * @param winners ����������
     * @param loser   �����������
     * @return ������� � ������ ������
     */
    private String getConfiguredResults(List<String> winners, String loser) {
        if (winners.size() == 0) {
            return "�����";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("����������:").append(separator).append(separator);
        int i = 1;
        for (String s : winners) {
            builder.append(i).append(" �����: ����� ").append(s).append(separator);
            i++;
        }
        builder.append("� �������: ����� ").append(loser);
        return builder.toString();
    }

    /**
     * ������� ����� ������
     *
     * @param info ���-���
     */
    private void showUpResults(String info) {
        JOptionPane.showMessageDialog(this, info, "�����", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * ������� ������� �������� ������
     *
     * @param name ��� ������
     */
    private void displayFirstTurnPlayer(String name) {
        main.append("������ ����� �����: " + name + separator);
    }

    /**
     * ������� ���-�� ��������
     *
     * @param n �����
     */
    private void displayPlayersNumber(int n) {
        main.append("������: " + n + separator);
    }

    /**
     * ������� ��������� �������� �����
     *
     * @param trumpRankInfo ����
     * @param symbol        ������ �����
     */
    private void displayTrumpCard(String trumpRankInfo, char symbol) {
        main.append("�������� �����: " + separator);
        printOutCard(trumpRankInfo, symbol);
    }

    /**
     * ���� ������ �� ����������� ���� � �� �����������
     *
     * @param cards ������ ����
     */
    private void showUpCards(List<Card> cards) {
        String[] rankInfos = cards.stream().map(x -> x.getRank().getInfo()).toList().toArray(String[]::new);
        char[] symbols = new char[rankInfos.length];

        int i = 0;
        for (Card c : cards) {
            symbols[i] = c.getSuit().getSymbol();
            i++;
        }

        printOutCards(rankInfos, symbols);
        main.append(separator);
    }

    /**
     * ����������� ���� �����
     *
     * @param rankInfo ���� �����
     * @param symbol   ������ �����
     */
    private void printOutCard(String rankInfo, char symbol) {
        printOutCards(new String[]{rankInfo}, new char[]{symbol});
    }

    /**
     * ���������� ���� � ���
     *
     * @param rankInfos ���-��� � ������������
     * @param symbols   ���-��� � ������
     */
    private void printOutCards(java.lang.String[] rankInfos, char[] symbols) {
        main.append(separator);
        for (int i = 0; i < rankInfos.length; i++) {
            main.append("������    ");
        }
        main.append(separator);
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                for (char symbol : symbols) {
                    main.append("|");
                    main.append(java.lang.String.valueOf(symbol));
                    main.append("   ");
                    main.append("|    ");
                }
            }
            if (i == 1) {
                for (int k = 0; k < rankInfos.length; k++) {
                    main.append("|");
                    main.append("    ");
                    main.append("|    ");
                }
            }
            if (i == 2) {
                for (java.lang.String rankInfo : rankInfos) {
                    main.append("|");
                    if (rankInfo.length() == 2) {
                        main.append("  ");
                    } else {
                        main.append("   ");
                    }
                    main.append(rankInfo);
                    main.append("|    ");
                }
            }
            main.append(separator);
        }
        for (int i = 0; i < rankInfos.length; i++) {
            main.append("������    ");
        }
        main.append(separator);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
