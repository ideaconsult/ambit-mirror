package ambit2.sln;

import java.util.ArrayList;
import java.util.Stack;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.sln.dictionary.ISLNDictionaryObject;
import ambit2.sln.dictionary.PredefinedSLNDictionary;
import ambit2.sln.dictionary.SLNDictionary;

public class SLNParser {
	private boolean FlagTolerateSpaces = false;
	private boolean FlagLogExpressionInMolAttribute = false; // Preserved for
																// future use
	private boolean FlagAllowBondExpressionInRingClosure = true;
	private boolean FlagCheckTheNumberOfCoordinates = false;

	String sln;
	SLNContainer container;
	SLNDictionary globalDictionary = null;
	Stack<SLNAtom> brackets = new Stack<SLNAtom>();
	ArrayList<SLNParserError> errors = new ArrayList<SLNParserError>();
	// TreeMap<Integer,RingClosure> indexes = new
	// TreeMap<Integer,RingClosure>();

	// Work variables for Component Level Grouping (inspired from SMARTS)
	boolean FlagComponentLevelGrouping = false;
	int curComponent;
	public int numFragments;
	public int maxCompNumber;

	int curChar;
	SLNAtom prevAtom;
	// int curBondType;
	int nChars;
	SLNBond curBond;
	SLNAtomExpression curAtExp;
	SLNBondExpression curBondExp;
	String extractError = "";

	public SLNParser() {
		globalDictionary = PredefinedSLNDictionary.getDictionary();
	}

	public SLNParser(SLNDictionary globalDictionary) {
		this.globalDictionary = globalDictionary;
	}

	public boolean getFlagTolerateSpaces() {
		return FlagTolerateSpaces;
	}

	public void setFlagTolerateSpaces(boolean tolerateSpaces) {
		FlagTolerateSpaces = tolerateSpaces;
	}

	public void setFlagAllowBondExpressionInRingClosure(
			boolean flagAllowBondExpressionInRingClosure) {
		FlagAllowBondExpressionInRingClosure = flagAllowBondExpressionInRingClosure;
	}

	public boolean getFlagAllowBondExpressionInRingClosure() {
		return FlagAllowBondExpressionInRingClosure;
	}

	public SLNContainer parse(String sln) {
		this.sln = sln;
		container = new SLNContainer(SilentChemObjectBuilder.getInstance());
		container.setGlobalDictionary(globalDictionary);
		errors.clear();
		init();
		parse();
		return container;
	}

	void init() {
		nChars = sln.length();
		prevAtom = null;
		curChar = 0;
		brackets.clear();
		curBond = null;
		// indexes.clear();
		// TODO ?
	}

	void parse() {
		// default bond is set to be single bond
		curBond = new SLNBond(SilentChemObjectBuilder.getInstance());
		curBond.bondType = SLNConst.B_TYPE_1;

		while ((curChar < nChars) && (errors.size() == 0)) {

			if (Character.isLowerCase(sln.charAt(curChar))
					|| Character.isDigit(sln.charAt(curChar))) {
				errors.add(new SLNParserError(sln,
						"Incorrect begining of an atom!", curChar, ""));

				curChar++;
				continue;
			}

			if (Character.isUpperCase(sln.charAt(curChar)))
				parseAtom();
			else
				parseSpecialSymbol();
		}

		// Treat unclosed brackets
		if (!brackets.empty())
			newError("There are unclosed brackets", -1, "");

	}

	void parseAtom() {
		// extract atom name
		String atomName = extractAtomName();
		int atomType = -1;

		// analyze atomName
		if (globalDictionary.containsObject(atomName,
				ISLNDictionaryObject.Type.ATOM)) {
			atomType = SLNConst.GlobDictOffseet;
		} else {
			if (container.getLocalDictionary() != null) {
				atomType = SLNConst.LocalDictOffseet;
			} else {
				for (int i = 1; i < SLNConst.elSymbols.length; i++)
					if (atomName.equals(SLNConst.elSymbols[i]))
						atomType = i;
			}
		}

		if (atomType == -1)
			errors.add(new SLNParserError(sln, "Incorrect atom name", curChar,
					""));

		SLNAtom newAtom = new SLNAtom(SilentChemObjectBuilder.getInstance());
		newAtom.atomType = atomType;
		newAtom.atomName = atomName;

		// The SLN parser allows H atoms to be before or after atoms expression
		// i.e. CH[S=R] and C[S=R]H are both correct variants
		boolean ReadHAtoms = false;

		if (curChar < nChars)
			if (sln.charAt(curChar) == 'H') {
				int nH = 1;
				curChar++;
				if (curChar < nChars)
					if (Character.isDigit(sln.charAt(curChar)))
						nH = getInteger();
				newAtom.numHAtom = nH;
				ReadHAtoms = true;
			}

		if (curChar < nChars)
			if (sln.charAt(curChar) == '[') {
				String atomExpression = extractAtomExpression();
				analyzeAtomExpression(atomExpression);
				newAtom.atomExpression = curAtExp;

				// Transfer the id info from AtomExpression to SLNAtom object
				// (this information is duplicated)
				newAtom.atomID = newAtom.atomExpression.atomID;

				// Check id for duplication:
				if (newAtom.atomID != -1) {
					SLNAtom prevIDAtom = getAtomByID(newAtom.atomID);
					if (prevIDAtom != null)
						newError("Duplicated atom id " + newAtom.atomID + ".",
								curChar, "");
				}
			}

		if (curChar < nChars)
			if (sln.charAt(curChar) == 'H') {
				if (ReadHAtoms)
					newError(
							"H atoms are specified before and after atom attributes",
							curChar + 1, "");
				else {
					int nH = 1;
					curChar++;
					if (curChar < nChars)
						if (Character.isDigit(sln.charAt(curChar)))
							nH = getInteger();
					newAtom.numHAtom = nH;
				}
			}

		addAtom(newAtom);

	}

