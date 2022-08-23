package ch.uzh.ifi.SoftCon.Group14_A3_E3;

import java.util.List;
import java.util.Random;

public abstract class ComputerOpponent {

    /**
     * Method selects a random move from a moveList
     * @param moveList Takes input of List of all possible Moves for a Player
     * @return returns an Array which contains the coordinates for a move
     */
    public int[] chooseMoveRandom(List<int[]> moveList){
        Random randomizer = new Random();
        assert moveList.size() > 0;
        int randomSelector = randomizer.nextInt(moveList.size());
        return moveList.get(randomSelector);
    }

    /**
     *
     * @param start start coordinate, array with two entries
     * @param end end coordinate, array with two entries
     * @param b board
     * @return boolean, true if piece would newly become a king
     */
    public boolean nextMoveKing(int [] start, int[] end, Board b){
        assert b != null;
        assert start.length == 2 && end.length == 2;
        return b.becomesKing(start, end[0]);
    }

    public boolean moveFromLastRow(int[] start, Board b){
        assert b != null;
        assert start.length == 2;
        return b.isLastRow(start);
    }

    public boolean isEatenNextMove(int [] move, Board b){
        assert b != null;
        return b.isEatenNextMove(move);
    }
    public boolean isKing(int [] coordinate, Board b){
        assert b != null;
        assert coordinate.length == 2;
        return b.isKing(coordinate);
    }
}
