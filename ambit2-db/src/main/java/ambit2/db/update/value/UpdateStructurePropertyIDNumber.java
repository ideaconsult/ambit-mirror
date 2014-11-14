package ambit2.db.update.value;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ValueWriter;
import ambit2.db.readers.PropertyValue;

/**
	Updates property value of a structure
 * @author nina
 *
 */
public class UpdateStructurePropertyIDNumber extends AbstractUpdate<IStructureRecord, PropertyValue<Number>> {
	public static final String insert_descriptorvalue = 
			"INSERT INTO property_values (id,idproperty,idchemical,idstructure,idvalue_string,status,user_name,text,value_num,idtype) ";
	public static final String select_number = "values (null,?,?,?,null,?,SUBSTRING_INDEX(user(),'@',1),null,?,'NUMERIC')";
	
	protected String[] sql = new String[] {
			String.format("%s %s %s",
					insert_descriptorvalue,
					select_number,
					ValueWriter.onduplicate_number)
	};

	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getObject().getProperty()==null) || getObject().getProperty().getId()<=0) throw new AmbitException("Undefined property");
		if ((getGroup() == null) || getGroup().getIdstructure()<=0) throw new AmbitException("Undefined structure");
		List<QueryParam> l = new ArrayList<QueryParam>();
		Number value = getObject().getValue();

		String longText = null;
		
		if (index == 0) {

			l.add(new QueryParam<Integer>(Integer.class,getObject().getProperty().getId()));
			l.add(new QueryParam<Integer>(Integer.class,getGroup().getIdchemical()));
			l.add(new QueryParam<Integer>(Integer.class,getGroup().getIdstructure()));
			l.add(new QueryParam<Integer>(Integer.class,getObject().getError().ordinal()+1));
			if (value == null)
				l.add(new QueryParam<Double>(Double.class,null));
			else
				l.add(new QueryParam<Double>(Double.class,value.doubleValue()));
			//l.add(new QueryParam<Double>(Double.class,value));
			//l.add(new QueryParam<Integer>(Integer.class,error.ordinal()));
			
		} else throw new AmbitException("Undefined index"+index);
		
		return l;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		
	}
}
