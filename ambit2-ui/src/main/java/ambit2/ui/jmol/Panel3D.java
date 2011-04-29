/*
Copyright (C) 2005-2006  

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

package ambit2.ui.jmol;

import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jmol.api.JmolStatusListener;
import org.jmol.popup.JmolPopup;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IAmbitEditor;

/**
 * Makes use of {@link org.jmol.api.JmolViewer} to display 3D molecule.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class Panel3D extends JPanel implements IAmbitEditor<IAtomContainer> ,PropertyChangeListener {
	   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ImageTools3D painter3D;

	     
	   public Panel3D() {
		 super();
		 Dimension size = new Dimension(400,400);
   	     setPreferredSize(size);
   	     try {
   	    	 painter3D = new ImageTools3D(this,size);
   	     } catch (Exception x) {

   	    	 painter3D = null;
   	     }
 
	   }
	 
	 
	   public void paint(Graphics g) {
          painter3D.paint(g);
	   }
	 
	   public JComponent getJComponent() {
		   return this;
	   }
	   public boolean confirm() {
		return false;
	   }
	   public IAtomContainer getObject() {
		return null;
	   }
	   public void setObject(IAtomContainer mol) {
			 painter3D.setObject(mol);
	   }

	public boolean isEditable() {

		return false;
	}

	public void setEditable(boolean editable) {
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof IAtomContainer)
			setObject((IAtomContainer)evt.getNewValue());
		
	}
}

class StatusListener implements JmolStatusListener {
	protected JmolPopup popup;
	public StatusListener(JmolPopup popup) {
		this.popup = popup;
	}
	public String createImage(String file, String type, Object text_or_bytes,
			int quality) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void createImage(String arg0, String arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	public String eval(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public float[][] functionXY(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void handlePopupMenu(int x, int y) {
		popup.show(x,y);
		
	}

	public void notifyAtomHovered(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void notifyAtomPicked(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void notifyFileLoaded(String arg0, String arg1, String arg2,
			Object arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	public void notifyFrameChanged(int arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}

	public void notifyNewDefaultModeMeasurement(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void notifyNewPickingModeMeasurement(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void notifyResized(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void notifyScriptStart(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void notifyScriptTermination(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void sendConsoleEcho(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void sendConsoleMessage(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void sendSyncScript(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void setCallbackFunction(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void showConsole(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	public void showUrl(String arg0) {
		// TODO Auto-generated method stub
		
	}
	public String dialogAsk(String type, String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
	public Hashtable getRegistryInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	public void notifyCallback(int type, Object[] data) {
		// TODO Auto-generated method stub
		
	}
	public boolean notifyEnabled(int callback_pick) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

