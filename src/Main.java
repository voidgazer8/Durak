
import gui.MainView;
import logic.GameManager;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        MainView view = new MainView();

        int randomPlayersNumber = new Random().nextInt(2, 6 + 1);
        GameManager game = new GameManager(randomPlayersNumber);

        view.showGame(game);
    }
}
