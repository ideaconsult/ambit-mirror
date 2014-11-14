package ambit2.db.chemrelation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.StructureRelation;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.BooleanCondition;

/**
 * Similar to {@link ReadStructureRelation} , but retrieves the related compounds for the entire dataset
 * @author nina
 *
 */
public class ReadDatasetRelation  extends AbstractQuery<String, ISourceDataset, BooleanCondition, StructureRelation> implements IQueryRetrieval<StructureRelation> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;

	public final static String sql_query = 
		"select idchemical1,idchemical2,metric,relation from chem_relation r " +
		"join structure s on s.idchemical = r.idchemical1\n"+
		"join query_results using(idstructure)\n"+
		"where idquery=? and relation=? " +
		"group by idchemical1,idchemical2,relation order by idchemical1,idchemical2,relation" ;
	
	public final static String sql_dataset = 
		"select idchemical1,idchemical2,metric,relation from chem_relation r " +
		"join structure s on s.idchemical = r.idchemical1\n"+
		"join struc_dataset using(idstructure)\n"+
		"where id_srcdataset=? and relation=? " +
		"group by idchemical1,idchemical2,relation order by idchemical1,idchemical2,relation" ;
	protected StructureRelation result = new StructureRelation();
	
	public ReadDatasetRelation() {
		this(STRUCTURE_RELATION.HAS_TAUTOMER.name(),null);
	}
	public ReadDatasetRelation(String relation, ISourceDataset dataset) {
		super();
		setFieldname(relation);
		setValue(dataset);
		result.setFirstStructure(new StructureRecord());
		result.setSecondStructure(new StructureRecord());
	}	
	@Override
	public String getSQL() throws AmbitException {
		return (getValue() instanceof SourceDataset)?sql_dataset:sql_query;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((getValue()!=null) && (getValue().getID()>0))
			params.add(new QueryParam<Integer>(Integer.class,getValue().getID()));
		else throw new AmbitException("Empty ID");
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class,getFieldname()));
		else throw new AmbitException("Relation not specified");		
		return params;
	}
	@Override
	public String toString() {
		if (getValue()!=null)
			return String.format("%s of /dataset/%d",getFieldname()==null?"":getFieldname(),getValue().getID());
		return getFieldname();
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(StructureRelation object) {
		return 1;
	}
	@Override
	public StructureRelation getObject(ResultSet rs)
			throws AmbitException {
		try {
			result.getFirstStructure().clear();
			result.getSecondStructure().clear();
			result.getFirstStructure().setIdchemical(rs.getInt(1));
			result.getSecondStructure().setIdchemical(rs.getInt(2));
			result.setRelation(rs.getDouble(3));
			result.setRelationType(STRUCTURE_RELATION.valueOf(rs.getString(4)));
			return result;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}	
}