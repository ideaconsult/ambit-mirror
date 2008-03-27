package ambit2.ui.actions.dbadmin;

import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.database.DbConnection;
import ambit2.data.molecule.SourceDataset;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.ReadSubstanceProcessor;
import ambit2.database.readers.DbStructureReader;
import ambit2.database.search.DbDatasetReader;
import ambit2.database.writers.AtomEnvironmentWriter;
import ambit2.database.writers.DbStructureWriter;
import ambit2.database.writers.FingerprintWriter;
import ambit2.database.writers.SmilesWriter;
import ambit2.exceptions.AmbitException;
import ambit2.io.batch.DefaultBatchStatistics;
import ambit2.io.batch.IBatchStatistics;
import ambit2.processors.AromaticityProcessor;
import ambit2.processors.Builder3DProcessor;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.structure.AtomEnvironmentGenerator;
import ambit2.processors.structure.FingerprintGenerator;
import ambit2.processors.structure.SmilesGeneratorProcessor;
import ambit2.ui.UITools;
import ambit2.ui.actions.BatchAction;

/**
 * Optionally generates SMILES, Fingerprints or atom environments for the compounds in the database and writes them back into database.
 * Example: creates two buttons, first uses {@link ambit2.ui.actions.dbadmin.DbOpenAction} to open a connection to database on click,
 * the second uses {@link DBGenerator} to generate SMILES, Fingerprints or atom environments. 
 <pre>
 	
 	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(false);
 	DbOpenAction openAction = new DbOpenAction(dbadinData,null);
 	
 	JButton buttonOpen = new JButton(importAction);
 	
 	DBGenerator generateAction = new DBGenerator(dbAdminData,null);
 	JButton generate = new JButton(generateAction);
 	
 </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DBGenerator extends BatchAction {
	public static final String SQL_SMILES="select idsubstance from substance where (smiles is null) or (usmiles=0)";
	public static final String SQL_FP1024="select substance.idsubstance from substance left join fp1024 using(idsubstance) where (fp1024.status is null) or (fp1024.status != 1)";
	public static final String SQL_DATASET="select idsubstance from substance where idsubstance in (select idsubstance from struc_dataset join structure using(idstructure) where id_srcdataset=? )";	
	//protected static final String SQL_AE="select substance.idsubstance,status from substance left join fpaeid using(idsubstance) where (status is null) or (status='invalid')";
	public static final String SQL_AE="select substance.idsubstance,status from substance left join fpaeid using(idsubstance) where (status is null) or (status='invalid') group by idsubstance";
	public static final String SQL_AE_DATASET="select substance.idsubstance,status from substance left join fpaeid using(idsubstance) where ((status is null) or (status='invalid')) and substance.idsubstance in (select idsubstance from struc_dataset join structure using(idstructure) where id_srcdataset=? )";
    //will build only structures without 3D coordinates (type structure < "3D no H")
	public static final String SQL_3D_DATASET = "select idstructure,idsubstance from structure where type_structure < 4 and idstructure in (select idstructure from struc_dataset where id_srcdataset=?)";
	public static final String SQL_3D = "select idstructure,idsubstance from structure where type_structure < 4";    

	public static Object[] possibilities = {"SMILES", "Fingerprints","Atom Environments","Aromaticity","Generate 3D coordinates"};
	protected int selectedAction = 0;

	 
	
	public DBGenerator(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Generate SMILES/Fingerprints/Atom environments");
	}

	public DBGenerator(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/database.png"));
	}

	public DBGenerator(Object userData, JFrame mainFrame, String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		
		putValue(AbstractAction.SHORT_DESCRIPTION,"Generates SMILES, Fingerprints or Atom environments for compounds in the database.");

	}

	public IIteratingChemObjectReader getReader() {
		if (userData instanceof ISharedDbData) {
			final SourceDataset  d;
			if (userData instanceof AmbitDatabaseToolsData) 
				d = ((AmbitDatabaseToolsData)userData).getSrcDataset();
			else d = null;	
			DbConnection dbconn = ((ISharedDbData) userData).getDbConnection();
			try {
				switch (selectedAction) {
				case 0: return new DbStructureReader(dbconn.getConn(),SQL_SMILES) {
					public String toString() {
						return "Reads structures without SMILES from database";
					}
				};
				case 1: 
					if (d ==null) 
						return new DbStructureReader(dbconn.getConn(),SQL_FP1024) {
							public String toString() {
								return "Reads structures without calculated fingerprints from database";
							}
						};
						else return new DbStructureReader(dbconn.getConn(),SQL_DATASET) {
							public String toString() {
								return "Reads structures without calculated fingerprints from "+d.getName();
							}
							@Override
							public void setParameters(PreparedStatement ps) throws SQLException {
								ps.setInt(1,d.getId());
							}							
						};
				
				case 2: if (d == null)   
						return new DbStructureReader(dbconn.getConn(),SQL_AE) {
						public String toString() {
							return "Reads structures without calculated atom environments from database";
						}						    
						};
					else {
						return new DbStructureReader(dbconn.getConn(),SQL_DATASET) {
							@Override
							public void setParameters(PreparedStatement ps) throws SQLException {
								ps.setInt(1,d.getId());
							}
							@Override
							public String toString() {
								return "Reads structures without calculated atom environments from " + d.getName();
							}
						};
					}
				case 3: {
					if (d != null)
						return new DbDatasetReader(dbconn.getConn(),d,-1,100);
					else return new DbStructureReader(dbconn.getConn(),"select idsubstance from substance order by idsubstance") {
						@Override
						public String toString() {
							// 
							return "All database structures";
						}
					};
				}		
				case 4: if (d == null)   
					return new DbStructureReader(dbconn.getConn(),SQL_3D) {
					public String toString() {
						return "Reads structures without 3D structures";
					}						    
					};
				else {
					return new DbStructureReader(dbconn.getConn(),SQL_3D_DATASET) {
						@Override
						public void setParameters(PreparedStatement ps) throws SQLException {
							ps.setInt(1,d.getId());
						}
						@Override
						public String toString() {
							return "Reads structures without 3D coordinates from " + d.getName();
						}
					};
				}				
				default:
				}	
			} catch (AmbitException x) {
			}
		}
		return null;
	}

	public IChemObjectWriter getWriter() {
		if (userData instanceof ISharedDbData) {
			DbConnection dbconn = ((ISharedDbData) userData).getDbConnection();

			switch (selectedAction) {
			case 0: return new SmilesWriter(dbconn.getConn(),dbconn.getUser());
			case 1: return new FingerprintWriter(dbconn.getConn(),dbconn.getUser());
			case 2: return new AtomEnvironmentWriter(dbconn.getConn(),dbconn.getUser());
			case 3: return new DbStructureWriter(dbconn.getConn(),dbconn.getUser());
			case 4: return new DbStructureWriter(dbconn.getConn(),dbconn.getUser());
			}
		}
		return null;		
	}

    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Processed ");
    	return bs;
    }

	public IAmbitProcessor getProcessor() {
		if (userData instanceof ISharedDbData) {
			DbConnection dbconn = ((ISharedDbData) userData).getDbConnection();
			try {
				ProcessorsChain processors = new ProcessorsChain();
				processors.addProcessor(new ReadSubstanceProcessor(dbconn.getConn()));
				switch (selectedAction) {
				case 0: {processors.add(new SmilesGeneratorProcessor()); break;}
				case 1: {processors.add(new FingerprintGenerator());break;}
				case 2: {processors.add(new AtomEnvironmentGenerator());break;}
				case 3: {processors.add(new AromaticityProcessor());break;}
				case 4: {processors.add(new Builder3DProcessor());break;}
				}
				return processors;
			} catch (AmbitException x) {
				
			}
		}
		return null;
	}
	public void run(ActionEvent arg0) {
		String s = (String)JOptionPane.showInputDialog(
                mainFrame,"Options",
                "Select what to generate",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                "SMILES");

		if ((s != null) && (s.length() > 0))  {
			for (int i=0;i < possibilities.length;i++)
				if (s.equals(possibilities[i])) {
					selectedAction = i;
					break;
				}
	
			super.run(arg0);
		}
	}

}
