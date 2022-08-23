package ch.uzh.ifi.SoftCon.Group14_A3_E3;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Board Class. Prints the board. Makes move on the board. Stores the Values
 */
public class Board implements Observer, Subject {
    public enum Piece {
        R_P, // Red Pawn, Piece of Player 1
        R_K, // Red King, Piece of Player 1
        W_P, // White Pawn, Piece of Player 2
        W_K, // White King, Piece of Player 2
        I_SQ // illegal Squares
    }

    private final int nrSquares = 8;
    //Starting and ending position of move method
    private final int[] startPos = new int[2];
    private final int[] endPos = new int[2];

    // Piece array for storing the different pieces
    public final Piece[][] arrBoard = new Piece[nrSquares][nrSquares];
    public Game.Player player;
    private final List<Observer> observers = new ArrayList<>();
    private String str;


    /**
     * Constructs a new board
     */
    public Board(Game.Player player) {
        // notify as observer of player
        this.player = player;

        // initialize Pieces like in the picture from assignment 1.
        // Board starts at the bottom (counterintuitive)
        // even rows on board (odd in array)
        for (int j = 1; j < nrSquares; j += 2) {
            arrBoard[1][j] = Piece.W_P; // row 2
            arrBoard[5][j] = Piece.R_P; // row 6
            arrBoard[7][j] = Piece.R_P; // row 8

            // illegal positions (white fields)
            for (int i = 0; i < nrSquares; i += 2) {
                arrBoard[i][j] = Piece.I_SQ;
            }
        }
        // odd rows
        for (int j = 0; j < nrSquares; j += 2) {
            arrBoard[0][j] = Piece.W_P; // row 1
            arrBoard[2][j] = Piece.W_P; // row 3
            arrBoard[6][j] = Piece.R_P; // row 7

            // illegal positions (white fields)
            for (int i = 1; i < nrSquares; i += 2) {
                arrBoard[i][j] = Piece.I_SQ;
            }
        }

    }

    public Piece[][] getBordArray() {
        Piece[][] copy = new Piece[nrSquares][];
        for (int i = 0; i < nrSquares; ++i) {
            copy[i] = Arrays.copyOf(arrBoard[i], arrBoard[i].length);
        }
        return copy;
    }

