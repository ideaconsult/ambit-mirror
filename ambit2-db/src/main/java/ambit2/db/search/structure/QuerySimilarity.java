package ambit2.db.search.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.IQueryCondition;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;


public abstract class QuerySimilarity<F,T,C extends IQueryCondition> extends AbstractStructureQuery<F,T,C> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3509155736213435907L;
	protected double threshold = 0.5;
	protected boolean forceOrdering = false;
	public boolean isForceOrdering() {
		return forceOrdering;
	}
	public void setForceOrdering(boolean forceOrdering) {
		this.forceOrdering = forceOrdering;
	}
	public QuerySimilarity() {
		//setFieldname("similarity");


	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public double getThreshold() {
		return threshold;
	}
	@Override
	public String toString() {
		if ((getFieldname()==null) && (getValue()==null)) return getClass().getName();
		StringBuilder b = new StringBuilder();
		b.append(getFieldname());
		b.append(' ');
		b.append(getCondition());
		b.append(' ');
		b.append(getThreshold());
		return b.toString();
	}	
	protected void retrieveMetric(IStructureRecord record, ResultSet rs) throws SQLException {
		record.setRecordProperty(Property.getInstance(rs.getMetaData().getColumnName(5),toString(),"http://ambit.sourceforge.net"), retrieveValue(rs));
	}	
}
