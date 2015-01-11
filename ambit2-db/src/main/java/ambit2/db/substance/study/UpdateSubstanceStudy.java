/* UpdateSubstanceRelation
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

package ambit2.db.substance.study;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;

public class UpdateSubstanceStudy extends AbstractUpdate<String,ProtocolApplication<Protocol, IParams, String, IParams, String>> {
	
	private static final String[] create_sql = {
		"INSERT INTO substance_protocolapplication (document_prefix,document_uuid,topcategory,endpointcategory,endpoint,guidance," +
		"substance_prefix,substance_uuid,params,interpretation_result,interpretation_criteria,reference,reference_year,reference_owner," +
		"reliability,isRobustStudy,isUsedforClassification,isUsedforMSDS,purposeFlag,studyResultType)\n" +
		"values(?,unhex(replace(?,'-','')),?,?,?,?,?,unhex(replace(?,'-','')),?,?,?,?,?,?,?,?,?,?,?,?) on duplicate key update\n"+
		"substance_prefix=values(substance_prefix),substance_uuid=values(substance_uuid),topcategory=values(topcategory),\n"+
		"endpointcategory=values(endpointcategory),endpoint=values(endpoint),guidance=values(guidance),params=values(params)," +
		"interpretation_result=values(interpretation_result),interpretation_criteria=values(interpretation_criteria)," +
		"reference=values(reference),reference_year=values(reference_year),reference_owner=values(reference_owner),reliability=values(reliability)," +
		"isRobustStudy=values(isRobustStudy),isUsedforClassification=values(isUsedforClassification)," +
		"isUsedforClassification=values(isUsedforClassification),purposeFlag=values(purposeFlag),studyResultType=values(studyResultType)" 
	};
	

	public UpdateSubstanceStudy(String substanceuuid, ProtocolApplication<Protocol, IParams, String, IParams, String> papp) {
		super();
		setGroup(substanceuuid);
		setObject(papp);
	}


	public void setID(int index, int id) {
	}

	protected void check() throws AmbitException  {
		if (getGroup()==null) throw new AmbitException("No substance UUID");
		if (getObject() == null) throw new AmbitException("No measurement");
		if (getObject().getDocumentUUID() == null) throw new AmbitException("No measurement UUID");
		if (getObject().getProtocol() == null) throw new AmbitException("No protocol");
		//if (getObject().getProtocol().getEndpoint() == null) throw new AmbitException("No endpoint");
		
	}
	@Override
	public String[] getSQL() throws AmbitException {
		check();
		return create_sql;
	}
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		check();
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		addParameters(index, params1);
		return params1;
	}	
	
	public void addParameters(int index, List<QueryParam> params1) throws AmbitException {	
		Object o_uuid = getObject().getDocumentUUID();
		String[] cmp_uuid = {null,o_uuid==null?null:o_uuid.toString()};
		if (cmp_uuid==null || cmp_uuid.length<2) throw new AmbitException("Invalid UUID "+o_uuid.toString());
		if (o_uuid!=null) cmp_uuid = I5Utils.splitI5UUID(o_uuid.toString());
		params1.add(new QueryParam<String>(String.class, cmp_uuid[0]));
		params1.add(new QueryParam<String>(String.class, cmp_uuid[1]));		
		params1.add(new QueryParam<String>(String.class, getObject().getProtocol().getTopCategory()));
		params1.add(new QueryParam<String>(String.class, getObject().getProtocol().getCategory()));
		
		params1.add(new QueryParam<String>(String.class, getObject().getProtocol().getEndpoint()==null?"":truncate(getObject().getProtocol().getEndpoint(),255)));
		if ((getObject().getProtocol().getGuideline() == null) || (getObject().getProtocol().getGuideline().size()==0))
			params1.add(new QueryParam<String>(String.class, ""));
		else {
			Object g = getObject().getProtocol().getGuideline().get(0);
			if (g!=null && g.toString().length()>255) g = g.toString().substring(0,254);
			params1.add(new QueryParam<String>(String.class,g==null?null:g.toString()));
		}	
		
		Object s_uuid = getGroup();
		String[] subst_uuid = {null,s_uuid==null?null:s_uuid.toString()};
		if (subst_uuid==null || subst_uuid.length<2) throw new AmbitException("Invalid UUID "+s_uuid.toString());
		if (s_uuid!=null) subst_uuid = I5Utils.splitI5UUID(s_uuid.toString());
		params1.add(new QueryParam<String>(String.class, subst_uuid[0]));
		params1.add(new QueryParam<String>(String.class, subst_uuid[1]));
		
		params1.add(new QueryParam<String>(String.class, getObject().getParameters().toString()));
		
		String result = getObject().getInterpretationResult();
		if (result!=null && result.length()>128) result = result.substring(0,127);
		params1.add(new QueryParam<String>(String.class, result));
		params1.add(new QueryParam<String>(String.class, getObject().getInterpretationCriteria()));
		params1.add(new QueryParam<String>(String.class, getObject().getReference()));
		try {
			params1.add(new QueryParam<Integer>(Integer.class, Integer.parseInt(getObject().getReferenceYear())));
		} catch (Exception x) {
			params1.add(new QueryParam<Integer>(Integer.class, null));
		}
		try {
			params1.add(new QueryParam<String>(String.class, truncate(getObject().getReferenceOwner(),128)));
		} catch (Exception x) {
			params1.add(new QueryParam<String>(String.class, null));
		}
		//reliability
		try { params1.add(new QueryParam<String>(String.class, truncate(getObject().getReliability().getValue().toString(),45)));
		} catch (Exception x) {	params1.add(new QueryParam<String>(String.class, null));}		
		
		try { params1.add(new QueryParam<Boolean>(Boolean.class, Boolean.parseBoolean(getObject().getReliability().getIsRobustStudy().toString())));
		} catch (Exception x) {	params1.add(new QueryParam<Boolean>(Boolean.class, null));}
		try { params1.add(new QueryParam<Boolean>(Boolean.class, Boolean.parseBoolean(getObject().getReliability().getIsUsedforClassification().toString())));
		} catch (Exception x) {	params1.add(new QueryParam<Boolean>(Boolean.class, null));}
		try { params1.add(new QueryParam<Boolean>(Boolean.class, Boolean.parseBoolean(getObject().getReliability().getIsUsedforMSDS().toString())));
		} catch (Exception x) {	params1.add(new QueryParam<Boolean>(Boolean.class, null));}
		try { params1.add(new QueryParam<String>(String.class, truncate(getObject().getReliability().getPurposeFlag().toString(),32)));
		} catch (Exception x) {	params1.add(new QueryParam<String>(String.class, null));}
		try { params1.add(new QueryParam<String>(String.class, truncate(getObject().getReliability().getStudyResultType().toString(),128)));
		} catch (Exception x) {	params1.add(new QueryParam<String>(String.class, null));}
	}

}
