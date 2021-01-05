package ambit2.sln;

import java.util.ArrayList;
import java.util.Stack;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.sln.dictionary.ISLNDictionaryObject;
import ambit2.sln.dictionary.MacroAtomDictionaryObject;
import ambit2.sln.dictionary.PredefinedSLNDictionary;
import ambit2.sln.dictionary.SLNDictionary;

public class SLNParser {
	
	public static class ParserState {
		String sln = null;
		SLNContainer container = null;
	}
	
	private boolean FlagTolerateSpaces = false;
	private boolean FlagLogExpressionInMolAttribute = false; // Preserved for
																// future use
	private boolean FlagAllowBondExpressionInRingClosure = true;
	private boolean FlagCheckTheNumberOfCoordinates = false;

	String sln;
	SLNContainer container;
	SLNSubstance slnSubstance;
	SLNDictionary globalDictionary = null;
	Stack<SLNAtom> brackets = new Stack<SLNAtom>();
	ArrayList<SLNParserError> errors = new ArrayList<SLNParserError>();
	
	ArrayList<Integer> localDictionaryObjectBeginPos = new ArrayList<Integer>();
	ArrayList<Integer> localDictionaryObjectEndPos = new ArrayList<Integer>();
	
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
	String errorContextPrefix = "";

	public SLNParser() {
		globalDictionary = PredefinedSLNDictionary.getDictionary(this);
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
				
		handleLocalDictionary();
				
		init();
		parse();
		return container;
	}
	
	public SLNSubstance parseSLNSubstance(String sln) {
		this.sln = sln;
		slnSubstance = new SLNSubstance();
		container = new SLNContainer(SilentChemObjectBuilder.getInstance());
		container.setGlobalDictionary(globalDictionary);
		errors.clear();
				
		handleLocalDictionary();
		
		init();
		parse();
		
		//If the slnSubstance contains more than one container, 
		//previous containers are already added 
		slnSubstance.containers.add(container);
		
		return slnSubstance;
	}
	
	public void handleLocalDictionary() {
		nChars = sln.length(); //simple init
		findDictionaryObjectPositions();
		SLNDictionary locDictionary = parseLocalDictionaryObjects();
		container.setLocalDictionary(locDictionary);
	}
	
	void init() {
		nChars = sln.length();
		prevAtom = null;
		curChar = 0;
		brackets.clear();
		curBond = null;		
	}
	
	ParserState getState() {
		ParserState state = new ParserState();
		state.sln = sln;
		state.container = container;
		return state;
	}
	
	void restoreState (ParserState state) {
		sln = state.sln;
		container = state.container;
	}

	/*
	 * Basic parsing of a SLNContainer
	 */
	void parse() {
		// default bond is set to be single bond
		curBond = new SLNBond(SilentChemObjectBuilder.getInstance());
		curBond.bondType = SLNConst.B_TYPE_1;

		while ((curChar < nChars) && (errors.size() == 0)) {

			if (Character.isLowerCase(sln.charAt(curChar))
					|| Character.isDigit(sln.charAt(curChar))) 
			{
				newError("Incorrect begining of an atom!", curChar, "");
				curChar++;
				continue;
			}

			if (Character.isUpperCase(sln.charAt(curChar)))
				parseAtom();
			else
				parseSpecialSymbol();  //handle bonds, ring closures, molecule attributes etc.
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
				//index 0 is included for "Any" atom support
				for (int i = 0; i < SLNConst.elSymbols.length; i++)
					if (atomName.equals(SLNConst.elSymbols[i]))
						atomType = i;
			}
		}

