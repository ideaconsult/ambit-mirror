package ambit2.db.search.structure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.db.search.NumberCondition;

/**
 * First bitset is from structural keys, second from fingerprints
 * @author nina
 *
 <pre>
select SQL_NO_CACHE -1 as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,s1.preference as metric,null as text,-1 as idproperty
FROM structure s1
LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)
join (

select idchemical from fp1024 fp join sk1024 sk using(idchemical)
where (bit_count('207704626823199' & sk.fp1) + bit_count('0' & sk.fp2) + bit_count('0' & sk.fp3) +
bit_count('0' & sk.fp4) + bit_count('0' & sk.fp5) + bit_count('0' & sk.fp6) + bit_count('0' & sk.fp7) +
bit_count('0' & sk.fp8) + bit_count('0' & sk.fp9) + bit_count('0' & sk.fp10) + bit_count('0' & sk.fp11) +
bit_count('0' & sk.fp12) + bit_count('0' & sk.fp13) + bit_count('0' & sk.fp14) + bit_count('0' & sk.fp15) +
bit_count('0' & sk.fp16))=15
and
(bit_count('0' & fp.fp1) + bit_count('2147483648' & fp.fp2) +
bit_count('8589934592' & fp.fp3) + bit_count('2305843009215799296' & fp.fp4) +
bit_count('0' & fp.fp5) + bit_count('0' & fp.fp6) + bit_count('128' & fp.fp7) +
bit_count('0' & fp.fp8) + bit_count('4611686018427387904' & fp.fp9) + bit_count('137438953472' & fp.fp10) +
bit_count('0' & fp.fp11) + bit_count('2615635083264' & fp.fp12) + bit_count('0' & fp.fp13) +
bit_count('0' & fp.fp14) + bit_count('8589934592' & fp.fp15) + bit_count('0' & fp.fp16))=13

) a on a.idchemical=s1.idchemical
where s2.idchemical is null
and s1.type_structure != 'NA'
 limit 0,1000;
 </pre>
 */
public class QueryPrescreenBitSet extends AbstractStructureQuery<BitSet,BitSet,NumberCondition> {
	protected String sql_struc = 
	"select ? as idquery,s1.idchemical,s1.idstructure,if(type_structure='NA',0,1) as selected,m as metric,null as text from structure s1\n"+
	"join (\n"+
	"%s\n"+
	") a on a.idchemical=s1.idchemical\n"+
	"where s1.type_structure != 'NA'";
	
	protected String sql_chemical = 
	//"select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,m as metric,null as text from structure "+group;
	"select ? as idquery,s1.idchemical,s1.idstructure,0 as selected,m as metric,null as text\n"+
	"FROM structure s1\n"+
	"LEFT JOIN structure s2 ON s1.idchemical = s2.idchemical  AND (1E10*s1.preference+s1.idstructure) > (1E10*s2.preference+s2.idstructure)\n"+
	"join (\n"+
	"%s\n"+
	") a on a.idchemical=s1.idchemical\n"+
	"where s2.idchemical is null\n"+
	"and s1.type_structure != 'NA'";
	/*
	protected String sql_prescreen = 
	"select fp.idchemical,fp.bc+sk.bc as m from fp1024 fp\n"+
	"join sk1024 sk\n"+
	"using(idchemical)\n"+
	"where \n"+
	"(bit_count(? & sk.fp1) + bit_count(? & sk.fp2) + bit_count(? & sk.fp3) +\n"+
	"bit_count(? & sk.fp4) + bit_count(? & sk.fp5) + bit_count(? & sk.fp6) + bit_count(? & sk.fp7) +\n"+
	"bit_count(? & sk.fp8) + bit_count(? & sk.fp9) + bit_count(? & sk.fp10) + bit_count(? & sk.fp11) +\n"+
	"bit_count(? & sk.fp12) + bit_count(? & sk.fp13) + bit_count(? & sk.fp14) + bit_count(? & sk.fp15) +\n"+
	"bit_count(? & sk.fp16))=?\n"+
	"and\n"+
	"(bit_count(? & fp.fp1) + bit_count(? & fp.fp2) +\n"+
	"bit_count(? & fp.fp3) + bit_count(? & fp.fp4) +\n"+
	"bit_count(? & fp.fp5) + bit_count(? & fp.fp6) + bit_count(? & fp.fp7) +\n"+
	"bit_count(? & fp.fp8) + bit_count(? & fp.fp9) + bit_count(? & fp.fp10) +\n"+
	"bit_count(? & fp.fp11) + bit_count(? & fp.fp12) + bit_count(? & fp.fp13) +\n"+
	"bit_count(? & fp.fp14) + bit_count(? & fp.fp15) + bit_count(? & fp.fp16))=?\n";
	*/
	protected String sk_prescreen = 
		"select ? as idquery,idchemical,-1,0 as selected,sk.bc as metric,null as text\n"+
		"from sk1024 sk\n"+
		"where\n"+
		"(bit_count(? & sk.fp1) + bit_count(? & sk.fp2) + bit_count(? & sk.fp3) +\n"+
		"bit_count(? & sk.fp4) + bit_count(? & sk.fp5) + bit_count(? & sk.fp6) + bit_count(? & sk.fp7) +\n"+
		"bit_count(? & sk.fp8) + bit_count(? & sk.fp9) + bit_count(? & sk.fp10) + bit_count(? & sk.fp11) +\n"+
		"bit_count(? & sk.fp12) + bit_count(? & sk.fp13) + bit_count(? & sk.fp14) + bit_count(? & sk.fp15) +\n"+
		"bit_count(? & sk.fp16))=?\n"+
		"order by idchemical\n";
	

