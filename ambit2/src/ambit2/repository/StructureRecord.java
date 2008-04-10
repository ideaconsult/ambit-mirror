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

package ambit2.repository;

import java.util.Map;


public class StructureRecord {
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
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public int getIdchemical() {
		return idchemical;
	}
	public void setIdchemical(int idchemical) {
		this.idchemical = idchemical;
	}
	public int getIdstructure() {
		return idstructure;
	}
	public void setIdstructure(int idstructure) {
		this.idstructure = idstructure;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Map getProperties() {
		return properties;
	}
	public void setProperties(Map properties) {
		this.properties = properties;
	}
	public void clear() {
		setContent(null);
		setIdchemical(-1);
		setIdstructure(-1);
		setProperties(null);
		setFormat(null);
	}

}	