	String extractAtomName() {
		int startPos = curChar;
		curChar++;
		while (curChar < nChars) {
			if (Character.isLowerCase(sln.charAt(curChar))
					|| Character.isDigit(sln.charAt(curChar)))
				curChar++;
			else
				break;
		}

		return sln.substring(startPos, curChar);
	}

	String extractAtomExpression() {
		curChar++;
		int startPos = curChar;
		int openBrackets = 1;
		while ((curChar < nChars) && (openBrackets > 0) /*
														 * && (errors.size() ==
														 * 0)
														 */) {
			if (sln.charAt(curChar) == '[')
				openBrackets++;
			else if (sln.charAt(curChar) == ']')
				openBrackets--;

			curChar++;
		}

		if (openBrackets > 0)
			newError("Incorrect atom expression - bracket '[' not closed",
					curChar + 1, "");

		return sln.substring(startPos, curChar - 1);
	}

	public void analyzeAtomExpression(String atomExpr) {
		// System.out.println("***** AtExpr " + atomExpr);

		if (atomExpr.trim().equals("")) {
			newError("Empty atom expression", curChar + 1, "");
			return;
		}
		curAtExp = new SLNAtomExpression();
		int pos = 0;

		// Check atom ID
		if (Character.isDigit(atomExpr.charAt(pos))) {
			int startPos = pos;
			while (pos < atomExpr.length()) {
				if (Character.isDigit(atomExpr.charAt(pos)))
					pos++;
				else
					break;
			}
			int endPos = pos;

			if (pos < atomExpr.length()) {
				if (atomExpr.charAt(pos) == ':')
					pos++;
				else {
					newError("Missing symbol ':' after atom ID", curChar, "");
					return;
				}
			}

			String idString = atomExpr.substring(startPos, endPos);
			try {
				curAtExp.atomID = Integer.parseInt(idString);
			} catch (Exception e) {
				newError("Incorrect atom ID - too big", curChar, "");
				return;
			}
		}

		// Handle all attributes and logical operations
		while (pos < atomExpr.length()) {
			if (atomExpr.charAt(pos) == ' ') {
				pos++;
				if (FlagTolerateSpaces)
					continue;
				else {
					newError("Space symbol found: ", curChar, "");
					break;
				}
			}

			if (Character.isLetter(atomExpr.charAt(pos))) {
				// Read attribute name
				int startPos = pos;
				while (pos < atomExpr.length()) {
					if (Character.isLetter(atomExpr.charAt(pos))
							|| Character.isDigit(atomExpr.charAt(pos)))
						pos++;
					else
						break;
				}

				String attrName = atomExpr.substring(startPos, pos);
				if (pos < atomExpr.length()) {
					if (atomExpr.charAt(pos) == '=')
						pos++;
					else {
						// Register attribute without value (it is allowed by
						// the SLN syntax
						SLNExpressionToken newToken = analyzeAtomAttribute(
								attrName, null);
						curAtExp.tokens.add(newToken);
						continue;
					}
				}

				if (pos >= atomExpr.length()) {
					// '=' is found but the end of atom expression is reached.
					newError("Missing value for attribute " + attrName + " ",
							curChar, "");
					return;
				}

				// Read attribute value (after '=')
				startPos = pos;
				while (pos < atomExpr.length()) {
					if (Character.isLetter(atomExpr.charAt(pos))
							|| Character.isDigit(atomExpr.charAt(pos))
							|| (atomExpr.charAt(pos) == '+')
							|| (atomExpr.charAt(pos) == '-')
							|| (atomExpr.charAt(pos) == '.')
							|| (atomExpr.charAt(pos) == '*'))
						pos++;
					else
						break;
				}

				String attrValue = atomExpr.substring(startPos, pos);

				// Register attribute with a value
				SLNExpressionToken newToken = analyzeAtomAttribute(attrName,
						attrValue);
				curAtExp.tokens.add(newToken);

				continue;
			}

			// Read special symbol
			switch (atomExpr.charAt(pos)) {
			case '!':
				SLNExpressionToken newToken0 = new SLNExpressionToken(
						SLNConst.LO + SLNConst.LO_NOT);
				curAtExp.tokens.add(newToken0);
				break;
			case '&':
				SLNExpressionToken newToken1 = new SLNExpressionToken(
						SLNConst.LO + SLNConst.LO_AND);
				curAtExp.tokens.add(newToken1);
				break;
			case '|':
				SLNExpressionToken newToken2 = new SLNExpressionToken(
						SLNConst.LO + SLNConst.LO_OR);
				curAtExp.tokens.add(newToken2);
				break;
			case ';':
				SLNExpressionToken newToken3 = new SLNExpressionToken(
						SLNConst.LO + SLNConst.LO_ANDLO);
				curAtExp.tokens.add(newToken3);
				break;

			default: {
				newError(
						"Incorrect symbol in atom expression '"
								+ atomExpr.charAt(pos) + "' ", curChar, "");
				return;
			}
			}

			pos++;
		}
	}

