/*
Copyright (C) 2007-2008  

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

package ambit2.base.data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.idea.modbcum.i.facet.IFacet;
import ambit2.base.interfaces.IStructureRecord;


public class StructureRecord implements IStructureRecord {
	/**
	 * 
	 */
	private static final long serialVersionUID = -650816773926953033L;
	protected boolean preferedStructure = true;
	protected int dataEntryID=-1;
	protected int id_srcdataset = -1;
	
	public int getDataEntryID() {
		return dataEntryID;
	}
	public void setDataEntryID(int dataEntryID) {
		this.dataEntryID = dataEntryID;
	}
	protected String inchiKey;
	protected String formula;
	protected String smiles;
	protected String inchi;
	protected int idchemical;
	protected int idstructure;
	protected String content;
	protected String format;
	protected ILiteratureEntry reference = null;
	protected Map<Property,Object> properties;
	protected STRUC_TYPE type = STRUC_TYPE.NA;
	protected boolean selected = true;
	protected List<IFacet> facets;
	
	public STRUC_TYPE getType() {
		return type;
	}
	public void setType(STRUC_TYPE type) {
		this.type = type;
	}
	public Map<Property,Object> getMap() {
		return properties;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	@Override
	public void setInchiKey(String key) {
		this.inchiKey = key;
		
	}
	public String getInchiKey() {
		return inchiKey;
	}

	public String getSmiles() {
		return smiles;
	}
	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}
	public String getInchi() {
		return inchi;
	}
	public void setInchi(String inchi) {
		this.inchi = inchi;
	}
	
	
	public StructureRecord() {
		this(-1,-1,"","");
	}
	public StructureRecord(int idchemical,int idstructure,String content, String format) {
		setIdchemical(idchemical);
		setIdstructure(idstructure);
		setContent(content);
		setFormat(format);
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#getFormat()
     */
	public String getFormat() {
		return format;
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#setFormat(java.lang.String)
     */
	public void setFormat(String format) {
		this.format = format;
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#getIdchemical()
     */
	public int getIdchemical() {
		return idchemical;
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#setIdchemical(int)
     */
	public void setIdchemical(int idchemical) {
		this.idchemical = idchemical;
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#getIdstructure()
     */
	public int getIdstructure() {
		return idstructure;
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#setIdstructure(int)
     */
	public void setIdstructure(int idstructure) {
		this.idstructure = idstructure;
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#getContent()
     */
	public String getContent() {
		return content;
	}
	public String getWritableContent() {
		if (MOL_TYPE.SDF.toString().equals(getFormat())) {
			int p = getContent().indexOf("M  END");
			if (p > 0)
				return getContent().substring(0,p+6);
		} 
		return getContent();
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#setContent(java.lang.String)
     */
	public void setContent(String content) {
		this.content = content;
		setSmiles(null);
		setInchi(null);
		setInchiKey(null);
	}
	/* 
	public Map getProperties() {
		return properties;
	}

	public void setProperties(Map properties) {
		this.properties = properties;
	}
	*/
	public Object getProperty(Property key) {
		if (properties != null)
			return properties.get(key);
		else return null;
	}
	public void setProperty(Property key,Object value) {
		if (key == null) return;
		if (properties == null) properties = createProperties();
		if (value == null) properties.remove(key);
		else properties.put(key, value);
			
	}
	protected Map<Property,Object> createProperties() {
		return new Hashtable<Property, Object>();
		//return new TreeMap<Property, Object>();
	}
	public Iterable<Property> getProperties() {
		if (properties==null) properties = createProperties();
		return properties.keySet();
	}
	
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#clear()
     */
	public void clear() {
		setSmiles(null);
		setInchi(null);
		setInchiKey(null);
		setFormula(null);
		setContent(null);
		setIdchemical(-1);
		setIdstructure(-1);
		setReference(null);
		if (properties!=null) properties.clear();
		setFormat(null);
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("idchemical=");
		b.append(getIdchemical());
		b.append("\tidstructure=");
		b.append(getIdstructure());		
		return b.toString();
	}
	public int getNumberOfProperties() {
		if (properties==null) return 0;
		return properties.size();
	}
	public Object removeProperty(Property key) {
		if (properties!= null)
			return properties.remove(key);
		else return null;
		
	}
	public void clearProperties() {
		if (properties!=null)
			properties.clear();
		
	}
	public void addProperties(Map newProperties) {
		Iterator keys = newProperties.keySet().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			if (key instanceof Property)
				setProperty((Property)key,newProperties.get(key));
			else
				setProperty(Property.getInstance(key.toString(),getReference()),newProperties.get(key));
		}
	}
	public ILiteratureEntry getReference() {
		return reference;
	}
	public void setReference(ILiteratureEntry reference) {
		this.reference = reference;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		StructureRecord record = new StructureRecord();
		record.setContent(getContent());
		record.setFormat(getFormat());
		record.setFormula(getFormula());
		record.setIdchemical(getIdchemical());
		record.setIdstructure(getIdstructure());
		record.setSmiles(getSmiles());
		record.setInchi(getInchi());
		for (Property p : getProperties()) 
			record.setProperty(p,getProperty(p));
		return record;
	}	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean value) {
		this.selected = true;
		
	}
	@Override
	public boolean usePreferedStructure() {
		return preferedStructure;
	}
	@Override
	public void setUsePreferedStructure(boolean value) {
		preferedStructure = value;
		
	}
	@Override
	public int getDatasetID() {
		return id_srcdataset;
	}
	@Override
	public void setDatasetID(int id) {
		id_srcdataset = id;
	}
	@Override
	public void addFacet(IFacet facet) {
		if (facets == null) facets = new ArrayList<IFacet>();
		facets.add(facet);
	}
	@Override
	public void clearFacets() {
		if (facets!=null) facets.clear();
		
	}
	@Override
	public Iterable<IFacet> getFacets() {
		return facets;
	}
	@Override
	public void removeFacet(IFacet facet) {
		if (facets!=null) facets.remove(facet);
	}
}	
