package ch.uzh.ifi.SoftCon.Group14_A3_E3;

import javax.swing.JPanel;
import java.awt.Color;

/**
 * Class creates the BoardPanel (The checkerboard)
 */
public class BoardPanel extends JPanel {

    JPanel[][] boardPanels; // array of Panels (fields)
    int size;

    public BoardPanel(int size, Color darkBrown, Color lightBrown){
        super();
        boardPanels = new JPanel[8][8];
        this.size = size;
        this.setLayout(null);

        this.setBackground(new Color(60,60,60));
        this.setBounds(0,0,8*size,8*size);

        for (int i = 0; i<8; i++) {
            for (int j = 0; j < 8; j++) {
                boardPanels[i][j] = new JPanel();
            }
        }

        for (int j=1; j<8; j+=2){
            boardPanels[1][j].setBackground(darkBrown);
            boardPanels[3][j].setBackground(darkBrown);
            boardPanels[5][j].setBackground(darkBrown);
            boardPanels[7][j].setBackground(darkBrown);

            boardPanels[2][j].setBackground(lightBrown);
            boardPanels[4][j].setBackground(lightBrown);
            boardPanels[6][j].setBackground(lightBrown);
            boardPanels[0][j].setBackground(lightBrown);
        }
        for (int j=0; j<8; j+=2){
            boardPanels[1][j].setBackground(lightBrown);
            boardPanels[3][j].setBackground(lightBrown);
            boardPanels[5][j].setBackground(lightBrown);
            boardPanels[7][j].setBackground(lightBrown);

            boardPanels[0][j].setBackground(darkBrown);
            boardPanels[2][j].setBackground(darkBrown);
            boardPanels[4][j].setBackground(darkBrown);
            boardPanels[6][j].setBackground(darkBrown);
        }

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                this.add(boardPanels[i][j]);
                boardPanels[i][j].setBounds(i*size, j*size, size, size);
            }
        }
    }

}
