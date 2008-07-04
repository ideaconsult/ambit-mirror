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

import java.awt.Cursor;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;

/**
 * An extension of {@link AbstractAction}.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public abstract class NPBaseAction<T> extends AbstractAction implements Comparable {
	protected ActionMap actions = null;
	protected String group;
	protected int orderInGroup = 0;
	protected static Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    protected static Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3259956609977047146L;

	public NPBaseAction() {
		this("NA");
	}

	public NPBaseAction(String arg0) {
		this(arg0,null,"Default");
	}

	public NPBaseAction(String arg0, Icon arg1, String group) {
		super(arg0, arg1);
		this.group = group;
	}

	public void setActions(ActionMap actions) {
		this.actions = actions;
	}


    public void enableActions(boolean enable) {
    	if (actions != null) {
        	Object[] k = actions.allKeys();
        	if (k!=null)
        		for (int i=0; i < k.length; i++)actions.get(k[i]).setEnabled(enable);
    	}
        setEnabled(enable);
    }

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
    
    public synchronized int getOrderInGroup() {
        return orderInGroup;
    }
    public synchronized void setOrderInGroup(int orderInGroup) {
        this.orderInGroup = orderInGroup;
    }
    public int compareTo(Object arg0) {
        int r = -1;
        if (arg0 instanceof NPBaseAction) {
            NPBaseAction ma = (NPBaseAction) arg0;
            r = getGroup().compareTo(ma.getGroup());
            if (r ==0) {
                r = getOrderInGroup() - ma.getOrderInGroup();
            }
        } 
        return r;
    }
}
