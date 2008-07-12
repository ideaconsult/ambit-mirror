package ambit.database.search;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import ambit.data.molecule.SourceDataset;
import ambit.database.readers.DbStructureReader;
import ambit.exceptions.AmbitException;

/**
 * Reads structures from a specified database subset, defined by {@link ambit.data.molecule.SourceDataset}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbDatasetReader extends DbStructureReader {
	protected SourceDataset srcDataset = null;
	protected PreparedStatement ps = null;	
	protected static final String sql=
		"select structure.idstructure,idsubstance from structure,"+
		"\nstruc_dataset,src_dataset where src_dataset.name=? and src_dataset.id_srcdataset = struc_dataset.id_srcdataset"+
		"\nand structure.idstructure=struc_dataset.idstructure ";
	protected static final String sqlAll=
		"select structure.idstructure,idsubstance from structure";
		

	public DbDatasetReader(Connection connection,SourceDataset srcDataset, int page, int pagesize)  throws AmbitException {
		super();
		setPage(page);
		setPagesize(pagesize);
		ps = prepareSQLStatement(connection,srcDataset,page,pagesize);
		if (ps == null) throw new AmbitException("Error when preparing SQL statement!");
	
	}
	protected PreparedStatement prepareSQLStatement(Connection conn,
			SourceDataset srcDataset,  int page, int pagesize) throws AmbitException {
		boolean hasQuery = false;
		try {
			String theSQL = sql;
			if (srcDataset == null) theSQL = sqlAll;
			if (page >=0)
				ps = conn.prepareStatement(theSQL+" limit "+page2Limit());
			else 
				ps = conn.prepareStatement(theSQL);
			if (srcDataset != null)
				ps.setString(1,srcDataset.getName());
			setResultset(ps.executeQuery());
		} catch (SQLException x) {
				throw new AmbitException(x);
		}
			
		return ps;
	
	}
	

	public void close() throws IOException {
		super.close();
		if (ps == null) return;
		try {
			ps.close();
		} catch (SQLException x) {
			throw new IOException(x.getMessage());
			
		}
	}
	public String toString() {
		if (srcDataset == null) return super.toString();
		else return "Retrieve compounds from dataset "+ srcDataset.getReference().getName();
	}

}
