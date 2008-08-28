package nplugins.shell.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jgoodies.binding.beans.Model;

public class TaskMonitor extends Model implements PropertyChangeListener {
	/**
     * 
     */
    private static final long serialVersionUID = 3016682708344138145L;

    public void propertyChange(PropertyChangeEvent arg0) {
        firePropertyChange(arg0);
        
    }
}
