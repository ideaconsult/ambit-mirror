package ambit2.sln;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

public class SLNParser 
{
	String sln;
	SLNContainer container;
	SLNDictionary globalDictionary = null;
	Stack<IQueryAtom> brackets = new Stack<IQueryAtom>();
	ArrayList<SLNParserError> errors = new ArrayList<SLNParserError>();
	ArrayList<IQueryBond>directionalBonds = new ArrayList<IQueryBond>();
	ArrayList<IQueryBond>processedDirBonds = new ArrayList<IQueryBond>();
	ArrayList<IQueryBond>processedDoubleBonds = new ArrayList<IQueryBond>();
	ArrayList<IQueryBond>newStereoDoubleBonds = new ArrayList<IQueryBond>();
	TreeMap<Integer,RingClosure> indexes = new TreeMap<Integer,RingClosure>();

	//Work variables for Component Level Grouping
	boolean FlagCLG = false; 
	int curComponent;
	public int numFragments;
	public int maxCompNumber;

	int curChar;	
	IQueryAtom prevAtom;
	int curBondType;
	int nChars;
	IQueryBond curBond;



	public SLNParser()
	{
		globalDictionary = PredefinedSLNDictionary.getDictionary();
	}

	public SLNParser(SLNDictionary globalDictionary)
	{
		this.globalDictionary = globalDictionary;
	}


	public SLNContainer parse(String sln)
	{
		this.sln = sln;
		container = new SLNContainer();
		errors.clear();
		init();
		parse();
		return container;
	}

	void init()
	{
		//TODO

		nChars = sln.length();
		prevAtom = null;
		curChar = 0;
		brackets.clear();
		directionalBonds.clear();
		curBond = null;
		curBondType = SLNConst.BT_UNDEFINED;
		indexes.clear();
	}

	void parse()
	{
		//TODO

		while ((curChar < nChars) && (errors.size() == 0))
		{
			if (Character.isLowerCase(sln.charAt(curChar)) && Character.isDigit(sln.charAt(curChar)))
				continue;
			else
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
		//Parsing the atom symbols allowed to be used without brackets [] 
		IQueryAtom curAtom = null;
		String symb = null;

		switch (sln.charAt(curChar))
		{
		/*  In contrast to SMILES, aromaticity 
		 * is not an atomic property, but a property of the bond
		 *  //Aromatic atoms
		case 'a':
			curAtom = new AromaticAtom(); 
			curChar++;
			break;
		case 'c': 
		case 'o':
		case 'n':
		case 's':
		case 'p':	
			char ch = Character.toUpperCase(sln.charAt(curChar));	
			curAtom = new AromaticSymbolQueryAtom(); 
			curAtom.setSymbol(Character.toString(ch));
			curChar++;
			break;
		 */
		case 'C':
			symb = "C";
			curChar++;
			if (curChar < nChars)
			{
				if (sln.charAt(curChar) == 'l')
				{	
					symb = "Cl";
					curChar++;
				}	
			}
			curAtom = new AliphaticSymbolQueryAtom();
			curAtom.setSymbol(symb);
			break;

		case 'B':
			symb = "B";
			curChar++;
			if (curChar < nChars)
			{
				if (sln.charAt(curChar) == 'r')
				{	
					symb = "Br";
					curChar++;
				}	
			}
			curAtom = new AliphaticSymbolQueryAtom();
			curAtom.setSymbol(symb);
			break;

		case 'A':
			curAtom = new AliphaticAtom(); 
			curChar++;
			break;
		case 'O':
		case 'N':
		case 'S':
		case 'P':
		case 'F':
		case 'I':			
			curAtom = new AliphaticSymbolQueryAtom(); 
			curAtom.setSymbol(Character.toString(sln.charAt(curChar)));
			curChar++;
			break;

		case 'H':
			symb = "H";
			curAtom = new HydrogenAtom();
			curChar++;
			if (Character.isDigit(sln.charAt(curChar)))
			{
				//TODO
			}
			break;
		}	

		if (curAtom == null)
			newError("Incorrect atomic symbol", curChar+1, "");
		else		
			addAtom(curAtom);
	}

	void parseAtomIndex()
	{
		//TODO
		if (sln.charAt(curChar) == '[')
		{				
			curChar++;
			if (curChar == nChars)
			{	
				newError("Incorrect ring closure",curChar,"");
				return;
			}	
			if (sln.charAt(curChar) == '@' && Character.isDigit(sln.charAt(curChar)))
				registerIndex(getInteger());
			else
				newError("Incorrect ring closure",curChar,"");				
		}
		else
		{	
			registerIndex(Character.getNumericValue(sln.charAt(curChar)));
			curChar++;			
		}
	}

	int getInteger()
	{
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
		SLNParserError err;
		//TODO
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

	void addAtom(IQueryAtom atom)
	{
		//TODO
	}

	void parseSpecialSymbol()
	{
		switch (sln.charAt(curChar))
		{
		case '*': //Any atom
			IQueryAtom curAtom = new AnyAtom();
			curChar++;
			addAtom(curAtom);			
			break;
			//Bond expression symbols - bond types and logical operations
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
			parseBondExpression();
			break;		
		case '[': //Atom index which > 9 (has two digits)
			parseAtomIndex();
			break;
		case ']':
			newError("Incorrect opening bracket ']' ", curChar+1,"");
			break;

		case '(':			
			if (prevAtom == null)
			{	
				if (FlagCLG)
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
					newError("Component Level Grouping is off: incorrect openning brackect", curChar+1,"");
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

	void addBond(IQueryAtom atom0, IQueryAtom atom1)
	{
		//TODO
	}

	void parseBondExpression()
	{
		//TODO
	}

	public void setComponentLevelGrouping(boolean flag)
	{
		FlagCLG = flag;
	}
}
