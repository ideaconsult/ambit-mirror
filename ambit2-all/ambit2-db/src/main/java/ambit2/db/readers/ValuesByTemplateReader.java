package ambit2.db.readers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryExecutor;

/**
 * Reads set of fields and values given template name 
 * @author nina
 *
 */
public abstract class ValuesByTemplateReader<Result> extends AbstractDBProcessor<IStructureRecord,Result> {
	protected IQueryRetrieval<Property> propertyQuery;
	public IQueryRetrieval<Property> getPropertyQuery() {
		return propertyQuery;
	}
	public void setPropertyQuery(IQueryRetrieval<Property> propertyQuery) {
		this.propertyQuery = propertyQuery;
	}
	protected RetrieveField names;
	protected QueryExecutor executort;
	protected QueryExecutor executorn;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8369720672395084533L;

	public ValuesByTemplateReader() {
		names = new RetrieveField();
		executort = new QueryExecutor();
		executorn = new QueryExecutor();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		executort.setConnection(connection);
		executort.open();
		executorn.setConnection(connection);
		executorn.open();		
	}
	public Result  process(IStructureRecord target) throws AmbitException {
		if ((target.getIdchemical()<=0) && (target.getIdstructure()<=0))
			throw new AmbitException("Structure not defined!");
		Result book = null;
				
		ResultSet rs=null;
		try {
			rs = executort.process(propertyQuery);
			while (rs.next()) {
				Property p = propertyQuery.getObject(rs);
				
				names.setFieldname(p);
				names.setValue(target);

				ResultSet values = null;
				try {
					values = executorn.process(names);
					while (values.next()) {
						Object value = names.getObject(values);
						if (book == null)
							book = createResult(target);
						set(book,p, value);
					}
					
				} catch (Exception x) {
					logger.log(Level.WARNING,x.getMessage(),x);
				} finally {
					try {executorn.closeResults(values);} catch (Exception x) {}
				}
			}
		} catch (SQLException x) {
			
		} finally  {
			try {executort.closeResults(rs);} catch (Exception x) {};
			
		}
		return book;
	}
	protected abstract Result createResult(IStructureRecord target) throws AmbitException ;
	protected abstract void set(Result result, Property fieldname, Object value) throws AmbitException;
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}


}
