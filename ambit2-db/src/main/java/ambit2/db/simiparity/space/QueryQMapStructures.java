package ambit2.db.simiparity.space;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

public class QueryQMapStructures extends AbstractStructureQuery<String,Integer,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	public final static String sql = 
		"select idsasmap,idchemical,-1,1,g2 as metric,null as text,a,b,c,d,fisher from qsasmap4 where idsasmap=? order by g2 desc ";
	public QueryQMapStructures(SourceDataset dataset) {
		this(dataset==null?null:dataset.getId());
	}
	public QueryQMapStructures() {
		this((SourceDataset)null);
	}
	public QueryQMapStructures(Integer id) {
		super();
		setFieldname("metric");
		setValue(id);
		setCondition(NumberCondition.getInstance("="));
	}	
	public String getSQL() throws AmbitException {
		return 	String.format(sql,getCondition().getSQL(),getValue()==null?"":"?");
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue()!=null)
			params.add(new QueryParam<Integer>(Integer.class,getValue()));
		else throw new AmbitException("Empty ID");
		return params;
	}
	@Override
	public String toString() {
		if (getValue()==null) return "QSASMap";
		return String.format("QSASMap %d",getValue());
	}

	@Override
	public boolean isPrescreen() {
		return true;
	}
	@Override
	public double calculateMetric(IStructureRecord object) {

		return 1;
	}
	@Override
	protected void retrieveMetric(IStructureRecord record, ResultSet rs) throws SQLException {
		record.setProperty(Property.getInstance("metric",toString(),"http://ambit.sourceforge.net"), retrieveValue(rs));
		record.setProperty(Property.getInstance("a",toString(),"http://ambit.sourceforge.net"), rs.getFloat(7));
		record.setProperty(Property.getInstance("b",toString(),"http://ambit.sourceforge.net"), rs.getFloat(8));
		record.setProperty(Property.getInstance("c",toString(),"http://ambit.sourceforge.net"), rs.getFloat(9));
		record.setProperty(Property.getInstance("d",toString(),"http://ambit.sourceforge.net"), rs.getFloat(10));
		record.setProperty(Property.getInstance("fisher",toString(),"http://ambit.sourceforge.net"), rs.getFloat(11));
	}	
}
/**
select * from (
select s1.idchemical i,s2.idchemical j,
	  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
	  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
	  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
	  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
	  /(f1.bc + f2.bc -
	  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
	  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
	  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
	  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
	  ) sim, s1.value_num v1,s2.value_num v2
	  from   struc_dataset d1
	  join   struc_dataset d2
	  join property_values s2 on d2.idstructure=s2.idstructure
	  join property_values s1 on d1.idstructure=s1.idstructure
	  join fp1024 f1 on f1.idchemical=s1.idchemical
	  join fp1024 f2 on f2.idchemical=s2.idchemical
	  where d1.id_srcdataset=112
	  and d2.id_srcdataset= 112 
      and s1.idproperty=274 and s2.idproperty=274
	  and s1.idchemical != s2.idchemical
and s1.idchemical=12391
) a where sim>0.75
*/