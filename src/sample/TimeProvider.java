package sample;

public interface TimeProvider {
    void addTimeChangeListener(TimerChangeListener pl);

    void addTimeChangeListener(TimerChangeListener pl, String prop);

    void removeTimeChangeListener(TimerChangeListener pl);
}
