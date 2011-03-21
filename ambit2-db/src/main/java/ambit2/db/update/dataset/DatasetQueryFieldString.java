package ambit2.db.update.dataset;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.PropertyValue;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

public class DatasetQueryFieldString extends AbstractStructureQuery<ISourceDataset, PropertyValue<String>, StringCondition> {

	public final static String sqlField = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		
		"join struc_dataset using(idstructure) join property_values using(idstructure)\n"+
		"join property_string using(idvalue_string) where\n"+
		" id_srcdataset = ? and idproperty = ? and value = ?";

	public final static String sqlField_Rdatasets = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text\n" +	
		"FROM structure s1\n"+
		"join query_results using(idstructure) join property_values using(idstructure)\n"+
		"join property_string using(idvalue_string) where\n"+
		"idquery = ? and idproperty = ? and value = ?";	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4878852850705024770L;

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		
		if ((getFieldname()==null) || getFieldname().getID()<=0) throw new AmbitException("No dataset!");
		params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
		
		if (getValue()==null) throw new AmbitException("No feature!");	
		
		if (getValue().getProperty()==null || getValue().getProperty().getId()<=0) throw new AmbitException("No feature!");		
		params.add(new QueryParam<Integer>(Integer.class, getValue().getProperty().getId()));
		
		params.add(new QueryParam<String>(String.class, getValue().getValue()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		if ((getFieldname()==null) || getFieldname().getID()<=0) throw new AmbitException("No dataset!");
		if (getFieldname() instanceof SourceDataset)
			return sqlField;
		else
			return sqlField_Rdatasets;
	}

}
