package ch.uzh.ifi.SoftCon.Group14_A3_E3;

public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}
