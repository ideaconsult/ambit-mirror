/*
Copyright (C) 2005-2007  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package nplugins.shell.application;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.Icon;

/**
 * 
 * Base implementation . 
 * <br>
 * 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-4
 */
public class NPluginsAction<T,V> extends NPBaseAction {
	protected static Logger logger = Logger.getLogger("nplugins.shell");
	protected TaskMonitor taskMonitor;
	protected Task<T,V> task;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5979583679466214875L;

	public NPluginsAction(Task<T,V> task) {
		this(task,"Go!","Default");
	}

	public NPluginsAction(Task<T,V> task, String arg0, String group) {
		this(task,arg0,null, group);
	}

	public NPluginsAction(Task<T,V> task, String arg0, Icon arg1, String group) {
		super(arg0, arg1, group);
		setTask(task);
	}

	
	public void actionPerformed(final ActionEvent event) {
        enableActions(false);
	    SwingWorker<T, V> worker = new SwingWorker<T, V>() {
            @Override
            protected T doInBackground() throws Exception {
                
                return task.execute(taskMonitor);
            }
            @Override
            protected void process(List<V> chunks) {
                for (V i : chunks) 
                    System.out.println(i); 
            }
            
            @Override
            protected void done() {
                task.done();                
                super.done();
                enableActions(true);
            }
        };
        worker.addPropertyChangeListener(taskMonitor);
        worker.execute();
        
    }


	public ActionMap getActions() {
		return actions;
	}

    public synchronized Task getTask() {
        return task;
    }

    public synchronized void setTask(Task task) {
        this.task = task;
    }

    public synchronized TaskMonitor getTaskMonitor() {
        return taskMonitor;
    }

    public synchronized void setTaskMonitor(TaskMonitor taskMonitor) {
        this.taskMonitor = taskMonitor;
    }
    
}
