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

package ambit2.core.data;

import java.util.Map;

import ambit2.core.processors.structure.MoleculeReader.MOL_TYPE;


public class StructureRecord implements IStructureRecord {
	protected int idchemical;
	protected int idstructure;
	protected String content;
	protected String format;
	protected Map properties;
	
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
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#getProperties()
     */
	public Map getProperties() {
		return properties;
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#setProperties(java.util.Map)
     */
	public void setProperties(Map properties) {
		this.properties = properties;
	}
	public Object getProperty(Object key) {
		if (properties != null)
			return properties.get(key);
		else return null;
	}
	/* (non-Javadoc)
     * @see ambit2.repository.IStructureRecord#clear()
     */
	public void clear() {
		setContent(null);
		setIdchemical(-1);
		setIdstructure(-1);
		setProperties(null);
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
}	
