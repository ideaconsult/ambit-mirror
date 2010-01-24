/* Dictionary.java
 * Author: nina
 * Date: Feb 6, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.base.data;



/**
 * encapsulates pseudo ontology , defined in template and dictionary tables
 * @author nina
 *
 */
public class Dictionary extends Property {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1091010081539426004L;
	protected String relationship;
	
	public Dictionary() {
		this(null,null);
	}
	public Dictionary(String template,String parentTemplate) {
		this(template,parentTemplate,"is_a");
	}	
	public Dictionary(String template,String parentTemplate,String relationship) {
		super(template,parentTemplate==null?null:new LiteratureEntry(parentTemplate,opentox_TupleFeature));
		setTemplate(template);
		setParentTemplate(parentTemplate);
		setRelationship(relationship);
		setClazz(Dictionary.class);
		setNominal(true);
	}	

	public String getTemplate() {
		return getName();
	}
	public void setTemplate(String template) {
		setName(template);
	}
	public String getParentTemplate() {
		return getReference()==null?null:getTitle();
	}
	public void setParentTemplate(String parentTemplate) {
		if (parentTemplate==null) setReference(null);
		else {
			setReference(new LiteratureEntry(parentTemplate,opentox_TupleFeature));
		}
	}
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (getParentTemplate()!=null) {
			b.append(getParentTemplate());
			b.append('.');
		}
		b.append(getTemplate());
		
		return b.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Dictionary) {
			Dictionary d = (Dictionary)obj;
			boolean parentOK = (getParentTemplate() == null) && (d.getParentTemplate()==null);
			parentOK = parentOK || (
					(getParentTemplate() != null) && (d.getParentTemplate()!=null) &&
					getParentTemplate().equals(d.getParentTemplate())
					);
			if (parentOK) {
				if ((getTemplate() == null) && (d.getTemplate()==null)) return true;
				else return (getTemplate() != null) && (d.getTemplate()!=null) &&  getTemplate().equals(d.getTemplate());
			}
		} 
		return false;
	}
	@Override
	public int getId() {
		return -1;
	}
}
