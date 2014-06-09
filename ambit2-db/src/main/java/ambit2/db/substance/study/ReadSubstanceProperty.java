package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.AbstractPropertyRetrieval;

public class ReadSubstanceProperty extends AbstractPropertyRetrieval<String, Profile<Property>, StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6014257070546479407L;
	
	private static String sql = 
		//"select idproperty,name,units,title,url,idreference,comments,null,islocal,type from properties join catalog_references using(idreference) %s";
	"select topcategory,endpointcategory,guidance,hex(endpointhash) hash,e.endpoint effectendpoint,unit,conditions from\n"+ 
	"substance_protocolapplication p join substance_experiment e on p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid\n"+
	"where endpointhash=unhex(?) limit 1";
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getFieldname()));
		return params;
	}
	
	@Override
	public Property getObject(ResultSet rs) throws AmbitException {
		try {
			LiteratureEntry ref = new LiteratureEntry(rs.getString(3), rs.getString(4));
			ref.setType(_type.Substance);
			SubstanceProperty p = new SubstanceProperty(rs.getString(5), rs.getString(6), ref);
			p.setIdentifier(rs.getString(4));
			
			p.setLabel(String.format("http://www.opentox.org/echaEndpoints.owl#%s",rs.getString(2).replace("_SECTION", "")));
			//p.setLabel(Property.rs.getString(1)+"/"+rs.getString(2));
			return p;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}
