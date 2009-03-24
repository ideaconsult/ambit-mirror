package ambit2.db.readers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.TemplateQuery;

/**
 * Reads set of fields and values given template name 
 * @author nina
 *
 */
public abstract class ValuesByTemplateReader<Result> extends AbstractDBProcessor<IStructureRecord,Result> {
	protected TemplateQuery template;
	protected RetrieveField names;
	protected QueryExecutor executort;
	protected QueryExecutor executorn;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8369720672395084533L;

	public ValuesByTemplateReader() {
		template= new TemplateQuery();
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
				
		template.setValue(getTemplateName());
		ResultSet rs=null;
		try {
			rs = executort.process(template);
			while (rs.next()) {
				Property p = template.getObject(rs);
				
				names.setFieldname(p.getName());
				names.setValue(target);

				ResultSet values = null;
				try {
					values = executorn.process(names);
					while (values.next()) {
						String value = names.getObject(values);
						if (book == null)
							book = createResult();
						set(book,p.getName(), value);
					}
					
				} catch (Exception x) {
					x.printStackTrace();
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
	protected abstract Result createResult() throws AmbitException ;
	protected abstract String getTemplateName();
	protected abstract void set(Result result, String fieldname, String value) throws AmbitException;
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}


}
