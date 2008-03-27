/* QMRFFileSaveAction.java
 * Author: Nina Jeliazkova
 * Date: Mar 8, 2007 
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

package ambit2.data.qmrf;

import java.awt.Container;
import java.io.File;

import javax.swing.Icon;

import ambit2.ui.UITools;

public class QMRFFileSaveAction extends QMRFFileSaveAsAction {

    public QMRFFileSaveAction(QMRFData userData, Container mainFrame) {
        this(userData, mainFrame,"Save");

    }

    public QMRFFileSaveAction(QMRFData userData, Container mainFrame, String name) {
        this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/save_16.png"));

    }

    public QMRFFileSaveAction(QMRFData userData, Container mainFrame, String name,
            Icon icon) {
        super(userData, mainFrame, name, icon);

    }
    @Override
    public File selectFile(String defaultDir) {
        File file = new File(getQMRFData().getQmrf().getSource());
        if (file.exists()) return file; 
        else return super.selectFile(defaultDir);
    }

}
