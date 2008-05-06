/**
 * Created on 2005-1-18
 *
 */
package ambit2.domain;

import java.io.InputStream;

import javax.swing.JOptionPane;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.exceptions.AmbitException;
import ambit2.io.FileInputState;
import ambit2.io.FileOutputState;
import ambit2.io.FileServices;
import ambit2.io.MyIOUtilities;
import ambit2.ui.domain.ADDataSetDialog;
import ambit2.data.IAmbitObjectListener;
import ambit2.data.descriptors.Descriptor;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.model.ModelFactory;
import ambit2.data.model.ModelType;
import ambit2.data.molecule.DataContainer;
import ambit2.database.data.DefaultSharedDbData;


/**
 * A class to hold training and test data sets as well as a domain estimation method
 * Used in {@link ambit2.applications.discovery.AmbitDiscoveryApp} application 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class DataModule extends DefaultSharedDbData{
	public QSARDataset modelData;
	public QSARdatasets testData;
	public DataCoverage method;
	
	protected FileServices jnlpServices = null; 
	/**
	 * 
	 */
	public DataModule() {
		this("ambit_discovery.xml");
	}
	public DataModule(String configfile) {
	    super(configfile);
	    //loadConfiguration();
		modelData = new QSARDataset();
		testData = new QSARdatasets();
		method = new DataCoverageDescriptors();
	}	
	public void createNewModel() {
		modelData.clear(false);
		modelData.getModel().addDescriptor
			(new Descriptor("X1",ReferenceFactory.createEmptyReference()));		
		//modelData.setNpoints(100,true);
		//randomData(modelData);		
		modelData.setMethod(method);
		ADDataSetDialog.editDataset(modelData,"Edit new training data set");		
		modelData.setReadonly(true);
	}
	/*TODO implement demo as below
	 * //Obtain the current classloader
	 	ClassLoader classLoader = this.getClass().getClassLoader();
	 //Load the company logo image
	  Image companyLogo = classLoader.getResource("images/companyLogo.gif");
	 * 
	 */
	public void createDemoDataset(String caption ,
							QSARDataset dataset, 
							InputStream stream,String ext) throws AmbitException {
		
		QSARDatasetFactory.loadDebnathSmilesDataset(dataset,
				dataset.getModel(),stream,ext);
		dataset.setMethod(method);
		dataset.setReadonly(true);		
		
	}

	protected void createDemoDataset(String caption ,
								QSARDataset dataset, 
								String filename)  throws AmbitException {

		QSARDatasetFactory.loadDebnathSmilesDataset(dataset,
				dataset.getModel(),filename);
		dataset.setMethod(method);
		dataset.setReadonly(true);		
	
	}
	public void createDemoTestset()  throws AmbitException {
		if (modelData.getNdescriptors() != 0) {
			String filename = "demo/Glende_smiles.csv";

			InputStream stream = DataModule.class.getResourceAsStream(filename);			

			if (stream != null) {
				QSARDataset ds = new QSARDataset();
				ds.getModel().setName(filename);
				ds.getModel().setModelType(new ModelType("Linear Regression"));
				
				ModelFactory.loadDebnathMutagenicityQSAR(ds.getModel());
				
				ds.setMethod(method);

			    createDemoDataset("Mutagenicity model test set",ds,stream,filename);
				testData.addItem(ds);
				testData.refreshMethod();
			} else {
			    throw new AmbitException("Can't find resource "+filename);
			}

		}	
				
	}
	public void createDemoModel(IAmbitObjectListener ao) throws AmbitException {
		String filename = "ambit/domain/demo/Debnath_smiles.csv";
		
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(filename);
		
		if (stream != null) {
			modelData.addAmbitObjectListener(ao);
			modelData.clear(false);
			modelData.setStreamName(filename);
			//modelData.getModel().setName(filename);
			modelData.getModel().setModelType(new ModelType("Linear Regression"));
			ModelFactory.loadDebnathMutagenicityQSAR(modelData.getModel());
		    
			createDemoDataset("Debnath Mutagenicity model",modelData,stream,filename);
		} else throw new AmbitException("Can't find resource "+filename);
	}	
	
	public boolean openModel() throws AmbitException {
		method.clear();
		modelData.getModel().clear();
		boolean r = loadData(modelData,null);
		setMethod(ADomainMethodType._modeRANGE);
		modelData.setMethod(method);
		
		return r;
	}

	public boolean loadData(QSARDataset ds, AllData match) {
		boolean r = false;
	try {
		if (jnlpServices == null) {
			jnlpServices = new FileServices();
			jnlpServices.init();
		}
		try {
			//System.out.println("Trying JWS FileServices");			
			r = jnlpServices.open("open.csv",FileInputState.extensions,ds,match);
		} catch (Exception e) {
			//TODO exception
			e.printStackTrace();
		}
		if (r) return true;
		else {
			//System.out.println("Trying local file access 1");
			return MyIOUtilities.loadFile(null,"",FileInputState.extensions,FileInputState.extensionDescription,ds,match);
		}
	} catch (Exception xx) {
		//System.out.println("Trying local file access 2");		
		//xx.printStackTrace();
		return MyIOUtilities.loadFile(null,"",FileInputState.extensions,FileInputState.extensionDescription,ds,match);
	}
	
	}
	
	public void openTestData() {
		QSARDataset ds = new QSARDataset();
		boolean r = loadData(ds,null); //modelData.data);		
		ds.readonly  = true;
		ds.setMethod(method);	
		testData.addItem(ds);
		testData.refreshMethod();
	}	
	public boolean saveModel() {
		return saveData(modelData);
	}
	public void randomData() {
		if (randomData(modelData)) {
			method.clear();
			modelData.setMethod(method);
		}
	}
	protected boolean randomData(QSARDataset ds) {
		
		boolean go = ds.isEmpty(); 
		if (go || (JOptionPane.showConfirmDialog( null,
					"This will clear all current points in <" + 
					ds.getName() + "> ?",
				    "Please confirm",						
				JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)) {
				go = true;
				int nd = modelData.getNdescriptors();
				double[] m = new double[nd];
				for (int i = 0; i < nd; i++) m[i] = Math.random()*2;
				//TODO randomdata
				//ds.randomData(m);
		}
		return go;
	}
	public void randomTestData() {
		QSARDataset ds = (QSARDataset)testData.getSelectedItem();
		randomTestData(ds);
	}
	public void randomTestData(QSARDataset ds) {
		if (ds != null) {
			if (randomData(ds)) {
				ds.setMethod(method);			
				testData.fireAmbitObjectEvent();				
			}
		}
	}		
	
	
	public void newTestData() {
		if (modelData.getNdescriptors() != 0) {
			QSARDataset ds = new QSARDataset(modelData.getModel());
			ds.setMethod(method);
			randomTestData(ds);			
			ADDataSetDialog.editDataset(ds,"Edit new test data set");			
			testData.addItem(ds);
		}	
	}	

	public boolean saveTestData() {
		QSARDataset ds = (QSARDataset) testData.getSelectedItem();
		return saveData(ds);
	}
	protected boolean saveData(QSARDataset ds) {
		if (ds == null) return false;
		try {
			if (jnlpServices == null) {
				jnlpServices = new FileServices();
				jnlpServices.init();
			}
			if (jnlpServices.save("save.csv",FileOutputState.extensions,FileOutputState.extensionDescription,ds)) return true;
			else
				return MyIOUtilities.saveFile(null,"",FileOutputState.extensions,FileOutputState.extensionDescription,ds);				
		} catch (Exception xx) {
			//TODO JWS exception
			//xx.printStackTrace();
			return MyIOUtilities.saveFile(null,"",FileOutputState.extensions,FileOutputState.extensionDescription,ds);
		}
	}		
	
	public boolean calcModelCoverage() {
		try {
		return modelData.estimateCoverage(method);
		} catch (Exception e) {
			JOptionPane.showMessageDialog( null,
				    e.toString(),
					"defaultError",
				    JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}
	public boolean assessCoverage(boolean selected) throws Exception {
		if (method.isEmpty()) {
			new Exception("assess coverage: method empty");
			//System.err.println("assess coverage: method empty");
			return false;
		}
		boolean r = true;
		int x = testData.getSelectedIndex();
		
		if (selected) {
			QSARDataset ds = testData.getDataSet(x);
			if (ds != null)	
			r = ds.assessCoverage(method);
		} else //all
			for (int i = 0; i < testData.size();i++) 
				r &= testData.getDataSet(i).assessCoverage(method);
		
		testData.setSelectedIndex(x,true);			
		return r;
	}

	public void setFingerPrintType(String fpType) {
		if ((method != null) && (method instanceof DataCoverageFingerprints)) {
			((DataCoverageFingerprints)method).setFpComparison(fpType);
			modelData.refreshMethod();			
		}	
	}
	public void setAtomEnvironmentType(String aeType) {
		if ((method != null) && (method instanceof DataCoverageAtomEnvironment)) {
			((DataCoverageAtomEnvironment)method).setAEComparison(aeType);
			modelData.refreshMethod();
		}	
	}	
	public void setMethod(String methodName) throws AmbitException {
		for (int i = 0; i < ADomainMethodType.methodName.length; i++) {
			if (methodName.equals(ADomainMethodType.methodName[i])) {
				//TODO setmethod
				if (method.getAppDomainMethodType().getId() == i) {
					method.clear();
					modelData.refreshMethod();
					//testData.refreshMethod();
				} else {
				 	modelData.setMethod(null);
					testData.setMethod(null);			
					setMethod(i);
				 	modelData.setMethod(method);
					testData.setMethod(method);			
					
				}
			}
		}
	} 
	
	private void setMethod(int i ) throws AmbitException {
		switch (i) {
		case (ADomainMethodType._modeRANGE): {
			method = null; 
			method = new DataCoverageDescriptors();
			break;
		}
		case (ADomainMethodType._modeEUCLIDEAN): {
			method = null; 
			method = new DataCoverageDistance();
			break;			
		}
		case (ADomainMethodType._modeMAHALANOBIS): {
			//TODO Mahalanobis distance
			break;			
		}
		case (ADomainMethodType._modeCITYBLOCK): {
			method = new DataCoverageDistance(ADomainMethodType._modeCITYBLOCK);		
			break;			
		}
		case (ADomainMethodType._modeDENSITY): {
			method = new DataCoverageDensity();
			break;			
		}
		case (ADomainMethodType._modeLEVERAGE): {
			//TODO Leverage
			break;			
		}
		case (ADomainMethodType._modeFINGERPRINTS): {
			method = new DataCoverageFingerprints();			
			break;			
		}
		case (ADomainMethodType._modeATOMENVIRONMENT): {
			method = new DataCoverageAtomEnvironment();			
			break;			
		}
		case (ADomainMethodType._modeATOMENVIRONMENTRANK): {
		      try {
		          Class classDefinition = Class.forName("ambit2.ranking.DataCoverageAtomEnvironmentRank");
		          method = (DataCoverage)classDefinition.newInstance();
		      } catch (Exception e) {
		    	  method = null;
		    	  throw new AmbitException(e);
		      }
		      /*
			method = new DataCoverageAtomEnvironmentRank(ADomainMethodType._modeATOMENVIRONMENTRANK);
	        //  Object obj = AmbitObject.createObject("ambit2.ranking.DataCoverageAtomEnvironmentRank.class");
	          //if (obj != null) method = (DataCoverage)obj;
			break;
			*/			
		}				
	
		}
	}
	/*
	private void setMethod(int i ) throws AmbitException {
	    String methodClass = ADomainMethodType.methodClass[i];
	      Object obj = null;
	      try {
	          Class classDefinition = AmbitDiscoveryApp.class.getClassLoader().loadClass(methodClass);
	          obj = classDefinition.newInstance();
	      } catch (InstantiationException e) {
	          throw new AmbitException(e);
	      } catch (IllegalAccessException e) {
	          throw new AmbitException(e);
	      } catch (ClassNotFoundException e) {
	          throw new AmbitException(e);
	      }
	      	    
		    
		    if (obj != null) {
		        method = (DataCoverage) obj;
				switch (i) {
				case (ADomainMethodType._modeCITYBLOCK): {
					method.setMode(ADomainMethodType._modeCITYBLOCK);		
					break;			
				}
				}		        
		    }


	}
	*/
	public void setPreprocessing(String method) {
		
	}

	public boolean togglePCA() {
		if (method instanceof DataCoverageDescriptors) {
			boolean p = ((DataCoverageDescriptors)method).isPca();
			((DataCoverageDescriptors)method).setPca(!p);
			return !p;
		} else return false;	
	}
	
	public double getThreshold() {
		return method.getPThreshold();
	}

	public void setThreshold(double val) {
		method.setPThreshold(val);
		modelData.refreshMethod();
		testData.refreshMethod();
	}
	public void setCenterMode(int val) {
		if (method instanceof DataCoverageDescriptors) {
			((DataCoverageDescriptors)method).setCmode(val);
			modelData.refreshMethod();
			testData.refreshMethod();			
		}	
	}
	public int getCenterMode() {
		
		if (method instanceof DataCoverageDescriptors) {		
			return ((DataCoverageDescriptors)method).getDatasetCenterPoint().getId();
		} else return -1;
	}
	/* (non-Javadoc)
     * @see ambit2.data.ISharedData#getMolecule()
     */
    public IMolecule getMolecule() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see ambit2.data.ISharedData#getQuery()
     */
    public IMolecule getQuery() {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see ambit2.data.ISharedData#newMolecule()
     */
    public void newMolecule() {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see ambit2.data.ISharedData#newQuery()
     */
    public void newQuery() {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see ambit2.data.ISharedData#setMolecule(org.openscience.cdk.interfaces.Molecule)
     */
    public void setMolecule(IMolecule molecule) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see ambit2.data.ISharedData#setQuery(org.openscience.cdk.interfaces.Molecule)
     */
    public void setQuery(IMolecule molecule) {
        // TODO Auto-generated method stub

    }
    public DataContainer getMolecules() {
        return null; //TODO modelData; 
    }
    public DataContainer getQueries() {
        return null; //TODO testData; 
    }
}
