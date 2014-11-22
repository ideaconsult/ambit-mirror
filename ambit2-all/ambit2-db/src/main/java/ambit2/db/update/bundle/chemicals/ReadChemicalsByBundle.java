package ambit2.db.update.bundle.chemicals;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.EQCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

public class ReadChemicalsByBundle  extends AbstractStructureQuery<SubstanceEndpointsBundle,StructureRecord,EQCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3139508348246513746L;
	/**
	 * 
	 */
	private static String sql = 
		"select idbundle,idchemical,-1,1,1 as metric,inchikey as text from chemicals join bundle_chemicals using(idchemical) where idbundle=?";
	
	public ReadChemicalsByBundle() {
		super();
	}
	public ReadChemicalsByBundle(SubstanceEndpointsBundle bundle) {
		super();
		setFieldname(bundle);
	}
	
	@Override
	public String getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		if (getFieldname()!=null) {
			params1.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
			return params1;
		}
		throw new AmbitException("Unspecified bundle");
	}



}
