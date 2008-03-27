package ambit2.database.search;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.database.DbConnection;
import ambit2.exceptions.AmbitException;
import ambit2.data.molecule.SourceDataset;
import ambit2.database.query.ExperimentQuery;
import ambit2.database.query.TemplateFieldQuery;
import ambit2.database.readers.DbStructureReader;

/**
 * Search for experimental data. The query is specified in  {@link ambit2.database.query.ExperimentQuery}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbSearchExperiments extends DbStructureReader {
	protected ExperimentQuery experiments;
	protected Statement stmt;
	/**
	 * 
	 * @param connection
	 * @param experiments  The query {@link ExperimentQuery}
	 * @param srcDataset The dataset to search . If null, searches entire database.
	 * @param page
	 * @param pagesize
	 * @throws AmbitException
	 */
	/*
	public DbSearchExperiments(DbConnection connection, ExperimentQuery experiments,
			SourceDataset srcDataset, int page, int pagesize) throws AmbitException {
		super();
		openResultSet(connection, experiments,srcDataset,page,pagesize);
	}
	*/

	public DbSearchExperiments(Connection connection, ExperimentQuery experiments,
			SourceDataset srcDataset, int page, int pagesize) throws AmbitException {
		super();
		openResultSet(connection, experiments,srcDataset,page,pagesize);
	}	
	public DbSearchExperiments(ResultSet resultset) {
		super(resultset);
	}

	public DbSearchExperiments(Connection connection, String sql)
			throws AmbitException {
		super(connection, sql);
		
	}
	protected void openResultSet(DbConnection connection, ExperimentQuery experiments,
			SourceDataset srcDataset, int page, int pagesize) throws AmbitException {
		openResultSet(connection.getConn(),experiments,srcDataset,page,pagesize);
	}
	protected void openResultSet(Connection connection, ExperimentQuery experiments,
			SourceDataset srcDataset, int page, int pagesize) throws AmbitException {
		setPage(page);
		setPagesize(pagesize);
		this.experiments = experiments;
		if ((experiments==null) || (experiments.size()==0)) throw new AmbitException("Empty query!");

		try {
			stmt = connection.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			String sql = experiments.toSQL(srcDataset,page,pagesize);
			if (sql.equals("")) throw new AmbitException("Empty query");
			System.out.println(sql);
			setResultset(stmt.executeQuery(sql));
		} catch (SQLException x) {
			resultset = null;
			stmt = null;
			throw new AmbitException(x);
		}
	}
	public Object next() {
		Object o = super.next();
		
		if (o instanceof IChemObject) {
			
			int k = 7;
			try {
			for (int i=0; i < experiments.size();i++) {
				TemplateFieldQuery q = experiments.getFieldQuery(i);
				if (q.isEnabled() ) {
					Object p = resultset.getString(k);
					if (p != null)
						((IChemObject) o).setProperty(q,p);
					k++;					
				}			
			}	
			} catch (SQLException x) {
				//x.printStackTrace();
			}
			
		}
		return o;
	}
	public void close() throws IOException {
		super.close();
		try {
			if (stmt != null) stmt.close();
		} catch (Exception x) {
			
		}
	}


}
