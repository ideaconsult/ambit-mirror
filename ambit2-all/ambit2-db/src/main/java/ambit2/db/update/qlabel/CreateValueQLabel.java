package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.QLabel;

public class CreateValueQLabel extends AbstractUpdate<Integer,QLabel> {

	protected String[] sql = {
			"insert into quality_labels (id,sameas,`label`,`text`) values (?,?,?,?) on duplicate key update `label`=values(`label`),text=values(text),updated=CURRENT_TIMESTAMP"
	};
	protected String[] sql_defaultuser = {
			"insert into quality_labels (id,sameas,`label`,`text`) values (?,SUBSTRING_INDEX(user(),'@',1),?,?) on duplicate key update `label`=values(`label`),`text`=values(`text`),sameas=values(sameas),updated=CURRENT_TIMESTAMP"
	};
	
	public CreateValueQLabel(Integer property_value, QLabel label) {
		setGroup(property_value);
		setObject(label);
	}
	public CreateValueQLabel() {
		this(null,null);
	}	
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null) throw new AmbitException("Label not defined");
		if (getGroup()==null) throw new AmbitException("Property Value not defined");
		
		List<QueryParam> params1 = new ArrayList<QueryParam>();
		params1.add(new QueryParam<Integer>(Integer.class, getGroup()));
		if (getObject().getUser()!=null) 
			params1.add(new QueryParam<String>(String.class, getObject().getUser().getName()));
		params1.add(new QueryParam<String>(String.class, getObject().getLabel().toString()));
		params1.add(new QueryParam<String>(String.class, getObject().getText()));

		return params1;
	}

	public String[] getSQL() throws AmbitException {
		return (getObject().getUser()==null)?sql_defaultuser:sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
