/* TemplateDeleteProperty.java
 * Author: nina
 * Date: Apr 2, 2009
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

package ambit2.db.update.dictionary;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;

public class TemplateDeleteProperty  extends AbstractUpdate<Dictionary,Property> {
	public static final String[] delete_sql = {
		"DELETE template_def from template,template_def,properties,catalog_references\n"+
		"where properties.name=? and catalog_references.title=? and template.name=?\n"+
		"and template.idtemplate = template_def.idtemplate and template_def.idproperty=properties.idproperty and catalog_references.idreference=properties.idreference"
	};

	public TemplateDeleteProperty(Dictionary template,Property property) {
		super();
		setGroup(template);
		setObject(property);
	}
	
	public TemplateDeleteProperty() {
		this(null,null);
	}	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<String>(String.class, getObject().getName()));
		params1.add(new QueryParam<String>(String.class, getObject().getReference().getTitle()));
		params1.add(new QueryParam<String>(String.class, getGroup().getTemplate()));
		return params1;
	}

	public String[] getSQL() throws AmbitException {
		return delete_sql;
	}

	public void setID(int index, int id) {
	
	}

}
