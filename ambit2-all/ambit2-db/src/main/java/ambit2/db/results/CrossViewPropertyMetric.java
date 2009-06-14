package ambit2.db.results;

import java.beans.PropertyChangeListener;
import java.sql.ResultSet;

import javax.swing.ListModel;
import javax.swing.table.AbstractTableModel;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyStats;
import ambit2.base.data.Range;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.property.FilteredCount;
import ambit2.db.search.property.FilteredCountNominal;
import ambit2.db.search.property.FilteredCountNumeric;

import com.jgoodies.binding.beans.ExtendedPropertyChangeSupport;

/*
 * TODO cache results
 * TODO hook this as with the Find tool in the QueryBrowser
 * TODO use it as an editor for defining ranges/criteria
 */
public class CrossViewPropertyMetric extends AbstractTableModel {

	protected ListModel propertyValue;
	protected ListModel metric;
	protected FilteredCountNominal queryNominal = new FilteredCountNominal();
	protected FilteredCountNumeric queryNumeric = new FilteredCountNumeric();
	protected QueryExecutor<FilteredCount> exec;
	private ExtendedPropertyChangeSupport changeSupport;


	/**
	 * 
	 */
	private static final long serialVersionUID = 3510156202915550926L;

    public final synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (changeSupport == null) {
			changeSupport = new ExtendedPropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(listener);
	}
    
    public final synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
		if (listener == null || changeSupport == null) {
		return;
		}
		changeSupport.removePropertyChangeListener(listener);
	}    
    public final synchronized void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (changeSupport == null) {
			changeSupport = new ExtendedPropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}    
    public final synchronized void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
		if (listener == null || changeSupport == null) {
			return;
		}
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}    
	public Property getProperty() {
		return queryNominal.getProperty();
	}

	public void setProperty(Property property) {
		queryNominal.setProperty(property);
		queryNumeric.setProperty(property);
		fireTableStructureChanged();
	}
	
	public int getColumnCount() {
		return metric==null?0:metric.getSize()+1;
	}

	public int getRowCount() {
		return propertyValue==null?0:propertyValue.getSize();
	}
	public IStoredQuery getQuery() {
		return queryNominal.getQuery();
	}

	public void setQuery(IStoredQuery query) {
		queryNominal.setQuery(query);
		queryNumeric.setQuery(query);
		fireTableStructureChanged();
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			if (exec == null) return "";
			if (getProperty() == null) return "";
			Object p = propertyValue.getElementAt(rowIndex);
			if (columnIndex == 0) return p;
	
			FilteredCount q = null;
	
			if (p instanceof Range) {
				Range range = (Range)p;
				if (range.getMinValue() instanceof Number) 
					q = queryNumeric;
				else 	q = queryNominal;
				q.setValue(range);
			} else {
				q = queryNominal;
				queryNominal.setValue(new Range(p.toString()));
			}
			if (q == null) return "";
			q.setMetric((Range<Double>)metric.getElementAt(columnIndex-1)); 
			q.setQuery(getQuery());
			try {
				PropertyStats result = null;
				ResultSet rs = exec.process(q);
				while (rs.next()) {
					result = q.getObject(rs);
					break;
				}
				exec.closeResults(rs);
				if (result.getCount() == 0) return "";

				else return result;
			} catch (Exception x) {
				x.printStackTrace();
				return x.getMessage();
			}
		} catch (Exception x) {
			return x.getMessage();			
		}
	}

	public ListModel getRows() {
		return propertyValue;
	}

	public void setRows(ListModel rows) {
		this.propertyValue = rows;
		fireTableStructureChanged();
	}

	public ListModel getColumns() {
		return metric;
	}

	public void setColumns(ListModel columns) {
		this.metric = columns;
		fireTableStructureChanged();
	}
	public QueryExecutor<FilteredCount> getExec() {
		return exec;
	}

	public void setExec(QueryExecutor<FilteredCount> exec) {
		this.exec = exec;
		exec.setCache(true);
		fireTableStructureChanged();
	}
	@Override
	public String getColumnName(int column) {
		if (column > 0)
			return metric.getElementAt(column-1).toString();
		else return "Metric-->";
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i=0; i < getColumnCount();i++) { 
			b.append(getColumnName(i));
			b.append('\t');
		}
		for (int i=0; i < getRowCount();i++) {
			b.append("\n");
			for (int j=0; j < getColumnCount();j++) {
				b.append(getValueAt(i,j));
				b.append('\t');
			}
		}
		return b.toString();
	}

}
