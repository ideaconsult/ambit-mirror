package nplugins.demo;

import java.beans.PropertyChangeEvent;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import nplugins.core.NPluginsException;
import nplugins.shell.INanoPlugin;
import nplugins.shell.PluginMainPanel;
import nplugins.shell.application.NPluginsAction;
import nplugins.shell.application.Task;
import nplugins.shell.application.TaskMonitor;
import nplugins.shell.application.Utils;

public class DemoPlugin implements INanoPlugin {
	protected String name="demo";
    public DemoPlugin() {
        try {
        Thread.sleep(1000);
        } catch (Exception x) {
            
        }
    }
	public PluginMainPanel createMainComponent() {
		return new DemoMainPanel(this);
	}

	public JComponent[] createDetailsComponent() {
	    return null;
	}

	public JComponent[] createOptionsComponent() {
        ActionMap a = getActions();
        Object[] keys = a.keys();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
        for (int i=0; i < keys.length;i++)
            p.add(new JButton(a.get(keys[i])));
        return new JComponent[] {p};
	}

	public ActionMap getActions() {
		ActionMap map = new ActionMap();
        NPluginsAction action =  new NPluginsAction<Integer,Integer>(
                new Task<Integer, Integer>() {
           public Integer execute(TaskMonitor monitor) throws NPluginsException {
               int i = 0; 
               int count = 10; 
               while (!isCancelled() && i < count) {
                 i++; 
                 
                 //publish(new Integer[] { i }); 
                 //setProgress(count * i / count); 
                 try {
                     Thread.sleep(300);
                     throw new Exception("test");
                 } catch (Exception x) {
                     throw new NPluginsException(x);
                 }
               } 

               return count; 
                  }       
           public boolean isCancelled() {
            return false;
           }
           public void cancel() {
           
            
           }
           public void done() {

           }
        },getName(),null);
        map.put(action,action);
        return map;
        
	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	public void setParameters(String[] args) {
		if ((args != null) && (args.length>0))
            setName(args[0]);

	}

	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ResourceBundle getResourceBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	public int compareTo(INanoPlugin arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public ImageIcon getIcon() {
		return Utils.createImageIcon("nplugins/shell/resources/busyicons/idle-icon.png");
	}
	@Override
	public String toString() {
	    return name;
	}
    public synchronized String getName() {
        return name;
    }
    public synchronized void setName(String name) {
        this.name = name;
    }
}
