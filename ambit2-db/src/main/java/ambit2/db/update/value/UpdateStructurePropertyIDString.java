package ambit2.db.update.value;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.AbstractPropertyWriter.mode;
import ambit2.db.readers.PropertyValue;

/**
 * Changes property value for a given structure
 * @author nina
 *
 */
public class UpdateStructurePropertyIDString extends AbstractUpdate<IStructureRecord, PropertyValue<String>> {
	public static final String select_string = "select null,?,?,?,idvalue_string,?,SUBSTRING_INDEX(user(),'@',1),?,null,'STRING' from property_string where value=?";
	public static final String insert_string = "INSERT IGNORE INTO property_string (value) VALUES (?)";	
	public static final String onduplicate_string = " on duplicate key update property_values.idvalue_string=property_string.idvalue_string, property_values.status=values(status), text=values(text),value_num=null,idtype='STRING',user_name=values(user_name) ";
	
	protected String[] sql = new String[] {
			insert_string,
			String.format("%s %s %s",
					UpdateStructurePropertyIDNumber.insert_descriptorvalue,
					select_string,
					onduplicate_string)
	};

	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((getObject().getProperty()==null) || getObject().getProperty().getId()<=0) throw new AmbitException("Undefined property");
		if ((getGroup() == null) || getGroup().getIdstructure()<=0) throw new AmbitException("Undefined structure");		
		List<QueryParam> l = new ArrayList<QueryParam>();
		String value = getObject().getValue();
		mode error = getObject().getError();
		String longText = null;
    	if ((value != null) && (value.length()>255)) {
    		longText = value;    		
    		value = value.substring(0,255);
    		error = mode.TRUNCATED;
    	} 				
		if (index == 0) {
			l.add(new QueryParam<String>(String.class,value));
		} else if (index == 1) {
			l.add(new QueryParam<Integer>(Integer.class,getObject().getProperty().getId()));
			l.add(new QueryParam<Integer>(Integer.class,getGroup().getIdchemical()));
			l.add(new QueryParam<Integer>(Integer.class,getGroup().getIdstructure()));
			l.add(new QueryParam<Integer>(Integer.class,error.ordinal()+1));
			l.add(new QueryParam<String>(String.class,longText));
			l.add(new QueryParam<String>(String.class,value));
			//l.add(new QueryParam<Integer>(Integer.class,error.ordinal()));
			//l.add(new QueryParam<String>(String.class,longText));
		} else throw new AmbitException("Undefined index"+index);
		
		return l;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		
		
	}

}
