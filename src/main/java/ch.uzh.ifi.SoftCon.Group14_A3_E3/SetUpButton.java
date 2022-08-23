package ch.uzh.ifi.SoftCon.Group14_A3_E3;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

public class SetUpButton extends JButton {
    public SetUpButton(String name, int size){
        this.setText(name);
        this.setBackground(Color.white);
        this.setForeground(new Color(24, 2, 2));
        this.setBorder(BorderFactory.createRaisedSoftBevelBorder());
        this.setFocusable(false);
        this.setFont(new Font("Corbel Light", Font.PLAIN, size/20));
        this.setVerticalAlignment(JButton.CENTER);
        this.setHorizontalAlignment(JButton.CENTER);
    }
}
