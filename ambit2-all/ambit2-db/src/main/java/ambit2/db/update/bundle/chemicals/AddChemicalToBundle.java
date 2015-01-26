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

package ambit2.db.update.bundle.chemicals;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;

public class AddChemicalToBundle extends AbstractUpdate<SubstanceEndpointsBundle, IStructureRecord> {
    private static final String[] update_sql = { "insert into bundle_chemicals values (?,?,now(),?,?) on duplicate key update tag=values(tag),remarks=values(remarks)" };

    // private ILiteratureEntry reference = null;
    public AddChemicalToBundle(SubstanceEndpointsBundle bundle, IStructureRecord dataset) {
	this(dataset);
	setGroup(bundle);
    }

    public AddChemicalToBundle(IStructureRecord dataset) {
	super(dataset);
    }

    public AddChemicalToBundle() {
	this(null);
    }

    @Override
    public List<QueryParam> getParameters(int index) throws AmbitException {
	List<QueryParam> params = new ArrayList<QueryParam>();

	if (getGroup() == null || getGroup().getID() <= 0)
	    throw new AmbitException("Bundle not defined");
	if (getObject() == null)
	    throw new AmbitException("Chemical not defined");

	params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));

	if (getObject().getIdchemical() > 0) {
	    params.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
	} else
	    throw new AmbitException("Chemical not defined");

	Object tag = null;
	Object remarks = null;
	ILiteratureEntry reference = LiteratureEntry.getBundleReference(getGroup());
	for (Property property : getObject().getProperties()) {
	    if ("tag".equals(property.getName()) && property.getReference().equals(reference)) {
		tag = getObject().getProperty(property);
	    } else if ("remarks".equals(property.getName()) && property.getReference().equals(reference)) {
		remarks = getObject().getProperty(property);
	    }
	}
	params.add(new QueryParam<String>(String.class, tag == null ? null : tag.toString()));
	params.add(new QueryParam<String>(String.class, remarks == null ? null : remarks.toString()));
	return params;

    }

    @Override
    public String[] getSQL() throws AmbitException {

	if (getGroup() == null || getGroup().getID() <= 0)
	    throw new AmbitException("Bundle not defined");
	if (getObject() == null)
	    throw new AmbitException("Chemical not defined");

	if (getObject().getIdchemical() > 0) {
	    return update_sql;
	}
	throw new AmbitException("Chemical not defined");
    }

    public void setID(int index, int id) {

    }

}
