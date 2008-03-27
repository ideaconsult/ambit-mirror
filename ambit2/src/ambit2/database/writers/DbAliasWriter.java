package ambit2.database.writers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.exceptions.AmbitException;
import ambit2.data.AmbitUser;

/**
 * Writes many kind of identifiers into database (table alias). The list of aliases is set in the constructor. The values are expected to come as molecule properties.
 * Used in {@link ambit2.database.writers.DbSubstanceWriter}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbAliasWriter extends AbstractDbStructureWriter {
	protected ArrayList aliases;
	protected static final String AMBIT_insertAlias = "INSERT INTO alias (idstructure,alias,alias_type) VALUES (?,?,?) ON DUPLICATE KEY UPDATE alias=?";
	/**
	 * 
	 * @param connection
	 * @param user
	 * @param aliases The list of identifier names to write.
	 */
	public DbAliasWriter(Connection connection, AmbitUser user, ArrayList aliases) {
		super(connection,user);
		this.connection = connection;
		this.aliases = aliases;
	}
	public int write(int idstructure, IChemObject object) throws AmbitException {
		Object property;
		int count = 0;
		try {
			if (aliases == null) throw new AmbitException("Alias captions not defined!");
			for (int i = 0; i < aliases.size(); i ++) {
				property = object.getProperty(aliases.get(i));
				if (property != null) {
					prepareStatement();
					ps.clearParameters();
					ps.setInt(1,idstructure);
					ps.setString(2,property.toString());
					ps.setString(3,aliases.get(i).toString());
					ps.setString(4,property.toString());
					ps.executeUpdate();
					count++;
				}
			}
			records_written++;
			if ((records_written % 1000) == 0)
			    if (!connection.getAutoCommit())
			        connection.commit();
			if (count == 0) logger.warn("No aliases written!");
		} catch (Exception e) {
			logger.error(e);
			throw new  AmbitException(e);
		}
		return idstructure;
	}

	protected void prepareStatement() throws SQLException {
		if (ps == null)
			ps = connection.prepareStatement(AMBIT_insertAlias);
	}
	public ArrayList getAliases() {
		return aliases;
	}
	public void setAliases(ArrayList aliases) {
		this.aliases = aliases;
	}
	public String toString() {
		return "Write compound aliases to database";
	}
}
