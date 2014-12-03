package ambit2.db.update.qlabel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;

public class CreateQLabelFingerprints extends AbstractUpdate<IStructureRecord, BitSet> {
	protected static String[] sql = {
		//1
		"insert ignore into ausers (user_name,email,lastname,keywords,homepage) values (\"quality\",\"quality\",\"Automatic quality verifier\",\"quality\",\"http://ambit.sourceforge.net\");",
		//3
		"insert into quality_structure (idstructure,user_name,`label`,`text`)\n"+
		"SELECT idstructure,\"quality\",\"ProbablyOK\",? FROM structure left join fp1024 using(idchemical)\n"+
		"where idchemical=? and idstructure=?\n"+
		"and fp1=? and fp2=? and fp3=? and fp4=? and fp5=? and fp6=? and fp7=? and fp8=?\n"+
		"and fp9=? and fp10=? and fp11=? and fp12=? and fp13=? and fp14=? and fp15=? and fp16=?\n"+
		"on duplicate key update `label`=values(`label`), `text`=values(`text`)\n",

		//4
		"insert into quality_structure (idstructure,user_name,`label`,`text`)\n"+
		"SELECT idstructure,\"quality\",\"ERROR\",? FROM structure left join fp1024 using(idchemical)\n"+
		"where (idchemical=? and idstructure=?) and (\n"+
		"fp1!=? or fp2!=? or fp3!=? or fp4!=? or fp5!=? or fp6!=? or fp7!=? or fp8!=?\n"+
		"or fp9!=? or fp10!=? or fp11!=? or fp12!=? or fp13!=? or fp14!=? or fp15!=? or fp16!=? )\n"+
		"on duplicate key update `label`=values(`label`), `text`=values(`text`)\n",	
	};
	int n3 = 1; //was 3;
	int n4 = 2; //was 4;
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if ((index==n3) || (index==n4)) {
			BigInteger[] h16 = new BigInteger[16];
			MoleculeTools.bitset2bigint16(getObject(),64,h16);
			
			List<QueryParam> params = new ArrayList<QueryParam>();

			
			if (index==4) {
				StringBuilder b = new StringBuilder();
				b.append("Different fingerprints for the same chemical\t");
				for (int i=0; i <16;i++) {b.append( h16[i]); b.append('\t');}
				params.add(new QueryParam<String>(String.class, b.toString()));
			} else
				params.add(new QueryParam<String>(String.class, "Same fingerprints"));
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdstructure()));			
			for (int i=0; i <16;i++) {
				params.add(new QueryParam<BigInteger>(BigInteger.class, h16[i]));
			}
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
