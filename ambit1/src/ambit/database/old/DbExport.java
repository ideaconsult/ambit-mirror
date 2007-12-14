/**
 * Created on 2004-11-5
 *
 */
package ambit.database.old;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.benchmark.Benchmark;
import ambit.data.molecule.Compound;
import ambit.database.DbConnection;
import ambit.database.core.DbSQL;
import ambit.exceptions.AmbitException;

/**
 * Database API <br>
 * Export compounds to a text file 
 * @author Nina Jeliazkova <br>
 * @deprecated
 * <b>Modified</b> 2005-4-7
 */
public class DbExport extends DbConnection {
	  
    private String filename = "DbExport.txt";
	/**
	 * 
	 */
	public DbExport(String database) {
		super(database);

	}
	public DbExport(String database,String filename) {
		super(database);
		this.filename = filename;
 
	}

	/**
	 * @param conn
	 */
	public DbExport(DbConnection conn) throws AmbitException {
		super(conn);

	}
	public DbExport(String host, String port, String database, String user, String password) {
		super(host,port, database,user,password);
	}
	/**
	 * @param conn 
	 */
	public DbExport(DbConnection conn, String filename) throws AmbitException  {
		super(conn);
		this.filename = filename;		

	}	
	/**
	 * @return {@link ResultSet}
	 */
	public ResultSet getSubstances(Statement stmt) throws SQLException {
	    ResultSet rs = stmt.executeQuery(DbSQL.AMBIT_Substances);	    
	    return rs;
}
	public ResultSet getStructures(Statement stmt) throws SQLException {
	    ResultSet rs = stmt.executeQuery(DbSQL.AMBIT_Structures);	    
	    return rs;
	}

	public ResultSet getStructures(Statement stmt, long limit) throws SQLException {
	    ResultSet rs = stmt.executeQuery(DbSQL.AMBIT_StructuresLimit(limit));	    
	    return rs;
	}
	
	public ResultSet getSubstancesID(Statement stmt, long limit) throws SQLException {
	    ResultSet rs = stmt.executeQuery(DbSQL.AMBIT_SubstancesIDLimit+limit + ";");
	    return rs;
	}
	public ResultSet getSubstancesID(Statement stmt) throws SQLException {
	    ResultSet rs = stmt.executeQuery(DbSQL.AMBIT_SubstancesID);	    
	    return rs;
	}


	public ResultSet getSubstancesIDByCB(Statement stmt, int lowCB, int highCB, long limit) throws SQLException {
	    ResultSet rs = stmt.executeQuery(DbSQL.AMBIT_SubstancesIDByCBLimit(lowCB,highCB,limit));	    
	    return rs;
	}
	public ResultSet getSubstancesIDByCB(Statement stmt,int lowCB, int highCB) throws SQLException {
	    //ResultSet rs = stmt.executeQuery(DbSql.Ambit_SubstancesID);	    
	    ResultSet rs = stmt.executeQuery(DbSQL.AMBIT_SubstancesIDByCB(lowCB,highCB));
	    			    
	    return rs;
	}
	
	public ResultSet getRecords(Statement stmt) throws SQLException {
	    ResultSet rs = stmt.executeQuery("select lri_simple.id,formula,uncompress(ctext) from lri_simple,lric where lric.id = lri_simple.id order by id;");
		    return rs;
	}

	public void printResultSet(ResultSet rs) {
		int c = 0;
		try {	
		//rs.first(); 			
		int columns = (rs.getMetaData()).getColumnCount();
		while (rs.next()) {
			for (int i = 0; i<columns; i++)		
				logger.debug("'"+rs.getString(i+1)+"'\t");					
			logger.debug("\n");
			c++;
			//if (c > 10) break;
		}
	
		} catch (Exception e) {
			System.out.println(e.getMessage());                		    						
			e.printStackTrace();
		}

	}
	
	
	public void verifyResultSet(ResultSet rs) {
		int c = 0;
		try {	
		//rs.first(); 			
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		int formulaIdx = 0; 
		int structureIdx = 0;
		int idx = 0;
		String formulaStr = "";
		
		
		for (int i = 0; i<columns; i++)	{
			if (rsmd.getColumnName(i+1).equals("formula")) formulaIdx = i+1;
			if (rsmd.getColumnName(i+1).equals("uncompress(structure)")) structureIdx = i+1;
			//if (rsmd.getColumnName(i+1).equals("ctext")) structureIdx = i+1;
			//if (rsmd.getColumnName(i+1).equals("name")) formulaIdx = i+1;
			//if (rsmd.getColumnName(i+1).equals("more")) structureIdx = i+1;
			
			if (rsmd.getColumnName(i+1).equals("id")) idx = i+1;			
			System.out.println("'"+rsmd.getColumnName(i+1)+"'");			
		}
		IMolecule mol = null;
		while (rs.next()) {
				formulaStr = "";

				try {
					mol = Compound.readMolecule(rs.getString(formulaIdx));					
		 		    MFAnalyser mfa = new MFAnalyser((IAtomContainer) mol);
		 			if ( !formulaStr.equals(mfa.getMolecularFormula())) {
		 				System.out.println("Error in " + formulaStr);
		 			}	
				} catch (CDKException e) {
					e.printStackTrace();
					System.out.println("Error in record ID= " +rs.getString(idx) + "'" + formulaStr);
					continue;
				}
			c++;
			if (c > 1000) break;			
					
		}
		
		
		} catch (Exception e) {
			System.out.println(e.getMessage());                		    						
			e.printStackTrace();
		}
		System.out.println(c + " compounds read.");

	}
	
	public void closeStatement(Statement stmt) throws SQLException {
		if (stmt != null) 
	 		stmt.close();
	 	stmt = null;
	}    	

	public void closeRecordset(ResultSet rs) throws SQLException {
		if (rs != null)	rs.close();
 		rs = null;
 	}    	

	
	public static void main(String[] args) {
		
		  System.out.println(new Date().toString());	
	      DbExport dbexport = new DbExport("export.txt");	
      	      
	      try {
		      dbexport.open();
	      	
		  Statement stmt = dbexport.createUnidirectional();
	      ResultSet rs = dbexport.getRecords(stmt);
	      //dbexport.printResultSet(rs);
	      Benchmark b = new Benchmark();
	      b.mark();
	      dbexport.verifyResultSet(rs);
	      b.record();
	      b.report();
	      dbexport.closeRecordset(rs);	      
	      dbexport.closeStatement(stmt);	      
	      dbexport.close();
	      } catch (AmbitException x) {
	    		x.printStackTrace();
	      } catch (SQLException e) {
	      	e.printStackTrace();
	      }
	      
	      
	}
}
