package ambit2.db.substance.study;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.AbstractPropertyRetrieval;

public class ReadSubstanceProperty extends AbstractPropertyRetrieval<SubstanceProperty, Profile<Property>, StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6014257070546479407L;
	
	private static String sql = 
	"select p.topcategory,p.endpointcategory,guidance,hex(endpointhash) hash,e.endpoint effectendpoint,unit,conditions from\n"+ 
	"substance_protocolapplication p join substance_experiment e on p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid\n"+
	"where p.topcategory=? and p.endpointcategory=? and e.endpoint=? and endpointhash=unhex(?) limit 1";
	
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) ||
			(getFieldname().getTopcategory()==null) ||
			(getFieldname().getEndpointcategory()==null) ||
			(getFieldname().getName()==null) ||
			(getFieldname().getIdentifier()==null)
			) throw new AmbitException("Empty property id");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getFieldname().getTopcategory()));
		params.add(new QueryParam<String>(String.class, getFieldname().getEndpointcategory()));
		params.add(new QueryParam<String>(String.class, getFieldname().getName()));
		params.add(new QueryParam<String>(String.class, getFieldname().getIdentifier()));
		return params;
	}
	
	@Override
	public Property getObject(ResultSet rs) throws AmbitException {
		try {
			LiteratureEntry ref = new LiteratureEntry(rs.getString(3), rs.getString(3));
			ref.setType(_type.Substance);
			SubstanceProperty p = new SubstanceProperty(
					rs.getString(1),rs.getString(2),
					rs.getString(5), rs.getString(6), ref);
			p.setIdentifier(rs.getString(4));
			return p;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}
