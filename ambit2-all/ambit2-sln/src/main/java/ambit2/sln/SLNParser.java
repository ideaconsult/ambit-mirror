package ambit2.sln;

import java.util.ArrayList;
import java.util.Set;
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
		//curBondType = SLNConst.BT_UNDEFINED;
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

		//Treat incorrectly used indexes
		if (indexes.size() != 0)
		{				
			newError("There are unclosed ring indices",-1, "");
			Set<Integer> keys = indexes.keySet();

			for (Integer key : keys)
				newError("Ring index " + key + " is unclosed",-1, "");
		}

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
		System.out.println("***** AtExpr " + atomExpr);
		
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
							 || Character.isDigit(atomExpr.charAt(pos)) )
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
							(atomExpr.charAt(pos) == '-')	 
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
				SLNExpressionToken newToken0 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_NOT,0,null,null);  
				curAtExp.tokens.add(newToken0);
				break;
			case '&':
				SLNExpressionToken newToken1 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_AND,0,null,null);  
				curAtExp.tokens.add(newToken1);
				break;
			case '|':
				SLNExpressionToken newToken2 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_OR,0,null,null);  
				curAtExp.tokens.add(newToken2);				
				break;
			case ';':
				SLNExpressionToken newToken3 = new SLNExpressionToken(SLNConst.LO + SLNConst.LO_ANDLO,0,null,null);  
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
		if (value == null)
			System.out.println("Attribute " + name);
		else
			System.out.println("Attribute " + name + "=" + value);
		
		//Handle charge attribute
		if (name.equals("charge"))
		{
			int charge = extractInteger(value);
			if (extractError.equals(""))
			{
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.A_ATTR_charge,charge,null,null);
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
			if (extractError.equals("") || isotope >= 0)
			{
				SLNExpressionToken token = new SLNExpressionToken(SLNConst.A_ATTR_I,isotope,null,null);
				return token;
			}
			else
			{
				newError("Incorrect isotope value " + value, curChar,"");
				return null;
			}
			
		}
		
		//By default it is an user defined attribute
		SLNExpressionToken token = new SLNExpressionToken(SLNConst.A_ATTR_USER_DEFINED,0,name,value);
		return token;
	}
	
	void parseA_ATTR_atomID()
	{
		//TODO
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
		case '.':	//new part of the structure		

		case '!':	// logical "not"
		case '&':	// logical "and"
		case '|':	// logical "or"
		case ';':	// low-binding version of "and"		
		case '@':	// ring closure
			parseBond();
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
		SLNBond newBond;
		if (curBond == null)
		{	
			newBond = new SLNBond();
			newBond.bondType = SLNConst.B_TYPE_1;
		}
		else
			newBond = curBond;

		IAtom[] atoms = new SLNAtom[2];
		atoms[0] = atom0;
		atoms[1] = atom1;
		newBond.setAtoms(atoms);
		container.addBond(newBond);

		//nullify info to read next bond
		curBond = null;
	}

	void parseBond()
	{	
		int lo = -1;
		int bo = SLNConst.getBondCharNumber(sln.charAt(curChar));		
		if (bo != -1)
		{
			curChar++;
			if (curChar == nChars)
			{
				newError("Smarts string ends incorrectly with a bond expression", curChar,"");				
				return;
			}
			//TODO
		}

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
