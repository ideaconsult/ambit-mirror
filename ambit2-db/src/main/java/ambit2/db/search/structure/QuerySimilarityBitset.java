package ambit2.db.search.structure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;

/**
 * Tanimoto similarity of {@link BitSet}. Use {@link  FingerprintGenerator} to obtain Bitset of {@link  IAtomContainer}.
 * @author Nina Jeliazkova nina@acad.bg
 * 
 * order by metric forces filesort! ordering not necessary if results go into query_results table
 * introduce query caching - in this case via BitSet
 *
 */
public class QuerySimilarityBitset extends QuerySimilarity<String,BitSet,NumberCondition> {

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
		StringBuffer b = new StringBuffer();
		if (isChemicalsOnly()) {
			b.append("select ? as idquery,L.idchemical,idstructure,if(type_structure='NA',0,1) as selected,round(cbits/(bc+?-cbits),2) as metric,null as text from");
			b.append("\n(select fp1024.idchemical,idstructure,(");
			for (int h=0; h < 16; h++) {
				b.append("bit_count(? & fp");
				b.append(Integer.toString(h+1));
				b.append(")");
				if (h<15) b.append(" + "); else b.append(") ");
			}
			b.append(" as cbits,bc from fp1024 join structure using(idchemical) where type_structure != 'NA' group by idchemical ");

			b.append (") as L, chemicals ");
			b.append("where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical ");
			if (isForceOrdering()) b.append("order by metric desc");			
		} else {
		
			//b.append("select cbits,bc,? as NA,round(cbits/(bc+?-cbits),2) as ");
			b.append("select ? as idquery,L.idchemical,L.idstructure,1 as selected,round(cbits/(bc+?-cbits),2) as metric,null as text from");
			b.append("\n(select fp1024.idchemical,structure.idstructure,(");
			for (int h=0; h < 16; h++) {
				b.append("bit_count(? & fp");
				b.append(Integer.toString(h+1));
				b.append(")");
				if (h<15) b.append(" + "); else b.append(") ");
			}
			b.append(" as cbits,bc from fp1024 join structure using(idchemical) ");

			b.append (") as L, chemicals ");
			b.append("where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical ");
			if (isForceOrdering()) b.append("order by metric desc");
		}			
	
		return b.toString();

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
	/*
			    
 */

}
