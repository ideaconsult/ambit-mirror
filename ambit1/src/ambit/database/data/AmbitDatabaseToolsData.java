package ambit.database.data;

import java.awt.Component;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;

import ambit.data.DefaultData;
import ambit.data.IDataContainers;
import ambit.data.JobStatus;
import ambit.data.descriptors.FuncGroupsDescriptorFactory;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.DataContainer;
import ambit.data.molecule.SmartsFragmentsList;
import ambit.data.molecule.SourceDataset;
import ambit.data.molecule.SourceDatasetList;
import ambit.database.DbConnection;
import ambit.database.aquire.DbAquireProcessor;
import ambit.database.core.DbDescriptors;
import ambit.database.core.DbSrcDataset;
import ambit.database.processors.BatchFactory;
import ambit.database.processors.ExperimentSearchProcessor;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadCASProcessor;
import ambit.database.processors.ReadExperimentsProcessor;
import ambit.database.processors.ReadNameProcessor;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.database.query.DescriptorQueryList;
import ambit.database.query.DistanceQuery;
import ambit.database.query.ExperimentConditionsQuery;
import ambit.database.query.ExperimentQuery;
import ambit.database.readers.SearchFactory;
import ambit.exceptions.AmbitException;
import ambit.io.FastTemplateHandler3D;
import ambit.log.AmbitLogger;
import ambit.processors.Builder3DProcessor;
import ambit.processors.IAmbitProcessor;
import ambit.processors.IdentifiersProcessor;
import ambit.processors.ProcessorsChain;

