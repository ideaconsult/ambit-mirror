package ambit.test.database;

import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.TestCase;

import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.experiment.TemplateField;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.data.model.Model;
import ambit.data.molecule.MolProperties;
import ambit.data.molecule.MolPropertiesFactory;
import ambit.data.molecule.SourceDataset;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.domain.DataModule;
import ambit.exceptions.AmbitException;
import ambit.io.FileInputState;
import ambit.ui.actions.dbadmin.DbBatchImportQSARModels;

public class DbImportTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbImportTest.class);
	}

	public DbImportTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

/*
	public void fixBadLigandInfoSDF() {
		try {
			for (int i=0;i<8;i++) {
				String s = Integer.toString(i+1);
				DbImport.fixLigandInfoSDF("E:\\nina\\LRI_Ambit\\Chemical Databases\\ligand.info\\ligand_info_subset_"+s+".sdf", 
				"E:\\nina\\LRI_Ambit\\Chemical Databases\\ligand.info\\ligand_info_subset_"+s+"_fixed.sdf");
			}	
						
		} catch (AmbitException x) {
			x.printStackTrace();
		}
		
	}
	*/
	public MolProperties createProperties() {
        MolProperties props = null;
        try {
            props = MolPropertiesFactory.createDebnathModelProperties();
            Model m = props.getQsarModel();
            assertEquals("Environ. Mol. Mutagen.",m.getReference().getJournal().getAbbreviation());
            assertEquals(4,m.getReference().getAuthors().size());
        } catch (AmbitException x) {
            x.printStackTrace();
            fail();
        }
        
        assertEquals(4,props.getDescriptors().size());
        assertEquals(3,props.getIdentifiers().size());
        assertEquals(1,props.getExperimental().size());
        assertEquals(1,props.getQSAR().size());
        return props;
	}
	
	public void testImportQSAR() {
		AmbitDatabaseToolsData d = new AmbitDatabaseToolsData(true);
		try {
			d.open("localhost", "33060", "ambit", "root", "",false);
			String sourceFileName = "ambit/domain/demo/Debnath_smiles.csv";
    	    			
			DbBatchImportQSARModels b = new DbBatchImportQSARModels(d,null) {
			    
			    /* (non-Javadoc)
                 * @see ambit.ui.actions.dbadmin.DbBatchImportQSARModels#getReaderListener()
                 */
			    /* (non-Javadoc)
                 * @see ambit.ui.actions.dbadmin.DbBatchImportAction#getReader()
                 */
                public IIteratingChemObjectReader getReader() {
                    try {
                        interactive = false;
                        String sourceFileName = "demo/Debnath_smiles.csv";
                    IIteratingChemObjectReader reader = FileInputState.getReader(
                            DataModule.class.getResourceAsStream(sourceFileName), sourceFileName,null);
    	    	    reader.addChemObjectIOListener(getReaderListener());
    	    	    dataset = new SourceDataset(sourceFileName,ReferenceFactory.createDatasetReference(sourceFileName,""));
    	    	    return reader;
                    } catch (Exception x) {
                        x.printStackTrace();
                        fail();
                    }
                    return null;
                }
                protected IReaderListener getReaderListener() {
                    return new MyListener(this,createProperties());
                }
			};
			
			//b.actionPerformed(null);
			b.run(null);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}

class MyListener implements IReaderListener {
    DbBatchImportQSARModels b;
    MolProperties props;
    public MyListener(DbBatchImportQSARModels b, MolProperties props) {
        super();
        this.b = b;
        this.props = props;
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.listener.ReaderListener#frameRead(org.openscience.cdk.io.ReaderEvent)
     */
    public void frameRead(ReaderEvent arg0) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.listener.ChemObjectIOListener#processIOSettingQuestion(org.openscience.cdk.io.setting.IOSetting)
     */
    public void processIOSettingQuestion(IOSetting setting) {
        //MolProperties props = ((MolPropertiesIOSetting)setting).getProperties();
		try {
			if (props.getQsarModel().getReference() == null)
				props.getQsarModel().setReference((LiteratureEntry) (b.getDataset().getReference().clone()));
			} catch (Exception x) {
				
			}
			props.getQsarModel().getStudy().setName("Mutagenicity Salmonella TA98");
			b.getDescriptorLookup().putAll(props.getDescriptors());
			b.getExperimentParser().setTemplate(props.getTemplate());
			b.getExperimentParser().setReference(b.getDataset().getReference());
			b.getQsarPointsParser().setProperties(props);
			
			b.getExperimentParser().setDefaultStudy(props.getQsarModel().getStudy());
			b.getExperimentParser().setLookup(props.getExperimental());
			
			Hashtable t = props.getExperimental();
			Enumeration e = t.keys();
			while (e.hasMoreElements()) {
				Object key = e.nextElement();
				Object value = t.get(key);
				if ((value != null) && (value instanceof TemplateField) && 
						((TemplateField) value).isResult()) {
					props.getQsarModel().setExperimentField((TemplateField) value);
					break;
				}
				/*
				if ((value instanceof AmbitColumnType) && 
					((AmbitColumnType) value).getId()==AmbitColumnType._ctYpredicted) {
					props.getQsarModel().setExperimentField(key.toString());
					break;
				}
				*/
			}
			

    }
}
