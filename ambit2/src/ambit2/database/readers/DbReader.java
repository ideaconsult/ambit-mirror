package ambit2.database.readers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.AmbitDatabaseFormat;
import ambit2.log.AmbitLogger;

/**
 * An abstract class for all database reader classes. A reader usually opens a 
 * {@link java.sql.ResultSet} in the constructor and then iterates the {@link java.sql.ResultSet}
 * by its {@link org.openscience.cdk.io.iterator.IIteratingChemObjectReader#next()} method.<br>
 * Use the reader by iterating its records:
 * 
 * <pre>
  IIteratingChemObjectReader reader ;
 //create the reader
  while (reader.hasNext()) {
  	Object compound = reader.next();
  	//This is usually a IAtomContainer object
  }
  reader.close(); // necessary, otherwise the recordset will stay open and waste resources
  </pre>
 * or by assigning the reader to a batch process {@link ambit2.io.batch.IBatch}, {@link ambit2.io.batch.DefaultBatchProcessing},
 * which does the iteration by itself.
 * TODO provide bidirectional iteration , when allowed by ResultSet.  
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 2, 2006
 */

public abstract class DbReader extends DefaultIteratingChemObjectReader {
	protected static AmbitLogger logger = new AmbitLogger(DbReader.class);
	protected ResultSet resultset = null ;
	protected Hashtable hashtable = null;
	protected ResultSetMetaData rsmd = null;
	protected int numCols=0;
	protected int page = 0;
	protected int pagesize = 100;
    protected boolean readIDOnly = false;
	
	public DbReader() {
		super();
	}
	public DbReader(ResultSet resultset) {
		super();
		setResultset(resultset);
		
	}
	public void setResultset(ResultSet resultset) {
		this.resultset = resultset;
		try {
			rsmd = resultset.getMetaData();
			numCols = rsmd.getColumnCount();

		} catch (SQLException x) {
			logger.error(x);
			numCols = 0;
		}
	}
	public IResourceFormat getFormat() {
        return new AmbitDatabaseFormat();
	}

	public void close() throws IOException {
		try {
			if (resultset != null)
				resultset.close();
		} catch (SQLException x) {
			logger.error(x);
			throw new IOException(x.getMessage());
		}

	}

	public boolean hasNext() {
		try {
			if (resultset != null) return resultset.next();
			else return false;
		} catch (SQLException x) {
			logger.error(x);
			return false;
		}
	}

	public String toString() {
		return "Reads structures from database";
	}
	public ResultSet getResultset() {
		return resultset;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public String page2Limit() {
		return Integer.toString(page*pagesize) + "," + Integer.toString(pagesize);
	}
    public synchronized boolean isReadIDOnly() {
        return readIDOnly;
    }
    public synchronized void setReadIDOnly(boolean readIDOnly) {
        this.readIDOnly = readIDOnly;
    }
    public int getStructure() throws Exception {
        return resultset.getInt("idstructure");
  }
  public int getSubstance() throws Exception {
      return resultset.getInt("idsubstance");
  }

  public String getSmiles() throws Exception {
      return resultset.getString("smiles");
  }
  public String getCAS() throws Exception {
      return resultset.getString("casno");
  }
  public String getName() throws Exception {
      return resultset.getString("name");
  }          
  public String getFormula() throws Exception {
      return resultset.getString("formula");
  }
  public String getMolWeight() throws Exception {
      return resultset.getString("molweight");
  }
  protected void setPropertySubstance(IAtomContainer mol) {
      try {
            mol.setProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE,new Integer(getSubstance()));
      } catch (Exception x) {
         logger.warn(x);
      }
  }
  protected void setPropertyStructure(IAtomContainer mol) {
      try {
            mol.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,new Integer(getStructure()));
         } catch (Exception x) {
         logger.warn(x);
         }
  }
  
}
