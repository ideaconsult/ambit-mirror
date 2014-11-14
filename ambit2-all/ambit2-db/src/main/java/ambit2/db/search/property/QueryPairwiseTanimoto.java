package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.relation.SimilarityRelation;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.IStoredQuery;

/**
 * Generatise pairwise similarities between structures in given datasets
 * @author nina
 *
 */
public class QueryPairwiseTanimoto extends AbstractQuery<ISourceDataset, ISourceDataset, BooleanCondition, SimilarityRelation> implements IQueryRetrieval<SimilarityRelation> {
	protected SimilarityRelation relation = 
		new SimilarityRelation(new StructureRecord(),new StructureRecord());
	/**
	 * 
	 */
	protected double threshold = 0.8;
	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	private static final long serialVersionUID = -5148418049120426363L;
	private static final String sql_tanimoto = 
	    "(bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +\n"+
	    "bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +\n"+
	    "bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +\n"+
	    "bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))\n"+
	    "/(f1.bc + f2.bc -\n"+
	    "(bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +\n"+
	    "bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +\n"+
	    "bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +\n"+
	    "bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))\n"+
	    ") Tanimoto\n";

	private static final String sql_header = 
		String.format(
		"select idchemical1,idstructure1,idchemical2,idstructure2,Tanimoto from\n"+
		"(\n"+
		"SELECT s1.idchemical as idchemical1,s1.idstructure as idstructure1,s2.idchemical as idchemical2,s2.idstructure as idstructure2,\n"+
		"%s FROM ",sql_tanimoto);

	private static final String sql_dataset = 
		String.format(
		"%s struc_dataset as d1 " +
		"join struc_dataset as d2 \n" +
		"join structure s1 on d1.idstructure=s1.idstructure\n"+
		"join fp1024 as f1 on s1.idchemical=f1.idchemical\n"+
		"join structure s2 on d2.idstructure=s2.idstructure\n"+
		"join fp1024 as f2 on s2.idchemical=f2.idchemical\n"+
		"where d1.id_srcdataset=? and d2.id_srcdataset=?\n"+
		"and f1.idchemical < f2.idchemical\n"+ 
		") as c where Tanimoto>?\n",sql_header);	
	
	private static final String sql_rdataset = 
		String.format(
		"%s query_results as s1 " +
		"join query_results as s2 \n" +
		"join fp1024 as f1 on s1.idchemical=f1.idchemical\n"+
		"join fp1024 as f2 on s2.idchemical=f2.idchemical\n"+
		"where s1.idquery=? and s2.idquery=?\n"+
		"and f1.idchemical < f2.idchemical\n"+ 
		") as c where Tanimoto>?\n",sql_header);	
	
	private static final String sql_dataset_rdataset = 
		String.format(
		"%s struc_dataset as d1 \n" +
		"join structure s1 on d1.idstructure=s1.idstructure\n"+		
		"join query_results as s2 \n" +
		"join fp1024 as f1 on s1.idchemical=f1.idchemical\n"+
		"join fp1024 as f2 on s2.idchemical=f2.idchemical\n"+
		"where d1.id_srcdataset=? and s2.idquery=?\n"+
		"and f1.idchemical < f2.idchemical\n"+ 
		") as c where Tanimoto>?\n",sql_tanimoto);	
	
	public List<QueryParam> getParameters() throws AmbitException {
		ISourceDataset q1 = (getFieldname()!=null)?getFieldname():getValue();
		ISourceDataset q2 = (getValue()!=null)?getValue():getFieldname();
		
		if ((q1==null) && (q2==null)) throw new AmbitException("Datasets not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		
		if ((q1 instanceof IStoredQuery) && !(q2 instanceof IStoredQuery)) {
				//switch the parameter orders; dataset is first, rdataset second
				params.add(new QueryParam<Integer>(Integer.class, q2.getID()));
				params.add(new QueryParam<Integer>(Integer.class, q1.getID()));
				return params;
			}
		//otherwise the order doesn't matter
		params.add(new QueryParam<Integer>(Integer.class, q1.getID()));
		params.add(new QueryParam<Integer>(Integer.class, q2.getID()));
		params.add(new QueryParam<Double>(Double.class, getThreshold()));
		return params;	
	}

	public String getSQL() throws AmbitException {
		ISourceDataset q1 = (getFieldname()!=null)?getFieldname():getValue();
		ISourceDataset q2 = (getValue()!=null)?getValue():getFieldname();
		if (q1 instanceof IStoredQuery) {
			if (q2 instanceof IStoredQuery) return sql_rdataset;
			else return sql_dataset_rdataset;
		} else if (q2 instanceof IStoredQuery) return sql_dataset_rdataset;
			else return sql_dataset;
	}
	
	@Override
	public SimilarityRelation getObject(ResultSet rs)
			throws AmbitException {
		try {
			relation.getFirstStructure().clear();
			relation.getSecondStructure().clear();
			int i=0;
			relation.getFirstStructure().setIdchemical(rs.getInt(i*2+1));
			relation.getFirstStructure().setIdchemical(rs.getInt(i*2+2));
			i=1;
			relation.getSecondStructure().setIdchemical(rs.getInt(i*2+1));
			relation.getSecondStructure().setIdchemical(rs.getInt(i*2+2));
			
			return new SimilarityRelation(relation.getFirstStructure(),relation.getSecondStructure(), rs.getDouble(5));
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(SimilarityRelation object) {
		return 1;
	}

}
