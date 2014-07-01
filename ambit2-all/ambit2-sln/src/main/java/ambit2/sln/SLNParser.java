package ambit2.sln;

import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

import org.openscience.cdk.interfaces.IAtom;


public class SLNParser 
{
	private boolean FlagTolerateSpaces = false;

	String sln;
	SLNContainer container;
	SLNDictionary globalDictionary = null;
	Stack<SLNAtom> brackets = new Stack<SLNAtom>();
	ArrayList<SLNParserError> errors = new ArrayList<SLNParserError>();
	TreeMap<Integer,RingClosure> indexes = new TreeMap<Integer,RingClosure>();

	//Work variables for Component Level Grouping (inspired from SMARTS)
	boolean FlagComponentLevelGrouping = false; 
	int curComponent;
	public int numFragments;
	public int maxCompNumber;


	int curChar;	
	SLNAtom prevAtom;
	//int curBondType;
	int nChars;
	SLNBond curBond;
	SLNAtomExpression curAtExp;
	SLNBondExpression curBondExp;
	String extractError ="";



	public SLNParser()
	{
		globalDictionary = PredefinedSLNDictionary.getDictionary();
	}

	public SLNParser(SLNDictionary globalDictionary)
	{
		this.globalDictionary = globalDictionary;
	}

	public boolean getTolerateSpaces()
	{
		return FlagTolerateSpaces;
	}

	public void setTolerateSpaces(boolean tolerateSpaces)
	{
		FlagTolerateSpaces = tolerateSpaces;
	}


	public SLNContainer parse(String sln)
	{
		this.sln = sln;
		container = new SLNContainer();
		container.setGlobalDictionary(globalDictionary);
		errors.clear();
		init();
		parse();
		return container;
	}

	void init()
	{
		nChars = sln.length();
		prevAtom = null;
		curChar = 0;
		brackets.clear();
		curBond = null;
		indexes.clear();
	}

	void parse()
	{	
		while ((curChar < nChars) && (errors.size() == 0))
		{

			if (Character.isLowerCase(sln.charAt(curChar)) || Character.isDigit(sln.charAt(curChar)))
			{
				errors.add(new SLNParserError(sln, "Incorrect begining of an atom!", curChar, ""));

				curChar++;
				continue;
			}

			if (Character.isUpperCase(sln.charAt(curChar)))
				parseAtom();
			else
				parseSpecialSymbol();
		}

		//Treat unclosed brackets
		if (!brackets.empty())		
			newError("There are unclosed brackets",-1, "");

	}

