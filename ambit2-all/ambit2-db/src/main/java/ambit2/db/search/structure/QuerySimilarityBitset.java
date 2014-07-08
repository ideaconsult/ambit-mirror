package ambit2.db.search.structure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;

/**
 * Tanimoto similarity of {@link BitSet}. Use {@link  FingerprintGenerator} to obtain Bitset of {@link  IAtomContainer}.
 * @author Nina Jeliazkova nina@acad.bg
 * 
 * order by metric forces filesort! ordering not necessary if results go into query_results table
 * introduce query caching - in this case via BitSet
 <pre>
-- chemicals --

select 1 as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,cbits/(bc+?-cbits) as metric,null as text
FROM structure s1
LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)
join (

select fp1024.idchemical,(bit_count('0' & fp1) + bit_count('2147483648' & fp2) + bit_count('8589934592' & fp3) + bit_count('2305843009215791104' & fp4) +
 bit_count('0' & fp5) + bit_count('0' & fp6) + bit_count('0' & fp7) + bit_count('0' & fp8) + bit_count('4611686018427387904' & fp9) + bit_count('0' & fp10) +
 bit_count('0' & fp11) + bit_count('274877906944' & fp12) + bit_count('0' & fp13) + bit_count('0' & fp14) + bit_count('0' & fp15) + bit_count('0' & fp16))
  as cbits,bc from fp1024 

) a on a.idchemical=s1.idchemical 
where 
s2.idchemical is null and 
(cbits/(bc+6-cbits)>0.6)
order by metric


-- structure --

select 1 as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,cbits/(bc+?-cbits) as metric,null as text
FROM structure s1
join (

select fp1024.idchemical,(bit_count('0' & fp1) + bit_count('2147483648' & fp2) + bit_count('8589934592' & fp3) + bit_count('2305843009215791104' & fp4) +
 bit_count('0' & fp5) + bit_count('0' & fp6) + bit_count('0' & fp7) + bit_count('0' & fp8) + bit_count('4611686018427387904' & fp9) + bit_count('0' & fp10) +
 bit_count('0' & fp11) + bit_count('274877906944' & fp12) + bit_count('0' & fp13) + bit_count('0' & fp14) + bit_count('0' & fp15) + bit_count('0' & fp16))
  as cbits,bc from fp1024 

) a on a.idchemical=s1.idchemical 
where  
(cbits/(bc+6-cbits)>0.6)
 </pre>
 */
public class QuerySimilarityBitset extends QuerySimilarity<String,BitSet,NumberCondition> {
	/*
	protected final String sql_all = 
		"select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,round(cbits/(bc+?-cbits),2) as metric,null as text\n"+
		"FROM structure s1\n"+
		"%s"+
		"join (\n"+

		"select fp1024.idchemical,(bit_count(? & fp1) + bit_count(? & fp2) + bit_count(? & fp3) + bit_count(? & fp4) +\n"+
		"bit_count(? & fp5) + bit_count(? & fp6) + bit_count(? & fp7) + bit_count(? & fp8) + bit_count(? & fp9) + bit_count(? & fp10) +\n"+
		"bit_count(? & fp11) + bit_count(? & fp12) + bit_count(? & fp13) + bit_count(? & fp14) + bit_count(? & fp15) + bit_count(? & fp16)) \n"+
		" as cbits,bc from fp1024\n"+ 

		") a on a.idchemical=s1.idchemical\n"+ 
		"where\n"+ 
		"%s"+ 
		"(cbits/(bc+?-cbits)>?)\n"+
		"%s";
	//order by metric --> this forces filesort
	
	protected final String sql_chemicals = "LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n";
	protected final String where_chemicals = "s2.idchemical is null and\n";
	*/
	protected final String order = "order by metric desc";

	
	protected final String sql_similarity = 
			"select ? as idquery,idchemical,-1,1 as selected,round(cbits/(bc+?-cbits),2) as metric,null as text\n"+
			"from (\n"+
			"select fp1024.idchemical,(bit_count(? & fp1) + bit_count(? & fp2) + bit_count(? & fp3) + bit_count(? & fp4) +\n"+
			"bit_count(? & fp5) + bit_count(? & fp6) + bit_count(? & fp7) + bit_count(? & fp8) + bit_count(? & fp9) + bit_count(? & fp10) +\n"+
			"bit_count(? & fp11) + bit_count(? & fp12) + bit_count(? & fp13) + bit_count(? & fp14) + bit_count(? & fp15) + bit_count(? & fp16))\n"+
			" as cbits,bc from fp1024\n"+
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
		return String.format(sql_similarity,isForceOrdering()?order:"");
		/*
		
		return String.format(sql_all, 
				isChemicalsOnly()?sql_chemicals:"", 
				isChemicalsOnly()?where_chemicals:"",
				isForceOrdering()?order:"");
		*/

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
