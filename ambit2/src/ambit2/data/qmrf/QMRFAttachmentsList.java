/* QMRFAttachmentsList.java
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

package ambit2.data.qmrf;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.io.AmbitFileFilter;
import ambit2.io.MyIOUtilities;
import ambit2.io.XMLException;

public class QMRFAttachmentsList extends ArrayList<QMRFAttachment> implements InterfaceQMRF {
    protected String name;
    protected String itemID;
    protected String title;
    public synchronized String getTitle() {
        return title;
    }
    public synchronized void setTitle(String title) {
        this.title = title;
    }
    public QMRFAttachmentsList(String collectionID, String itemID, String title) {
        super();
        setName(collectionID);
        setItemID(itemID);
        setTitle(title);
    }
    public void fromXML(Element xml) throws XMLException {
        NodeList children = xml.getElementsByTagName(itemID);
        for (int k=0; k < children.getLength(); k++) 
            if (Node.ELEMENT_NODE==children.item(k).getNodeType()) {
                QMRFAttachment a = new QMRFAttachment(children.item(k).getNodeName());
                a.fromXML((Element)children.item(k));
                add(a);
            }
    }

    public Element toXML(Document document) throws XMLException {
        Element a_element = document.createElement(name);
        for (int j=0; j < size(); j++) 
            a_element.appendChild(get(j).toXML(document));
        return a_element;
    }

    public void setName(String name) {
        this.name = name;
        
    }
    public synchronized String getName() {
        return name;
    }
    public synchronized String getItemID() {
        return itemID;
    }
    public synchronized void setItemID(String itemID) {
        this.itemID = itemID;
    }
    public void add(File file) throws Exception {
    	QMRFAttachment q = new QMRFAttachment(itemID,file);

    	//q.setEmbedded(true);
    	q.setEmbedded(false);
        add(q);
    }
    

}
