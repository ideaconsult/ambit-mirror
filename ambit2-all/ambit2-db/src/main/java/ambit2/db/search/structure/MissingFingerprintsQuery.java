/* MissingFingerprintsQuery.java
 * Author: nina
 * Date: Feb 8, 2009
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

package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.EQCondition;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;

public class MissingFingerprintsQuery extends AbstractStructureQuery<FPTable, String, EQCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8554160549010780854L;
	
	public final static String sqlField =
		/*
		"select ? as idquery, chemicals.idchemical,idstructure,1 as selected,1 as metric,null as text\n"+
		"from structure join chemicals using(idchemical)\n"+
		"left join %s using(idchemical)\n"+
		"where (structure.type_structure != 'NA') &&((%s.status is null)\n"+
		"or (structure.updated > %s.updated))\n"+
		"union\n"+
		"select ? as idquery, chemicals.idchemical,idstructure,1 as selected,0 as metric,null as text\n"+
		"from structure join chemicals using(idchemical)\n"+
		"join %s using(idchemical)\n"+
		"where (structure.type_structure != 'NA') && (%s.status = 'invalid')\n";
	*/
		"select ? as idquery, chemicals.idchemical,idstructure,1 as selected,1 as metric,null as text\n"+
		"from structure join chemicals using(idchemical)\n"+
		"left join %s as fingerprints using(idchemical)\n"+
		"where (structure.type_structure != 'NA') and\n"+
		"(\n"+
		"(fingerprints.status is null) or (structure.updated > fingerprints.updated) or (fingerprints.status = 'invalid')\n"+
		")\n"+
		"order by idchemical\n";
		//limit 100
	
	public final static String sqlFieldStruc =
		
		"select ? as idquery, idchemical,idstructure,1 as selected,1 as metric,null as text\n"+
		"from structure\n"+
		"left join %s as fingerprint using(idchemical,idstructure)\n"+
		"where (structure.type_structure != 'NA') &&\n"+
		"(\n"+
		"(fingerprint.status is null) or (structure.updated > fingerprint.updated) or (fingerprint.status = 'invalid')\n"+
		") order by idchemical,idstructure\n";
		
		/*
		"select ? as idquery, structure.idchemical,idstructure,1 as selected,1 as metric,null as text\n"+
		"from structure\n"+
		"left join %s using(idchemical,idstructure)\n"+
		"where (structure.type_structure != 'NA') & ((%s.status is null)\n"+
		"or (structure.updated > %s.updated))\n"+
		"union\n"+
		"select ? as idquery, idchemical,idstructure,1 as selected,0 as metric,null as text\n"+
		"from %s where (%s.status = 'invalid')";
		*/
	public final static String sqlSMARTS =
		
		"select ? as idquery, structure.idchemical,idstructure,1 as selected,1 as metric,null as text\n"+
		"from structure\n"+
		"where (structure.type_structure != 'NA')  and atomproperties is null";
		
	public final static String sqlINCHI =
			"select ? as idquery, idchemical,-1,1 as selected,1 as metric,null as text from chemicals " +
			"where (inchi is null) and ((inchikey is null) or (inchikey is not null) and (inchikey != 'ERROR'))\n";

	
	public MissingFingerprintsQuery(FPTable table) {
		super();
		setCondition(EQCondition.getInstance());
		setFieldname(table);
	}	
	public MissingFingerprintsQuery() {
		this(FPTable.fp1024);
	}
	public String getSQL() throws AmbitException {
		String table = getFieldname().getTable();
		if (FPTable.inchi.equals(getFieldname())) return sqlINCHI;
		if (FPTable.smarts_accelerator.equals(getFieldname())) return sqlSMARTS;
		else
		return String.format(
				getFieldname().equals(FPTable.fp1024_struc)?sqlFieldStruc:sqlField,table);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		return params;
	}
	@Override
	public String toString() {
		return (getFieldname()==null)?"Fingerprints to be calculated":getFieldname().toString();
	}

}
