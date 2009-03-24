/* DescriptorMopacShell.java
 * Author: Nina Jeliazkova
 * Date: 2008-12-13 
 * Revision: 1.0 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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
package ambit2.mopac;

import java.io.File;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.external.ShellException;
import ambit2.base.log.AmbitLogger;


/**
 * Used by {@link DescriptorMopacShell} <br> 
 * Invokes mopac.exe from Mopac 7.1  http://openmopac.net/Downloads/Downloads.html.<br>
 * Doesn't work for molecules with number of heavy atoms > 60, number of all atoms > 120.<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-8
 */
public class DescriptorMopacShell implements IMolecularDescriptor {
    protected static AmbitLogger logger = new  AmbitLogger(DescriptorMopacShell.class);
    protected String[] params = new String[] {"mopac_commands","mopac_executable","force_field"};

	public static final String EHOMO = "EHOMO";
	public static final String ELUMO = "ELUMO";
    protected String force_field = MopacShell.defaultparams[2]; 
    
    protected MopacShell mopac_shell;
    /**
     * 
     */

    public DescriptorMopacShell() throws ShellException {
        super();
        mopac_shell = new MopacShell();
    }
    /*
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
*/    
    public String toString() {
    	return "Calculates electronic descriptors by " + mopac_shell + " (http://openmopac.net)";
    }
    
    public String[] getParameterNames() {
        return params;
    }
    public Object[] getParameters() {
        Object params[] = new Object[4];
        params[0] = mopac_shell.getMopac_commands();
        try {
        	params[1] = mopac_shell.getExecutable();
        } catch (Exception x) {
        	params[1] = MopacShell.defaultparams[1];
        }
        params[2] = force_field;
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
            "AMBIT 2.0");
    };
    public void setParameters(Object[] arg0) throws CDKException {
        if (arg0.length > 3) {
            throw new CDKException("DescriptorMopacShell at most 3 parameters");
        }
        if (arg0.length > 0)
        	mopac_shell.setMopac_commands(arg0[0].toString());
        if (arg0.length > 2)
        	force_field = arg0[2].toString();
        try {
	        if (arg0.length > 1)
	        	mopac_shell.addExecutable(arg0[1].toString(), null) ;
	        File file = new File(mopac_shell.getExecutable());
	        if (!file.exists()) 
	        	mopac_shell.addExecutable(MopacShell.defaultparams[1],null);
        } catch (Exception x) {
        	logger.error(x);
        }
    }
    public DescriptorValue calculate(IAtomContainer arg0) throws CDKException {
    	try {
            logger.info(toString());
	        IAtomContainer newmol = mopac_shell.runShell(arg0);
	        DoubleArrayResult r = new DoubleArrayResult(Mopac7Reader.parameters.length);
	        for (int i=0; i< Mopac7Reader.parameters.length;i++) 
	        try {
                String result = newmol.getProperty(Mopac7Reader.parameters[i]).toString();
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

}
