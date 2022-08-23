package ch.uzh.ifi.SoftCon.Group14_A3_E3;

/**
 * Makes and launches a Checkers game.
 */
public class Game{

    public enum Player {
        Red, // Red Player
        White // White Player
    }

    MainFrame mainFrame;
    public Game() {

    }

    /**
     * Creates a new Frame, sets it up
     */
    public void launch() {
        mainFrame = new MainFrame(this);
        mainFrame.setup();
    }

    /**
     * Main method of game.
     * @param args
     * arguments from command line arguments (ignored)
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.launch();

    }


}
