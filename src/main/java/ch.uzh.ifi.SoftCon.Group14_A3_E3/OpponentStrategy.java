package ch.uzh.ifi.SoftCon.Group14_A3_E3;

import java.util.List;

public interface OpponentStrategy {
    int[] chooseMoveOpponent(List<int[]> moveList, Board board);
}
