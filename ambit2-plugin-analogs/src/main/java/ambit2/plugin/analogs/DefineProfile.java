/* DefineProfile.java
 * Author: nina
 * Date: Dec 28, 2008
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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

package ambit2.plugin.analogs;

import java.sql.ResultSet;
import java.util.List;

import ambit2.core.data.Dictionary;
import ambit2.core.data.Profile;
import ambit2.core.data.Property;
import ambit2.db.readers.IRetrieval;
import ambit2.db.readers.RetrieveFieldNames;
import ambit2.db.search.IQueryObject;
import ambit2.plugin.performers.QueryProperties;
import ambit2.plugin.performers.QueryTemplates;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

/**
 * Encapsulates profile definition (what properties are of interest).
 * Retrieves available fields from AMBIT database (by {@link RetrieveFieldNames}).
 * @author nina
 *
 */
public class DefineProfile extends Sequence {
	public DefineProfile() {

		Primitive<IQueryObject, List<Dictionary>> retrieveTemplates = new Primitive<IQueryObject, List<Dictionary>>(
				DBWorkflowContext.QUERY,
				DBWorkflowContext.DESCRIPTORS,
				new QueryTemplates("Descriptors")
				) {
			@Override
			public synchronized String getName() {
				return "Select available descriptors";
			};
		};		
		Primitive<IQueryObject, Profile> retrieveFields = new Primitive<IQueryObject, Profile>(
				DBWorkflowContext.QUERY,
				DBWorkflowContext.PROFILE,
				new QueryProperties(DBWorkflowContext.DESCRIPTORS)
				) {
			@Override
			public synchronized String getName() {
				return "Retrieve available endpoints";
			};
		};
		addStep(retrieveTemplates);
		addStep(retrieveFields);
	    addStep(new UserInteraction<Profile>(
	        		new Profile(),
	        		DBWorkflowContext.PROFILE,
	        		"Define profile"));
	}
}


class QueryProfile extends QueryPerformer<IQueryObject, Profile,Property> {
	@Override
	protected IQueryObject getTarget() {
		return new RetrieveFieldNames();
	};
	protected Profile process(IQueryObject query,ResultSet rs) throws Exception {							
		throw new Exception("Not implemented");
	}
	protected Profile retrieve(IRetrieval<Property> query,
			ResultSet rs) throws Exception {
		System.out.println(query.toString());
		Profile profile = new Profile();
        while (rs.next()) {
        	Property value = query.getObject(rs);
        	profile.add(value);
        };
        return profile;
	};	
}