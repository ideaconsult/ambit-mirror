/* QMRFAttachments.java
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit2.io.XMLException;
import ambit2.ui.editors.IAmbitEditor;

public class QMRFAttachments extends QMRFSubChapterText {
    protected static final String[][] attachments_id = 
            {{"attachment_training_data","molecules","Training data"},
            {"attachment_validation_data","molecules","Validation data"},
            {"attachment_documents","document","Other documents"} };
    protected QMRFAttachmentsList[] attachments;

	public QMRFAttachments(String elementID) {
        super(elementID);
        attachments = new QMRFAttachmentsList[attachments_id.length];
        for (int i=0; i < attachments_id.length; i++)
            attachments[i] = new QMRFAttachmentsList(attachments_id[i][0],
                    attachments_id[i][1],attachments_id[i][2]);
    }
    @Override
    public void clear() {
        for (int i=0; i < attachments_id.length; i++)
            attachments[i].clear();
    }
    public void fromXML(Element xml) throws XMLException {
        super.fromXML(xml);
        for (int i=0; i < attachments_id.length; i++) {
            NodeList nodes = xml.getElementsByTagName(attachments_id[i][0]);
            
            for (int j=0; j < nodes.getLength(); j++) 
                if (Node.ELEMENT_NODE==nodes.item(j).getNodeType()) 
                    attachments[i].fromXML((Element)nodes.item(j));
        }

    }

    public Element toXML(Document document) throws XMLException {
        Element element = super.toXML(document);
        for (int i=0; i < attachments_id.length; i++) 
            element.appendChild(attachments[i].toXML(document));
        return element;
    }
    protected QMRFAttachment addAttachment(int index,QMRFAttachment attachment) {
        if ((index >=0) && (index < attachments_id.length)) {
            attachment.setName(attachments_id[index][1]);
            attachments[index].add(attachment);
            return attachment;
        } else return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QMRFAttachments) {
            QMRFAttachments a = (QMRFAttachments) obj;
            return super.equals(obj) &&
                    (a.attachments[0].toString().equals(attachments[0].toString())) &&
                    (a.attachments[1].toString().equals(attachments[1].toString())) &&
                    (a.attachments[2].toString().equals(attachments[2].toString()));
        } else return super.equals(obj);
    }
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(super.toString());
        for (int i=0; i < attachments_id.length; i++) {
            b.append(attachments_id[i][0]);
            b.append("\n");
            for (int j=0; j < attachments[i].size(); j++) {
                b.append(Integer.toString(j+1));
                b.append(".");
                b.append(attachments_id[i][1]);
                b.append("\t");
                b.append(attachments[i].get(j).toString());
                b.append("\n");
            }
            
        }    
        return b.toString();
    }
    @Override
    public IAmbitEditor editor(boolean isEditable) {
        return new QMRFAttachmentsEditor(this, editable); 
    }
    public synchronized QMRFAttachmentsList[] getAttachments() {
        return attachments;
    }

}
