/**
 * Created on 2005-2-24
 *
 */
package ambit2.data.molecule;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Reader;
import java.io.StringReader;
import java.util.BitSet;

import javax.vecmath.Vector2d;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.smiles.SmilesParserWrapper;



/**
 * Various structure processing. 
 * TODO refactor.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class MoleculeTools {
	public final static int _FPLength = 1024;
	protected static HydrogenAdder adder = null;
	protected static Fingerprinter fingerprinter = null;
	/**
	 * 
	 */
	public MoleculeTools() {
		super();
	}
//CMLReader(java.lang.String url)  
	public static BitSet getFingerPrint(String smiles, int fpLength) throws Exception  {
	    SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
	    IMolecule mol = sp.parseSmiles(smiles);
	    if (fingerprinter == null) fingerprinter = new Fingerprinter(fpLength);
	    return fingerprinter.getFingerprint(mol);
		    
	}	

	public static BitSet getFingerPrint(String smiles) throws Exception  {
		return getFingerPrint(smiles,_FPLength);
	}

	public static long bitset2Long(BitSet bs) {
		long h = 0;
		for (int i = 0; i < 64; i++ ) {
			 h = h << 1;	
			 if (bs.get(i)) {
			 	h |= 1;
			 }
	    }
		return h;
	}
	
	public static void bitset2Long16(BitSet bs,int size,long[] h16) {
		
		int L = h16.length;
		for (int j = 0; j < L; j++) {
			long h = 0;
			for (int i = (size-1); i >=0; i-- ) {
				h = h << 1;	
				if (bs.get(j*size+i)) {
					h |= 1;
					//System.out.println("j="+j+"\ti="+i+"\t"+(j*size+i));
				}
			}
			h16[j] = h;
			//System.out.println("0x"+Long.toHexString(h));
		}	
	}
	protected static BufferedImage generateImageFromSmiles(String smiles, int width,
			int height, Color background) {
		BufferedImage buffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffer.createGraphics();
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		try {
			SmilesParserWrapper p = SmilesParserWrapper.getInstance();
			IMolecule mol = p.parseSmiles(smiles);

			// SetOfMolecules molecules =
			// ConnectivityChecker.partitionIntoMolecules(mol);
			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			/*
			 * if (molecules.getAtomContainerCount() > 1) { for (int i=0; i<
			 * molecules.getAtomContainerCount();i++) { Molecule a =
			 * molecules.getMolecule(i); if (!GeometryTools.has2DCoordinates(a)) {
			 * sdg.setMolecule((Molecule)a,false); sdg.generateCoordinates(new
			 * Vector2d(0,1)); } } mol =
			 * (Molecule)SetOfMoleculesManipulator.getAllInOneContainer(molecules);
			 * molecules = null; } else {
			 */
			sdg.setMolecule(mol, false);
			sdg.generateCoordinates(new Vector2d(0, 1));
			mol = sdg.getMolecule();

			sdg = null;

			Renderer2DModel r2dm = new Renderer2DModel();
			Renderer2D renderer = new Renderer2D(r2dm);
			Dimension imageSize = new Dimension(width, height);

			r2dm.setBackgroundDimension(imageSize);
			r2dm.setBackColor(background);
			r2dm.setForeColor(Color.BLACK);
			r2dm.setDrawNumbers(false);
			r2dm.setUseAntiAliasing(true);
			r2dm.setColorAtomsByType(false);
			r2dm.setShowImplicitHydrogens(false);
			r2dm.setShowAromaticity(true);

			GeometryTools.translateAllPositive(mol,r2dm.getRenderingCoordinates());
			GeometryTools.scaleMolecule(mol, imageSize, 0.8,r2dm.getRenderingCoordinates());
			GeometryTools.center(mol, imageSize,r2dm.getRenderingCoordinates());
			
			renderer.paintMolecule(mol, g,true,false);

			renderer = null;
			r2dm = null;
			imageSize = null;
			p = null;

		} catch (InvalidSmilesException x) {

		} catch (Exception x) {

		}
		return buffer;
	}
	
	public static boolean analyzeSubstance(IAtomContainer molecule) {
		if ((molecule == null) || (molecule.getAtomCount()==0)) return false;
		int noH = 0;
		MFAnalyser mfa = new MFAnalyser(molecule);
		String formula = "";
		if (mfa.getAtomCount("H") == 0) {
			noH = 1;
			//TODO to insert H if necessary
			//TODO this uses new SaturationChecker() which relies on cdk/data/config
			if (adder == null)
				adder = new HydrogenAdder();
			try {
				adder.addImplicitHydrogensToSatisfyValency(molecule);
				int atomCount = molecule.getAtomCount();
				formula = mfa.analyseAtomContainer(molecule);
			} catch (CDKException x) {
				x.printStackTrace();
				//TODO exception
				formula = "defaultError";
			}
			
		} else formula = mfa.getMolecularFormula();
		molecule.setProperty(AmbitCONSTANTS.FORMULA,formula);
		double mass = mfa.getMass();
		molecule.setProperty(AmbitCONSTANTS.MOLWEIGHT,new Double(mass));

		int structureType = getStrucType(molecule,noH);		
		molecule.setProperty(AmbitCONSTANTS.STRUCTURETYPE,new StructureType(structureType));
		
		molecule.setProperty(AmbitCONSTANTS.SUBSTANCETYPE,new CompoundType(getSubstanceType(formula)));
		return true;
	}
	public static int getStrucType(IAtomContainer molecule, int noH) {
	    int structureType = StructureType.strucType3DH;
		if (molecule.getAtomCount() > 0) {
			int has3D = 0;
			int has2D = 0;		
			for (int i = 0 ; i < molecule.getAtomCount(); i++) {
				IAtom a = molecule.getAtom(i);
				if (a.getPoint3d() != null) has3D++;
				if (a.getPoint2d() != null) has2D++;			
			}
			if (molecule.getAtomCount() == has3D) 
				structureType = StructureType.strucType3DH - noH;
			else if (molecule.getAtomCount() == has2D) 
				structureType = StructureType.strucType2DH - noH;
			else structureType = StructureType.strucTypeSmiles;
		} else structureType = StructureType.strucTypeSmiles;
		return structureType;
	}
	protected static int getSubstanceType(String formula) {
		//TODO add mixture/organometallic recognition
		if (formula.equals("")) return CompoundType.substTypeOrganic;
		else if (formula.startsWith("C"))
			return CompoundType.substTypeOrganic;
		else return CompoundType.substTypeInorganic;		
	}
	
	public static void  main(String[] args) {
		for (int i=0; i < 10;i++) {
			BufferedImage b = generateImageFromSmiles("c1cc(CCCCC)ccc1CCCCCCC"
					,100,100,Color.white);
			System.out.println((i+1));
		}	
		while (true) {}
	}
	public static IMolecule readMolfile(Reader molfile) throws Exception {
		MDLReader reader = new MDLReader(molfile);
        ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IMoleculeSet som = model.getMoleculeSet();
        return som.getMolecule(0);
        
    }    	
	public static IMolecule readMolfile(String molfile) throws Exception {
		return readMolfile(new StringReader(molfile));
    }    
    public static IMolecule readCMLMolecule(String cml) throws CDKException {
        IMolecule mol = null;
        StringReader strReader = null;
        try {
            strReader= new StringReader(cml);
            return readCMLMolecule(strReader);
        } catch (Exception e) {
            e.printStackTrace();
            return mol;
        }

        
    }     
    public static IMolecule readCMLMolecule(Reader cmlReader) throws CDKException {
        IMolecule mol = null;
        
         CMLReader reader = new CMLReader(cmlReader);
         IChemFile obj = null;
         
            obj = (IChemFile) reader.read(DefaultChemObjectBuilder.getInstance().newChemFile());
            int n = obj.getChemSequenceCount();
            if (n > 1)
                System.out.println("> 1 sequence in a record");      
            for (int j=0; j < n; j++) {
                IChemSequence seq = obj.getChemSequence(j);
                int m = seq.getChemModelCount();
                if (m > 1) 
                    System.out.println("> 1 model in a record");            
                for (int k = 0; k < m; k++) {
                    IChemModel mod = seq.getChemModel(k);
                    IMoleculeSet som = mod.getMoleculeSet();
                    if (som.getMoleculeCount() > 1)
                        System.out.println("> 1 molecule in a record");
                    for (int l=0; l < som.getMoleculeCount(); l++) {
                        mol = som.getMolecule(l);
                    
                    return mol;
                    }
            
                }
            }

         reader = null;
         return mol;
    }        
    
}
