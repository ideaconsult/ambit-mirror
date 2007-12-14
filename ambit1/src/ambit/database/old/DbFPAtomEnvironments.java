/**
 * Created on 2005-6-16

 * @author Nina Jeliazkova nina@acad.bg
 *
 * Project : ambit
 * Package : ambit.database.old
 * Filename: DbFPAtomEnvironments.java
 */
package ambit.database.old;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.descriptors.AtomEnvironment;
import ambit.data.descriptors.AtomEnvironmentDescriptor;
import ambit.database.DbConnection;
import ambit.exceptions.AmbitException;

/**
 * TODO Add DbFPAtomEnvironments description
 * @author Nina Jeliazkova <br>
 * @version 0.1, 2005-6-16
 * @deprecated Use {@link ambit.processors.structure.AtomEnvironmentGenerator}
 */
public class DbFPAtomEnvironments extends DbSubstanceIterator implements IDBMolAction {
    protected HydrogenAdder hAdder = null;
	protected AtomEnvironmentDescriptor aeDescriptor = null;
	protected Object[] aeParams = null;
	protected final String AMBIT_insertFPAE = 
		"insert ignore into fpae (ae,atom) values(?,?);"; 
	protected final String AMBIT_insertFPAEID = 
		"insert ignore into fpaeid (idsubstance,idfpae,time_elapsed,status) select ?,idfpae,?,? from fpae where ae=?;";
	protected PreparedStatement psFPSubstance = null;
	protected PreparedStatement psFPAE = null;
	
	protected int[] aeResult = null;
	/**
	 * 
	 * Constructor
	 * @param conn
	 */
	public DbFPAtomEnvironments(DbConnection conn)  {
		super(conn);
	}			
	public void initializeInsert() throws SQLException {
		psFPAE = conn.prepareStatement(AMBIT_insertFPAE);		
		psFPSubstance = conn.prepareStatement(AMBIT_insertFPAEID);
	}
	
	public void close() throws SQLException {
		super.close();
		if (psFPSubstance != null) psFPSubstance.close(); psFPSubstance = null;
		if (psFPAE != null) psFPAE.close(); psFPAE = null;		
	}

	

	public int insertFPAE(int idsubstance, IMolecule mol) throws AmbitException {
	    if ((mol == null) || (mol.getAtomCount() == 0)) 
	        throw new AmbitException("Empty molecule");
	    /*
	    if (hAdder == null) hAdder = new HydrogenAdder();
	    try {
	        hAdder.addExplicitHydrogensToSatisfyValency(mol);
	    } catch (Exception x) {
	        return onError(idsubstance, mol, x);        
	    }
		*/
	    try {
		    MFAnalyser mfa = new MFAnalyser(mol);
		    //mfa.removeHydrogensPreserveMultiplyBonded();
		    
			if (aeDescriptor == null) {
				aeDescriptor = new AtomEnvironmentDescriptor();
				aeParams = new Object[1];
			}
			long time_elapsed;
			int status = 1;
			String ae, atomS;
			int rows = 0;
			
			    
			for (int a = 0; a < mol.getAtomCount(); a++) {
			    //calculation
			    try {
					time_elapsed = System.currentTimeMillis();
					aeParams[0] = new Integer(a);
					aeDescriptor.setParameters(aeParams);
					
					if (aeResult == null)
					    aeResult = new int[aeDescriptor.getAtomFingerprintSize()];
					
					aeDescriptor.doCalculation(mol,aeResult);
					time_elapsed = System.currentTimeMillis() - time_elapsed;
					status = 1;
					//result formatting
					ae = AtomEnvironment.atomFingerprintToString(aeResult,'\t');
					atomS = aeDescriptor.atomTypeToString(aeResult[1]);
				
			    } catch (CDKException x) {
			        time_elapsed = 0;
			        status = 3;
			        ae = x.getMessage();
			        atomS = "Error";
			    }
				
				//inserting
				if ((psFPSubstance == null) || (psFPAE == null)) initializeInsert();
				psFPAE.clearParameters();
				psFPAE.setString(1,ae);
				psFPAE.setString(2,atomS);
				psFPAE.executeUpdate();
				
				psFPSubstance.clearParameters();
				psFPSubstance.setInt(1,idsubstance);
				psFPSubstance.setLong(2,time_elapsed);
				psFPSubstance.setInt(3,status);
				psFPSubstance.setString(4,ae);
				psFPSubstance.executeUpdate();
				rows++;
			}	
			return rows;
	    } catch (SQLException x) {
	    	throw new AmbitException(x);
	    }
	}
	
	/* (non-Javadoc)
     * @see ambit.database.IDBMolAction#doAction(int, org.openscience.cdk.Molecule)
     */
    public int doAction(int idsubstance, Molecule mol) throws AmbitException {
        return insertFPAE(idsubstance,mol);
    }
    /* (non-Javadoc)
     * @see ambit.database.IDBMolAction#onError(int, org.openscience.cdk.Molecule, java.lang.Exception)
     */
    public int onError(int idsubstance, Molecule mol, Exception error) throws AmbitException {
		//inserting
        String msg = "error";
        if (error != null)  {
            error.printStackTrace();
            msg = error.getMessage();
        }
         
        try {
			if ((psFPSubstance == null) || (psFPAE == null)) initializeInsert();
			psFPAE.clearParameters();
			psFPAE.setString(1,msg);
			psFPAE.setString(2,"Error");
			psFPAE.executeUpdate();
			
			psFPSubstance.clearParameters();
			psFPSubstance.setInt(1,idsubstance);
			psFPSubstance.setLong(2,0);
			psFPSubstance.setInt(3,2); //error
			psFPSubstance.setString(4,msg);
			
			psFPSubstance.executeUpdate();
	        return 1;
        } catch (SQLException x) {
        	throw new  AmbitException(x);
        }
    }
    public void invalidateExisting() throws AmbitException {
    	try {
    		conn.setAutoCommit(true);
	    	Statement st = conn.createStatement();
	    	//st.execute("update fpaeid set status='invalid' where status='valid'");
	    	st.execute("truncate fpaeid;");
	    	st.execute("truncate fpae;");
	    	//conn.commit();
	    	st.close();
	    	
	    	
	    	st = null;
    	} catch (Exception x) {
    		throw new AmbitException(x);
    	}
    }
}
