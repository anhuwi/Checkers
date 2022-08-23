package ch.uzh.ifi.SoftCon.Group14_A3_E3;

import java.util.List;

public class OpponentAverage extends ComputerOpponent implements OpponentStrategy {
    @Override
    public int[] chooseMoveOpponent(List<int[]> moveList, Board board){
        return chooseMoveRandom(moveList);
    }
}
