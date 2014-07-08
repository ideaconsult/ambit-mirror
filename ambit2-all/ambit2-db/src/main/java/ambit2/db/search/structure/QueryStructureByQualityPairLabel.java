package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ConsensusLabel;
import ambit2.base.data.ConsensusLabel.CONSENSUS_LABELS;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StringCondition;


public class QueryStructureByQualityPairLabel  extends AbstractStructureQuery<ISourceDataset, ConsensusLabel, StringCondition> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3232148472829083139L;
	public final static String sql = 
		"select ? as idquery,structure.idchemical,idstructure,if(type_structure='NA',0,1) as selected," +
		"cast(quality_chemicals.label as unsigned) as metric,quality_chemicals.label as text,id_srcdataset from structure join struc_dataset using(idstructure)" +
		"%s"+
		"left join quality_chemicals using(idchemical)\n %s %s %s";

	public final static String sql_chemical = 
		"select ? as idquery,idchemical,-1,1,cast(quality_chemicals.label as unsigned) as metric,quality_chemicals.label as text from quality_chemicals %s %s %s";
		
	public final static String where = " where quality_chemicals.label %s ? ";
	public final static String where_null = " where quality_chemicals.label is null ";

	public final static String where_text = " and text = ?";
	
	//protected final static String join_struc_dataset = "join struc_dataset using(idstructure)\n";
	protected final static String where_struc_dataset = "and id_srcdataset = ?\n";
	
	protected final static String join_struc_query = "join query_results using(idstructure)\n";
	protected final static String where_struc_query = "and idquery = ?\n";
	
	public QueryStructureByQualityPairLabel() {
		super();
		setValue(null);
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
	}
	public QueryStructureByQualityPairLabel(CONSENSUS_LABELS label) {
		super();
		setCondition(StringCondition.getInstance(StringCondition.C_EQ));
		setValue(new ConsensusLabel(label));
		
	}	
	public QueryStructureByQualityPairLabel(String label) {
		this(CONSENSUS_LABELS.valueOf(label));
	}	
	public String getSQL() throws AmbitException {
		String join = "";
		String where_dataset = null;
		String thesql = sql;
		if ((getFieldname()!=null) && (getFieldname().getID()>0)) {
			if (getFieldname() instanceof SourceDataset) {
				join = "";//join_struc_dataset;
				where_dataset = where_struc_dataset;
			}
			else {
				join = join_struc_query;
				where_dataset = where_struc_query;
			}
		} else thesql = sql_chemical;
		
		if (getValue()==null)
			return String.format(thesql,join, where_null,"","");
		else
			return String.format(thesql,join, 
					String.format(where,getCondition()),
							getValue().getText()==null?"":where_text,
							where_dataset==null?"":where_dataset
							
							);
	}

	public List<QueryParam> getParameters() throws AmbitException {
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		
		if (getValue()!=null) {
			params.add(new QueryParam<String>(String.class, getValue().getLabel().toString()));
			if (getValue().getText()!=null)
				params.add(new QueryParam<String>(String.class, getValue().getText()));
		}
		if ((getFieldname()!=null) && (getFieldname().getID()>0)) {
			params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
		}
				
		return params;
	}
	@Override  
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Consensus labels");
		if (getValue()!=null) {
			b.append(getCondition().toString());
			b.append(getValue());
		}
		return b.toString();
	}
	@Override
	public Object retrieveValue(ResultSet rs) throws SQLException {
		ConsensusLabel q = new ConsensusLabel();
		if (rs.getObject(5)!=null)
			q.setLabel(CONSENSUS_LABELS.values()[rs.getInt(5)-1]);
		q.setText(rs.getString(6));
		return q;
	}

	@Override
	public String getKey() {
		return null;
	}
	@Override
	public String getCategory() {
		return null;
	}
	@Override
	public double calculateMetric(IStructureRecord object) {

		return 1;
	}
	@Override
	public boolean isPrescreen() {
		return true;
	}
}
