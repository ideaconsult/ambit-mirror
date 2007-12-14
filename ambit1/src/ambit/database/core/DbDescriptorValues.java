/**
 * Created on 2005-3-18
 *
 */
package ambit.database.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import ambit.database.DbConnection;
import ambit.database.DbCore;
import ambit.database.exception.DbAmbitException;
import ambit.database.exception.DbDescriptorException;



/**
 * Database API<br>
 * Reads , writes and looks for descriptor values into the database  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DbDescriptorValues extends DbCore {
	/**
	 * 
	 */
	
	public DbDescriptorValues(DbConnection conn)  {
		super(conn);
	}			
	public void initializeInsert() throws DbAmbitException {
		initializePrepared("insert into dvalues (iddescriptor,idstructure,value,error,status) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE value=?,error=?;");
	}

	public void addNullValue(int iddescriptor, int idstructure, double value, double error) throws DbAmbitException {
		if (ps == null) initializeInsert();
		try {
			ps.clearParameters();
			ps.setInt(1,iddescriptor);
			ps.setInt(2,idstructure);
			ps.setNull(3,Types.DOUBLE);
			ps.setNull(4,Types.DOUBLE);
			ps.setNull(6,Types.DOUBLE);
			ps.setNull(7,Types.DOUBLE);
			ps.setString(5,"ERROR");		
			ps.executeUpdate();
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"addValue",x);
		}
	}
	public void addValue(int iddescriptor, int idstructure, double value, double error) throws DbAmbitException {
		if (ps == null) initializeInsert();
		try {
			ps.clearParameters();
			ps.setInt(1,iddescriptor);
			ps.setInt(2,idstructure);
			ps.setDouble(3,value);
			ps.setDouble(4,error);
			ps.setDouble(6,value);
			ps.setDouble(7,error);
			ps.setString(5,"OK");		
			ps.executeUpdate();
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"addValue",x);
		}
	}
	public double getValue(int iddescriptor, int idstructure) throws DbDescriptorException {
		double d=0;
		try {
		ResultSet rs = stmt.executeQuery("SELECT value,error FROM dvalues WHERE iddescriptor="+iddescriptor + " and idstructure=" + idstructure + ";");
		while (rs.next()) {
			d = rs.getDouble(1);
			break;
		}
		//TODO if not found
		return d;
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"getValue",x);
		}
	}
	public double deleteValue(int iddescriptor, int idstructure) throws DbDescriptorException {
		try {
		return stmt.executeUpdate("DELETE FROM dvalues WHERE iddescriptor="+iddescriptor + " and idstructure=" + idstructure + ");");
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"deleteValue",x);
		}
	}
	public double deleteValuesPerDescriptor(int iddescriptor) throws DbDescriptorException {
		try {
		return stmt.executeUpdate("DELETE FROM dvalues WHERE iddescriptor="+iddescriptor + ");");
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"deleteValuesPerDescriptor",x);
		}
	}
	public double deleteValuesPerStructure(int idstructure) throws DbDescriptorException {
		try {
		return stmt.executeUpdate("DELETE FROM dvalues WHERE idstructure="+idstructure + ");");
		} catch (SQLException x) {
			throw new DbDescriptorException(null,"deleteValuesPerStructure",x);
		}
	}		
}
