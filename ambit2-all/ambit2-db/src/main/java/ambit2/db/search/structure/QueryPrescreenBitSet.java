package ambit2.db.search.structure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;

/**
 * First bitset is from structural keys, second from fingerprints
 * @author nina
 *
 */
public class QueryPrescreenBitSet extends AbstractStructureQuery<BitSet,BitSet,NumberCondition> {
	protected String sql_struc = 
	"select ? as idquery,idchemical,idstructure,1 as selected,fp.bc+sk.bc as metric,null as text from structure\n";
	protected String sql_chemical = 
	"select ? as idquery,idchemical,idstructure,1 as selected,fp.bc+sk.bc as metric,null as text from structure "+group;

	protected String sql = 
	"%s\n"+
	"join fp1024 fp\n"+
	"using(idchemical)\n"+
	"join sk1024 sk\n"+
	"using(idchemical)\n"+
	"where %s\n"+
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
	"and structure.type_structure != 'NA'\n";
	

	protected String sql_exact = 
	"%s\n"+
	"join fp1024 fp\n"+
	"using(idchemical)\n"+
	"join sk1024 sk\n"+
	"using(idchemical)\n"+
	"where %s\n"+
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
	"and structure.type_structure != 'NA'\n";
	
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
		
		return String.format(getCondition().equals(NumberCondition.getInstance("="))?sql_exact:sql,
				isChemicalsOnly()?sql_chemical:sql_struc,
				isChemicalsOnly()?where_group:"");

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
		bitset2params(getFieldname(), params);
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