	protected String fp_prescreen = 
		"select ? as idquery,idchemical,-1,0 as selected,fp.bc as metric,null as text\n"+
		"from fp1024 fp\n"+
		"where\n"+
		"(bit_count(? & fp.fp1) + bit_count(? & fp.fp2) +\n"+
		"bit_count(? & fp.fp3) + bit_count(? & fp.fp4) +\n"+
		"bit_count(? & fp.fp5) + bit_count(? & fp.fp6) + bit_count(? & fp.fp7) +\n"+
		"bit_count(? & fp.fp8) + bit_count(? & fp.fp9) + bit_count(? & fp.fp10) +\n"+
		"bit_count(? & fp.fp11) + bit_count(? & fp.fp12) + bit_count(? & fp.fp13) +\n"+
		"bit_count(? & fp.fp14) + bit_count(? & fp.fp15) + bit_count(? & fp.fp16))=?\n"+
		"order by idchemical\n";
	
	protected String sql_all = 
		"select ? as idquery,idchemical,-1,0 as selected,1 as metric,null as text\n"+
		"from chemicals\n"+
		"order by idchemical\n";	
	
	protected String sql_prescreen = 
		"select ? as idquery,idchemical,-1,0 as selected,fp.bc+sk.bc as metric,null as text\n"+
		"from fp1024 fp\n"+
		"join sk1024 sk\n"+
		"using(idchemical)\n"+
		"where\n"+
		"(bit_count(? & sk.fp1) + bit_count(? & sk.fp2) + bit_count(? & sk.fp3) +\n"+
		"bit_count(? & sk.fp4) + bit_count(? & sk.fp5) + bit_count(? & sk.fp6) + bit_count(? & sk.fp7) +\n"+
		"bit_count(? & sk.fp8) + bit_count(? & sk.fp9) + bit_count(? & sk.fp10) + bit_count(? & sk.fp11) +\n"+
		"bit_count(? & sk.fp12) + bit_count(? & sk.fp13) + bit_count(? & sk.fp14) + bit_count(? & sk.fp15) +\n"+
		"bit_count(? & sk.fp16))=?\n"+
		"and\n"+
		"(bit_count(? & fp.fp1) + bit_count(? & fp.fp2) +\n"+
		"bit_count(? & fp.fp3) + bit_count(? & fp.fp4) +\n"+
		"bit_count(? & fp.fp5) + bit_count(? & fp.fp6) + bit_count(? & fp.fp7) +\n"+
		"bit_count(? & fp.fp8) + bit_count(? & fp.fp9) + bit_count(? & fp.fp10) +\n"+
		"bit_count(? & fp.fp11) + bit_count(? & fp.fp12) + bit_count(? & fp.fp13) +\n"+
		"bit_count(? & fp.fp14) + bit_count(? & fp.fp15) + bit_count(? & fp.fp16))=?\n"+
		"order by idchemical\n";

