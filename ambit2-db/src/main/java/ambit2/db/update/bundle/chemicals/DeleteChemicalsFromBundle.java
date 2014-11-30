package ambit2.db.update.bundle.chemicals;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;

public class DeleteChemicalsFromBundle  extends AbstractUpdate<SubstanceEndpointsBundle,IStructureRecord> {
	private static final String[] delete_sql =  {"delete from bundle_chemicals where idbundle=? and idchemical=?"	};
	
	
	public DeleteChemicalsFromBundle(SubstanceEndpointsBundle bundle,IStructureRecord record) {
		this(record);
		setGroup(bundle);
	}
	
	public DeleteChemicalsFromBundle(IStructureRecord record) {
		super(record);
	}
	public DeleteChemicalsFromBundle() {
		this(null);
	}	
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		
		if (getGroup()==null || getGroup().getID()<=0) throw new AmbitException("Bundle not defined");
		if (getObject() == null )  throw new AmbitException("Substance not defined");
		
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
		
		if (getObject().getIdchemical()>0) {
			params.add(new QueryParam<Integer>(Integer.class, getObject().getIdchemical()));
			return params;
		}
		throw new AmbitException("Chemical not defined");
		
	}
	@Override
	public String[] getSQL() throws AmbitException {
		if (getGroup()==null || getGroup().getID()<=0) throw new AmbitException("Bundle not defined");
		if (getObject() == null )  throw new AmbitException("Substance not defined");
			
		if (getObject().getIdchemical()>0) {
			return delete_sql;
		} 		
		throw new AmbitException("Chemical not defined");
	}
	public void setID(int index, int id) {
		
	}
}