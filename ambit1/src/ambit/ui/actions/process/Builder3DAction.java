/* Builder3DAction.java
 * Author: Nina Jeliazkova
 * Date: Jul 12, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit.ui.actions.process;

import javax.swing.Icon;
import javax.swing.JFrame;

import ambit.database.data.AmbitDatabaseToolsData;
import ambit.io.FastTemplateHandler3D;
import ambit.processors.Builder3DProcessor;
import ambit.processors.IAmbitProcessor;

/**
 * Generates 3D structure of the current compound by {@link ambit.processors.Builder3DProcessor}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 12, 2006
 */
public class Builder3DAction extends MoleculeCalculationAction {
    /**
     * @param userData
     * @param mainFrame
     */
    public Builder3DAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Builder 3D");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     */
    public Builder3DAction(Object userData, JFrame mainFrame, String name) {
        this(userData, mainFrame, name,null);
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     * @param icon
     */
    public Builder3DAction(Object userData, JFrame mainFrame, String name,
            Icon icon) {
        super(userData, mainFrame, name, icon);
        interactive = false;
    }
    /* (non-Javadoc)
     * @see ambit.ui.actions.BatchAction#getProcessor()
     */
    public IAmbitProcessor getProcessor() {
        if (userData instanceof AmbitDatabaseToolsData) {
	        return new Builder3DProcessor(((AmbitDatabaseToolsData) userData).getTemplateHandler());
        } else return new Builder3DProcessor(FastTemplateHandler3D.getInstance());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Build 3D";
    }
}
