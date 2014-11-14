
/* 
 * Modified by Nikolay Kochev
 * 
 * Copyright (C) 2009  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package ambit2.descriptors.fingerprints;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.QuerySequenceElement;
import ambit2.smarts.SmartsParser;

/**
 * Generates a Pubchem fingerprint for a molecule.
 * <p/>
 * These fingerprints are described 
 * <a href="ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem_fingerprints.txt">
 * here</a> and are of the structural key type, of length 881. See
 * {@link org.openscience.cdk.fingerprint.Fingerprinter} for a
 * more detailed description of fingerprints in general. This implementation is 
 * based on the public domain code made available by the NCGC
 * <a href="http://www.ncgc.nih.gov/pub/openhts/code/NCGC_PubChemFP.java.txt">
 * here</a>
 * 
 * 
 * Some SMARTS patterns have been modified from the original code, since they 
 * were based on explicit H matching. As a result, we replace the explicit H's
 * with a query of the #N&!H0 where N is the atomic number. Thus bit 344 was 
 * originally <code>[#6](~[#6])([H])</code> but is written here as 
 * <code>[#6&!H0]~[#6]</code>. In some cases, where the H count can be reduced
 * to single possibility we directly use that H count. An example is bit 35, 
 * which was <code>[#6](~[#6])(~[#6])(~[#6])([H])</code> and is rewritten as 
 * <code>[#6H1](~[#6])(~[#6])(~[#6]</code>.
 * <p/>
 * 
 * <b>Warning - this class is not thread-safe and uses stores intermediate steps
 * internally. Please use a seperate instance of the class for each thread.</b>
 *
 * @author Rajarshi Guha
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 * @cdk.module fingerprint
 * @cdk.githash
 * @cdk.threadnonsafe
 */
@TestClass("org.openscience.cdk.fingerprint.PubchemFingerprinterTest")
public class PubChemFingerprinter  {

	/** 
	 * Number of bits in this fingerprint.
	 */
	public static final int FP_SIZE = 881;
	private byte[] m_bits;
	private transient List<PubChemBitSubstructure> bitSubstructures = new ArrayList<PubChemBitSubstructure>();
	private transient IsomorphismTester isoTester = new IsomorphismTester();
	private transient SmartsParser parser = new SmartsParser();


	public PubChemFingerprinter() {
		 m_bits = new byte[(FP_SIZE + 7) >> 3];
		initPubChemBitSubstructures();
	}


	/**
	 * Calculate 881 bit Pubchem fingerprint for a molecule.
	 * <p/>
	 * See 
	 * <a href="ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem_fingerprints.txt">here</a>
	 * for a description of each bit position.
	 *
	 * @param atomContainer the molecule to consider
	 * @return the fingerprint
	 * @throws CDKException if there is an error during substructure 
	 * searching or atom typing
	 * @see #getFingerprintAsBytes()
	 */
	public BitSet getFingerprint(IAtomContainer atomContainer) throws CDKException {
		generateFp(atomContainer);
		BitSet fp = new BitSet(FP_SIZE);
		for (int i = 0; i < FP_SIZE; i++) {
			if (isBitOn(i)) fp.set(i);
		}
		return fp;
	}


	public int getSize() {
		return FP_SIZE;
	}

	static class CountElements {
		int[] counts = new int[120];

		public CountElements(IAtomContainer m) {
			for (int i = 0; i < m.getAtomCount(); i++)
				++counts[m.getAtom(i).getAtomicNumber()];
		}

		public int getCount(int atno) {
			return counts[atno];
		}

		public int getCount(String symb) {
			return counts[PeriodicTable.getAtomicNumber(symb)];
		}
	}

	static class CountRings {
		int[][] sssr = {};
		IRingSet ringSet;

		public CountRings(IAtomContainer m) {
			//ringSet = Cycles.sssr(m).toRingSet();  //this CDK 1.5 approach
			SSSRFinder sssrf = new SSSRFinder(m);
			ringSet = sssrf.findSSSR();
		}

		public CountRings(IRingSet ringSet) {           
			this.ringSet = ringSet;
		}

		public int countAnyRing(int size) {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (ring.getAtomCount() == size)
					c++;
			}
			return c;
		}

		private boolean isCarbonOnlyRing(IAtomContainer ring) {
			for (IAtom ringAtom : ring.atoms()) {
				if (!ringAtom.getSymbol().equals("C")) return false;
			}
			return true;
		}

		private boolean isRingSaturated(IAtomContainer ring) {
			for (IBond ringBond : ring.bonds()) {
				if (ringBond.getOrder() != IBond.Order.SINGLE
						|| ringBond.getFlag(CDKConstants.ISAROMATIC)
						/*|| ringBond.getFlag(CDKConstants.SINGLE_OR_DOUBLE) */) return false;
			}
			return true;
		}

		private boolean isRingUnsaturated(IAtomContainer ring) {
			return !isRingSaturated(ring);
		}

		private int countNitrogenInRing(IAtomContainer ring) {
			int c = 0;
			for (IAtom ringAtom : ring.atoms()) {
				if (ringAtom.getSymbol().equals("N")) c++;
			}
			return c;
		}

		private int countHeteroInRing(IAtomContainer ring) {
			int c = 0;
			for (IAtom ringAtom : ring.atoms()) {
				if (!ringAtom.getSymbol().equals("C") 
						&& !ringAtom.getSymbol().equals("H"))
					c++;
			}
			return c;
		}

		private boolean isAromaticRing(IAtomContainer ring) {
			for (IBond bond : ring.bonds())
				if (!bond.getFlag(CDKConstants.ISAROMATIC)) return false;
			return true;
		}

