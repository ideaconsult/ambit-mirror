/* QMRFAttachmentsEditor.java
 * Author: Nina Jeliazkova
 * Date: Feb 24, 2007 
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

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public class QMRFAttachmentsEditor extends QMRFSubChapterTextEditor {

    public QMRFAttachmentsEditor(QMRFAttachments chapter, boolean editable) {
        this(chapter,4);
        setEditable(editable);
        setPreferredSize(new Dimension(200,100));
    }

    public QMRFAttachmentsEditor(QMRFAttachments chapter, int indent) {
        super(chapter, indent);
        //setPreferredSize(new Dimension(300,100));
        /*
        setMinimumSize(new Dimension(200,300));
        setPreferredSize(new Dimension(200,300));
        */
        setPreferredSize(new Dimension(200,100));
    }
    
    @Override
    protected JComponent[] createJComponents() {
    
        QMRFAttachments a = (QMRFAttachments) chapter;
        
        /*
        JTabbedPane p = new JTabbedPane();

        for (int i=0; i < a.getAttachments().length; i++) {
            p.addTab(a.getAttachments()[i].getTitle(),
                    new QMRFAttachmentsBrowser(a.getAttachments()[i]));
        }
        p.setBackground(Color.white);
        p.setPreferredSize(new Dimension(200,200));
        return p;
        */
        JComponent[] c = new JComponent[a.getAttachments().length];
        for (int i=0; i < a.getAttachments().length; i++) {
            c[i] = new QMRFAttachmentsBrowser(a.getAttachments()[i],!a.isReadOnly());
            c[i].setBorder(BorderFactory.createTitledBorder(a.getAttachments()[i].getTitle()));
            c[i].setPreferredSize(new Dimension(200,200));
	        c[i].setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
	        c[i].setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        }
        return c;
    
    }

}
