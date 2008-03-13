/*
 * Created on 2006-4-8
 *
 */
package ambit2.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.log.AmbitLogger;


/**
 * Used by {@link DescriptorMopacShell} <br> 
 * Invokes mopac.exe from Mopac 7.1  http://openmopac.net/Downloads/Downloads.html.<br>
 * Doesn't work for molecules with number of heavy atoms > 60, number of all atoms > 120.<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-8
 */
public class DescriptorMopacShell implements IMolecularDescriptor {
    protected static AmbitLogger logger = new  AmbitLogger(DescriptorMopacShell.class);
	//protected static toxTree.logging.TTLogger logger = new toxTree.logging.TTLogger(DescriptorMopacShell.class);
	public static String[] defaultparams = {"PM3 NOINTER NOMM BONDS MULLIK PRECISE GNORM=0.0","helper","MOPAC_7.1.exe",""}; //mmff94
    //public static String[] defaultparams = {"PM3 NOINTER BONDS MULLIK PRECISE EF GNORM=0.0","helper","MOPAC_7.1.exe","mm2"};
	public static  String MESSAGE_UNSUPPORTED_TYPE = "Unsupported atom type ";
	protected int maxHeavyAtoms = 60;
	protected int maxAllAtoms = 120;
	
    protected String[] params = new String[] {"mopac_commands","mopac_path","mopac_executable","force_field"};
    protected String mopac_commands = defaultparams[0];
	
