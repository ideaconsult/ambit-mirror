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

package ambit2.smarts;

import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;
import org.openscience.cdk.ringsearch.SSSRFinder;

/**
 * Implements SMARTS parser 
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SmartsParser 
{	
	String smarts;
	IQueryAtomContainer container;
	Vector<SmartsParserError> errors = new Vector<SmartsParserError>();
	Stack<IQueryAtom> brackets = new Stack<IQueryAtom>();
	Vector<SMARTSBond> directionalBonds = new Vector<SMARTSBond>(); 
	Vector<Integer> directions = new Vector<Integer>();
	//Vector<Integer> absDirections = new Vector<Integer>();
	Vector<SMARTSBond> processedDirBonds = new Vector<SMARTSBond>();
	Vector<SMARTSBond> processedDoubleBonds = new Vector<SMARTSBond>();
	Vector<SMARTSBond> newStereoDoubleBonds = new Vector<SMARTSBond>();
	TreeMap<Integer,RingClosure> indexes = new TreeMap<Integer,RingClosure>();
		
	boolean mNeedNeighbourData;
	boolean mNeedValencyData;
	boolean mNeedRingData;    //data with ring sizes for each atom
	boolean mNeedRingData2;	  //data with ring 'internal formal numbers' for each atom
	boolean mNeedExplicitHData;
	boolean mNeedParentMoleculeData;
	public boolean hasRecursiveSmarts;
	public boolean mSupportMOEExtension = true;
	public boolean mUseMOEvPrimitive = false;
	public boolean mSupportOpenEyeExtension = true;
	public boolean mSupportOpenBabelExtension = true;
	public boolean mSupportSmirksSyntax = false; 
	public boolean mSupportDoubleBondAromaticityNotSpecified = false;  //by default "=" is DoubleNonAromatic
	
	//Work variables for Component Level Grouping
	boolean FlagCLG = false;  
	int curComponent;
	public int numFragments;
	public int maxCompNumber;
	public Vector<IQueryAtomContainer> fragments = new Vector<IQueryAtomContainer>();
	public Vector<Integer> fragmentComponents = new Vector<Integer>();
	IQueryAtomContainer curFragment;
	
	//TreeMap<IAtom,Integer> atomComponents = new TreeMap<IAtom,Integer>();
	//TreeMap<IBond,Integer> bondComponents = new TreeMap<IBond,Integer>();
	
	//Basic work variables
	int curChar;	
	IQueryAtom prevAtom;
	SMARTSBond curBond;
	SmartsAtomExpression curAtExpr;
	int curBondType;
	int nChars;
	boolean insideRecSmarts;
	int curSmirksMapIndex = -1;
	
	public IQueryAtomContainer parse(String sm)
	{
		smarts = sm;
		container = new QueryAtomContainer();		
		errors.clear();
		nullifyDataFlags();
		init();
		parse();
		return container;
	}
	
	void init()
	{	
		nChars = smarts.length();
		brackets.clear();
		indexes.clear();
		directionalBonds.clear();
		directions.clear();
		prevAtom = null;
		curBond = null;
		curBondType = SmartsConst.BT_UNDEFINED;		
		curChar = 0;
		insideRecSmarts = false;
		//atomComponents.clear();
		//bondComponents.clear();
		fragments.clear();
		fragmentComponents.clear();
		curComponent = 0;
		numFragments = 0;
		maxCompNumber = 0;
	}
	
	void parse()
	{	
		while ((curChar < nChars) && (errors.size()== 0))
		{
			if (Character.isLetter(smarts.charAt(curChar)))
			{
				parseAtom();
			}
			else
			if (Character.isDigit(smarts.charAt(curChar)))
			{
				parseAtomIndex();	
			}
			else 
			{
				parseSpecialSymbol();   // symbol '%' is handled by  parseAtomIndex() as well
			}
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
		
		if (directionalBonds.size() > 0)
			setDoubleBondsStereoInfo();
		
		//Handle chirality info
		for (int i = 0; i < container.getAtomCount(); i++)		
			if (container.getAtom(i) instanceof SmartsAtomExpression)
			{
				SmartsAtomExpression sa = (SmartsAtomExpression) container.getAtom(i); 				
				handleChirality(sa);
			}	
		
		
		setNeededDataFlags();
		
		
		//The global work variables are stored in order to use them again in the recursion 
		//and to preserve the original values for the code after recursion	
		IQueryAtomContainer container0 = container;
		Vector<IQueryAtomContainer> fragments0 = fragments;
		Vector<Integer> fragmentComponents0 = fragmentComponents;
		
		//Handle recursive smarts 
		for (int i = 0; i < container0.getAtomCount(); i++)
		{
			if (container0.getAtom(i) instanceof SmartsAtomExpression)
			{
				SmartsAtomExpression sa = (SmartsAtomExpression) container0.getAtom(i); 
				for (int j = 0; j < sa.recSmartsStrings.size(); j++)
				{	
					hasRecursiveSmarts = true;
					smarts = sa.recSmartsStrings.get(j);
					
					//New instances of the global working variables are created for the recursion process
					//This is needed in order to not mess up the original containers.
					//Generally within recursive SMARTS expression fragments and component level groping do not make sense, 
					//however in the current version the parser will parse this information, but it will not be stored any where
					container = new QueryAtomContainer();
					fragments = new Vector<IQueryAtomContainer>();
					fragmentComponents = new Vector<Integer>();
					
					init();
					insideRecSmarts = true;
					parse();
					sa.recSmartsContainers.add(container);
					insideRecSmarts = false;
				}		
			}
		}
		
		//Restoring the global work variables after recursion
		container = container0;
		fragments = fragments0;
		fragmentComponents = fragmentComponents0;
		
	}
	
	public void setComponentLevelGrouping(boolean flag)
	{
		FlagCLG = flag;
	}
	
	public boolean needNeighbourData()
	{
		return(mNeedNeighbourData);
	}
	
	public boolean needExplicitHData()
	{
		return(mNeedExplicitHData);
	}
	
	public boolean needValencyData()
	{
		return(mNeedValencyData);
	}
	
	public boolean needRingData()
	{
		return(mNeedRingData);
	}
	
	public boolean needRingData2()
	{
		return(mNeedRingData2);
	}
	
	public boolean needParentMoleculeData()
	{
		return(mNeedParentMoleculeData);
	}
	
	void nullifyDataFlags()
	{
		mNeedNeighbourData = false;		
		mNeedValencyData = false;
		mNeedRingData = false;
		mNeedRingData2 = false;
		mNeedExplicitHData = false;
		mNeedParentMoleculeData = false;
		hasRecursiveSmarts = false;
	}
	
	public void setNeededDataFlags()
	{	
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			if (container.getAtom(i) instanceof SmartsAtomExpression)
			{
				SmartsAtomExpression sa = (SmartsAtomExpression)container.getAtom(i);
				for (int j = 0; j < sa.tokens.size(); j++)
				{	
					SmartsExpressionToken tok = (SmartsExpressionToken)sa.tokens.get(j);					
					if (tok.type == SmartsConst.AP_H)
						mNeedExplicitHData = true;
					
					if ((tok.type == SmartsConst.AP_D) ||
						(tok.type == SmartsConst.AP_X) ||
						(tok.type == SmartsConst.AP_H) ||
						(tok.type == SmartsConst.AP_h))
						mNeedNeighbourData = true;
					else
					{						
						if ((tok.type == SmartsConst.AP_iMOE) ||
							(tok.type == SmartsConst.AP_vMOE) ||
							(tok.type == SmartsConst.AP_OB_Hybr))	
							mNeedParentMoleculeData = true;
						else
							if (tok.type == SmartsConst.AP_x)
							{	
								mNeedParentMoleculeData = true;
								mNeedRingData2 = true;
							}		
							else
							{						
								if (tok.type == SmartsConst.AP_v) 
									mNeedValencyData = true;
								else							
									if ((tok.type == SmartsConst.AP_R) ||
											(tok.type == SmartsConst.AP_r))
										mNeedRingData = true;
							}
					}	
				}	
			}
		}
		
		//Additional check for the flag variables based on the bond list
		if (mNeedRingData == false)
		{	
			for (int i = 0; i < container.getBondCount(); i++)
			{
				if (container.getBond(i) instanceof SmartsBondExpression)
				{
					SmartsBondExpression sb = (SmartsBondExpression)container.getBond(i);
					for (int j = 0; j < sb.tokens.size(); j++)
					{
						if (sb.tokens.get(j).intValue() == SmartsConst.BT_RING)
						{	
							mNeedRingData2 = true;
							break;
						}	
					}
					if (mNeedRingData2)
						break;
				}
				else
					if (container.getBond(i) instanceof RingQueryBond)
					{
						mNeedRingData2 = true;
						break;
					}
			}
		}		
	}
	
	
	void newError(String msg, int pos, String param)
	{
		SmartsParserError err;
		if (insideRecSmarts)			
			err = new SmartsParserError(smarts,"Inside recursive Smarts: "+msg,pos, param);
		else
			err = new SmartsParserError(smarts,msg,pos,param);
		errors.add(err);
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
	
	public Vector<SmartsParserError> getErrors()
	{	
		return (errors);
	}
	
	void newFragment()
	{
		numFragments++; //A new fragments is started. It is inside "current component"
		curFragment = new QueryAtomContainer();
		fragments.add(curFragment);
		fragmentComponents.add(new Integer(curComponent));
	}
	
	void addAtom(IQueryAtom atom)
	{	
		container.addAtom(atom);
		if (prevAtom != null)
		{	
			curFragment.addAtom(atom);
			addBond(prevAtom, atom);
		}	
		else
		{	
			newFragment(); //A new fragments is started. It is inside "current component"
			curFragment.addAtom(atom);
		}	
		
		
		if (mSupportSmirksSyntax)
		{	
			if (curSmirksMapIndex > -1)
				atom.setProperty("SmirksMapIndex", new Integer(curSmirksMapIndex));
			
			//resetting for the next atom
			curSmirksMapIndex = -1;
		}	
		
		//resetting for the next atom
		prevAtom = atom;
		curBond = null;
		curBondType = SmartsConst.BT_UNDEFINED;
	}
	
	void addBond(IQueryAtom atom0, IQueryAtom atom1)
	{
		if (curBond == null)
		{
			switch (curBondType)
			{
			case SmartsConst.BT_ANY:
				curBond = new AnyOrderQueryBond();				
				break;				
			case SmartsConst.BT_SINGLE:				
				curBond = new SingleNonAromaticBond();
				break;
			case SmartsConst.BT_DOUBLE:
				if (mSupportDoubleBondAromaticityNotSpecified)
					curBond = new DoubleBondAromaticityNotSpecified();
				else
					curBond = new DoubleNonAromaticBond();									
				break;
			case SmartsConst.BT_TRIPLE:	
				curBond = new OrderQueryBond(IBond.Order.TRIPLE);									
				break;					
			/*
			case SmartsConst.BT_DOUBLE:
				curBond = new DoubleStereoBond();
				break;
			*/	
			case SmartsConst.BT_AROMATIC:
				curBond = new AromaticQueryBond();				
				break;
			case SmartsConst.BT_RING:
				curBond = new RingQueryBond();				
				break;	
			case SmartsConst.BT_DOWN:
			case SmartsConst.BT_UP:
			case SmartsConst.BT_DOWNUNSPEC:
			case SmartsConst.BT_UPUNSPEC:
				//Directional bond is treated as a single bond.
				//Additionally this bond is stored in contaner directionaBonds for further processing
				curBond = new OrderQueryBond(IBond.Order.SINGLE);				
				directionalBonds.add(curBond);
				directions.add(new Integer(curBondType));
				break;
				
			case SmartsConst.BT_UNDEFINED:
				curBond = new SingleOrAromaticBond();								
				break;
			}
		}
		
		IAtom[] atoms = new IAtom[2];
	    atoms[0] = (IAtom)atom0;
	    atoms[1] = (IAtom)atom1;
	    curBond.setAtoms(atoms);
	    container.addBond(curBond);
	    curFragment.addBond(curBond);
	    //System.out.println("--> " + SmartsHelper.bondToStringExhaustive(container,curBond));
	    //System.out.println(SmartsHelper.getBondsString(container)+"\n");
	}
		
	
	void parseAtom()
	{
		//Parsing the atom symbols allowed to be used without brackets [] 
		IQueryAtom curAtom = null;
		String symb = null;
		switch (smarts.charAt(curChar))
		{
		//Aromatic atoms
		case 'a':
			curAtom = new AromaticAtom(); 
			curChar++;
			break;
		case 'c': 
		case 'o':
		case 'n':
		case 's':
		case 'p':	
			char ch = Character.toUpperCase(smarts.charAt(curChar));			
			curAtom = new AromaticSymbolQueryAtom(); 
			curAtom.setSymbol(Character.toString(ch));
			curChar++;
			break;
			
		case 'C':
			symb = "C";
			curChar++;
			if (curChar < nChars)
			{
				if (smarts.charAt(curChar) == 'l')
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
				if (smarts.charAt(curChar) == 'r')
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
			curAtom.setSymbol(Character.toString(smarts.charAt(curChar)));
			curChar++;
			break;
		}	
		
		if (curAtom == null)
			newError("Incorrect atomic symbol", curChar+1, "");
		else		
			addAtom(curAtom);
	}
	
	void parseAtomIndex()
	{		
		if (smarts.charAt(curChar) == '%')
		{				
			curChar++;
			if (curChar == nChars)
			{	
				newError("Incorrect ring closure",curChar,"");
				return;
			}	
			
			if (Character.isDigit(smarts.charAt(curChar)))
				registerIndex(getInteger());
			else
				newError("Incorrect ring closure",curChar,"");				
		}
		else
		{	
			registerIndex(Character.getNumericValue(smarts.charAt(curChar)));
			curChar++;			
		}	
	}
	
	int getInteger()
	{
		if (!Character.isDigit(smarts.charAt(curChar)))
			return(-1);
		
		int n = 0;
		while (curChar < nChars)
		{
			char ch = smarts.charAt(curChar);
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
		Integer i = new Integer(n);
		RingClosure rc = indexes.get(i);
		
		if (rc == null)
		{
			//Currently this index is not associated with any atom 
			//i.e. it is a new index, or if was used previously the ring has been closed yet and this index can be used again
			
			RingClosure rc1 =  new RingClosure();
			rc1.firstAtom = prevAtom;
			
			if (curBond == null)					
				rc1.firstBond = curBondType;				
			else
			{	
				//This is no longer treated as an error as it was previously (since 30.07.2013)
				//newError("Use of a bond expression for the first appearance of atom index",curChar+1,"");
				SmartsBondExpression sbe = (SmartsBondExpression)curBond;
				rc1.firstBond = curBondType;
				rc1.firstBondExpression = sbe;
			}
			indexes.put(i,rc1);
			
			//After first index appearance current bond data must be reset 
			//For example: without reseting, a bug is caused when ring closure is with a double bond 
			//e.g. CC=1CC=1 is parsed like it is CC1=CC=1
			curBond = null; 
			curBondType = SmartsConst.BT_UNDEFINED;
		}
		else
		{
			//Currently this index is already associated with an atom
			//Therefore this is the second appearance of this index i.e. it defines a ring closure 
			
			
			if (rc.firstBondExpression != null)  //treating an opened ring closure with a bond expression
			{
				if (curBond == null)
				{
					if (curBondType == SmartsConst.BT_UNDEFINED)
					{
						curBond = rc.firstBondExpression;
						addBond(rc.firstAtom, prevAtom);
					}
					else
					{	
						//The closing bond type is not a bond expression
						newError("Ring closure index "+n+" is associated with a bond expression and a bond type",-1,"");
					}	
				}
				else
				{	
					//compare the opening and closing bond expressions --> they must be identical
					if (rc.firstBondExpression.isIdenticalTo((SmartsBondExpression)curBond))
					{
						addBond(rc.firstAtom, prevAtom);
					}
					else
					{
						//The closing and opening bond expressions are not identical
						newError("Ring closure index "+n+" is associated with two different bond expressions",-1,"");
					}
				}
				
				//Reseting the "current" bond data
				curBond = null;
				curBondType = SmartsConst.BT_UNDEFINED;	
			}
			else
			{	
				if (rc.firstBond == SmartsConst.BT_UNDEFINED) //First index position is with UNDEFINED bond type
				{
					addBond(rc.firstAtom, prevAtom);
					//Reseting the "current" bond data
					curBond = null; 
					curBondType = SmartsConst.BT_UNDEFINED;				
				}	
				else 
				{
					//First index position is with a DEFINED bond type (but not a bond expression)
					
					if (curBond == null)
					{	
						if (curBondType == SmartsConst.BT_UNDEFINED)	
						{
							//It is allowed to have bond definition at the first index position
							//when the second position is with undefined bond type
							curBondType = rc.firstBond;
							addBond(rc.firstAtom, prevAtom);
							//Reseting the "current" bond data
							curBond = null; 
							curBondType = SmartsConst.BT_UNDEFINED;
						}
						else
						{	
							//Both index positions have bond types - they must be equal 
							if (rc.firstBond != curBondType)
							{	
								newError("Ring closure index "+n+" is associated with two different bond types",-1,"");
							}	
							else
							{	
								addBond(rc.firstAtom, prevAtom);
								//Reseting the "current" bond data
								curBond = null;
								curBondType = SmartsConst.BT_UNDEFINED;						
							}
						}
					}
					else
					{	
						//Closing the ring at the second index position with a bond expression
						//Then error is reported
						newError("Ring closure index "+n+" is associated with a bond type and a bond expression",-1,"");
					}	
				}
				
			}//if (rc.firstBondExpression != null)
			
			//This index is made available for another usage for ring closure definitions
			//This is allowed by SMARTS standard but it makes some SMARTS more difficult to read.
			indexes.remove(i);	
		}
	}
	
	void parseSpecialSymbol()
	{
		switch (smarts.charAt(curChar))
		{
		case '*': //Any atom
			IQueryAtom curAtom = new AnyAtom();
			curChar++;
			addAtom(curAtom);			
			break;
		//Bond expression symbols - bond types and logical operations		  
		case '-':
		case '=':
		case '#':
		case '~':		
		case '/':
		case '\\':
		case ':':		
		case '@':
		case '!':
		case '&':  
		case ',':
		case ';':
			parseBondExpression();
			break;		
		case '%': //Atom index which > 9 (has two digits)
			parseAtomIndex();
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
			if (smarts.charAt(curChar-1)=='(')
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
		case '[':
			parseAtomExpression();
			break;
		case ']':
			newError("Incorrect closing bracket ']' ", curChar+1,"");
			break;	
		case '.':
			if (FlagCLG)
			{
				curChar++;
				prevAtom = null;
				curBond = null;
				curBondType = SmartsConst.BT_UNDEFINED;
			}
			else
				newError("Zero bond order (disclosure) is not allowed. Component Level Grouping is off.", curChar+1,"");
			break;
			
		default:
			newError("Incorrect symbol", curChar+1,"");
			break;
		}
	}
	
	void parseBondExpression()
	{
		//System.out.println("** curChar = " + curChar+"  " + smarts.charAt(curChar));
		SmartsBondExpression sbe;
		int lo = -1;
		int bo = SmartsConst.getBondCharNumber(smarts.charAt(curChar));		
		if (bo != -1)
		{
			curChar++;
			if (curChar == nChars)
			{
				newError("Smarts string ends incorrectly with a bond expression", curChar,"");				
				return;
			}
			curBondType = bo;
			//Checking for bond types  /?   \?
			if ( ((curBondType == SmartsConst.BT_UP)||(curBondType == SmartsConst.BT_DOWN)) 
					&& (smarts.charAt(curChar) == '?') )
			{	
				curChar++;
				if (curChar == nChars)
				{
					newError("Smarts string ends incorrectly with a bond expression", curChar,"");				
					return;
				}
				
				if (curBondType == SmartsConst.BT_UP)
					curBondType = SmartsConst.BT_UPUNSPEC;
				else
					if (curBondType == SmartsConst.BT_DOWN)
						curBondType = SmartsConst.BT_DOWNUNSPEC;
			}
			
			if ((SmartsConst.getBondCharNumber(smarts.charAt(curChar)) == -1) && 
				(SmartsConst.getLogOperationCharNumber(smarts.charAt(curChar)) == -1))
			{	 
				return; //This is one symbol bond expression
			}	 
			
			curBond = new SmartsBondExpression();
			sbe = (SmartsBondExpression)curBond;
			sbe.tokens.add(new Integer(bo));
		}
		else //First symbol from the bond expression is a logical operation
		{	
			lo = SmartsConst.getLogOperationCharNumber(smarts.charAt(curChar));
			if (lo == SmartsConst.LO_NOT)
			{
				curBond = new SmartsBondExpression();
				sbe = (SmartsBondExpression)curBond;
				sbe.tokens.add(new Integer(SmartsConst.LO+lo));
			}
			else
			{	
				newError("Incorrect bond expression", curChar+1,"");
				return;
			}
			curChar++;
		}		
		//System.out.println("sbe" + sbe.toString());
	
		bo = SmartsConst.getBondCharNumber(smarts.charAt(curChar));
		//Checking for bond types  /?   \?
		if  ((bo == SmartsConst.BT_UP)||(bo == SmartsConst.BT_DOWN))
		{	
			if (curChar+1 == nChars)
			{
				newError("Smarts string ends incorrectly with a bond expression", curChar+1,"");				
				return;
			}
			if (smarts.charAt(curChar+1) == '?') 
			{	
				if (bo == SmartsConst.BT_UP)
					bo = SmartsConst.BT_UPUNSPEC;
				else
					bo = SmartsConst.BT_DOWNUNSPEC;
				curChar++;
			}	
		}		
		
		if (bo == -1)
			lo = SmartsConst.getLogOperationCharNumber(smarts.charAt(curChar));
		else 
			lo = -1;
		
		while ((bo != -1) || (lo != -1)) 
		{
			int prevToken = ((Integer)sbe.tokens.lastElement()).intValue();
			//System.out.println("prevToken = " + prevToken);
			//System.out.println("sbe" + sbe.toString());
			if (bo != -1)
			{
				if (prevToken < SmartsConst.LO)
				{
					//Adding default HI_AND(&)         /it was OR (,) but it was wrong!!! /
					sbe.tokens.add(new Integer(SmartsConst.LO+SmartsConst.LO_AND));
				}
				sbe.tokens.add(new Integer(bo));
			}
			else
			{
				if (prevToken >= SmartsConst.LO)
				{					
					if (lo != SmartsConst.LO_NOT)
					{
						newError("Incorrect bond expression - no oprenad between logical operation", curChar+1,"");
						return; 
					}	
				}
				sbe.tokens.add(new Integer(SmartsConst.LO+lo));
			}				
			
			curChar++;
			if (curChar == nChars)
			{
				newError("Smarts string ends incorrectly with a bond expression", curChar,"");
				return;
			}
			bo = SmartsConst.getBondCharNumber(smarts.charAt(curChar));
			//Checking for bond types  /?   \?
			if  ((bo == SmartsConst.BT_UP)||(bo == SmartsConst.BT_DOWN))
			{	
				if (curChar+1 == nChars)
				{
					newError("Smarts string ends incorrectly with a bond expression", curChar+1,"");				
					return;
				}
				if (smarts.charAt(curChar+1) == '?') 
				{	
					if (bo == SmartsConst.BT_UP)
						bo = SmartsConst.BT_UPUNSPEC;
					else
						bo = SmartsConst.BT_DOWNUNSPEC;
					curChar++;
				}	
			}
			
			if (bo == -1)
				lo = SmartsConst.getLogOperationCharNumber(smarts.charAt(curChar));
			else 
				lo = -1;
		}
	}
	
		
	void parseAtomExpression()
	{	
		curChar++;
		int openBrackets = 1;
		curAtExpr = new SmartsAtomExpression();
		while ((curChar < nChars) && (openBrackets > 0) && (errors.size() == 0))
		{	
			if (smarts.charAt(curChar)=='[')
			 {
				 openBrackets++;
				 curChar++;
			 }
			 else
			 if (smarts.charAt(curChar)==']')
			 {
				 openBrackets--;
				 curChar++;
			 }
			 else
				 parseAtomPrimitive();
		}
		
		
		if (errors.size() == 0)
		{
			//Check whether the last token  it is a logical operation (i.e. an operand is missing)
			//e.g. [C;]		
			int lastTok = getLastAtomToken();		
			if (lastTok == -1) //check for empty expressions e.g. []
				newError("Empty atom expression", curChar,""); //curChar+1 is not added here
			else
				if (lastTok >= SmartsConst.LO)			
					newError("Atom expression incorrectly ends with logical operation. Operand is missing", curChar,"");  //curChar+1 is not added here
		}
		
		
		if (errors.size() > 0)
			return;
		
		if (openBrackets > 0)		
			newError("Incorrect atom expression - [] block is not closed",curChar,"");
		else
			addAtom(curAtExpr);
	}
	
	public void testForDefaultAND()
	{
		int tok = getLastAtomToken();
		if (tok >= 0 && tok < SmartsConst.LO)
			curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.LO+SmartsConst.LO_AND,0));
	}
	
	public int testFor2CharElement()
	{
		if (curChar < nChars-1)
		{
			if (Character.isLowerCase(smarts.charAt(curChar+1)))
			{
				String symbol = smarts.substring(curChar,curChar+2);			
				curChar+=2;
				int par = SmartsConst.getElementNumber(symbol);			
				if (par == -1)
					newError("Incorrect atom type in atom expression", curChar,"");
				else
					curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_A,par));
				return (1);
			}	
		}
		return(0);
	}
	
	public int getLastAtomToken()
	{
		if (curAtExpr.tokens.size()==0)
			return(-1);
		SmartsExpressionToken tok = (SmartsExpressionToken) (curAtExpr.tokens.get(curAtExpr.tokens.size()-1));
		return(tok.type);
	}
	
	void parseAtomPrimitive()
	{
		if (Character.isLetter(smarts.charAt(curChar)))
		{
			switch (smarts.charAt(curChar))
			{			
			case 'a':
				testForDefaultAND();
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_a,0));			
				curChar++;
				break;				
			case 'A':
				parseAP_A();
				break;	
			case 'D':				
				parseAP_AtomPrimitive(SmartsConst.AP_D,true);
				break;
			case 'H':				
				parseAP_AtomPrimitive(SmartsConst.AP_H,true);
				break;
			case 'h':				
				parseAP_AtomPrimitive(SmartsConst.AP_h,false);
				break;	
			case 'R':				
				parseAP_RPrimitive(true);
				break;	
			case 'r':				
				parseAP_rPrimitive(false);
				break;	
			case 'v':
				if ((mSupportMOEExtension) && (mUseMOEvPrimitive) )
					parseAP_AtomPrimitive(SmartsConst.AP_vMOE,false);	
				else	
					parseAP_AtomPrimitive(SmartsConst.AP_v,false);
				break;
			case 'X':				
				parseAP_AtomPrimitive(SmartsConst.AP_X,true);
				break;
			case 'x':				
				parseAP_xPrimitive(false);
				break;
			case 'i':
				if (mSupportMOEExtension)
					parseAP_iPrimitive(false);
				else
					parseAP_AtomSymbol();	
				break;
			case 'q':  //q_MOE is equivalent to Daylight x  
				if (mSupportMOEExtension)
					parseAP_xPrimitive(false);
				else
					parseAP_AtomSymbol();
				break;
			default:
				parseAP_AtomSymbol();	
			}
		}
		else
		if (Character.isDigit(smarts.charAt(curChar)))
		{
			parseAP_AtomMass();
		}
		else 
		{
			switch (smarts.charAt(curChar))
			{
			case ' ':
				curChar++;
				break;
			case '!':
				parseAP_NOT();
				break;
			case '&':
				parseAP_LogOperation(SmartsConst.LO_AND);
				break;
			case ',':
				parseAP_LogOperation(SmartsConst.LO_OR);
				break;
			case ';':
				parseAP_LogOperation(SmartsConst.LO_ANDLO);
				break;
			case '#':
				parseAP_AtomNumber();
				break;
			case '@':
				parseAP_Chirality();
				break;
			case '-':
				parseAP_Charge(-1);
				break;	
			case '+':
				parseAP_Charge(1);
				break;	
			case '$':
				parseAP_RecursiveSmarts();
				break;	
			case '*':
				testForDefaultAND();
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_ANY,0));			
				curChar++;
				break;
			case '^':
				if (mSupportOpenBabelExtension)
					parseAP_OpenBabel_Hybridization();
				else
				{
					newError("Incorrect symbol in atom expression", curChar+1,"");
					curChar++;
				}
				break;
			case ':':
				if (mSupportSmirksSyntax)
					parseAP_SmirksMaping();
				else
				{
					newError("Smirks mapping is not switched on!", curChar+1,"");
					curChar++;
				}
				break;
			default:
				newError("Incorrect symbol in atom expression", curChar+1,"");
				curChar++;
			}
		}		
	}
	
	void parseAP_AtomSymbol()
	{	
		testForDefaultAND();
		if (Character.isLowerCase(smarts.charAt(curChar)))
		{	
			char symbol = Character.toUpperCase(smarts.charAt(curChar)); 
			int par = SmartsConst.getElementNumberFromChar(symbol);	
			curChar++;
			if (par == -1)
				newError("Incorrect aromatic atom type in atom expression", curChar,"");
			else
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_a,par));			
			
		}
		else
		{
			int n = 1;
			if (curChar < nChars-1)
				if (Character.isLowerCase(smarts.charAt(curChar+1)))
					n = 2;
			//TODO to handle '3-char' element  
			String symbol = smarts.substring(curChar,curChar+n);			 
			curChar+=n;
			int par = SmartsConst.getElementNumber(symbol);			
			if (par == -1)
				newError("Incorrect aliphatic atom type in atom expression", curChar,"");
			else
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_A,par));
		}
	}
	
	void parseAP_NOT()
	{	
		testForDefaultAND();
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.LO+SmartsConst.LO_NOT,0));
		curChar++;
	}
	
	void parseAP_LogOperation(int logOp)
	{
		int prevTok = getLastAtomToken();
		if (prevTok < 0)
			newError("Atom expression incorrectly starts with logical opreation", curChar+1,"");
		else
		{	
			if (prevTok >= SmartsConst.LO)
				newError("Incorrect expression - missing operand", curChar+1,"");
			else	
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.LO+logOp,0));
		}
		curChar++;
	}
	
	void parseAP_AtomMass()
	{
		testForDefaultAND();
		int mass = getInteger();
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_Mass,mass)); 
	}	
	
	void parseAP_A()
	{
		testForDefaultAND();
		if (testFor2CharElement() == 1)
			return;
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_A,0));			
		curChar++;
	}
	
	void parseAP_AtomPrimitive(int logOpType, boolean elTest)
	{
		//This function is applied for primitives D,H,h,v,X,r, vMOE
		testForDefaultAND();
		if (elTest)
			if (testFor2CharElement() == 1)
				return;
		int symbolPos = curChar;
		curChar++;		
		int par = getInteger();		
		if (par == -1)
		{	
			if ((logOpType == SmartsConst.AP_H))				
				if (isHydrogenAtom(symbolPos))
				{	
					//This token is treated as hydrogen atom 
					//not as atom attribute "number of attached H atoms" 
					curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_A,1));
					return;
				}				
			par = 1;
		}		
		curAtExpr.tokens.add(new SmartsExpressionToken(logOpType,par));
	}
	
	boolean isHydrogenAtom(int symbolPos)
	{
		//Cases in which 'H' token is treated as Hydrogen atom
		//according to changes of SMARTS from version 4.4x
		//Following case are treated as hydrogen atom:
		//[H], [2H], [H,F]  (not sure for [H,F] that it must be treated as a H atom according SMARTS standard)
		//[*H] is treated as H count attribute		
		char prevCh = smarts.charAt(symbolPos-1);
		if (prevCh == '*')
			return(false);
		if (prevCh == '[')
			return(true);
		if (prevCh == '&')
		{
			if (curAtExpr.tokens.size() > 0)
			{	
				SmartsExpressionToken tok =  curAtExpr.tokens.get(curAtExpr.tokens.size()-1);
				if (tok.type == SmartsConst.AP_Mass)
					return(true);
			}	
		}
			
		return(false);	
	}
	
	void parseAP_RPrimitive(boolean elTest)
	{	
		testForDefaultAND();
		if (elTest)
			if (testFor2CharElement() == 1)
				return;
		curChar++;
		int par = getInteger(); //-1 is a possible parameter value for the default case (only "R")		
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_R,par));
	}
	
	void parseAP_rPrimitive(boolean elTest)
	{	
		testForDefaultAND();
		if (elTest)
			if (testFor2CharElement() == 1)
				return;
		curChar++;
		int par = getInteger(); 
		if (par == -1)
		{
			par = 1;
		}
		else
		{
			if (par < 3)
				newError("Incorrect integer value for r-primitive!", curChar,"");
		}
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_r,par));
	}
	
	void parseAP_xPrimitive(boolean elTest)
	{
		testForDefaultAND();
		if (elTest)
			if (testFor2CharElement() == 1)
				return;
		curChar++;
		int par = getInteger(); 
		if (par == -1)
		{
			//This code is not realy needed. But it is written
			//to stress the handling of default case.
			par = -1; //The default value 
		}		
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_x,par));
	}
	
	void parseAP_iPrimitive(boolean elTest)
	{
		testForDefaultAND();
		curChar++;
		int par = 0;
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_iMOE,par));
	}
	
	void parseAP_AtomNumber()
	{
		testForDefaultAND();
		curChar++;
		
		if (mSupportMOEExtension)  //#G<n>  #N  #X
		{
			if (curChar < nChars)
			{
				if (parseMOEExpression())
					return;
			}	
		}	
		
		int par = getInteger();
		if (par == -1)
			newError("Incorrect atomic number after #", curChar,"");
		else
		{	
			if ((par < 1) || (par >= SmartsConst.elSymbols.length))
				newError("Incorrect atomic number after #", curChar,"");
			else
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_AtNum,par));
		}	
	}
	
	
	void parseAP_OpenBabel_Hybridization()
	{	
		testForDefaultAND();
		curChar++;
		
		int par = getInteger();
		if (par == -1)
			newError("Missing hybridization parameter after ^", curChar,"");
		else
		{	
			if ((par < 1) || (par >3))
				newError("Incorrect hybridization after ^", curChar,"");
			else
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_OB_Hybr,par));
		}	
	}
	
	void parseAP_SmirksMaping()
	{
		//it is not called in this case  testForDefaultAND()
		curChar++;
		
		int par = getInteger();
		if (par == -1)
			newError("Missing Smirks Mapping index after ", curChar,"");
		else
		{	
			if ((par < 0) || (par >10000))
				newError("Incorrect Smirks Mapping index ", curChar,"");
			else
			{	
				//System.out.println("Map Index " + par);
				if (curSmirksMapIndex > 0)
				{
					newError("Smirks Mapping index is specified more than once per atom ",
							curChar,"");
				}
				else
					curSmirksMapIndex = par;
			}
		}
	}
	
	boolean parseMOEExpression()
	{
		//Support for the following primitives: #G<n>  #N  #X
		if (smarts.charAt(curChar) == 'G')
		{
			curChar++;
			int par = getInteger();
			if (par == -1)
				newError("Incorrect atomic number after #G", curChar,"");
			else
			{	
				if ((par < 1) || (par > 8))
					newError("Incorrect atomic number after #G", curChar,"");
				else
					curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_GMOE,par));
				
			}
			return(true);
		}
		else
			if (smarts.charAt(curChar) == 'N')
			{
				curChar++;
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_NMOE,0));
				return(true);
			}
			else
				if (smarts.charAt(curChar) == 'X')
				{
					curChar++;
					curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_XMOE,0));
					return(true);
				}
		
		return(false);
	}
	
	void parseAP_Chirality()
	{
		//Currently only simple chiral classes are implemented  @,@@
		testForDefaultAND();
		curChar++;
		int par = SmartsConst.ChC_Clock;
		if (smarts.charAt(curChar) == '@')		
			curChar++;
		else
			par = SmartsConst.ChC_AntiClock;
		
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_Chiral,par));
	}
	
	void parseAP_Charge(int sign)
	{
		testForDefaultAND();
		curChar++;
		char ch;
		if (sign < 0)
			ch = '-';
		else		
			ch = '+';
		int num = 1; //The number of '+' or '-' symbols
		while (curChar < nChars)
		{
			if (smarts.charAt(curChar) == ch)
			{
				num++;
				curChar++;
			}
			else
				break;
		}
		
		if (num > 1)		
			curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_Charge,sign*num));
		else
		{
			if (curChar < nChars)
			{
				if (Character.isDigit(smarts.charAt(curChar)))
				{
					int par = getInteger();
					if (par == -1)
						newError("Incorrect charge ", curChar,"");
					else
						curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_Charge,sign*par));
				}
				else
					curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_Charge,sign));
			}
			else
				curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_Charge,sign));			
		}
	}		
	
	public void parseAP_RecursiveSmarts()
	{
		curChar++;
		if (curChar >= nChars)
		{
			newError("Incorrect recursive smarts", curChar, "");
			return;
		}
			
		if (smarts.charAt(curChar) != '(')
		{
			newError("Incorrect recursive smarts", curChar+1, "");
			return;
		}
		
		curChar++;
		int openBrackets = 1;
		int firstChar = curChar;
		while ((curChar < nChars) && (openBrackets > 0))
		{	
			if (smarts.charAt(curChar) == '(')
				openBrackets++;
			else
				if (smarts.charAt(curChar) == ')')
					openBrackets--;
			curChar++;
		}
		
		if ((curChar >= nChars) && (openBrackets > 0)) 
		{	
			newError("Incorrect recursive smarts. String end is reached within $(expression)", curChar, "");
			return;
		}	
					
		if (firstChar == curChar-1)
			newError("Empty recursive smarts", curChar, "");
		curAtExpr.tokens.add(new SmartsExpressionToken(SmartsConst.AP_Recursive,curAtExpr.recSmartsStrings.size()));
		curAtExpr.recSmartsStrings.add(smarts.substring(firstChar,curChar-1));
	}
	
	int getAbsoluteChirality(IAtom atom, int relChirality)
	{
		//Notes:
		//1. In some cases absolute chirality is imposible to be 
		//determined from the SMARTS since some atom types and bond types are not defined
		//2. This procedure uses the order of atom registration in container 
		//to determine the relative chirality
		
		
		java.util.List ca = container.getConnectedAtomsList(atom);
		if (ca.size() != 4) 
			return(SmartsConst.ChC_Unspec);
		
		int atNCode[][] = new int[4][];
		for (int i=0; i<4; i++)
		{	
			atNCode[i] = getAtomNeighbourCode(atom, (IAtom)ca.get(i));
			if (atNCode[i] == null)
				return(SmartsConst.ChC_Unspec);
		}
		
		boolean FlagClockWise;
		if (relChirality == SmartsConst.ChC_Clock)
			FlagClockWise = true;
		else
			FlagClockWise = false;		
		//Neighbour atoms are re-ordered in incresing order of
		//their "Neighbour codes" (i.e. their their priorities)
		//To obtain order (1,2,3,4), several permutations are done in
		//in "bubble-algorithm" manner. At each permuation FlagClockWise
		//is changed. 
		for (int i = 2; i >=0; i--)
			for (int j = 0; j <= i; j++)
			{
				if (compareNeighbourCodes(atNCode[j],atNCode[j+1]) > 0)
				{
					FlagClockWise = !FlagClockWise;
					int temp[];
					temp = atNCode[j];
					atNCode[j] = atNCode[j+1];
					atNCode[j+1] = temp;
				}
			}
		
		//Ordering (1-->2,3,4 - Clock) corresponds to (4,3,2-->1 - Clock) i.e. R		
		//CIP rule:
		//First, you rotate the molecule so that the 
		//group of lowest priority is pointing directly away from you. For the other 
		//three groups, you determine the direction of high to low priority. If the direction 
		//is clockwise, the configuration is R (for rectus = right). If the direction is anti- 
		//anticlockwise, the configuration is S (for sinister = left).
		if (FlagClockWise == true)
			return(SmartsConst.ChC_R);
		else
			return(SmartsConst.ChC_S);
	}
	
	int compareNeighbourCodes(int atCode1[], int atCode2[])
	{
		int n;
		if (atCode1.length < atCode2.length)
			n = atCode1.length;
		else
			n = atCode2.length;
		
		for (int i = 0; i < n; i++)
		{
			if (atCode1[i] < atCode2[i])
				return(-1);
			else
				if (atCode1[i] > atCode2[i])
					return(1);
		}
		
		
		if (atCode1.length < atCode2.length)
			return(-1);
		else	
			if (atCode1.length > atCode2.length)
				return(1);
		
		return(0);
	}
	
	int getAtomType(IAtom atom)
	{
		if (atom instanceof SmartsAtomExpression)
		{
			SmartsAtomExpression at = (SmartsAtomExpression)atom;
			//The first found primitive defining atom type is used
			for (int i = 0; i < at.tokens.size(); i++)
			{
				SmartsExpressionToken tok = at.tokens.get(i);
				if (tok.type == SmartsConst.AP_ANY)
					return(-1);
				
				if ((tok.type == SmartsConst.AP_A) ||
					(tok.type == SmartsConst.AP_a) ||
					(tok.type == SmartsConst.AP_AtNum))
				{
					if (i>0)
						if (at.tokens.get(i-1).type == (SmartsConst.LO+SmartsConst.LO_NOT))							
							//atom type token is negated therefore atom type is unspecified
							return(-1); 
					
					if (tok.param > 0)
						return (tok.param);
					else
						return(-1);
				}
			}
		}
		
		if ((atom instanceof AliphaticSymbolQueryAtom)|| 
			(atom instanceof AromaticSymbolQueryAtom))
		{	
			return(SmartsConst.getElementNumber(atom.getSymbol()));
		}
		
		//atom type is undefined for the other possible instances ot the atom:
		//AntAtom, AromaticAtom, AliphaticAtom.
		//Also this code will be reached when there is no atom type definition
		//within the SmartsAtomExpression
		return(-1);
	}
	
	static int getBondType (IBond.Order order)
	{
		if (order == IBond.Order.SINGLE)
			return(1);
		if (order == IBond.Order.DOUBLE)
			return(2);
		if (order == IBond.Order.TRIPLE)
			return(3);
		return(1);
	}
	
	int getBondType(IBond bond)
	{	
		if (bond instanceof OrderQueryBond)
			return(getBondType(bond.getOrder()));
		if (bond instanceof SingleOrAromaticBond)
			return(1);
		if (bond instanceof DoubleNonAromaticBond)
			return(2);
		if (bond instanceof DoubleBondAromaticityNotSpecified)
			return(2);
		if (bond instanceof DoubleStereoBond)
			return(2);
		return(-1);
	}
	
	
	public int[] getAtomNeighbourCode(IAtom center, IAtom neighAtom)
	{
		Vector<Integer> code = new Vector<Integer>();
		Vector<IAtom> usedAtoms = new Vector<IAtom> ();
		Vector<IAtom> layer = new Vector<IAtom> ();
		Vector<IAtom> nextLayer;
				
		int atType = getAtomType(neighAtom);		
		if (atType == -1)
			return null;
		code.add(new Integer(atType));
		usedAtoms.add(center);
		usedAtoms.add(neighAtom);
		layer.add(neighAtom);
		//Two layers of neighAtom are used to form the code 
		nextLayer = addLayerToCode(code,layer,usedAtoms);
		layer = nextLayer;
		nextLayer = addLayerToCode(code,layer,usedAtoms);
		int result[] = new int [code.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = code.get(i).intValue();
		return result;
	}
	
	Vector<IAtom> addLayerToCode(Vector<Integer> code, Vector<IAtom> layer, Vector<IAtom> usedAtoms)
	{
		if (layer == null)
			return null;
		
		Vector<IAtom> nextLayer = new Vector<IAtom>(); 
		Vector<Integer> atTypes = new Vector<Integer>();
		Vector<Integer> boTypes = new Vector<Integer>(); 
		
		for(int i = 0; i < layer.size(); i++)
		{
			java.util.List ca = container.getConnectedAtomsList(layer.get(i));			
			for(int k = 0; k < ca.size(); k++)
			{
				IAtom at = (IAtom)ca.get(k);				
				if (!isAtomUsed(at,usedAtoms))
				{
					int atType = getAtomType(at);					
					if (atType == -1)
						return(null);
					IBond bo = container.getBond(layer.get(i),at);
					int boType = getBondType(bo);					
					if (boType == -1)
						return(null);
					usedAtoms.add(at);
					nextLayer.add(at);
					atTypes.add(new Integer(atType));
					boTypes.add(new Integer(boType));					
				}
			}
		}
		
		int n = atTypes.size();
		Integer tmp;
		//Sorting the atoms according to their atomic numbers		
		for (int i = n-2; i >= 0; i--)
			for(int k = 0; k <=i; k++)
			{
				if (atTypes.get(k).intValue() < atTypes.get(k+1).intValue())
				{
					tmp = atTypes.get(k).intValue();
					atTypes.set(k,atTypes.get(k+1).intValue());
					atTypes.set(k+1,tmp);
					tmp = boTypes.get(k).intValue();
					boTypes.set(k,boTypes.get(k+1).intValue());
					boTypes.set(k+1,tmp);
				}
			}
		//Adding the atoms and their bonds from the nextLayer to the code
		for(int i = 0; i < n; i++)
			code.add(atTypes.get(i));
		for(int i = 0; i < n; i++)
			code.add(boTypes.get(i));
		
		return nextLayer;
	}
	
	boolean isAtomUsed(IAtom atom, Vector<IAtom> v)	
	{
		for(int i = 0; i < v.size(); i++)
			if (atom == v.get(i))
				return(true);
		return(false);
	}
	
	void handleChirality(SmartsAtomExpression atom)
	{
		for (int i = 0; i < atom.tokens.size(); i++)
		{			
			SmartsExpressionToken tok = atom.tokens.get(i);
			if (tok.type == SmartsConst.AP_Chiral)
			{	
				tok.param = getAbsoluteChirality(atom,tok.param);
				//System.out.println("chirality = " + tok.param);
			}	
		}
	}
	
	void setDoubleBondsStereoInfo()
	{
		processedDirBonds.clear();
		processedDoubleBonds.clear();
		newStereoDoubleBonds.clear();		
		for(int i = 0; i < directionalBonds.size(); i++)
		{	
			SMARTSBond dirBond = directionalBonds.get(i);			
			//System.out.println("Dir: "+ SmartsHelper.bondAtomNumbersToString(container, dirBond));
			if (isBondProcessed(dirBond,processedDirBonds))
				continue;
			
			IAtom at0 = dirBond.getAtom(0);
			IAtom at1 = dirBond.getAtom(1);
			if (isAtomForStereoDoubleBond(at0))
			{
				SMARTSBond dBo = getNeighborDoubleBond(dirBond, 0);				
				if (dBo != null) 
					if (!isBondProcessed(dBo,processedDoubleBonds))
						setDoubleStereoBond(dBo, at0, at1, dirBond, directions.get(i).intValue(),0);
			}
			if (isAtomForStereoDoubleBond(at1))
			{
				SMARTSBond dBo = getNeighborDoubleBond(dirBond, 1);
				if (dBo != null)
					if (!isBondProcessed(dBo,processedDoubleBonds))
						setDoubleStereoBond(dBo, at1, at0, dirBond, directions.get(i).intValue(),1);
			}
			processedDirBonds.add(dirBond);
		}
		
		//Replace all double bonds with new Stereo Double Bonds
		for (int i = 0; i < processedDoubleBonds.size(); i++)
		{
			container.removeBond(processedDoubleBonds.get(i));
			container.addBond(newStereoDoubleBonds.get(i));
		}
		
	}
	
	boolean isDirectionalBond(SMARTSBond bond)
	{
		for(int i = 0; i < directionalBonds.size(); i++)
			if (directionalBonds.get(i) == bond)
				return(true);
		return false;
	}
	
	boolean isAtomForStereoDoubleBond(IAtom atom)	
	{
		//Following elements could participate in a stereo double bond: C, N, P, Si
		if (atom.getSymbol().equals("C"))
			return true;
		if (atom.getSymbol().equals("N"))			
			return true;
		if (atom.getSymbol().equals("P"))			
			return true;
		if (atom.getSymbol().equals("Si"))			
			return true;		
		return false;		
	}
	
	boolean isBondProcessed(SMARTSBond bond, Vector<SMARTSBond> processedBonds)
	{
		for(IQueryBond bo: processedBonds)
		{
			if (bo == bond)
				return(true);
		}
		return(false);
	}
	
	SMARTSBond getNeighborDoubleBond(SMARTSBond bond, int atNum)
	{	
		//bond is a directional single bond
		IAtom at = bond.getAtom(atNum);	
		java.util.List ca = container.getConnectedAtomsList(at);
		for(int k = 0; k < ca.size(); k++)
		{
			IBond bo = container.getBond(at,(IAtom)ca.get(k));
			if (bo != bond)
				if (bo instanceof OrderQueryBond)
				{	
					if (bo.getOrder() == IBond.Order.DOUBLE)
						return (SMARTSBond)bo;
				}	
		}
		return(null);
	}
		
	
	void setDoubleStereoBond(SMARTSBond doubleBond, IAtom atom, IAtom at0, 
			SMARTSBond dirBond,  int direction, int atomPos)
	{	
		//Input defines a fragment of the type  "a0--atom=="
		//Direction of bond "a0--atom" is defined by parameters: direction and atPos
		//atomPos is the number ot 'atom' inside the CDK representation of bond "a0--atom"
		//One of the bonds "atom1--at2" or "atom1--at3" must be a directional bond otherwise
		//the stereo configuration is not determined.
		//
		//(1)The remaining part of this fragment is determined first
		//    at0                      at2
		//       \    doubleBond      /
		//         atom   ==   atom1
		//       /                   \
		//    at1                     at3				
		//(2) The Priorities of atoms at0, at1, at2, at3 are determined
		//(3) The absolute stereo configuration is determined from the priorities and
		// the directions of bonds: "at0--atom" and "atom1--at2/at3" 
		//(4)A new object of the type DoubleStereoBond is registered
		//
		//
		
		/*System.out.println("-- start -- atom = " + container.getAtomNumber(atom) + ",  at0 = " + container.getAtomNumber(at0));
		*/
		
		//(1) Determining atoms: at1, at2, at3, atom1
		//If afterwards at1,at2 or at3 is left to be null pointer, then it is assumed to be an implicit H atom
		IAtom atom1;
		IAtom at1 = null;
		IAtom at2 = null;
		IAtom at3 = null;
		if (doubleBond.getAtom(0) == atom)
			atom1 = doubleBond.getAtom(1);
		else
			atom1 = doubleBond.getAtom(0);
				
		java.util.List ca = container.getConnectedAtomsList(atom);
		for(int k = 0; k < ca.size(); k++)
		{	
			if ((ca.get(k) != at0) && (ca.get(k) != atom1))
			{
				at1 = (IAtom)ca.get(k);
				break;
			}	
		}
		
		/*System.out.println("   atom1 = " + container.getAtomNumber(atom1));*/
		
		boolean FlagDir2 = false; //These flags indicate which atom (at2 or at3) 
		boolean FlagDir3 = false; //is part of a direction single bond
		ca = container.getConnectedAtomsList(atom1);
		for(int k = 0; k < ca.size(); k++)
		{	
			IAtom otherAt = (IAtom)ca.get(k);
			if (otherAt == atom)
				continue;
			IBond bo = container.getBond(atom1,otherAt);
			if (at2 == null)
			{
				FlagDir2 = isDirectionalBond((SMARTSBond)bo);
				at2 = otherAt;
			}
			else				
			{	
				if (at3 == null)
				{
					FlagDir3 = isDirectionalBond((SMARTSBond)bo);
					at3 = otherAt;
				}
				else
					break; //Already found two atoms connected to atom1
			}
		}
		
		/*System.out.println("   Flags 2 and 3 = " + FlagDir2+" "+FlagDir3);*/
		
		if (!(FlagDir2 || FlagDir3))
			return; //No direction bond found on the other side of the double bond
		
		//(2) Setting the atom priorities
		int pAt0[] = getAtomNeighbourCode(atom, at0);
		int pAt1[];
		int pAt2[];
		int pAt3[];
		if (at1 == null)
		{
			pAt1 = new int[1];
			pAt1[0] = 1;
		}
		else
			pAt1 = getAtomNeighbourCode(atom, at1);
		
		if (at2 == null)
		{
			pAt2 = new int[1];
			pAt2[0] = 1;			
		}
		else
			pAt2 = getAtomNeighbourCode(atom1, at2);
		
		if (at3 == null)
		{
			pAt3 = new int[1];
			pAt3[0] = 1;
		}
		else
			pAt3 = getAtomNeighbourCode(atom1, at3);
		
		/*
		System.out.println("Processign double bond");		
		System.out.print("*At "+container.getAtomNumber(at0)+" --> ");
		SmartsHelper.printIntArray(pAt0);
		System.out.print(" At "+container.getAtomNumber(at1)+" --> ");
		SmartsHelper.printIntArray(pAt1);
		System.out.print(FlagDir2?"*":" ");  
		System.out.print("At "+container.getAtomNumber(at2)+" --> ");
		SmartsHelper.printIntArray(pAt2);
		System.out.print(FlagDir3?"*":" ");
		System.out.print("At "+container.getAtomNumber(at3)+" --> ");
		SmartsHelper.printIntArray(pAt3);
		*/
		
		//(3) Determinig the absolute stereo configuration (CIS/TRANS)
		boolean isUnspecified;
		boolean isCis;
		int direction2 = 0;
		SMARTSBond dirBond2;
		
		if (FlagDir2)  //Getting the second directional bond and its direction 
			dirBond2 = (SMARTSBond)container.getBond(atom1,at2);
		else
			dirBond2 = (SMARTSBond)container.getBond(atom1,at3);
		for(int i = 0; i < directions.size(); i++)
		{	
			if (directionalBonds.get(i) == dirBond2)
			{
				direction2 = directions.get(i).intValue();
				break;
			}
		}
		/*System.out.println("   Second dir bond "+ SmartsHelper.bondAtomNumbersToString(container, dirBond2));
		*/
		//"UP/DOWN" is interpreted as a direction from the double bond atom toward the outside atom
		//For example atom-->at0, atom1-->at2
		boolean isUp, isUp2; 		
		isUp  = (direction  == SmartsConst.BT_UP) ||(direction  == SmartsConst.BT_UPUNSPEC);
		isUp2 = (direction2 == SmartsConst.BT_UP) ||(direction2 == SmartsConst.BT_UPUNSPEC);
		isCis = (isUp == isUp2);
		
		//Additionl checks for the directions are done
		//During the parsing directions are determined from the first atom appearance toward second one
		//which does not mean the direction is "from the double bond toward the periphery"
		//The container numbering is used for this purpose
		if (container.getAtomNumber(atom) > container.getAtomNumber(at0))
			isCis = !isCis; //The direction was registered as "periphery-->doublebond"		
		if (FlagDir2)
		{
			if (container.getAtomNumber(atom1) > container.getAtomNumber(at2))
				isCis = !isCis; //The direction was registered as "periphery-->doubleBond"
		}
		else
		{
			if (container.getAtomNumber(atom1) > container.getAtomNumber(at3))
				isCis = !isCis; //The direction was registered as "periphery-->doubleBond"
		}
		
		//Additional check for the atom priorities is done
		//The priorities of atoms at0, at1, at2 and at3 influence the
		//final result for the absolute stereo configuration 
		if (compareNeighbourCodes(pAt0,pAt1) < 0)
			isCis = !isCis; //In absolut manner at1 has priority but at0 was used so far. Hence "changing isCis" 
		if (FlagDir2)
		{
			//In absolute manner at3 has priority but at2 was used so far. Hence "changing isCis"
			if (compareNeighbourCodes(pAt2,pAt3) < 0)
				isCis = !isCis; 
		}
		else
		{
			//In absolute manner at2 has priority but at3 was used so far. Hence "changing isCis"
			if (compareNeighbourCodes(pAt3,pAt2) < 0)
				isCis = !isCis;
		}
		
		isUnspecified  = (direction  == SmartsConst.BT_UPUNSPEC) || (direction  == SmartsConst.BT_DOWNUNSPEC) ||
						(direction2  == SmartsConst.BT_UPUNSPEC) || (direction2  == SmartsConst.BT_DOWNUNSPEC);
		
		
		//(4) Registering a new Double Stereo bond  
		DoubleStereoBond stereoBond = new DoubleStereoBond();
		stereoBond.setAtom(atom,0);
		stereoBond.setAtom(atom1,1);
		if (isCis)
		{
			if (isUnspecified)
				stereoBond.stereoParameter = SmartsConst.BT_CISUNSPEC;
			else
				stereoBond.stereoParameter = SmartsConst.BT_CIS;
		}
		else
		{
			if (isUnspecified)
				stereoBond.stereoParameter = SmartsConst.BT_TRANSUNSPEC;
			else
				stereoBond.stereoParameter = SmartsConst.BT_TRANS;
		}
		processedDoubleBonds.add(doubleBond);
		newStereoDoubleBonds.add(stereoBond);
		
		//Registering the second directional bonds as a processed one
		processedDirBonds.add(dirBond2);
	}
	
	
	public void setSMARTSData(IAtomContainer container) throws Exception
	{	
		prepareTargetForSMARTSSearch(mNeedNeighbourData, mNeedValencyData, 
							mNeedRingData, mNeedRingData2, mNeedExplicitHData, mNeedParentMoleculeData, 
							container);
	}
	
	static public void prepareTargetForSMARTSSearch(boolean neighbourData, boolean valenceData, 
				boolean ringData, boolean ringData2, boolean explicitHData , boolean parentMoleculeData, 
				IAtomContainer container) throws Exception
	{
		if (neighbourData)
			setNeighbourData(container);
		
		if (valenceData)
			setValenceData(container);
		
		if (ringData || ringData2)
			setRingData(container, ringData, ringData2);
				
		if (explicitHData)
			setExplicitHAtomData(container);
		
		if (parentMoleculeData)
			setParentMoleculeData(container);
	}
	
	static public void prepareTargetForSMARTSSearch(SmartsFlags flags, IAtomContainer container)
{
	if (flags.mNeedNeighbourData)
		setNeighbourData(container);
	
	if (flags.mNeedValenceData)
		setValenceData(container);
	
	if (flags.mNeedRingData || flags.mNeedRingData2)
		setRingData(container, flags.mNeedRingData, flags.mNeedRingData2);
			
	if (flags.mNeedExplicitHData)
		setExplicitHAtomData(container);
	
	if (flags.mNeedParentMoleculeData)
		setParentMoleculeData(container);
}
	
	
	static public void setNeighbourData(IAtomContainer container)
	{	
		IBond bond = null;		
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			IAtom at = container.getAtom(i);
			at.setFormalNeighbourCount(0);			
		}
		
		for (int f = 0; f < container.getBondCount(); f++)
		{
			bond = container.getBond(f);
			IAtom at1 = bond.getAtom(0);
			IAtom at2 = bond.getAtom(1);
			at1.setFormalNeighbourCount(at1.getFormalNeighbourCount()+1);			
			at2.setFormalNeighbourCount(at2.getFormalNeighbourCount()+1);			
		}
	}
	
	static public void setValenceData(IAtomContainer container)
	{	
		IBond bond = null;		
		Integer hci;
		int hc;
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			IAtom at = container.getAtom(i);
        	/*
        	https://sourceforge.net/tracker/?func=detail&aid=3020065&group_id=20024&atid=120024
			hci = at.getHydrogenCount();
			*/				
			hci = at.getImplicitHydrogenCount();
    		hc = 0;
    		if (hci != null)
    			hc = hci.intValue();
			//at.setValency(at.getHydrogenCount());   !!! - in some cases getHydrogenCount() returns null 
    		at.setValency(hc);
		}
		
		for (int f = 0; f < container.getBondCount(); f++)
		{
			bond = container.getBond(f);
			IAtom at1 = bond.getAtom(0);
			IAtom at2 = bond.getAtom(1);			
			at1.setValency(at1.getValency() + getBondType(bond.getOrder()));			
			at2.setValency(at2.getValency() + getBondType(bond.getOrder()));
		}
	} 
	
	static public void setExplicitHAtomData(IAtomContainer container)
	{
		for (int i = 0; i < container.getAtomCount(); i++)
			container.getAtom(i).removeProperty(CMLUtilities.ExplicitH);
	
		for (int i = 0; i < container.getBondCount(); i++)
		{
			IBond bo = container.getBond(i);
			if (bo.getAtom(0).getSymbol().equals("H"))
			{
				IAtom at = bo.getAtom(1);
				Integer ha = (Integer)at.getProperty(CMLUtilities.ExplicitH);
				if (ha == null)
					at.setProperty(CMLUtilities.ExplicitH, new Integer(1));
				else
					at.setProperty(CMLUtilities.ExplicitH, new Integer(1+ha.intValue()));
			}
			if (bo.getAtom(1).getSymbol().equals("H"))
			{
				IAtom at = bo.getAtom(0);
				Integer ha = (Integer)at.getProperty(CMLUtilities.ExplicitH);
				if (ha == null)
					at.setProperty(CMLUtilities.ExplicitH, new Integer(1));
				else
					at.setProperty(CMLUtilities.ExplicitH, new Integer(1+ha.intValue()));
			}
		}
	}
	
	static public int[] getExplicitHAtomData(IAtomContainer container)
	{
		int numH[] = new int[container.getAtomCount()];
		for (int i = 0; i < numH.length; i++)
			numH[0] = 0; 
			
		for (int i = 0; i < container.getBondCount(); i++)
		{
			IBond bo = container.getBond(i);
			if (bo.getAtom(0).getSymbol().equals("H"))
			{
				IAtom at = bo.getAtom(1);
				numH[container.getAtomNumber(at)]++;
			}
			if (bo.getAtom(1).getSymbol().equals("H"))
			{
				IAtom at = bo.getAtom(0);
				numH[container.getAtomNumber(at)]++;
			}
		}
		return(numH);
	}
	
	
	static public void setRingData(IAtomContainer container, boolean rData, boolean rData2)
	{	
		SSSRFinder sssrf = new SSSRFinder(container);
		IRingSet ringSet = sssrf.findSSSR();
		IRingSet atomRings;
		
		if (rData)
		{
			for (int i = 0; i < container.getAtomCount(); i++)
			{	
				IAtom atom = container.getAtom(i);
				atomRings = ringSet.getRings(atom);			
				int n = atomRings.getAtomContainerCount();
				if (n > 0)
				{	
					int ringData[] = new int [n];
					for (int k = 0; k < n; k++ )
					{	
						ringData[k] = atomRings.getAtomContainer(k).getAtomCount();
					}	
					atom.setProperty(CMLUtilities.RingData, ringData);				
				}	
			}
		} //end of ringData
		
		if (rData2)
		{
			for (int i = 0; i < container.getAtomCount(); i++)
			{	
				IAtom atom = container.getAtom(i);
				atomRings = ringSet.getRings(atom);			
				int n = atomRings.getAtomContainerCount();
				if (n > 0)
				{	
					int ringData2[] = new int [n];
					for (int k = 0; k < n; k++ )
					{	
						ringData2[k] = getRingNumberInRingSet(atomRings.getAtomContainer(k), ringSet);
					}	
					atom.setProperty(CMLUtilities.RingData2, ringData2);				
				}	
			}
		} //end of ringData2
		
	}
	
	static public Vector<int[]> getRindData(IAtomContainer container, IRingSet ringSet)
	{
		Vector<int[]> v = new Vector<int[]>();
		IRingSet atomRings;
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			IAtom atom = container.getAtom(i);
			atomRings = ringSet.getRings(atom);			
			int n = atomRings.getAtomContainerCount();
			if (n > 0)
			{	
				int ringData[] = new int [n];
				for (int k = 0; k < n; k++ )					
					ringData[k] = atomRings.getAtomContainer(k).getAtomCount();					
				v.add(ringData);				
			}
			else
				v.add(null);
		}
		return(v);
	}
	
	static public Vector<int[]> getRindData2(IAtomContainer container, IRingSet ringSet)
	{
		Vector<int[]> v = new Vector<int[]>();
		IRingSet atomRings;
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			IAtom atom = container.getAtom(i);
			atomRings = ringSet.getRings(atom);			
			int n = atomRings.getAtomContainerCount();
			if (n > 0)
			{	
				int ringData2[] = new int [n];
				for (int k = 0; k < n; k++ )					
					ringData2[k] = getRingNumberInRingSet(atomRings.getAtomContainer(k), ringSet);
				v.add(ringData2);
			}
			else
				v.add(null);
		}
		return(v);
	}
	
	static public int getRingNumberInRingSet(IAtomContainer ring, IRingSet rs)
	{
		for (int i = 0; i < rs.getAtomContainerCount(); i++)
		{	
			if (ring == rs.getAtomContainer(i))
				return(i);
		}	
		return(-1);
	}
	
	static public void setParentMoleculeData(IAtomContainer container)
	{	
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			IAtom atom = container.getAtom(i);			
			atom.setProperty("ParentMoleculeData", container);
		}
	}
	
}
