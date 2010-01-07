package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class CreateQLabelCAS extends AbstractUpdate<AmbitUser, String> {
	protected static String select_cas = 

		"(\n"+
		"select name,value,idstructure,user_name,id,\n"+
		"mod(\n"+
		"if(length(value)>11,substring(right(value,12),1,1)*9,0)+\n"+
		"if(length(value)>10,substring(right(value,11),1,1)*8,0)+\n"+
		"if(length(value)>9,substring(right(value,10),1,1)*7,0)+\n"+
		"if(length(value)>8,substring(right(value,9),1,1)*6,0)+\n"+
		"if(length(value)>7,substring(right(value,8),1,1)*5,0)+\n"+
		"if(length(value)>6,substring(right(value,7),1,1)*4,0)+\n"+
		"substring(right(value,6),1,1)*3+\n"+
		"substring(right(value,4),1,1)*2+\n"+
		"substring(right(value,3),1,1)*1\n"+
		",10) cs,\n"+
		"right(value,1) g\n"+
		"from\n"+
		"properties\n"+
		"join\n"+
		"property_values using(idproperty)\n"+
		"join property_string using(idvalue_string)\n"+
		"where (length(value)<=13)\n"+
		"and (value regexp \"^[[:digit:]]{2,7}-[[:digit:]]{2}-[[:digit:]]$\") =1\n"+
		"and idtype='STRING'\n"+
		"and substring(value,1,1)>=0\n"+
		"and substring(right(value,2),1,1)='-'\n"+
		"and substring(right(value,5),1,1)='-'\n"+
		") A\n";
	protected static String[] sql = {
		"insert ignore into roles (role_name) values (\"ambit_quality\");",
		"insert ignore into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (?,\"d66636b253cb346dbb6240e30def3618\",\"CAS verifier\",\"Automatic quality verifier\",now(),\"confirmed\",\"quality\",\"http://ambit.sourceforge.net\");",
		"insert ignore into user_roles (user_name,role_name) values (?,\"ambit_quality\");",
		
		"delete from quality_structure where user_name=?",
		/*
		"insert into quality_structure (idstructure,user_name,label,text,updated)\n"+
		"select idstructure,?,if(cs=g,'OK','ERROR'),name,now() from\n"+				
		select_cas+
		"on duplicate key update label=values(label),text=values(text),updated=values(updated)\n",
		*/
		"insert into quality_labels (id,user_name,label,text,updated)\n"+
		"select id,?,if(cs=g,'OK','ERROR'),name,now() from\n"+	
		select_cas+
		"on duplicate key update label=values(label),text=values(text),updated=values(updated)\n"
	};
	
	
	public CreateQLabelCAS() {
		super();
		setGroup(new AmbitUser("CAS numbers"));
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {

		if ((index == 1) || (index == 2) || (index == 3) || (index == 4)  ){
			List<QueryParam> p = new ArrayList<QueryParam>();
			if (getGroup() ==null) setGroup(new AmbitUser("CAS numbers"));
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