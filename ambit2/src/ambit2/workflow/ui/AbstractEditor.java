/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.workflow.ui;

import javax.swing.JComponent;

import ambit2.ui.editors.IAmbitEditor;

import com.microworkflow.ui.WorkflowContextListener;

public abstract class AbstractEditor<T> extends WorkflowContextListener implements IAmbitEditor<T>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -642999519451477721L;
	protected T object;
	public JComponent getJComponent() {
		return this;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
		
	}

}