    /**
     * Constructor to clone an existing Board
     *
     * @param existingBoard, existing board
     */
    public Board(Board existingBoard) {
        for (int i = 0; i < nrSquares; ++i) {
            System.arraycopy(existingBoard.arrBoard[i], 0, this.arrBoard[i], 0, nrSquares);
        }
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.updateLabel(this.str);
        }
    }

    @Override
    public void updatePlayer(Game.Player currentPlayer, OpponentStrategy strategy) {
        this.player = currentPlayer;
    }

    @Override
    public void updateLabel(String str) {
    }

    /**
     * Change board because of move
     *
     * @param start, starting position for the piece
     * @param end,   ending position for the piece
     */
    private void updateBoard(int[] start, int[] end, List<int[]> captured) {
        this.arrBoard[end[0]][end[1]] = this.arrBoard[start[0]][start[1]];
        this.arrBoard[start[0]][start[1]] = null;

        if (captured.size() > 0) {
            for (int[] p : captured) {
                this.arrBoard[p[0]][p[1]] = null;
            }
        }
    }

    /**
     * Check if piece changes to king
     * Changes value of piece to king
     */
    public void checkKing() {
        // checks last row on board.
        for (int i = 0; i < nrSquares; i += 2) {
            // checks for King for red
            if (arrBoard[0][i] == Piece.R_P) {
                arrBoard[0][i] = Piece.R_K;
            }
            // Checks for King for white
            if (arrBoard[7][i + 1] == Piece.W_P) {
                arrBoard[7][i + 1] = Piece.W_K;
            }
        }
    }

    /**
     * method to determine if a piece would newly become a king at given start coordinate
     *
     * @param start coordinates where piece is currently at
     * @param row   is which row the piece would get to (from end coordinate: end[0])
     * @return boolean, true if piece would newly become a king, false otherwise
     */
    public boolean becomesKing(int[] start, int row) {
        assert start.length == 2;
        if (this.arrBoard[start[0]][start[1]] == Piece.W_P && this.player == Game.Player.White && row == this.getNrSquares() - 1) {
            return true;
        } else return this.arrBoard[start[0]][start[1]] == Piece.R_P && this.player == Game.Player.Red && row == 0;
    }

    /**
     * method determines if a piece at given coordinate is in its last row
     *
     * @param coordinate: coordinates of board
     * @return boolean, true if piece at coordinate is at its last row
     */
    public boolean isLastRow(int[] coordinate) {
        assert coordinate.length == 2;
        return (this.arrBoard[coordinate[0]][coordinate[1]] == Piece.R_P && coordinate[0] == this.getNrSquares())
                || (this.arrBoard[coordinate[0]][coordinate[1]] == Piece.W_P && coordinate[0] == 0);
    }

    /**
     * method determines if a piece at a given coordinate is a king
     *
     * @param coordinate: array with 2 entries
     * @return boolean, true if piece at coordinate is a king
     */
    public boolean isKing(int[] coordinate) {
        assert coordinate.length == 2;
        return this.arrBoard[coordinate[0]][coordinate[1]] == Piece.R_K ||
                this.arrBoard[coordinate[0]][coordinate[1]] == Piece.W_K;
    }


    /**
     * @return private value nrSquare (int) = 8
     */
    private int getNrSquares() {
        return nrSquares;
    }

    /**
     * Searches the board for all pieces of a player
     *
     * @param player, Player 1 or 2
     * @return list of coordinates of pieces, if none exist, an empty list
     */
    private List<int[]> getAllPieces(Game.Player player) {
        // Find tiles with matching IDs
        List<int[]> points = new ArrayList<>();
        if (player == Game.Player.Red) {
            for (int i = 0; i < nrSquares; i++) {
                for (int j = 0; j < nrSquares; j++) {
                    if (this.arrBoard[i][j] == Piece.R_P || this.arrBoard[i][j] == Piece.R_K) {
                        int[] coordinate = {i, j};
                        points.add(coordinate);
                    }
                }
            }
        } else {
            for (int i = 0; i < nrSquares; i++) {
                for (int j = 0; j < nrSquares; j++) {
                    if (this.arrBoard[i][j] == Piece.W_P || this.arrBoard[i][j] == Piece.W_K) {
                        int[] coordinate = {i, j};
                        points.add(coordinate);
                    }
                }
            }
        }
        return points;
    }

    /**
     * Searches the board for all possible moves of a player
     *
     * @param player, Player 1 or 2
     * @return list of possible moves, if none exist, an empty list
     */
    public List<int[]> getAllMoves(Game.Player player) {
        List<int[]> moves = new ArrayList<>();
        List<int[]> pieces = getAllPieces(player);
        List<int[]> canCapture = new ArrayList<>();
        List<int[]> canMove = new ArrayList<>();
        for (int[] start : pieces) {
            if (canCaptureInOneJump(start)) {
                canCapture.add(start);
            } else if (canSingleMove(this.arrBoard[start[0]][start[1]], start)) {
                canMove.add(start);
            }
        }
        if (canCapture.size() > 0) {
            for (int[] start : canCapture) {
                moves = this.getAllMovesHelper(canCaptureInOneJumpList(start), moves, start);
            }
        } else if (canMove.size() > 0) {
            for (int[] start : canMove) {
                List<int[]> options = this.SingleMoveList(start);
                for (int[] end : options) {
                    moves.add(new int[]{start[0], start[1], end[0], end[1]});
                }
            }
        }
        return moves;
    }

    /**
     * helper method for getAllMoves(.)
     *
     * @param OldOptions list of move-arrays, is changed each recursive call
     * @param moves      final move list
     * @param current    current move
     * @return adapted list with possible moves for getAllMoves(.)
     */
    private List<int[]> getAllMovesHelper(List<int[]> OldOptions, List<int[]> moves, int[] current) {
        if (OldOptions.size() > 0) {
            for (int[] oldOption : OldOptions) {
                int[] start = new int[]{current[current.length - 2], current[current.length - 1]};
                int[] total = new int[current.length + 2];
                System.arraycopy(current, 0, total, 0, current.length);
                total[total.length - 2] = oldOption[0];
                total[total.length - 1] = oldOption[1];
                Board boardCopy = new Board(this);
                makeMoveFakeBoard(start, oldOption, boardCopy);
                List<int[]> NewOptions = boardCopy.canCaptureInOneJumpList(oldOption);
                moves = boardCopy.getAllMovesHelper(NewOptions, moves, total);
            }
        } else {
            moves.add(current);
        }
        return moves;
    }


    /**
     * @return coordinates of move (startPosition & endPosition)
     */
    public int[][] positionCoordinates() {
        return new int[][]{startPos, endPos};
    }

    /**
     * Changes the users input string into two sets of Coordinates
     * startPos[], with the coordinates of starting Position
     * endPos[], with x and y coordinates of position after the move
     *
     * @param move: the input string from the user
     */
    private void initPositions(String move) {
        // convert String move to two arrays[2] with coordinates.
        startPos[0] = Character.getNumericValue(move.charAt(2)) - 1; // get start row (y-Axis)
        startPos[1] = ((int) move.charAt(1) - 97);  // get start column (x-Axis)
        endPos[0] = Character.getNumericValue(move.charAt(7)) - 1; // get end row (y-Axis)
        endPos[1] = ((int) move.charAt(6) - 97);    // get end column (x-Axis)
    }

    /**
     * Checks if a move is valid / legal to do
     * @param move : inputString
     * @param player: Game.Player (red or white)
     * @return boolean: True if move is valid, false if not valid
     */
    public boolean isValid(String move, Game.Player player) {
        this.player = player;
        initPositions(move);

        //check for primitive validity
        if (primitiveIsNotValid()) {
            return false;
        }

        // helper value
        int rowDiff = endPos[0] - startPos[0];
        int colDiff = endPos[1] - startPos[1];

        // List of Pieces from Player
        List<int[]> posPiecesPlayer;
        posPiecesPlayer = this.getAllPieces(this.player);

        // case Single Move, only possible if no Jump Move is possible
        if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1) {
            // we have already checked if player is allowed to move in this direction for Pawns
            // if the field is free, directly move it, if no other piece can be captured by any other piece
            for (int[] pos : posPiecesPlayer) {
                // check that no Jump Moves are possible
                if (canCaptureInOneJump(pos)) {
                    this.str = "if possible, you have to capture a piece!";
                    notifyObservers();
                    return false;
                }
            }
            return true;
        }

        // case Single Jump Move
        if (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 2) {
            int meanRow = (startPos[0] + 1 + endPos[0] + 1) / 2 - 1;
            int meanColumn = (startPos[1] + 1 + endPos[1] + 1) / 2 - 1;
            // Checks if single jump move is possible and rightly done (capture of opposite piece)
            if (canCaptureInOneJump(startPos) && ((this.player == Game.Player.White &&
                    (this.arrBoard[meanRow][meanColumn] == Piece.R_K ||
                            this.arrBoard[meanRow][meanColumn] == Piece.R_P)) ||
                    (this.player == Game.Player.Red &&
                            (this.arrBoard[meanRow][meanColumn] == Piece.W_K ||
                                    this.arrBoard[meanRow][meanColumn] == Piece.W_P)))) {
                return true;
            } else {
                int count = 0;
                int possibilities;
                Board bCopy = new Board(this); // copies board
                possibilities = singleJump(startPos, count, bCopy); // number of possibilities to do the multiple jump

                if(possibilities > 0) {
                    this.str = "you entered a multiple jump. Please enter as single jumps! ";
                } else {
                    this.str = "your single jump move is not correct";
                }
                notifyObservers();
                return false;
            }
        }

        // case Multiple Jump Move
        if (Math.abs(rowDiff) >= 3 || Math.abs(rowDiff) == 0) {
            int count = 0;
            int possibilities;

            Board bCopy = new Board(this); // copies board
            possibilities = singleJump(startPos, count, bCopy); // number of possibilities to do the multiple jump

            if (possibilities > 0) {
                this.str = "you have entered a valid multiple Jump. Please enter your move as single jumps! ";
                notifyObservers();
            }
            return false;
        }
        this.str = "move is not valid, enter another one";
        notifyObservers();
        return false;
    }

    /**
     * checks for: illegal position (white fields), player moves to start field, end field is empty, move own piece,
     * if the player is allowed to move it this direction
     *
     * @return (boolean) false if any of these conditions is violated, else true
     */
    private boolean primitiveIsNotValid() {

        // check if either coordinate is an illegal position (white fields)
        if (this.arrBoard[startPos[0]][startPos[1]] == Piece.I_SQ ||
                this.arrBoard[endPos[0]][endPos[1]] == Piece.I_SQ) {
            this.str = "the current or the future position is a white square";
            notifyObservers();
            return true;
        }

        // check if both coordinates are the same
        if (Arrays.equals(startPos, endPos)) {
            // special case for testing multiple jump move
            int count = 0;
            int possibilities;
            List<Integer> interResult = new ArrayList<>();
            List<int[]> newpos;
            Board bCopy = new Board(this); // copies board
            // has to make a single jump first, testing for all new positions
            newpos = getAllNewPos(startPos, bCopy);
            for (int[] e : newpos) {
                // update Board
                Board b_new = new Board(bCopy);
                makeMoveFakeBoard(startPos, e, b_new);
                interResult.add(singleJump(e, count, b_new));
            }
            // number of possibilities to do the multiple jump
            possibilities = interResult.stream().mapToInt(Integer::intValue).sum();
            // check if multiple jump move is possible, then move is valid.
            if (possibilities > 0) {
                return false;
            } else {
                this.str = "the current and future piece position are the same";
                notifyObservers();
                return true;
            }
        }

        // check if the square at the end-position is not empty
        if (this.arrBoard[endPos[0]][endPos[1]] != null) {
            this.str = "the future Piece Position is not empty or is white";
            notifyObservers();
            return true;
        }

        // check if the square at the start-position is empty
        if (this.arrBoard[startPos[0]][startPos[1]] == null) {
            this.str = "entered start position is empty";
            notifyObservers();
            return true;
        }

        // check if current player (white) wants to move opponents piece
        if (this.player == Game.Player.White && (this.arrBoard[startPos[0]][startPos[1]] == Piece.R_P
                || this.arrBoard[endPos[0]][endPos[1]] == Piece.R_K)) {
            this.str = "you are not allowed to move the other players piece";
            notifyObservers();
            return true;
        }
        // check if current player (red) wants to move opponents piece
        if (this.player == Game.Player.Red && (this.arrBoard[startPos[0]][startPos[1]] == Piece.W_P
                || this.arrBoard[endPos[0]][endPos[1]] == Piece.W_K)) {
            this.str = "you are not allowed to move the other players piece";
            notifyObservers();
            return true;
        }

        // check if player is allowed to move in that direction
        if (this.arrBoard[startPos[0]][startPos[1]] == Piece.R_P && startPos[0] - endPos[0] < 0) {
            this.str = "you are not allowed to move with this piece in this direction";
            notifyObservers();
            return true;
        }
        if (this.arrBoard[startPos[0]][startPos[1]] == Piece.W_P && startPos[0] - endPos[0] > 0) {
            this.str = "you are not allowed to move with this piece in this direction";
            notifyObservers();
            return true;
        }
        return false;
    }

    /**
     * Get all new positions from possible turns
     *
     * @param coordinate: int array of length 2
     * @param bCopy:      copy of Board
     * @return list with all possible positions
     */
    private List<int[]> getAllNewPos(int[] coordinate, Board bCopy) {
        // Array with all possible Positions for next turn
        List<int[]> pos = new ArrayList<>();
        Piece start = this.arrBoard[startPos[0]][startPos[1]];

        if (start == Piece.W_P || start == Piece.W_K) {
            // top right corner
            if ((coordinate[0] + 2 < bCopy.nrSquares && coordinate[1] + 2 < bCopy.nrSquares) &&
                    ((bCopy.arrBoard[coordinate[0] + 1][coordinate[1] + 1] == Piece.R_P ||
                            bCopy.arrBoard[coordinate[0] + 1][coordinate[1] + 1] == Piece.R_K) &&
                            bCopy.arrBoard[coordinate[0] + 2][coordinate[1] + 2] == null)) {
                int[] e = {coordinate[0] + 2, coordinate[1] + 2};
                pos.add(e);
            }
            // top left corner
            if ((coordinate[0] + 2 < bCopy.nrSquares && coordinate[1] - 2 >= 0) &&
                    (bCopy.arrBoard[coordinate[0] + 1][coordinate[1] - 1] == Piece.R_P ||
                            bCopy.arrBoard[coordinate[0] + 1][coordinate[1] - 1] == Piece.R_K) &&
                    bCopy.arrBoard[coordinate[0] + 2][coordinate[1] - 2] == null) {
                int[] e = {coordinate[0] + 2, coordinate[1] - 2};
                pos.add(e);
            }
            if (start == Piece.W_K) {
                // bottom right corner
                if ((coordinate[0] - 2 >= 0 && coordinate[1] + 2 < bCopy.nrSquares) &&
                        (bCopy.arrBoard[coordinate[0] - 1][coordinate[1] + 1] == Piece.R_P ||
                                bCopy.arrBoard[coordinate[0] - 1][coordinate[1] + 1] == Piece.R_K) &&
                        bCopy.arrBoard[coordinate[0] - 2][coordinate[1] + 2] == null) {
                    int[] e = {coordinate[0] - 2, coordinate[1] + 2};
                    pos.add(e);
                }
                // bottom left corner
                if ((coordinate[0] - 2 >= 0 && coordinate[1] - 2 >= 0) &&
                        (bCopy.arrBoard[coordinate[0] - 1][coordinate[1] - 1] == Piece.R_P ||
                                bCopy.arrBoard[coordinate[0] - 1][coordinate[1] - 1] == Piece.R_K) &&
                        bCopy.arrBoard[coordinate[0] - 2][coordinate[1] - 2] == null) {
                    int[] e = {coordinate[0] - 2, coordinate[1] - 2};
                    pos.add(e);
                }
            }
        }
        if (start == Piece.R_P || start == Piece.R_K) {
            if ((coordinate[0] - 2 >= 0 && coordinate[1] + 2 < bCopy.nrSquares) &&
                    (bCopy.arrBoard[coordinate[0] - 1][coordinate[1] + 1] == Piece.W_P ||
                            bCopy.arrBoard[coordinate[0] - 1][coordinate[1] + 1] == Piece.W_K) &&
                    bCopy.arrBoard[coordinate[0] - 2][coordinate[1] + 2] == null) {
                int[] e = {coordinate[0] - 2, coordinate[1] + 2};
                pos.add(e);
            }
            // bottom left corner
            if ((coordinate[0] - 2 >= 0 && coordinate[1] - 2 >= 0) &&
                    (bCopy.arrBoard[coordinate[0] - 1][coordinate[1] - 1] == Piece.W_P ||
                            bCopy.arrBoard[coordinate[0] - 1][coordinate[1] - 1] == Piece.W_K) &&
                    bCopy.arrBoard[coordinate[0] - 2][coordinate[1] - 2] == null) {
                int[] e = {coordinate[0] - 2, coordinate[1] - 2};
                pos.add(e);
            }

            if (start == Piece.R_K) {
                // top right corner
                if ((coordinate[0] + 2 < this.nrSquares && coordinate[1] + 2 < this.nrSquares) &&
                        (bCopy.arrBoard[coordinate[0] + 1][coordinate[1] + 1] == Piece.W_P ||
                                bCopy.arrBoard[coordinate[0] + 1][coordinate[1] + 1] == Piece.W_K) &&
                        bCopy.arrBoard[coordinate[0] + 2][coordinate[1] + 2] == null) {
                    int[] e = {coordinate[0] + 2, coordinate[1] + 2};
                    pos.add(e);
                }

                // top left corner
                if ((coordinate[0] + 2 < this.nrSquares && coordinate[1] - 2 >= 0) &&
                        (bCopy.arrBoard[coordinate[0] + 1][coordinate[1] - 1] == Piece.W_P ||
                                bCopy.arrBoard[coordinate[0] + 1][coordinate[1] - 1] == Piece.W_K) &&
                        bCopy.arrBoard[coordinate[0] + 2][coordinate[1] - 2] == null) {
                    int[] e = {coordinate[0] + 2, coordinate[1] - 2};
                    pos.add(e);
                }
            }
        }
        return pos;
    }


    /**
     * Makes a move on a board (copy)
     *
     * @param start:   int array of length 2
     * @param end:     int array of length 2
     * @param b_local: copy of Board
     */
    private void makeMoveFakeBoard(int[] start, int[] end, Board b_local) {
        b_local.makeMoveHelper(start, end);
    }

    /**
     * executes a move on any board (used by makeMoveFakeBoard and MakeMove)
     *
     * @param start int array of length 2
     * @param end   int array of length 2
     */
    private void makeMoveHelper(int[] start, int[] end) {
        assert start.length == 2 && end.length == 2;
        List<int[]> captured = new ArrayList<>();
        int rowDiff = end[0] - start[0];
        if (Math.abs(rowDiff) == 2) {
            int meanRow = (start[0] + 1 + end[0] + 1) / 2 - 1;
            int meanColumn = (start[1] + 1 + end[1] + 1) / 2 - 1;
            int[] capPiece = {meanRow, meanColumn};
            captured.add(capPiece);
        }
        this.updateBoard(start, end, captured);
    }

    /**
     * executes Move
     *
     * @param move: String
     */
    public void makeMove(String move) {
        // Defining the normal Move function, mo check if valid
        initPositions(move);
        this.makeMoveHelper(startPos, endPos);
    }

    /**
     * single jump on any board (copies)
     *
     * @param pos:     integer array (coordinates)
     * @param count:   integer
     * @param b_local: Board
     * @return int
     */
    private int singleJump(int[] pos, int count, Board b_local) {
        // safes all possible positions for new jumps
        List<int[]> newpos;
        if (Arrays.equals(endPos, pos)) {
            count += 1;
        } else {
            newpos = getAllNewPos(pos, b_local);
            for (int[] e : newpos) {
                // update Board
                Board b_new = new Board(b_local);
                makeMoveFakeBoard(pos, e, b_new);
                return singleJump(e, count, b_new);
            }
        }
        return count;
    }

    /**
     * Scans neighbourhood for possible OneJump moves
     *
     * @param coordinate, coordinate on board
     * @return a List of all possible SingleJump moves from given location
     */
    private List<int[]> canCaptureInOneJumpList(int[] coordinate) {
        List<int[]> options = new ArrayList<>();
        // start is the piece at the coordinates
        Piece start = this.arrBoard[coordinate[0]][coordinate[1]];

        // case white piece
        if (start == Piece.W_P || start == Piece.W_K) {
            // top right corner
            if ((coordinate[0] + 2 < this.nrSquares && coordinate[1] + 2 < this.nrSquares) &&
                    ((this.arrBoard[coordinate[0] + 1][coordinate[1] + 1] == Piece.R_P ||
                            this.arrBoard[coordinate[0] + 1][coordinate[1] + 1] == Piece.R_K) &&
                            this.arrBoard[coordinate[0] + 2][coordinate[1] + 2] == null)) {
                int[] end = new int[]{coordinate[0] + 2, coordinate[1] + 2};
                options.add(end);
            }
            // top left corner
            if ((coordinate[0] + 2 < this.nrSquares && coordinate[1] - 2 >= 0) &&
                    (this.arrBoard[coordinate[0] + 1][coordinate[1] - 1] == Piece.R_P ||
                            this.arrBoard[coordinate[0] + 1][coordinate[1] - 1] == Piece.R_K) &&
                    this.arrBoard[coordinate[0] + 2][coordinate[1] - 2] == null) {
                int[] end = new int[]{coordinate[0] + 2, coordinate[1] - 2};
                options.add(end);
            }
            // case white king
            if (start == Piece.W_K) {
                // bottom right corner
                if ((coordinate[0] - 2 >= 0 && coordinate[1] + 2 < this.nrSquares) &&
                        (this.arrBoard[coordinate[0] - 1][coordinate[1] + 1] == Piece.R_P ||
                                this.arrBoard[coordinate[0] - 1][coordinate[1] + 1] == Piece.R_K) &&
                        this.arrBoard[coordinate[0] - 2][coordinate[1] + 2] == null) {
                    int[] end = new int[]{coordinate[0] - 2, coordinate[1] + 2};
                    options.add(end);
                }
                // bottom left corner
                if ((coordinate[0] - 2 >= 0 && coordinate[1] - 2 >= 0) &&
                        (this.arrBoard[coordinate[0] - 1][coordinate[1] - 1] == Piece.R_P ||
                                this.arrBoard[coordinate[0] - 1][coordinate[1] - 1] == Piece.R_K) &&
                        this.arrBoard[coordinate[0] - 2][coordinate[1] - 2] == null) {
                    int[] end = new int[]{coordinate[0] - 2, coordinate[1] - 2};
                    options.add(end);
                }
            }
        }
        // case red piece
        if (start == Piece.R_P || start == Piece.R_K) {
            // bottom right corner
            if ((coordinate[0] - 2 >= 0 && coordinate[1] + 2 < this.nrSquares) &&
                    (this.arrBoard[coordinate[0] - 1][coordinate[1] + 1] == Piece.W_P ||
                            this.arrBoard[coordinate[0] - 1][coordinate[1] + 1] == Piece.W_K) &&
                    this.arrBoard[coordinate[0] - 2][coordinate[1] + 2] == null) {
                int[] end = new int[]{coordinate[0] - 2, coordinate[1] + 2};
                options.add(end);
            }
            // bottom left corner
            if ((coordinate[0] - 2 >= 0 && coordinate[1] - 2 >= 0) &&
                    (this.arrBoard[coordinate[0] - 1][coordinate[1] - 1] == Piece.W_P ||
                            this.arrBoard[coordinate[0] - 1][coordinate[1] - 1] == Piece.W_K) &&
                    this.arrBoard[coordinate[0] - 2][coordinate[1] - 2] == null) {
                int[] end = new int[]{coordinate[0] - 2, coordinate[1] - 2};
                options.add(end);
            }
            // case red king
            if (start == Piece.R_K) {
                // top right corner
                if ((coordinate[0] + 2 < this.nrSquares && coordinate[1] + 2 < this.nrSquares) &&
                        (this.arrBoard[coordinate[0] + 1][coordinate[1] + 1] == Piece.W_P ||
                                this.arrBoard[coordinate[0] + 1][coordinate[1] + 1] == Piece.W_K) &&
                        this.arrBoard[coordinate[0] + 2][coordinate[1] + 2] == null) {
                    int[] end = new int[]{coordinate[0] + 2, coordinate[1] + 2};
                    options.add(end);
                }
                // top left corner
                if ((coordinate[0] + 2 < this.nrSquares && coordinate[1] - 2 >= 0) &&
                        (this.arrBoard[coordinate[0] + 1][coordinate[1] - 1] == Piece.W_P ||
                                this.arrBoard[coordinate[0] + 1][coordinate[1] - 1] == Piece.W_K) &&
                        this.arrBoard[coordinate[0] + 2][coordinate[1] - 2] == null) {
                    int[] end = new int[]{coordinate[0] + 2, coordinate[1] - 2};
                    options.add(end);
                }
            }
        }

        return options;
    }

    public boolean canCaptureInOneJump(int[] coordinate) {
        return this.canCaptureInOneJumpList(coordinate).size() > 0;
    }

    /**
     * smaller variant of is_valid method, only checks for SingleJumps
     *
     * @param move:      String
     * @param old_input: String
     * @param player: Game.Player (Red, White)
     * @return boolean
     */
    public boolean isValidSameTurn(String move, String old_input, Game.Player player) {
        this.player = player;
        // Board b = Game.getBoard();
        initPositions(move);


        // get end position from old move
        int[] y = {Character.getNumericValue(old_input.charAt(7)) - 1, ((int) old_input.charAt(6) - 97)};
        // compare it to new input
        if (!Arrays.equals(y, startPos)) {
            this.str = "you must move with the same piece as before!";
            notifyObservers();
            return false;
        }

        // check for primitive validity
        if (primitiveIsNotValid()) {
            return false;
        }

        // check if entry is a single jump
        if (Math.abs(endPos[0] - startPos[0]) != 2 || Math.abs(endPos[1] - startPos[1]) != 2) {
            return false;
        }

        int meanRow = (startPos[0] + 1 + endPos[0] + 1) / 2 - 1;
        int meanColumn = (startPos[1] + 1 + endPos[1] + 1) / 2 - 1;
        // checks if move is possible and correctly done (capture of opposite piece)
        return canCaptureInOneJump(startPos) && ((this.player == Game.Player.White &&
                (this.arrBoard[meanRow][meanColumn] == Piece.R_K ||
                        this.arrBoard[meanRow][meanColumn] == Piece.R_P)) ||
                (this.player == Game.Player.Red &&
                        (this.arrBoard[meanRow][meanColumn] == Piece.W_K ||
                                this.arrBoard[meanRow][meanColumn] == Piece.W_P)));

    }

    /**
     * function that checks if the player whose turn it is still has a Piece. If there is no Piece it returns true
     *
     * @param player, Player whose move it is
     * @return true if there is no Piece left
     */
    public boolean noPieceLeft(Game.Player player) {
        return getAllPieces(player).size() == 0;
    }

    /**
     * function that checks if the player whose turn it is still has no possible move left. If there is no possible move left, it returns true
     *
     * @param player, Player whose move it is
     * @return false if there is a possible move, true if there is no possible move
     */
    public boolean notMoveExists(Game.Player player) {
        // case Red Player
        if (player == Game.Player.Red) {
            for (int i = 0; i < this.nrSquares; i++) {
                for (int j = 0; j < this.nrSquares; j++) {
                    int[] test = {i, j};
                    if (this.arrBoard[i][j] == Piece.R_P || this.arrBoard[i][j] == Piece.R_K) {
                        if (canSingleMove(this.arrBoard[i][j], test)) {
                            return false;
                        } else if (canCaptureInOneJump(test)) {
                            return false;
                        }
                    }
                }
            }
        }
        // case White Player
        else if (player == Game.Player.White) {
            for (int i = 0; i < this.nrSquares; i++) {
                for (int j = 0; j < this.nrSquares; j++) {
                    int[] test = {i, j};
                    if (this.arrBoard[i][j] == Piece.W_P || this.arrBoard[i][j] == Piece.W_K) {
                        if (canSingleMove(this.arrBoard[i][j], test)) {
                            return false;
                        } else if (canCaptureInOneJump(test)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * method that checks if a piece can do a single move, if it can it returns true
     *
     * @param piece,       piece from Board: W_P, W_K, R_P, R_K, I_SQ
     * @param coordinates, integer array with 2 entries, both \in {0,..., nrSquares-1}
     * @return boolean: true if a SingleMove is possible, false otherwise
     */
    private boolean canSingleMove(Piece piece, int[] coordinates) {
        int i = coordinates[0]; // Y-Coordinate
        int j = coordinates[1]; // X-Coordinate

        // checking all possible single moves for a Red Pawn
        if (piece == Piece.R_P) {
            // checks if the piece can do a single move, if yes it returns true
            // in the middle
            if (i > 0 && j < this.nrSquares - 1 && j > 0) {
                return this.arrBoard[i - 1][j + 1] == null || this.arrBoard[i - 1][j - 1] == null;
            }
            // left side
            else if (i > 0 && j == 0) {
                return this.arrBoard[i - 1][j + 1] == null;
            }
            // right side
            else if (i > 0 && j == this.nrSquares - 1) {
                return this.arrBoard[i - 1][j - 1] == null;
            }
        }

        // checking all possible single moves for a White Pawn
        else if (piece == Piece.W_P) {
            //checks if the piece can do a single move, if yes it returns true
            if (i < this.nrSquares - 1 && j < this.nrSquares - 1 && j > 0) {
                return this.arrBoard[i + 1][j + 1] == null || this.arrBoard[i + 1][j - 1] == null;
            } else if (j == 0 && i < this.nrSquares - 1) {
                return this.arrBoard[i + 1][j + 1] == null;
            } else if (j == this.nrSquares - 1 && i < this.nrSquares - 1) {
                return this.arrBoard[i + 1][j - 1] == null;
            }
        }
        //checking if the kings can do a Single Move
        else if (piece == Piece.R_K || piece == Piece.W_K) {
            if (i < 7 && i > 0 && j < 7 && j > 0) {
                return this.arrBoard[i + 1][j + 1] == null || this.arrBoard[i + 1][j - 1] == null || this.arrBoard[i - 1][j - 1] == null || this.arrBoard[i - 1][j + 1] == null;
            }
            //case in bottom left corner
            else if (i == 0 && j == 0) {
                return this.arrBoard[i + 1][j + 1] == null;
            }
            //case in top right corner
            else if (i == this.nrSquares - 1 && j == this.nrSquares - 1) {
                return this.arrBoard[i - 1][j - 1] == null;
            }
            //bottom row
            else if (i == 0 && j > 0) {
                return this.arrBoard[i + 1][j + 1] == null || this.arrBoard[i + 1][j - 1] == null;
            }
            //top row
            else if (i == this.nrSquares - 1 && j < this.nrSquares) {
                return this.arrBoard[i - 1][j + 1] == null || this.arrBoard[i - 1][j - 1] == null;
            }
            //left side
            else if (i > 0 && j == 0) {
                return this.arrBoard[i + 1][j + 1] == null || this.arrBoard[i - 1][j + 1] == null;
            }
            //left side
            else if (i < this.nrSquares - 1 && j == this.nrSquares - 1) {
                return this.arrBoard[i + 1][j - 1] == null || this.arrBoard[i - 1][j - 1] == null;
            }
        }
        return false;
    }

    /**
     * method that returns all possible SingleMoves from a given position
     *
     * @param coordinates, integer array with 2 entries, both \in {0,..., nrSquares-1}
     * @return a list of all possible SingleMoves from a given position
     */
    private List<int[]> SingleMoveList(int[] coordinates) {
        int i = coordinates[0]; // Y-Coordinate
        int j = coordinates[1]; // X-Coordinate
        Piece start = this.arrBoard[i][j];
        List<int[]> moves = new ArrayList<>();

        // checking all possible single moves for a Red Pawn
        if (start == Piece.R_P) {
            // checks on the left
            if (j < this.nrSquares - 1 && this.arrBoard[i - 1][j + 1] == null) {
                int[] end = {i - 1, j + 1};
                moves.add(end);
            }
            // checks on the right
            if (j > 0 && this.arrBoard[i - 1][j - 1] == null) {
                int[] end = {i - 1, j - 1};
                moves.add(end);
            }
        }
        // checking all possible single moves for a White Pawn
        else if (start == Piece.W_P) {
            if (j < this.nrSquares - 1 && this.arrBoard[i + 1][j + 1] == null) {
                int[] end = {i + 1, j + 1};
                moves.add(end);
            }
            if (j > 0 && this.arrBoard[i + 1][j - 1] == null) {
                int[] end = {i + 1, j - 1};
                moves.add(end);
            }
        }
        //checking if the kings can do a Single Move
        else if (start == Piece.R_K || start == Piece.W_K) {
            if (i < this.nrSquares - 1 && j < this.nrSquares - 1 && this.arrBoard[i + 1][j + 1] == null) {
                int[] end = {i + 1, j + 1};
                moves.add(end);
            }
            if (i < this.nrSquares - 1 && j > 0 && this.arrBoard[i + 1][j - 1] == null) {
                int[] end = {i + 1, j - 1};
                moves.add(end);
            }
            // checks on the left
            if (i > 0 && j < this.nrSquares - 1 && this.arrBoard[i - 1][j + 1] == null) {
                int[] end = {i - 1, j + 1};
                moves.add(end);
            }
            // checks on the right
            if (i > 0 && j > 0 && this.arrBoard[i - 1][j - 1] == null) {
                int[] end = {i - 1, j - 1};
                moves.add(end);
            }
        }
        return moves;
    }

    /**
     * Method that checks if the move results in the piece getting eaten
     *
     * @param move: whole move
     * @return boolean: true if piece can get eaten by opponent when executing move
     */
    public Boolean isEatenNextMove(int[] move) {
        int[] finalPosition = {move[move.length - 2], move[move.length - 1]};
        if (finalPosition[0] == 0 || finalPosition[0] == this.getNrSquares() - 1
                || finalPosition[1] == 0 || finalPosition[1] == this.getNrSquares() - 1) {//Final Position on Piece is not on edge of board
            return false;
        }
        Board fakeBoard = new Board(this);
        for (int i = 0; i < move.length - 3; i += 2) {
            int[] start = {move[i], move[i + 1]};
            int[] next = {move[i + 2], move[i + 3]};
            makeMoveFakeBoard(start, next, fakeBoard);
        }
        // case down, left
        int[] pos = {finalPosition[0] - 1, finalPosition[1] - 1};
        if (fakeBoard.canCaptureInOneJump(pos)) {
            return true;
        }
        // case down, right
        pos = new int[]{finalPosition[0] - 1, finalPosition[1] + 1};
        if (fakeBoard.canCaptureInOneJump(pos)) {
            return true;
        }
        // case up, left
        pos = new int[]{finalPosition[0] + 1, finalPosition[1] - 1};
        if (fakeBoard.canCaptureInOneJump(pos)) {
            return true;
        }
        // case up, right/
        pos = new int[]{finalPosition[0] + 1, finalPosition[1] + 1};
        return fakeBoard.canCaptureInOneJump(pos);
    }

    /**
     * translates integers into letters, helper function for giveMove()
     *
     * @return letter, gives the letter which corresponds to the int
     */
    private String getCharFromInt(int i) {
        return String.valueOf((char) (i + 97));
    }

    /**
     * @param moveAsList gets the move in list form [1,3,4,5] which would get translated to [b3]X[e5]
     * @return moveString, returns the move in string form, that can be fed right into the makeMove() function
     */
    public String MoveListToString(int[] moveAsList) {
        assert moveAsList != null && moveAsList.length > 0;
        return "[" + getCharFromInt(moveAsList[1]) + (moveAsList[0] + 1) + "]X[" + getCharFromInt(moveAsList[3]) + (moveAsList[2] + 1) + "]";
    }
}