package ambit2.database.writers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.data.AmbitUser;
import ambit2.database.core.DbSQL;
import ambit2.database.exception.DbAmbitException;

/**
 * Looks for identical substances (by SMILES) and does the necessary database processing to eliminate duplicates
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class PackSubstancesWriter extends AbstractDbStructureWriter {
	
	public PackSubstancesWriter(Connection connection, AmbitUser user) {
		
		super(connection, user);
		try {
			connection.setAutoCommit(true);
		} catch (SQLException x) {
			logger.error(x);
		}
	}

	public void write(IChemObject object) throws CDKException {
		try {
			int idsubstance = getIdSubstance(object);
			if (idsubstance <= 0) throw new CDKException(AmbitCONSTANTS.AMBIT_IDSUBSTANCE + " not defined!");
			Object smiles = object.getProperty(AmbitCONSTANTS.SMILES);
			if (smiles == null) throw new CDKException(AmbitCONSTANTS.SMILES + " not defined!");
			
			if (ps == null) prepareStatement();
			ps.clearParameters();
			ps.setInt(1,idsubstance);
			ps.setString(2,smiles.toString());				
			ps.execute();
			
			if (!connection.getAutoCommit())
				connection.commit();
			
		} catch (SQLException x) {
			throw new CDKException(x.getMessage());			
		} catch (DbAmbitException x) {
			throw new CDKException(x.getMessage());
		}
	}
	public int write(int idstructure, IChemObject object) throws AmbitException {

		return 0;
	}

	protected void prepareStatement() throws SQLException {
		ps = connection.prepareStatement(DbSQL.AMBIT_packStructures);
	}
	public void close() throws IOException {
		try {
			Statement stmt = connection.createStatement();
		
			logger.info("delete duplicate substances");					
			stmt.execute(DbSQL.AMBIT_deleteOrphanSubstances);
			connection.commit();

			logger.info("delete duplicates (fingerprint)");					
			stmt.execute(DbSQL.AMBIT_deleteOrphanFingerprints);					
			connection.commit();
			stmt.close(); stmt = null;				
			//TODO atom environments, distances
		} catch (SQLException e) {
			e.printStackTrace();
		}
		


		super.close();
	}
}
