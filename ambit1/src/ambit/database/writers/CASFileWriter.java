package ambit.database.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.IChemObjectWriter;

import ambit.data.AmbitUser;
import ambit.exceptions.AmbitException;
import ambit.exceptions.AmbitIOException;
import ambit.io.FileOutputState;
import ambit.io.FileWithHeaderWriter;
import ambit.misc.AmbitCONSTANTS;

public class CASFileWriter extends AbstractDbStructureWriter {	
	protected static final String SELECT_CAS="select casno  from cas where idstructure=?";
	protected IChemObjectWriter fileWriter=null;
	
	public CASFileWriter(Connection connection, AmbitUser user, File file) throws AmbitIOException, FileNotFoundException, SQLException {
		super(connection,user);
		fileWriter = FileOutputState.getWriter(new FileOutputStream(file),file.getName());
		if (fileWriter instanceof FileWithHeaderWriter) {
			ArrayList<String> header = new ArrayList<String>();
			header.add(CDKConstants.CASRN);
			header.add(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
			((FileWithHeaderWriter)fileWriter).setHeader(header);
		}
		prepareStatement();
		
	}
	@Override
	public int write(int idstructure, IChemObject object) throws AmbitException {
		object.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,Integer.toString(idstructure));
		Object cas = object.getProperty(CDKConstants.CASRN);
		if (cas == null)
		try {
			ps.setInt(1,idstructure);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				cas = rs.getString(1);
				object.setProperty(CDKConstants.CASRN, cas);
			}
		
		} catch (Exception x) {
			logger.error(x);
		}
		try {
			fileWriter.write(object);
		} catch (CDKException x) {
			throw new AmbitException(x);
		}
		return idstructure;
	}

	@Override
	protected void prepareStatement() throws SQLException {
		ps = getConnection().prepareStatement(SELECT_CAS);
	}
	@Override
	public void close() throws IOException {
		try {
			if (ps != null) ps.close();
		} catch (Exception x) {
			logger.error(x);
		}
		try {
			if (fileWriter != null)
				fileWriter.close();		
			} catch (Exception x) {
				logger.error(x);
			}
		super.close();
	}

}
