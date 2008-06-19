package ambit.database.writers;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;

import ambit.data.descriptors.DescriptorsHashtable;
import ambit.data.molecule.CompoundType;
import ambit.data.molecule.MoleculeTools;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.exceptions.AmbitException;
import ambit.misc.AmbitCONSTANTS;

/**
 * This is the main class to write molecules to the database. The class encapsulates writing 
 * structure, identifiers and properties.
 * Uses {@link ambit.database.writers.DbStructureWriter} to write structures<br>
 * Uses {@link ambit.database.writers.DbCASWriter} to write CAS numbers<br>
 * Uses {@link ambit.database.writers.DbNameWriter} to write names<br>
 * Uses {@link ambit.database.writers.DbAliasWriter} to write aliases<br>
 * Uses {@link ambit.database.writers.PropertyDescriptorWriter} to write descriptors<br>
 *<b>Example</b>:
 <pre>
 public void doInsertCompounds(String database,String fileName, int nread,int nwritten) {
 		DescriptorsHashtable descriptorLookup = new DescriptorsHashtable();
    	DbConnection dbconnection = null;
    	try {
    	    IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream(fileName),fileName);
    	    reader.addChemObjectIOListener(new AmbitSettingsListener(null,IOSetting.LOW) {
    	    	public void processIOSettingQuestion(IOSetting setting) {
    	    		super.processIOSettingQuestion(setting);
    	    		if (setting instanceof MolPropertiesIOSetting) {
    	    			descriptorLookup.putAll(((MolPropertiesIOSetting)setting).getProperties().getDescriptors());
    	    		}	
    	    	}
    	    });

    		dbconnection = new DbConnection("localhost","33060",database,"root","");
    		dbconnection.open();
			//This will be stored into database as data set origin, so prepare some meaningful entry :) 
			SourceDataset dataset = new SourceDataset(fileName,
					ReferenceFactory.createDatasetReference(fileName, "test"));
					
			//There could be many properties stored in the file, we are interested only of these as identifiers.		
			ArrayList aliases = new ArrayList();
			aliases.add("ChemName_IUPAC");
			aliases.add("INChI");
			aliases.add("NSC");
			aliases.add("ID");
			aliases.add("Code");
			aliases.add("KEGG");
			
			//Create the writer
			DbSubstanceWriter writer = new DbSubstanceWriter(dbconnection,dataset,aliases,descriptorLookup);
			
			//Now prepare processors to do some work before writing to database
			ProcessorsChain processors = new ProcessorsChain();
			//format identifiers
			processors.add(new IdentifiersProcessor());
			//CAS lookup if necessary
		    //processors.add(new CASSmilesLookup(dbconnection.getConn(),false));
		    //Generate unique SMILES fo be used by the FindUniqueProcessor
			processors.add(new SmilesGeneratorProcessor(5*60*1000));
			//Finally look if this compound is already stored in the database. If yes, the writer will write only the new info (e.g. new descriptors or another alias).
	    	processors.add(new FindUniqueProcessor(dbconnection.getConn()));
			
			//Create batch process
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer, 
					processors,
					new EmptyBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());
			//start it
			batch.start();
			
			//here the batch should be already completed.
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			assertEquals(nread, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(nwritten, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(nread, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(nread-nwritten, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));		
			
			
    	} catch (Exception x) {
    	    x.printStackTrace();
    		logger.error(x);
    		fail();
    	} finally {
    		try {
    		if (dbconnection != null) dbconnection.close();
    		} catch (SQLException e) {
    			
    		}
    	}
    }
 </pre>
 * See also {@link ambit.ui.actions.dbadmin.DbBatchImportAction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbSubstanceWriter extends AbstractDbStructureWriter {
	protected ArrayList aliases;
	protected DbConnection dbconnection = null ;
	protected DbStructureWriter structureWriter = null;
	protected SourceDatasetWriter datasetWriter = null;
	protected PropertyDescriptorWriter descriptorWriter = null;

	protected DbCASWriter casWriter = null;
	protected DbNameWriter nameWriter = null;
	protected DbAliasWriter aliasWriter = null;
	protected final static int idx_substance = 1;
	protected final static int idx_formula = 2;
	protected final static int idx_molweight = 3;
	protected final static int idx_smiles = 4;
	protected final static int idx_usmiles = 5;
	protected final static int idx_substtype = 6;
	protected final static int idx_time = 7;
	protected long count = 0;

	protected static final String AMBIT_insertSubstance = 
		"INSERT INTO substance (idsubstance,formula,molweight,smiles,usmiles,stype_id,time_elapsed) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE formula=?,molweight=?,smiles=?,usmiles=?,stype_id=?,time_elapsed=?";

	public DbSubstanceWriter(DbConnection dbconnection) {
		super(dbconnection.getConn(),dbconnection.getUser());
		this.dbconnection = dbconnection;
	}	
	/**
	 * 
	 * @param dbconnection
	 * @param dataset The dataset origin
	 * @param aliases  Identifier names to be used in {@link DbAliasWriter}
	 * @param descriptorLookup  Descriptors to be used in {@link PropertyDescriptorWriter}
	 */
	public DbSubstanceWriter(DbConnection dbconnection,SourceDataset dataset,ArrayList aliases,DescriptorsHashtable descriptorLookup) {
		this(dbconnection);
		this.aliases = aliases;
		if (dataset != null) {
			datasetWriter = new SourceDatasetWriter(dataset,dbconnection);
			//datasetWriter.setDefaultDataset(dataset);
		}
       	if (descriptorWriter == null) descriptorWriter = new PropertyDescriptorWriter(dbconnection,descriptorLookup);		
		try {
		    setKeyCheck(dbconnection.getConn(),false);
		    dbconnection.getConn().setAutoCommit(false);
		} catch (Exception x) {
		    logger.error(x);
		}
	}		
	public void write(IChemObject object) throws CDKException {
        try {
        	int idstructure = -1;
        	write(idstructure,object);
           	if (structureWriter == null) structureWriter = new DbStructureWriter(connection,dbconnection.getUser());
           	structureWriter.write(object); 
           	idstructure = getIdStructure(object);
           	if (idstructure > 0) {
	           	if (datasetWriter == null) datasetWriter = new SourceDatasetWriter(dbconnection);
	           	datasetWriter.write(idstructure,object);           	
	           	if (casWriter == null) casWriter = new DbCASWriter(connection,user);
	           	casWriter.write(idstructure,object);           	
	           	if (nameWriter == null) nameWriter = new DbNameWriter(connection,user);
	           	nameWriter.write(idstructure,object);           	
	           	if (aliasWriter == null) aliasWriter = new DbAliasWriter(connection,dbconnection.getUser(),aliases);
	           	aliasWriter.write(idstructure,object);
	           	descriptorWriter.write(idstructure,object);
           	}
           	count++;
           	if (!dbconnection.getConn().getAutoCommit() && ((count % 100)==0)) {
           	    
           	    dbconnection.getConn().commit();
           	}
           	
        } catch (SQLException x) {
        	logger.error(x);
        	throw new CDKException(x.getMessage());
          	
        } catch (AmbitException x) {
        	logger.error(x);
        	throw new CDKException(x.getMessage());
        }
	}	
	public int write(int idstructure, IChemObject object) throws AmbitException {
		if (object == null) throw new AmbitException("Null molecule");
		if (!(object instanceof IAtomContainer)) throw new AmbitException("Not a molecule");
       	int idsubstance = -1;
       	try {
       		idsubstance = getIdSubstance(object);
       	} catch (Exception x) {
       		idsubstance = -1;
       	}
       	idsubstance = writeSubstance(idsubstance,idstructure,(IAtomContainer)object);
       	return idstructure;
	}
	private int writeSubstance(int idsubstance,int idstructure, IAtomContainer molecule) throws AmbitException {
		int autoIncKeySubstance = -1;
		if (molecule == null) 
				throw new AmbitException("Molecule not assigned");
		if (molecule.getAtomCount()==0)
			throw new AmbitException("Empty molecule can not be stored into database! ");
		try {
			prepareStatement();
			ps.clearParameters();
			
			if (idsubstance > 0) ps.setInt(idx_substance,idsubstance);
			else ps.setNull(idx_substance,Types.INTEGER);
			Object formula = molecule.getProperty(AmbitCONSTANTS.FORMULA);
			if (formula == null) MoleculeTools.analyzeSubstance(molecule);
			formula = molecule.getProperty(AmbitCONSTANTS.FORMULA);
			if (formula == null) {
				ps.setNull(idx_formula,Types.VARCHAR);
				ps.setNull(idx_formula+6,Types.VARCHAR);
			} else {
				ps.setString(idx_formula,formula.toString());
				ps.setString(idx_formula+6,formula.toString());
			}
			
			Double mw = (Double)molecule.getProperty(AmbitCONSTANTS.MOLWEIGHT);
			if (mw == null) {
				ps.setNull(idx_molweight,Types.DOUBLE);
				ps.setNull(idx_molweight+6,Types.DOUBLE);				
			} else {
				ps.setDouble(idx_molweight,mw.doubleValue());
				ps.setDouble(idx_molweight+6,mw.doubleValue());
			}
			
			int unique_smiles = 1;
			long time_elapsed = 0;
			Object smiles = molecule.getProperty(AmbitCONSTANTS.UNIQUE_SMILES);
			if ((smiles == null) || (smiles.equals("") || (smiles.toString().indexOf("error")>=0))) {
				smiles = molecule.getProperty(AmbitCONSTANTS.SMILES);
				unique_smiles =0;
				Object time = molecule.getProperty(AmbitCONSTANTS.UNIQUE_SMILES_TIME);
				if (time != null) time_elapsed = ((Long)time).longValue();				
			} else {
				Object time = molecule.getProperty(AmbitCONSTANTS.UNIQUE_SMILES_TIME);
				if (time != null) time_elapsed = ((Long)time).longValue();
			}	
			
			if ((smiles==null) || (smiles.toString().equals(""))) {
				ps.setNull(idx_smiles,Types.CHAR);
				ps.setNull(idx_usmiles,Types.BOOLEAN);
				ps.setNull(idx_time,Types.INTEGER);
				ps.setNull(idx_smiles+6,Types.CHAR);
				ps.setNull(idx_usmiles+6,Types.BOOLEAN);
				ps.setNull(idx_time+6,Types.INTEGER);
			} else {
				ps.setString(idx_smiles,smiles.toString());
				ps.setInt(idx_usmiles,unique_smiles);
				ps.setLong(idx_time,time_elapsed);
				ps.setString(idx_smiles+6,smiles.toString());
				ps.setInt(idx_usmiles+6,unique_smiles);		
				ps.setLong(idx_time+6,time_elapsed);
			}
			Object substtype = molecule.getProperty(AmbitCONSTANTS.SUBSTANCETYPE);
			if (substtype== null) MoleculeTools.analyzeSubstance(molecule);
			substtype = molecule.getProperty(AmbitCONSTANTS.SUBSTANCETYPE);
			if (substtype == null) {
				ps.setNull(idx_substtype,Types.INTEGER);
				ps.setNull(idx_substtype+6,Types.INTEGER);
			} else {
				int stype = ((CompoundType) substtype).getId();
				ps.setInt(idx_substtype,stype);
				ps.setInt(idx_substtype+6,stype);				
			}
			ps.executeUpdate();
			autoIncKeySubstance = getAutoGeneratedKey(ps);
			if (autoIncKeySubstance==-1) autoIncKeySubstance = idsubstance;
			molecule.setProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE, new Integer(autoIncKeySubstance));
			if (logger.isDebugEnabled())
				logger.debug("Written \t"+AmbitCONSTANTS.AMBIT_IDSUBSTANCE+"\t"+molecule.getProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE));
		    return autoIncKeySubstance;
		} catch (Exception x) {
			throw new AmbitException("Error writing substance\t"+ x.getMessage(),x);
		}

	}	

    protected void prepareStatement() throws SQLException {
    	if (ps == null) ps = connection.prepareStatement(AMBIT_insertSubstance);
    	
    }
    public void close() throws IOException {
    	super.close();
		try {
	       	if (!dbconnection.getConn().getAutoCommit()) {
	       	    dbconnection.getConn().commit();
	       	}    	
	    	if (structureWriter != null) structureWriter.close();structureWriter = null;
	    	if (datasetWriter != null) datasetWriter.close();datasetWriter = null;
			
		    setKeyCheck(dbconnection.getConn(),true);
		} catch (Exception x) {
		    logger.error(x);
		}
    }
	public ArrayList getAliases() {
		return aliases;
	}
	public void setAliases(ArrayList aliases) {
		this.aliases = aliases;
	}
	public String toString() {
		return "Writes chemical compound (structure,identifiers and descriptors) to database";
	}
	public PropertyDescriptorWriter getDescriptorWriter() {
		return descriptorWriter;
	}
	public void setDescriptorWriter(PropertyDescriptorWriter descriptorWriter) {
		this.descriptorWriter = descriptorWriter;
	}
}
