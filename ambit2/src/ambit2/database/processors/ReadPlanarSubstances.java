package ambit2.database.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.MoleculeTools;

/**
 * Reads only planar structures from the database. This is achieved by filtering compounds with {@link ambit2.data.descriptors.SpherosityDescriptor} < 0.2
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ReadPlanarSubstances extends ReadStructureProcessor {
    /**
     * @param connection
     */
    public ReadPlanarSubstances(Connection connection) throws AmbitException {
        super(connection);
        queryID = AmbitCONSTANTS.AMBIT_IDSUBSTANCE;
    	
    }
    public String getSQL() {
    	String sql = 
    	    "select structure.idstructure,idsubstance,value,uncompress(structure) as ustructure from ddictionary "+
    	    "\nleft join dvalues using(iddescriptor) join structure using(idstructure) "+
    	    "\nwhere structure.idsubstance =? and name=\"ambit2.data.descriptors.SpherosityDescriptor\" and value <=0.2";
    	System.out.println(sql);
    	return sql;
    
    	
    }    
    public Object process(Object object) throws AmbitException {
        if (ps == null) return object;
        if (object instanceof IMolecule)
        try { 
            IMolecule mol = (IMolecule) object;
            Object id = mol.getProperty(queryID);
            if (id == null) return object;
            int idstructure = -1;
            try {
                idstructure = Integer.parseInt(id.toString());
                if (idstructure <= 0) return object;
            } catch (Exception x) {
                return object;
            }
            ps.clearParameters();
            ps.setInt(1,idstructure);
            ResultSet resultset = ps.executeQuery();
            int n=0;;
            while (resultset.next()) {
            	n++;
                try {
 	 		       String cml = resultset.getString("ustructure");
 	 		       IMolecule newMol = MoleculeTools.readCMLMolecule(cml);
 	 		       newMol.setProperties(mol.getProperties());
 	 		       mol = newMol;
 	 	       } catch (SQLException x) {
 	 	           
 	 	       }            	
	 	       try {
	 		       String idsubstance = resultset.getString("idsubstance");
	 		       if (idsubstance != null) 
	 		           mol.setProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE,new Integer(idsubstance));
	 	       } catch (SQLException x) {
	 	           
	 	       }	     
	 	       try {
	 		       String idsubstance = resultset.getString("idstructure");
	 		       if (idsubstance != null) 
	 		           mol.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,new Integer(idsubstance));
	 	       } catch (SQLException x) {
	 	           
	 	       }		 
	 	       try {
	 		       double d = resultset.getDouble("value");
	 		       
	 		       mol.setProperty("Spherosity",Double.toString(d));
	 	       } catch (SQLException x) {
	 	           
	 	       }		 	       
            }
            if (n > 0)
            	return mol;
            else return null;
        } catch (Exception x) {
            //logger.error(x);
            x.printStackTrace();
            return null;
        }
        return object;
    }

}
