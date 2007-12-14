/* MySQLShellTest.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit.test.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.AmbitUser;
import ambit.data.descriptors.DescriptorsHashtable;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbAdmin;
import ambit.database.DbConnection;
import ambit.database.MySQLShell;
import ambit.database.processors.FindUniqueProcessor;
import ambit.database.writers.DbSubstanceWriter;
import ambit.exceptions.AmbitException;
import ambit.io.AmbitSettingsListener;
import ambit.io.FileInputState;
import ambit.io.MolPropertiesIOSetting;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.EmptyBatchConfig;
import ambit.io.batch.IBatchStatistics;
import ambit.io.batch.IJobStatus;
import ambit.log.AmbitLogger;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.structure.SmilesGeneratorProcessor;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class AllTest extends TestCase {
	AmbitLogger logger = new AmbitLogger(AllTest.class);
	DescriptorsHashtable descriptorLookup = new DescriptorsHashtable(); 
	
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTest.class);
        
    }
    protected void setUp() throws Exception {
    	super.setUp();
    	AmbitLogger.configureLog4j(false);
    }

    public void xtestStart() {
        MySQLShell ms = new MySQLShell();
        try {
            ms.startMySQL();
            DbAdmin dbc = new DbAdmin("localhost","33060","mysql","root","");
            if (dbc.open(false)) {
                System.out.println("successfully connected");
                assertTrue(dbc.databaseExist("mysql"));
                assertFalse(dbc.databaseExist("ambit"));
                
            }
            dbc.close();
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        } catch (SQLException x) {
            x.printStackTrace();
            fail();
        }
        try {
            ms.stopMySQL();
            System.out.println("stopped");
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        }
    }


    public void test() {
    	boolean startmysql = true;
    	boolean drop = false;
    	everything(startmysql,drop);
    }
    public void everything(boolean startmysql,boolean drop) {
        String db = "ambittest";
        MySQLShell ms = new MySQLShell();
        try {
            if (startmysql)ms.startMySQL();
            DbAdmin dbc = new DbAdmin("localhost","33060","mysql","root","");
            dbc.setCreateSQLFile("ambit/database/ambit_create.sql");
            if (dbc.open(false)) {
                System.out.println("successfully connected");
                
	                if (dbc.databaseExist(db)) dbc.dropDatabase(db);
	                assertFalse(dbc.databaseExist(db));
	                try {
		                dbc.createDatabase(db);
		                assertTrue(dbc.databaseExist(db));
		                
		                System.out.println(db + "\t created");
		                dbc.createTables(db);
						AmbitUser user = new AmbitUser("guest");
						dbc.createUsers(db, user);
	                } catch (AmbitException x) {
	                    x.printStackTrace();
	                    System.out.println(db + "\t rollback");
	                    dbc.dropDatabase(db);
	                    fail();
	                }
	                Hashtable tables = new Hashtable();
	
	
	                String[] ts = new String[] {
	                		"ambituser",
	                		"stype",
	                		"substance",
	                		"structure",
	                		"src_dataset",
	                		"struc_dataset",
	                		"struc_user",
	                		"fp1024",
	                		"fpae",
	                		"fpaeid",
	                		"gamut",
	                		"alias",
	                		"cas",
	                		"name",
	                		"author",
	                		"journal",
	                		"literature",
	                		"ref_authors",
	                		"ddictionary",
	                		"localdvalues",
	                		"dvalues",
	                		"dgroup",
	                		"descrgroups",
	                		"dict_user",
	                		"dsname",
	                		"datasets",
	                		"template",
	                		"study_fieldnames",
	                		"template_def",
	                		"study",
	                		"experiment",
	                		"hierarchy",
	                		"study_conditions",
	                		"study_results",
	                		"modeltype",
	                		"qsars",
	                		"qsar_user",
	                		"qsardata",
	                		"qsardescriptors",
	                		"query",
	                		"timings",
	                		"version",
	                		"atom_distance",
	                		"atom_structure",
	                		"products","reaction"
	                };
	               
	                for (int i=0; i < ts.length;i++)
	                    tables.put(ts[i],new Boolean(false));
	                
	                int n = dbc.tablesExist(tables,true);
	                assertEquals(ts.length,n);
	                System.out.println(n + "\ttables OK");
                 
                boolean ok = true;
                try {
                    doInsertCompounds(db,"data/misc/NCTRER_v2a_232_1Mar05.sdf",232,232);
                    //TODO stereo smiles when H is involved are not recognised correctly
                    //should be numbers(232,232);
                    numbers(229,229,232);
                    System.out.println( "\tcompounds inserted");
                    doInsertCompounds(db,"data/misc/224824.sdf",1,1);
                    numbers(230,230,232);
                    System.out.println( "\tcompounds inserted");
                    doInsertCompounds(db,"data/misc/NCTRER_v2a_232_1Mar05.sdf",232,232);
                    numbers(230,237,239);
                    doInsertCompounds(db,"data/misc/BCF_Grammatica_SMILES.csv",238,221);
                    numbers(230+221,230+221,239+238);
                    System.out.println( "\tcompounds inserted");
                } catch (Exception x) {
                	ok = false;
                    System.out.println(x.getMessage());
                } finally {
                	if (drop)
                	dbc.dropDatabase(db);
                	System.out.println(db + " dropped");
                }
                assertFalse(dbc.databaseExist(db));
            }
            dbc.close();
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        } catch (SQLException x) {
            x.printStackTrace();
            fail();
        } finally {
	        try {
	        	if (startmysql)
	            ms.stopMySQL();
	            System.out.println("stopped");
	        } catch (AmbitException x) {
	            x.printStackTrace();
	            fail();
	        }
        }
    }
    public void doInsertCompounds(String database,String fileName, int nread,int nwritten) {
    	DbConnection dbconnection = null;
    	try {
    	    IIteratingChemObjectReader reader = FileInputState.getReader(
					new FileInputStream(fileName),fileName,null);
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
			
			SourceDataset dataset = new SourceDataset(fileName,
					ReferenceFactory.createDatasetReference(fileName, "test"));
			ArrayList aliases = new ArrayList();
			aliases.add("ChemName_IUPAC");
			aliases.add("INChI");
			aliases.add("NSC");
			aliases.add("ID");
			aliases.add("Code");
			aliases.add("KEGG");
			
			DbSubstanceWriter writer = new DbSubstanceWriter(dbconnection,dataset,aliases,descriptorLookup);
			
			
			IAmbitProcessor processors = getProcessor(dbconnection);
			
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer, 
					processors,
					new EmptyBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());
			batch.start();
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
    public IAmbitProcessor getProcessor(DbConnection dbconnection) {
		ProcessorsChain processors = new ProcessorsChain();
		processors.add(new IdentifiersProcessor());
		
		    try {
		        //processors.add(new CASSmilesLookup(dbconnection.getConn(),false));
		        SmilesGeneratorProcessor smigen = new SmilesGeneratorProcessor(5*60*1000);
				processors.add(smigen);
		    	processors.add(new FindUniqueProcessor(dbconnection.getConn()));
		    } catch (Exception x) {
		    	
		    }
		
		return processors;
		
	}    
    /*
    public void doInsertCompounds(String database,String name, int n) {
    	DbConnection dbconnection = null;
    	try {
    		
    		
    		IteratingMDLReader reader = new IteratingMDLReader(
					new FileInputStream(name));
    		dbconnection = new DbConnection("localhost","33060",database,"root","");
    		dbconnection.open();
			
			SourceDataset dataset = new SourceDataset(name,
					ReferenceFactory.createDatasetReference(name, "test"));
			ArrayList aliases = new ArrayList();
			aliases.add("ChemName_IUPAC");
			aliases.add("INChI");
			DbSubstanceWriter writer = new DbSubstanceWriter(dbconnection,dataset,aliases);
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new DefaultAmbitProcessor() {
				public Object process(Object object) throws AmbitException {
					ChemObject co = (ChemObject) object;
					Object cas = co.getProperty("CAS");
					if (cas != null ) co.setProperty(CDKConstants.CASRN,cas);
					co.removeProperty("CAS");
					Object name = co.getProperty("ChemName");
					if (name != null ) co.setProperty(CDKConstants.NAMES,name);
					co.removeProperty("ChemName");
					
					return object;
				}
			});
			SmilesGeneratorProcessor smigen = new SmilesGeneratorProcessor(5*60*1000);
			
			processors.add(smigen);
			processors.add(new FindUniqueProcessor(dbconnection.getConn()));
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer, 
					processors,
					new EmptyBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());
			batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			assertEquals(n, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(n, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(n, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(0, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));		
			
			
    	} catch (Exception x) {
    		fail();
    	} finally {
    		try {
    		if (dbconnection != null) dbconnection.close();
    		} catch (SQLException e) {
    			
    		}
    	}
    }
    */
    public void numbers(int n, int nstruc, int n_names) {
    	DbConnection dbconnection = new DbConnection("localhost","33060","ambittest","root","");
    	try {
		dbconnection.open();
		Statement st = dbconnection.createUnidirectional();
		ResultSet rs = st.executeQuery("select count(*) from substance");
		rs.next();
		int substances = rs.getInt(1);
		rs.close();
		assertEquals(n, substances);
		rs = st.executeQuery("select count(*) from structure");
		rs.next();
		int structures = rs.getInt(1);
		rs.close();
		assertEquals(nstruc, structures);
		rs = st.executeQuery("select count(*) from name");
		rs.next();
		int names = rs.getInt(1);
		rs.close();
		assertEquals(names, n_names);	
		rs = st.executeQuery("select count(*) from struc_dataset");
		rs.next();
		int strucs = rs.getInt(1);
		rs.close();
		assertEquals(nstruc, strucs);
    	} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				dbconnection.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
    }
    
    public void testIsRecent() {
        try {
            DbAdmin dbc = new DbAdmin("localhost","33060","ambit","root","");
            if (dbc.open(false)) {
                System.out.println("successfully connected");
                assertTrue(dbc.databaseExist("ambit"));
                //StringWriter writer = new StringWriter();
                try {
	                FileWriter writer = new FileWriter(new File("ambit_reverse.sql"));
	                dbc.reverseEngineering(new BufferedWriter(writer));
	                writer.flush();
	                writer.close();
                } catch (IOException x) {
                	x.printStackTrace();
                }
                
            }
            dbc.close();
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        } catch (SQLException x) {
            x.printStackTrace();
            fail();
        }

    }
}
