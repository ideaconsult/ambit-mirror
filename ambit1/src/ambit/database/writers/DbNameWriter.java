package ambit.database.writers;

import java.sql.Connection;
import java.sql.SQLException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;

import ambit.data.AmbitUser;
import ambit.exceptions.AmbitException;
/**
 * Writes chemical names to database. The name is expected to be in object.getProperty(CDKConstants.NAME).
 * Use {@link ambit.processors.IdentifiersProcessor} to correct for possible different NAME captions.
 * Used in {@link ambit.database.writers.DbSubstanceWriter} 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbNameWriter extends AbstractDbStructureWriter {
	protected static final String AMBIT_insertName = "INSERT INTO name (idstructure,name) VALUES (?,?) ON DUPLICATE KEY UPDATE name=? ";
	public DbNameWriter(Connection connection,AmbitUser user) {
		super(connection,user);
	}
	public int write(int idstructure, IChemObject object) throws AmbitException {
		try {
			Object name = object.getProperty(CDKConstants.NAMES); 
			if (name != null) {
				String n = name.toString().trim(); 
				if (!n.equals("")) {
					prepareStatement();
					ps.clearParameters();
					ps.setInt(1,idstructure);
					ps.setString(2,n);
					ps.setString(3,n);
					ps.executeUpdate();
					records_written++;
					if ((records_written % 1000) == 0)
					    if (!connection.getAutoCommit())
					        connection.commit();
				} else logger.warn("Empty name "+n);
			} else  logger.info(CDKConstants.NAMES + " not defined.");
		} catch (Exception e) {
			logger.error(e);
			throw new AmbitException("",e);
		}
		return idstructure;
	}
	protected void prepareStatement() throws SQLException {
		if (ps == null)
			ps = connection.prepareStatement(AMBIT_insertName);
	}
	public String toString() {
		return "Write chemical name to database";
	}
}
