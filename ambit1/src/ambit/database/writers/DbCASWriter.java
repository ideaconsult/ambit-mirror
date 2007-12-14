package ambit.database.writers;

import java.sql.Connection;
import java.sql.SQLException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.interfaces.IChemObject;

import ambit.data.AmbitUser;
import ambit.exceptions.AmbitException;

/**
 * Writes CAS number to database. The CAS number is expected to be in object.getProperty(CDKConstants.CASRN).
 * Use {@link ambit.processors.IdentifiersProcessor} to correct for possible different CAS captions.
 * Used in {@link ambit.database.writers.DbSubstanceWriter} 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbCASWriter extends AbstractDbStructureWriter {
	protected static final String AMBIT_insertCAS = "INSERT INTO cas (idstructure,casno) VALUES (?,?) ON DUPLICATE KEY UPDATE casno=? ";
	
	public DbCASWriter(Connection connection,AmbitUser user) {
		super(connection,user);
	}
	public int write(int idstructure, IChemObject object) throws AmbitException {
		try {
			Object cas = object.getProperty(CDKConstants.CASRN); 
			if (cas != null) {
				String c = cas.toString().trim();
				if (CASNumber.isValid(c)) {
					prepareStatement();
					ps.clearParameters();
					ps.setInt(1,idstructure);
					ps.setString(2,c);
					ps.setString(3,c);
					ps.executeUpdate();
					records_written++;
					if ((records_written % 1000) == 0)
					    if (!connection.getAutoCommit())
					        connection.commit();
				} else logger.warn("Invalid CAS RN "+c);
			} else logger.info(CDKConstants.CASRN+ " not defined.");
		} catch (Exception e) {
			logger.error(e);
			throw new AmbitException("",e);
		}
		return idstructure;
	}
	

	protected void prepareStatement() throws SQLException {
		if (ps == null)
			ps = connection.prepareStatement(AMBIT_insertCAS);
	}
	public String toString() {
		return "Write CAS registry numbers to database";
	}
}
