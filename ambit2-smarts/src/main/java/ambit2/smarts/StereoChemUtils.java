package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

public class StereoChemUtils 
{
	/**
	 * 
	 * @param originalDBS - the stereo element from the original molecule that will be cloned
	 * @param originalMol - the original molecule
	 * @param cloneMol - the clone molecule. Its atoms and bond must have already be cloned
	 * @param newAtoms - the cloned/new atoms 
	 * @param newBonds - the cloned/new bonds
	 * @return
	 */
	
	public static DoubleBondStereochemistry cloneDoubleBondStereochemistry(
							DoubleBondStereochemistry originalDBS, 
							IAtomContainer originalMol,
							IAtomContainer cloneMol,
							IAtom newAtoms[], 
							IBond newBonds[])
	{
		if (originalDBS.getStereoBond() == null)
			return null;
		
		int steroBondNum = originalMol.getBondNumber(originalDBS.getStereoBond());
		IBond bonds0[] = originalDBS.getBonds();
		
		if (bonds0 == null)
			return null;
		
		IBond bonds[] = new IBond[bonds0.length];
		for (int i = 0; i < bonds0.length; i++)
		{
			int boNum = originalMol.getBondNumber(bonds0[i]);
			bonds[i] = newBonds[boNum];
		}
		
		DoubleBondStereochemistry dbs = new DoubleBondStereochemistry(newBonds[steroBondNum], bonds, originalDBS.getStereo());
		
		return dbs;
	}
	
	public static TetrahedralChirality cloneTetrahedralChirality(
			TetrahedralChirality originalTHC, 
			IAtomContainer originalMol,
			IAtomContainer cloneMol,
			IAtom newAtoms[])
	{	
		IAtom chiralAtom = originalTHC.getChiralAtom();
		if (chiralAtom == null)
			return null;
		
		int chiralAtomNum = originalMol.getAtomNumber(chiralAtom);
		IAtom ligands0[] = originalTHC.getLigands();
		if (ligands0 == null)
			return null;
		
		IAtom ligands[] = new IAtom[ligands0.length];
		for (int i = 0; i < ligands0.length; i++)
		{
			int atNum = originalMol.getAtomNumber(ligands0[i]);
			ligands[i] = newAtoms[atNum];
		}
		
		TetrahedralChirality thc = new TetrahedralChirality(newAtoms[chiralAtomNum], ligands, originalTHC.getStereo());
		
		return thc;
	}
	
	public static void checkStereoElements(IAtomContainer mol)
	{
		List<IStereoElement> okElements = new ArrayList<IStereoElement>();
		for (IStereoElement element : mol.stereoElements())
		{
			if (element instanceof DoubleBondStereochemistry)
			{
				int status = checkDoubleBondStereochemistry((DoubleBondStereochemistry) element, mol);
				if (status == 0)
					okElements.add(element);
				//System.out.println("DBStereo status = " + status);
				continue;
			}
			
			
			if (element instanceof TetrahedralChirality)
			{
				int status = checkTetrahedralChirality((TetrahedralChirality) element, mol);
				if (status == 0)
					okElements.add(element);
				//System.out.println("Chiral atom status = " + status);
				continue;
			}
			
			//System.out.println(element.getClass().getName());
			//TODO handle ExtendedTetrahedral stereo elements
			
			okElements.add(element);
		}
		
		mol.setStereoElements(okElements);
	}
	
	/**
	 *  
	 * @param dbs - DoubleBondStereochemistry object to checked
	 * @param mol - target molecule (IAtomContainer) 
	 * @return check result:
	 * 	0 - OK
	 * 	1 - stereo bond is null 
	 * 	2 - stereo bond is not present in the molecule
	 * 	3 - stereo bond is not double bond
	 *  4 - stereo bond does not contain 2 atoms
	 * 	5 - ligand bonds array is null
	 *  6 - one of the ligand bonds is not present in the molecule 
	 *  7 - one of the ligand bonds is not adjacent to the stereo bond
	 *  8 - one of the ligand bonds is not single bond 
	 *  9 - stereo Conformation is null
	 */
	public static int checkDoubleBondStereochemistry(DoubleBondStereochemistry dbs, IAtomContainer mol)
	{
		IBond stereoBond = dbs.getStereoBond();
		if (stereoBond == null)
			return 1;
		
		if (!mol.contains(stereoBond))
			return 2;
		
		if (stereoBond.getOrder() != IBond.Order.DOUBLE)
			return 3;
		
		if (stereoBond.getAtomCount() != 2)
			return 4;
		
		IBond ligandBonds[] = dbs.getBonds();
		
		if (ligandBonds == null)
			return 5;
		
		//System.out.println("ligandBonds.length = " + ligandBonds.length);			
		
		for (int i = 0; i < ligandBonds.length; i++)
		{
			if (!mol.contains(ligandBonds[i]))
				return 6;
			
			if ( (!ligandBonds[i].contains(stereoBond.getAtom(0))) &&
					(!ligandBonds[i].contains(stereoBond.getAtom(1))) )
				return 7;
			
			if (ligandBonds[i].getOrder() != IBond.Order.SINGLE)
				return 8;
		}
		
		if (dbs.getStereo() == null)
			return 9;
		
		return 0;
	}
	
