/* CreateSubstance
 * Author: nina
 * Date: Aug 06, 2013
 * 
 * Copyright (C) 2005-2013  Ideaconsult Ltd.
 * 
 * Contact: www.ideaconsult.net
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

package ambit2.db.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;

public class UpdateSubstance<C extends SubstanceRecord> extends AbstractUpdate<C, C> {
    public static final String[] update_sql = { "update substance set prefix=?,uuid=unhex(replace(?,'-','')),documentType=?,format=?,name=?,publicname=?,content=?,substanceType=?,rs_prefix=?,rs_uuid=unhex(replace(?,'-','')),owner_prefix=?,owner_uuid=unhex(replace(?,'-','')),owner_name=? where idsubstance=?" };

    public UpdateSubstance(C chemical) {
	super(chemical);
    }

    public UpdateSubstance() {
	this(null);
    }

    public List<QueryParam> getParameters(int index) throws AmbitException {
	if (getObject() == null || getObject().getIdsubstance() <= 0)
	    throw new AmbitException("Substance not defined");
	List<QueryParam> params1 = new ArrayList<QueryParam>();
	String o_uuid = getObject().getSubstanceUUID();
	String[] uuid = { null, o_uuid };
	if (o_uuid != null) {
	    uuid = I5Utils.splitI5UUID(o_uuid.toString());
	    params1.add(new QueryParam<String>(String.class, uuid[0]));
	    params1.add(new QueryParam<String>(String.class, uuid[1]));
	} else {
	    params1.add(new QueryParam<String>(String.class, null));
	    params1.add(new QueryParam<String>(String.class, null));
	}
	params1.add(new QueryParam<String>(String.class, "Substance"));
	params1.add(new QueryParam<String>(String.class, getObject().getFormat()));
	params1.add(new QueryParam<String>(String.class, getObject().getSubstanceName()));
	params1.add(new QueryParam<String>(String.class, getObject().getPublicName()));
	params1.add(new QueryParam<String>(String.class, getObject().getContent()));
	params1.add(new QueryParam<String>(String.class, getObject().getSubstancetype()));
	String rs_uuid = getObject().getReferenceSubstanceUUID();
	uuid = new String[] { null, rs_uuid };
	if (rs_uuid != null) {
	    uuid = I5Utils.splitI5UUID(rs_uuid.toString());
	    params1.add(new QueryParam<String>(String.class, uuid[0]));
	    params1.add(new QueryParam<String>(String.class, uuid[1]));
	} else {
	    params1.add(new QueryParam<String>(String.class, null));
	    params1.add(new QueryParam<String>(String.class, null));
	}

	String ownerUUID = getObject().getOwnerUUID();
	uuid = new String[] { null, ownerUUID };
	if (ownerUUID != null) {
	    uuid = I5Utils.splitI5UUID(ownerUUID.toString());
	    params1.add(new QueryParam<String>(String.class, uuid[0]));
	    params1.add(new QueryParam<String>(String.class, uuid[1]));
	} else {
	    params1.add(new QueryParam<String>(String.class, null));
	    params1.add(new QueryParam<String>(String.class, null));
	}
	params1.add(new QueryParam<String>(String.class, getObject().getOwnerName()));
	params1.add(new QueryParam<Integer>(Integer.class, getObject().getIdsubstance()));
	return params1;

    }

    public String[] getSQL() throws AmbitException {
	return update_sql;
    }

    public void setID(int index, int id) {

    }
}
