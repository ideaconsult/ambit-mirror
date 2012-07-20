package ambit2.db;

import java.sql.CallableStatement;
import java.sql.SQLException;


public interface IStoredProcStatement {
	public void getStoredProcedureOutVars(CallableStatement statement) throws SQLException;
}