	/**
	 * 
	 * @param thc - TetrahedralChirality object to be checked
	 * @param mol - target molecule (IAtomContainer)
	 * @return check result:
	 * 	0 - OK
	 * 	1 - chiralAtom (chiral center) is null
	 *  2 - chiralAtom is not present in the molecule
	 *  3 - ligand atoms array is null
	 *  4 - ligand atoms array size is not 4
	 *  5 - one of the ligand atoms is null
	 *  6 - one of the ligand atoms is not present in the molecule
	 *  7 - one of the ligand atoms is not connected to the chiralAtom
	 *  8 - one of the ligand atoms is connected with non-single bond
	 *  9 - stereo is null
	 *  10 - chiralAtom with missing implicit H atom
	 */
	
	public static int checkTetrahedralChirality(TetrahedralChirality thc, IAtomContainer mol)
	{
		IAtom chiralAtom = thc.getChiralAtom();
		
		if (chiralAtom == null)
			return 1;
		
		if (!mol.contains(chiralAtom))
			return 2;
		
		IAtom ligands[] = thc.getLigands();
		
		if (ligands == null)
			return 3;
		
		if (ligands.length != 4)
			return 4;
		
		for (int i = 0; i < ligands.length; i++)
		{
			if (ligands[i] == null)
				return 5;
			
			if (!mol.contains(ligands[i]))
				return 6;
			
			IBond b = mol.getBond(chiralAtom, ligands[i]);
			
			if (b == null)
			{	
				if (ligands[i] == chiralAtom)
				{
					//ligands[i] is supposed to be the imlicit H atom connected to the ligand
					//that is why the bond is null
					if (chiralAtom.getImplicitHydrogenCount() == 1)
						return 0;
					else
						return 10;
				}
				else
					return 7;
			}	
			
			if (b.getOrder() != IBond.Order.SINGLE)
				return 8;
		}
		
		if (thc.getStereo() == null)
			return 9;
		
		return 0;
	}
	
	public static String getTetrahedralChiralityStatusString(int status)
	{
		switch (status)
		{
		case 0:
			return "OK";
			
		case 1:
			return "chiralAtom (chiral center) is null";
		
		case 2:
			return "chiralAtom is not present in the molecule";
			
		case 3:
			return "ligand atoms array is null";
			
		case 4:
			return "ligand atoms array size is not 4";
			
		case 5:
			return "one of the ligand atoms is null";
			
		case 6:
			return "one of the ligand atoms is not present in the molecule";
		
		case 7:
			return "one of the ligand atoms is not connected to the chiralAtom";
		
		case 8:
			return "one of the ligand atoms is connected with non-single bond";
			
		case 9:
			return "stereo is null";
			
		case 10:
			return "Chiral atom with missing implicit H atom";
			
		default:
			return "Unknown status";
		}
	}
	
	public static String tetrahedralChirality2String(TetrahedralChirality thc, IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		IAtom chiralAtom = thc.getChiralAtom();
		
		if (chiralAtom == null)
			sb.append("center = null ");
		else
			sb.append("center = " + chiralAtom.getSymbol() + "(" + mol.getAtomNumber(chiralAtom) + ") ");
				
		IAtom ligands[] = thc.getLigands();
		
		if (ligands == null)
		{
			sb.append("ligands = null ");
		}
		else
		{	
			sb.append("ligands = ");
			for (int i = 0; i < ligands.length; i++)
			{
				if (ligands[i] == null)
				{	
					sb.append(" null");
					continue;
				}	
				
				IBond b = mol.getBond(chiralAtom, ligands[i]);

				if (b == null)
					sb.append(" ~null bond~");
				else
				{
					switch (b.getOrder())
					{
					case SINGLE:
						sb.append(" -");
						break;
					case DOUBLE:
						sb.append(" =");
						break;	
					case TRIPLE:
						sb.append(" #");
						break;	
					case QUADRUPLE:
						sb.append(" ~4~");
						break;
					case QUINTUPLE:
						sb.append(" ~5~");
						break;
					case SEXTUPLE:
						sb.append(" ~6~");
						break;
					case UNSET:
						sb.append(" ~unset~");
						break;
					}
				}
				
				if (ligands[i] == null)
					sb.append("null ");
				else
					sb.append(ligands[i].getSymbol() + "(" + mol.getAtomNumber(ligands[i]) + ") ");
			}
		}	
		
		sb.append(" stereo = " + thc.getStereo());
		
		return sb.toString();
	}
	
}
