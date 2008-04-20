/* AbstractQMRFChapter.java
 * Author: Nina Jeliazkova
 * Date: Feb 21, 2007 
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

import java.text.BreakIterator;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ambit2.io.XMLException;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.data.AmbitObject;

public class AbstractQMRFChapter extends AmbitObject {
	protected boolean readOnly = false;
    protected QMRFAttributes attributes;    

    public AbstractQMRFChapter() {
        this("chapter");
    }
    public AbstractQMRFChapter(String elementID) {
        this(elementID,"0","Title","Help");
    }    
    public AbstractQMRFChapter(String elementID, String chapter,String title,String help) {
        super(elementID);
        attributes = new QMRFAttributes(); //new String[] {"chapter","name","help"});
        setChapter(chapter);
        setTitle(title);
        setHelp(help);
        
    }
    public String getWrappedHelp(int maxchars) {
    	String source = getHelp();
    	BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(source);

    	StringBuffer wrappedHelp = new StringBuffer();
        wrappedHelp.append("<html><body>");
        int chars = 0;
        int start = boundary.first();
        for (int end = boundary.next();
	        end != BreakIterator.DONE;
	        start = end, end = boundary.next()) {
        	if ((chars >= maxchars) || ((chars + end - start) > maxchars)) {
        		wrappedHelp.append("<p>");
        		chars = 0;
        	}
        	wrappedHelp.append(source.substring(start,end));
        	chars += (end - start);
        }
        wrappedHelp.append("</body></html>");
        return wrappedHelp.toString();
    }    
/*    
    public String getWrappedHelp(int chars) {
    	String help = getHelp();
    	StringBuffer wrappedHelp = new StringBuffer();
        wrappedHelp.append("<html><body>");
        int i = 0;
        while (i<help.length()) {
        	int end = i+chars-1;
        	if (end >= help.length())
        		wrappedHelp.append(help.substring(i));
        	else	
        		wrappedHelp.append(help.substring(i,i+chars));
        	wrappedHelp.append("<p>");
        	i+=chars;
        }
        wrappedHelp.append("</body></html>");
        return wrappedHelp.toString();
    }
*/    
    public String getHelp() {
    	return attributes.get("help");
    }

    public void setHelp(String help) {
    	attributes.put("help",help);
    }

    public String getChapter() {
    	return attributes.get("chapter");
    }

    public void setChapter(String chapter) {
    	attributes.put("chapter",chapter);
    }
    public String getTitle() {
        return attributes.get("name");
    }

    public void setTitle(String title) {
        attributes.put("name",title);
    }
    @Override
    public boolean equals(Object obj) {
        return compareTo(obj)==0;
    }
    @Override
    public int compareTo(Object o) {
    	int r = -1;
    	if (o instanceof QMRFChapter) {
    		AbstractQMRFChapter q = (AbstractQMRFChapter) o;
    		r = this.compareTo(o);
    		if (r==0) {
    			r = getChapter().compareTo(q.getChapter());
    			if (r==0) {
    				r = getName().compareTo(q.getName());
    			}
    			
    		}
            return r;
    	} else return getChapter().compareTo(o.toString());
    	
    }

    @Override
    public String toString() {
    	StringBuffer b = new StringBuffer();
    	b.append("Section ");
    	b.append(getChapter());
    	b.append(".");
    	
    	return b.toString();
    }

    public void fromXML(Element xml) throws XMLException {
    	clear();
    	setName(xml.getNodeName());
    	attributes.fromXML(xml);
    }

    public Element toXML(Document document) throws XMLException {
    	Element element = document.createElement(getName());
    	attributes.toXML(element);
    	return element;
    }
    @Override
    public IAmbitEditor editor(boolean editable) {
        return new AbstractQMRFChapterEditor(this) {
        	@Override
        	protected JComponent[] createJComponents() {
        		return new JComponent[] {new JLabel("")};
        	}
       	
        };
    }
    @Override
    public void setName(String name) {

    	super.setName(name);
    }
    /*
	public QMRFAttributes getAttributes() {
		return attributes;
	}
    
	public void setAttributes(QMRFAttributes attributes) {
		this.attributes = attributes;
	}
    */ 
    public String getAttribute(String attribute) {
        return attributes.get(attribute);
    }
    
    public void setAttribute(String attribute, String value) {
        attributes.put(attribute, value);
        setModified(true);
    }
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}

