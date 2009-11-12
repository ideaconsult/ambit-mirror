package ambit2.db.results;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import ambit2.base.data.TypedListModel;

public class MemoryRowsModel<T> extends AbstractListModel implements TypedListModel<T>, PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5034323147503404064L;
	protected AmbitRows<T> rows;
	protected List<T> content;
	public MemoryRowsModel(AmbitRows<T> rows) {
		this.rows = rows;
		content = new ArrayList<T>();

		rows.addPropertyChangeListener(this);
	}
	protected void rows2List() {
		content.clear();
		try {
			rows.first();
			while (rows.next()) {
				content.add(rows.getObject());
			}
		} catch (Exception x) {
			
		}
	}
	public T getElementAt(int index) {
		try {
			return content.get(index);
		}catch (Exception x) {
			return null;
		}
	}

	public int getSize() {
		return content.size();
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("status")) {
			rows2List();
			fireContentsChanged(this,0,content.size());

		}
		
	}
/*
	public boolean hasNext() {
		try {
			rows.next();
			return !rows.isAfterLast();
		} catch (SQLException x) {
			return false;
		}		 
	}

	public T next() {
		try {
			return rows.getObject();
		} catch (AmbitException x) {
			return null;
		}
	}
*/
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	
}