		if (atomType == -1)
			newError("Incorrect atom name", curChar,"");

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
						nH = getIntegerFromSequence(100);
				if (nH < -1)
					newError("Incorrect number of H atoms", curChar,"");
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
							nH = getIntegerFromSequence(9);
					if (nH == -1)
						newError("Incorrect number of H atoms", curChar,"");
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
					|| Character.isDigit(sln.charAt(curChar))
					|| (sln.charAt(curChar)== '_'))
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
								
				//Read comparison operation
				int comparisonOperation = SLNConst.CO_NO_COMPARISON;				
				String attrName = atomExpr.substring(startPos, pos);
				if (pos < atomExpr.length()) 
				{					
					if (atomExpr.charAt(pos) == '=')						
					{	
						comparisonOperation = SLNConst.CO_EQUALS;
						pos++;
					}	
					else if (atomExpr.charAt(pos) == '<')						
					{	
						comparisonOperation = SLNConst.CO_LESS_THAN;
						pos++;
						if (pos < atomExpr.length())
						{	
							if (atomExpr.charAt(pos) == '=')
							{
								comparisonOperation = SLNConst.CO_LESS_OR_EQUALS;
								pos++;
							}
							else if (atomExpr.charAt(pos) == '>')
							{
								comparisonOperation = SLNConst.CO_DIFFERS;
								pos++;
							}	
						}	
					}
					else if (atomExpr.charAt(pos) == '>')
					{
						comparisonOperation = SLNConst.CO_GREATER_THAN;
						pos++;
						if (pos < atomExpr.length())
							if (atomExpr.charAt(pos) == '=')
							{
								comparisonOperation = SLNConst.CO_GREATER_OR_EQUALS;
								pos++;
							}
					}
					else if (atomExpr.charAt(pos) == '!') 
					{
						//Handle '!=' differs comparison operation
						pos++;
						if (pos < atomExpr.length())
						{
							if (atomExpr.charAt(pos) == '=')
							{	
								comparisonOperation = SLNConst.CO_DIFFERS;
								pos++;
							}
							else 
							{
								newError("Incorrect comparison operation for attribute " + attrName + " ",
									curChar, "");
								return;
							}	
						}
					}
				}

				if (pos >= atomExpr.length()) 
				{	
					//The end of atom expression is reached.
					if (comparisonOperation == SLNConst.CO_NO_COMPARISON)
					{
						SLNExpressionToken newToken = analyzeAtomAttribute(attrName, null);
						if (newToken != null)
						{
							newToken.comparisonOperation = comparisonOperation;
							curAtExp.tokens.add(newToken);
						}	
						continue;
					}
					
					newError("Missing value for attribute " + attrName + " ",
							curChar, "");
					return;
				}
				
				//Check for the case "NO_COMPARISON"
				//This is used for 
				if (comparisonOperation == SLNConst.CO_NO_COMPARISON)
				{
					SLNExpressionToken newToken = analyzeAtomAttribute(attrName, null);
					if (newToken != null)
					{	
						newToken.comparisonOperation = comparisonOperation; 
						curAtExp.tokens.add(newToken);
					}	
					continue;
				}

				// Read attribute value (after comparson operation: '=', "<",...)
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
				
				if (newToken != null)
				{	
					newToken.comparisonOperation = comparisonOperation; 
					curAtExp.tokens.add(newToken);
				}
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
			if (value == null)
			{
				newError("Missing stereo-chemistry value ",
						curChar, "");
				return null;
			}
			int param = SLNConst.SLNStringToAtomStereoChemAttr(value);
			if (param == -1) {
				newError("Incorrect stereo-chemistry value " + value,
						curChar, "");
				return null;
			}
			SLNExpressionToken token = new SLNExpressionToken(
					SLNConst.A_ATTR_s, param);
			return token;
		}

		// Handle atom attribute spin
		if (name.equals("spin")) {
			if (value == null)
			{
				newError("Missing value for attribute spin",
						curChar, "");
				return null;
			}
			int param = SLNConst.SLNStringToSpinAttr(value);
			if (param == -1) {
				newError("Incorrect spin value " + value, curChar, "");
				return null;
			}
			SLNExpressionToken token = new SLNExpressionToken(
					SLNConst.A_ATTR_spin, param);
			return token;
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
			if (value == null)
			{
				newError("Missing value for attribute c",
						curChar, "");
				return null;
			}
			int c = SLNConst.SLNStringToCoverageQueryAttr(value);
			if (c == -1) {
				newError("Incorrect coverage value " + value, curChar, "");
				return null;
			}
			SLNExpressionToken token = new SLNExpressionToken(
					SLNConst.QA_ATTR_c, c);
			return token;
		}

		// Handle query atom attribute f- filled valences
		if (name.equals("f")) {
			if (value == null)
			{
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_f, "");
				return token;
			} 
			else 
			{
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

		// Handle query atom attribute n - noncovering flag
		if (name.equals("n")) {
			if (value == null) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_n, "");
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
            if (value == null) {
                SLNExpressionToken token = new SLNExpressionToken(
                        SLNConst.QA_ATTR_r, "");
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
			int hac = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_hac, hac);
				return token;
			} else {
				newError("Incorrect heavy atom count (hac) value " + value,
						curChar, "");
				return null;
			}
		}

		// Handle atom attribute hydrogen count
		if (name.equals("hc")) {
			int hc = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_hc, hc);
				return token;
			} else {
				newError("Incorrect hydrogen count (hc) value " + value,
						curChar, "");
				return null;
			}
		}

		// Handle atom attribute hetero atom count
		if (name.equals("htc")) {
			int htc = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_htc, htc);
				return token;
			} else {
				newError("Incorrect hetero atom count (htc) value " + value,
						curChar, "");
				return null;
			}			
		}

		// Handle query atom attribute molecular weight attribute
		//TODO check -this attribute is not logical for atom
		if (name.equals("mw")) {
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

		// Handle query atom attribute ntc - number of nonterminal atoms
		if (name.equals("ntc")) {
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

		// Handle query atom attribute src - the smallest ring count
		if (name.equals("src")) {
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

		// Handle query atom attribute tac - total number of atoms attached to
		// the qualified atom
		if (name.equals("tac")) {
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

		// Handle query atom attribute tbo - total bond order of an atom
		if (name.equals("tbo")) {
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
		
		// Handle query atom attribute rbc - ring bond count of an atom
		if (name.equals("rbc")) {
			int rbc = extractInteger(value);
			if (extractError.equals("")) {
				SLNExpressionToken token = new SLNExpressionToken(
						SLNConst.QA_ATTR_rbc, rbc);
				return token;
			} else {
				newError("Incorrect tbo value " + value, curChar, "");
				return null;
			}
		}
		
		// By default it is an user defined attribute
		if (value != null)
			if (value.equals(""))
			{	
				newError("Missing value for user defined attribute " + name, curChar, "");
				return null;
			}	
		SLNExpressionToken token = new SLNExpressionToken(name, value);
		return token;
	}

	int getIntegerFromSequence(int maxValue) 
	{
		if (!Character.isDigit(sln.charAt(curChar)))
			return (-1);

		int n = 0;
		boolean FlagMaxValueErr = false;
		while (curChar < nChars) {
			char ch = sln.charAt(curChar);
			if (Character.isDigit(ch)) 
			{
				if (!FlagMaxValueErr)
				{	
					n = 10 * n + Character.getNumericValue(ch);
					if (n > maxValue)
						FlagMaxValueErr = true;
				}
				curChar++;
			} else
				break;
		}
		if 	(FlagMaxValueErr)
			return -2;
		
		return (n);
	}

	void newError(String msg, int pos, String param) {
		SLNParserError error = new SLNParserError(sln, errorContextPrefix + msg, pos, param);
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
		
		case '{':
			//Simple handling: end of the parsing procedure
			//and jumping to the end of sln string
			curChar = nChars;
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
		
		//TODO extractBondExpression()
		
		
		//TODO analyzeBondExpression()
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
				while (pos < bondExpr.length()) 
				{
					if (Character.isLetter(bondExpr.charAt(pos))
							|| Character.isDigit(bondExpr.charAt(pos))
							|| (bondExpr.charAt(pos) == '*'))
						pos++;
					else
					{	
						if (attrName.equals("type"))
						{
							switch (bondExpr.charAt(pos))
							{
								case '-':
								case '=':
								case '#':
								case ':':	
								{	
									pos++;
									continue;
								}
								default:
									break;
							}
						}
						break;
					}	
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

				// This is not allowed by the SLN standard for an attribute value
				// '~' can be specified directly as any bond
				//if (value.equals("~"))   
				//	btype = SLNConst.B_TYPE_ANY;
				
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
			{
				if ( (curChar+1) < nChars)
				{	
					//Check for <= comparison operation
					if (sln.charAt(curChar+1) != '=')					
						openBrackets++;
				}
				else
					openBrackets++;
			}	
			else if (sln.charAt(curChar) == '>')
			{	
				if ( (curChar+1) < nChars)
				{	
					//Check for <= comparison operation
					if (sln.charAt(curChar+1) != '=')					
						openBrackets--;
				}
				else					
					openBrackets--;
			}	

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
				/*
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
				*/
				
				//Read comparison operation
				int comparisonOperation = SLNConst.CO_NO_COMPARISON;				
				
				if (pos < molAttr.length()) 
				{					
					if (molAttr.charAt(pos) == '=')						
					{	
						comparisonOperation = SLNConst.CO_EQUALS;
						pos++;
					}	
					else if (molAttr.charAt(pos) == '<')						
					{	
						comparisonOperation = SLNConst.CO_LESS_THAN;
						pos++;
						if (pos < molAttr.length())
						{	
							if (molAttr.charAt(pos) == '=')
							{
								comparisonOperation = SLNConst.CO_LESS_OR_EQUALS;
								pos++;
							}
							else if (molAttr.charAt(pos) == '>')
							{
								comparisonOperation = SLNConst.CO_DIFFERS;
								pos++;
							}	
						}	
					}
					else if (molAttr.charAt(pos) == '>')
					{
						comparisonOperation = SLNConst.CO_GREATER_THAN;
						pos++;
						if (pos < molAttr.length())
							if (molAttr.charAt(pos) == '=')
							{
								comparisonOperation = SLNConst.CO_GREATER_OR_EQUALS;
								pos++;
							}
					}
					else if (molAttr.charAt(pos) == '!') 
					{
						//Handle '!=' differs comparison operation
						pos++;
						if (pos < molAttr.length())
						{
							if (molAttr.charAt(pos) == '=')
							{	
								comparisonOperation = SLNConst.CO_DIFFERS;
								pos++;
							}
							else 
							{
								newError("Incorrect comparison operation for attribute " + attrName + " ",
									curChar, "");
								return;
							}	
						}
					}
				}
				
				//Check for the case "NO_COMPARISON"				 
				if (comparisonOperation == SLNConst.CO_NO_COMPARISON)
				{
					newError("Missing comparison operation for attribute " + attrName + " ",
							curChar, "");
					return;
				}
				
				//Check for no value attribute (end reached)
				if (pos >= molAttr.length()) 
				{	
					newError("Missing value for attribute " + attrName + " ",
							curChar, "");
					return;
				}
				
				
				// Read attribute value (after =, >=, <=, >, < or != )
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

				registerMoleculeAttribute(attrName, attrValue, comparisonOperation);
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

	void registerMoleculeAttribute(String name, String value, int comparisonOperation) {
		// System.out.println("Registering Molecule Attribute: " + name + "  " +
		// value);
		
		//TODO check comparisonOperation for the standard attributes
		
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
			container.getAttributes().userDefiendAttrComparisonOperation.put(name, comparisonOperation);
			
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
	
	void findDictionaryObjectPositions() {
		localDictionaryObjectBeginPos.clear();
		localDictionaryObjectEndPos.clear();
		
		int pos = 0;
		int openBrackets = 0;
		int beginPos = -1; 
		
		while (pos < nChars)
		{
			if (sln.charAt(pos) == '{') 
			{	
				if (openBrackets >= 1)
				{
					newError("Incorrect opening bracket '{'", pos, "");
					return;
				}
				else
				{	
					openBrackets++;
					beginPos = pos;
				}	
			}
			
			if (sln.charAt(pos) == '}') 
			{
				if (openBrackets != 1)
				{
					newError("Incorrect closing bracket '}'", pos, "");
					return;
				}
				else
				{
					openBrackets--;
					//Register new dictionary object substring
					localDictionaryObjectBeginPos.add(beginPos);
					localDictionaryObjectEndPos.add(pos);
					//System.out.println(sln.substring(beginPos,pos+1));
				}	
			}
			
			pos++;
		}
		
		if (openBrackets >= 1)
		{
			newError("Missing closing bracket '{'", pos, "");
			return;
		}
	}
	
	SLNDictionary parseLocalDictionaryObjects() {
		
		if (!errors.isEmpty())
			return null;
		
		if (localDictionaryObjectBeginPos.isEmpty())
			return null;
		
		SLNDictionary dict = new SLNDictionary(); 
		
		for (int i = 0; i < localDictionaryObjectBeginPos.size(); i++)
		{
			int beginPos = localDictionaryObjectBeginPos.get(i)+1;
			int endPos = localDictionaryObjectEndPos.get(i);
			
			String locDictObjStr = sln.substring(beginPos,endPos);
			//System.out.println(locDictObjStr);
			ISLNDictionaryObject dictObj = parseDictionaryObject(locDictObjStr);
			if (dictObj != null)
				dict.addDictionaryObject(dictObj);
		}
		
		return dict;
	}
	
		
	public ISLNDictionaryObject parseDictionaryObject(String dictObjectString)
	{
		if (dictObjectString.isEmpty())
			return null;
				
		//Handle dictionary object name
		int pos = 0;
		if (Character.isUpperCase(dictObjectString.charAt(0)))
			pos++;
		else
		{
			newError("Incorrecr dictionary object: " + dictObjectString, pos, "");
			return null;
		}
		
		int n = dictObjectString.length();
		
		while ((pos < n) && 
				(Character.isLowerCase(dictObjectString.charAt(pos)) 
						|| Character.isDigit(dictObjectString.charAt(pos))
						|| dictObjectString.charAt(pos) == '_')
				)
		{
			pos++;
		}
		
		if (pos == n)
		{
			newError("Incorrecr dictionary object: " + dictObjectString, -1, "");
			return null;
		}
		
		String dictObjName = dictObjectString.substring(0,pos); 
		
		if (dictObjectString.charAt(pos) != ':')
		{
			newError("Incorrecr dictionary object: " + dictObjectString, -1, "");
			return null;
		}
		else
			pos++;
		
		if (pos == n)
		{
			newError("Incorrecr dictionary object: " + dictObjectString, -1, "");
			return null;
		}
				
		
		ParserState state = getState();
		errorContextPrefix = "Parsing macro/markush atom: " + dictObjectString + ": ";
		
		//Parsing a macro atom
		//System.out.println("Parsing dict. object: " + sln);
		sln = dictObjectString.substring(pos);
		container = new SLNContainer(SilentChemObjectBuilder.getInstance());
		
		
		
		init();
		parse();
		
		ISLNDictionaryObject dictObj = null;
		if (errors.isEmpty())
		{
			dictObj = new MacroAtomDictionaryObject(dictObjName, dictObjectString, container, null);
		}
		
		restoreState(state);
		errorContextPrefix = "";
		
		return dictObj;
	}

}
