package ambit2.core.processors.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.fingerprint.EStateFingerprinter;
import org.openscience.cdk.fingerprint.ExtendedFingerprinter;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.fingerprint.HybridizationFingerprinter;
import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.fingerprint.SubstructureFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;

public class FingerPrintGeneratorTest {
	@Test
	public void test() throws Exception {
		BigInteger [] expected = 
				/*{
				new BigInteger("1126174803623952"), 
				new BigInteger("1152961362175993860"), 
				new BigInteger("2305843011903291728"),
				new BigInteger("25769820160"), 
				new BigInteger("1099511627776"),
				new BigInteger("33554496"), 
				new BigInteger("8864814662912"),
				new BigInteger("4803285265416200"), 
				new BigInteger("9223372050830336000"),
				new BigInteger("2454675102173515776"),
				new BigInteger("1152921504611041536"),
				new BigInteger("155684661740175875"), 
				new BigInteger("6917529131798265856"), 
				new BigInteger("310748377023266816"),
				new BigInteger("67110400"), 
				new BigInteger("2305843009213710336")};
				*/
		{
		new BigInteger("576462128137896992"),
		new BigInteger("10376293541473171584"),
		new BigInteger("2305843009214747777"),
		new BigInteger("72127962782121984"),
		new BigInteger("52776558133248"),
		new BigInteger("0"),
		new BigInteger("338649581355264"),
		new BigInteger("4366794756"),
		new BigInteger("17730770043904"),
		new BigInteger("2314887591865356288"),
		new BigInteger("2305844109263241488"),
		new BigInteger("2400709834665132544"),
		new BigInteger("13835058125075449856"),
		new BigInteger("10741891072"),
		new BigInteger("9007208196998144"),
		new BigInteger("18014398511644672")	
		};
		
		MoleculeReader molreader = new MoleculeReader();
		FingerprintGenerator gen = new FingerprintGenerator(new Fingerprinter());
		InputStream in = FingerPrintGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/core/data/fp/fptest.mol");
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			BitSet bs1 = gen.process(molreader.process(record));
			BigInteger[] h16 = new BigInteger[16];
			MoleculeTools.bitset2bigint16(bs1,64,h16);
			new Random(10000);
			BitSet bs2 = gen.process(molreader.process(record));
			Assert.assertEquals(bs1,bs2);
			MoleculeTools.bitset2bigint16(bs2,64,h16);
			for (int i=0; i <16;i++) {
				Assert.assertEquals(expected[i], h16[i]);
			}
		}
		/*
		IStructureRecord record = new StructureRecord();
		record.setContent();
		MoleculeReader reader = new MoleculeReader();
		reader.process(target)
		*/
	}
	
	@Test
	public void testPolyAromatics() throws Exception {
		MoleculeReader molreader = new MoleculeReader();
		FingerprintGenerator gen = new FingerprintGenerator(new Fingerprinter());
		InputStream in = FingerPrintGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/core/data/fp/polyaromatics.sdf");
		RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
		while (reader.hasNext()) {
			IStructureRecord record = reader.nextRecord();
			IAtomContainer mol = molreader.process(record);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
			gen.setHydrogens(false);
			mol = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(mol);
			BitSet bs1 = gen.process(mol);
			
			System.out.println(bs1);

			ExtendedFingerprinter efp = new ExtendedFingerprinter();
			System.out.println(efp.getClass().getName());
			System.out.println(efp.getFingerprint(mol));
			
			PubchemFingerprinter pfp = new PubchemFingerprinter();
			System.out.println(pfp.getClass().getName());
			System.out.println(pfp.getFingerprint(mol));
			
			EStateFingerprinter esfp = new EStateFingerprinter();
			System.out.println(esfp.getClass().getName());
			System.out.println(esfp.getFingerprint(mol));
			
			MACCSFingerprinter mfp = new MACCSFingerprinter();
			System.out.println(mfp.getClass().getName());
			System.out.println(mfp.getFingerprint(mol));
			
			SubstructureFingerprinter sfp = new SubstructureFingerprinter();
			System.out.println(sfp.getClass().getName());
			System.out.println(sfp.getFingerprint(mol));
			
			HybridizationFingerprinter hfp = new HybridizationFingerprinter();
			System.out.println(hfp.getClass().getName());
			System.out.println(hfp.getFingerprint(mol));
			/*
			BigInteger[] h16 = new BigInteger[16];
			MoleculeTools.bitset2bigint16(bs1,64,h16);
			new Random(10000);
			BitSet bs2 = gen.process(molreader.process(record));
			Assert.assertEquals(bs1,bs2);
			MoleculeTools.bitset2bigint16(bs2,64,h16);
			for (int i=0; i <16;i++) {
				Assert.assertEquals(expected[i], h16[i]);
			}
			*/
		}
		/*
		IStructureRecord record = new StructureRecord();
		record.setContent();
		MoleculeReader reader = new MoleculeReader();
		reader.process(target)
		*/
		reader.close();
	}
	
	/*
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	false
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	false
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
20
c1ccc2cc3c5cccc4cccc(c3(cc2(c1)))c45
{43, 51, 53, 60, 62, 72, 87, 95, 99, 101, 114, 119, 144, 157, 158, 161, 174, 213, 220, 222, 253, 271, 275, 301, 311, 334, 335, 390, 453, 463, 486, 498, 520, 523, 535, 574, 586, 607, 611, 628, 636, 637, 649, 726, 742, 793, 829, 861, 946, 1003, 1015}
{3, 11, 21, 25, 30, 41, 45, 51, 53, 61, 71, 72, 78, 79, 81, 84, 92, 141, 148, 150, 152, 154, 187, 190, 196, 214, 215, 226, 269, 270, 276, 292, 296, 299, 301, 330, 332, 334, 364, 369, 372, 389, 391, 411, 414, 415, 418, 434, 436, 438, 443, 467, 469, 473, 477, 491, 504, 514, 524, 541, 562, 572, 585, 591, 602, 629, 638, 654, 669, 696, 700, 702, 715, 727, 728, 738, 741, 746, 770, 777, 805, 808, 818, 824, 826, 827, 830, 840, 846, 848, 878, 880, 903, 906, 926, 930, 932, 949, 958, 966, 973, 983, 986, 994, 995, 1009, 1010, 1011, 1012, 1013, 1014, 1015, 1016, 1017}
{0, 1, 9, 10, 11, 12, 143, 147, 178, 179, 185, 186, 192, 193, 199, 200, 255, 257, 259, 261, 283, 284, 332, 333, 344, 355, 356, 370, 371, 384, 385, 416, 430, 434, 441, 446, 470, 490, 516, 520, 524, 552, 556, 564, 570, 578, 582, 584, 595, 599, 603, 608, 618, 634, 640, 660, 664, 668, 677, 678, 679, 688, 696, 697, 708, 709, 710, 712, 713, 734, 755, 776, 797, 818, 839, 860}
{11, 16, 17}
{21, 95, 104, 124, 143, 144, 149, 161, 162, 164}
{273, 306}
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
C[true	C.sp2]	C[true	C.sp2]	true
20
c1ccc2cc3c5cccc4cccc(c3(cc2(c1)))c45
{62, 95, 161, 213, 253, 463, 535, 574, 742}
{30, 53, 78, 187, 415, 541, 878, 926, 949, 1009, 1010, 1011, 1012, 1013, 1014, 1015, 1016, 1017}
{9, 10, 11, 12, 143, 144, 178, 179, 185, 186, 192, 193, 199, 200, 255, 257, 259, 261, 284, 332, 333, 355, 356, 384, 385, 416, 430, 441, 470, 490, 520, 524, 552, 556, 564, 570, 578, 582, 584, 595, 603, 608, 618, 634, 640, 660, 664, 668, 677, 678, 679, 688, 696, 697, 708, 709, 710, 712, 713, 734, 755, 776, 797, 818, 839, 860}
{17}
{21, 95, 104, 124, 144, 161, 162, 164}
{273, 306}

	 */
}