	SLNExpressionToken analyzeAtomAttribute(String name, String value) {
		/*
		 * if (value == null) System.out.println("Attribute " + name); else
		 * System.out.println("Attribute " + name + "=" + value);
		 */
		// Handle charge attribute
		if (name.equals("charge")) {
			int charge = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.A_ATTR_charge, charge);
				return token;
			} else {
				newError("Incorrect charge value " + value, curChar, "");
				return null;
			}
		}

		// Handle isotope attribute
		if (name.equals("I")) {
			int isotope = extractInteger(value);
			if (extractError.equals("")) {
				if (isotope < 0) {
					newError("Isotope could not be negative" + value, curChar,
							"");
				}
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.A_ATTR_I, isotope);
				return token;
			} else {
				newError("Incorrect isotope value " + value, curChar, "");
				return null;
			}
		}

		// Handle fcharge attribute
		if (name.equals("fcharge")) {
			double fcharge = extractDouble(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.A_ATTR_fcharge, fcharge);
				return token;
			} else {
				newError("Incorrect fcharge value " + value, curChar, "");
				return null;
			}
		}

		// Handle stereo-chemistry atom attribute s
		if (name.equals("s")) {
			if (extractError.equals("")) {
				int param = SLNConst.SLNStringToAtomStereoChemAttr(value);
				if (param == -1) {
					newError("Incorrect stereo-chemistry value " + value,
							curChar, "");
					return null;
				}
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.A_ATTR_s, param);
				return token;
			} else {
				newError("Incorrect stereo-chemistry value " + value, curChar,
						"");
				return null;
			}
		}

		// Handle atom attribute spin
		if (name.equals("spin")) {
			if (extractError.equals("")) {
				int param = SLNConst.SLNStringToSpinAttr(value);
				if (param == -1) {
					newError("Incorrect spin value " + value, curChar, "");
					return null;
				}
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.A_ATTR_spin, param);
				return token;
			} else {
				newError("Incorrect spin value " + value, curChar, "");
				return null;
			}
		}

		// Handle query atom attribute mapNum
		if (name.equals("mapNum")) {
			int mapNum = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_mapNum, mapNum);
				return token;
			} else {
				newError("Incorrect map number value " + value, curChar, "");
				return null;
			}
		}

		// Handle query atom attribute "convered"- c
		if (name.equals("c")) {
			if (extractError.equals("")) {
				int c = SLNConst.SLNStringToCoverageQueryAttr(value);
				if (c == -1) {
					newError("Incorrect coverage value " + value, curChar, "");
					return null;
				}
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_c, c);
				return token;
			} else {
				newError("Incorrect coverage value " + value, curChar, "");
				return null;
			}
		}

		// Handle query atom attribute f- filled valences
		if (name.equals("f")) {
			int f = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_f, f);
				return token;
			} else {
				newError("Incorrect f value " + value, curChar, "");
				return null;
			}
		}

		// Handle query atom attribute is
		if (name.equals("is")) {
			int is = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_is, is);
				return token;
			} else {
				newError("Incorrect is value " + value, curChar, "");
				return null;
			}
		}

		// Handle query atom attribute n - noncovering
		if (name.equals("n")) {
			int n = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_n, n);
				return token;
			} else {
				newError("Incorrect n value " + value, curChar, "");
				return null;
			}
		}

		// Handle query atom attribute not
		if (name.equals("not")) {
			int not = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_not, not);
				return token;
			} else {
				newError("Incorrect not value " + value, curChar, "");
				return null;
			}
		}

		// Handle query atom attribute r
		if (name.equals("r")) {
			int r = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_r, r);
				return token;
			} else {
				newError("Incorrect r value " + value, curChar, "");
				return null;
			}
		}

		// Handle query atom attribute v - Markush and macro atom valence
		// information
		if (name.equals("v")) {
			int v = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_v, v);
				return token;
			} else {
				newError("Incorrect v value " + value, curChar, "");
				return null;
			}
		}

		// Handle atom attribute heavy atom count
		if (name.equals("hac")) {
			if (extractError.equals("")) {
				int hac = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QA_ATTR_hac, hac);
					return token;
				} else {
					newError("Incorrect heavy atom count value " + value,
							curChar, "");
					return null;
				}
			}
		}

		// Handle atom attribute hydrogen count
		if (name.equals("hc")) {
			if (extractError.equals("")) {
				int hc = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QA_ATTR_hc, hc);
					return token;
				} else {
					newError("Incorrect hydrogen count value " + value,
							curChar, "");
					return null;
				}
			}
		}

		// Handle atom attribute hetero atom count
		if (name.equals("htc")) {
			if (extractError.equals("")) {
				int htc = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QA_ATTR_htc, htc);
					return token;
				} else {
					newError("Incorrect hetero atom count value " + value,
							curChar, "");
					return null;
				}
			}
		}

		// Handle query atom attribute molecular weight attribute
		if (name.equals("mw")) {
			if (extractError.equals("")) {
				double mw = extractDouble(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QA_ATTR_mw, mw);
					return token;
				} else {
					newError("Incorrect molecular weight value " + value,
							curChar, "");
					return null;
				}
			}
		}

		// Handle query atom attribute ntc - number of nonterminal atoms
		if (name.equals("ntc")) {
			if (extractError.equals("")) {
				int ntc = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QA_ATTR_ntc, ntc);
					return token;
				} else {
					newError("Incorrect ntc value " + value, curChar, "");
					return null;
				}
			}
		}

		// Handle query atom attribute src - the smallest ring count
		if (name.equals("src")) {
			if (extractError.equals("")) {
				int src = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QA_ATTR_src, src);
					return token;
				} else {
					newError("Incorrect src value " + value, curChar, "");
					return null;
				}
			}
		}

		// Handle query atom attribute tac - total number of atoms attached to
		// the qualified atom
		if (name.equals("tac")) {
			if (extractError.equals("")) {
				int tac = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QA_ATTR_tac, tac);
					return token;
				} else {
					newError("Incorrect tac value " + value, curChar, "");
					return null;
				}
			}
		}

		// Handle query atom attribute tbo - total bond order of an atom
		if (name.equals("tbo")) {
			if (extractError.equals("")) {
				int tbo = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QA_ATTR_tbo, tbo);
					return token;
				} else {
					newError("Incorrect tbo value " + value, curChar, "");
					return null;
				}
			}
		}
		// By default it is an user defined attribute
		SLNExpressionToken token = new SLNExpressionToken(name, value);
		return token;
	}

	int getInteger() {
		// TODO to protect against very large integers

		if (!Character.isDigit(sln.charAt(curChar)))
			return (-1);

		int n = 0;
		while (curChar < nChars) {
			char ch = sln.charAt(curChar);
			if (Character.isDigit(ch)) {
				n = 10 * n + Character.getNumericValue(ch);
				curChar++;
			} else
				break;
		}
		return (n);
	}

	void newError(String msg, int pos, String param) {
		SLNParserError error = new SLNParserError(sln, msg, pos, param);
		errors.add(error);
	}

	public String getErrorMessages() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++) {
			sb.append(errors.get(i).getError() + "\n");
		}
		return (sb.toString());
	}

	public ArrayList<SLNParserError> getErrors() {
		return (errors);
	}

	void addAtom(SLNAtom atom) {
		container.addAtom(atom);
		if (prevAtom != null) {
			addBond(prevAtom, atom);
		}
		prevAtom = atom;

	}

	void parseSpecialSymbol() {
		switch (sln.charAt(curChar)) {
		// Bond expression symbols - bond types and logical operations
		case '~': // any bond
		case '-': // single bond
		case '=': // double bond
		case '#': // triple bond
		case ':': // aromatic bond
			parseBond();
			break;

		case '.': // zero order bond - disconnection (new part of the structure)
			curChar++;
			curBond = null;
			break;

		case '@': // ring closure
			parseRingClosure();
			break;

		case '<': // molecule attributes
			parseMoleculeAttributes();
			break;

		case '(':
			if (prevAtom == null) {
				if (curComponent > 0) {
					newError("Incorrect nested componet brackets", curChar + 1,
							"");
				} else {
					brackets.push(prevAtom);
					maxCompNumber++;
					curComponent = maxCompNumber;
				}
			} else
				brackets.push(prevAtom);

			curChar++;
			break;
		case ')':
			if (brackets.empty()) {
				// System.out.println("curChar = " + curChar);
				newError("Incorrect closing brackect", curChar + 1, "");
				return;
			}
			;
			// Not empty brackets stack guarantees that curChar > 0
			if (sln.charAt(curChar - 1) == '(') {
				newError("Empty branch/substituent ", curChar + 1, "");
				brackets.pop(); // This prevents generation of another error
								// "There are unclosed brackets"
				return;
			}
			;

			prevAtom = brackets.pop();
			if (prevAtom == null)
				curComponent = 0;
			curChar++;
			break;

		default:
			newError("Incorrect symbol '" + sln.charAt(curChar) + "'",
					curChar + 1, "");
			curChar++;
		}

	}

	void addBond(SLNAtom atom0, SLNAtom atom1) {
		IAtom[] atoms = new SLNAtom[2];
		atoms[0] = atom0;
		atoms[1] = atom1;
		if (curBond != null) {
			curBond.setAtoms(atoms);
			container.addBond(curBond);
		}

		// Default bond is set to be single bond.
		SLNBond newBond = new SLNBond(SilentChemObjectBuilder.getInstance());
		curBond = newBond;
		curBond.bondType = SLNConst.B_TYPE_1;
	}

	void parseBond() {
		if (curChar == 0) {
			newError("SLN string starts incorrectly with a bond expression",
					curChar, "");
			return;
		}

		char boChar = sln.charAt(curChar);
		curChar++;
		if (curChar == nChars) {
			newError("SLN string ends incorrectly with a bond expression",
					curChar, "");
			return;
		}

		if (curBond == null)
			curBond = new SLNBond(SilentChemObjectBuilder.getInstance());

		curBond.bondType = SLNConst.SLNCharToBondTypeAttr(boChar);

		if (curChar < nChars)
			if (sln.charAt(curChar) == '[') {
				String bondExpression = extractBondExpression();
				analyzeBondExpression(bondExpression);
				curBond.bondExpression = curBondExp;
			}
	}

	String extractBondExpression() {
		curChar++;
		int startPos = curChar;
		int openBrackets = 1;
		while ((curChar < nChars) && (openBrackets > 0) /*
														 * && (errors.size() ==
														 * 0)
														 */) {
			if (sln.charAt(curChar) == '[')
				openBrackets++;
			else if (sln.charAt(curChar) == ']')
				openBrackets--;

			curChar++;
		}

		if (openBrackets > 0)
			newError("Incorrect bond expression - bracket '[' not closed",
					curChar + 1, "");

		return sln.substring(startPos, curChar - 1);
	}

	public void analyzeBondExpression(String bondExpr) {
		// System.out.println("**BondExpr " + bondExpr);
		if (bondExpr.trim().equals("")) {
			newError("Empty bond expression", curChar + 1, "");
			return;
		}
		curBondExp = new SLNBondExpression();
		int pos = 0;

		// Handle all attributes and logical operations
		while (pos < bondExpr.length()) {
			if (bondExpr.charAt(pos) == ' ') {
				pos++;
				if (FlagTolerateSpaces)
					continue;
				else {
					newError("Space symbol found: ", curChar, "");
					break;
				}
			}

			if (Character.isLetter(bondExpr.charAt(pos))) {
				// Read attribute name
				int startPos = pos;
				while (pos < bondExpr.length()) {
					if (Character.isLetter(bondExpr.charAt(pos))
							|| Character.isDigit(bondExpr.charAt(pos)))
						pos++;
					else
						break;
				}

				String attrName = bondExpr.substring(startPos, pos);
				if (pos < bondExpr.length()) {
					if (bondExpr.charAt(pos) == '=')
						pos++;
				}
				if (pos >= bondExpr.length()) {
					// '=' is found but the end of bond expression is reached.
					newError("Missing value for attribute " + attrName + " ",
							curChar, "");
					return;
				}

				// Read attribute value (after '=')
				startPos = pos;
				while (pos < bondExpr.length()) {
					if (Character.isLetter(bondExpr.charAt(pos))
							|| Character.isDigit(bondExpr.charAt(pos))
							|| (bondExpr.charAt(pos) == '*'))
						pos++;
					else
						break;
				}

				String attrValue = bondExpr.substring(startPos, pos);

				// Register attribute with a value
				SLNExpressionToken newToken = analyzeBondAttribute(attrName,
						attrValue);
				curBondExp.tokens.add(newToken);

				continue;
			}

			// Read special symbol
			switch (bondExpr.charAt(pos)) {
			case '!':
				SLNExpressionToken newToken0 = new SLNExpressionToken(
						SLNConst.LO + SLNConst.LO_NOT);
				curBondExp.tokens.add(newToken0);
				break;
			case '&':
				SLNExpressionToken newToken1 = new SLNExpressionToken(
						SLNConst.LO + SLNConst.LO_AND);
				curBondExp.tokens.add(newToken1);
				break;
			case '|':
				SLNExpressionToken newToken2 = new SLNExpressionToken(
						SLNConst.LO + SLNConst.LO_OR);
				curBondExp.tokens.add(newToken2);
				break;
			case ';':
				SLNExpressionToken newToken3 = new SLNExpressionToken(
						SLNConst.LO + SLNConst.LO_ANDLO);
				curBondExp.tokens.add(newToken3);
				break;

			default: {
				newError(
						"Incorrect symbol in bond expression '"
								+ bondExpr.charAt(pos) + "' ", curChar, "");
				return;
			}
			}

			pos++;
		}
	}

	SLNExpressionToken analyzeBondAttribute(String name, String value) {
		if (value == null)
			System.out.println("Attribute" + name);
		else
			System.out.println("Attribute " + name + " = " + value);

		// Handle query bond attribute type - bond type specified by the bond
		// character
		if (name.equals("type")) {
			if (extractError.equals("")) {
				int btype = -1;

				if (value.equals("~"))
					btype = SLNConst.B_TYPE_ANY;
				if (value.equals("1") || value.equals("-"))
					btype = SLNConst.B_TYPE_1;
				if (value.equals("2") || value.equals("="))
					btype = SLNConst.B_TYPE_2;
				if (value.equals("3") || value.equals("#"))
					btype = SLNConst.B_TYPE_3;
				if (value.equals("4") || value.equals(":"))
					btype = SLNConst.B_TYPE_aromatic;

				if (btype == -1) {
					newError("Incorrect bond query type value " + value,
							curChar, "");
					return null;
				} else {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QB_ATTR_type, btype);
					return token;
				}
			}
		}

		// Handle stereo-chemistry bond attribute
		if (name.equals("s")) {
			if (extractError.equals("")) {
				int s = SLNConst.SLNStringToBondStereoChemAttr(value);
				if (s == -1) {
					newError("Incorrect stereo-chemistry bond value " + value,
							curChar, "");
					return null;
				}
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.B_ATTR_s, s);
				return token;
			} else {
				newError("Incorrect stereo-chemistry bond value " + value,
						curChar, "");
				return null;
			}
		}

		// Handle query bond attribute src - the smallest ring count
		if (name.equals("src")) {
			if (extractError.equals("")) {
				int src = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QB_ATTR_src, src);
					return token;
				} else {
					newError("Incorrect src value " + value, curChar, "");
					return null;
				}
			}
		}

		// Handle query bond attribute rbc - ring bond count
		if (name.equals("rbc")) {
			if (extractError.equals("")) {
				int rbc = extractInteger(value);
				if (extractError.equals("")) {
					SLNExpressionToken token = new SLNExpressionToken(
							SLNConst.QB_ATTR_rbc, rbc);
					return token;
				} else {
					newError("Incorrect rbc value " + value, curChar, "");
					return null;
				}
			}
		}

		// By default it is an user defined attribute
		SLNExpressionToken token = new SLNExpressionToken(name, value);
		return token;

	}

	void parseRingClosure() {
		curChar++;

		if (curChar >= nChars) {
			newError("Missing ring closure value ", curChar, "");
			return;
		}

		int id = -1;
		if (Character.isDigit(sln.charAt(curChar))) {
			int startPos = curChar;
			while (curChar < nChars) {
				if (Character.isDigit(sln.charAt(curChar)))
					curChar++;
				else
					break;
			}

			String idString = sln.substring(startPos, curChar);
			id = extractInteger(idString);
		} else {
			newError("Missing ring closure value ", curChar, "");
			return;
		}

		// System.out.println("@id = " + id);

		SLNAtom idAtom = getAtomByID(id);
		if (idAtom == null) {
			newError("Incorrect ring closure value " + id
					+ ". No such atom id!", curChar, "");
			return;
		}

		addBond(prevAtom, idAtom);
	}

	SLNAtom getAtomByID(int id) {
		for (int i = 0; i < container.getAtomCount(); i++) {
			SLNAtom idAtom = (SLNAtom) container.getAtom(i);
			if (idAtom.atomID == id)
				return idAtom;
		}
		return null;
	}

	void parseMoleculeAttributes() {
		if (curChar == 0) // This restriction could be omitted
		{
			newError(
					"SLN string starts incorrectly with a molecule attributes",
					curChar, "");
			return;
		}

		if (curChar == nChars - 1) {
			newError("Incorrect molecule attributes section at the end '<..'",
					curChar, "");
			return;
		}

		String moleculeAttributes = extractMoleculeAttributes();
		analyzeMoleculeAttributes(moleculeAttributes);
	}

	String extractMoleculeAttributes() {
		curChar++;
		int startPos = curChar;
		int openBrackets = 1;
		while ((curChar < nChars) && (openBrackets > 0) && (errors.size() == 0)) {
			if (sln.charAt(curChar) == '<')
				openBrackets++;
			else if (sln.charAt(curChar) == '>')
				openBrackets--;

			curChar++;
		}

		return sln.substring(startPos, curChar - 1);
	}

	void analyzeMoleculeAttributes(String molAttr) {
		if (molAttr.trim().equals("")) {
			// It is not considered as an error!
			return;
		}
		int pos = 0;

		// Handle all attributes and logical operations
		while (pos < molAttr.length()) {
			if (molAttr.charAt(pos) == ' ') {
				pos++;
				if (FlagTolerateSpaces)
					continue;
				else {
					newError("Space symbol found: ", curChar, "");
					break;
				}
			}

			if (Character.isLetter(molAttr.charAt(pos))) {
				// Read attribute name
				int startPos = pos;
				while (pos < molAttr.length()) {
					if (Character.isLetter(molAttr.charAt(pos))
							|| Character.isDigit(molAttr.charAt(pos)))
						pos++;
					else
						break;
				}

				// TODO add check for ':=' and '^=' attribute assignments

				String attrName = molAttr.substring(startPos, pos);
				if (pos < molAttr.length()) {
					if (molAttr.charAt(pos) == '=')
						pos++;
				}
				if (pos >= molAttr.length()) {
					// '=' is found but the end of bond expression is reached.
					newError("Missing value for attribute " + attrName + " ",
							curChar, "");
					return;
				}

				// Read attribute value (after '=')
				startPos = pos;
				while (pos < molAttr.length()) {
					/*
					 * if (Character.isLetter(molAttr.charAt(pos)) ||
					 * Character.isDigit(molAttr.charAt(pos))||
					 * (molAttr.charAt(pos) == '*'))
					 */
					if (molAttr.charAt(pos) != ';')
						pos++;
					else
						break;
				}

				String attrValue = molAttr.substring(startPos, pos);

				registerMoleculeAttribute(attrName, attrValue);
				continue;
			}

			// Read special symbol
			switch (molAttr.charAt(pos)) {
			case '!':
				if (FlagLogExpressionInMolAttribute) {
					SLNExpressionToken newToken0 = new SLNExpressionToken(
							SLNConst.LO + SLNConst.LO_NOT);
					// TODO
				} else {
					newError(
							"Logical expressions in molecule attribute section are not allowed!",
							curChar, "");
					return;
				}
				break;
			case '&':
				// Logical expressions are not stored currently in
				// SLNContainerAttributes so nothing is done
				// SLNExpressionToken newToken1 = new
				// SLNExpressionToken(SLNConst.LO + SLNConst.LO_AND);
				break;
			case '|':
				if (FlagLogExpressionInMolAttribute) {
					SLNExpressionToken newToken2 = new SLNExpressionToken(
							SLNConst.LO + SLNConst.LO_OR);
					// TODO
				} else {
					newError(
							"Logical expressions in molecule attribute section are not allowed!",
							curChar, "");
					return;
				}
				break;
			case ';':
				// Logical expressions currently are not stored in
				// SLNContainerAttributes so nothing is done
				// SLNExpressionToken newToken3 = new
				// SLNExpressionToken(SLNConst.LO + SLNConst.LO_ANDLO);
				break;

			default:
				newError(
						"Incorrect symbol in molecule expression '"
								+ molAttr.charAt(pos) + "' ", curChar, "");
				return;
			}

			pos++;
		}
	}

	void registerMoleculeAttribute(String name, String value) {
		// System.out.println("Registering Molecule Attribute: " + name + "  " +
		// value);

		// Handle predefined molecule attributes
		if (name.equals("name")) {
			container.getAttributes().name = value;
			return;
		}

		if (name.equals("regid") || name.equals("REGID")) {
			container.getAttributes().regid = value;
			return;
		}

		if (name.equals("type")) {
			container.getAttributes().type = value;
			return;
		}

		if (name.equalsIgnoreCase("coord2d")) {
			parser2DCoordinates(value);
			return;
		}

		if (name.equalsIgnoreCase("coord3d")) {
			parser3DCoordinates(value);
			return;
		}

		// By default it is an user defined attribute
		if (name.equals("")) {
			newError("Attribute name in molecule attr. section!", curChar, "");
			return;
		}

		if (container.getAttributes().userDefiendAttr.containsKey(name)) {
			newError("Attribute " + name
					+ " in molecule attr. section is repeated!", curChar, "");
			return;
		} else {
			container.getAttributes().userDefiendAttr.put(name, value);
			// System.out.println("put " + name + " = " + value);
		}
	}

	void parser2DCoordinates(String coord) {
		container.getAttributes().coord2d = new ArrayList<double[]>();
		int cPos = 0;
		int nAtom = 0;
		while (cPos < coord.length()) {
			String brackets = extractBracketsFromPosition(coord, cPos);
			if (brackets == null) {
				newError("Incorrect 2D coordinates for atom #" + (nAtom + 1)
						+ "  " + extractError, curChar, "");
				return;
			}

			cPos += brackets.length();

			String xystring = brackets.substring(1, brackets.length() - 1); // removing:
																			// '('
																			// and
																			// ')'
			String tokens[] = xystring.split(",");
			if (tokens.length != 2) {
				newError("Incorrect 2D coordinates for atom #" + (nAtom + 1)
						+ " : " + brackets, curChar, "");
				return;
			}

			double c[] = new double[2];
			c[0] = extractDouble(tokens[0]);
			if (extractError != "") {
				newError("Incorrect 2D coordinates for atom #" + (nAtom + 1)
						+ " : " + brackets, curChar, "");
				return;
			}

			c[1] = extractDouble(tokens[1]);
			if (extractError != "") {
				newError("Incorrect 2D coordinates for atom #" + (nAtom + 1)
						+ " : " + brackets, curChar, "");
				return;
			}

			nAtom++;
			container.getAttributes().coord2d.add(c);

			// Handling the separator ',' for the next coordinates
			if (cPos < coord.length()) {
				if (coord.charAt(cPos) != ',') {
					newError(
							"Missing separator ',' in 2D coordinates after atom #"
									+ nAtom, curChar, "");
					return;
				} else
					cPos++;
			}
		}

		if (FlagCheckTheNumberOfCoordinates)
			if (nAtom != container.getAtomCount()) {
				newError(
						"The number of 2D coordinates is different from the atom count",
						curChar, "");
				return;
			}
	}

	void parser3DCoordinates(String coord) {
		container.getAttributes().coord3d = new ArrayList<double[]>();
		int cPos = 0;
		int nAtom = 0;
		while (cPos < coord.length()) {
			String brackets = extractBracketsFromPosition(coord, cPos);
			if (brackets == null) {
				newError("Incorrect 3D coordinates for atom #" + (nAtom + 1)
						+ "  " + extractError, curChar, "");
				return;
			}

			cPos += brackets.length();

			String xyzstring = brackets.substring(1, brackets.length() - 1); // removing:
																				// '('
																				// and
																				// ')'
			String tokens[] = xyzstring.split(",");
			if (tokens.length != 3) {
				newError("Incorrect 3D coordinates for atom #" + (nAtom + 1)
						+ " : " + brackets, curChar, "");
				return;
			}

			double c[] = new double[3];

			for (int i = 0; i < 3; i++) {
				c[i] = extractDouble(tokens[i]);
				if (extractError != "") {
					newError("Incorrect 3D coordinates for atom #"
							+ (nAtom + 1) + " : " + brackets, curChar, "");
					return;
				}
			}

			nAtom++;
			container.getAttributes().coord3d.add(c);

			// Handling the separator ',' for the next coordinates
			if (cPos < coord.length()) {
				if (coord.charAt(cPos) != ',') {
					newError(
							"Missing separator ',' in 3D coordinates after atom #"
									+ nAtom, curChar, "");
					return;
				} else
					cPos++;
			}
		}

		if (FlagCheckTheNumberOfCoordinates)
			if (nAtom != container.getAtomCount()) {
				newError(
						"The number of 3D coordinates is different from the atom count",
						curChar, "");
				return;
			}
	}

	String extractBracketsFromPosition(String target, int pos) {
		if (target.charAt(pos) == '(') {
			int n = pos + 1;
			while (n < target.length()) {
				if (target.charAt(n) == ')')
					return target.substring(pos, n + 1);
				n++;
			}

			extractError = "Missing closing bracket ')'";
			return null; // Error exit missing
		} else {
			extractError = "Missing opening bracket '('";
			return null;
		}
	}

	int extractInteger(String valueString) {
		extractError = "";
		try {
			int value;
			if (valueString.charAt(0) == '+')
				value = Integer.parseInt(valueString.substring(1));
			else
				value = Integer.parseInt(valueString);
			return value;
		} catch (Exception e) {
			extractError = "Incorrect integer value " + valueString;
			return 0;
		}
	}

	double extractDouble(String valueString) {
		extractError = "";
		try {
			double value;
			if (valueString.charAt(0) == '+')
				value = Double.parseDouble(valueString.substring(1));
			else
				value = Double.parseDouble(valueString);
			return value;
		} catch (Exception e) {
			extractError = "Incorrect double value " + valueString;
			return 0.0;
		}
	}

}
