package ambit2.workflow.ui;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JComponent;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.IDBProcessor;
import ambit2.db.SessionID;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.ui.editors.IAmbitEditor;

/**
 * This is a wrapper for {@link QueryResultsPanel}
 * @author nina
 *
 */
public class StoredQueryResultsPanel implements IAmbitEditor<IStoredQuery>,
		IDBProcessor<IStoredQuery, IStoredQuery> {
	protected QueryResultsPanel panel = new QueryResultsPanel();
	protected IStoredQuery query = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4180037205239150808L;

	public boolean confirm() {
		return panel.confirm();
	}

	public JComponent getJComponent() {
		return panel.getJComponent();
	}

	public IStoredQuery getObject() {
		return query;
	}

	public boolean isEditable() {
		return panel.isEditable();
	}

	public void setEditable(boolean editable) {
		panel.setEditable(editable);
		
	}

	public void setObject(IStoredQuery object) {
		try {
			panel.setObject(new QueryStoredResults(object));
			this.query = object;
		} catch (Exception x) {
			try {panel.setQuery(null);} catch (Exception xx) {x.printStackTrace();}
			this.query = null;
		}
	}

	public Connection getConnection() {
		return panel.getConnection();
	}

	public void setConnection(Connection connection) throws DbAmbitException {
		panel.setConnection(connection);
	}

	public long getID() {
		return panel.getID();
	}

	public boolean isEnabled() {
		return panel.isEnabled();
	}

	public IStoredQuery process(IStoredQuery target) throws AmbitException {
		return target;
	}

	public void setEnabled(boolean value) {
		panel.setEnabled(value);
		
	}

	public void close() throws SQLException {
		panel.close();
		
	}

	public SessionID getSession() {
		return panel.getSession();
	}

	public void open() throws DbAmbitException {
		panel.open();
		
	}

	public void setSession(SessionID session) {
		panel.setSession(session);
		
	}

}