	public static String EHOMO = "EHOMO";
	public static String ELUMO = "ELUMO";
    protected String mopac_path = defaultparams[1];
    protected String mopac_executable = defaultparams[2];
    protected String force_field = defaultparams[3]; 	
    		//for winmopac use "mopac.exe";
    protected String inFile = "data/mopac.dat";
    protected String[] outFile = {
    		"data/mopac.dat.out",
    		"data/mopac.dat.arc",
    		"data/mopac.dat.log",
    		"data/mopac.dat.temp",
    		"data/mopac.dat.end"};
    protected ArrayList<String> table;
	protected ShellSmi2SDF smi2sdf;
	protected ShellMengine mengine;    
	protected boolean errorIfDisconnected = true;
    /*
    protected ModelBuilder3D mb3d = null;
    protected TemplateHandler3D h3d = null;
    protected ForceField forceField = null;
    */
    /**
     * 
     */
    public DescriptorMopacShell() throws ShellException {
        this("helper","MOPAC_7.1.exe");
    }
    public DescriptorMopacShell(String mopac_path, String mopac_exec) throws ShellException {
        super();
        this.mopac_path = mopac_path;
        this.mopac_executable = mopac_exec;
        table = new ArrayList<String>();
        table.add("C");
        table.add("H");
        table.add("Cl");
        table.add("B");
        table.add("F");
        table.add("I");
        table.add("Br");
        table.add("O");
        table.add("N");
        table.add("S");
        table.add("P");
    	smi2sdf = new ShellSmi2SDF();
    	mengine = new ShellMengine();  
    }
    /**
     * @return Returns the mopac_path.
     */
    public synchronized String getMopac_path() {
        return mopac_path;
    }
    /**70/120
     * @param mopac_path The mopac_path to set.
     */
    public synchronized void setMopac_path(String mopac_path) {
        this.mopac_path = mopac_path;
    }
    protected void runMopac(IAtomContainer atomcontainer) throws CDKException {
    	IAtomContainerSet a  = ConnectivityChecker.partitionIntoMolecules(atomcontainer);
    	IAtomContainer mol = null;
    	if (a.getAtomContainerCount()>1)
    		if (errorIfDisconnected)
    			throw new CDKException("Molecule disconnected");
    		else {
    			
    			for (int i=0; i < a.getAtomContainerCount();i++)
    				if ((mol == null) || (mol.getAtomCount() < a.getAtomContainer(i).getAtomCount())) 
    					mol = a.getAtomContainer(i);
    		}
    	else mol = atomcontainer;
    		
    			
        MFAnalyser mfa = new MFAnalyser(mol);
        int heavy = mfa.getHeavyAtoms().size();
        int light = (mol.getAtomCount()-heavy);
        if (heavy>maxHeavyAtoms) {
        	throw new CDKException("Skipping - heavy atoms ("+heavy + ") > " + maxHeavyAtoms );
        } else if (light > maxAllAtoms) {
        	throw new CDKException("Skipping - all atoms ("+light + ") > " + maxAllAtoms );
        }
        
        List v = mfa.getElements();
        for (int i=0; i < v.size();i++) {
            if (!table.contains(v.get(i).toString().trim())) {
                throw new CDKException(MESSAGE_UNSUPPORTED_TYPE +v.get(i));
            }
        }
            
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            throw new CDKException(mopac_executable + " not supported for "+osName);
        }
        else if (osName.startsWith("Windows")) {
            try {
            	
                prepareInput(mol);

                String executable = mopac_path + "\\" + mopac_executable + " "+inFile;
                debug("<MOPAC filename=\""+executable+"\">");
                long now=System.currentTimeMillis();                
                Process p = Runtime.getRuntime().exec(executable
                		,new String[]{},
                        new File(mopac_path));

                InputStream stderr = p.getErrorStream();
                InputStreamReader isr = new InputStreamReader(stderr);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                debug("<stdout>");
                while ( (line = br.readLine()) != null)
                    debug(line);
                debug("</stdout>");

                debug("<wait process=\""+executable+"\">");

                int exitVal = p.waitFor();
                debug("</wait>");
                debug("<exitcode value=\""+Integer.toString(exitVal)+"\">");
                debug("<elapsed_time units=\"ms\">"+Long.toString(System.currentTimeMillis()-now)+ "</elapsed_time>");                
                debug("</MOPAC>");
                try {
                    debug("<parse>");
                    parseOutput(mol);
                    debug("</parse>");
                } catch (Exception x) {
                    debug("</parse>");
                    throw new CDKException(mopac_executable + x.getMessage());
                    
                }
                if (exitVal != 0) 
                	throw new CDKException(mopac_executable + " exit with error "+exitVal);
            } catch (Throwable x) {
            	debug(x.getMessage());
                throw new CDKException(x.getMessage());
            }
        } else { //assume Unix or Linux
           try {
                
                prepareInput(mol);

                String executable = mopac_path + "\\linux\\mopac "+inFile;
                debug("<MOPAC filename=\""+executable+"\">");
                long now=System.currentTimeMillis();                
                Process p = Runtime.getRuntime().exec(executable
                        ,new String[]{},
                        new File(mopac_path));

                InputStream stderr = p.getErrorStream();
                InputStreamReader isr = new InputStreamReader(stderr);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                debug("<stdout>");
                while ( (line = br.readLine()) != null)
                    debug(line);
                debug("</stdout>");

                debug("<wait process=\""+executable+"\">");

                int exitVal = p.waitFor();
                debug("</wait>");
                debug("<exitcode value=\""+Integer.toString(exitVal)+"\">");
                debug("<elapsed_time units=\"ms\">"+Long.toString(System.currentTimeMillis()-now)+ "</elapsed_time>");                
                debug("</MOPAC>");
                try {
                    debug("<parse>");
                    parseOutput(mol);
                    debug("</parse>");
                } catch (Exception x) {
                    debug("</parse>");
                    throw new CDKException(mopac_executable + x.getMessage());
                    
                }
                if (exitVal != 0) 
                    throw new CDKException(mopac_executable + " exit with error "+exitVal);
            } catch (Throwable x) {
                debug(x.getMessage());
                throw new CDKException(x.getMessage());
            }
        }
        if (mol != atomcontainer) {
        	Map<Object,Object> map = mol.getProperties();
        	Iterator<Object> keys = map.keySet().iterator();
        	while (keys.hasNext()) {
        		Object key = keys.next();
        		Object value = map.get(key);
        		if (value != null)
        			atomcontainer.setProperty(key,value);
        	}
        }
        	
 
    }
    public void prepareInput(IAtomContainer mol) throws Exception {
    	if (mol instanceof IMolecule) {
			smi2sdf.setOutputFile("test.sdf");
			smi2sdf.runShell((IMolecule)mol);
			mengine.setInputFile("test.sdf");
			mengine.setOutputFile("good.sdf");
			IMolecule newmol = mengine.runShell((IMolecule)mol);
	    	debug("Writing MOPAC input");
	        Mopac7Writer wri = new Mopac7Writer(new FileOutputStream(mopac_path + "/" + inFile));
	        wri.setOptimize(1);
	        wri.setMopacCommands(mopac_commands);
	        wri.write(newmol);
	        wri.close();
	        for (int i=0; i< outFile.length;i++) {
	            File f = new File(mopac_path + "/"+outFile[i]);
	            if (f.exists()) f.delete();
	        }
			
    	}
    }
    /*
    public void prepareInput(IAtomContainer mol) throws Exception {
    	if (!"".equals(force_field) && (mol instanceof IMolecule)) {
            long now = System.currentTimeMillis();
            debug("<FORCE_FIELD name=\""+force_field+"\">");
            if (h3d == null) {
                debug("<templates>");
                h3d = TemplateHandler3D.getInstance();
                debug(h3d.toString());
                debug("</templates>");
            } 
            debug("<structure mode=\"initialize\">");
            if (mb3d == null) 
                mb3d = ModelBuilder3D.getInstance(h3d, force_field);
            try {
                mb3d.generate3DCoordinates((IMolecule)mol,false);
            } catch (Exception x) {
                debug("<error name=\""+ x.getMessage() + "\"/>");
            }
            debug("<elapsed_time units=\"ms\">"+Long.toString(System.currentTimeMillis()-now)+ "</elapsed_time>");
            debug("</structure>");
            
            if (forceField == null) forceField = new ForceField();
            debug("<structure mode=\"minimize\" class=\"" + forceField.getClass().getName() + "\">");
            try {
                
                forceField.setUsedGMMethods(false, true, false);
                //forceField.setConvergenceParametersForSDM(15, 0.000000001);
                forceField.setConvergenceParametersForCGM(100000, 1);
                forceField.setPotentialFunction(force_field);
    
                forceField.setMolecule((IMolecule)mol, false);
                forceField.minimize();
                mol = forceField.getMolecule();
            } catch (Exception x) {
                debug("<error name=\""+ x.getMessage() + "\"/>");
            }
            debug("<elapsed_time units=\"ms\">"+Long.toString(System.currentTimeMillis()-now)+ "</elapsed_time>");                
            debug("</structure>");
            
            debug("</FORCE_FIELD>");
    	} else debug("<FORCE_FIELD name=\"none\">");
    	debug("Writing MOPAC input");
        Mopac7Writer wri = new Mopac7Writer(new FileOutputStream(mopac_path + "/" + inFile));
        wri.setMopacCommands(mopac_commands);
        wri.write(mol);
        wri.close();
        for (int i=0; i< outFile.length;i++) {
            File f = new File(mopac_path + "/"+outFile[i]);
            if (f.exists()) f.delete();
        }
    }
    */
    public void parseOutput(IAtomContainer mol) throws Exception {
        for (int i=0; i< 2;i++) {
            String fname = mopac_path+"/" + outFile[i]; 
            File f = new File(fname);
            if (!f.exists()) continue;
            debug("<outfile name=\""+ fname + "\">");
            try {
                Mopac7Reader re = new Mopac7Reader(new FileInputStream(f));
                re.read(mol);
                re.close();
                f.delete();
            } catch (Exception x) {
                debug("<error name=\""+ x.getMessage() + "\"/>");
                debug("</outfile>");
                throw new Exception(x);
            }
            debug("</outfile>");
        }
    }
    public String toString() {
    	return "Calculates electronic descriptors by " + mopac_executable + " (http://openmopac.net)";
    }
    
    public String[] getParameterNames() {
        return params;
    }
    public Object[] getParameters() {
        Object params[] = new Object[4];
        params[0] = mopac_commands;
        params[1] = mopac_path;
        params[2] = mopac_executable;
        params[3] = force_field;
        return (params);
    }
    public Object getParameterType(String arg0) {
        return "";
    }
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            //"http://qsar.sourceforge.net/dicts/qsar-descriptors:MOPAC7.1",
        	"http://openmopac.net/Downloads/MOPAC_7.1executable.zip",
            this.getClass().getName(),
            "$Id: DescriptorMopacShell.java,v 0.2 2007/09/21 13:02:00 nina@acad.bg$",
            "ToxTree");
    };
    public void setParameters(Object[] arg0) throws CDKException {
        if (arg0.length > 4) {
            throw new CDKException("DescriptorMopacShell at most 4 parameters");
        }
        if (arg0.length > 0)
        	mopac_commands = arg0[0].toString();
        if (arg0.length > 1)
        	mopac_path = arg0[1].toString();
        if (arg0.length > 2)
        	mopac_executable = arg0[2].toString();
        if (arg0.length > 3)
        	force_field = arg0[3].toString();        
        
        File file = new File(mopac_path+"\\"+mopac_executable);
        if (!file.exists()) {
        	//fallback to defaults
        	mopac_path = defaultparams[1];
        	mopac_executable = defaultparams[2];
        	throw new CDKException(file.getAbsolutePath() + " do not exists!");	
        }
    }
    public DescriptorValue calculate(IAtomContainer arg0) throws CDKException {
    	try {
            logger.info(toString());
	        runMopac(arg0);
	        DoubleArrayResult r = new DoubleArrayResult(Mopac7Reader.parameters.length);
	        for (int i=0; i< Mopac7Reader.parameters.length;i++) 
	        try {
                String result = arg0.getProperty(Mopac7Reader.parameters[i]).toString();
                logger.debug(Mopac7Reader.parameters[i]+" = "+result);
	            r.add(Double.parseDouble(result));
	        } catch (Exception x) {
                logger.warn(x.getMessage());
	            r.add(Double.NaN);
	        }
	       
	        return new DescriptorValue(getSpecification(),
	                getParameterNames(),getParameters(),r,Mopac7Reader.parameters);
    	} catch (Exception x) {
    		throw new CDKException(x.getMessage());
    	}
        
    }
    public IDescriptorResult getDescriptorResultType() {
    	return new DoubleArrayResult();
    }
    protected void debug(String message) {
    	logger.info(message);
    }