/**
 * Extension of {@link ambit.database.data.DefaultSharedDbData} to hold queries and results in {@link ambit.applications.dbadmin.AmbitDatabase} application.
 * Example:
 * <pre>
 *     public void testSearch() {
        //A fully functional demo for structure, descriptors and experimental data queries.
        JFrame frame = null;
     	AmbitDatabaseToolsData dbadminData = new AmbitDatabaseToolsData(true);
     	
     	dbadminData.loadData();
     	
     	FileExportAction exportToFile = new FileExportAction(dbadminData,frame);
     	
     	DbOpenAction openAction = new DbOpenAction(dbadminData,frame);
     	DbConnectionStatusAction statusAction = new DbConnectionStatusAction(dbadminData,frame,"CAS search");
     	
     	DbResultsDestinationAction resultsAction = new DbResultsDestinationAction(dbadminData,frame,"Results destination");

     	AbstractAction simMethodAction =  new QueryOptionsAction(dbadminData,frame,"Similarity method");
     	
     	ActionMap structureActions = new ActionMap();
     	AbstractAction exactSearchAction =  new DbExactSearchAction(dbadminData,frame,"Exact search");
     	AbstractAction subSearchAction =  new DbSubstructureSearchAction(dbadminData,frame,"Substructure");
     	AbstractAction simSearchAction =  new DbSimilaritySearchAction(dbadminData,frame,"Similarity");
     	
     	structureActions.put("Exact search",exactSearchAction);
     	structureActions.put("Substructure search",subSearchAction);
     	structureActions.put("Similarity search",simSearchAction);
     	structureActions.put("CurrentStructure",new DbStructureToQuery(dbadminData,frame));
     	ActionFactory.setParentActions(structureActions,structureActions);		
     	dbadminData.setStructureActions(structureActions);
     	
     	DbDescriptorsSearchAction descriptorsSearchAction = new DbDescriptorsSearchAction(dbadminData,frame,"Search by descriptors");
     	ActionMap descriptorsActions = new ActionMap();
     	descriptorsActions.put("Search by descriptors",descriptorsSearchAction);
     	dbadminData.setDescriptorActions(descriptorsActions);
     	
     	DbExperimentsSearchAction experimentSearchAction = new DbExperimentsSearchAction(dbadminData,frame,"Search by experimental data");
     	ActionMap experimentActions = new ActionMap();
     	experimentActions.put("Search by experiments",experimentSearchAction);
		experimentActions.put("Experiments by Study", new ExperimentsByStudyAction(dbadminData,frame,"Experiments by Study"));
     	dbadminData.setExperimentsActions(experimentActions);
     	
     	SelectDatasetAction datasetAction = new SelectDatasetAction(dbadminData,frame,"Dataset");
     	
     	//add all actions to the toolbar
     	JToolBar toolbar = new JToolBar();
     	toolbar.add(openAction);
     	toolbar.add(statusAction);
     	toolbar.add(resultsAction);
     	toolbar.add(datasetAction);
     	toolbar.add(simMethodAction);
     	toolbar.add(exportToFile);

     	//results panel
     	CompoundPanel panel = new CompoundPanel(dbadminData.getMolecules(),null, Color.white,Color.black,JSplitPane.VERTICAL_SPLIT);
     	panel.setPreferredSize(new Dimension(300,300));
     	panel.setBorder(BorderFactory.createTitledBorder("Results"));
     	
     	//Query panel
     	JTabbedPane queryPanel = new JTabbedPane();
     	queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
     	
     	StructureQueryPanel structurePanel  = new StructureQueryPanel(dbadminData.getQueries(),dbadminData.getStructureActions());
     	queryPanel.addTab("Structure query",structurePanel);
     	
     	//Descriptor query panel
     	DescriptorQueryPanel descriptorsQueryPanel = new DescriptorQueryPanel(
				(DescriptorQueryList) dbadminData.getDescriptors(),dbadminData.getDescriptorActions());
     	queryPanel.addTab("Descriptors query",descriptorsQueryPanel);

     	//Experiments, study query panel
     	ExperimentsQueryPanel experimentsQueryPanel = new ExperimentsQueryPanel(
				(ExperimentQuery) dbadminData.getExperiments(),
				(ExperimentConditionsQuery) dbadminData.getStudyConditions(),
				dbadminData.getExperimentsActions());
     	queryPanel.addTab("Experiments query",experimentsQueryPanel);
     	
     	
		AmbitStatusBar statusBar = new AmbitStatusBar(new Dimension(100,24));
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		dbadminData.getJobStatus().addObserver((AmbitStatusBar)statusBar);
		dbadminData.getBatchStatistics().addObserver((AmbitStatusBar)statusBar);
		
     	JPanel mainPanel = new JPanel(new GridLayout(3,1));
     	//Container mainPanel = frame.getContentPane();
     	mainPanel.setLayout(new BorderLayout());
     	mainPanel.setPreferredSize(new Dimension(800,600));

     	
     	mainPanel.add(toolbar,BorderLayout.NORTH);
     	mainPanel.add(panel,BorderLayout.CENTER);
     	mainPanel.add(queryPanel,BorderLayout.EAST);
     	mainPanel.add(statusBar,BorderLayout.SOUTH);
     	
     	
     	//displays everithing in a dialog
     	JOptionPane.showMessageDialog(null,mainPanel,"",JOptionPane.PLAIN_MESSAGE,null);
     	
    }    

 * </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class AmbitDatabaseToolsData extends DefaultSharedDbData implements Runnable, IDataContainers {
	protected SourceDataset srcDataset;
	protected SmartsFragmentsList fragments;
	protected DescriptorQueryList descriptors;
	protected ExperimentQuery experiments;
	protected ExperimentConditionsQuery studyConditions;
	protected DataContainer query;
	protected DataContainer molecules;
	
	
	protected int similarityMethod = SearchFactory.MODE_FINGERPRINT;
    protected double similarityThreshold = 0.5;
	
    protected String aquire_endpoint_description = null;
    protected String aquire_endpoint = null;
    protected String aquire_species = null;
    protected boolean aquire_simpletemplate = false;
    
	protected ActionMap descriptorActions;
	protected ActionMap experimentsActions;
	protected ActionMap structureActions;
	protected ActionMap smartsActions;
	protected boolean loading = false;
	protected TemplateHandler3D templateHandler;
	

	public AmbitDatabaseToolsData(DbConnection dbConnection, boolean init) {
		super();
		molecules = new DataContainer();
		query = new DataContainer();
		if (init)
			loadData();
		srcDataset = null;
	}	
	public AmbitDatabaseToolsData(boolean init) {
		this(null,init);
	}

	public void setDbConnection(DbConnection dbConnection) {
		super.setDbConnection(dbConnection);
		loadData();
	}
	public void open(String host, String port, String database, String user, String password, boolean verify) throws AmbitException {
		super.open(host, port, database, user, password, verify);
		loadData();
	}
	public DescriptorQueryList getDescriptors() {
		return descriptors;
	}
	public void loadData() {
		
	    if (dbConnection != null) {
	        loading = true;
	        try {
				initDescriptors(dbConnection);
				initContainer(dbConnection.getConn());
				initExperiments(dbConnection.getConn());
	        } catch (Exception x) {
	        	getJobStatus().setError(x);
	            logger.error(x);
	        } finally {
	            loading = false;
	        }
	    } 
	}
	public void initExperiments(Connection connection) {
	    if (connection == null) return;
		//test
		try {
			ExperimentSearchProcessor p = new ExperimentSearchProcessor(connection);

			p.loadQuery(null,experiments,true);
			p.loadQuery(null,studyConditions,false);
			p.readStudy(experiments.getStudyList());
			p.close();
			p = null;
			setChanged();
			notifyObservers(experiments);
		} catch (Exception x) {
			
		}
	}
	public void initDescriptors(DbConnection dbConnection) {

	    if (dbConnection.isClosed()) return;		
		//descriptors.loadDefault();
		try {
			
			
			DbDescriptors dbd = new DbDescriptors(dbConnection);
			dbd.initialize();
			dbd.initializeInsert();
			/*
			for (int i=0; i < descriptors.size()-1;i++) {
				dbd.getDescriptorByName(descriptors.getDescriptorDef(i));
			}
			*/
			
			dbd.loadQuery(descriptors);
			DistanceQuery q = new DistanceQuery("C","N",11);
			q.setCondition("between");
			q.setMaxValue(13);
			q.setMinValue(11);
			q.setEnabled(false);
			descriptors.addItem(q);
			dbd.close();
			dbd = null;
			/*
			descriptors.getDescriptorQuery(0).setEnabled(true);
			descriptors.getDescriptorQuery(1).setEnabled(true);
			descriptors.getDescriptorQuery(1).setValue(-10);
			*/
			
		} catch (AmbitException x) {
		    logger.error(x);
			JOptionPane.showMessageDialog(null,x.toString());
		
		} catch (SQLException x) {
			JOptionPane.showMessageDialog(null,x.toString());
		
		}
		setChanged();
		notifyObservers(descriptors);
	}

	
	protected void init() {
        mysqlShell = null;	
        dbConnection = null;
		descriptors = new DescriptorQueryList();
		experiments = new ExperimentQuery();
		studyConditions = new ExperimentConditionsQuery();
		AmbitLogger.configureLog4j(true);
		jobStatus = new JobStatus();
		jobStatus.setModified(true);
		
	    loadConfiguration();

		try {
			fragments = new SmartsFragmentsList();					
			FuncGroupsDescriptorFactory.getFragments(
				FuncGroupsDescriptorFactory.getDocument(defaultData.get(DefaultData.FRAGMENTS).toString()),
				fragments
				);
		} catch (Exception x) {
			jobStatus.setError(x);
			logger.error(x);
		}
		
		try {
			tryMYSQL();
		    dbConnection = new DbConnection(
		    		defaultData.get(DefaultData.HOST).toString(),
		    		defaultData.get(DefaultData.PORT).toString(),
		    		defaultData.get(DefaultData.DATABASE).toString(),
		    		defaultData.get(DefaultData.USER).toString(),
		    		"");			
			if (dbConnection != null) {
			    Object pass = defaultData.get(DefaultData.PASSWORD);
	            //MySQL running, but password is needed to connect
	            if ((pass !=null) && pass.equals("YES")) {
	                //bring password dialog
//	            	QDbConnectionDialog.execute((JFrame)null,this);
	                //JOptionPane.showMessageDialog(null,"password");
	            } else dbConnection.open(false);
			}	
		} catch (AmbitException x) {
			jobStatus.setError(x);
			//JOptionPane.showMessageDialog(null,x.toString());
		}
		
	}
	
	public void initContainer(Connection connection)  {
	    try {
	        molecules.clear();
	        /*
	        DatasetSearchAction a = new DatasetSearchAction(this,null) {
	        	public IIteratingChemObjectReader getSearchReader(Connection connection,
	        			Object query, int page, int pagesize) throws AmbitException {
	        	    
	        		SourceDataset d = null;
	        		if (userData instanceof AmbitDatabaseToolsData) {
	        			d = ((AmbitDatabaseToolsData)userData).getSrcDataset();
	        			if (d==null) { 
	        				return null;
	        			}
	        		} else {	
	        			d = new SourceDataset();
	        			d.setName(AmbitCONSTANTS.AQUIRE);
	        		} 
	        		return new DbDatasetReader(connection,d,0,100);
	        		
	        	}
	        	
                public IBatchStatistics getBatchStatistics() {
                    IBatchStatistics b = super.getBatchStatistics();
                    b.setResultCaption("Retrieved");
                    return b;
                }
                public IAmbitProcessor getProcessor() {
                    try {
                    if (userData instanceof ISharedData) {
                        ISharedDbData dbaData = ((ISharedDbData) userData);
                        ProcessorsChain processors = new ProcessorsChain();
            			processors.add(new ReadSubstanceProcessor(dbaData.getDbConnection().getConn()));
            			processors.add(new ReadAliasProcessor(dbaData.getDbConnection().getConn()));
                       	processors.add(new ReadNameProcessor(dbaData.getDbConnection().getConn()));
                       	processors.add(new ReadCASProcessor(dbaData.getDbConnection().getConn()));
                       	processors.add(new ReadSMILESProcessor(dbaData.getDbConnection().getConn()));
                       	//processors.add(new ReadExperimentsProcessor(null,dbaData.getDbConnection().getConn()));        
                       	//processors.add(new ReadDescriptorsProcessor(null,dbaData.getDbConnection().getConn()));
                       	return processors;	
            		}
                    } catch (Exception x) {
                        logger.error(x);
                    }
                    return super.getProcessor();
                }
	        };
	        a.setActions(((AmbitAction)structureActions.get(structureActions.keys()[0])).getActions());
	        setSrcDataset(new SourceDataset("Carcinogenic Potency Database Summary Tables - All Species",
	        		ReferenceFactory.createEmptyReference()));
	        //"EPA Fathead Minnow Acute Toxicity"
	        a.setEnabledAll(false);
		    a.run(null);
		    a.done();
		    a.setEnabledAll(true);
		    */
		    
	    } catch (Exception x) {
	        
	    }
	    setSrcDataset(null);
	}	
	/*
	public void initContainer(Connection connection)  {
	    int[] ids = {234950,2250,2411,2509,2535,3044,5304,135,430,550,651,720,724,728,790,1042,1076,1097,1158,1223,1224,1249,1250,1422,1688,1874,
	            1919,1973,2005,2064,2065,2073,2154,2178,2181,2184,2185,2209,2258,2263,2485,2492,
	            2494,2501,2713,2726,2729,2735,2743,2745,2836,2887,2936,2940,2945,2966,2978,3038,3039,
	            3040,3041,3042,3063,3067,3118,3121,3122,3177,3208,3278,3336,3368,3501,3506,3509,3544,3593,
	            3622,3682,3825,3869,3909,3917,4105,4233,4303,4423,4425,4434,4505,4527,4529,4533,4546,4561,
	            4568,4569,4582,4737,4743,4769,4867,4879,4881,4892,4944,4959,4999,5292,5307,5317,5429,5697,
	            5743,5912,6000,6003,6024,6122,6168,6176,6288,6378,6403,6413,6477,6499,6621,6951,6954,7251,7264,7286,
	            7327,7329,7349,7357,7359,7364,7369,7532,7556,7629,7630,7674,7675,7680,7720,7794,7799,7853,7863,7955,
	            7982,7990,7992,8037,8046,8063,8072,8228,8229,8290,8342,8357,8399,8400,8415,8417,8418,8420,8424,8431,
	            8442,8445,8449,8451,8471,8472,8476,8478,8492,8493,8494,8498,8503,8509,8572,8624,8786,8812,8828,8846,
	            8849,8924,8929,8943,8948,8954,8955,8959,8967,9093,9359,10255,10308,10643,11162,11165,11398,11756,12891,12959,
	            13011,13477,13557,14083,14085,14158,14201,14383,14680,15393,15429,15456,16164,16412,16696,16711,16895,16905,16907,17162,
	            17287,17996,18150,18344,18345,18347,18349,18351,18354,18361,18365,18368,18372,18803,19187,19584,21000,21079,21119,21125,
	            21135,21150,21153,21581,21604,22467,22477,22637,23116,23214,23349,23369,23554,23619,23621,23940,24993,24995,25134,25360,
	            25857,26061,26184,26895,27408,27554,27563,27757,27944,28176,28568,29187,29680,29682,29896,30388,30882,31234,32022,32030,
	            32742,33087,33200,33269,33658,33981,34247,36002,36618,36623,36920,36925,36936,38887,40729,40863,41436,43343,45535,
	            45685,45715,46605,46607,46742,47031,47643,48028,48198,48492,49175,49566,49861,51619,52201,53851,54590,
	            55176,55237,55292,55759,56104,56110,56615,56887,56902,57811,57950,59357,60159,60714,60915,61054,61089,61413,62794,63778,
	            63783,63786,63789,63790,67064,69576,71259,81762,89921,89922,89925,89934,90552,93949,97247,101214,101215,102749,109041,110141,
	            110976,117930,119011,121185,121186,121484,126198,126914,126992,127705,130816,134518,138471,138627,139143,140638,140956,141120,142042,142043,
	            142088,148864,156832,162354,163988,169588,170893,173946,178742,178743,178744,178746,178783,181853,182283,182331,182352,183373,183735,183744,
	            183853,183913,183961,183997,184067,184539,184585,184590,184632,185238,185565,185579,185675,185680,185706,185708,185795,185968,185969,186071,
	            186277,186349,187076,187139,187140,187516,188078,188081,188885,190643,193028
	    };
	    try {
	        molecules.clear();
		    IAmbitProcessor p = new ReadStructureProcessor(connection);
		    int n=100;
		    for (int i=0; i < n;i++) {
		        Molecule mol = new org.openscience.cdk.Molecule();
		        mol.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,new Integer(ids[i]));
		        mol = (Molecule) p.process(mol);
		        if ((i==0) || (i==(n-1)))
		            molecules.addMolecule(mol);
		        else molecules.addMoleculeNoNotify(mol);
		    }
		    
		    molecules.notifyObservers();
		    molecules.first();
	    } catch (Exception x) {
	        
	    }
	}
	*/
	/* (non-Javadoc)
	 * @see ambit.database.IDataContainers#getMolecules()
	 */
	public DataContainer getMolecules() {
		return molecules;
	}
	/* (non-Javadoc)
	 * @see ambit.database.IDataContainers#getQueries()
	 */
	public DataContainer getQueries() {
		return query;
	}
	

	/* (non-Javadoc)
	 * @see ambit.database.IDataContainers#setMolecules(ambit.data.molecule.DataContainer)
	 */
	public void setMolecules(DataContainer molecules) {
		this.molecules = molecules;
	}
	public void setQueries(DataContainer molecules) {
		this.query = molecules;
		
	}
	public IMolecule getMolecule() {
		return (IMolecule)molecules.getMolecule();
	}
	public void setMolecule(IMolecule molecule) {
		molecules.setMolecule(molecule);
	}
	/* (non-Javadoc)
     * @see ambit.data.ISharedData#getQuery()
     */
    public IMolecule getQuery() {
        return (IMolecule)query.getMolecule();
    }
    /* (non-Javadoc)
     * @see ambit.data.ISharedData#newQuery()
     */
    public void newQuery() {
        query.newMolecule();

    }
    /* (non-Javadoc)
     * @see ambit.data.ISharedData#setQuery(org.openscience.cdk.interfaces.Molecule)
     */
    public void setQuery(IMolecule molecule) {
        query.setMolecule(molecule);

    }
    public void newMolecule() {
        molecules.newMolecule();
    }


	public ExperimentQuery getExperiments() {
		return experiments;
	}


	public void setExperiments(ExperimentQuery experiments) {
		this.experiments = experiments;
	}


	public ActionMap getDescriptorActions() {
		return descriptorActions;
	}


	public void setDescriptorActions(ActionMap descriptorActions) {
		this.descriptorActions = descriptorActions;
	}


	public ActionMap getExperimentsActions() {
		return experimentsActions;
	}


	public void setExperimentsActions(ActionMap experimentsActions) {
		this.experimentsActions = experimentsActions;
	}


	public int getSimilarityMethod() {
		return similarityMethod;
	}


	public void setSimilarityMethod(int similarityMethod) {
		this.similarityMethod = similarityMethod;
		
	}


	public SourceDataset getSrcDataset() {
		return srcDataset;
	}


	public void setSrcDataset(SourceDataset srcDataset) {
		if (srcDataset == null) this.srcDataset = null;
		else
	        if (srcDataset.getId() < 1)
	        	this.srcDataset = null;
	        else
	        	this.srcDataset = srcDataset;
		setChanged();
		notifyObservers(srcDataset);
	}

	
	public ActionMap getSMARTSActions() {
		return smartsActions;
	}
	
	public void setSMARTSActions(ActionMap smartsActions) {
		this.smartsActions = smartsActions;
	}
	
	
	public ActionMap getStructureActions() {
		return structureActions;
	}

	
	public void setStructureActions(ActionMap structureActions) {
		this.structureActions = structureActions;
	}
	
    public synchronized ExperimentConditionsQuery getStudyConditions() {
        return studyConditions;
    }
    public synchronized void setStudyConditions(ExperimentConditionsQuery studyConditions) {
        this.studyConditions = studyConditions;
    }
    public void setCurrentMoleculeAsQuery() throws Exception {
    	IAtomContainer a  =molecules.getMolecule();
    	if (a != null) query.setMolecule((IAtomContainer)a.clone());
    }
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        //((AmbitAction)structureActions.get(structureActions.keys()[0])).setEnabledAll(false);
        try {
            loadData();
        } catch (Exception x) {
        	getJobStatus().setError(x);
        	logger.error(x);

        }
        //((AmbitAction)structureActions.get(structureActions.keys()[0])).setEnabledAll(true);
        
    }
    public synchronized boolean isLoading() {
        return loading;
    }
    public synchronized void setLoading(boolean loading) {
        this.loading = loading;
    }
    public synchronized TemplateHandler3D getTemplateHandler() {
        if (templateHandler == null) templateHandler = FastTemplateHandler3D.getInstance();
        return templateHandler;
    }
    public synchronized void setTemplateHandler(
            TemplateHandler3D templateHandler) {
        this.templateHandler = templateHandler;
    }
    public synchronized double getSimilarityThreshold() {
        return similarityThreshold;
    }
    public synchronized void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }
    public synchronized String getAquire_endpoint() {
        return aquire_endpoint;
    }
    public synchronized void setAquire_endpoint(String aquire_endpoint) {
        if ("".equals(aquire_endpoint)) this.aquire_endpoint = null;
        else this.aquire_endpoint = aquire_endpoint;
    }
    public synchronized String getAquire_species() {
        return aquire_species;
    }
    public synchronized void setAquire_species(String aquire_species) {
        this.aquire_species = aquire_species;
    }
    public synchronized String getAquire_endpoint_description() {
        return aquire_endpoint_description;
    }
    public synchronized void setAquire_endpoint_description(
            String aquire_endpoint_description) {
        this.aquire_endpoint_description = aquire_endpoint_description;
    }
    public ProcessorsChain createProcessorChain() {
		ProcessorsChain processors = new ProcessorsChain();
		
		Connection connection = getDbConnection().getConn();
		BatchFactory.getIdentifiersProcessors(connection,processors);
		    
		IdentifiersProcessor  identifiersProcessor = (IdentifiersProcessor) processors.getProcessor(0); 
		IAmbitProcessor p;
		    
		p = new Builder3DProcessor(getTemplateHandler());
		p.setEnabled(false);
		processors.addProcessor(p);
		    
		BatchFactory.getCalculationProcessors(connection,getDescriptors(), processors);
		try {
				p = new ReadExperimentsProcessor(getExperiments(),connection);
				p.setEnabled(false);
				processors.addProcessor(p);
				
		    	p = new ReadSMILESProcessor(connection);
		    	p.setEnabled(false);
			    processors.addProcessor(p);
			    p = new ReadCASProcessor(connection);
			    p.setEnabled(false);
			    processors.addProcessor(p);
			    p = new ReadNameProcessor(connection);
			    p.setEnabled(false);			    
			    processors.addProcessor(p);
			    p = new ReadAliasProcessor(connection);
			    p.setEnabled(false);			    
			    processors.addProcessor(p);
			    p = new DbAquireProcessor(connection);
			    p.setEnabled(false);			    
			    processors.addProcessor(p);			
			    
				p = new ReadExperimentsProcessor(null,connection);
				p.setEnabled(false);			    
				processors.addProcessor(p);									
		} catch (Exception x) {
		    	logger.error(x);
		}
		    
		return processors;

    }
	public boolean isAquire_simpletemplate() {
		return aquire_simpletemplate;
	}
	public void setAquire_simpletemplate(boolean aquire_simpletemplate) {
		this.aquire_simpletemplate = aquire_simpletemplate;
	}
	
	public SourceDataset selectDataset(SourceDatasetList datasets, boolean readOnly, Component parent, boolean includeAll) throws AmbitException {
		if (getDbConnection() == null) throw new AmbitException("[DATABASE] Not connected.");
		DbSrcDataset ds = new DbSrcDataset(getDbConnection());
		//SourceDatasetList datasets = null;
		//Object[] possibilities ;
			ds.initialize();
			ds.searchDatasets(null,datasets);
			if (datasets == null) datasets = new SourceDatasetList();
            
			SourceDataset dataset = null;
			if (includeAll) {
				dataset = new SourceDataset("ALL",ReferenceFactory.createDatasetReference("All database structures", ""));
	            dataset.setEditable(false);
	            datasets.addItem(0,dataset);
			}

            
            datasets.editor(true).view(parent, true, "Select a dataset or add a new one");
            for (int i=0; i < datasets.size(); i++)
                if  (datasets.getItem(i).isSelected()) {
                    dataset = (SourceDataset) datasets.getItem(i); 
                    
                    break;
                }
            return dataset;
	}
	public SmartsFragmentsList getFragments() {
		return fragments;
	}
	public void setFragments(SmartsFragmentsList fragments) {
		this.fragments = fragments;
	}

}
