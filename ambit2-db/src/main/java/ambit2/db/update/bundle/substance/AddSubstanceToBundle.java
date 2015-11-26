/* AddSubstanceToBundle
 * 
 * Copyright (C) 2014  Ideaconsult Ltd.
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

package ambit2.db.update.bundle.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;

public class AddSubstanceToBundle extends AbstractUpdate<SubstanceEndpointsBundle,SubstanceRecord> {
	private static final String[] update_sql =  {"insert into bundle_substance select idsubstance,?,now(),prefix,uuid,?,? from substance where idsubstance=? on duplicate key update substance_prefix=values(substance_prefix),substance_uuid=values(substance_uuid),tag=values(tag),remarks=values(remarks)"	};
	
	private static final String[] update_sql_uuid =  {"insert into bundle_substance select idsubstance,?,now(),prefix,uuid,?,? from substance where prefix=? and uuid=unhex(?) on duplicate key update substance_prefix=values(substance_prefix),substance_uuid=values(substance_uuid),tag=values(tag),remarks=values(remarks)"  }	;
	
	public AddSubstanceToBundle(SubstanceEndpointsBundle bundle,SubstanceRecord dataset) {
		this(dataset);
		setGroup(bundle);
	}
	
	public AddSubstanceToBundle(SubstanceRecord dataset) {
		super(dataset);
	}
	public AddSubstanceToBundle() {
		this(null);
	}	
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		
		if (getGroup()==null || getGroup().getID()<=0) throw new AmbitException("Bundle not defined");
		if (getObject() == null )  throw new AmbitException("Substance not defined");

		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		
		Object tag = null;
		Object remarks = null;
		ILiteratureEntry reference = LiteratureEntry.getBundleReference(getGroup());
		for (Property property : getObject().getRecordProperties()) {
		    if ("tag".equals(property.getName()) && property.getReference().equals(reference)) {
			tag = getObject().getRecordProperty(property);
		    } else if ("remarks".equals(property.getName()) && property.getReference().equals(reference)) {
			remarks = getObject().getRecordProperty(property);
		    }
		}
		params.add(new QueryParam<String>(String.class, tag == null ? null : tag.toString()));
		params.add(new QueryParam<String>(String.class, remarks == null ? null : remarks.toString()));
		
		if (getObject().getIdsubstance()>0) {
			params.add(new QueryParam<Integer>(Integer.class, getObject().getIdsubstance()));
			return params;
		} else if (getObject().getSubstanceUUID()!= null) {
			String o_uuid = getObject().getSubstanceUUID();
			if (o_uuid==null) throw new AmbitException("Empty substance id");
			String[] uuid = new String[]{null,o_uuid==null?null:o_uuid.toString()};
			if (o_uuid!=null) 
				uuid = I5Utils.splitI5UUID(o_uuid.toString());
			params.add(new QueryParam<String>(String.class, uuid[0]));
			params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			return params;
		}
		throw new AmbitException("Substance not defined");
		
	}
	@Override
	public String[] getSQL() throws AmbitException {
		
		if (getGroup()==null || getGroup().getID()<=0) throw new AmbitException("Bundle not defined");
		if (getObject() == null )  throw new AmbitException("Substance not defined");
			
		if (getObject().getIdsubstance()>0) {
			return update_sql;
		} else if (getObject().getSubstanceUUID()!= null) 
			return update_sql_uuid;
		
		throw new AmbitException("Substance not defined");
	}
	public void setID(int index, int id) {
		
	}
}
