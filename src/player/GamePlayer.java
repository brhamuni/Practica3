package player;

import java.awt.*;

public abstract class GamePlayer {

    protected int myMark;
    protected int depth;
    public GamePlayer(int mark, int depth){
        myMark = mark;
        this.depth = depth;
    }

    public int getMyMark(){
        return myMark;
    }

    abstract public boolean isUserPlayer();

    abstract public String playerName();

    abstract public Point play(int[][] board);

}
