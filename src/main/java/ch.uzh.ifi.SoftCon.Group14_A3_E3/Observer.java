package ch.uzh.ifi.SoftCon.Group14_A3_E3;

public interface Observer {
    void updatePlayer(Game.Player currentPlayer, OpponentStrategy strategy);
    void updateLabel(String str);
}
