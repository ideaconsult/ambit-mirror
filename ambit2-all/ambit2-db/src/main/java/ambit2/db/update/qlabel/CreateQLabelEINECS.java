package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.AmbitUser;

public class CreateQLabelEINECS  extends AbstractUpdate<AmbitUser, String> {
	protected static String select_einecs = 
		"(\n"+
		"select name,value,idstructure,user_name,id,\n"+
		"mod(substring(value,1,1) +\n"+
		"2*substring(value,2,1) +\n"+
		"3*substring(value,3,1) +\n"+
		"4*substring(value,5,1) +\n"+
		"5*substring(value,6,1) +\n"+
		"6*substring(value,7,1),11) abcdef,\n"+
		"substring(value,9,1) g\n"+
		"from\n"+
		"properties\n"+
		"join\n"+
		"property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)\n"+
		"where (length(value)=9) and (substring(value,9,1)<11)\n"+
		"and (value regexp \"^([[:digit:]]{3}-){2}[[:digit:]]$\") =1\n"+
		"and (substring(value,4,1)='-')\n"+
		"and (substring(value,8,1)='-')\n"+
		") e\n";		
	
	protected static String[] sql = {
		//1
		"insert ignore into ausers (user_name,email,lastname,keywords,homepage) values (?,\"CAS verifier\",\"Automatic quality verifier\",\"quality\",\"http://ambit.sourceforge.net\");",
		//3
		"delete from quality_structure where user_name=?",
		//4
		"insert into quality_labels (id,user_name,label,text,updated)\n"+
		//5
		"select id,?,if(abcdef=g,'OK','ERROR'),name,now() from\n"+	
		select_einecs+
		"on duplicate key update label=values(label),text=values(text),updated=values(updated)\n"		
	};

	public CreateQLabelEINECS() {
		super();
		setGroup(new AmbitUser("EINECS numbers"));
	}
	int n1 = 0; //was 1;
	int n2 = -1; //was 2;
	int n3 = 1; //was 3;
	int n4 = 2; //was 4;
	public List<QueryParam> getParameters(int index) throws AmbitException {

		if ((index == n1) || (index == n2) || (index == n3) || (index == n4)){
			List<QueryParam> p = new ArrayList<QueryParam>();
			if (getGroup() ==null) setGroup(new AmbitUser("EINECS numbers"));
			p.add(new QueryParam<String>(String.class,getGroup().getName()));
			return p;
		} else return null;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
	}

}