package ambit2.db.update.value;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.AbstractPropertyWriter.mode;
import ambit2.db.processors.ValueWriter;
import ambit2.db.readers.PropertyValue;

public class UpdateCompoundPropertyValueNumber  extends AbstractUpdate<IStructureRecord, PropertyValue<Double>> {

	public static final String select_number_compound = "select null,?,idchemical,idstructure,null,?,SUBSTRING_INDEX(user(),'@',1),null,?,'NUMERIC' from structure where idchemical=? order by idstructure limit 1";
	
	protected String[] sql = new String[] {
			String.format("%s %s %s",
					UpdateStructurePropertyIDNumber.insert_descriptorvalue,
					select_number_compound,
					ValueWriter.onduplicate_number)
	};

	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getObject().getProperty()==null) || getObject().getProperty().getId()<=0) throw new AmbitException("Undefined property");
		if ((getGroup() == null) || getGroup().getIdchemical()<=0) throw new AmbitException("Undefined compound");
		List<QueryParam> l = new ArrayList<QueryParam>();
		Double value = getObject().getValue();
		mode error = getObject().getError();
		String longText = null;
		
		if (index == 0) {

			l.add(new QueryParam<Integer>(Integer.class,getObject().getProperty().getId()));
			l.add(new QueryParam<Integer>(Integer.class,error.ordinal()+1));
			l.add(new QueryParam<Double>(Double.class,value));
			l.add(new QueryParam<Integer>(Integer.class,getGroup().getIdchemical()));

		} else throw new AmbitException("Undefined index"+index);
		
		return l;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		
	}
}
