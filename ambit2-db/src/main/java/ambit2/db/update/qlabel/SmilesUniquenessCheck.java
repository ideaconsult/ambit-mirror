package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class SmilesUniquenessCheck extends AbstractUpdate<IStructureRecord, String> {

	protected static String[] sql = {
		"insert ignore into roles (role_name) values (\"ambit_quality\");",
		"insert ignore into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (\"quality_smiles\",\"7dac3b7c36524e7fac79d1ca6086f775\",\"quality_smiles\",\"Verifies if different chemicals have the same smiles\",now(),\"confirmed\",\"quality_smiles\",\"http://ambit.sourceforge.net\");",
		"insert ignore into user_roles (user_name,role_name) values (\"quality_smiles\",\"ambit_quality\");",

		"insert into quality_structure (idstructure,user_name,`label`,`text`)\n"+
		"select idstructure,'quality_smiles',Q,'Same SMILES for different chemicals' from\n"+
		"(\n"+
		"select ? as chem,? as struc,min(idchemical),max(idchemical),\n"+
		"CASE when min(idchemical) is null THEN 'ProbablyOK'\n"+
		"WHEN min(idchemical)=max(idchemical) THEN 'ProbablyOK' ELSE 'ProbablyERROR' END as Q\n"+
		"from  chemicals where smiles=?\n"+
		") as L\n"+
		"join structure s on s.idstructure=L.struc\n"+
		"where Q='ProbablyERROR'"
	};
		
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((index==3)) {
			
			List<QueryParam> params = new ArrayList<QueryParam>();
			
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdstructure()));
			params.add(new QueryParam<String>(String.class, getObject()));
			return params;
		} else
		return null;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
