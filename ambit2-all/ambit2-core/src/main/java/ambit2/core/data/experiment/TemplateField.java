/* TemplateField.java
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import ambit2.base.data.AmbitBean;
import ambit2.base.exceptions.AmbitIOException;

/**
 * A {@link ambit2.core.data.experiment.StudyTemplate} field.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-2
 */
public class TemplateField extends AmbitBean {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7598061386536935899L;
	protected String units = "";
    protected boolean numeric = true;
    protected boolean isResult = false;
    protected String name="DEFAULT";
    protected int id;
    /**
     * 
     */
    public TemplateField() {
        super();
    }

    /**
     * @param name
     */
    public TemplateField(String name) {
        super();
        setName(name);
    }
    /**
     * Creates a templte field
     * @param name 
     * @param units
     * @param numeric
     * @param isResult
     */
    public TemplateField(String name,String units,boolean numeric,boolean isResult) {
        super();
        setUnits(units);
        setNumeric(numeric);
        setResult(isResult);
        setName(name);
    }

    /**
     * @param name
     * @param id
     */
    public TemplateField(String name, int id) {
        super();
        setName(name);
        setId(id);
    }

    public synchronized String getUnits() {
        return units;
    }
    public synchronized void setUnits(String units) {
        this.units = units;
    }
    public synchronized boolean isNumeric() {
        return numeric;
    }
    public synchronized void setNumeric(boolean numeric) {
        this.numeric = numeric;
    }
    public String getType() {
    	if (numeric) return "numeric"; else return "string";
    }
    public String getMode() {
    	if (isResult) return "result"; else return "condition";
    }

	public boolean isResult() {
		return isResult;
	}

	public void setResult(boolean isResult) {
		this.isResult = isResult;
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
			writer.write("\" numeric=");
			if (numeric) writer.write("\"True\""); else writer.write("\"False\"");
			writer.write(" result=");
			if (isResult) writer.write("\"True\""); else writer.write("\"False\"");
			writer.write("/>");
			return true;
		} catch ( IOException  x) {
			throw new AmbitIOException(this.getClass().getName(),x);
		}
	}
    public boolean equals(Object obj) {
    	return super.equals(obj);
    }	
    /*
    
    public boolean equals(Object obj) {
    	if (obj instanceof TemplateField) {
	        TemplateField f = (TemplateField) obj;
	        return super.equals(obj) && units.equals(f.getUnits())
	        	&& (numeric == f.isNumeric()) && (isResult == f.isResult());
    	} else return name.equals(obj.toString());
    }
    
    public int compareTo(Object obj) {
    	if (obj instanceof TemplateField) {
	        TemplateField f = (TemplateField) obj;
	        return super.compareTo(obj);
    	} else return name.compareTo(obj.toString());

    }
    */
	/* (non-Javadoc)
     * @see ambit2.data.AmbitObject#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name.replace('"','\'');
        
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
}
