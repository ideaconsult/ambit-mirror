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

package ambit.data.qmrf;

import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ambit.data.AmbitList;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitEditor;
import ambit.data.IAmbitObjectListener;
import ambit.io.XMLException;

public class QMRFChapter extends AbstractQMRFChapter implements InterfaceQMRF, IAmbitObjectListener {
	protected AmbitList subchapters;
    protected Hashtable<String, Catalog> catalogs;
    
	public synchronized Hashtable<String, Catalog> getCatalogs() {
        return catalogs;
    }
    public synchronized void setCatalogs(Hashtable<String, Catalog> catalogs) {
        this.catalogs = catalogs;
    }
    public QMRFChapter() {
		this("chapter");
	}
    public QMRFChapter(String elementID) {
        this(elementID,"0","Title","Help");
    }    
	public QMRFChapter(String elementID, String chapter,String title,String help) {
		super(elementID,chapter,title,help);
		//subchapters = new ArrayList<AbstractQMRFChapter>();
        subchapters = new AmbitList();

	}
    public void addSubchapter(AbstractQMRFChapter subchapter ) {
        subchapters.addItem(subchapter);
        subchapter.addAmbitObjectListener(this);
    }
    @Override
    public void clear() {
        super.clear();
        for (int i=0; i < subchapters.size();i++)
            subchapters.getItem(i).removeAmbitObjectListener(this);        
        subchapters.clear();

    }
    public void fromXML(Element xml) throws XMLException {
        super.fromXML(xml);
        NodeList children = xml.getChildNodes();
        
        String[] yesno = new String[]{"Yes","No"};
        String[] more = new String[]{"All","Some","No","Unknown"};
        
        Hashtable<String,String[]> option = new Hashtable<String,String[]>(); 
        	option.put("training_set_availability",yesno);
        	option.put("training_set_descriptors",more);
        	option.put("dependent_var_availability",more);
        	option.put("validation_set_availability",yesno);
        	option.put("validation_set_descriptors",more);
        	option.put("validation_dependent_var_availability",more);
        
        
        for (int i=0; i < children.getLength();i++) {
            if (Node.ELEMENT_NODE==children.item(i).getNodeType()) {
                //System.out.println(children.item(i).getNodeName());
            	String name = children.item(i).getNodeName();
            	if (name == null) continue;
            	AbstractQMRFChapter subchapter ;
            	if (option.containsKey(name))
            		subchapter = new QMRFSubChapterQuestion("answer",option.get(name));
            	else
            		if ("training_set_data".equals(name) || "validation_set_data".equals(name))
            			subchapter = new QMRFSubChapterDataset(name);
            		else if ("attachments".equals(name)) {
                        subchapter = new QMRFAttachments(name);
            		} else if ("model_endpoint".equals(name)) {
                        	subchapter = new QMRFSubChapterReference(name);
                        	((QMRFSubChapterReference) subchapter).setCatalog(
                                    catalogs.get("endpoints_catalog"));
                    } else if ("QSAR_software".equals(name) || "app_domain_software".equals(name) 
                                || "descriptors_generation_software".equals(name)) {
                        subchapter = new QMRFSubChapterReference(name);
                        ((QMRFSubChapterReference) subchapter).setCatalog(
                                catalogs.get("software_catalog"));      
                    } else if ("qmrf_authors".equals(name) || "model_authors".equals(name)) {
                        subchapter = new QMRFSubChapterReference(name);
                        ((QMRFSubChapterReference) subchapter).setCatalog(
                                catalogs.get("authors_catalog"));
                    } else if ("references".equals(name) || "bibliography".equals(name)) {
                        subchapter = new QMRFSubChapterReference(name);
                        ((QMRFSubChapterReference) subchapter).setCatalog(
                                catalogs.get("publications_catalog"));     
                    } else if ("algorithms_descriptors".equals(name)) {
                        subchapter = new QMRFSubChapterReference(name);
                        ((QMRFSubChapterReference) subchapter).setCatalog(
                                catalogs.get("descriptors_catalog"));     
                    } else if ("algorithm_explicit".equals(name)) {
                        subchapter = new QMRFSubchapterAlgorithm(name);
                        ((QMRFSubchapterAlgorithm) subchapter).setCatalog(
                                catalogs.get("algorithms_catalog"));
                    } else if ("QMRF_number".equals(name) || "date_publication".equals(name)){// || "qmrf_date".equals(name) || "qmrf_date_revision".equals(name) || "model_date".equals(name) ) {
                    	subchapter = new QMRFSubChapterDate(name);                   	
                    } else	
            			subchapter = new QMRFSubChapterText(name);
                subchapter.fromXML((Element)children.item(i));
                //editable if parent is 
                subchapter.setEditable(isEditable());
                addSubchapter(subchapter);
            }            
        }        
    }

    public Element toXML(Document document) throws XMLException {
        Element element = super.toXML(document);
        for (int i=0; i < subchapters.size(); i++)
            element.appendChild(((AbstractQMRFChapter)subchapters.getItem(i)).toXML(document));
        return element;
    }
    public synchronized AmbitList getSubchapters() {
        return subchapters;
    }
    public synchronized void setSubchapters(AmbitList subchapters) {
        this.subchapters = subchapters;
    }
    @Override
    public void setNotModified() {
        super.setNotModified();
        for (int i=0; i < subchapters.size();i++) {
            subchapters.getItem(i).setNotModified();
        }
        subchapters.setNotModified();
    }
    @Override
    public IAmbitEditor editor(boolean editable) {
        return new QMRFChapterEditor(this);
    }

    public void ambitObjectChanged(AmbitObjectChanged event) {
        setModified(event.getObject().isModified());
        
    }
}


