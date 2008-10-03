package ambit2.db.search;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.data.MoleculeTools;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.structure.FingerprintGenerator;

/**
 * Tanimoto similarity of {@link BitSet}. Use {@link  FingerprintGenerator} to obtain Bitset of {@link  IAtomContainer}.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QuerySimilarityBitset extends QuerySimilarity<BitSet,NumberCondition> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6862334340898438441L;
	public QuerySimilarityBitset() {
		setFieldname("Tanimoto");
	}
	/**
	 * select ? as idquery,idchemical,idstructure,1 as selected,Tanimoto as metric
	 */
	public String getSQL() throws AmbitException {

		//int bc = bitset.cardinality();
		StringBuffer b = new StringBuffer();
			//b.append("select cbits,bc,? as NA,round(cbits/(bc+?-cbits),2) as ");
			b.append("select ? as idquery,L.idchemical,-1 as idstructure,1 as selected,round(cbits/(bc+?-cbits),2) as metric from");
			b.append("\n(select fp1024.idchemical,(");
			for (int h=0; h < 16; h++) {
				b.append("bit_count(? & fp");
				b.append(Integer.toString(h+1));
				b.append(")");
				if (h<15) b.append(" + "); else b.append(") ");
			}
			b.append(" as cbits,bc from fp1024 ");

			b.append (") as L, chemicals ");
			b.append("where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical order by metric desc");
					
	
		return b.toString();

	}
	public List<QueryParam> getParameters() throws AmbitException {
		long[] h16 = new long[16];
		MoleculeTools.bitset2Long16(getBitset(),64,h16);
		int bc = getBitset().cardinality();
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<Integer>(Integer.class, bc));
		params.add(new QueryParam<Integer>(Integer.class, bc));
		for (int h=0; h < 16; h++)
			params.add(new QueryParam<Long>(Long.class, h16[h]));
		params.add(new QueryParam<Integer>(Integer.class, bc));
		params.add(new QueryParam<Double>(Double.class, getThreshold()));		
		return params;
	}
	/*
			    
 */
	public BitSet getBitset() {
		return getValue();
	}
	public void setBitset(BitSet bitset) {
		setValue(bitset);
	}


}
