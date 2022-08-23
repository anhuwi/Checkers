package ch.uzh.ifi.SoftCon.Group14_A3_E3;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame implements ActionListener, Observer, KeyListener {

    /**
     * Variables for the whole game
     */
    int size;
    int window_size;
    Game game;
    boolean inProgress = true;
    OpponentStrategy[] opponentStrategies = new OpponentStrategy[2];

    /**
     * Variables for SetupPanel at the beginning of every game
     */
    private enum PlayerType {
        Human,
        CompEasy,
        CompAverage,
        CompHard
    }
    JLayeredPane layeredPane;
    PlayerType[] res = new PlayerType[2];
    boolean player1Chosen = false;
    boolean player2Chosen = false;
    SetUpButton goButton;
    SetUpButton player1ButtonHUMAN;
    SetUpButton player1ButtonAVERAGE;
    SetUpButton player1ButtonEASY;
    SetUpButton player1ButtonHARD;
    SetUpButton player2ButtonHUMAN;
    SetUpButton player2ButtonAVERAGE;
    SetUpButton player2ButtonEASY;
    SetUpButton player2ButtonHARD;
    ButtonGroup buttonGroup1;
    ButtonGroup buttonGroup2;

    /**
     * Variables for the rest of the game
     */
    BoardPanel boardPanel;
    JPanel boardBorder;
    JTextField entry;
    JLabel messageLabel;
    JPanel messagePanel;
    JButton messageButton;
    JButton newGameButton;
    JButton helpButton;
    JTextArea textArea;
    JScrollPane scrollPane;
    private ImageIcon redPawn = new ImageIcon("src/main/pictures/redPawn.png");
    private ImageIcon redKing = new ImageIcon("src/main/pictures/redKing.png");
    private ImageIcon whitePawn = new ImageIcon("src/main/pictures/whitePawn.png");
    private ImageIcon whiteKing = new ImageIcon("src/main/pictures/whiteKing.png");
    private final Board board;
    String input;
    String old_input;
    private Game.Player player;
    private OpponentStrategy strategy;
    private boolean hasEnteredJumpMove = false;
    String playerOut;
    Color lightBrown = new Color(210,180,140); // field color
    Color darkBrown = new Color(160,82,45); // field color

    /**
     * Constructs a new mainFrame object
     * @param g: a Game
     */
    public MainFrame(Game g){
        
        size = 80; // size of one field
        this.game = g;
        // set up of Frame
        this.setTitle("Checkers");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminates application and running code
        this.setLayout(null);
        this.setResizable(true); // prevents window size to be changed
        ImageIcon image = new ImageIcon("src/main/pictures/checkers.png"); // create an image icon
        this.setIconImage(image.getImage()); //change image of frame
        this.getContentPane().setBackground(Color.white);
        this.getRootPane().setDefaultButton(messageButton);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });


        this.board = new Board(player); // make board an instance variable
        this.board.registerObserver(this);
    }

    /**
     * Creates Panels and Panes after the game is initialized
     */
    public void setUpGameBoard() {
        redPawn = new ImageIcon(redPawn.getImage().getScaledInstance(size-10,size-10, Image.SCALE_SMOOTH));
        redKing = new ImageIcon(redKing.getImage().getScaledInstance(size-10,size-10, Image.SCALE_SMOOTH));
        whitePawn = new ImageIcon(whitePawn.getImage().getScaledInstance(size-10,size-10, Image.SCALE_SMOOTH));
        whiteKing = new ImageIcon(whiteKing.getImage().getScaledInstance(size-10,size-10, Image.SCALE_SMOOTH));

        // initialize player and strategy
        player = Game.Player.Red;
        strategy = getPlayerStrategy(player);

        // change mainFrame
        this.setSize(13*size + 12, 10*size + 35);
        this.setLocationRelativeTo(null);

        setUpMessagePanel();
        setUpMessagePanelLightSetup();

        // layeredMessagePane is everything below the board
        JLayeredPane layeredMessagePane = new JLayeredPane();
        JPanel messagePanelLight = setUpMessagePanelLightSetup();
        messagePanelLight.setBackground(lightBrown);
        messagePanelLight.setBounds(0,0, 9*size, size);
        layeredMessagePane.setBounds(0,(9*size), 9*size, size);
        layeredMessagePane.add(messagePanelLight,JLayeredPane.DEFAULT_LAYER);
        layeredMessagePane.add(messagePanel, JLayeredPane.DRAG_LAYER);
        this.add(layeredMessagePane);

        // boardP is the boardPanel + the Labeling + the boardBorder
        JLayeredPane boardP = new JLayeredPane();
        boardP.setBounds(0,0,9*size, 9*size);
        JPanel boardLabelingPanel = setUpBoardLabeling();

        boardPanel = new BoardPanel(size, lightBrown, darkBrown);
        boardPanel.setLayout(null);
        boardPanel.setBackground(new Color(60,60,60));
        boardPanel.setBounds(size/2,size/2,8*size,8*size);

        boardBorder = new JPanel();
        boardBorder.setOpaque(false);
        boardBorder.setLayout(null);
        boardBorder.setBounds(size/2,size/2,8*size,8*size);
        boardBorder.setBorder(BorderFactory.createLineBorder(darkBrown, size/16));

        boardP.add(boardPanel, JLayeredPane.PALETTE_LAYER);
        boardP.add(boardBorder, JLayeredPane.DRAG_LAYER);
        boardP.add(boardLabelingPanel, JLayeredPane.DEFAULT_LAYER);
        this.add(boardP);

        setUpScrollPane();
        this.setVisible(true);

        // initialize first turn
        turn();
    }

    /**
     * Helper function, determines which Piece to draw
     * @param piece Board.Piece, from enum
     * @return ImageIcon
     */
    private ImageIcon imageToBeDrawn(Board.Piece piece){
        if(piece == Board.Piece.R_P) return redPawn;
        else if(piece == Board.Piece.R_K) return redKing;
        else if(piece == Board.Piece.W_P) return whitePawn;
        else if(piece == Board.Piece.W_K) return whiteKing;
        else return null;
    }

    /**
     * returns a Label with the correct Icon
     * @param piece Board.Piece
     * @return JLabel with an Icon
     */
    private JLabel labelPiece(Board.Piece piece){
        JLabel label = new JLabel();
        label.setIcon(imageToBeDrawn(piece));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        return label;
    }

    /**
     * Method to redraw the board (before every Move)
     */
    private void redraw(){
        Board.Piece[][] arrBoard = this.board.getBordArray();
        Component[] components = boardPanel.getComponents();
        for(int i = 0; i<components.length; i++){
            Component component = components[i];
            if (component instanceof JPanel comp) {
                Component[] componentsOfJPanel = comp.getComponents();
                for (Component componentOfJPanel : componentsOfJPanel) {
                    if (componentOfJPanel instanceof JLabel label){
                        label.setVisible(false);
                        comp.remove(label);
                    }
                }
                // add label
                int j = i/8;
                int k = i % 8;
                comp.add(labelPiece(arrBoard[7-k][j]));
            }
        }
    }

    /**
     * SetUp (Frame) to set up the game. Contains all SetUpButtons
     */
    public void setup(){
        window_size = 8*size;
        this.setSize(window_size+12,window_size+35); // DO NOT CHANGE
        this.setLocationRelativeTo(null); // Place in the middle of the screen

        // layeredPane contains everything important
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setBounds(0,0, window_size, window_size);

        JPanel panelBackGround = new BoardPanel(window_size/8, new Color(210,180,140, 108), new Color(160,82,45, 77));
        panelBackGround.setBackground(Color.white);

        // Set up buttons
        buttonGroup1 = new ButtonGroup();
        buttonGroup2  = new ButtonGroup();
        this.goButton = new SetUpButton("Go", window_size);
        this.player1ButtonHUMAN = new SetUpButton("Human", window_size);
        this.buttonGroup1.add(player1ButtonHUMAN);
        this.player1ButtonAVERAGE = new SetUpButton("Average", window_size);
        this.buttonGroup1.add(player1ButtonAVERAGE);
        this.player1ButtonEASY = new SetUpButton("Easy", window_size);
        this.buttonGroup1.add(player1ButtonEASY);
        this.player1ButtonHARD = new SetUpButton("Hard", window_size);
        this.buttonGroup1.add(player1ButtonHARD);
        this.player2ButtonHUMAN = new SetUpButton("Human", window_size);
        this.buttonGroup2.add(player2ButtonHARD);
        this.player2ButtonAVERAGE = new SetUpButton("Average", window_size);
        this.buttonGroup2.add(player2ButtonAVERAGE);
        this.player2ButtonEASY = new SetUpButton("Easy", window_size);
        this.buttonGroup2.add(player2ButtonEASY);
        this.player2ButtonHARD = new SetUpButton("Hard", window_size);
        this.buttonGroup2.add(player2ButtonHARD);
        
        // framePanel - invisible Panel with same size as layeredPane
        JPanel framePanel = new JPanel();
        framePanel.setBounds(0, 0, window_size, window_size);
        framePanel.setLayout(null);
        framePanel.setOpaque(false);

        // panelMenuPanel - is the inner 6x6 Grid
        JPanel panelMenuPanel = new JPanel();
        panelMenuPanel.setBounds(window_size/8, window_size/8, 6*(window_size/8), 6*(window_size/8));
        panelMenuPanel.setLayout(null);
        panelMenuPanel.setOpaque(false);

        // Contains the Message "Welcome to checkers game!"
        JPanel panelMessage = messagePanelSetup();
        panelMessage.setBounds(0,0,6*window_size/8, window_size/8);

        // 1x3 Grid containing "Player 1" or "Player 2"
        JPanel panelP1 = panelPSetup("Player 1");
        JPanel panelP2 = panelPSetup("Player 2");
        panelP1.setBounds(0, window_size/8, 3*window_size/8, window_size/8);
        panelP2.setBounds(3*window_size/8, window_size/8, 3*window_size/8, window_size/8);

        // 1x6 Grid containing Go-Button
        JPanel goPanel = goButtonPanelSetup(goButton);
        goPanel.setBounds(window_size/8, 7*window_size/8, 6*window_size/8, window_size/8);
        framePanel.add(goPanel);

        // 4x3 Grid containing 4 Buttons each
        JPanel panelPlayer1 = playerPanelSetup(buttonGroup1);
        JPanel panelPlayer2 = playerPanelSetup(buttonGroup2);
        panelPlayer1.setBounds(0, 2*window_size/8, 3*window_size/8, window_size/2);
        panelPlayer2.setBounds(3*window_size/8, 2*window_size/8, 3*window_size/8, window_size/2);
        
        // add everything
        panelMenuPanel.add(panelMessage);
        panelMenuPanel.add(panelP1);
        panelMenuPanel.add(panelP2);
        panelMenuPanel.add(panelPlayer1);
        panelMenuPanel.add(panelPlayer2);

        framePanel.add(panelMenuPanel);
        
        layeredPane.add(framePanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(panelBackGround, JLayeredPane.DEFAULT_LAYER); // Background
        
        this.add(layeredPane);
        this.setVisible(true);
    }

    /**
     * Sets up the playerPanel for method setup
     * @param buttonGroup for determining which buttons to add (with 4 buttons)
     * @return JPanel
     */
    private JPanel playerPanelSetup(ButtonGroup buttonGroup){
        JPanel panelPlayer = new JPanel();
        panelPlayer.setOpaque(false);
        panelPlayer.setLayout(null);

        JPanel panel1;
        JPanel panel2;
        JPanel panel3;
        JPanel panel4;

        if(buttonGroup == buttonGroup1){
            panel1 = optionPanelSetup(player1ButtonHUMAN);
            panel2 = optionPanelSetup(player1ButtonEASY);
            panel3 = optionPanelSetup(player1ButtonAVERAGE);
            panel4 = optionPanelSetup(player1ButtonHARD);
        }
        else {
            panel1 = optionPanelSetup(player2ButtonHUMAN);
            panel2 = optionPanelSetup(player2ButtonEASY);
            panel3 = optionPanelSetup(player2ButtonAVERAGE);
            panel4 = optionPanelSetup(player2ButtonHARD);
        }

        panel1.setBounds(0,0, 3* size, size);
        panel2.setBounds(0, size, 3* size, size);
        panel3.setBounds(0,2* size, 3* size, size);
        panel4.setBounds(0,3* size, 3* size, size);

        panelPlayer.add(panel1);
        panelPlayer.add(panel2);
        panelPlayer.add(panel3);
        panelPlayer.add(panel4);

        return panelPlayer;
    }

    /**
     * Helper method for setup
     * @param button Human, Easy, Average or Hard Button
     * @return JPanel, (1x3 Grid)
     */
    private JPanel optionPanelSetup(SetUpButton button){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setOpaque(false);
        Dimension dimension = new Dimension(5*size/2, size/2);
        button.addActionListener(this);
        button.setBounds(size/4, size/4, (int)dimension.getWidth(), (int)dimension.getHeight());
        panel.add(button);
        return panel;
    }

    /**
     * 1x3 grid Panel with Player 1 or Player 2 as label
     * @param s: String
     * @return JPanel
     */
    private JPanel panelPSetup(String s){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(s);
        label.setFont(new Font("Corbel", Font.BOLD, window_size/20));

        panel.add(label);
        label.setVerticalAlignment(JLabel.BOTTOM);
        label.setHorizontalAlignment(JLabel.CENTER);
        return panel;
    }

    /**
     * Panel with Go-Button
     * @param button Go-Button
     * @return JPanel
     */
    private JPanel goButtonPanelSetup(SetUpButton button){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(null);
        Dimension d = new Dimension(11*size/2, size/2);
        button.setBounds(size/4, size/4, (int)d.getWidth(), (int)d.getHeight());
        button.addActionListener(this);
        // disable button at beginning
        button.setEnabled(false);
        panel.add(button);
        return panel;
    }

    /**
     * @return JPanel containing Label saying "Welcome to checkers game!"
     */
    private JPanel messagePanelSetup(){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel("Welcome to checkers game!");
        label.setFont(new Font("Corbel", Font.BOLD, window_size/20));
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);
        return panel;
    }

    /**
     * @return JPanel containing NewGame and Help Buttons
     */
    private JPanel setUpMessagePanelLightSetup() {
        JPanel p = new JPanel();
        p.setBackground(lightBrown);
        p.setBounds(0,0, 9*size, size);
        p.setLayout(null);


        newGameButton = new JButton("New Game");
        newGameButton.setBounds(104 * size/16, 3*size/8 + size/12, size, size/3);
        newGameButton.addActionListener(this);
        newGameButton.setFont(new Font("Microsoft JhengHei UI", Font.BOLD, size/6));
        newGameButton.setBackground(new Color(0xFFB9A08D, true));
        newGameButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        newGameButton.setFocusable(false);
        newGameButton.setOpaque(true);

        helpButton = new JButton("Help");
        helpButton.setBounds(122 * size/16, 3*size/8 + size/12, size, size/3);
        helpButton.addActionListener(this);
        helpButton.setFont(new Font("Microsoft JhengHei UI", Font.BOLD, size/6));
        helpButton.setBackground(new Color(0xFFB9A08D, true));
        helpButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        helpButton.setFocusable(false);
        helpButton.setOpaque(true);

        p.add(newGameButton);
        p.add(helpButton);
        return p;
    }

    /**
     * Sets up a scrollPane to display old moves
     */
    private void setUpScrollPane() {
        textArea = new JTextArea(20, 20);
        textArea.setFont(new Font("Microsoft JhengHei UI", Font.PLAIN, size/6));
        textArea.setEditable(false);
        textArea.setText("""
                Welcome to a checkers game. The Players \s
                are asked to enter move after move. The move\s
                has to be entered in the following form:\s
                [current piece position]X[future piece position]""" + "\n\n");
        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(9*size, 0, 4*size, 10*size);
        this.add(scrollPane);
    }

    /**
     * sets up the MessagePanel, which contains a textField, a Label to display a message and an Enter-Button
     */
    private void setUpMessagePanel() {
        messagePanel = new JPanel();

        messageLabel = new JLabel();
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setVerticalAlignment(JLabel.NORTH);
        messageLabel.setBounds(0,0,9*size, size/2);
        messageLabel.setFont(new Font("Microsoft JhengHei UI Light", Font.BOLD, size/4));

        entry = new JTextField();
        entry.addKeyListener(this);
        entry.setFont(new Font("Microsoft JhengHei UI", Font.BOLD, size/4));
        entry.setBounds(3*size, 3*size/8, size+3*size/8+size/16, size/2);
        entry.setBorder(BorderFactory.createLoweredBevelBorder());

        messageButton = new JButton("Enter");
        messageButton.addActionListener(this);
        messageButton.setBounds(73 * size/16, 3*size/8+size/12, size, size/3);
        messageButton.setBackground(new Color(0xFFB9A08D, true));
        messageButton.setFont(new Font("Microsoft JhengHei UI", Font.BOLD, size/6));
        messageButton.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        messageButton.setFocusable(false);
        messageButton.setOpaque(true);

        messagePanel.setLayout(null);
        messagePanel.setBounds(0,0, 9*size, size);
        messagePanel.setOpaque(false);
        messagePanel.setVisible(false);

        messagePanel.add(messageLabel);
        messagePanel.add(entry);
        messagePanel.add(messageButton);
    }

    /**
     * @return opaque JPanel containing the BoardLabeling
     */
    private JPanel setUpBoardLabeling() {
        JPanel boardLabelPanel = new JPanel();
        boardLabelPanel.setLayout(null);
        boardLabelPanel.setBounds(0,0,size+size*8, size+size*8);
        boardLabelPanel.setBackground(lightBrown);
        char[] c = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        char[] d = {'8', '7', '6', '5', '4', '3', '2', '1'};
        Font font = new Font("Corbel", Font.BOLD, size/4);
        for(int i = 0; i<8; i++){
            JLabel labelUp = new JLabel(String.valueOf(c[i]));
            JLabel labelDown = new JLabel(String.valueOf(c[i]));
            JLabel labelLeft = new JLabel(String.valueOf(d[i]));
            JLabel labelRight = new JLabel(String.valueOf(d[i]));
            labelUp.setVerticalAlignment(JLabel.CENTER);
            labelUp.setHorizontalAlignment(JLabel.CENTER);
            labelUp.setFont(font);
            labelUp.setForeground(darkBrown);
            labelUp.setBounds(size/2 + i*size,0, size, size/2);

            labelDown.setVerticalAlignment(JLabel.CENTER);
            labelDown.setHorizontalAlignment(JLabel.CENTER);
            labelDown.setFont(font);
            labelDown.setForeground(darkBrown);
            labelDown.setBounds(size/2 + i*size,(int)(8.5*size), size, size/2);

            labelLeft.setVerticalAlignment(JLabel.CENTER);
            labelLeft.setHorizontalAlignment(JLabel.CENTER);
            labelLeft.setFont(font);
            labelLeft.setForeground(darkBrown);
            labelLeft.setBounds(0,size/2 + i*size, size/2, size);

            labelRight.setVerticalAlignment(JLabel.CENTER);
            labelRight.setHorizontalAlignment(JLabel.CENTER);
            labelRight.setFont(font);
            labelRight.setForeground(darkBrown);
            labelRight.setBounds((int)(8.5*size),size/2 + i*size, size/2, size);

            boardLabelPanel.add(labelUp);
            boardLabelPanel.add(labelDown);
            boardLabelPanel.add(labelLeft);
            boardLabelPanel.add(labelRight);
        }
        return boardLabelPanel;
    }

    /**
     * Initializes a new turn
     */
    public void turn() {
        redraw();
        if(!inProgress){
            return;
        }

        if (this.player == Game.Player.Red) {
            playerOut = "Player 1 (red), ";
        } else {
            playerOut = "Player 2 (white), ";
        }

        int [] moves;
        if(strategy != null){
            // computer move
            // don't allow for user input:
            messagePanel.setVisible(false);
            moves = strategy.chooseMoveOpponent(board.getAllMoves(this.player), board);
            for (int i = 0; i < moves.length-3; i += 2){
                String input = board.MoveListToString(new int[]{moves[i], moves[i+1], moves[i+2], moves[i+3]});
                board.makeMove(input);
                setTextScrollPanel(input);
            }
            board.checkKing();
            this.isFinished();
            changePlayer();
            turn();
        }

        else {
            // show dialog
            this.setMessage("please enter a move");
            messagePanel.setVisible(true);
            entry.requestFocus();
        }
    }

    /**
     * checks if a game is finished. If so, proceed to finished()
     */
    private void isFinished(){
        if(player== Game.Player.Red){
            //check if player has no pieces or cannot move anymore
            if(board.noPieceLeft(Game.Player.White) || board.notMoveExists(Game.Player.White)){
                setMessage("Congratulations Player 1 (red), you won!");
                finished();
            }
        }else{
            //check if player has no pieces or cannot move anymore
            if(board.noPieceLeft(Game.Player.Red) || board.notMoveExists(Game.Player.Red)){
                setMessage("Congratulations Player 2 (white), you won!");
                finished();
            }
        }
    }

    /**
     * Ask user for a new game
     */
    private void finished() {
        inProgress = false;
        redraw();

        if (this.player == Game.Player.Red) {
            playerOut = "Player 1 (red), ";
        } else {
            playerOut = "Player 2 (white), ";
        }
        int output = JOptionPane.showOptionDialog(null,
                playerOut + " Do you want to play again?",
                "Congratulations "+ playerOut +"you won!",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                null,
                null);
        if(output == JOptionPane.YES_OPTION){
            this.setVisible(false);
            this.dispose();
            // launch new Game
            game.launch();
        }
        else if(output == JOptionPane.NO_OPTION){
            // Trigger Window-Closing event
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        // cancel: do nothing
    }

    /**
     * check if input is syntactically correct
     * @param input: String
     * @return boolean
     */
    private boolean isSyntaxNotValid(String input){
        String allowed_chars = "abcdefgh";
        String allowed_nums = "12345678";
        return (input.length() != 9 ||
                input.charAt(0) != '[' ||
                input.charAt(5) != '[' ||
                input.charAt(3) != ']' ||
                input.charAt(8) != ']' ||
                allowed_chars.indexOf(input.charAt(1)) == -1 ||
                allowed_chars.indexOf(input.charAt(6)) == -1 ||
                allowed_nums.indexOf(input.charAt(2)) == -1 ||
                allowed_nums.indexOf(input.charAt(7)) == -1 ||
                input.charAt(4) != 'X');
    }

    /**
     * Triggered if user has input a move. Checks for validity and executes a move. Triggers another turn
     */
    private void userInputMove(){
        int [][] coordinates;
        old_input = input; // might be null
        input = entry.getText();
        entry.setText("");

        if(!hasEnteredJumpMove){
            // check if input syntax is exactly as specified
            if(isSyntaxNotValid(input)){
                // shorten garbled input
                if(input.length() >= 10){
                    input = input.substring(0,9) + "...";
                }
                setMessage("your entry " + input +" is syntactically invalid"); // turn again, but with same player
            }
            // extensive validity check
            else if(board.isValid(input, player)){
                board.makeMove(input);
                setTextScrollPanel(input);
                coordinates = board.positionCoordinates();
                if((Math.abs(coordinates[0][0] - coordinates[1][0]) == 2 &&
                        Math.abs(coordinates[0][1] - coordinates[1][1]) == 2 &&
                        board.canCaptureInOneJump(coordinates[1]))){
                    setMessage("you may enter another move");
                    hasEnteredJumpMove = true;
                }
                else{
                    // move is over here
                    // change player
                    hasEnteredJumpMove = false;
                    this.isFinished();
                    changePlayer();
                }
                // new turn here
                turn();
            }
        }
        // case last move was a Jump move
        else{
            // check if valid, if not, enter another move
            if(isSyntaxNotValid(input)){
                if(input.length() >= 9){
                    input = input.substring(0,9) + "...";
                }
                setMessage("your entry " + input +" is syntactically invalid");
            }
            // isValidSameTurn allows only for single jump moves
            else if(!board.isValidSameTurn(input, old_input, player)){
                setMessage("please enter a single jump move");
            }
            else {
                board.makeMove(input);
                setTextScrollPanel(input);
                coordinates = board.positionCoordinates();
                if((Math.abs(coordinates[0][0] - coordinates[1][0]) == 2 &&
                        Math.abs(coordinates[0][1] - coordinates[1][1]) == 2 &&
                        board.canCaptureInOneJump(coordinates[1]))){
                    hasEnteredJumpMove = true;
                    setMessage("you may enter another move:");
                }
                else{
                    hasEnteredJumpMove = false;
                    this.isFinished();
                    changePlayer();
                }
                turn();
            }
        }
        board.checkKing();
    }

    /**
     * changes Player (and thus, strategy)
     */
    private void changePlayer() {
        if (player == Game.Player.Red) {
            player = Game.Player.White;
        } else{
            player = Game.Player.Red;
        }
        changeStrategy();
    }

    /**
     * sets strategy to currentPlayer
     */
    private void changeStrategy() {
        strategy = getPlayerStrategy(player);
    }

    /**
     * @param player Game.Player (Red, White)
     * @return strategy
     */
    private  OpponentStrategy getPlayerStrategy(Game.Player player){
        if (player == Game.Player.Red) {
            return opponentStrategies[0];
        } else {
            return opponentStrategies[1];
        }
    }

    /**
     * Appends a move string to the TextPanel
     * @param input: String
     */
    private void setTextScrollPanel(String input) {
        textArea.setText(
                textArea.getText()
                        + " "
                        + playerOut.substring(0, playerOut.length() - 2)
                        + ":\n"
                        + " ".repeat(30)
                        + input + "\n");
    }

    /**
     * Sets the message on the messagePanel
     * @param s: String
     */
    private void setMessage(String s){
        if (this.player == Game.Player.Red) {
            playerOut = "Player 1 (red), ";
        } else {
            playerOut = "Player 2 (white), ";
        }
        this.messageLabel.setText(playerOut + s);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Color color = new Color(0xFF729DDB, true);
        Object source = e.getSource();

        // Cases for Setup
        if(source == this.player1ButtonEASY){
            player1ButtonEASY.setBackground(color);
            res[0] = PlayerType.CompEasy;
            player1ButtonAVERAGE.setBackground(Color.white);
            player1ButtonHARD.setBackground(Color.white);
            player1ButtonHUMAN.setBackground(Color.white);
            player1Chosen = true;
        }
        else if(source == this.player1ButtonAVERAGE){
            player1ButtonEASY.setBackground(Color.white);
            player1ButtonAVERAGE.setBackground(color);
            res[0] = PlayerType.CompAverage;
            player1ButtonHARD.setBackground(Color.white);
            player1ButtonHUMAN.setBackground(Color.white);
            player1Chosen = true;
        }
        else if(source == this.player1ButtonHARD){
            player1ButtonEASY.setBackground(Color.white);
            player1ButtonAVERAGE.setBackground(Color.white);
            player1ButtonHARD.setBackground(color);
            res[0] = PlayerType.CompHard;
            player1ButtonHUMAN.setBackground(Color.white);
            player1Chosen = true;
        }
        else if(source == this.player1ButtonHUMAN){
            player1ButtonEASY.setBackground(Color.white);
            player1ButtonAVERAGE.setBackground(Color.white);
            player1ButtonHARD.setBackground(Color.white);
            player1ButtonHUMAN.setBackground(color);
            res[0] = PlayerType.Human;
            player1Chosen = true;
        }

        if(source == this.player2ButtonEASY){
            player2ButtonEASY.setBackground(color);
            res[1] = PlayerType.CompEasy;
            player2ButtonAVERAGE.setBackground(Color.white);
            player2ButtonHARD.setBackground(Color.white);
            player2ButtonHUMAN.setBackground(Color.white);
            player2Chosen = true;
        }
        else if(source == this.player2ButtonAVERAGE){
            player2ButtonEASY.setBackground(Color.white);
            player2ButtonAVERAGE.setBackground(color);
            res[1] = PlayerType.CompAverage;
            player2ButtonHARD.setBackground(Color.white);
            player2ButtonHUMAN.setBackground(Color.white);
            player2Chosen = true;
        }
        else if(source == this.player2ButtonHARD){
            player2ButtonEASY.setBackground(Color.white);
            player2ButtonAVERAGE.setBackground(Color.white);
            player2ButtonHARD.setBackground(color);
            res[1] = PlayerType.CompHard;
            player2ButtonHUMAN.setBackground(Color.white);
            player2Chosen = true;
        }
        else if(source == this.player2ButtonHUMAN){
            player2ButtonEASY.setBackground(Color.white);
            player2ButtonAVERAGE.setBackground(Color.white);
            player2ButtonHARD.setBackground(Color.white);
            player2ButtonHUMAN.setBackground(color);
            res[1] = PlayerType.Human;
            player2Chosen = true;
        }
        if(player1Chosen && player2Chosen){
            // Enable goButton only when players are chosen correctly
            goButton.setEnabled(true);
        }
        if(source == goButton){
            // If goButton pressed, set up the GameBoard
            if(res[0] == PlayerType.Human) opponentStrategies[0] = null;
            if(res[0] == PlayerType.CompEasy) opponentStrategies[0] = new OpponentEasy();
            if(res[0] == PlayerType.CompAverage) opponentStrategies[0] = new OpponentAverage();
            if(res[0] == PlayerType.CompHard) opponentStrategies[0] = new OpponentDifficult();
            if(res[1] == PlayerType.Human) opponentStrategies[1] = null;
            if(res[1] == PlayerType.CompEasy) opponentStrategies[1] = new OpponentEasy();
            if(res[1] == PlayerType.CompAverage) opponentStrategies[1] = new OpponentAverage();
            if(res[1] == PlayerType.CompHard) opponentStrategies[1] = new OpponentDifficult();

            layeredPane.setVisible(false);
            this.remove(layeredPane);
            this.setVisible(false);
            setUpGameBoard();
        }

        // Cases in game
        if(source == messageButton) {
            userInputMove();
            entry.requestFocus();
        }
        else if(source == newGameButton){
            this.setVisible(false);
            this.dispose();
            game.launch();
        }
        else if(source == helpButton){
            String[] options = {"Got it"};
            JOptionPane.showOptionDialog(this,
                """
                        - The checkerboard is an 8x8 grid of light and dark squares in the famous “checkerboard” pattern. Each
                          player has a dark square on the far left and a light square on his far right. The double-corner sometimes
                          mentioned is the distinctive pair of dark squares in the near right corner.
                        - The red player moves first.
                        - A player must move each turn. If the player cannot move, the player loses the game.
                        - In each turn, a player can make a simple move, a single jump, or a multiple jump move.
                          i Simple move: Single pieces can move one adjacent square diagonally forward away from the player.
                          A piece can only move to a vacant dark square.
                          ii Single jump move: A player captures an opponent’s piece by jumping over it, diagonally, to an
                          adjacent vacant dark square. The opponent’s captured piece is removed from the board. The player
                          can never jump over, even without capturing, one of the player’s own pieces. A player cannot jump
                          the same piece twice.
                          iii Multiple jump move: Within one turn, a player can make a multiple jump move with the same piece
                          by jumping from vacant dark square to vacant dark square. The player must capture one of the
                          opponent’s pieces with each jump. The player can capture several pieces with a move of several
                          jumps.
                        - If a jump move is possible, the player must make that jump move. A multiple jump move must be
                          completed. The player cannot stop part way through a multiple jump. If the player has a choice of
                          jumps, the player can choose among them, regardless of whether some of them are multiple, or not.
                        - When a single piece reaches the row of the board furthest from the player, i.e the king-row, by reason
                          of a simple move, or as the completion of a jump, it becomes a king. This ends the player’s turn. The
                          opponent crowns the piece by placing a second piece on top of it.
                        - A king follows the same move rules as a single piece except that a king can move and jump diagonally
                          forward away from the player or diagonally backward toward the player. Within one multiple jump
                          move, the jumps can be any combination of forward or backward jumps. At any point, if multiple
                          jumps are available to a king, the player can choose among them.
                        - A player who loses all of their pieces to captures loses the game.
                        
                        - You must enter a move in the following format: [current position]X[future position], e.g [b6]X[c5]""",
                "Help",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        }
    }

    @Override
    public void updatePlayer(Game.Player currentPlayer, OpponentStrategy strategy) {
        this.player = currentPlayer;
        this.strategy = strategy;
    }

    @Override
    public void updateLabel(String str) {
        this.setMessage(str);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            userInputMove();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