	void parseAtom()
	{
		//extract atom name
		String atomName = extractAtomName();
		int atomType = -1;



		//analyze atomName
		if (globalDictionary.containsAtomName(atomName))
		{
			atomType = SLNConst.GlobDictOffseet;
		}
		else
		{			
			if (container.getLocalDictionary() != null)
			{	
				atomType = SLNConst.LocalDictOffseet;
			}
			else
			{
				for (int i = 1; i < SLNConst.elSymbols.length; i++)
					if (atomName.equals(SLNConst.elSymbols[i]))
						atomType = i;
			}
		}


		if (atomType == -1)
			errors.add(new SLNParserError(sln, "Incorrect atom name", curChar, ""));

		SLNAtom newAtom = new SLNAtom();
		newAtom.atomType = atomType;
		newAtom.atomName = atomName;


		//The SLN parser allows H atoms to be before or after atoms expression 
		//i.e. CH[S=R] and C[S=R]H are both correct variants
		boolean ReadHAtoms = false;

		if (curChar < nChars)
			if (sln.charAt(curChar) == 'H')
			{
				int nH = 1;
				curChar++;
				if (curChar < nChars)
					if (Character.isDigit(sln.charAt(curChar)))
						nH = getInteger();
				newAtom.numHAtom = nH;
				ReadHAtoms = true;
			}

		if (curChar < nChars)
			if (sln.charAt(curChar) == '[')
			{	
				String atomExpression = extractAtomExpression();				
				analyzeAtomExpression(atomExpression);
				newAtom.atomExpression = curAtExp;
			}

		if (curChar < nChars)
			if (sln.charAt(curChar) == 'H')
			{
				if (ReadHAtoms)				
					newError("H atoms are specified before and after atom attributes", curChar+1,"");
				else
				{	
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

	String extractAtomName()
	{	
		int startPos = curChar;
		curChar++;
		while (curChar < nChars)
		{
			if (Character.isLowerCase(sln.charAt(curChar)) || Character.isDigit(sln.charAt(curChar)))
				curChar++;
			else 
				break;
		}

		return sln.substring(startPos, curChar);
	}

	String extractAtomExpression()
	{
		curChar++;
		int startPos = curChar;
		int openBrackets = 1;
		while ((curChar < nChars) && (openBrackets > 0) && (errors.size() == 0))
		{
			if (sln.charAt(curChar)=='[')			
				openBrackets++;
			else
				if (sln.charAt(curChar)==']')				
					openBrackets--;

			curChar++;
		}

		return sln.substring(startPos,curChar-1);
	}

	public void analyzeAtomExpression(String atomExpr)
	{
	//	System.out.println("***** AtExpr " + atomExpr);

		if (atomExpr.trim().equals(""))
		{
			newError("Empty atom expression", curChar+1,"");
			return;
		}
		curAtExp = new SLNAtomExpression();
		int pos = 0;

		//Check atom ID
		if (Character.isDigit(atomExpr.charAt(pos)))
		{
			int startPos = pos;
			while (pos < atomExpr.length())
			{
				if ( Character.isDigit(atomExpr.charAt(pos)) )
					pos++;
				else
					break;	
			}
			int endPos = pos;

			if (pos < atomExpr.length())
			{	
				if (atomExpr.charAt(pos) == ':')
					pos++;
				else
				{
					newError("Missing symbol ':' after atom ID", curChar,"");
					return;
				}
			}

			String idString = atomExpr.substring(startPos, endPos);
			try
			{
				curAtExp.atomID = Integer.parseInt(idString);
			}
			catch(Exception e)
			{
				newError("Incorrect atom ID - too big",curChar,"");
				return;
			}

		}
		//Handle all attributes and logical operations
		while (pos < atomExpr.length())
		{
			if(atomExpr.charAt(pos) == ' ')
			{	
				pos++;
				if (FlagTolerateSpaces)
					continue;
				else
				{
					newError("Space symbol found: ", curChar, "");
					break;
				}	
			}


			if (Character.isLetter(atomExpr.charAt(pos)))
			{
				//Read attribute name
				int startPos = pos;
				while (pos < atomExpr.length())
				{
					if (Character.isLetter(atomExpr.charAt(pos)) 
							|| Character.isDigit(atomExpr.charAt(pos))  )
						pos++;
					else
						break;
				}

				String attrName = atomExpr.substring(startPos, pos);
				if(pos < atomExpr.length())
				{
					if(atomExpr.charAt(pos) == '=')
						pos++;
					else
					{	
						//Register attribute without value (it is allowed by the SLN syntax 
						SLNExpressionToken newToken =  analyzeAtomAttribute(attrName, null);
						curAtExp.tokens.add(newToken);
						continue;
					}
				}

				if(pos >= atomExpr.length())				
				{
					//'=' is found but the end of atom expression is reached.
					newError("Missing value for attribute " + attrName + " ",curChar,"");
					return;
				}

				//Read attribute value (after '=')
				startPos = pos;
				while (pos < atomExpr.length())
				{
					if (Character.isLetter(atomExpr.charAt(pos)) ||
							Character.isDigit(atomExpr.charAt(pos)) ||
							(atomExpr.charAt(pos) == '+' ) || 
							(atomExpr.charAt(pos) == '-')  ||
							(atomExpr.charAt(pos) == '.')  ||
							(atomExpr.charAt(pos) == '*')
							)
						pos++;
					else
						break;
				}

				String attrValue = atomExpr.substring(startPos, pos);

				//Register attribute with a value 
				SLNExpressionToken newToken =  analyzeAtomAttribute(attrName, attrValue);
				curAtExp.tokens.add(newToken);

				continue;
			}

			//Read special symbol
			switch  (atomExpr.charAt(pos))
			{
			case '!':
				SLNExpressionToken newToken0 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_NOT);  
				curAtExp.tokens.add(newToken0);
				break;
			case '&':
				SLNExpressionToken newToken1 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_AND);  
				curAtExp.tokens.add(newToken1);
				break;
			case '|':
				SLNExpressionToken newToken2 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_OR);  
				curAtExp.tokens.add(newToken2);				
				break;
			case ';':
				SLNExpressionToken newToken3 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_ANDLO);  
				curAtExp.tokens.add(newToken3);				
				break;	

			default:
			{
				newError("Incorrect symbol in atom expression '"+atomExpr.charAt(pos)+"' ",curChar,"");
				return;
			}
			}

			pos++;
		}
	}

	SLNExpressionToken analyzeAtomAttribute(String name, String value)
	{
		/*		if (value == null)
			System.out.println("Attribute " + name);
		else
			System.out.println("Attribute " + name + "=" + value);
		 */
		//Handle charge attribute
		if (name.equals("charge"))
		{
			int charge = extractInteger(value);
			if (extractError.equals(""))
			{
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.A_ATTR_charge,charge);
				return token;
			}
			else
			{
				newError("Incorrect charge value " + value, curChar,"");
				return null;
			}
		}

		//Handle isotope attribute
		if (name.equals("I"))
		{
			int isotope = extractInteger(value);
			if (extractError.equals(""))
			{
				if (isotope < 0)
				{
					newError("Isotope could not be negative" +value, curChar,"");
				}
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.A_ATTR_I,isotope);
				return token;
			}
			else
			{
				newError("Incorrect isotope value " + value, curChar,"");
				return null;
			}
		}

		//Handle fcharge attribute
		if (name.equals("fcharge"))
		{
			double fcharge = extractDouble(value);
			if (extractError.equals(""))
			{
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.A_ATTR_fcharge,fcharge);
				return token;
			}
			else
			{
				newError("Incorrect fcharge value " + value, curChar,"");
				return null;
			}
		}

		//Handle stereo-chemistry atom attribute s
		if (name.equals("s"))
		{	
			if (extractError.equals(""))
			{
				int param = SLNConst.SLNStringToAtomStereoChemAttr(value);
				if (param == -1)
				{
					newError("Incorrect stereo-chemistry value " + value, curChar,"");
					return null;
				}
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.A_ATTR_s,param);
				return token;
			}
			else
			{
				newError("Incorrect stereo-chemistry value " + value, curChar,"");
				return null;
			}
		}

		//Handle atom attribute spin
		if (name.equals("spin"))
		{
			if (extractError.equals(""))
			{
				int param = SLNConst.SLNStringToSpinAttr(value);
				if (param == -1)
				{
					newError("Incorrect spin value " + value, curChar,"");
					return null;
				}
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.A_ATTR_spin,param);
				return token;
			}
			else
			{
				newError("Incorrect spin value " + value, curChar,"");
				return null;
			}
		}


		//By default it is an user defined attribute
		SLNExpressionToken token = new SLNExpressionToken(name,value);
		return token;
	}

	void parseAtomIndex()   //!!!  ???????
	{	
		if (Character.isDigit(sln.charAt(curChar)))
			registerIndex(getInteger());
	}

	int getInteger()
	{
		//TODO to protect against very large integers

		if (!Character.isDigit(sln.charAt(curChar)))
			return(-1);

		int n = 0;
		while (curChar < nChars)
		{
			char ch = sln.charAt(curChar);
			if (Character.isDigit(ch))
			{		
				n = 10*n + Character.getNumericValue(ch);
				curChar++;
			}
			else
				break;
		}
		return(n);
	}

	void registerIndex(int n)
	{
		//TODO
	}


	void newError(String msg, int pos, String param)
	{
		SLNParserError error = new SLNParserError(sln, msg, pos, param);
		errors.add(error);
	}

	public String getErrorMessages()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < errors.size(); i++)
		{	
			sb.append(errors.get(i).getError() + "\n");
		}	
		return (sb.toString());
	}

	public ArrayList<SLNParserError> getErrors()
	{	
		return (errors);
	}

	void addAtom(SLNAtom atom)
	{
		container.addAtom(atom);
		if (prevAtom != null)
		{	
			addBond(prevAtom, atom);
		}

		prevAtom = atom;
	}

	void parseSpecialSymbol()
	{
		switch (sln.charAt(curChar))
		{
		//Bond expression symbols - bond types and logical operations
		case '~':	//any bond
		case '-':	//single bond
		case '=':	//double bond
		case '#':	//triple bond
		case ':':	//aromatic bond		
			parseBond();
			break;

		case '.':	//zero order bond - disconnection (new part of the structure)
			curChar++;
			curBond = null;
			break;

		case '@':	//ring closure
			parseRingClosure();
			break;	

		case '(':							
			if (prevAtom == null)
			{
				if (curComponent > 0)
				{	
					newError("Incorrect nested componet brackets", curChar+1,"");
				}
				else
				{
					brackets.push(prevAtom);
					maxCompNumber++;
					curComponent = maxCompNumber;
				}
			}	
			else			
				brackets.push(prevAtom);

			curChar++;
			break;	
		case ')':
			if (brackets.empty())
			{	
				//System.out.println("curChar = " + curChar);
				newError("Incorrect closing brackect", curChar+1,"");
				return;
			};				
			//Not empty brackets stack guarantees that curChar > 0
			if (sln.charAt(curChar-1)=='(')
			{	
				newError("Empty branch/substituent ", curChar+1,"");
				brackets.pop(); //This prevents generation of another error "There are unclosed brackets"
				return;
			};

			prevAtom = brackets.pop();
			if (prevAtom == null)
				curComponent = 0;
			curChar++;
			break;

		}
	}

	void addBond(SLNAtom atom0, SLNAtom atom1)
	{	
		IAtom[] atoms = new SLNAtom[2];
		atoms[0] = atom0;
		atoms[1] = atom1;
		if (curBond != null)
		{	
			curBond.setAtoms(atoms);
			container.addBond(curBond);
		}

		//default bond is set to be single bond
		SLNBond newBond = new SLNBond();
		curBond = newBond;
		curBond.bondType = SLNConst.B_TYPE_1;

	}


	void parseBond()
	{	
		if (curChar == 0)
		{
			newError("SLN string starts incorrectly with a bond expression", curChar,"");				
			return;
		}

		char boChar = sln.charAt(curChar);		
		curChar++;
		if (curChar == nChars)
		{
			newError("SLN string ends incorrectly with a bond expression", curChar,"");				
			return;
		}

		if (curBond == null)
			curBond = new SLNBond();

		curBond.bondType = SLNConst.SLNCharToBondTypeAttr(boChar);

		if (curChar < nChars)
			if (sln.charAt(curChar) == '[')
			{	
				String bondExpression = extractBondExpression();				
				analyzeBondExpression(bondExpression);
				curBond.bondExpression = curBondExp;
			}
	}	


	String extractBondExpression()
	{
		curChar++;
		int startPos = curChar;
		int openBrackets = 1;
		while ((curChar < nChars) && (openBrackets > 0) && (errors.size() == 0))
		{
			if (sln.charAt(curChar)=='[')			
				openBrackets++;
			else
				if (sln.charAt(curChar)==']')				
					openBrackets--;

			curChar++;
		}

		return sln.substring(startPos,curChar-1);
	}

	public void analyzeBondExpression(String bondExpr)
	{
		//	System.out.println("**BondExpr " + bondExpr);
		if (bondExpr.trim().equals(""))
		{
			newError("Empty bond expression", curChar+1,"");
			return;
		}
		curBondExp = new SLNBondExpression();
		int pos = 0;

		//Handle all attributes and logical operations
		while (pos < bondExpr.length())
		{
			if(bondExpr.charAt(pos) == ' ')
			{	
				pos++;
				if (FlagTolerateSpaces)
					continue;
				else
				{
					newError("Space symbol found: ", curChar, "");
					break;
				}	
			}

			if (Character.isLetter(bondExpr.charAt(pos)))
			{
				//Read attribute name
				int startPos = pos;
				while (pos < bondExpr.length())
				{
					if (Character.isLetter(bondExpr.charAt(pos))||
							Character.isDigit(bondExpr.charAt(pos)))
						pos++;
					else 
						break;
				}

				String attrName = bondExpr.substring(startPos, pos);
				if(pos < bondExpr.length())
				{
					if(bondExpr.charAt(pos) == '=')
						pos++;
				}
				if(pos >= bondExpr.length())				
				{
					//'=' is found but the end of bond expression is reached.
					newError("Missing value for attribute " + attrName + " ",curChar,"");
					return;
				}

				//Read attribute value (after '=')
				startPos = pos;
				while (pos < bondExpr.length())
				{
					if (Character.isLetter(bondExpr.charAt(pos)) ||
							Character.isDigit(bondExpr.charAt(pos))||
							(bondExpr.charAt(pos) == '*'))
						pos++;
					else
						break;
				}

				String attrValue = bondExpr.substring(startPos, pos);

				//Register attribute with a value 
				SLNExpressionToken newToken =  analyzeBondAttribute(attrName, attrValue);
				curBondExp.tokens.add(newToken);

				continue;
			}

			//Read special symbol
			switch  (bondExpr.charAt(pos))
			{
			case '!':
				SLNExpressionToken newToken0 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_NOT);  
				curBondExp.tokens.add(newToken0);
				break;
			case '&':
				SLNExpressionToken newToken1 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_AND);  
				curBondExp.tokens.add(newToken1);
				break;
			case '|':
				SLNExpressionToken newToken2 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_OR);  
				curBondExp.tokens.add(newToken2);				
				break;
			case ';':
				SLNExpressionToken newToken3 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_ANDLO);  
				curBondExp.tokens.add(newToken3);				
				break;	

			default:
			{
				newError("Incorrect symbol in bond expression '"+bondExpr.charAt(pos)+"' ",curChar,"");
				return;
			}
			}

			pos++;
		}
	}

	SLNExpressionToken analyzeBondAttribute (String name, String value)
	{
		/*	if (value == null)
			System.out.println("Attribute" + name);
		else
			System.out.println("Attribute " + name + " = " + value);
		 */
		//TODO
		//Handle bond type attribute
		if (name.equals("type"))
		{
			int typeParam = SLNConst.SLNStringToBondTypeAttr(value);
			if (extractError.equals(""))
			{
				if (typeParam == -1)
				{
					newError("Incorrect bond type value " + value, curChar,"");
					return null;
				}
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.B_ATTR_type,typeParam);
				return token;
			}
			else
			{
				newError("Incorrect bond type value " + value, curChar,"");
				return null;
			}
		}
		//Handle stereo-chemistry bond attribute
		if (name.equals("s"))
		{
			int param = SLNConst.SLNStringToBondStereoChemAttr(value);
			if (extractError.equals(""))
			{
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.B_ATTR_s,param);
				return token;
			}
			else
			{
				newError("Incorrect bond stereo-chemistry value " + value, curChar,"");
				return null;
			}
		}

		//By default it is an user defined attribute
		SLNExpressionToken token = new SLNExpressionToken(name,value);
		return token;
	}

	void parseRingClosure()
	{	
		curChar++;
		if (sln.charAt(curChar)==' ')			
		{
			newError("Missing ring closure value " , curChar,"");
		}

		curChar++;
		getAtomByID();
		curBond.atoms();
		container.addBond(curBond);
	}

	String getAtomByID()
	{
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			SLNAtom idAtom = (SLNAtom) container.getAtom(i);
			if(!(idAtom.atomID == 0))
				return idAtom.atomName;
			else
				newError("Missing atom ID value " , curChar,"");	
		}
		return null;
	}

	int extractInteger(String valueString)
	{
		extractError = "";
		try
		{
			int value;
			if (valueString.charAt(0) == '+')
				value = Integer.parseInt(valueString.substring(1));
			else	
				value = Integer.parseInt(valueString);
			return value;
		}
		catch(Exception e)
		{
			extractError = "Incorrect integer value " + valueString;
			return 0;
		}
	}

	double extractDouble(String valueString)
	{
		extractError = "";
		try
		{
			double value;
			if (valueString.charAt(0) == '+')
				value = Double.parseDouble(valueString.substring(1));
			else
				value = Double.parseDouble(valueString);
			return value;
		}
		catch(Exception e)
		{
			extractError = "Incorrect double value " + valueString;
			return 0.0;
		}
	}
}
