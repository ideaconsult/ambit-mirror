/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/
package ambit2.smarts.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;

import ambit2.smarts.SmartsHelper;
import ambit2.smarts.SmartsParser;

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class TestSmartsSearch extends TestCase 
{	 
	public List<List<Integer>>  matchingAtoms = null;
	public SmartsParser smartsParser = new SmartsParser();
	public LoggingTool logger;
	public IQueryAtomContainer mQuery;
	public IAtomContainer mTarget;
	SmartsHelper helper = new SmartsHelper(SilentChemObjectBuilder.getInstance());
	
	public TestSmartsSearch() 
	{   
		logger = new LoggingTool(this);
	}
	
	public static Test suite() {
		return new TestSuite(TestSmartsSearch.class);
	}
	
	public boolean matches(IAtomContainer atomContainer, IQueryAtomContainer query) throws CDKException 
	{		
		// lets see if we have a single atom query
		if (query.getAtomCount() == 1) {
			// lets get the query atom
			IQueryAtom queryAtom = (IQueryAtom) query.getAtom(0);			
			matchingAtoms = new ArrayList<List<Integer>>();
			Iterator<IAtom> atoms = atomContainer.atoms().iterator();
			while (atoms.hasNext()) 
			{
				IAtom atom = atoms.next();
				if (queryAtom.matches(atom)) {
					List<Integer> tmp = new ArrayList<Integer>();
					tmp.add(atomContainer.getAtomNumber(atom));
					matchingAtoms.add(tmp);
				}
			}
		}
		else 
		{
			List bondMapping = UniversalIsomorphismTester.getSubgraphMaps(atomContainer, query);
			matchingAtoms = getAtomMappings(bondMapping, atomContainer);
		}		
		return matchingAtoms.size() != 0;
	}
	
	
	
	public int[] match(String smarts, String smiles) throws Exception 
	{
		IQueryAtomContainer query = smartsParser.parse(smarts);
		mQuery = query;
		String error = smartsParser.getErrorMessages();
		if (!error.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + error);
			throw(new Exception("Smarts Parser errors:\n" + error));
		}
		SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer atomContainer = sp.parseSmiles(smiles);
		mTarget = atomContainer;
		smartsParser.setSMARTSData(atomContainer);
		
		boolean status = matches(atomContainer,mQuery);
		if (status) {
			return new int[] {countMatches(), getUniqueMatchingAtoms().size() };
		} 
		else 
		{
			return new int[]{0,0};
		}
	}
	
	private int[] match1(String smarts, IAtomContainer atomContainer) throws Exception 
	{
		IQueryAtomContainer query = smartsParser.parse(smarts);
		mQuery = query;
		String error = smartsParser.getErrorMessages();
		if (!error.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + error);
			throw(new Exception("Smarts Parser errors:\n" + error));
		}
		mTarget = atomContainer;
		smartsParser.setSMARTSData(atomContainer);		
		boolean status = matches(atomContainer,mQuery);
		if (status) {
			return new int[] {countMatches(), getUniqueMatchingAtoms().size() };
		} 
		else 
		{
			return new int[]{0,0};
		}
	}
	
	private List<List<Integer>> getAtomMappings(List bondMapping, IAtomContainer atomContainer) 
	{
		List<List<Integer>> atomMapping = new ArrayList<List<Integer>>();
		// loop over each mapping
		for (Object aBondMapping : bondMapping) {
			List list = (List) aBondMapping;
			
			List<Integer> tmp = new ArrayList<Integer>();
			IAtom atom1 = null;
			IAtom atom2 = null;
			// loop over this mapping
			for (Object aList : list) {
				RMap map = (RMap) aList;
				int bondID = map.getId1();
				
				// get the atoms in this bond
				IBond bond = atomContainer.getBond(bondID);
				atom1 = bond.getAtom(0);
				atom2 = bond.getAtom(1);
				
				Integer idx1 = atomContainer.getAtomNumber(atom1);
				Integer idx2 = atomContainer.getAtomNumber(atom2);
				
				if (!tmp.contains(idx1)) tmp.add(idx1);
				if (!tmp.contains(idx2)) tmp.add(idx2);
			}
			if (tmp.size() > 0) atomMapping.add(tmp);
			
			// If there is only one bond, check if it matches both ways.
			if (list.size() == 1 && atom1.getAtomicNumber() == atom2.getAtomicNumber()) {
				List<Integer> tmp2 = new ArrayList<Integer>();
				tmp2.add(tmp.get(0));
				tmp2.add(tmp.get(1));
				atomMapping.add(tmp2);
			}
		}
		return atomMapping;
	}
	
	public int countMatches() 
	{
		return matchingAtoms.size();
	}
	
	public List<List<Integer>> getUniqueMatchingAtoms() 
	{
		List<List<Integer>> ret = new ArrayList<List<Integer>>();
		for (List<Integer> atomMapping : matchingAtoms) 
		{
			Collections.sort(atomMapping);			
			// see if this sequence of atom indices is present
			// in the return container
			boolean present = false;
			for (List<Integer> r : ret) {
				if (r.size() != atomMapping.size()) continue;
				Collections.sort(r);
				boolean matches = true;
				for (int i = 0; i < atomMapping.size(); i++) {
					int index1 = atomMapping.get(i);
					int index2 = r.get(i);
					if (index1 != index2) {
						matches = false;
						break;
					}
				}
				if (matches) {
					present = true;
					break;
				}
			}
			if (!present) ret.add(atomMapping);
		}
		return ret;
	}
	
	/**
	 * Tests from
	 * From http://www.daylight.com/dayhtml_tutorials/languages/smarts/index.html
	 */
	public void testPropertyCharge1() throws Exception {
		int[] results = match("[+1]", "[OH-].[Mg+2]");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testPropertyCharge2() throws Exception {
		int[] results = match("[+1]", "COCC(O)Cn1ccnc1[N+](=O)[O-]");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyCharge3() throws Exception {
		int[] results = match("[+1]", "[NH4+]");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyCharge4() throws Exception {
		int[] results = match("[+1]", "CN1C(=O)N(C)C(=O)C(N(C)C=N2)=C12");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testPropertyCharge5() throws Exception {
		int[] results = match("[+1]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyAromatic1() throws Exception {
		int[] results = match("[a]", "c1cc(C)c(N)cc1");
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);
	}
	
	public void testPropertyAromatic2() throws Exception {
		int[] results = match("[a]", "c1c(C)c(N)cnc1");
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);
	}
	
	public void testPropertyAromatic3() throws Exception {
		int[] results = match("[a]", "c1(C)c(N)cco1");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	public void testPropertyAromatic4() throws Exception {
		int[] results = match("[a]", "c1c(C)c(N)c[nH]1");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	/*
	//On1ccccc1 is not recognized as aromatic  ????
	//It seem that [O-][n+]1ccccc1 is a correct representation in CDK 
	
	public void testPropertyAromatic5() throws Exception {
		int[] results = match("[a]", "On1ccccc1");		
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);
	}
	*/	
	public void testPropertyAromatic6() throws Exception {
		int[] results = match("[a]", "[O-][n+]1ccccc1");
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);
	}
	
	public void testPropertyAromatic7() throws Exception {
		int[] results = match("[a]", "c1ncccc1C1CCCN1C");
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);
	}
	
	public void testPropertyAromatic8() throws Exception {
		int[] results = match("[a]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);
	}
	
	/**
	 * Triazoles
	 * @throws Exception
	 */
	public void testPropertyAromatic9() throws Exception {
		int[] results = match("c1cnnn1", "C1=CN=NN1");
		assertEquals(2, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyAliphatic1() throws Exception {
		int[] results = match("[A]", "c1cc(C)c(N)cc1");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyAliphatic2() throws Exception {
		int[] results = match("[A]", "CCO");
		assertEquals(3, results[0]);
		assertEquals(3, results[1]);
	}
	
	public void testPropertyAliphatic3() throws Exception {
		int[] results = match("[A]", "C=CC=CC=C");
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);
	}
	
	public void testPropertyAliphatic4() throws Exception {
		int[] results = match("[A]", "CC(C)(C)C");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	public void testPropertyAliphatic5() throws Exception {
		int[] results = match("[A]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
		assertEquals(15, results[0]);
		assertEquals(15, results[1]);
	}
	
	public void testPropertyAliphatic6() throws Exception {
		int[] results = match("[A]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
		assertEquals(19, results[0]);
		assertEquals(19, results[1]);
	}
	
	public void testPropertyAtomicNumber1() throws Exception {
		int[] results = match("[#6]", "c1cc(C)c(N)cc1");
		assertEquals(7, results[0]);
		assertEquals(7, results[1]);
	}
	
	public void testPropertyAtomicNumber2() throws Exception {
		int[] results = match("[#6]", "CCO");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyAtomicNumber3() throws Exception {
		int[] results = match("[#6]", "C=CC=CC=C-O");
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);
	}
	
	public void testPropertyAtomicNumber4() throws Exception {
		int[] results = match("[#6]", "CC(C)(C)C");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	public void testPropertyAtomicNumber5() throws Exception {
		int[] results = match("[#6]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
		assertEquals(20, results[0]);
		assertEquals(20, results[1]);
	}
	
	public void testPropertyAtomicNumber6() throws Exception {
		int[] results = match("[#6]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
		assertEquals(17, results[0]);
		assertEquals(17, results[1]);
	}
	
	public void testPropertyAtomicNumber7() throws Exception {
		int[] results = match("[#6]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
		assertEquals(21, results[0]);
		assertEquals(21, results[1]);
	}
	
	public void testPropertyR1() throws Exception {
		int[] results = match("[R2]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
		assertEquals(7, results[0]);
		assertEquals(7, results[1]);
	}
	
	public void testPropertyR2() throws Exception {
		int[] results = match("[R2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
		assertEquals(6, results[0]);
		assertEquals(6, results[1]);          
	}
	
	public void testPropertyR3() throws Exception {
		int[] results = match("[R2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
		assertEquals(4, results[0]);
		assertEquals(4, results[1]);
	}
	
	public void testPropertyR4() throws Exception {
		int[] results = match("[R2]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
		assertEquals(4, results[0]);
		assertEquals(4, results[1]);
	}
	
	public void testPropertyR5() throws Exception {
		int[] results = match("[R2]", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testPropertyr1() throws Exception {
		int[] results = match("[r5]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
		assertEquals(9, results[0]);
		assertEquals(9, results[1]);
	}
	
	public void testPropertyr2() throws Exception {
		int[] results = match("[r5]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testPropertyr3() throws Exception {
		int[] results = match("[r5]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	public void testPropertyr4() throws Exception {
		int[] results = match("[r5]", "C123C5C(OC(=O)C)C=CC2C(N(C)CC1)Cc(ccc4OC(=O)C)c3c4O5");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	public void testPropertyr5() throws Exception {
		int[] results = match("[r5]", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	public void testPropertyValence1() throws Exception {
		int[] results = match("[v4]", "C");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyValence2() throws Exception {
		int[] results = match("[v4]", "CCO");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyValence3() throws Exception {
		int[] results = match("[v4]", "[NH4+]");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	/*
	public void testPropertyValence4() throws Exception {
		int[] results = match("[v4]", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
		assertEquals(16, results[0]);
		assertEquals(16, results[1]);
	}
	
	public void testPropertyValence5() throws Exception {
		int[] results = match("[v4]", "[Cl-].[Cl-].NC(=O)c2cc[n+](COC[n+]1ccccc1C=NO)cc2");
		assertEquals(16, results[0]);
		assertEquals(16, results[1]);
	}
	*/
	
	public void testPropertyX1() throws Exception {
		int[] results = match("[X2]", "CCO");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyX2() throws Exception {
		int[] results = match("[X2]", "O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyX3() throws Exception {
		int[] results = match("[X2]", "CCC(=O)CC");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testPropertyX4() throws Exception {
		int[] results = match("[X2]", "FC(Cl)=C=C(Cl)F");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyX5() throws Exception {
		int[] results = match("[X2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
		assertEquals(3, results[0]);
		assertEquals(3, results[1]);
	}
	
	public void testPropertyX6() throws Exception {
		int[] results = match("[X2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
		assertEquals(3, results[0]);
		assertEquals(3, results[1]);
	}
	
	public void testPropertyD1() throws Exception {
		int[] results = match("[D2]", "CCO");
		assertEquals(1, results[0]);
	}
	
	public void testPropertyD2() throws Exception {
		int[] results = match("[D2]", "O");
		assertEquals(0, results[0]);
	}
	
	public void testPropertyD3() throws Exception {
		int[] results = match("[D2]", "CCC(=O)CC");
		assertEquals(2, results[0]);
	}
	
	public void testPropertyD4() throws Exception {
		int[] results = match("[D2]", "FC(Cl)=C=C(Cl)F");
		assertEquals(1, results[0]);
	}
	
	public void testPropertyD5() throws Exception {
		int[] results = match("[D2]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
		assertEquals(12, results[0]);
	}
	
	public void testPropertyD6() throws Exception {
		int[] results = match("[D2]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
		assertEquals(8, results[0]);
	}
	
	public void testPropertyHAtom1() throws Exception {        
		int[] results = match("[H]", "[H+].[Cl-]");        
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyHAtom2() throws Exception {
		int[] results = match("[H]", "[2H]");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyHAtom3() throws Exception {    	
		int[] results = match("[H]", "[H][H]");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyHAtom4() throws Exception {
		int[] results = match("[H]", "[CH4]");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testPropertyHAtom5() throws Exception {
		//Additionally Changed
		int[] results = match("[H]", "[H]C([H])([H])[H]");
		assertEquals(4, results[0]);
		assertEquals(4, results[1]);
	}
	
	public void testPropertyHTotal1() throws Exception {
		int[] results = match("[H1]", "CCO");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyHTotal2() throws Exception {
		int[] results = match("[H1]", "[2H]C#C");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyHTotal3() throws Exception {       
		int[] results = match("[H1]", "[H]C(C)(C)C");    	
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyHTotal4() throws Exception {
		int[] results = match("[H1]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
		assertEquals(11, results[0]);
		assertEquals(11, results[1]);
	}
	
	public void testPropertyHTotal5() throws Exception {
		int[] results = match("[H1]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
		assertEquals(10, results[0]);
		assertEquals(10, results[1]);
	}
	
	public void testPropertyHTotal6() throws Exception {    	
		int[] results = match("[H1]", "[H][H]");    	    	
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyAnyAtom1() throws Exception {
		int[] results = match("[*]", "C");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyAnyAtom2() throws Exception {
		int[] results = match("[*]", "[2H]C");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyAnyAtom3() throws Exception {
		int[] results = match("[*]", "[H][H]");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testPropertyAnyAtom4() throws Exception {
		int[] results = match("[*]", "[1H]C([1H])([1H])[1H]");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	public void testPropertyAtomicMass1() throws Exception {
		int[] results = match("[13C]", "[13C]");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyAtomicMass2() throws Exception {    
		int[] results = match("[13C]", "[C]");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testPropertyAtomicMass3() throws Exception {
		int[] results = match("[13*]", "[13C]Cl");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testPropertyAtomicMass4() throws Exception {
		int[] results = match("[12C]", "CCl");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testBondSingle1() throws Exception {
		int[] results = match("CC", "C=C");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testBondSingle2() throws Exception {
		int[] results = match("CC", "C#C");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testBondSingle3() throws Exception {
		int[] results = match("CC", "CCO");
		assertEquals(2, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testBondSingle4() throws Exception {
		int[] results = match("CC", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
		assertEquals(28, results[0]);
		assertEquals(14, results[1]);
	}
	
	public void testBondSingle5() throws Exception {
		int[] results = match("CC", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
		assertEquals(14, results[0]);
		assertEquals(7, results[1]);
	}
	
	public void testBondAny1() throws Exception {
		int[] results = match("C~C", "C=C");
		assertEquals(2, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testBondAny2() throws Exception {
		int[] results = match("C~C", "C#C");
		assertEquals(2, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testBondAny3() throws Exception {
		int[] results = match("C~C", "CCO");
		assertEquals(2, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testBondAny4() throws Exception {
		int[] results = match("C~C", "C1C(C)=C(C=CC(C)=CC=CC(C)=CCO)C(C)(C)C1");
		assertEquals(38, results[0]);
		assertEquals(19, results[1]);
	}
	
	public void testBondAny5() throws Exception {
		int[] results = match("[C,c]~[C,c]", "CC1(C)SC2C(NC(=O)Cc3ccccc3)C(=O)N2C1C(=O)O");
		assertEquals(28, results[0]);
		assertEquals(14, results[1]);
	}
	
	public void testBondRing1() throws Exception {
		int[] results = match("C@C", "C=C");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testBondRing2() throws Exception {
		int[] results = match("C@C", "C#C");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testBondRing3() throws Exception {        
		int[] results = match("C@C", "C1CCCCC1");
		assertEquals(12, results[0]);
		assertEquals(6, results[1]);
	}
	
	public void testBondRing4() throws Exception {        
		int[] results = match("[C,c]@[C,c]", "c1ccccc1Cc1ccccc1");
		assertEquals(24, results[0]);
		assertEquals(12, results[1]);
	}
	
	public void testBondRing5() throws Exception {        
		int[] results = match("[C,c]@[C,c]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
		assertEquals(30, results[0]);
		assertEquals(15, results[1]);
	}
	
	public void testBondRing6() throws Exception {        
		int[] results = match("[C,c]@[C,c]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
		assertEquals(44, results[0]);
		assertEquals(22, results[1]);
	}
	
	//This molecule is specially created for Stereo Bond tests
	//Since stereo bond is not implemented in smiles parser. 
	IMolecule getFCCCl(int absStereo) throws Exception
	{
		Molecule mol = new Molecule();
		Atom a1 = new Atom("F");
		mol.addAtom(a1);
		Atom a2 = new Atom("C");
		mol.addAtom(a2);    	
		Atom a3 = new Atom("C");
		mol.addAtom(a3);
		Atom a4 = new Atom("Cl");
		mol.addAtom(a4);
		mol.addBond(new Bond(a1,a2,IBond.Order.SINGLE));
		Bond b = new Bond(a2,a3,IBond.Order.DOUBLE);
		
		throw new Exception("TODO rewrite b.setStereo(absStereo)");
		/*
		b.setStereo(absStereo);
		
		mol.addBond(b);
		mol.addBond(new Bond(a3,a4,IBond.Order.SINGLE));
		return(mol);
		*/
	}
	
	
	/*
	public void testBondStereo1() throws Exception {
		Molecule mol = getFCCCl(SmartsConst.ABSOLUTE_TRANS);    	
		int[] results = match1("F/?C=C/Cl", mol);
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testBondStereo2() throws Exception {
		//int[] results = match("F/?C=C/Cl", "FC=C/Cl");
		Molecule mol = getFCCCl(0);
		int[] results = match1("F/?C=C/Cl", mol);
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	public void testBondStereo3() throws Exception {
		//int[] results = match("F/?C=C/Cl", "FC=CCl");
		Molecule mol = getFCCCl(0);
		int[] results = match1("F/?C=C/Cl", mol);
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	public void testBondStereo4() throws Exception {
		//int[] results = match("F/?C=C/Cl", "F\\C=C/Cl");
		Molecule mol = getFCCCl(SmartsConst.ABSOLUTE_CIS);
		int[] results = match1("F/?C=C/Cl", mol);
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	*/
	
	public void testLogicalNot1() throws Exception {
		int[] results = match("[!c]", "c1cc(C)c(N)cc1");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testLogicalNot2() throws Exception {
		int[] results = match("[!c]", "c1c(C)c(N)cnc1");
		assertEquals(3, results[0]);
		assertEquals(3, results[1]);
	}
	
	public void testLogicalNot3() throws Exception {
		int[] results = match("[!c]", "c1(C)c(N)cco1");
		assertEquals(3, results[0]);
		assertEquals(3, results[1]);
	}
	
	public void testLogicalNot4() throws Exception {
		int[] results = match("[!c]", "c1c(C)c(N)c[nH]1");
		assertEquals(3, results[0]);
		assertEquals(3, results[1]);
	}
	
	public void testLogicalNot5() throws Exception {
		int[] results = match("[!c]", "O=n1ccccc1");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testLogicalNot6() throws Exception {
		int[] results = match("[!c]", "[O-][n+]1ccccc1");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testLogicalNot7() throws Exception {
		int[] results = match("[!c]", "c1ncccc1C1CCCN1C");
		assertEquals(7, results[0]);
		assertEquals(7, results[1]);
	}
	
	public void testLogicalNot8() throws Exception {
		int[] results = match("[!c]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
		assertEquals(16, results[0]);
		assertEquals(16, results[1]);
	}
	
	public void testLogicalOr1() throws Exception {
		int[] results = match("[N,O,o]", "c1cc(C)c(N)cc1");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testLogicalOr2() throws Exception {
		int[] results = match("[N,O,o]", "c1c(C)c(N)cnc1");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testLogicalOr3() throws Exception {
		int[] results = match("[N,O,o]", "c1(C)c(N)cco1");
		assertEquals(2, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testLogicalOr4() throws Exception {
		int[] results = match("[N,O,o]", "c1c(C)c(N)c[nH]1");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testLogicalOr5() throws Exception {
		int[] results = match("[N,O,o]", "O=n1ccccc1");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testLogicalOr6() throws Exception {
		int[] results = match("[N,O,o]", "[O-][n+]1ccccc1");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testLogicalOr7() throws Exception {
		int[] results = match("[N,O,o]", "c1ncccc1C1CCCN1C");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testLogicalOr8() throws Exception {
		int[] results = match("[N,O,o]", "c1ccccc1C(=O)OC2CC(N3C)CCC3C2C(=O)OC");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	public void testLogicalOrHighAnd1() throws Exception {
		int[] results = match("[N,#6&+1,+0]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
		assertEquals(24, results[0]);
		assertEquals(24, results[1]);
	}
	
	public void testLogicalOrHighAnd2() throws Exception {
		int[] results = match("[N,#6&+1,+0]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
		assertEquals(25, results[0]);
		assertEquals(25, results[1]);
	}
	
	public void testLogicalOrHighAnd3() throws Exception {
		int[] results = match("[N,#6&+1,+0]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
		assertEquals(24, results[0]);
		assertEquals(24, results[1]);
	}
	
	public void testLogicalOrHighAnd4() throws Exception {
		int[] results = match("[N,#6&+1,+0]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
		assertEquals(21, results[0]);
		assertEquals(21, results[1]);
	}
	
	public void testLogicalOrHighAnd5() throws Exception {
		int[] results = match("[N,#6&+1,+0]", "N1N([Hg-][O+]=C1N=Nc2ccccc2)c3ccccc3");
		assertEquals(17, results[0]);
		assertEquals(17, results[1]);
	}
	
	/*
	 public void testLogicalOrHighAnd6() throws Exception {
	 //TODO: This takes a long time to match
	  long start = Calendar.getInstance().getTimeInMillis();
	  //int[] results = match("[N,#6&+1,+0]", "[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
	   new SmilesParser(SilentChemObjectBuildergetInstance());
	   SMARTSParser.parse("[N,#6&+1,+0]");
	   long end = Calendar.getInstance().getTimeInMillis();
	   System.out.println( (end - start) );
	   //assertEquals(23, results[0]);
	    }
	    */
	
	public void testLogicalOrHighAnd7() throws Exception {
		int[] results = match("[N,#6&+1,+0]", "[Cl-].Clc1ccc([I+]c2cccs2)cc1");
		assertEquals(12, results[0]);
		assertEquals(12, results[1]);
	}
	
	public void testLogicalOrLowAnd1() throws Exception {
		int[] results = match("[#7,C;+0,+1]", "CCN(CC)C(=O)C1CN(C)C2CC3=CNc(ccc4)c3c4C2=C1");
		assertEquals(15, results[0]);
		assertEquals(15, results[1]);
	}
	
	public void testLogicalOrLowAnd2() throws Exception {
		int[] results = match("[#7,C;+0,+1]", "N12CCC36C1CC(C(C2)=CCOC4CC5=O)C4C3N5c7ccccc76");
		assertEquals(17, results[0]);
		assertEquals(17, results[1]);
	}
	
	public void testLogicalOrLowAnd3() throws Exception {
		int[] results = match("[#7,C;+0,+1]", "COc1cc2c(ccnc2cc1)C(O)C4CC(CC3)C(C=C)CN34");
		assertEquals(13, results[0]);
		assertEquals(13, results[1]);
	}
	
	public void testLogicalOrLowAnd4() throws Exception {
		int[] results = match("[#7,C;+0,+1]", "C123C5C(O)C=CC2C(N(C)CC1)Cc(ccc4O)c3c4O5");
		assertEquals(12, results[0]);
		assertEquals(12, results[1]);
	}
	
	public void testLogicalOrLowAnd5() throws Exception {
		int[] results = match("[#7,C;+0,+1]", "N1N([Hg-][O+]=C1N=Nc2ccccc2)c3ccccc3");
		assertEquals(5, results[0]);
		assertEquals(5, results[1]);
	}
	
	// //TODO: this takes very long. It is the same smiles. So the bottle neck
	// might be in the AtomContainer
	/*
	 public void testLogicalOrLowAnd6() throws Exception { 
	 int[] results = match("[#7,C;+0,+1]", "[Na+].[Na+].[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24");
	 assertEquals(1, results[0]);    	
	 }
	 */
	public void testLogicalOrLowAnd7() throws Exception {
		//It seems that [I+] confuses the aromaticity detector
		//int[] results = match("[#7,C;+0,+1]", "[Cl-].Clc1ccc([I+]c2cccs2)cc1");
		int[] results = match("[#7,C;+0,+1]", "[Cl-].Clc1ccc(Oc2cccs2)cc1");    	
		//System.out.println(SmartsHelper.getAtomsAttributes(mTarget));
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testRing1() throws Exception {
		int[] results = match("C1CCCCC1", "C1CCCCC1CCCC");
		assertEquals(12, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testRing2() throws Exception {
		int[] results = match("C1CCCCC1", "C1CCCCC1C1CCCCC1");
		assertEquals(24, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testRing3() throws Exception {
		int[] results = match("C1CCCCC1", "C1CCCC12CCCCC2");
		assertEquals(12, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testRing4() throws Exception {
		int[] results = match("C1CCCCC1", "c1ccccc1O");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testRing5() throws Exception {
		int[] results = match("C1CCCCC1", "c1ccccc1CCCCCC");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testRing6() throws Exception {
		int[] results = match("C1CCCCC1", "CCCCCC");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testAromaticRing1() throws Exception {
		int[] results = match("c1ccccc1", "c1ccccc1");  
		assertEquals(12, results[0]);
		assertEquals(1, results[1]);
		
		//The second test is an additional one where the query bonds are explicitely set to be aromatic
		//The semantics of "cc" is to treat the unspecified (missing) bond as single or aromatic 
		int[] results2 = match("c1:c:c:c:c:c:1", "c1ccccc1");  
		assertEquals(12, results2[0]);
		assertEquals(1, results2[1]);
	}
	
	public void testAromaticRing2() throws Exception {    	
		int[] results = match("c1ccccc1", "c1cccc2c1cccc2");
		assertEquals(24, results[0]);
		assertEquals(2, results[1]);
	}
	
	public void testAromaticRing3() throws Exception {
		int[] results = match("c1ccccn1", "c1cccc2c1cccc2");
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testAromaticRing4() throws Exception {        
		int[] results = match("c1ccccn1", "c1cccc2c1cccn2");
		assertEquals(2, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid1() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid2() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCCNC(N)=N)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid3() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(N)=O)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid4() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(O)=O)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid5() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CS)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid6() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCC(N)=O)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid7() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCC(O)=O)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid8() throws Exception {        
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC([H])C(O)=O");    	
		assertEquals(0, results[0]);
		assertEquals(0, results[1]);
	}
	
	public void testAminoAcid9() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CNC=N1)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid10() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(CC)C)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid11() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC(C)C)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid12() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCCCN)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid13() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CCSC)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid14() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CC=CC=C1)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid15() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "OC(C1CCCN1)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid16() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CO)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid17() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(C)O)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid18() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CNC2=C1C=CC=C2)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid19() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(CC1=CC=C(O)C=C1)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
	public void testAminoAcid20() throws Exception {
		int[] results = match("[NX3,NX4+][CX4H]([*])[CX3](=[OX1])[O,N]", "NC(C(C)C)C(O)=O");
		assertEquals(1, results[0]);
		assertEquals(1, results[1]);
	}
	
}
