package ambit2.db.update.fp;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;

public class ReadStructuresByFingerprint extends AbstractStructureQuery<String,IFingerprint<FPTable,String>,StringCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2852604942293779318L;
	public static final String sql=
			"select ? as idquery,idchemical,-1,null,bc,null\n" +
			"from %s "+
			"where concat(\n"+
			"hex(fp1),'-',\n"+
			"hex(fp2),'-',\n"+
			"hex(fp3),'-',\n"+
			"hex(fp4),'-',\n"+
			"hex(fp5),'-',\n"+
			"hex(fp6),'-',\n"+
			"hex(fp7),'-',\n"+
			"hex(fp8),'-',\n"+
			"hex(fp9),'-',\n"+
			"hex(fp10),'-',\n"+
			"hex(fp11),'-',\n"+
			"hex(fp12),'-',\n"+
			"hex(fp13),'-',\n"+
			"hex(fp14),'-',\n"+
			"hex(fp15),'-',\n"+
			"hex(fp16)\n"+
			") = ?";
	
	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) throw new AmbitException("Missing fingerprint value!");
		List<QueryParam> p = new ArrayList<QueryParam>();
		p.add(new QueryParam<Integer>(Integer.class, getId()));
		p.add(new QueryParam<String>(String.class,getValue().getBits()));
		return p;
	}

	@Override
	public String getSQL() throws AmbitException {
		if (getValue().getType()==null)  throw new AmbitException("Empty parameter");
		return String.format(sql,getValue().getType().getTable());
	}

}
