/*
 * Created on 2006-4-8
 *
 */
package ambit.processors.descriptors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorGroup;
import ambit.data.descriptors.DescriptorGroups;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.io.Mopac7Reader;
import ambit.io.Mopac7Writer;


/**
 * Used by {@link ambit.processors.descriptors.MopacProcessor} <br> 
 * Invokes mopac.exe from WinMopac 7.21 (ref) distribution.<br>
 * Doesn't work for molecules with number of heavy atoms > 70, number of all atoms > 120.<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-8
 */
public class MopacShell implements IMolecularDescriptor {
    protected String mopac_path = "helper";
    protected String mopac_executable = "mopac.exe";
    protected String inFile = "cin";
    protected String[] outFile = {"cout","results","clog","cout","matrix"};
    protected ArrayList table;
    protected DescriptorDefinition eHomo, eLumo;
    /**
     * 
     */
    public MopacShell() {
        this("helper","mopac.exe");
    }
    public MopacShell(String mopac_path, String mopac_exec) {
        super();
        this.mopac_path = mopac_path;
        this.mopac_executable = mopac_exec;
        table = new ArrayList();
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
        DescriptorGroups g = new DescriptorGroups();
        g.addItem(new DescriptorGroup("Electronic"));

        eHomo = createDescriptor_eHomo(g,getReference());
        eLumo = createDescriptor_eLumo(g,getReference());
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
    public void runMopac(IAtomContainer mol) throws CDKException {
        MFAnalyser mfa = new MFAnalyser(mol);
        int heavy = mfa.getHeavyAtoms().size();
        int light = (mol.getAtomCount()-heavy);
        if (heavy>70) {
            System.out.println("Skipping - heavy atoms > 70 / "+heavy);
            return;
        } else if (light > 120) {
            System.out.println("Skipping - light atoms > 120 / "+light);
            return;            
        }
        
        Vector v = mfa.getElements();
        for (int i=0; i < v.size();i++) {
            if (!table.contains(v.get(i).toString().trim())) {
                System.out.println("Skipping "+v.get(i));
                return;
            }
        }
            
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            throw new CDKException("MOPAC7 not supported for "+osName);
        }
        else if (osName.startsWith("Windows")) {
            try {
                prepareInput(mol);
                Process p = Runtime.getRuntime().exec(mopac_path + "\\" + mopac_executable,new String[]{},
                        new File(mopac_path));
                p.waitFor();
                parseOutput(mol);
            } catch (Exception x) {
                throw new CDKException(x.getMessage());
            }
        } else { //assume Unix or Linux
            throw new CDKException("MOPAC7 not supported for "+osName);
        }
 
    }
    public void prepareInput(IAtomContainer mol) throws Exception {
        Mopac7Writer wri = new Mopac7Writer(new FileOutputStream(mopac_path + "/" + inFile));
        wri.write(mol);
        wri.close();
        for (int i=0; i< outFile.length;i++) 
            new File(mopac_path + "/"+outFile[i]).delete();
    }
    public void parseOutput(IAtomContainer mol) throws Exception {
        for (int i=0; i< 2;i++) {
            File f = new File(mopac_path+"/" + outFile[i]);
            Mopac7Reader re = new Mopac7Reader(new FileInputStream(f));
            re.setEHomo(eHomo); re.setELumo(eLumo);
            re.read(mol);
            re.close();
            f.delete();
            
        }
        
    }
    public String toString() {
    	return "WinMopac 7.21";
    }
    public LiteratureEntry getReference() {
    	return ReferenceFactory.createDatasetReference(toString(), "http://www.mysiteinc.com/winmopac/");
    	
    }
    public static DescriptorDefinition createDescriptor_eLumo(DescriptorGroups g, LiteratureEntry ref) {
        DescriptorDefinition d = new DescriptorDefinition("eLUMO",ref);
		d.setDescriptorGroups(g);
		d.setUnits("eV");        
        d.setRemark("Energy of the lowest unoccupied molecular orbital");
        return d;
    }
    public static DescriptorDefinition createDescriptor_FinalHeatOfFormation(DescriptorGroups g, LiteratureEntry ref) {
        DescriptorDefinition d = new DescriptorDefinition("FINAL HEAT OF FORMATION",ref);
		d.setDescriptorGroups(g);
		d.setUnits("kCal");
        d.setRemark("Final heat of formation");
        return d;
    }
    public static DescriptorDefinition createDescriptor_eHomo(DescriptorGroups g, LiteratureEntry ref) {
        DescriptorDefinition d = new DescriptorDefinition("eHOMO",ref);
		d.setDescriptorGroups(g);
		d.setUnits("eV");        
        d.setRemark("Energy of the highest occupied molecular orbital");
        return d;
    }        
    public static DescriptorDefinition createDescriptor_TotalEnergy(DescriptorGroups g, LiteratureEntry ref) {
        DescriptorDefinition d = new DescriptorDefinition("TOTAL ENERGY",ref);
		d.setDescriptorGroups(g);
		d.setUnits("eV");             
        d.setRemark("Total energy");
        return d;
    }
    public static DescriptorDefinition createDescriptor_ElectronicEnergy(DescriptorGroups g, LiteratureEntry ref) {
        DescriptorDefinition d = new DescriptorDefinition("ELECTRONIC ENERGY",ref);
		d.setDescriptorGroups(g);
		d.setUnits("eV");               
        d.setRemark("Electronic energy");
        return d;
    }       
    public static DescriptorDefinition createDescriptor_CoreCoreRepulsion(DescriptorGroups g, LiteratureEntry ref) {
        DescriptorDefinition d = new DescriptorDefinition("CORE-CORE REPULSION",ref);
		d.setDescriptorGroups(g);
		d.setUnits("eV");                       
        d.setRemark("Core-core repulsion");
        return d;
    }           
    public static DescriptorDefinition createDescriptor_IonizationPotential(DescriptorGroups g, LiteratureEntry ref) {
        DescriptorDefinition d = new DescriptorDefinition("IONIZATION POTENTIAL",ref);
		d.setDescriptorGroups(g);
		d.setUnits("eV");           
        d.setRemark("Ionization potential");
        return d;
    }           
    public static DescriptorDefinition createDescriptor_MolWeight(DescriptorGroups g, LiteratureEntry ref) {
        DescriptorDefinition d = new DescriptorDefinition("MOLECULAR WEIGHT",ref);
        d.setRemark("Molecular weight");
        return d;
    }           
    
