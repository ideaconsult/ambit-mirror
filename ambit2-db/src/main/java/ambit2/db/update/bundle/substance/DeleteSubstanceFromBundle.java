package ambit2.db.update.bundle.substance;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.I5Utils;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;

public class DeleteSubstanceFromBundle  extends AbstractUpdate<SubstanceEndpointsBundle,SubstanceRecord> {
	private static final String[] delete_sql =  {"delete from bundle_substance where idbundle=? and idsubstance=?"	};
	
	private static final String[] delete_sql_uuid =  {"delete bundle_substance from bundle_substance,substance where idbundle=? and bundle_substance.idsubstance=substance.idsubstance and prefix=? and uuid=unhex(?)" }	;
	
	
	public DeleteSubstanceFromBundle(SubstanceEndpointsBundle bundle,SubstanceRecord record) {
		this(record);
		setGroup(bundle);
	}
	
	public DeleteSubstanceFromBundle(SubstanceRecord record) {
		super(record);
	}
	public DeleteSubstanceFromBundle() {
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
		} else if (getObject().getSubstanceUUID()!= null) {
			String o_uuid = getObject().getSubstanceUUID();
			if (o_uuid==null) throw new AmbitException("Empty substance id");
			String[] uuid = new String[]{null,o_uuid==null?null:o_uuid.toString()};
			if (o_uuid!=null) 
				uuid = I5Utils.splitI5UUID(o_uuid.toString());
			params.add(new QueryParam<String>(String.class, uuid[0]));
			params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			return params;
		}
		throw new AmbitException("Substance not defined");
		
	}
	@Override
	public String[] getSQL() throws AmbitException {
		if (getGroup()==null || getGroup().getID()<=0) throw new AmbitException("Bundle not defined");
		if (getObject() == null )  throw new AmbitException("Substance not defined");
			
		if (getObject().getIdsubstance()>0) {
			return delete_sql;
		} else if (getObject().getSubstanceUUID()!= null) 
			return delete_sql_uuid;
		
		throw new AmbitException("Substance not defined");
	}
	public void setID(int index, int id) {
		
	}
}