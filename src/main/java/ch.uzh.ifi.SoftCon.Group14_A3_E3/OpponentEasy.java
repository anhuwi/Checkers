package ch.uzh.ifi.SoftCon.Group14_A3_E3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpponentEasy extends ComputerOpponent implements OpponentStrategy {

    /**
     * Method that evaluates how "good" the different moves are. Returns either one move if it is the best or more than one if they are all considered the best moves
     * @param moveList List of all possible moves
     * @param b: Board
     * @return List of all moves that are considered "best". Size of that list can be one or more.
     */
    public List<int[]> chooseMoveByChoice(List<int[]> moveList, Board b){
        assert b != null;
        assert moveList != null && moveList.size() > 0;
        // assert playerSkill != null;

        List<Integer> ratingList = new ArrayList<>(Collections.nCopies(moveList.size(), 0));
        // weight system
        int becomeKing = 15;
        int willBeEaten = -10;
        int MoveLastRow = -13;
        int captureOneOpponent = 10;
        int kingFactor = 2;

        // make a list with equal size to moveList with a weights of how 'good' the moves with same index are
        for(int i = 0; i<moveList.size(); i++){
            int [] move = moveList.get(i);
            int len = move.length;
            int [] start = {move[0], move[1]};
            int [] end = {move[len-2], move[len-1]};

            // case only single move
            if (Math.abs(moveList.get(0)[0] - moveList.get(0)[2]) == 1){
                // case next move makes a king (good)
                if(nextMoveKing(start, end, b)){
                    ratingList.set(i, ratingList.get(i)+becomeKing);
                }
                // case move from last row (bad)
                if(moveFromLastRow(start, b)){
                    ratingList.set(i, ratingList.get(i)+MoveLastRow);
                }
                // case move results in being captured next move (bad)
                if(isEatenNextMove(move, b)){
                    int incr = (isKing(start,b)) ? ratingList.get(i)+kingFactor*willBeEaten : ratingList.get(i)+willBeEaten;
                    ratingList.set(i, incr);
                }
            }

            // case only jump-moves
            else{
                // initialize value at i-th index with how many opponents are captured with this move
                ratingList.set(i,  ((len-2)/2) * captureOneOpponent);
                // case move from last row (bad)
                if(moveFromLastRow(start, b)){
                    ratingList.set(i, ratingList.get(i)+MoveLastRow);
                }
                // case next move makes a king (good)
                if(nextMoveKing(start, end, b)){
                    ratingList.set(i, ratingList.get(i)+becomeKing);
                }
                // case move results in being captured next move (bad)
                if(isEatenNextMove(move, b)){
                    int incr = (isKing(start,b)) ? ratingList.get(i)+kingFactor*willBeEaten : ratingList.get(i)+willBeEaten;
                    ratingList.set(i, incr);
                }
            }
        }

        List<int[]> selectedMoves = new ArrayList<>();
        int rating = 1000;
        for (Integer integer : ratingList) {
            if(integer < rating){
                rating = integer;
            }
        }
        for(int i = 0; i < ratingList.size(); i++){
            if(ratingList.get(i) == rating){
                selectedMoves.add(moveList.get(i));
            }
        }
        return selectedMoves;
    }
    @Override
    public int[] chooseMoveOpponent(List<int[]> moveList, Board board) {
        return chooseMoveRandom(chooseMoveByChoice(moveList, board));
    }
}
