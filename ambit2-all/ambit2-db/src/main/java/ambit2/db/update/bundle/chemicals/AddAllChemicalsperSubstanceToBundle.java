package ambit2.db.update.bundle.chemicals;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.processor.DBSubstanceWriter;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;

/**
 * Adds all chemicals , defined as substance components to a bundle
 * Used by {@link DBSubstanceWriter} when importing a file as a bundle
 * @author nina
 *
 */
public class AddAllChemicalsperSubstanceToBundle extends AbstractUpdate<SubstanceEndpointsBundle,SubstanceRecord> {
	private static final String[] update_sql =  {"insert ignore into bundle_chemicals SELECT idbundle,idchemical,now(),null,null FROM bundle_substance join substance_relation using(idsubstance) where idbundle=? and idsubstance=?"	};
	
	public AddAllChemicalsperSubstanceToBundle(SubstanceEndpointsBundle bundle,SubstanceRecord dataset) {
		this(dataset);
		setGroup(bundle);
	}
	
	public AddAllChemicalsperSubstanceToBundle(SubstanceRecord dataset) {
		super(dataset);
	}
	public AddAllChemicalsperSubstanceToBundle() {
		this(null);
	}	
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		
		if (getGroup()==null || getGroup().getID()<=0) throw new AmbitException("Bundle not defined");
		if (getObject() == null )  throw new AmbitException("Substance not defined");

		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		
		if (getObject().getIdsubstance()>0) {
			params.add(new QueryParam<Integer>(Integer.class, getObject().getIdsubstance()));
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
		}
		throw new AmbitException("Substance not defined");
	}
	public void setID(int index, int id) {
		
	}
}