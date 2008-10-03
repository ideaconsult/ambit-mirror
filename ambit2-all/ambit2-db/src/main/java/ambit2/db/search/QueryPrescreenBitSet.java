package ambit2.db.search;

import java.util.ArrayList;
import java.util.List;

import ambit2.core.data.MoleculeTools;
import ambit2.core.exceptions.AmbitException;

public class QueryPrescreenBitSet extends QuerySimilarityBitset {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4995229206173686206L;
	/**
	 * select ? as idquery,idchemical,idstructure,1 as selected,cbits as metric
	 */
	public String getSQL() throws AmbitException {

		//int bc = bitset.cardinality();
		StringBuffer b = new StringBuffer();
			//b.append("select cbits,bc,? as NA,round(cbits/(bc+?-cbits),2) as ");
			b.append("select ? as idquery,L.idchemical,-1 as idstructure,1 as selected,cbits as metric from");
			b.append("\n(select fp1024.idchemical,(");
			for (int h=0; h < 16; h++) {
				b.append("bit_count(? & fp");
				b.append(Integer.toString(h+1));
				b.append(")");
				if (h<15) b.append(" + "); else b.append(") ");
			}
			b.append(" as cbits,bc from fp1024 ");

			b.append (") as L, chemicals ");
			b.append("where L.cbits=? and L.idchemical=chemicals.idchemical");

	
		return b.toString();

	}
	
	public List<QueryParam> getParameters() throws AmbitException {
		long[] h16 = new long[16];
		MoleculeTools.bitset2Long16(getBitset(),64,h16);
		int bc = getBitset().cardinality();
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		for (int h=0; h < 16; h++)
			params.add(new QueryParam<Long>(Long.class, h16[h]));
		params.add(new QueryParam<Integer>(Integer.class, bc));
	
		return params;
	}	
	@Override
	public String toString() {
		return "Substructure prescreen";
	}
}
