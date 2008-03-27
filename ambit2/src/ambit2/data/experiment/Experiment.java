/* Experiment.java
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

package ambit2.data.experiment;

import java.util.Hashtable;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.data.literature.AuthorEntry;
import ambit2.data.literature.LiteratureEntry;
import ambit2.exceptions.AmbitException;
import ambit2.exceptions.PropertyNotInTemplateException;
import ambit2.ui.data.AuthorEntryTableModel;
import ambit2.ui.data.experiment.ExperimentPropertyModel;
import ambit2.ui.editors.AbstractAmbitEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.data.AmbitObject;

/**
 * This class is encapsulating experimental data of a single {@link Study} for a molecule.
 * Results are stored in a {@link java.util.Hashtable} and have to correspond to the {@link ambit2.data.experiment.StudyTemplate}
 * result fields. The template can be accessed by {@link #getStudy()}.{@link Study#getTemplate()}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-2
 */
public class Experiment extends AmbitObject {
    static final String[] structure_types = {"tested","parent","metabolite"};    
    protected int structureType = 0;
    protected Study study = null;	
    protected LiteratureEntry reference = null;
    protected IMolecule molecule = null;
    protected IMolecule parent_compound = null;
    protected Hashtable results = null;
    
    /**
     * Creates an empty experiment
     *
     */
    public Experiment() {
        super();
    }

    /**
     * @param name
     */
    public Experiment(String name) {
        super(name);
    }

    /**
     * @param name
     * @param id
     */
    public Experiment(String name, int id) {
        super(name, id);
    }
    /**
     * Creates an experiment for a molecule, study and literature reference.
     * @param molecule
     * @param study
     * @param reference
     */
    public Experiment(IMolecule molecule, Study study, 	LiteratureEntry reference)  {
        super();
        setMolecule(molecule);
        setStudy(study);
        setReference(reference);
    }
    /**
     * Creates an experiment for a molecule, study, literature reference, parameter and value.<br>
     * @param molecule
     * @param study
     * @param reference
     * @param field  The experiment parameter (e.g. LC50)
     * @param result  The experiment result (e.g. 2.5 )
     * @throws AmbitException
     */
    public Experiment(IMolecule molecule, Study study, 	LiteratureEntry reference,
            Object field, Object result)  throws AmbitException {
        this(molecule,study,reference);
        setResult(field,result);
    }
        
    public synchronized Study getStudy() {
        return study;
    }
    public synchronized void setStudy(Study study) {
        this.study = study;
    }
    public synchronized IMolecule getMolecule() {
        return molecule;
    }
    public synchronized void setMolecule(IMolecule molecule) {
        this.molecule = molecule;
    }
    public synchronized LiteratureEntry getReference() {
        return reference;
    }
    public synchronized void setReference(LiteratureEntry reference) {
        this.reference = reference;
    }
    public void setStructureType(String type) {
        for (int i=0; i < structure_types.length; i++)
            if (type.equals(structure_types[i])) {
                structureType = i; return;
            }
    }
    public String getStructureType() {
        return structure_types[structureType];
    }
    public synchronized IMolecule getParent_Molecule() {
        return parent_compound;
    }
    public synchronized void setParent_compound(IMolecule parent_compound) {
        this.parent_compound = parent_compound;
    }
    public synchronized Object getResult(Object field) throws AmbitException {
        if (results == null) return null;
        else return results.get(study.template.getField(field));
    }
    /**
     * Adds an experiment result.
     * @param field Has to be one of the template fields; otherwise throws an exception
     * @param result
     * @throws AmbitException
     */
    public synchronized void setResult(Object field, Object result) throws AmbitException {
        if (result == null) throw new AmbitException("Undefined result!");
        if (study.template == null) throw new AmbitException("Undefined study template!\t"+field);
        TemplateField tf = study.template.getField(field);
        if (tf==null)  throw new PropertyNotInTemplateException("Unknown field "+field);
        if (results == null) results = new Hashtable();
        results.put(tf,result);

    }
    public Object clone() throws CloneNotSupportedException {
    	Experiment e = (Experiment) super.clone();
    	e.setStudy((Study)study.clone());
    	e.setReference((LiteratureEntry)reference.clone());
    	if (results != null) e.setResults((Hashtable)results.clone());
    	e.setStructureType(getStructureType());
    	if (molecule != null)
    		e.setMolecule((IMolecule)molecule.clone());
    	if (parent_compound != null)
    		e.setParent_compound((IMolecule)parent_compound.clone());
    	return e;
    }

	public Hashtable getResults() {
		return results;
	}

	public void setResults(Hashtable results) {
		this.results = results;
	}
	public String toString() {
		if (results != null)
			return results.toString();
		else return "NA";
	}
	@Override
	public IAmbitEditor editor(boolean editable) {
		return new AbstractAmbitEditor("Experiment",this) {
			protected ambit2.ui.data.AbstractPropertyTableModel createTableModel(AmbitObject object) {
				 return new ExperimentPropertyModel((Experiment)object);
			}
		};
	}
}
