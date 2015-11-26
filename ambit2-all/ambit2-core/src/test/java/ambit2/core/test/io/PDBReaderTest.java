package ambit2.core.test.io;

import java.io.InputStream;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.program.Mopac7Writer;
import org.openscience.cdk.silent.ChemFile;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.FileInputState;

public class PDBReaderTest {
	protected static Logger logger = Logger.getLogger(PDBReaderTest.class.getName());

	@Test
	public void testPDB() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/pdb/1010587Mercury.pdb");
		IIteratingChemObjectReader reader = FileInputState.getReader(in, "1010587Mercury.pdb");
		int count = 0;
		while (reader.hasNext()) {
			Object o = reader.next();
			logger.fine(o.toString());
			Assert.assertTrue(o instanceof IStructureRecord);
			count++;
		}
		reader.close();
		Assert.assertEquals(1, count);
	}
	@Test
	public void testPDBReader() throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/pdb/1010587Mercury.pdb");
		PDBReader reader = new PDBReader(in);
		ChemFile c = reader.read(new ChemFile());
		IAtomContainer a = c.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
		Assert.assertNotNull(a);
		Assert.assertTrue(a.getAtomCount()>0);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(a);
		for (IAtom m : a.atoms()) {
			/*
			System.out.print(m.getSymbol());
			System.out.print("\t");
			System.out.print(m.getAtomTypeName());
			System.out.print("\t");
			*/
			Assert.assertNull(m.getImplicitHydrogenCount());
		}
		CDKHydrogenAdder h = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		h.addImplicitHydrogens(a);
		for (IAtom m : a.atoms()) {
			if (m instanceof IPDBAtom) {
				IPDBAtom p = (IPDBAtom) m;
				System.out.println(p);
			}
			System.out.print(m.getClass().getName());
			System.out.print("\t");
			System.out.print(m.getSymbol());
			System.out.print("\t");
			System.out.print(m.getAtomTypeName());
			System.out.print("\t");
			System.out.print(m.getImplicitHydrogenCount());
			System.out.print("\t");
			System.out.print(m.getCharge());
			System.out.print("\t");
			System.out.println(m.getBondOrderSum());
		}
		Mopac7Writer w = new Mopac7Writer(System.out);
		w.write(a);
		w.close();
		reader.close();

	}
}
