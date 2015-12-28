package ambit2.db.search.structure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.NumberCondition;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.descriptors.processors.FPTable;

/**
 * Tanimoto similarity of {@link BitSet}. Use {@link  FingerprintGenerator} to obtain Bitset of {@link  IAtomContainer}.
 * @author Nina Jeliazkova nina@acad.bg
 * 
 * order by metric forces filesort! ordering not necessary if results go into query_results table
 * introduce query caching - in this case via BitSet
 <pre>
*/
public class QuerySimilarityBitset extends QuerySimilarity<String,BitSet,NumberCondition> {
	protected FPTable mode = FPTable.fp1024;
	public FPTable getMode() {
		return mode;
	}
	public void setMode(FPTable mode) {
		this.mode = mode;
	}
	protected final String order = "order by metric desc";

	
	protected final String sql_similarity = 
			"select ? as idquery,idchemical,-1,1 as selected,round(cbits/(bc+?-cbits),2) as metric,null as text\n"+
			"from (\n"+
			"select %s.idchemical,(bit_count(? & fp1) + bit_count(? & fp2) + bit_count(? & fp3) + bit_count(? & fp4) +\n"+
			"bit_count(? & fp5) + bit_count(? & fp6) + bit_count(? & fp7) + bit_count(? & fp8) + bit_count(? & fp9) + bit_count(? & fp10) +\n"+
			"bit_count(? & fp11) + bit_count(? & fp12) + bit_count(? & fp13) + bit_count(? & fp14) + bit_count(? & fp15) + bit_count(? & fp16))\n"+
			" as cbits,bc from %s\n"+
			") as a\n"+
			"where\n"+
			"(cbits/(bc+?-cbits)>?)\n%s";
			//"order by metric desc\n";
			
			
	/**
	 * 
	 */
	private static final long serialVersionUID = 6862334340898438441L;
	public QuerySimilarityBitset() {
		setFieldname("Tanimoto");
		setChemicalsOnly(false);
	}
	/**
	 * select ? as idquery,idchemical,idstructure,1 as selected,Tanimoto as metric
	 */
	public String getSQL() throws AmbitException {
		return String.format(sql_similarity,getMode().getTable(),getMode().getTable(),isForceOrdering()?order:"");
		
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) throw new AmbitException("Fingerprint not defined!");
		BigInteger[] h16 = new BigInteger[16];
		MoleculeTools.bitset2bigint16(getValue(),64,h16);
		int bc = getValue().cardinality();
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<Integer>(Integer.class, bc));
		for (int h=0; h < 16; h++)
			params.add(new QueryParam<BigInteger>(BigInteger.class, h16[h]));
		params.add(new QueryParam<Integer>(Integer.class, bc));
		params.add(new QueryParam<Double>(Double.class, getThreshold()));		
		return params;
	}
	
	@Override
	public boolean isPrescreen() {
		return true;
	}
	@Override
	public double calculateMetric(IStructureRecord object) {

		return 1;
	}
}
