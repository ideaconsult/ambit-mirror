package ambit2.db.search.structure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.db.processors.FP1024Writer.FPTable;
import ambit2.db.search.QueryParam;

public class QueryPrescreenBitSet extends QuerySimilarityBitset {
	protected FPTable fptable = FPTable.fp1024;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4995229206173686206L;
	public QueryPrescreenBitSet() {
		this(FPTable.fp1024);
	}
	public QueryPrescreenBitSet(FPTable fptable) {
		this.fptable = fptable;
	}
	
	/**
	 * select ? as idquery,idchemical,idstructure,1 as selected,cbits as substructure
	 */
	public String getSQL() throws AmbitException {

		//int bc = bitset.cardinality();
		StringBuffer b = new StringBuffer();
			//b.append("select cbits,bc,? as NA,round(cbits/(bc+?-cbits),2) as ");
			b.append("select ? as idquery,L.idchemical,L.idstructure,1 as selected,cbits as metric from");
			b.append(String.format("\n(select %s.idchemical,structure.idstructure,(",fptable.getTable()));
			for (int h=0; h < 16; h++) {
				b.append("bit_count(? & fp");
				b.append(Integer.toString(h+1));
				b.append(")");
				if (h<15) b.append(" + "); else b.append(") ");
			}
			b.append(String.format(" as cbits,bc from %s join structure using(idchemical) ",fptable.getTable()));

			b.append (") as L, chemicals ");
			b.append("where L.cbits=? and L.idchemical=chemicals.idchemical");

	
		return b.toString();

	}
	
	public List<QueryParam> getParameters() throws AmbitException {
		BigInteger[] h16 = new BigInteger[16];
		MoleculeTools.bitset2bigint16(getValue(),64,h16);
		
		int bc = (getValue()==null)?0:getValue().cardinality();
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		for (int h=0; h < 16; h++)
			params.add(new QueryParam<BigInteger>(BigInteger.class, h16[h]));
		params.add(new QueryParam<Integer>(Integer.class, bc));
	
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
