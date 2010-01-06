package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class CreateQLabelEINECS  extends AbstractUpdate<AmbitUser, String> {
	protected static String[] sql = {
		"insert ignore into roles (role_name) values (\"ambit_quality\");",
		"insert ignore into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (?,\"d66636b253cb346dbb6240e30def3618\",\"CAS verifier\",\"Automatic quality verifier\",now(),\"confirmed\",\"quality\",\"http://ambit.sourceforge.net\");",
		"insert ignore into user_roles (user_name,role_name) values (?,\"ambit_quality\");",

		//"LOCK TABLES quality_pair WRITE , quality_chemicals WRITE, fp1024_struc READ \n",
		
	
		"insert into quality_structure (idstructure,user_name,label,text,updated)\n"+
		"select idstructure,?,if(abcdef=g,'OK','ERROR'),name,now() from\n"+
		"(\n"+
		"select name,value,idstructure,\n"+
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
		"and (substring(value,4,1)='-')\n"+
		"and (substring(value,8,1)='-')\n"+
		") e\n"+
		"on duplicate key update label=values(label),text=values(text),updated=values(updated)\n"
	};

	public CreateQLabelEINECS() {
		super();
		setGroup(new AmbitUser("EINECS numbers"));
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {

		if ((index == 1) || (index == 2) || (index == 3)){
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