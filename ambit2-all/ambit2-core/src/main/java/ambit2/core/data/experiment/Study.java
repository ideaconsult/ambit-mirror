/* Study.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-2 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit2.core.data.experiment;

import java.util.Hashtable;

import ambit2.base.data.AmbitBean;
import ambit2.base.data.Template;

/**
 * A single study, defined by its template {@link Template} and conditions. 
 * The template defines only condition names (e.g. "Species", "Duration") and here in the {@link Study} class 
 * the condition values have to be specified. (e.g.  Species="Fathead Minnow", "Duration"="96h").
 * Conditions are stored in a {@link java.util.Hashtable}. 
 * Also, study results are stored in another {@link Hashtable}, but this is for completeness, since the actual results per molecule are stored in. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-2
 */
public class Study extends AmbitBean {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1502943233991253317L;
	protected Template template = null;
    protected Hashtable studyResults = null;
    protected String name;
    protected int id;
    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Study() {
        super();
    }

    /**
     * @param name
     */
    public Study(String name,Template template) {
        super();
        setName(name);
        studyResults = new Hashtable();
        setTemplate(template);
        
    }

    /**
     * @param name
     * @param id
     */
    public Study(String name, int id,Template template) {
        this(name,template);
        setId(id);
        studyResults = new Hashtable();
        setTemplate(template);
    }

    public synchronized Template getTemplate() {
        return template;
    }
    public synchronized void setTemplate(Template template) {
        this.template = template;
        setName(template.getName());
        
        if (studyResults != null) studyResults.clear();
        else studyResults = new Hashtable();
       
    }

    /* (non-Javadoc)
     * @see ambit2.data.AmbitObject#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof Study) {
            return super.equals(obj) && template.equals(((Study)obj).template) 
            ;
        } else return false;
    }
    /* (non-Javadoc)
     * @see ambit2.data.AmbitObject#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        Study study = (Study)super.clone();
        study.setTemplate(template);
        if (studyResults != null) study.setStudyResults((Hashtable)studyResults.clone());
        else study.setStudyResults(null);        
        return study;
    }
    public void clear() {
    }
    public String toString() {
    	return getName() + "\n" + template.getName();
    }
    
	public Hashtable getStudyResults() {
		return studyResults;
	}

	public void setStudyResults(Hashtable studyResults) {
        if (studyResults == null) studyResults = new Hashtable();
        this.studyResults.clear();
        this.studyResults.putAll(studyResults);
	}
	public String getTemplateName() {
		if (template == null) template = new Template("");
		return getTemplate().getName();
	}
	public void setTemplateName(String templatename) {
		if (template == null) template = new Template("");
		getTemplate().setName(templatename);
	}	
}

