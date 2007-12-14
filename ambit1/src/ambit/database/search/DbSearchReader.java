package ambit.database.search;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.molecule.SourceDataset;
import ambit.database.AmbitID;
import ambit.database.readers.DbReader;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;

/**
*  Abstract class to search structures from database. Expects
*  <pre>
ResultSet resultset; 
String cas = resultset.getString("casno");  CAS registry number, optional 
String formula = resultset.getString("formula");  Chemical formula, optional 
String idstructure = resultset.getString("idstructure"); unique structure identifier , mandatory
String idsubstance = resultset.getString("idsubstance"); unique substance identifier , optional 
</pre>
 * Note that most descendant of this class do not have "ustructure" field and therefore do not return complete structure.
 * This is by design and for efficient memory management. If the structure is necessary for further processing, pass the returned object through 
 * {@link ambit.database.processors.ReadStructureProcessor}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public abstract class DbSearchReader extends DbReader {
	protected SourceDataset srcDataset = null;
	protected PreparedStatement ps = null;
	protected IMolecule query = null;
    protected double threshold = 0.50;
    protected String similarityLabel = "Similarity";

	public DbSearchReader(Connection connection, IAtomContainer mol, SourceDataset srcDataset, 
                double threshold, int page, int pagesize)  throws AmbitException {
			super();
			this.srcDataset = srcDataset;
            setThreshold(threshold);
			setPage(page);
			setPagesize(pagesize);
			//if ((mol==null) || (mol.getAtomCount()==0)) throw new AmbitException("Empty query!");
			if (mol==null) throw new AmbitException("Empty query!");
			try {
			query = (IMolecule)mol.clone();
			} catch (CloneNotSupportedException x) {
			    throw new AmbitException(x);
			}
			MFAnalyser mfa = new MFAnalyser(query);
		    mfa.removeHydrogensPreserveMultiplyBonded();
			ps = prepareSQLStatement(connection,mol,page,pagesize, threshold);
			if (ps == null) throw new AmbitException("Error when preparing SQL statement!");
			try {
				setResultset(ps.executeQuery());
			} catch (SQLException x) {
				resultset = null;
				ps = null;
				throw new AmbitException(x);
			}
	}

	protected abstract PreparedStatement prepareSQLStatement(Connection conn, IAtomContainer mol, int page, int pagesize, double threshold) throws AmbitException;
	/* (non-Javadoc)
     * @see ambit.database.DbReader#close()
     */
    public void close() throws IOException {
        try {
            if (ps != null) ps.close();
            ps = null;
        } catch (Exception x) {
            
        }
   
        super.close();
    }

    public Double getSimilarity() throws Exception {
        return resultset.getDouble("similarity");
    }        
    public Object next() {
     
        if (readIDOnly)  {
            try {
                Double similarity = getSimilarity();
                if (similarity != null) {
                    //if (!accept(similarity)) return null;
                    return new AmbitID(getSubstance(),-1);
                } else return null;
                
            } catch (Exception x) {
                return null;
            }    
        } else return nextFat();
    }
    
    
    protected Object nextFat() {
        try { 
            IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
            String cml = "";
           try {
               Double similarity = getSimilarity();
               if (similarity != null) {
                   if (!accept(similarity)) return null;
                   mol.setProperty(similarityLabel,similarity);
               }
               
           } catch (SQLException x) {
               
           }    
 	       setPropertyStructure(mol);
           setPropertySubstance(mol);
       
 	       return mol;
        } catch (Exception x) {
            logger.error(x);
            return null;
        }
     }
    
    protected boolean accept(Double similarity) {
        return true;
    }


    public synchronized double getThreshold() {
        return threshold;
    }

    public synchronized void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public synchronized String getSimilarityLabel() {
        return similarityLabel;
    }

    public synchronized void setSimilarityLabel(String similarityLabel) {
        this.similarityLabel = similarityLabel;
    }


}
