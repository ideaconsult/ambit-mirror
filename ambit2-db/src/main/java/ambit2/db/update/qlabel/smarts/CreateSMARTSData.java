package ambit2.db.update.qlabel.smarts;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.smarts.CMLUtilities;

public class CreateSMARTSData extends AbstractUpdate<IStructureRecord,IStructureRecord> implements IQueryUpdate< IStructureRecord,IStructureRecord>{
	protected Property property = Property.getInstance(CMLUtilities.SMARTSProp,CMLUtilities.SMARTSProp);
			
	protected static final String[] sql = {
		"update structure set atomproperties=? where idchemical=? and idstructure=?" 
	};
	public CreateSMARTSData() {
		
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> p = new ArrayList<QueryParam>();
		if (getObject().getProperty(property)!=null) 
			p.add(new QueryParam<String>(String.class,getObject().getProperty(property).toString()));
		else 
			p.add(new QueryParam<String>(String.class,null));
	
		p.add(new QueryParam<Integer>(Integer.class,getObject().getIdchemical()));
		p.add(new QueryParam<Integer>(Integer.class,getObject().getIdstructure()));		
		
		return p;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
	
	}

}
