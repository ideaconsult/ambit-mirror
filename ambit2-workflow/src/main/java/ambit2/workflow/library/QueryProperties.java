/* QueryProperties.java
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

package ambit2.workflow.library;

import java.sql.ResultSet;
import java.util.List;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.db.readers.IRetrieval;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.property.TemplateQuery;
import ambit2.workflow.DBWorkflowContext;

public class QueryProperties extends QueryPerformer<IQueryObject, Profile,Property> {
	protected String templates_varname=DBWorkflowContext.TEMPLATES;
	
	public QueryProperties() {
		this(DBWorkflowContext.TEMPLATES);
	}
	public QueryProperties(String templates_varname) {
		this.templates_varname = templates_varname;
	}		
	@Override
	protected IQueryObject getTarget() {
		TemplateQuery query = new TemplateQuery();
		List<Dictionary> templates = getTemplates();
		for (Dictionary d : templates)
			query.setValue(d.getTemplate());
		
		return query;
	};
	protected Profile process(IQueryObject query,ResultSet rs) throws Exception {							
		throw new Exception("Not implemented");
	}
	protected Profile retrieve(IRetrieval<Property> query,
			ResultSet rs) throws Exception {
		Profile profile = new Profile();
        while (rs.next()) 
        	try {
	        	Property value = query.getObject(rs);
	        	profile.add(value);
        	} catch (Exception x) {
        		x.printStackTrace();
        };
        return profile;
	};
	
	public List<Dictionary>  getTemplates() {
		Object templates = getContext().get(templates_varname);
		if ((templates != null) && (templates instanceof List)) 
			return (List<Dictionary>) templates;
			else return null;
			
	}
}