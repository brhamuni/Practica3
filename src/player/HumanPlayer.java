package player;

import java.awt.*;

public class HumanPlayer extends GamePlayer {

    public HumanPlayer(int mark, int depth) {
        super(mark, depth);
    }

    @Override
    public boolean isUserPlayer() {
        return true;
    }

    @Override
    public String playerName() {
        return "User" ;
    }

    @Override
    public Point play(int[][] board) {
        return null;
    }

}
