package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;

public class SmilesUniquenessCheck extends AbstractUpdate<IStructureRecord, String> {

	protected static String[] sql = {
		//1
		"insert ignore into ausers (user_name,email,lastname,keywords,homepage) values (\"quality_smiles\",\"quality_smiles\",\"Verifies if different chemicals have the same smiles\",\"quality_smiles\",\"http://ambit.sourceforge.net\");",
		//3
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
	int n3 = 1; //was 3;
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((index==n3)) {
			
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
