package ambit2.db.facets.qlabel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.ConsensusLabel;
import ambit2.base.data.ConsensusLabel.CONSENSUS_LABELS;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.facets.AbstractFacetQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StringCondition;

public class DatasetChemicalsQualityStats  extends AbstractFacetQuery<CONSENSUS_LABELS,SourceDataset,StringCondition,IFacet<String>> {
	protected DatasetConsensusLabelFacet record;
	protected boolean summary = false;
	public boolean isSummary() {
		return summary;
	}

	public void setSummary(boolean summary) {
		this.summary = summary;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2657669483303463773L;
	protected static final String sql = 
		"SELECT id_srcdataset,name,count(distinct(idchemical)),q.label,q.text FROM src_dataset\n"+
		"join struc_dataset using(id_srcdataset)\n"+
		"join structure using(idstructure)\n"+
		"join quality_chemicals q using(idchemical)\n"+
		"%s\n"+
		"group by id_srcdataset,label,text with rollup\n";

	protected static final String sql_summary = 
		"SELECT null,null,count(idchemical),label,text FROM quality_chemicals\n"+
		"group by  label,text with rollup\n";

	public DatasetChemicalsQualityStats(String facetURL) {
		super(facetURL);
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(IFacet<String> object) {
		return 1;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((getValue() !=null) && getValue().getId()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getId()));
		
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		if ((getValue() !=null) && getValue().getId()>0) return String.format(sql,"where id_srcdataset=?");
		else return String.format(isSummary()?sql_summary:sql,"");
	}

	@Override
	public IFacet<String> getObject(ResultSet rs) throws AmbitException {
		if (record == null) 
			record = new DatasetConsensusLabelFacet(null);
		
		try {
			SourceDataset dataset = null;
			Object iddataset = rs.getObject(1);
			String dbname = rs.getString(2);
			if (iddataset!=null) {
				dataset = new SourceDataset(dbname);
				dataset.setId(rs.getInt(1));
				record.setDataset(dataset);
			}
			String label = rs.getString("label");
			String text = rs.getString("text");
			record.setValue(String.format("[%s] %s %s",
						dataset==null?"Total":dataset.getName(),label==null?"All labels":label,text==null?"":text));
			
			record.setCount(rs.getInt(3));
			ConsensusLabel l = null;
			if(label!=null) {
				l = new ConsensusLabel(CONSENSUS_LABELS.valueOf(rs.getString("label")));
				l.setText(text);
			}
			record.setProperty(l);
			
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			return record;
		}
	}
	
	@Override
	public String toString() {
		return "Compound quality label";
	}

}
