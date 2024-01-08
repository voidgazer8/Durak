
import gui.MainView;
import logic.GameManager;

import java.util.Random;

public class Main {

    public static void main(String[] args) {

        MainView view = new MainView();
        GameManager game = new GameManager();

        int randomPlayersNumber = new Random().nextInt(2, 6 + 1);
        game.setUpBasicPack();
        game.findTrumpCard();
        game.setUpPlayers(randomPlayersNumber);
        game.dealCards();
        game.findFirstTurnPlayerIndex();

        view.showGame(game);
    }
}
