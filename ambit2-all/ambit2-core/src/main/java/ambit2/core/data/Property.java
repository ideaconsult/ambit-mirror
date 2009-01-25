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

package ambit2.core.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import ambit2.core.exceptions.AmbitIOException;



public class Property<Generator>  {
	public enum IO_QUESTION  {
		IO_START,
		IO_TRANSLATE_NAME,
		IO_STOP,
	};
	protected String name = "NA";
	protected String label = "NA";
	protected String units = "";
	protected int id=-1;
	
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
	protected int order = 0;
	protected Class clazz = java.lang.String.class;
	protected boolean enabled = false;
	protected LiteratureEntry reference;

	public LiteratureEntry getReference() {
		return reference;
	}

	public void setReference(LiteratureEntry reference) {
		this.reference = reference;
	}

	public Property() {
		
	}

	public Property(String name) {
		this(name,name);
	}
	public Property(String name,String label) {
		this(name,label,0);
	}
	
	public Property(String name,String label,int order) {
		this(name,label,order,java.lang.String.class);
	}
	
	public Property(String name,String label,int order, Class clazz) {
		this(name,label,order,clazz,false);
	}	
	
	public Property(String name,String label,int order, Class clazz, boolean enabled) {
		setName(name);
		setLabel(label);
		setOrder(order);
		setClazz(clazz);
		setEnabled(enabled);
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
	public void setName(String name) {
		this.name = name;
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
}