/**
 *
 * 
aigin - use a Gaussian zmatrix geometry specification
biradical- system has two unpaired electrons
bonds - print bond order matrix
C.I. - configuration interaction (useful for excited states)
charge=N - charge for ions
dfp - Davidson-Fletcher-Powell method for geometry optimization
dipole - fit the ESP to the calculated dipole
drc - dynamic reaction coordinate calculation
esp - electrostatic potential calculation
esr - calculate rhf unpaired spin density
excited - optimize first excited singlet state
force - force calculation (vibrational frequencies)
geo-ok - override interatomic distance check
hyperfine- hyperfine coupling constants to be calculated
irc - intrinsic reaction coordinate calculation
k=(n,n) - one dimensional Brillouin zone structure (polymer)
localize - print localized orbitals
mmok - use molecular mechanics correction to CONH bonds
mullik - mulliken population analysis
nomm - do not use molecular mechanics correction to CONH bonds
open - open shell rhf calculation
pi - resolve density matrix into sigma and pi bonds
polar - calculate first, second and third order polarizablilities
precise - criteria to be increased by 100 times
pulay - use Pulay's converger to obtain a scf
restart - calculation restarted
root=n - root n to be optimized in a C.I. calculation
rot=n - symmetry number of the system is n
saddle - optimize transition state
scale - scaling factor for Van der Waals distance in ESP
symmetry - impose symmetry conditions
uhf - unrestricted calculation
vectors - print final eigenvectors (molecular orbital coeficients)
xyz - do all geometric operations in cartesian coordinates



 */
	public boolean isErrorIfDisconnected() {
		return errorIfDisconnected;
	}
	public void setErrorIfDisconnected(boolean errorIfDisconnected) {
		this.errorIfDisconnected = errorIfDisconnected;
	}    
}
