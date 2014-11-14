package ambit2.db.update.fpae;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.descriptors.AtomEnvironment;
import ambit2.descriptors.processors.AtomEnvironmentList;

public class CreateAtomEnvironment extends AbstractUpdate<IStructureRecord,IStructureRecord> {
	protected String ae_sql = "{CALL setAtomEnvironment(?,?,?,?,?,?,?,?,?,?)}";
	//protected String ae_sql = "CALL setAtomEnvironment(1,'Csp3',2,'a','b','c','d','f','x','valid')";
	protected Property property = Property.getInstance(AmbitCONSTANTS.AtomEnvironment,AmbitCONSTANTS.AtomEnvironment);
	
	
	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
		
		AtomEnvironment ae = ((AtomEnvironmentList)getGroup().getProperty(property)).get(index);
		params.add(new QueryParam<String>(String.class, ae.getCentral_atom()));
		params.add(new QueryParam<Integer>(Integer.class, ae.getFrequency()));

		for (int i=1; i < 7; i++) {
			String p = ae.getLevel2String(i);
			params.add(new QueryParam<String>(String.class, p==null?"":p ));
		}
		
		params.add(new QueryParam<String>(String.class, "valid"));
		return params;
	}

	@Override
	public String[] getSQL() throws AmbitException {
		AtomEnvironmentList ae = (AtomEnvironmentList)getGroup().getProperty(property);
		String[] sql = new String[ae.size()];
		for (int i=0; i < ae.size();i++)
			sql[i] = ae_sql;
		return sql;
	}

	@Override
	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isStoredProcedure() {
		return true;
	}
}
