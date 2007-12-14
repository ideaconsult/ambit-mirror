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

package ambit.data.experiment;

import java.util.Hashtable;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;
import ambit.exceptions.PropertyNotInTemplateException;
import ambit.ui.data.AbstractAmbitEditor;
import ambit.ui.data.experiment.StudyPropertyTableModel;

/**
 * A single study, defined by its template {@link StudyTemplate} and conditions. 
 * The template defines only condition names (e.g. "Species", "Duration") and here in the {@link Study} class 
 * the condition values have to be specified. (e.g.  Species="Fathead Minnow", "Duration"="96h").
 * Conditions are stored in a {@link java.util.Hashtable}. 
 * Also, study results are stored in another {@link Hashtable}, but this is for completeness, since the actual results per molecule are stored in {@link Experiment}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-2
 */
public class Study extends AmbitObject {
    protected StudyTemplate template = null;
    protected Hashtable studyConditions = null;
    protected Hashtable studyResults = null;

    public Study() {
        super();
    }

    /**
     * @param name
     */
    public Study(String name,StudyTemplate template) {
        super(name);
        studyConditions = new Hashtable();
        studyResults = new Hashtable();
        setTemplate(template);
        
    }

    /**
     * @param name
     * @param id
     */
    public Study(String name, int id,StudyTemplate template) {
        super(name, id);
        studyConditions = new Hashtable();
        studyResults = new Hashtable();
        setTemplate(template);
    }

    public synchronized Hashtable getStudyConditions() {
        return studyConditions;
    }
    public synchronized void setStudyConditions(Hashtable studyConditions) {
        if (studyConditions == null) studyConditions = new Hashtable();
        this.studyConditions.clear();
        this.studyConditions.putAll(studyConditions);
        setModified(true);
        notifyAll();
    }
    public synchronized StudyTemplate getTemplate() {
        return template;
    }
    public synchronized void setTemplate(StudyTemplate template) {
        this.template = template;
        setName(template.getName());
        if (studyConditions != null)  studyConditions.clear();
        else studyConditions = new Hashtable();
        if (template !=null)
        template.getStudyConditions(studyConditions);
        
        if (studyResults != null) studyResults.clear();
        else studyResults = new Hashtable();
        if (template !=null)
        template.getStudyResults(studyResults);        
        setModified(true);
        notifyAll();        
    }
    public void clearStudyConditions() {
    	if (studyConditions != null) studyConditions.clear();
    }
    /**
     * The field has to be one of the study conditions, defined in the template.
     * @param field
     * @param value
     * @throws AmbitException
     */
    public void setStudyCondition(Object field, Object value) throws AmbitException {
        if (template == null) throw new AmbitException("Undefined study template!");
        TemplateField tf = template.getField(field);
        if (tf == null)  throw new PropertyNotInTemplateException("Unknown field "+field);
        if (studyConditions == null) studyConditions = new Hashtable();
        studyConditions.put(tf,value);
    }
    public Object getStudyCondition(Object field) throws AmbitException {
        if (studyConditions == null) return null;
        else return studyConditions.get(field);
    }
    /* (non-Javadoc)
     * @see ambit.data.AmbitObject#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof Study) {
            return super.equals(obj) && template.equals(((Study)obj).template) 
            &&       	studyConditions.equals(((Study)obj).studyConditions)
            ;
        } else return false;
    }
    /* (non-Javadoc)
     * @see ambit.data.AmbitObject#clone()
     */
    public Object clone() throws CloneNotSupportedException {
        Study study = (Study)super.clone();
        study.setTemplate(template);
        if (studyConditions != null) study.setStudyConditions((Hashtable)studyConditions.clone());
        else study.setStudyConditions(null);
        if (studyResults != null) study.setStudyResults((Hashtable)studyResults.clone());
        else study.setStudyResults(null);        
        return study;
    }
    public void clear() {
    	if (studyConditions != null) studyConditions.clear();
    }
    public String toString() {
    	return getName() + "\n" + template.getName() + "\n" + studyConditions.toString();
    }
    
    
	public IAmbitEditor editor(boolean editable) {
		return new AbstractAmbitEditor("Study",this) {
			protected ambit.ui.data.AbstractPropertyTableModel createTableModel(AmbitObject object) {
				 return new StudyPropertyTableModel((Study)object);
			}
		};
	}
	

	public Hashtable getStudyResults() {
		return studyResults;
	}

	public void setStudyResults(Hashtable studyResults) {
        if (studyResults == null) studyResults = new Hashtable();
        this.studyResults.clear();
        this.studyResults.putAll(studyResults);
        setModified(true);

	}
}

