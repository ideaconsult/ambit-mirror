package ambit2.db.update.fp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.FP1024Writer.FP1024_status;

/**
 * To replace {@link FP1024Writer}
 * @author nina
 *
 */
public class CreateFingerprintStructure extends AbstractUpdate<IStructureRecord,BitSet> {
	
	protected BigInteger[] h16 = new BigInteger[16];	
	protected static String[] sql = { 
		"INSERT INTO fp1024_struc (idchemical,idstructure,bc,status," +
		"fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16,updated) " +
		"VALUES (?,?,?,?," +
		"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP()) ON DUPLICATE KEY UPDATE " +
		"bc=values(bc),status=values(status)," +
		"fp1=values(fp1),fp2=values(fp2),  fp3=values(fp3),  fp4=values(fp4),  fp5=values(fp5),  fp6=values(fp6),  fp7=values(fp7),  fp8=values(fp8)," +
		"fp9=values(fp9),fp10=values(fp10),fp11=values(fp11),fp12=values(fp12),fp13=values(fp13),fp14=values(fp14),fp15=values(fp15),fp16=values(fp16),"+
		"updated=CURRENT_TIMESTAMP()"
		
	};
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
		params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdstructure()));	
		FP1024_status status = FP1024_status.valid;
		int bitcount = 0;
		if (getObject() == null) {
			for (int i=0; i < h16.length; i++) h16[i] = new BigInteger("0");
			status = FP1024_status.error;
		} else {
			bitcount = getObject().cardinality();
			MoleculeTools.bitset2bigint16(getObject(),64,h16);	
		}
		params.add(new QueryParam<Integer>(Integer.class, bitcount));
		params.add(new QueryParam<Integer>(Integer.class, status.ordinal()+1));
		for (int i=0; i < h16.length; i++) 
			params.add(new QueryParam<BigInteger>(BigInteger.class, h16[i]));	
		return params;
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
