package ambit2.sln;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

import ambit2.smarts.DoubleBondAromaticityNotSpecified;
import ambit2.smarts.DoubleNonAromaticBond;
import ambit2.smarts.RingClosure;
import ambit2.smarts.SingleNonAromaticBond;
import ambit2.smarts.SingleOrAromaticBond;
import ambit2.smarts.SmartsAtomExpression;
import ambit2.smarts.SmartsBondExpression;
import ambit2.smarts.SmartsConst;

public class SLNParser 
{
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
			errors.add(new SLNParserError(sln, "Atom name", curChar, ""));
		
		SLNAtom newAtom = new SLNAtom();
		newAtom.atomType = atomType;
		newAtom.atomName = atomName;
		
		if (curChar < nChars)
			if (sln.charAt(curChar) == 'H')
			{
				int nH = 1;
				curChar++;
				if (curChar < nChars)
					if (Character.isDigit(sln.charAt(curChar)))
						nH = getInteger();
				newAtom.numHAtom = nH;
			}
		
		if (curChar < nChars)
			if (sln.charAt(curChar) == '[')
			{	
				String atomExpression = extractAtomExpression();
				
				System.out.println("AtExpr " + atomExpression);
				
				//TODO analyze atomExpression
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
		int openBrackets = 1;
		while ((curChar < nChars) && (openBrackets > 0) && (errors.size() == 0))
		{
			if (sln.charAt(curChar)=='[')
			 {
				 openBrackets++;
				 curChar++;
			 }
			 else
			 if (sln.charAt(curChar)==']')
			 {
				 openBrackets--;
				 curChar++;
			 }
			 else
				 break;
		}
		
		return sln.substring(curChar);
	}
	
	void parseAtomToken()
	{
		
	}

	void parseAtomIndex()   //!!!
	{
/*		if (sln.charAt(curChar) == '(')
		{				
			curChar++;
			if (sln.charAt(curChar) == '@')
				curChar++;
			{
				if (curChar == nChars)
				{	
					newError("Incorrect ring closure",curChar,"");
					return;
				}	

				if (Character.isDigit(sln.charAt(curChar)))
					registerIndex(getInteger());

				else
					newError("Incorrect ring closure",curChar,"");		
			}
		}
		else */
		{	
			if (Character.isDigit(sln.charAt(curChar)))
				registerIndex(getInteger());
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
			parseBondExpression();
			break;		

		//TODO
		//doesn't work for nested branches 
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
			newBond.bondType = SLNConst.BT_SINGLE;
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

	void parseBondExpression()
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

	
}
