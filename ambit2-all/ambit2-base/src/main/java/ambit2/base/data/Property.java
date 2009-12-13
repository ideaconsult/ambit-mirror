/*
Copyright (C) 2005-2008  

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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;

import ambit2.base.exceptions.AmbitIOException;

import com.jgoodies.binding.beans.Model;



public class Property extends Model implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6813235410505542235L;
	/*
	private static java.util.concurrent.CopyOnWriteArrayList<Property> properties = 
		new CopyOnWriteArrayList<Property>();	
		*/
	private static String defaultReference = "Default property reference";
	protected static final String Names = "Names";
	protected static final String CAS = "CasRN";
	protected static final String EC = "EC";
	public static synchronized Property getNameInstance() {
		return getInstance(Names, LiteratureEntry.getIUPACReference());
	}	
	public static synchronized Property getCASInstance() {
		Property p = getInstance(CAS, LiteratureEntry.getCASReference());
		p.setLabel(CAS);
		return p;
	}		
	public static synchronized Property getEINECSInstance() {
		Property p =  getInstance(EC, LiteratureEntry.getEINECSReference());
		p.setLabel(EC);
		return p;
	}			
	public static synchronized Property getInstance(String name,String reference) {
		return getInstance(name, reference,"");
	}
	public static synchronized Property getInstance(String name,LiteratureEntry reference) {
		if (reference == null)
			return getInstance(name, defaultReference,"http://ambit.sourceforge.net");
		else
			return getInstance(name, reference.getTitle(),reference.getURL());
	}	
	public static synchronized Property getInstance(String name,String reference, String url) {
		Property p = new Property(name,LiteratureEntry.getInstance(reference,url));
		p.setLabel(name);

		return p;
	}		
	public enum IO_QUESTION  {
		IO_START,
		IO_TRANSLATE_NAME,
		IO_STOP,
	};
	protected String name = "NA";
	protected String label = "NA";
	protected String units = "";
	protected int id=-1;
	protected int order = 0;
	protected Class clazz = java.lang.String.class;
	protected boolean enabled = false;
	protected LiteratureEntry reference = LiteratureEntry.getInstance();
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public LiteratureEntry getReference() {
		return reference;
	}

	public  void setReference(LiteratureEntry reference) {
		this.reference = reference;
	}
	private Property() {
		
	}
	/**
	 * public constructors for web services
	 * @param name
	 */
	public static String guessLabel(String n) {
		if (n.startsWith("cas")) return "CasRN";
		else if (n.contains("species")) return "Species";
		else if (n.contains("iupac")) return "IUPAC Name";		
		else if (n.contains("name")) return "Names";
		else if (n.contains("title")) return "Names";
		else if (n.contains("inchi")) return "InChI";
		else if (n.contains("smiles")) return "SMILES";
		return null;
	}
	public Property(String name) {
		this(name,name);
	}
	
	public Property(String name, LiteratureEntry reference) {
		this(name,"",reference);
	}	
	public Property(String name, String units, LiteratureEntry reference) {
		this(name,name);
		setUnits(units);
		this.reference = reference;
	}		
	private Property(String name,String label) {
		this(name,label,0);
	}
	public Property(String name,String label,String units) {
		this(name,label);
		setUnits(units);
	}	
	private Property(String name,String label,int order) {
		this(name,label,order,java.lang.String.class);
	}
	
	private Property(String name,String label,int order, Class clazz) {
		this(name,label,order,clazz,false);
	}	
	
	private Property(String name,String label,int order, Class clazz, boolean enabled) {
		this.name = name;
		setLabel(label);
		setOrder(order);
		setClazz(clazz);
		setEnabled(enabled);
	}	
	
    public int hashCode() {
    	int hash = 7;
    	int var_code = (null == getName() ? 0 : getName().hashCode());
    	hash = 31 * hash + var_code; 
    	var_code = (null == getReference().getTitle() ? 0 : getReference().getTitle().hashCode());
    	hash = 31 * hash + var_code; 
	
    	return hash;
    }	
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getName() {
		return name;
	}

	public String getTitle() {
		return getReference().getTitle();
	}
	public String getUrl() {
		return getReference().getURL();
	}	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	@Override
	public String toString() {
		return getName();
	}
	public boolean save(OutputStream out) throws AmbitIOException {
		return save(new OutputStreamWriter(out));
	}
	public boolean save(Writer writer) throws AmbitIOException {		
		try {
			writer.write("\n");
			writer.write("<field ");
			writer.write("name=\"");
			writer.write(getName());
			writer.write("\" units=\"");
			writer.write(getUnits());
			writer.write("\" class=");
			writer.write(getClazz().getName());
			writer.write("/>");
			return true;
		} catch ( IOException  x) {
			throw new AmbitIOException(this.getClass().getName(),x);
		}
	}	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Property) {
			return
			((Property)obj).getName().equals(getName()) &&
			((Property)obj).getReference().equals(getReference()) ; 
		} else return false;
	}
}