    public synchronized DescriptorDefinition getEHomo() {
        return eHomo;
    }
    public synchronized void setEHomo(DescriptorDefinition homo) {
        eHomo = homo;
    }
    public synchronized DescriptorDefinition getELumo() {
        return eLumo;
    }
    public synchronized void setELumo(DescriptorDefinition lumo) {
        eLumo = lumo;
    }
    public String[] getParameterNames() {
        // TODO Auto-generated method stub
        return null;
    }
    public Object[] getParameters() {
        // TODO Auto-generated method stub
        return null;
    }
    public Object getParameterType(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:WinMOPAC",
            this.getClass().getName(),
            "$Id: MopacShell.java,v 0.1 2006/10/17 8:34:00 Nina Jeliazkova Exp $",
            "ambit.acad.bg");
    };
    public void setParameters(Object[] arg0) throws CDKException {
        // TODO Auto-generated method stub
        
    }
    public DescriptorValue calculate(IAtomContainer arg0) throws CDKException {
        runMopac(arg0);
        DoubleArrayResult r = new DoubleArrayResult(Mopac7Reader.parameters.length);
        for (int i=0; i< r.size();i++) 
        try {
            r.add(Double.parseDouble(arg0.getProperty(Mopac7Reader.parameters[i]).toString()));
        } catch (Exception x) {
            r.add(Double.NaN);
        }
        return new DescriptorValue(getSpecification(),
                getParameterNames(),getParameters(),r);
        
    }
}
