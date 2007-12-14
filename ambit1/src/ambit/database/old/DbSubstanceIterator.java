/**
 * <b>Filename</b> DbSubstanceIterator.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-16
 * <b>Project</b> ambit
 */
package ambit.database.old;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.molecule.Compound;
import ambit.database.DbConnection;
import ambit.database.DbCore;
import ambit.database.exception.DbAmbitException;
import ambit.exceptions.AmbitException;

/**
 * @deprecated
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-16
 */
public class DbSubstanceIterator extends DbCore implements Runnable, IDBSubstanceIterator, IDBMolAction {
    protected static final String AMBIT_StrucBySubstance = "SELECT idstructure,uncompress(structure),type_structure from structure where idsubstance=? order by type_structure desc limit 1;";
    protected int rowsProcessed = 0;
    protected int rowsError = 0;
    protected String sqlSubstances = "SELECT idsubstance from substance limit 1000";
    protected int limit = 1000;
    protected IDBMolAction dbAction = null;
    protected int maxRows = 1000000;
    /**
     * @param conn
     */
    public DbSubstanceIterator(DbConnection conn) {
        super(conn);
        
    }
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
	    rowsProcessed = 0;
	    rowsError = 0;
	    if (dbAction == null) dbAction = this;
	    try {
	    conn.setAutoCommit(false);

	    int r = 1;
		while ( rowsProcessed <= maxRows)
		    try {
		         r = iterate(sqlSubstances,limit,dbAction);
		         rowsProcessed += r;
		         if (rowsProcessed == 0) break;
		    } catch (DbAmbitException x) {
		        x.printStackTrace();
		    }
		    
		conn.setAutoCommit(true);
	    } catch (SQLException x) {
	        x.printStackTrace();
	    }

		    
	}
    /* (non-Javadoc)
     * @see ambit.database.IDBSubstanceIterator#iterate(java.lang.String, long, ambit.database.IDBMolAction)
     */
    public int iterate(String sqlSubstances, long limit, 
            IDBMolAction action)
            throws DbAmbitException {
        //init
        int rows = 0;
        int errorRows = 0;
        int rowsToCommit = 0;
        
        if (stmt == null) initialize();
        if (ps == null) initializePrepared(AMBIT_StrucBySubstance);
        //get substances
        try {
	        ResultSet rsSubstances = stmt.executeQuery(sqlSubstances);
	        ResultSet rsStructure = null;		
	        int idsubstance;
	        IMolecule mol = null;
	        long base_time = System.currentTimeMillis();
	        int r = 0;
        
            //iterate over substances
    		while (rsSubstances.next()) {
    			
    			idsubstance = rsSubstances.getInt(1);
    			ps.clearParameters();
    			ps.setInt(1,idsubstance);
    			
    			rsStructure = ps.executeQuery();
    			//getStructure by idsubstance
    			while (rsStructure.next()) {
    				try {
    					//System.out.println(idsubstance);
    					mol = Compound.readMolecule(rsStructure.getString(2));
   					    r = action.doAction(idsubstance,mol);
    					rows += r;
    					rowsToCommit += r;
    				} catch (Exception x) {
   				        r = action.onError(idsubstance,mol,x);
    				    errorRows += r;
    				    rowsToCommit += r;
    				}
    			} //structures
    			rsStructure.close();
    			rsStructure = null;
    			if (((System.currentTimeMillis() - base_time ) > 300000)
    			        && (rowsToCommit > 0)) {
    				System.out.println(rows+"\t");					
    				getConn().commit();
    				base_time = System.currentTimeMillis();
    				rowsToCommit = 0;
    			}    		
    		} //substances
    		if (rowsToCommit > 0) getConn().commit();
    		
        } catch (Exception x) {
            throw new DbAmbitException(null,"DbSubstanceIterator.iterate",x);
        }
        return rows;
    }
    /* (non-Javadoc)
     * @see ambit.database.IDBMolAction#doAction(int, org.openscience.cdk.Molecule)
     */
    public int doAction(int idsubstance, IMolecule mol) throws AmbitException {
        System.out.println(idsubstance);
        //System.out.print('\t');
        //System.out.println(mol);
        return 1;
    }
    /* (non-Javadoc)
     * @see ambit.database.IDBMolAction#onError(int, org.openscience.cdk.Molecule, java.lang.Exception)
     */
    public int onError(int idsubstance, IMolecule mol, Exception error)
            throws AmbitException {
        System.err.println(idsubstance);
        //System.err.print('\t');
        //System.err.println(mol);
        return 1;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public String getSqlSubstances() {
        return sqlSubstances;
    }
    public void setSqlSubstances(String sqlSubstances) {
        this.sqlSubstances = sqlSubstances;
    }
    public IDBMolAction getAction() {
        return dbAction;
    }
    public void setAction(IDBMolAction action) {
        this.dbAction = action;
    }
    public int getMaxRows() {
        return maxRows;
    }
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
    public int getRowsError() {
        return rowsError;
    }
    public int getRowsProcessed() {
        return rowsProcessed;
    }
}