		public int countAromaticRing() {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (isAromaticRing(ring)) c++;
			}
			return c;
		}

		public int countHeteroAromaticRing() {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (!isCarbonOnlyRing(ring) && isAromaticRing(ring)) c++;
			}
			return c;
		}

		public int countSaturatedOrAromaticCarbonOnlyRing(int size) {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (ring.getAtomCount() == size
						&& isCarbonOnlyRing(ring)
						&& (isRingSaturated(ring) || isAromaticRing(ring)))
					c++;
			}
			return c;
		}

		public int countSaturatedOrAromaticNitrogenContainingRing(int size) {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (ring.getAtomCount() == size
						&& (isRingSaturated(ring) || isAromaticRing(ring))
						&& countNitrogenInRing(ring) > 0)
					++c;
			}
			return c;
		}

		public int countSaturatedOrAromaticHeteroContainingRing(int size) {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (ring.getAtomCount() == size
						&& (isRingSaturated(ring) || isAromaticRing(ring))
						&& countHeteroInRing(ring) > 0)
					++c;
			}
			return c;
		}

		public int countUnsaturatedCarbonOnlyRing(int size) {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (ring.getAtomCount() == size
						&& isRingUnsaturated(ring)
						&& !isAromaticRing(ring)
						&& isCarbonOnlyRing(ring))
					++c;
			}
			return c;
		}

		public int countUnsaturatedNitrogenContainingRing(int size) {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (ring.getAtomCount() == size
						&& isRingUnsaturated(ring)
						&& !isAromaticRing(ring)
						&& countNitrogenInRing(ring) > 0)
					++c;
			}
			return c;
		}

		public int countUnsaturatedHeteroContainingRing(int size) {
			int c = 0;
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (ring.getAtomCount() == size
						&& isRingUnsaturated(ring)
						&& !isAromaticRing(ring)
						&& countHeteroInRing(ring) > 0)
					++c;
			}
			return c;
		}
	}

	class PubChemBitSubstructure
	{
		private int bitNum;
		private String smarts;
		private IQueryAtomContainer smartsQuery;	
		private List<QuerySequenceElement> sequence;

		public int getBitNum() {
			return bitNum;
		}
		public void setBitNum(int bitNum) {
			this.bitNum = bitNum;
		}
		public String getSmarts() {
			return smarts;
		}
		public void setSmarts(String smarts) {
			this.smarts = smarts;
		}
		public IQueryAtomContainer getSmartsQuery() {
			return smartsQuery;
		}
		public void setSmartsQuery(IQueryAtomContainer smartsQuery) {
			this.smartsQuery = smartsQuery;
		}
		public List<QuerySequenceElement> getSequence() {
			return sequence;
		}
		public void setSequence(List<QuerySequenceElement> sequence) {
			this.sequence = sequence;
		}
	}

	private void addPubChemBitSubstructure(int bit, String smarts)
	{
		PubChemBitSubstructure pcbs = new PubChemBitSubstructure();
		pcbs.setBitNum(bit);
		pcbs.setSmarts(smarts);
		IQueryAtomContainer query = parser.parse(smarts); 
		//parser.setNeededDataFlags() is not called. This should not be needed for the key smarts queries
		//TODO - Probably only H info would be enough
		pcbs.setSmartsQuery(query);
		isoTester.setQuery(query);
		List<QuerySequenceElement> sequence = isoTester.transferSequenceToOwner();
		pcbs.setSequence(sequence);
		bitSubstructures.add(pcbs);
	}

	private void _generateFp(byte[] fp, IAtomContainer mol)
			throws CDKException {
		countElements(fp, mol);
		countRings(fp, mol);
		countSubstructures(fp, mol);
	}

	private void generateFp(IAtomContainer mol) throws CDKException {
		for (int i = 0; i < m_bits.length; ++i) {
			m_bits[i] = 0;
		}
		_generateFp(m_bits, mol);
	}

	private boolean isBitOn(int bit) {
		return (m_bits[bit >> 3] & MASK[bit % 8]) != 0;
	}

	/**
	 * Returns the fingerprint generated for a molecule as a byte[].
	 * <p/>
	 * Note that this should be immediately called after calling
	 * {@link #getBitFingerprint(org.openscience.cdk.interfaces.IAtomContainer)}
	 *
	 * @return The fingerprint as a byte array
	 * @see #getBitFingerprint(org.openscience.cdk.interfaces.IAtomContainer)
	 */  
	public byte[] getFingerprintAsBytes() {
		return m_bits;
	}

	/**
	 * Returns a fingerprint from a Base64 encoded Pubchem fingerprint.
	 *
	 * @param enc The Base64 encoded fingerprint
	 * @return A BitSet corresponding to the input fingerprint
	 */   
	public static BitSet decode(String enc) {
		byte[] fp = base64Decode(enc);
		if (fp.length < 4) {
			throw new IllegalArgumentException(
					"Input is not a proper PubChem base64 encoded fingerprint");
		}

		int len = (fp[0] << 24) | (fp[1] << 16)
				| (fp[2] << 8) | (fp[3] & 0xff);
		if (len != FP_SIZE) {
			throw new IllegalArgumentException(
					"Input is not a proper PubChem base64 encoded fingerprint");
		}

		// note the IChemObjectBuilder is passed as null because the SMARTSQueryTool
		// isn't needed when decoding
		PubChemFingerprinter pc = new PubChemFingerprinter();
		for (int i = 0; i < pc.m_bits.length; ++i) {
			pc.m_bits[i] = fp[i + 4];
		}

		BitSet ret = new BitSet(FP_SIZE);
		for (int i = 0; i < FP_SIZE; i++) {
			if (pc.isBitOn(i)) ret.set(i);
		}
		return ret;
	}


	// the first four bytes contains the length of the fingerprint
	private String encode() {
		byte[] pack = new byte[4 + m_bits.length];

		pack[0] = (byte) ((FP_SIZE & 0xffffffff) >> 24);
		pack[1] = (byte) ((FP_SIZE & 0x00ffffff) >> 16);
		pack[2] = (byte) ((FP_SIZE & 0x0000ffff) >> 8);
		pack[3] = (byte) (FP_SIZE & 0x000000ff);
		for (int i = 0; i < m_bits.length; ++i) {
			pack[i + 4] = m_bits[i];
		}
		return base64Encode(pack);
	}

	private static String BASE64_LUT =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
					"abcdefghijklmnopqrstuvwxyz0123456789+/=";

	// based on NCBI C implementation
	private static String base64Encode(byte[] data) {
		char c64[] = new char[data.length * 4 / 3 + 5];
		for (int i = 0, k = 0; i < data.length; i += 3, k += 4) {
			c64[k + 0] = (char) (data[i] >> 2);
			c64[k + 1] = (char) ((data[i] & 0x03) << 4);
			c64[k + 2] = c64[k + 3] = 64;
			if ((i + i) < data.length) {
				c64[k + 1] |= data[i + 1] >> 4;
			c64[k + 2] = (char) ((data[i + 1] & 0x0f) << 2);
			}
			if ((i + 2) < data.length) {
				c64[k + 2] |= data[i + 2] >> 6;
			c64[k + 3] = (char) (data[i + 2] & 0x3f);
			}
			for (int j = 0; j < 4; ++j) {
				c64[k + j] = BASE64_LUT.charAt(c64[k + j]);
			}
		}
		return new String(c64);
	}

	// based on NCBI C implementation
	private static byte[] base64Decode(String data) {
		int len = data.length();

		byte[] b64 = new byte[len * 3 / 4];
		byte[] buf = new byte[4];
		boolean done = false;

		for (int i = 0, j, k = 0; i < len && !done;) {
			buf[0] = buf[1] = buf[2] = buf[3] = 0;
			for (j = 0; j < 4 && i < len; ++j) {
				char c = data.charAt(i);
				if (c >= 'A' && c <= 'Z') {
					c -= 'A';
				} else if (c >= 'a' && c <= 'z') {
					c = (char) (c - 'a' + 26);
				} else if (c >= '0' && c <= '9') {
					c = (char) (c - '0' + 52);
				} else if (c == '+') {
					c = 62;
				} else if (c == '/') {
					c = 63;
				} else if (c == '=' || c == '-') {
					done = true;
					break;
				} else {
					++i;
					--j;
					continue;
				}
				buf[j] = (byte) c;
				++i;
			}

			if (k < b64.length && j >= 1) {
				b64[k++] = (byte) ((buf[0] << 2) | ((buf[1] & 0x30) >> 4));
			}
			if (k < b64.length && j >= 3) {
				b64[k++] = (byte) (((buf[1] & 0x0f) << 4)
						| ((buf[2] & 0x3c) >> 2));
			}
			if (k < b64.length && j >= 4) {
				b64[k++] = (byte) (((buf[2] & 0x03) << 6) | (buf[3] & 0x3f));
			}
		}
		return b64;
	}

	static final int BITCOUNT[] = {
		0, 1, 1, 2, 1, 2, 2, 3,
		1, 2, 2, 3, 2, 3, 3, 4,
		1, 2, 2, 3, 2, 3, 3, 4,
		2, 3, 3, 4, 3, 4, 4, 5,
		1, 2, 2, 3, 2, 3, 3, 4,
		2, 3, 3, 4, 3, 4, 4, 5,
		2, 3, 3, 4, 3, 4, 4, 5,
		3, 4, 4, 5, 4, 5, 5, 6,
		1, 2, 2, 3, 2, 3, 3, 4,
		2, 3, 3, 4, 3, 4, 4, 5,
		2, 3, 3, 4, 3, 4, 4, 5,
		3, 4, 4, 5, 4, 5, 5, 6,
		2, 3, 3, 4, 3, 4, 4, 5,
		3, 4, 4, 5, 4, 5, 5, 6,
		3, 4, 4, 5, 4, 5, 5, 6,
		4, 5, 5, 6, 5, 6, 6, 7,
		1, 2, 2, 3, 2, 3, 3, 4,
		2, 3, 3, 4, 3, 4, 4, 5,
		2, 3, 3, 4, 3, 4, 4, 5,
		3, 4, 4, 5, 4, 5, 5, 6,
		2, 3, 3, 4, 3, 4, 4, 5,
		3, 4, 4, 5, 4, 5, 5, 6,
		3, 4, 4, 5, 4, 5, 5, 6,
		4, 5, 5, 6, 5, 6, 6, 7,
		2, 3, 3, 4, 3, 4, 4, 5,
		3, 4, 4, 5, 4, 5, 5, 6,
		3, 4, 4, 5, 4, 5, 5, 6,
		4, 5, 5, 6, 5, 6, 6, 7,
		3, 4, 4, 5, 4, 5, 5, 6,
		4, 5, 5, 6, 5, 6, 6, 7,
		4, 5, 5, 6, 5, 6, 6, 7,
		5, 6, 6, 7, 6, 7, 7, 8
	};

	static final int MASK[] = {
		0x80,
		0x40,
		0x20,
		0x10,
		0x08,
		0x04,
		0x02,
		0x01
	};

	/*
     Section 1: Hierarchic Element Counts - These bs test for the
     presence or count of individual chemical atoms represented
     by their atomic symbol.
	 */
	private static void countElements(byte[] fp, IAtomContainer mol) {
		int b;
		CountElements ce = new CountElements(mol);

		b = 0;
		if (ce.getCount("H") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 1;
		if (ce.getCount("H") >= 8) fp[b >> 3] |= MASK[b % 8];
		b = 2;
		if (ce.getCount("H") >= 16) fp[b >> 3] |= MASK[b % 8];
		b = 3;
		if (ce.getCount("H") >= 32) fp[b >> 3] |= MASK[b % 8];
		b = 4;
		if (ce.getCount("Li") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 5;
		if (ce.getCount("Li") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 6;
		if (ce.getCount("B") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 7;
		if (ce.getCount("B") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 8;
		if (ce.getCount("B") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 9;
		if (ce.getCount("C") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 10;
		if (ce.getCount("C") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 11;
		if (ce.getCount("C") >= 8) fp[b >> 3] |= MASK[b % 8];
		b = 12;
		if (ce.getCount("C") >= 16) fp[b >> 3] |= MASK[b % 8];
		b = 13;
		if (ce.getCount("C") >= 32) fp[b >> 3] |= MASK[b % 8];
		b = 14;
		if (ce.getCount("N") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 15;
		if (ce.getCount("N") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 16;
		if (ce.getCount("N") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 17;
		if (ce.getCount("N") >= 8) fp[b >> 3] |= MASK[b % 8];
		b = 18;
		if (ce.getCount("O") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 19;
		if (ce.getCount("O") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 20;
		if (ce.getCount("O") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 21;
		if (ce.getCount("O") >= 8) fp[b >> 3] |= MASK[b % 8];
		b = 22;
		if (ce.getCount("O") >= 16) fp[b >> 3] |= MASK[b % 8];
		b = 23;
		if (ce.getCount("F") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 24;
		if (ce.getCount("F") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 25;
		if (ce.getCount("F") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 26;
		if (ce.getCount("Na") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 27;
		if (ce.getCount("Na") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 28;
		if (ce.getCount("Si") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 29;
		if (ce.getCount("Si") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 30;
		if (ce.getCount("P") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 31;
		if (ce.getCount("P") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 32;
		if (ce.getCount("P") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 33;
		if (ce.getCount("S") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 34;
		if (ce.getCount("S") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 35;
		if (ce.getCount("S") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 36;
		if (ce.getCount("S") >= 8) fp[b >> 3] |= MASK[b % 8];
		b = 37;
		if (ce.getCount("Cl") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 38;
		if (ce.getCount("Cl") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 39;
		if (ce.getCount("Cl") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 40;
		if (ce.getCount("Cl") >= 8) fp[b >> 3] |= MASK[b % 8];
		b = 41;
		if (ce.getCount("K") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 42;
		if (ce.getCount("K") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 43;
		if (ce.getCount("Br") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 44;
		if (ce.getCount("Br") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 45;
		if (ce.getCount("Br") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 46;
		if (ce.getCount("I") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 47;
		if (ce.getCount("I") >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 48;
		if (ce.getCount("I") >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 49;
		if (ce.getCount("Be") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 50;
		if (ce.getCount("Mg") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 51;
		if (ce.getCount("Al") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 52;
		if (ce.getCount("Ca") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 53;
		if (ce.getCount("Sc") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 54;
		if (ce.getCount("Ti") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 55;
		if (ce.getCount("V") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 56;
		if (ce.getCount("Cr") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 57;
		if (ce.getCount("Mn") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 58;
		if (ce.getCount("Fe") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 59;
		if (ce.getCount("Co") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 60;
		if (ce.getCount("Ni") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 61;
		if (ce.getCount("Cu") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 62;
		if (ce.getCount("Zn") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 63;
		if (ce.getCount("Ga") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 64;
		if (ce.getCount("Ge") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 65;
		if (ce.getCount("As") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 66;
		if (ce.getCount("Se") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 67;
		if (ce.getCount("Kr") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 68;
		if (ce.getCount("Rb") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 69;
		if (ce.getCount("Sr") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 70;
		if (ce.getCount("Y") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 71;
		if (ce.getCount("Zr") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 72;
		if (ce.getCount("Nb") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 73;
		if (ce.getCount("Mo") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 74;
		if (ce.getCount("Ru") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 75;
		if (ce.getCount("Rh") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 76;
		if (ce.getCount("Pd") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 77;
		if (ce.getCount("Ag") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 78;
		if (ce.getCount("Cd") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 79;
		if (ce.getCount("In") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 80;
		if (ce.getCount("Sn") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 81;
		if (ce.getCount("Sb") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 82;
		if (ce.getCount("Te") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 83;
		if (ce.getCount("Xe") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 84;
		if (ce.getCount("Cs") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 85;
		if (ce.getCount("Ba") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 86;
		if (ce.getCount("Lu") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 87;
		if (ce.getCount("Hf") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 88;
		if (ce.getCount("Ta") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 89;
		if (ce.getCount("W") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 90;
		if (ce.getCount("Re") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 91;
		if (ce.getCount("Os") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 92;
		if (ce.getCount("Ir") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 93;
		if (ce.getCount("Pt") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 94;
		if (ce.getCount("Au") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 95;
		if (ce.getCount("Hg") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 96;
		if (ce.getCount("Tl") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 97;
		if (ce.getCount("Pb") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 98;
		if (ce.getCount("Bi") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 99;
		if (ce.getCount("La") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 100;
		if (ce.getCount("Ce") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 101;
		if (ce.getCount("Pr") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 102;
		if (ce.getCount("Nd") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 103;
		if (ce.getCount("Pm") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 104;
		if (ce.getCount("Sm") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 105;
		if (ce.getCount("Eu") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 106;
		if (ce.getCount("Gd") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 107;
		if (ce.getCount("Tb") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 108;
		if (ce.getCount("Dy") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 109;
		if (ce.getCount("Ho") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 110;
		if (ce.getCount("Er") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 111;
		if (ce.getCount("Tm") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 112;
		if (ce.getCount("Yb") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 113;
		if (ce.getCount("Tc") >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 114;
		if (ce.getCount("U") >= 1) fp[b >> 3] |= MASK[b % 8];
	}

	/*
      Section 2: Rings in a canonic ESSR ring set-These bs test for the
      presence or count of the described chemical ring system.
      An ESSR ring is any ring which does not share three
      consecutive atoms with any other ring in the chemical
      structure.  For example, naphthalene has three ESSR rings
      (two phenyl fragments and the 10-membered envelope), while
      biphenyl will yield a count of only two ESSR rings.
	 */
	private static void countRings(byte[] fp, IAtomContainer mol) {
		CountRings cr = new CountRings(mol);
		int b;

		b = 115;
		if (cr.countAnyRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 116;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 117;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 118;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 119;
		if (cr.countUnsaturatedCarbonOnlyRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 120;
		if (cr.countUnsaturatedNitrogenContainingRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 121;
		if (cr.countUnsaturatedHeteroContainingRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];

		b = 122;
		if (cr.countAnyRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 123;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 124;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 125;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 126;
		if (cr.countUnsaturatedCarbonOnlyRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 127;
		if (cr.countUnsaturatedNitrogenContainingRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 128;
		if (cr.countUnsaturatedHeteroContainingRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];

		b = 129;
		if (cr.countAnyRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 130;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 131;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 132;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 133;
		if (cr.countUnsaturatedCarbonOnlyRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 134;
		if (cr.countUnsaturatedNitrogenContainingRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 135;
		if (cr.countUnsaturatedHeteroContainingRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];

		b = 136;
		if (cr.countAnyRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 137;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 138;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 139;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 140;
		if (cr.countUnsaturatedCarbonOnlyRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 141;
		if (cr.countUnsaturatedNitrogenContainingRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 142;
		if (cr.countUnsaturatedHeteroContainingRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];

		b = 143;
		if (cr.countAnyRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 144;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 145;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 146;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 147;
		if (cr.countUnsaturatedCarbonOnlyRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 148;
		if (cr.countUnsaturatedNitrogenContainingRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 149;
		if (cr.countUnsaturatedHeteroContainingRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];

		b = 150;
		if (cr.countAnyRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 151;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 152;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 153;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 154;
		if (cr.countUnsaturatedCarbonOnlyRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 155;
		if (cr.countUnsaturatedNitrogenContainingRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 156;
		if (cr.countUnsaturatedHeteroContainingRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];

		b = 157;
		if (cr.countAnyRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 158;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 159;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 160;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 161;
		if (cr.countUnsaturatedCarbonOnlyRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 162;
		if (cr.countUnsaturatedNitrogenContainingRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 163;
		if (cr.countUnsaturatedHeteroContainingRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];

		b = 164;
		if (cr.countAnyRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 165;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 166;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 167;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 168;
		if (cr.countUnsaturatedCarbonOnlyRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 169;
		if (cr.countUnsaturatedNitrogenContainingRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 170;
		if (cr.countUnsaturatedHeteroContainingRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];

		b = 171;
		if (cr.countAnyRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 172;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 173;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 174;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 175;
		if (cr.countUnsaturatedCarbonOnlyRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 176;
		if (cr.countUnsaturatedNitrogenContainingRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 177;
		if (cr.countUnsaturatedHeteroContainingRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];

		b = 178;
		if (cr.countAnyRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 179;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 180;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 181;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 182;
		if (cr.countUnsaturatedCarbonOnlyRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 183;
		if (cr.countUnsaturatedNitrogenContainingRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 184;
		if (cr.countUnsaturatedHeteroContainingRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];

		b = 185;
		if (cr.countAnyRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 186;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 187;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 188;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 189;
		if (cr.countUnsaturatedCarbonOnlyRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 190;
		if (cr.countUnsaturatedNitrogenContainingRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 191;
		if (cr.countUnsaturatedHeteroContainingRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];

		b = 192;
		if (cr.countAnyRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 193;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 194;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 195;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 196;
		if (cr.countUnsaturatedCarbonOnlyRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 197;
		if (cr.countUnsaturatedNitrogenContainingRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 198;
		if (cr.countUnsaturatedHeteroContainingRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];

		b = 199;
		if (cr.countAnyRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 200;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 201;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 202;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 203;
		if (cr.countUnsaturatedCarbonOnlyRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 204;
		if (cr.countUnsaturatedNitrogenContainingRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 205;
		if (cr.countUnsaturatedHeteroContainingRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];

		b = 206;
		if (cr.countAnyRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 207;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 208;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 209;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 210;
		if (cr.countUnsaturatedCarbonOnlyRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 211;
		if (cr.countUnsaturatedNitrogenContainingRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
		b = 212;
		if (cr.countUnsaturatedHeteroContainingRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];

		b = 213;
		if (cr.countAnyRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 214;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 215;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 216;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 217;
		if (cr.countUnsaturatedCarbonOnlyRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 218;
		if (cr.countUnsaturatedNitrogenContainingRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 219;
		if (cr.countUnsaturatedHeteroContainingRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];

		b = 220;
		if (cr.countAnyRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 221;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 222;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 223;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 224;
		if (cr.countUnsaturatedCarbonOnlyRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 225;
		if (cr.countUnsaturatedNitrogenContainingRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 226;
		if (cr.countUnsaturatedHeteroContainingRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];

		b = 227;
		if (cr.countAnyRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 228;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 229;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 230;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 231;
		if (cr.countUnsaturatedCarbonOnlyRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 232;
		if (cr.countUnsaturatedNitrogenContainingRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 233;
		if (cr.countUnsaturatedHeteroContainingRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];

		b = 234;
		if (cr.countAnyRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 235;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 236;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 237;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 238;
		if (cr.countUnsaturatedCarbonOnlyRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 239;
		if (cr.countUnsaturatedNitrogenContainingRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 240;
		if (cr.countUnsaturatedHeteroContainingRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];

		b = 241;
		if (cr.countAnyRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 242;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 243;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 244;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 245;
		if (cr.countUnsaturatedCarbonOnlyRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 246;
		if (cr.countUnsaturatedNitrogenContainingRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 247;
		if (cr.countUnsaturatedHeteroContainingRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];

		b = 248;
		if (cr.countAnyRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 249;
		if (cr.countSaturatedOrAromaticCarbonOnlyRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 250;
		if (cr.countSaturatedOrAromaticNitrogenContainingRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 251;
		if (cr.countSaturatedOrAromaticHeteroContainingRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 252;
		if (cr.countUnsaturatedCarbonOnlyRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 253;
		if (cr.countUnsaturatedNitrogenContainingRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 254;
		if (cr.countUnsaturatedHeteroContainingRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];

		b = 255;
		if (cr.countAromaticRing() >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 256;
		if (cr.countHeteroAromaticRing() >= 1) fp[b >> 3] |= MASK[b % 8];
		b = 257;
		if (cr.countAromaticRing() >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 258;
		if (cr.countHeteroAromaticRing() >= 2) fp[b >> 3] |= MASK[b % 8];
		b = 259;
		if (cr.countAromaticRing() >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 260;
		if (cr.countHeteroAromaticRing() >= 3) fp[b >> 3] |= MASK[b % 8];
		b = 261;
		if (cr.countAromaticRing() >= 4) fp[b >> 3] |= MASK[b % 8];
		b = 262;
		if (cr.countHeteroAromaticRing() >= 4) fp[b >> 3] |= MASK[b % 8];
	}

	private void countSubstructures(byte[] fp, IAtomContainer mol) throws CDKException 
	{	
		//H info is needed for some keys
		SmartsParser.setExplicitHAtomData(mol);
		
		//Calculates PubChem sections: 3,4,5,6,7 set up in 
		boolean res;
		for (PubChemBitSubstructure bs: bitSubstructures)
		{	
			isoTester.setSequence(bs.getSmartsQuery(), bs.getSequence());
			res = isoTester.hasIsomorphism(mol);
			if (res)
				fp[bs.getBitNum() >> 3] |= MASK[bs.getBitNum() % 8];
		}	
	}



	private void initPubChemBitSubstructures()
	{	
		//Section 3: Simple atom pairs. These bits test for the presence
		//of patterns of bonded atom pairs, regardless of bond order or
		//count.
		addPubChemBitSubstructure(263, "[Li&!H0]");
		addPubChemBitSubstructure(264, "[Li]~[Li]");
		addPubChemBitSubstructure(265, "[Li]~[#5]");
		addPubChemBitSubstructure(266, "[Li]~[#6]");
		addPubChemBitSubstructure(267, "[Li]~[#8]");
		addPubChemBitSubstructure(268, "[Li]~[F]");
		addPubChemBitSubstructure(269, "[Li]~[#15]");
		addPubChemBitSubstructure(270, "[Li]~[#16]");
		addPubChemBitSubstructure(271, "[Li]~[Cl]");
		addPubChemBitSubstructure(272, "[#5&!H0]");
		addPubChemBitSubstructure(273, "[#5]~[#5]");
		addPubChemBitSubstructure(274, "[#5]~[#6]");
		addPubChemBitSubstructure(275, "[#5]~[#7]");
		addPubChemBitSubstructure(276, "[#5]~[#8]");
		addPubChemBitSubstructure(277, "[#5]~[F]");
		addPubChemBitSubstructure(278, "[#5]~[#14]");
		addPubChemBitSubstructure(279, "[#5]~[#15]");
		addPubChemBitSubstructure(280, "[#5]~[#16]");
		addPubChemBitSubstructure(281, "[#5]~[Cl]");
		addPubChemBitSubstructure(282, "[#5]~[Br]");
		addPubChemBitSubstructure(283, "[#6&!H0]");
		addPubChemBitSubstructure(284, "[#6]~[#6]");
		addPubChemBitSubstructure(285, "[#6]~[#7]");
		addPubChemBitSubstructure(286, "[#6]~[#8]");
		addPubChemBitSubstructure(287, "[#6]~[F]");
		addPubChemBitSubstructure(288, "[#6]~[Na]");
		addPubChemBitSubstructure(289, "[#6]~[Mg]");
		addPubChemBitSubstructure(290, "[#6]~[Al]");
		addPubChemBitSubstructure(291, "[#6]~[#14]");
		addPubChemBitSubstructure(292, "[#6]~[#15]");
		addPubChemBitSubstructure(293, "[#6]~[#16]");
		addPubChemBitSubstructure(294, "[#6]~[Cl]");
		addPubChemBitSubstructure(295, "[#6]~[#33]");
		addPubChemBitSubstructure(296, "[#6]~[#34]");
		addPubChemBitSubstructure(297, "[#6]~[Br]");
		addPubChemBitSubstructure(298, "[#6]~[I]");
		addPubChemBitSubstructure(299, "[#7&!H0]");
		addPubChemBitSubstructure(300, "[#7]~[#7]");
		addPubChemBitSubstructure(301, "[#7]~[#8]");
		addPubChemBitSubstructure(302, "[#7]~[F]");
		addPubChemBitSubstructure(303, "[#7]~[#14]");
		addPubChemBitSubstructure(304, "[#7]~[#15]");
		addPubChemBitSubstructure(305, "[#7]~[#16]");
		addPubChemBitSubstructure(306, "[#7]~[Cl]");
		addPubChemBitSubstructure(307, "[#7]~[Br]");
		addPubChemBitSubstructure(308, "[#8&!H0]");
		addPubChemBitSubstructure(309, "[#8]~[#8]");
		addPubChemBitSubstructure(310, "[#8]~[Mg]");
		addPubChemBitSubstructure(311, "[#8]~[Na]");
		addPubChemBitSubstructure(312, "[#8]~[Al]");
		addPubChemBitSubstructure(313, "[#8]~[#14]");
		addPubChemBitSubstructure(314, "[#8]~[#15]");
		addPubChemBitSubstructure(315, "[#8]~[K]");
		addPubChemBitSubstructure(316, "[F]~[#15]");
		addPubChemBitSubstructure(317, "[F]~[#16]");
		addPubChemBitSubstructure(318, "[Al&!H0]");
		addPubChemBitSubstructure(319, "[Al]~[Cl]");
		addPubChemBitSubstructure(320, "[#14&!H0]");
		addPubChemBitSubstructure(321, "[#14]~[#14]");
		addPubChemBitSubstructure(322, "[#14]~[Cl]");
		addPubChemBitSubstructure(323, "[#15&!H0]");
		addPubChemBitSubstructure(324, "[#15]~[#15]");
		addPubChemBitSubstructure(325, "[#33&!H0]");
		addPubChemBitSubstructure(326, "[#33]~[#33]");

		//Section 4: Simple atom nearest neighbors.  These bits test for the
		//presence of atom nearest neighbor patterns, regardless of
		//bond order or count, but where bond aromaticity (denoted by
		//"~") is significant.
		addPubChemBitSubstructure(327, "[#6](~Br)(~[#6])");
		addPubChemBitSubstructure(328, "[#6](~Br)(~[#6])(~[#6])");
		addPubChemBitSubstructure(329, "[#6&!H0]~[Br]");
		addPubChemBitSubstructure(330, "[#6](~[Br])(:[c])");
		addPubChemBitSubstructure(331, "[#6](~[Br])(:[n])");
		addPubChemBitSubstructure(332, "[#6](~[#6])(~[#6])");
		addPubChemBitSubstructure(333, "[#6](~[#6])(~[#6])(~[#6])");
		addPubChemBitSubstructure(334, "[#6](~[#6])(~[#6])(~[#6])(~[#6])");
		addPubChemBitSubstructure(335, "[#6H1](~[#6])(~[#6])(~[#6])");
		addPubChemBitSubstructure(336, "[#6](~[#6])(~[#6])(~[#6])(~[#7])");
		addPubChemBitSubstructure(337, "[#6](~[#6])(~[#6])(~[#6])(~[#8])");
		addPubChemBitSubstructure(338, "[#6H1](~[#6])(~[#6])(~[#7])");
		addPubChemBitSubstructure(339, "[#6H1](~[#6])(~[#6])(~[#8])");
		addPubChemBitSubstructure(340, "[#6](~[#6])(~[#6])(~[#7])");
		addPubChemBitSubstructure(341, "[#6](~[#6])(~[#6])(~[#8])");
		addPubChemBitSubstructure(342, "[#6](~[#6])(~[Cl])");
		addPubChemBitSubstructure(343, "[#6&!H0](~[#6])(~[Cl])");
		addPubChemBitSubstructure(344, "[#6H,#6H2,#6H3,#6H4]~[#6]");
		addPubChemBitSubstructure(345, "[#6&!H0](~[#6])(~[#7])");
		addPubChemBitSubstructure(346, "[#6&!H0](~[#6])(~[#8])");
		addPubChemBitSubstructure(347, "[#6H1](~[#6])(~[#8])(~[#8])");
		addPubChemBitSubstructure(348, "[#6&!H0](~[#6])(~[#15])");
		addPubChemBitSubstructure(349, "[#6&!H0](~[#6])(~[#16])");
		addPubChemBitSubstructure(350, "[#6](~[#6])(~[I])");
		addPubChemBitSubstructure(351, "[#6](~[#6])(~[#7])");
		addPubChemBitSubstructure(352, "[#6](~[#6])(~[#8])");
		addPubChemBitSubstructure(353, "[#6](~[#6])(~[#16])");
		addPubChemBitSubstructure(354, "[#6](~[#6])(~[#14])");
		addPubChemBitSubstructure(355, "[#6](~[#6])(:c)");
		addPubChemBitSubstructure(356, "[#6](~[#6])(:c)(:c)");
		addPubChemBitSubstructure(357, "[#6](~[#6])(:c)(:n)");
		addPubChemBitSubstructure(358, "[#6](~[#6])(:n)");
		addPubChemBitSubstructure(359, "[#6](~[#6])(:n)(:n)");
		addPubChemBitSubstructure(360, "[#6](~[Cl])(~[Cl])");
		addPubChemBitSubstructure(361, "[#6&!H0](~[Cl])");
		addPubChemBitSubstructure(362, "[#6](~[Cl])(:c)");
		addPubChemBitSubstructure(363, "[#6](~[F])(~[F])");
		addPubChemBitSubstructure(364, "[#6](~[F])(:c)");
		addPubChemBitSubstructure(365, "[#6&!H0](~[#7])");
		addPubChemBitSubstructure(366, "[#6&!H0](~[#8])");
		addPubChemBitSubstructure(367, "[#6&!H0](~[#8])(~[#8])");
		addPubChemBitSubstructure(368, "[#6&!H0](~[#16])");
		addPubChemBitSubstructure(369, "[#6&!H0](~[#14])");
		addPubChemBitSubstructure(370, "[#6&!H0]:c");
		addPubChemBitSubstructure(371, "[#6&!H0](:c)(:c)");
		addPubChemBitSubstructure(372, "[#6&!H0](:c)(:n)");
		addPubChemBitSubstructure(373, "[#6&!H0](:n)");
		addPubChemBitSubstructure(374, "[#6H3]");
		addPubChemBitSubstructure(375, "[#6](~[#7])(~[#7])");
		addPubChemBitSubstructure(376, "[#6](~[#7])(:c)");
		addPubChemBitSubstructure(377, "[#6](~[#7])(:c)(:c)");
		addPubChemBitSubstructure(378, "[#6](~[#7])(:c)(:n)");
		addPubChemBitSubstructure(379, "[#6](~[#7])(:n)");
		addPubChemBitSubstructure(380, "[#6](~[#8])(~[#8])");
		addPubChemBitSubstructure(381, "[#6](~[#8])(:c)");
		addPubChemBitSubstructure(382, "[#6](~[#8])(:c)(:c)");
		addPubChemBitSubstructure(383, "[#6](~[#16])(:c)");
		addPubChemBitSubstructure(384, "[#6](:c)(:c)");
		addPubChemBitSubstructure(385, "[#6](:c)(:c)(:c)");
		addPubChemBitSubstructure(386, "[#6](:c)(:c)(:n)");
		addPubChemBitSubstructure(387, "[#6](:c)(:n)");
		addPubChemBitSubstructure(388, "[#6](:c)(:n)(:n)");
		addPubChemBitSubstructure(389, "[#6](:n)(:n)");
		addPubChemBitSubstructure(390, "[#7](~[#6])(~[#6])");
		addPubChemBitSubstructure(391, "[#7](~[#6])(~[#6])(~[#6])");
		addPubChemBitSubstructure(392, "[#7&!H0](~[#6])(~[#6])");
		addPubChemBitSubstructure(393, "[#7&!H0](~[#6])");
		addPubChemBitSubstructure(394, "[#7&!H0](~[#6])(~[#7])");
		addPubChemBitSubstructure(395, "[#7](~[#6])(~[#8])");
		addPubChemBitSubstructure(396, "[#7](~[#6])(:c)");
		addPubChemBitSubstructure(397, "[#7](~[#6])(:c)(:c)");
		addPubChemBitSubstructure(398, "[#7&!H0](~[#7])");
		addPubChemBitSubstructure(399, "[#7&!H0](:c)");
		addPubChemBitSubstructure(400, "[#7&!H0](:c)(:c)");
		addPubChemBitSubstructure(401, "[#7](~[#8])(~[#8])");
		addPubChemBitSubstructure(402, "[#7](~[#8])(:o)");
		addPubChemBitSubstructure(403, "[#7](:c)(:c)");
		addPubChemBitSubstructure(404, "[#7](:c)(:c)(:c)");
		addPubChemBitSubstructure(405, "[#8](~[#6])(~[#6])");
		addPubChemBitSubstructure(406, "[#8&!H0](~[#6])");
		addPubChemBitSubstructure(407, "[#8](~[#6])(~[#15])");
		addPubChemBitSubstructure(408, "[#8&!H0](~[#16])");
		addPubChemBitSubstructure(409, "[#8](:c)(:c)");
		addPubChemBitSubstructure(410, "[#15](~[#6])(~[#6])");
		addPubChemBitSubstructure(411, "[#15](~[#8])(~[#8])");
		addPubChemBitSubstructure(412, "[#16](~[#6])(~[#6])");
		addPubChemBitSubstructure(413, "[#16&!H0](~[#6])");
		addPubChemBitSubstructure(414, "[#16](~[#6])(~[#8])");
		addPubChemBitSubstructure(415, "[#14](~[#6])(~[#6])");


		//Section 5: Detailed atom neighborhoods - These bits test for the
		//presence of detailed atom neighborhood patterns, regardless
		//of count, but where bond orders are specific, bond
		//aromaticity matches both single and double bonds, and where
		//"-", "=", and "#" matches a single bond, double bond, and
		//triple bond order, respectively.
		addPubChemBitSubstructure(416, "[#6]=,:[#6]");
		addPubChemBitSubstructure(417, "[#6]#[#6]");
		addPubChemBitSubstructure(418, "[#6]=,:[#7]");
		addPubChemBitSubstructure(419, "[#6]#[#7]");
		addPubChemBitSubstructure(420, "[#6]=,:[#8]");
		addPubChemBitSubstructure(421, "[#6]=,:[#16]");
		addPubChemBitSubstructure(422, "[#7]=,:[#7]");
		addPubChemBitSubstructure(423, "[#7]=,:[#8]");
		addPubChemBitSubstructure(424, "[#7]=,:[#15]");
		addPubChemBitSubstructure(425, "[#15]=,:[#8]");
		addPubChemBitSubstructure(426, "[#15]=,:[#15]");
		addPubChemBitSubstructure(427, "[#6](#[#6])(-,:[#6])");
		addPubChemBitSubstructure(428, "[#6&!H0](#[#6])");
		addPubChemBitSubstructure(429, "[#6](#[#7])(-,:[#6])");
		addPubChemBitSubstructure(430, "[#6](-,:[#6])(-,:[#6])(=,:[#6])");
		addPubChemBitSubstructure(431, "[#6](-,:[#6])(-,:[#6])(=,:[#7])");
		addPubChemBitSubstructure(432, "[#6](-,:[#6])(-,:[#6])(=,:[#8])");
		addPubChemBitSubstructure(433, "[#6](-,:[#6])([Cl])(=,:[#8])");
		addPubChemBitSubstructure(434, "[#6&!H0](-,:[#6])(=,:[#6])");
		addPubChemBitSubstructure(435, "[#6&!H0](-,:[#6])(=,:[#7])");
		addPubChemBitSubstructure(436, "[#6&!H0](-,:[#6])(=,:[#8])");
		addPubChemBitSubstructure(437, "[#6](-,:[#6])(-,:[#7])(=,:[#6])");
		addPubChemBitSubstructure(438, "[#6](-,:[#6])(-,:[#7])(=,:[#7])");
		addPubChemBitSubstructure(439, "[#6](-,:[#6])(-,:[#7])(=,:[#8])");
		addPubChemBitSubstructure(440, "[#6](-,:[#6])(-,:[#8])(=,:[#8])");
		addPubChemBitSubstructure(441, "[#6](-,:[#6])(=,:[#6])");
		addPubChemBitSubstructure(442, "[#6](-,:[#6])(=,:[#7])");
		addPubChemBitSubstructure(443, "[#6](-,:[#6])(=,:[#8])");
		addPubChemBitSubstructure(444, "[#6]([Cl])(=,:[#8])");
		addPubChemBitSubstructure(445, "[#6&!H0](-,:[#7])(=,:[#6])");
		addPubChemBitSubstructure(446, "[#6&!H0](=,:[#6])");
		addPubChemBitSubstructure(447, "[#6&!H0](=,:[#7])");
		addPubChemBitSubstructure(448, "[#6&!H0](=,:[#8])");
		addPubChemBitSubstructure(449, "[#6](-,:[#7])(=,:[#6])");
		addPubChemBitSubstructure(450, "[#6](-,:[#7])(=,:[#7])");
		addPubChemBitSubstructure(451, "[#6](-,:[#7])(=,:[#8])");
		addPubChemBitSubstructure(452, "[#6](-,:[#8])(=,:[#8])");
		addPubChemBitSubstructure(453, "[#7](-,:[#6])(=,:[#6])");
		addPubChemBitSubstructure(454, "[#7](-,:[#6])(=,:[#8])");
		addPubChemBitSubstructure(455, "[#7](-,:[#8])(=,:[#8])");
		addPubChemBitSubstructure(456, "[#15](-,:[#8])(=,:[#8])");
		addPubChemBitSubstructure(457, "[#16](-,:[#6])(=,:[#8])");
		addPubChemBitSubstructure(458, "[#16](-,:[#8])(=,:[#8])");
		addPubChemBitSubstructure(459, "[#16](=,:[#8])(=,:[#8])");


		//Section 6: Simple SMARTS patterns - These bits test for the presence
		//of simple SMARTS patterns, regardless of count, but where
		//bond orders are specific and bond aromaticity matches both
		//single and double bonds.
		addPubChemBitSubstructure(460, "[#6]-,:[#6]-,:[#6]#[#6]");
		addPubChemBitSubstructure(461, "[#8]-,:[#6]-,:[#6]=,:[#7]");
		addPubChemBitSubstructure(462, "[#8]-,:[#6]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(463, "[#7]:[#6]-,:[#16&!H0]");
		addPubChemBitSubstructure(464, "[#7]-,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(465, "[#8]=,:[#16]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(466, "[#7]#[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(467, "[#6]=,:[#7]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(468, "[#8]=,:[#16]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(469, "[#16]-,:[#16]-,:[#6]:[#6]");
		addPubChemBitSubstructure(470, "[#6]:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(471, "[#16]:[#6]:[#6]:[#6]");
		addPubChemBitSubstructure(472, "[#6]:[#7]:[#6]-,:[#6]");
		addPubChemBitSubstructure(473, "[#16]-,:[#6]:[#7]:[#6]");
		addPubChemBitSubstructure(474, "[#16]:[#6]:[#6]:[#7]");
		addPubChemBitSubstructure(475, "[#16]-,:[#6]=,:[#7]-,:[#6]");
		addPubChemBitSubstructure(476, "[#6]-,:[#8]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(477, "[#7]-,:[#7]-,:[#6]:[#6]");
		addPubChemBitSubstructure(478, "[#16]-,:[#6]=,:[#7&!H0]");
		addPubChemBitSubstructure(479, "[#16]-,:[#6]-,:[#16]-,:[#6]");
		addPubChemBitSubstructure(480, "[#6]:[#16]:[#6]-,:[#6]");
		addPubChemBitSubstructure(481, "[#8]-,:[#16]-,:[#6]:[#6]");
		addPubChemBitSubstructure(482, "[#6]:[#7]-,:[#6]:[#6]");
		addPubChemBitSubstructure(483, "[#7]-,:[#16]-,:[#6]:[#6]");
		addPubChemBitSubstructure(484, "[#7]-,:[#6]:[#7]:[#6]");
		addPubChemBitSubstructure(485, "[#7]:[#6]:[#6]:[#7]");
		addPubChemBitSubstructure(486, "[#7]-,:[#6]:[#7]:[#7]");
		addPubChemBitSubstructure(487, "[#7]-,:[#6]=,:[#7]-,:[#6]");
		addPubChemBitSubstructure(488, "[#7]-,:[#6]=,:[#7&!H0]");
		addPubChemBitSubstructure(489, "[#7]-,:[#6]-,:[#16]-,:[#6]");
		addPubChemBitSubstructure(490, "[#6]-,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(491, "[#6]-,:[#7]:[#6&!H0]");
		addPubChemBitSubstructure(492, "[#7]-,:[#6]:[#8]:[#6]");
		addPubChemBitSubstructure(493, "[#8]=,:[#6]-,:[#6]:[#6]");
		addPubChemBitSubstructure(494, "[#8]=,:[#6]-,:[#6]:[#7]");
		addPubChemBitSubstructure(495, "[#6]-,:[#7]-,:[#6]:[#6]");
		addPubChemBitSubstructure(496, "[#7]:[#7]-,:[#6&!H0]");
		addPubChemBitSubstructure(497, "[#8]-,:[#6]:[#6]:[#7]");
		addPubChemBitSubstructure(498, "[#8]-,:[#6]=,:[#6]-,:[#6]");
		addPubChemBitSubstructure(499, "[#7]-,:[#6]:[#6]:[#7]");
		addPubChemBitSubstructure(500, "[#6]-,:[#16]-,:[#6]:[#6]");
		addPubChemBitSubstructure(501, "[Cl]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(502, "[#7]-,:[#6]=,:[#6&!H0]");
		addPubChemBitSubstructure(503, "[Cl]-,:[#6]:[#6&!H0]");
		addPubChemBitSubstructure(504, "[#7]:[#6]:[#7]-,:[#6]");
		addPubChemBitSubstructure(505, "[Cl]-,:[#6]:[#6]-,:[#8]");
		addPubChemBitSubstructure(506, "[#6]-,:[#6]:[#7]:[#6]");
		addPubChemBitSubstructure(507, "[#6]-,:[#6]-,:[#16]-,:[#6]");
		addPubChemBitSubstructure(508, "[#16]=,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(509, "[Br]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(510, "[#7&!H0]-,:[#7&!H0]");
		addPubChemBitSubstructure(511, "[#16]=,:[#6]-,:[#7&!H0]");
		addPubChemBitSubstructure(512, "[#6]-,:[#33]-[#8&!H0]");
		addPubChemBitSubstructure(513, "[#16]:[#6]:[#6&!H0]");
		addPubChemBitSubstructure(514, "[#8]-,:[#7]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(515, "[#7]-,:[#7]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(516, "[#6H,#6H2,#6H3]=,:[#6H,#6H2,#6H3]");
		addPubChemBitSubstructure(517, "[#7]-,:[#7]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(518, "[#8]=,:[#6]-,:[#7]-,:[#7]");
		addPubChemBitSubstructure(519, "[#7]=,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(520, "[#6]=,:[#6]-,:[#6]:[#6]");
		addPubChemBitSubstructure(521, "[#6]:[#7]-,:[#6&!H0]");
		addPubChemBitSubstructure(522, "[#6]-,:[#7]-,:[#7&!H0]");
		addPubChemBitSubstructure(523, "[#7]:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(524, "[#6]-,:[#6]=,:[#6]-,:[#6]");
		addPubChemBitSubstructure(525, "[#33]-,:[#6]:[#6&!H0]");
		addPubChemBitSubstructure(526, "[Cl]-,:[#6]:[#6]-,:[Cl]");
		addPubChemBitSubstructure(527, "[#6]:[#6]:[#7&!H0]");
		addPubChemBitSubstructure(528, "[#7&!H0]-,:[#6&!H0]");
		addPubChemBitSubstructure(529, "[Cl]-,:[#6]-,:[#6]-,:[Cl]");
		addPubChemBitSubstructure(530, "[#7]:[#6]-,:[#6]:[#6]");
		addPubChemBitSubstructure(531, "[#16]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(532, "[#16]-,:[#6]:[#6&!H0]");
		addPubChemBitSubstructure(533, "[#16]-,:[#6]:[#6]-,:[#7]");
		addPubChemBitSubstructure(534, "[#16]-,:[#6]:[#6]-,:[#8]");
		addPubChemBitSubstructure(535, "[#8]=,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(536, "[#8]=,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(537, "[#8]=,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(538, "[#7]=,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(539, "[#7]=,:[#6]-,:[#6&!H0]");
		addPubChemBitSubstructure(540, "[#6]-,:[#7]-,:[#6&!H0]");
		addPubChemBitSubstructure(541, "[#8]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(542, "[#8]-,:[#6]:[#6&!H0]");
		addPubChemBitSubstructure(543, "[#8]-,:[#6]:[#6]-,:[#7]");
		addPubChemBitSubstructure(544, "[#8]-,:[#6]:[#6]-,:[#8]");
		addPubChemBitSubstructure(545, "[#7]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(546, "[#7]-,:[#6]:[#6&!H0]");
		addPubChemBitSubstructure(547, "[#7]-,:[#6]:[#6]-,:[#7]");
		addPubChemBitSubstructure(548, "[#8]-,:[#6]-,:[#6]:[#6]");
		addPubChemBitSubstructure(549, "[#7]-,:[#6]-,:[#6]:[#6]");
		addPubChemBitSubstructure(550, "[Cl]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(551, "[Cl]-,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(552, "[#6]:[#6]-,:[#6]:[#6]");
		addPubChemBitSubstructure(553, "[#8]=,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(554, "[Br]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(555, "[#7]=,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(556, "[#6]=,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(557, "[#7]:[#6]-,:[#8&!H0]");
		addPubChemBitSubstructure(558, "[#8]=,:[#7]-,:c:c");
		addPubChemBitSubstructure(559, "[#8]-,:[#6]-,:[#7&!H0]");
		addPubChemBitSubstructure(560, "[#7]-,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(561, "[Cl]-,:[#6]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(562, "[Br]-,:[#6]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(563, "[#8]-,:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(564, "[#6]=,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(565, "[#6]:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(566, "[#8]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(567, "[#8]-,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(568, "N#[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(569, "[#7]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(570, "[#6]:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(571, "[#6&!H0]-,:[#8&!H0]");
		addPubChemBitSubstructure(572, "n:c:n:c");
		addPubChemBitSubstructure(573, "[#8]-,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(574, "[#8]-,:[#6]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(575, "[#8]-,:[#6]-,:[#6]:[#6]-,:[#8]");
		addPubChemBitSubstructure(576, "[#7]=,:[#6]-,:[#6]:[#6&!H0]");
		addPubChemBitSubstructure(577, "c:c-,:[#7]-,:c:c");
		addPubChemBitSubstructure(578, "[#6]-,:[#6]:[#6]-,:c:c");
		addPubChemBitSubstructure(579, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(580, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(581, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(582, "[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(583, "[Cl]-,:[#6]:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(584, "c:c-,:[#6]=,:[#6]-,:[#6]");
		addPubChemBitSubstructure(585, "[#6]-,:[#6]:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(586, "[#6]-,:[#16]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(587, "[#7]-,:[#6]:[#6]-,:[#8&!H0]");
		addPubChemBitSubstructure(588, "[#8]=,:[#6]-,:[#6]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(589, "[#6]-,:[#6]:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(590, "[#6]-,:[#6]:[#6]-,:[#8&!H0]");
		addPubChemBitSubstructure(591, "[Cl]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(592, "[#7]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(593, "[#7]-,:[#6]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(594, "[#6]-,:[#8]-,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(595, "c:c-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(596, "[#7]=,:[#6]-,:[#7]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(597, "[#8]=,:[#6]-,:[#6]-,:c:c");
		addPubChemBitSubstructure(598, "[Cl]-,:[#6]:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(599, "[#6H,#6H2,#6H3]-,:[#6]=,:[#6H,#6H2,#6H3]");
		addPubChemBitSubstructure(600, "[#7]-,:[#6]:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(601, "[#7]-,:[#6]:[#6]:[#6]-,:[#7]");
		addPubChemBitSubstructure(602, "[#8]=,:[#6]-,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(603, "[#6]-,:c:c:[#6]-,:[#6]");
		addPubChemBitSubstructure(604, "[#6]-,:[#8]-,:[#6]-,:[#6]:c");
		addPubChemBitSubstructure(605, "[#8]=,:[#6]-,:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(606, "[#8]-,:[#6]:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(607, "[#7]-,:[#6]-,:[#6]-,:[#6]:c");
		addPubChemBitSubstructure(608, "[#6]-,:[#6]-,:[#6]-,:[#6]:c");
		addPubChemBitSubstructure(609, "[Cl]-,:[#6]-,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(610, "[#6]-,:[#8]-,:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(611, "[#7]-,:[#6]-,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(612, "[#7]-,:[#6]-,:[#8]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(613, "[#6]-,:[#7]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(614, "[#6]-,:[#6]-,:[#8]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(615, "[#7]-,:[#6]-,:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(616, "c:c:n:n:c");
		addPubChemBitSubstructure(617, "[#6]-,:[#6]-,:[#6]-,:[#8&!H0]");
		addPubChemBitSubstructure(618, "c:[#6]-,:[#6]-,:[#6]:c");
		addPubChemBitSubstructure(619, "[#8]-,:[#6]-,:[#6]=,:[#6]-,:[#6]");
		addPubChemBitSubstructure(620, "c:c-,:[#8]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(621, "[#7]-,:[#6]:c:c:n");
		addPubChemBitSubstructure(622, "[#8]=,:[#6]-,:[#8]-,:[#6]:c");
		addPubChemBitSubstructure(623, "[#8]=,:[#6]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(624, "[#8]=,:[#6]-,:[#6]:[#6]-,:[#7]");
		addPubChemBitSubstructure(625, "[#8]=,:[#6]-,:[#6]:[#6]-,:[#8]");
		addPubChemBitSubstructure(626, "[#6]-,:[#8]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(627, "[#8]=,:[#33]-,:[#6]:c:c");
		addPubChemBitSubstructure(628, "[#6]-,:[#7]-,:[#6]-,:[#6]:c");
		addPubChemBitSubstructure(629, "[#16]-,:[#6]:c:c-,:[#7]");
		addPubChemBitSubstructure(630, "[#8]-,:[#6]:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(631, "[#8]-,:[#6]:[#6]-,:[#8&!H0]");
		addPubChemBitSubstructure(632, "[#6]-,:[#6]-,:[#8]-,:[#6]:c");
		addPubChemBitSubstructure(633, "[#7]-,:[#6]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(634, "[#6]-,:[#6]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(635, "[#7]-,:[#7]-,:[#6]-,:[#7&!H0]");
		addPubChemBitSubstructure(636, "[#6]-,:[#7]-,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(637, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(638, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(639, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(640, "[#6]=,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(641, "[#8]-,:[#6]-,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(642, "[#8]-,:[#6]-,:[#6]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(643, "[#6&!H0]-,:[#6]-,:[#7&!H0]");
		addPubChemBitSubstructure(644, "[#6]-,:[#6]=,:[#7]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(645, "[#8]=,:[#6]-,:[#7]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(646, "[#8]=,:[#6]-,:[#7]-,:[#6&!H0]");
		addPubChemBitSubstructure(647, "[#8]=,:[#6]-,:[#7]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(648, "[#8]=,:[#7]-,:[#6]:[#6]-,:[#7]");
		addPubChemBitSubstructure(649, "[#8]=,:[#7]-,:c:c-,:[#8]");
		addPubChemBitSubstructure(650, "[#8]=,:[#6]-,:[#7]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(651, "[#8]-,:[#6]:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(652, "[#8]-,:[#6]:[#6]:[#6]-,:[#7]");
		addPubChemBitSubstructure(653, "[#8]-,:[#6]:[#6]:[#6]-,:[#8]");
		addPubChemBitSubstructure(654, "[#7]-,:[#6]-,:[#7]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(655, "[#8]-,:[#6]-,:[#6]-,:[#6]:c");
		addPubChemBitSubstructure(656, "[#6]-,:[#6]-,:[#7]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(657, "[#6]-,:[#7]-,:[#6]:[#6]-,:[#6]");
		addPubChemBitSubstructure(658, "[#6]-,:[#6]-,:[#16]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(659, "[#8]-,:[#6]-,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(660, "[#6]-,:[#6]=,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(661, "[#8]-,:[#6]-,:[#8]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(662, "[#8]-,:[#6]-,:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(663, "[#8]-,:[#6]-,:[#6]-,:[#8&!H0]");
		addPubChemBitSubstructure(664, "[#6]-,:[#6]=,:[#6]-,:[#6]=,:[#6]");
		addPubChemBitSubstructure(665, "[#7]-,:[#6]:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(666, "[#6]=,:[#6]-,:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(667, "[#6]=,:[#6]-,:[#6]-,:[#8&!H0]");
		addPubChemBitSubstructure(668, "[#6]-,:[#6]:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(669, "[Cl]-,:[#6]:[#6]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(670, "[Br]-,:[#6]:c:c-,:[#6]");
		addPubChemBitSubstructure(671, "[#8]=,:[#6]-,:[#6]=,:[#6]-,:[#6]");
		addPubChemBitSubstructure(672, "[#8]=,:[#6]-,:[#6]=,:[#6&!H0]");
		addPubChemBitSubstructure(673, "[#8]=,:[#6]-,:[#6]=,:[#6]-,:[#7]");
		addPubChemBitSubstructure(674, "[#7]-,:[#6]-,:[#7]-,:[#6]:c");
		addPubChemBitSubstructure(675, "[Br]-,:[#6]-,:[#6]-,:[#6]:c");
		addPubChemBitSubstructure(676, "[#7]#[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(677, "[#6]-,:[#6]=,:[#6]-,:[#6]:c");
		addPubChemBitSubstructure(678, "[#6]-,:[#6]-,:[#6]=,:[#6]-,:[#6]");
		addPubChemBitSubstructure(679, "[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(680, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(681, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(682, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(683, "[#7]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(684, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(685, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(686, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(687, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(688, "[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(689, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(690, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(691, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(692, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(693, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]");
		addPubChemBitSubstructure(694, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]=,:[#8]");
		addPubChemBitSubstructure(695, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]");
		addPubChemBitSubstructure(696, "[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(697, "[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#6])-,:[#6]");
		addPubChemBitSubstructure(698, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(699, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#6])-,:[#6]");
		addPubChemBitSubstructure(700, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]-,:[#6]");
		addPubChemBitSubstructure(701, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#8])-,:[#6]");
		addPubChemBitSubstructure(702, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]-,:[#6]");
		addPubChemBitSubstructure(703, "[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#7])-,:[#6]");
		addPubChemBitSubstructure(704, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(705, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#8])-,:[#6]");
		addPubChemBitSubstructure(706, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](=,:[#8])-,:[#6]");
		addPubChemBitSubstructure(707, "[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#7])-,:[#6]");
		addPubChemBitSubstructure(708, "[#6]-,:[#6](-,:[#6])-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(709, "[#6]-,:[#6](-,:[#6])-,:[#6]-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(710, "[#6]-,:[#6]-,:[#6](-,:[#6])-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(711, "[#6]-,:[#6](-,:[#6])(-,:[#6])-,:[#6]-,:[#6]");
		addPubChemBitSubstructure(712, "[#6]-,:[#6](-,:[#6])-,:[#6](-,:[#6])-,:[#6]");


		//Section 7: Complex SMARTS patterns - These bits test for the presence
		//of complex SMARTS patterns, regardless of count, but where
		//bond orders and bond aromaticity are specific.
		addPubChemBitSubstructure(713, "[#6]c1ccc([#6])cc1");
		addPubChemBitSubstructure(714, "[#6]c1ccc([#8])cc1");
		addPubChemBitSubstructure(715, "[#6]c1ccc([#16])cc1");
		addPubChemBitSubstructure(716, "[#6]c1ccc([#7])cc1");
		addPubChemBitSubstructure(717, "[#6]c1ccc(Cl)cc1");
		addPubChemBitSubstructure(718, "[#6]c1ccc(Br)cc1");
		addPubChemBitSubstructure(719, "[#8]c1ccc([#8])cc1");
		addPubChemBitSubstructure(720, "[#8]c1ccc([#16])cc1");
		addPubChemBitSubstructure(721, "[#8]c1ccc([#7])cc1");
		addPubChemBitSubstructure(722, "[#8]c1ccc(Cl)cc1");
		addPubChemBitSubstructure(723, "[#8]c1ccc(Br)cc1");
		addPubChemBitSubstructure(724, "[#16]c1ccc([#16])cc1");
		addPubChemBitSubstructure(725, "[#16]c1ccc([#7])cc1");
		addPubChemBitSubstructure(726, "[#16]c1ccc(Cl)cc1");
		addPubChemBitSubstructure(727, "[#16]c1ccc(Br)cc1");
		addPubChemBitSubstructure(728, "[#7]c1ccc([#7])cc1");
		addPubChemBitSubstructure(729, "[#7]c1ccc(Cl)cc1");
		addPubChemBitSubstructure(730, "[#7]c1ccc(Br)cc1");
		addPubChemBitSubstructure(731, "Clc1ccc(Cl)cc1");
		addPubChemBitSubstructure(732, "Clc1ccc(Br)cc1");
		addPubChemBitSubstructure(733, "Brc1ccc(Br)cc1");
		addPubChemBitSubstructure(734, "[#6]c1cc([#6])ccc1");
		addPubChemBitSubstructure(735, "[#6]c1cc([#8])ccc1");
		addPubChemBitSubstructure(736, "[#6]c1cc([#16])ccc1");
		addPubChemBitSubstructure(737, "[#6]c1cc([#7])ccc1");
		addPubChemBitSubstructure(738, "[#6]c1cc(Cl)ccc1");
		addPubChemBitSubstructure(739, "[#6]c1cc(Br)ccc1");
		addPubChemBitSubstructure(740, "[#8]c1cc([#8])ccc1");
		addPubChemBitSubstructure(741, "[#8]c1cc([#16])ccc1");
		addPubChemBitSubstructure(742, "[#8]c1cc([#7])ccc1");
		addPubChemBitSubstructure(743, "[#8]c1cc(Cl)ccc1");
		addPubChemBitSubstructure(744, "[#8]c1cc(Br)ccc1");
		addPubChemBitSubstructure(745, "[#16]c1cc([#16])ccc1");
		addPubChemBitSubstructure(746, "[#16]c1cc([#7])ccc1");
		addPubChemBitSubstructure(747, "[#16]c1cc(Cl)ccc1");
		addPubChemBitSubstructure(748, "[#16]c1cc(Br)ccc1");
		addPubChemBitSubstructure(749, "[#7]c1cc([#7])ccc1");
		addPubChemBitSubstructure(750, "[#7]c1cc(Cl)ccc1");
		addPubChemBitSubstructure(751, "[#7]c1cc(Br)ccc1");
		addPubChemBitSubstructure(752, "Clc1cc(Cl)ccc1");
		addPubChemBitSubstructure(753, "Clc1cc(Br)ccc1");
		addPubChemBitSubstructure(754, "Brc1cc(Br)ccc1");
		addPubChemBitSubstructure(755, "[#6]c1c([#6])cccc1");
		addPubChemBitSubstructure(756, "[#6]c1c([#8])cccc1");
		addPubChemBitSubstructure(757, "[#6]c1c([#16])cccc1");
		addPubChemBitSubstructure(758, "[#6]c1c([#7])cccc1");
		addPubChemBitSubstructure(759, "[#6]c1c(Cl)cccc1");
		addPubChemBitSubstructure(760, "[#6]c1c(Br)cccc1");
		addPubChemBitSubstructure(761, "[#8]c1c([#8])cccc1");
		addPubChemBitSubstructure(762, "[#8]c1c([#16])cccc1");
		addPubChemBitSubstructure(763, "[#8]c1c([#7])cccc1");
		addPubChemBitSubstructure(764, "[#8]c1c(Cl)cccc1");
		addPubChemBitSubstructure(765, "[#8]c1c(Br)cccc1");
		addPubChemBitSubstructure(766, "[#16]c1c([#16])cccc1");
		addPubChemBitSubstructure(767, "[#16]c1c([#7])cccc1");
		addPubChemBitSubstructure(768, "[#16]c1c(Cl)cccc1");
		addPubChemBitSubstructure(769, "[#16]c1c(Br)cccc1");
		addPubChemBitSubstructure(770, "[#7]c1c([#7])cccc1");
		addPubChemBitSubstructure(771, "[#7]c1c(Cl)cccc1");
		addPubChemBitSubstructure(772, "[#7]c1c(Br)cccc1");
		addPubChemBitSubstructure(773, "Clc1c(Cl)cccc1");
		addPubChemBitSubstructure(774, "Clc1c(Br)cccc1");
		addPubChemBitSubstructure(775, "Brc1c(Br)cccc1");
		addPubChemBitSubstructure(776, "[#6][#6]1[#6][#6][#6]([#6])[#6][#6]1");
		addPubChemBitSubstructure(777, "[#6][#6]1[#6][#6][#6]([#8])[#6][#6]1");
		addPubChemBitSubstructure(778, "[#6][#6]1[#6][#6][#6]([#16])[#6][#6]1");
		addPubChemBitSubstructure(779, "[#6][#6]1[#6][#6][#6]([#7])[#6][#6]1");
		addPubChemBitSubstructure(780, "[#6][#6]1[#6][#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(781, "[#6][#6]1[#6][#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(782, "[#8][#6]1[#6][#6][#6]([#8])[#6][#6]1");
		addPubChemBitSubstructure(783, "[#8][#6]1[#6][#6][#6]([#16])[#6][#6]1");
		addPubChemBitSubstructure(784, "[#8][#6]1[#6][#6][#6]([#7])[#6][#6]1");
		addPubChemBitSubstructure(785, "[#8][#6]1[#6][#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(786, "[#8][#6]1[#6][#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(787, "[#16][#6]1[#6][#6][#6]([#16])[#6][#6]1");
		addPubChemBitSubstructure(788, "[#16][#6]1[#6][#6][#6]([#7])[#6][#6]1");
		addPubChemBitSubstructure(789, "[#16][#6]1[#6][#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(790, "[#16][#6]1[#6][#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(791, "[#7][#6]1[#6][#6][#6]([#7])[#6][#6]1");
		addPubChemBitSubstructure(792, "[#7][#6]1[#6][#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(793, "[#7][#6]1[#6][#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(794, "Cl[#6]1[#6][#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(795, "Cl[#6]1[#6][#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(796, "Br[#6]1[#6][#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(797, "[#6][#6]1[#6][#6]([#6])[#6][#6][#6]1");
		addPubChemBitSubstructure(798, "[#6][#6]1[#6][#6]([#8])[#6][#6][#6]1");
		addPubChemBitSubstructure(799, "[#6][#6]1[#6][#6]([#16])[#6][#6][#6]1");
		addPubChemBitSubstructure(800, "[#6][#6]1[#6][#6]([#7])[#6][#6][#6]1");
		addPubChemBitSubstructure(801, "[#6][#6]1[#6][#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(802, "[#6][#6]1[#6][#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(803, "[#8][#6]1[#6][#6]([#8])[#6][#6][#6]1");
		addPubChemBitSubstructure(804, "[#8][#6]1[#6][#6]([#16])[#6][#6][#6]1");
		addPubChemBitSubstructure(805, "[#8][#6]1[#6][#6]([#7])[#6][#6][#6]1");
		addPubChemBitSubstructure(806, "[#8][#6]1[#6][#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(807, "[#8][#6]1[#6][#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(808, "[#16][#6]1[#6][#6]([#16])[#6][#6][#6]1");
		addPubChemBitSubstructure(809, "[#16][#6]1[#6][#6]([#7])[#6][#6][#6]1");
		addPubChemBitSubstructure(810, "[#16][#6]1[#6][#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(811, "[#16][#6]1[#6][#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(812, "[#7][#6]1[#6][#6]([#7])[#6][#6][#6]1");
		addPubChemBitSubstructure(813, "[#7][#6]1[#6][#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(814, "[#7][#6]1[#6][#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(815, "Cl[#6]1[#6][#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(816, "Cl[#6]1[#6][#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(817, "Br[#6]1[#6][#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(818, "[#6][#6]1[#6]([#6])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(819, "[#6][#6]1[#6]([#8])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(820, "[#6][#6]1[#6]([#16])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(821, "[#6][#6]1[#6]([#7])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(822, "[#6][#6]1[#6](Cl)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(823, "[#6][#6]1[#6](Br)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(824, "[#8][#6]1[#6]([#8])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(825, "[#8][#6]1[#6]([#16])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(826, "[#8][#6]1[#6]([#7])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(827, "[#8][#6]1[#6](Cl)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(828, "[#8][#6]1[#6](Br)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(829, "[#16][#6]1[#6]([#16])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(830, "[#16][#6]1[#6]([#7])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(831, "[#16][#6]1[#6](Cl)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(832, "[#16][#6]1[#6](Br)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(833, "[#7][#6]1[#6]([#7])[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(834, "[#7][#6]1[#6](Cl)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(835, "[#7][#6]1[#6](Br)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(836, "Cl[#6]1[#6](Cl)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(837, "Cl[#6]1[#6](Br)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(838, "Br[#6]1[#6](Br)[#6][#6][#6][#6]1");
		addPubChemBitSubstructure(839, "[#6][#6]1[#6][#6]([#6])[#6][#6]1");
		addPubChemBitSubstructure(840, "[#6][#6]1[#6][#6]([#8])[#6][#6]1");
		addPubChemBitSubstructure(841, "[#6][#6]1[#6][#6]([#16])[#6][#6]1");
		addPubChemBitSubstructure(842, "[#6][#6]1[#6][#6]([#7])[#6][#6]1");
		addPubChemBitSubstructure(843, "[#6][#6]1[#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(844, "[#6][#6]1[#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(845, "[#8][#6]1[#6][#6]([#8])[#6][#6]1");
		addPubChemBitSubstructure(846, "[#8][#6]1[#6][#6]([#16])[#6][#6]1");
		addPubChemBitSubstructure(847, "[#8][#6]1[#6][#6]([#7])[#6][#6]1");
		addPubChemBitSubstructure(848, "[#8][#6]1[#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(849, "[#8][#6]1[#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(850, "[#16][#6]1[#6][#6]([#16])[#6][#6]1");
		addPubChemBitSubstructure(851, "[#16][#6]1[#6][#6]([#7])[#6][#6]1");
		addPubChemBitSubstructure(852, "[#16][#6]1[#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(853, "[#16][#6]1[#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(854, "[#7][#6]1[#6][#6]([#7])[#6][#6]1");
		addPubChemBitSubstructure(855, "[#7][#6]1[#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(856, "[#7][#6]1[#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(857, "Cl[#6]1[#6][#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(858, "Cl[#6]1[#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(859, "Br[#6]1[#6][#6](Br)[#6][#6]1");
		addPubChemBitSubstructure(860, "[#6][#6]1[#6]([#6])[#6][#6][#6]1");
		addPubChemBitSubstructure(861, "[#6][#6]1[#6]([#8])[#6][#6][#6]1");
		addPubChemBitSubstructure(862, "[#6][#6]1[#6]([#16])[#6][#6][#6]1");
		addPubChemBitSubstructure(863, "[#6][#6]1[#6]([#7])[#6][#6][#6]1");
		addPubChemBitSubstructure(864, "[#6][#6]1[#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(865, "[#6][#6]1[#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(866, "[#8][#6]1[#6]([#8])[#6][#6][#6]1");
		addPubChemBitSubstructure(867, "[#8][#6]1[#6]([#16])[#6][#6][#6]1");
		addPubChemBitSubstructure(868, "[#8][#6]1[#6]([#7])[#6][#6][#6]1");
		addPubChemBitSubstructure(869, "[#8][#6]1[#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(870, "[#8][#6]1[#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(871, "[#16][#6]1[#6]([#16])[#6][#6][#6]1");
		addPubChemBitSubstructure(872, "[#16][#6]1[#6]([#7])[#6][#6][#6]1");
		addPubChemBitSubstructure(873, "[#16][#6]1[#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(874, "[#16][#6]1[#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(875, "[#7][#6]1[#6]([#7])[#6][#6][#6]1");
		addPubChemBitSubstructure(876, "[#7][#6]1[#6](Cl)[#6][#6]1");
		addPubChemBitSubstructure(877, "[#7][#6]1[#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(878, "Cl[#6]1[#6](Cl)[#6][#6][#6]1");
		addPubChemBitSubstructure(879, "Cl[#6]1[#6](Br)[#6][#6][#6]1");
		addPubChemBitSubstructure(880, "Br[#6]1[#6](Br)[#6][#6][#6]1");	
	}

}
