package players;

import java.util.List;
import java.util.Random;

public class PlayerService {

    /**
     * узнать, сколько карт будет подкинуто
     * @param defenderHandSize размер руки отбивающегося
     * @param attackCount номер захода
     * @return случайное число
     */
    public static int howManyToDiscard(int defenderHandSize, int attackCount) {
        int n;
        if (attackCount == 0) {
            //если это первый заход, то подброшенных карт не может быть более 5
            n = new Random().nextInt(1, 5 + 1);
        } else {
            //иначе может быть подкинуто максимум 6 карт, и не более, чем карт в руке отбивающегося
            n = new Random().nextInt(1, defenderHandSize + 1);
            if (defenderHandSize > 6) {
                n = new Random().nextInt(1, 6 + 1);
            }
        }
        return n;
    }

    /**
     * узнать номер карты для сброса
     * @param validIndices номера карт, доступных для сброса
     * @return случайное число
     */
    public static int whatToDiscard(List<Integer> validIndices) {
        if (validIndices.size() == 0) {
            return -1; //если доступных карт нет, то игрок ничего не сбрасывает
        }
        int n = new Random().nextInt(0, validIndices.size());
        return validIndices.get(n);
    }
}
