package ambit.database.writers;

import java.sql.Connection;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IChemObject;

import ambit.data.AmbitUser;
import ambit.exceptions.AmbitException;

/**
 * Writes the user that submitted the structure. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbStructureUserWriter extends AbstractDbStructureWriter {
	protected static final String AMBIT_insertStrucUser = "INSERT IGNORE INTO struc_user (idstructure,idambituser) VALUES (?,(select idambituser from ambituser where mysqluser=?))";
	public DbStructureUserWriter(Connection connection,AmbitUser user) {
		super(connection,user);
	}
	//TODO user name 
	public int write(int idstructure, IChemObject object) throws AmbitException {
		try {
			if (user == null) throw new AmbitException("Undefined user");
			prepareStatement();
			ps.clearParameters();
			ps.setInt(1,idstructure);
			ps.setString(2,user.getName());
			ps.executeUpdate();
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		return idstructure;
	}

	protected void prepareStatement() throws SQLException {
		if (ps == null)
			ps = connection.prepareStatement(AMBIT_insertStrucUser);
	}
	public String toString() {
		return "The compounds are stored to database by "+user.getName();
	}

}
