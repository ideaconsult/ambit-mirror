/* QMRFSubChapterReferenceEditor.java
 * Author: Nina Jeliazkova
 * Date: Mar 4, 2007 
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

package ambit.data.qmrf;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import ambit.data.IAmbitEditor;

public class QMRFSubChapterReferenceEditor extends QMRFSubChapterTextEditor {
    public QMRFSubChapterReferenceEditor(QMRFSubChapterReference chapter, boolean editable) {
        super(chapter,editable);
    }

    protected JComponent[] createJComponents() {
    	/*
        IAmbitEditor e = 
            new CatalogReferenceEditor(((QMRFSubChapterReference)chapter).getCatalogReference(),
                    JSplitPane.VERTICAL_SPLIT,false);
    	*/
        IAmbitEditor e = 
            new CatalogReferenceEditor(((QMRFSubChapterReference)chapter).getCatalogReference(),
                    false);    	
        return new JComponent[] {e.getJComponent()};
    }
    

}
