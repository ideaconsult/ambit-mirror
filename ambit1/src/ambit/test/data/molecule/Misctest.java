/**
 * <b>Filename</b> Misctest.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-7-27
 * <b>Project</b> ambit
 */
package ambit.test.data.molecule;

import junit.framework.TestCase;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.smiles.SmilesParser;


/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-7-27
 */
public class Misctest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Misctest.class);
    }
    
	public int calculate(IAtomContainer container, int targetPosition) throws CDKException 
	{
		IAtom atom = container.getAtomAt(targetPosition);
		
		IAtomType atomType = findMatchingAtomType(container, atom);
		
		double bondOrderSum = container.getBondOrderSum(atom);
		int charge = atom.getFormalCharge();
		int hcount = atom.getHydrogenCount();
		int valency = atomType.getValency();
		double nLonePair = (valency - ( hcount + bondOrderSum ) - charge) / 2;
		
		int hybridization = (int)nLonePair + ( hcount + container.getConnectedAtoms(atom).length );
		
		System.out.println("ATOM : " + atomType.getAtomTypeName() + " bondOrderSum " + bondOrderSum + ", charge " + charge + ", hcount " + hcount + 
		             ", valency "  + valency + ", nLonePair " + nLonePair + ", hybridization "  + hybridization);
		
		int hybridizationCDK = 0;
		switch (hybridization) 
		{
			case 2:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP1;break;
			case 3:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP2;break;
			case 4:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3;break;
			case 5:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D1;break;
			case 6:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D2;break;
			case 7:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D3;break;
			case 8:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D4;break;
			case 9:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D5;break;
			default:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_UNSET;break;
		}

		return hybridizationCDK;
	}


	
	private IAtomType findMatchingAtomType(IAtomContainer container, IAtom atom) throws CDKException 
	{
		try {
			AtomTypeFactory atomATF = AtomTypeFactory.getInstance(
			        "org/openscience/cdk/config/data/valency2_atomtypes.xml",DefaultChemObjectBuilder.getInstance());

			// take atomtype for the given element...
			IAtomType atomType = atomATF.getAtomType(atom.getSymbol());
			return atomType;
			
		} catch (Exception ex1) 
		{
			throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
		}
	}
	    
    public void testAtomHybridizationVSEPRDescriptor() {
        //AtomHybridizationVSEPRDescriptor d = new AtomHybridizationVSEPRDescriptor();
        SmilesParser sp = new SmilesParser();
        Molecule mol;
        try {
            mol = sp.parseSmiles("CC(O)=O");;
        for (int i =0; i < mol.getAtomCount(); i++)
            calculate(mol,i);
        } catch (CDKException x) {
            x.printStackTrace();
        }
    }
}

