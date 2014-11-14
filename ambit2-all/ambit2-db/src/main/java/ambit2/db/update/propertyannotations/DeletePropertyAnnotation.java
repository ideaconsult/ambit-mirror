package ambit2.db.update.propertyannotations;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;

public class DeletePropertyAnnotation extends AbstractUpdate<Property,PropertyAnnotation> {
	protected final String[] sql = {
			"DELETE from property_annotation where idproperty=?"
	};
	@Override
	public String[] getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getGroup()==null) || (getGroup().getId()<=0)) throw new AmbitException("No property!");
		
		List<QueryParam> param = new ArrayList<QueryParam>();
		param.add(new QueryParam<Integer>(Integer.class,getGroup().getId()));
		return param;
	}

	@Override
	public void setID(int index, int id) {
		
		
	}

}