	protected String sql_exact = 
		"select ? as idquery,idchemical,-1,0 as selected,fp.bc+sk.bc as metric,null as text\n"+
		"from fp1024 fp\n"+
		"join sk1024 sk\n"+
		"using(idchemical)\n"+
		"where \n"+
		"(? = sk.fp1) and (? = sk.fp2) and (? = sk.fp3) and\n"+
		"(? = sk.fp4) and (? = sk.fp5) and (? = sk.fp6) and (? = sk.fp7) and\n"+
		"(? = sk.fp8) and (? = sk.fp9) and (? = sk.fp10) and (? = sk.fp11) and\n"+
		"(? = sk.fp12) and (? = sk.fp13) and (? = sk.fp14) and (? = sk.fp15) and\n"+
		"(? = sk.fp16)\n"+
		"and\n"+
		"(? = fp.fp1) and (? = fp.fp2) and\n"+
		"(? = fp.fp3) and (? = fp.fp4) and\n"+
		"(? = fp.fp5) and (? = fp.fp6) and (? = fp.fp7) and\n"+
		"(? = fp.fp8) and (? = fp.fp9) and (? = fp.fp10) and\n"+
		"(? = fp.fp11) and (? = fp.fp12) and (? = fp.fp13) and\n"+
		"(? = fp.fp14) and (? = fp.fp15) and (? = fp.fp16)\n"+
		"order by idchemical\n";
	
	/*
	protected String sql_exact = 
	"select idchemical,fp.bc+sk.bc as m from fp1024 fp\n"+
	"join sk1024 sk\n"+
	"using(idchemical)\n"+
	"where \n"+
	"(? = sk.fp1) and (? = sk.fp2) and (? = sk.fp3) and\n"+
	"(? = sk.fp4) and (? = sk.fp5) and (? = sk.fp6) and (? = sk.fp7) and\n"+
	"(? = sk.fp8) and (? = sk.fp9) and (? = sk.fp10) and (? = sk.fp11) and\n"+
	"(? = sk.fp12) and (? = sk.fp13) and (? = sk.fp14) and (? = sk.fp15) and\n"+
	"(? = sk.fp16)\n"+
	"and\n"+
	"(? = fp.fp1) and (? = fp.fp2) and\n"+
	"(? = fp.fp3) and (? = fp.fp4) and\n"+
	"(? = fp.fp5) and (? = fp.fp6) and (? = fp.fp7) and\n"+
	"(? = fp.fp8) and (? = fp.fp9) and (? = fp.fp10) and\n"+
	"(? = fp.fp11) and (? = fp.fp12) and (? = fp.fp13) and\n"+
	"(? = fp.fp14) and (? = fp.fp15) and (? = fp.fp16)\n";
	*/
	/**
	 * 
	 */
	private static final long serialVersionUID = -4995229206173686206L;
	protected boolean chemicalsOnly = true;
	public boolean isChemicalsOnly() {
		return chemicalsOnly;
	}

	public void setChemicalsOnly(boolean chemicalsOnly) {
		this.chemicalsOnly = chemicalsOnly;
	}

	public QueryPrescreenBitSet() {
		super();
		setCondition(NumberCondition.getInstance("<"));
	}
	
	public String getSQL() throws AmbitException {
		if (NumberCondition.getInstance("=").equals(getCondition())) return sql_exact;
		else
			if ((getValue()!=null) && (getFieldname()!= null)) return sql_prescreen;
		    if (getValue()!= null) return fp_prescreen;
		    if (getFieldname() != null) return sk_prescreen;
		    return sql_all;
				
			//return getCondition().equals(NumberCondition.getInstance("="))?sql_exact:sql_prescreen;
		/*
		return String.format(isChemicalsOnly()?sql_chemical:sql_struc,
				getCondition().equals(NumberCondition.getInstance("="))?sql_exact:sql_prescreen
				);
	*/
	}
	protected void bitset2params(BitSet bitset, List<QueryParam> params) throws AmbitException {
		BigInteger[] h16 = new BigInteger[16];
		MoleculeTools.bitset2bigint16(bitset,64,h16);
		
		int bc = (getValue()==null)?0:bitset.cardinality();
		for (int h=0; h < 16; h++)
			params.add(new QueryParam<BigInteger>(BigInteger.class, h16[h]));
		
		if (!getCondition().equals(NumberCondition.getInstance("=")))
			params.add(new QueryParam<Integer>(Integer.class, bc));

	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		if (getFieldname()!=null)
			bitset2params(getFieldname(), params);
		if (getValue()!=null)
			bitset2params(getValue(), params);
		return params;
	}	
	@Override
	public String toString() {
		return "Substructure ";
	}
	public double calculateMetric(IStructureRecord item) {
		return 1;
	}
	@Override
	public boolean isPrescreen() {
		return true;
	}
}
