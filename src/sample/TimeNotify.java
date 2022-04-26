package sample;

import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeNotify
        extends TimerTask
        implements TimeProvider {

    PropertyChangeSupport pcs = new PropertyChangeSupport(this) ;

    int secondes;

    public TimeNotify() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(this, 100, 100);
        setTimeValues();
    }

    private void setTimeValues() {
        LocalTime localTime = LocalTime.now();

        setSecondes(localTime.getSecond());
    }

    @Override
    public void run() {
        timeChanged();
    }

    List<TimerChangeListener> listeners = new LinkedList<>();

    @Override
    public void addTimeChangeListener(TimerChangeListener pl) {
        pcs.addPropertyChangeListener(pl);
    }

    @Override
    public void addTimeChangeListener(TimerChangeListener pl, String prop) {
        pcs.addPropertyChangeListener(prop,pl );
    }

    @Override
    public void removeTimeChangeListener(TimerChangeListener pl) {
        pcs.removePropertyChangeListener(pl);
    }

    private void timeChanged() {
        setTimeValues();
    }

    public void setSecondes(int newSecondes) {
        if (secondes == newSecondes)
            return;

        int oldValue = secondes;
        secondes = newSecondes;

        secondesChanged(oldValue, secondes);
    }

    private void secondesChanged(int oldValue, int secondes) {
        pcs.firePropertyChange(TimerChangeListener.SECONDE_PROP, oldValue, secondes);
    }

}
