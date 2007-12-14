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

package ambit.applications.discovery;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JFrame;

import ambit.domain.ADomainMethodType;
import ambit.domain.DataModule;
import ambit.ui.actions.ActionFactory;
import ambit.ui.actions.AmbitAction;

/**
 * An {@link ambit.ui.actions.ActionFactory} descendant to create specific {@link javax.swing.Action}s for {@link ambit.applications.discovery.AmbitDiscoveryApp}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class AmbitDiscoveryActionFactory extends ActionFactory {

	public AmbitDiscoveryActionFactory() {
		super();
	}
    public static AmbitAction createFileOpenAction(Object userData, JFrame mainFrame) {
    	return new ambit.applications.discovery.FileOpenAction(userData,mainFrame,true);
    }
    public static void createMethodAction(ActionMap actions, Object userData, JFrame mainFrame) {
        for (int f=0; f < ADomainMethodType.methodName.length; f++) {
        	AmbitAction a = new AmbitAction(userData,mainFrame,ADomainMethodType.methodName[f]) {
        		public void actionPerformed(ActionEvent arg0) {
        			try {
        			((DataModule) userData).setMethod(arg0.getActionCommand());
        			} catch (Exception x) {
        				
        			}
        		};
        	};
        	actions.put(ADomainMethodType.methodName[f],a);
        }    

    }
}


