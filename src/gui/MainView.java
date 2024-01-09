package gui;

import cards.Card;
import logic.GameManager;
import logic.GameAction;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * класс описывает работу по визуальному отображению игры
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
     * вывести процесс игры на экран
     *
     * @param game экземпляр игры
     */
    public void showGame(GameManager game) {
        displayPlayersNumber(game.getPlayersNumber()); //показ кол-ва игроков
        displayFirstTurnPlayer(String.valueOf(game.getFirstTurnPlayerIndex())); //кто ходит первым
        displayTrumpCard(game.getTrumpCard().getRank().getInfo(), game.getTrumpCard().getSuit().getSymbol()); //козырная карта

        List<GameAction> actions = game.run();

        for (GameAction action : actions) {
            main.append("-----------------------------------------------------------------" + separator);

            displayAttackersInfo(action.getAttackers());
            displayDefenderInfo(action.getDefenceMap(), action.getDefender(), action.isDefenceSuccessful());

        }
        showUpResults(getConfiguredResults(game.getWinners(), game.getLoser()));
    }

    /**
     * вывести инф-цию о действиях отбивающегося
     *
     * @param defenceMap сопоставление подкинутой карты и отбивающейся карты
     * @param defenderId  id отбивающегося
     * @param attackResult результат - отбился или забирает карты
     */
    private void displayDefenderInfo(Map<Card, Card> defenceMap, String defenderId, boolean attackResult) {
        main.append("Отбивается игрок: " + defenderId + separator);
        main.append("Кроет подкинутые карты:" + separator);
        if (attackResult) {
            showUpCards(defenceMap.keySet().stream().toList()); //вывод карт отбивающегося
            showUpCards(defenceMap.values().stream().toList()); //затем карт, которые ему суммарно подкинули
        }
        main.append(attackResult ? "Отбился" + separator : "Отбиться не смог, забирает карты" + separator);
    }

    /**
     * вывести инф-цию о действиях подкидывающих
     *
     * @param attackers подкидывающие
     */
    private void displayAttackersInfo(Map<String, List<Card>> attackers) {
        for (String attacker : attackers.keySet()) {
            //если у игрока не нашлось карт, то либо он просто не стал подкидывать, либо ему нечего было подкидывать
            int state = (attackers.get(attacker).size() == 0) ? 0 : 1;
            String s = (state == 0) ? " ничего не подкидывает" : " подкидывает:";
            main.append("Игрок " + attacker + s + separator);
            if (state != 0) {
                showUpCards(attackers.get(attacker));
            }
        }
    }

    /**
     * сформировать строковый результат партии
     *
     * @param winners победители
     * @param loser   проигравший
     * @return готовый к печати формат
     */
    private String getConfiguredResults(List<String> winners, String loser) {
        if (winners.size() == 0) {
            return "Ничья";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Победители:").append(separator).append(separator);
        int i = 1;
        for (String s : winners) {
            builder.append(i).append(" место: игрок ").append(s).append(separator);
            i++;
        }
        builder.append("В дураках: игрок ").append(loser);
        return builder.toString();
    }

    /**
     * вывести итоги партии
     *
     * @param info инф-ция
     */
    private void showUpResults(String info) {
        JOptionPane.showMessageDialog(this, info, "Итоги", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * вывести первого ходящего игрока
     *
     * @param name имя игрока
     */
    private void displayFirstTurnPlayer(String name) {
        main.append("Первым ходит игрок: " + name + separator);
    }

    /**
     * вывести кол-во играющих
     *
     * @param n число
     */
    private void displayPlayersNumber(int n) {
        main.append("Играют: " + n + separator);
    }

    /**
     * вывести параметры козырной карты
     *
     * @param trumpRankInfo ранг
     * @param symbol        символ масти
     */
    private void displayTrumpCard(String trumpRankInfo, char symbol) {
        main.append("Козырная карта: " + separator);
        printOutCard(trumpRankInfo, symbol);
    }

    /**
     * сбор данных из переданнных карт и их расппечатка
     *
     * @param cards список карт
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
     * распечатать одну карту
     *
     * @param rankInfo ранг карты
     * @param symbol   символ масти
     */
    private void printOutCard(String rankInfo, char symbol) {
        printOutCards(new String[]{rankInfo}, new char[]{symbol});
    }

    /**
     * распечатка карт в ряд
     *
     * @param rankInfos инф-ция о достоинствах
     * @param symbols   инф-ция о мастях
     */
    private void printOutCards(java.lang.String[] rankInfos, char[] symbols) {
        main.append(separator);
        for (int i = 0; i < rankInfos.length; i++) {
            main.append("——————    ");
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
            main.append("——————    ");
        }
        main.append(separator);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
