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

/**
 * 
 * @author Nikolay Kochev nick@uni-plovdiv.bg
 */
public class SmartsConst 
{
	//Global values for setting the absolute CIS/TRANS configuration
	public static final int ABSOLUTE_CIS = 10;
	public static final int ABSOLUTE_TRANS = 11;
	
	//Logical operations
	public static char LogOperationChars[] = {'!', '&', ',', ';'};
	public static final int LO_NOT = 0;
	public static final int LO_AND = 1;
	public static final int LO_OR = 2;
	public static final int LO_ANDLO = 3;
	public static final int LO = 1000;
		
	//Atom Primitives
	public static char AtomPrimChars[] = {'*','a','A','D','H','h','R','r','v','X',
										  '-','#','@','i','_','x'};
	public static final int AP_ANY = 0;
	public static final int AP_a =  1;
	public static final int AP_A = 2;
	public static final int AP_D = 3;
	public static final int AP_H = 4;
	public static final int AP_h = 5;
	public static final int AP_R = 6;
	public static final int AP_r = 7;
	public static final int AP_v = 8;
	public static final int AP_X = 9;
	public static final int AP_Charge = 10;
	public static final int AP_AtNum = 11;
	public static final int AP_Chiral = 12;
	public static final int AP_Mass = 13;
	public static final int AP_Recursive = 14;
	public static final int AP_x = 15;
	
	//Bond types
	public static char BondChars[] = {'~','-','=','#',':','@','/','\\'};
	public static final int BT_ANY = 0;
	public static final int BT_SINGLE = 1;
	public static final int BT_DOUBLE = 2;
	public static final int BT_TRIPLE = 3;
	public static final int BT_AROMATIC = 4;
	public static final int BT_RING = 5;
	public static final int BT_UP = 6;
	public static final int BT_DOWN = 7;
	public static final int BT_UPUNSPEC = 8;
	public static final int BT_DOWNUNSPEC = 9;
	public static final int BT_CIS = 10;
	public static final int BT_CISUNSPEC = 11;
	public static final int BT_TRANS = 12;
	public static final int BT_TRANSUNSPEC = 13;	
	public static final int BT_UNDEFINED = 100;
	
	
	//Chirality classes
	public static final int ChC_Unspec = 0;
	public static final int ChC_AntiClock = 1;
	public static final int ChC_Clock = 2;	
	public static final int ChC_R = 1001;
	public static final int ChC_S = 1002;
	
	
	//Matrix with the operation priorities {pij}
	//p[i][j] < 0 means that priority(i) < priority(j)
	//p[i][j] = 0 means that priority(i) = priority(j)
	//p[i][j] > 0 means that priority(i) > priority(j)
	public static int priority[][] = {
		//         !  &  ,  ;
		/* ! */  {-1, 1, 1, 1},
		/* & */  {-1, 0, 1, 1},
		/* , */  {-1,-1, 0, 1},
		/* , */  {-1,-1,-1, 0},
	};
	                
	
	public static String elSymbols[] =
	{
		"",                                                  
		"H","He","Li","Be","B",
		"C","N","O","F","Ne",         
		"Na","Mg","Al","Si","P",
		"S","Cl","Ar","K","Ca",      
		"Sc","Ti","V","Cr","Mn",
		"Fe","Co","Ni","Cu","Zn",    
		"Ga","Ge","As","Se","Br",
		"Kr","Rb","Sr","Y","Zr",    
		"Nb","Mo","Tc","Ru","Rh",
		"Pd","Ag","Cd","In","Sn",   
		"Sb","Te","I","Xe","Cs",
		"Ba","La","Ce","Pr","Nd",    
		"Pm","Sm","Eu","Gd","Tb",
		"Dy","Ho","Er","Tm","Yb",   
		"Lu","Hf","Ta","W","Re",
		"Os","Ir","Pt","Au","Hg",    
		"Tl","Pb","Bi","Po","At",
		"Rn","Fr","Ra","Ac","Th",   
		"Pa","U","Np","Pu","Am",
		"Cm","Bk","Cf","Es","Fm",    
		"Md","No","Lr","Rf","Db",
		"Sg","Bh","Hs","Mt","Ds", 
		"Rg"  		//up to element #111
	};
	
	
	public static int getLogOperationCharNumber (char ch)
	{
		for (int i=0; i < LogOperationChars.length; i++)
		{
			if (ch == LogOperationChars[i])
				return (i);
		}
		return(-1);
	}
	
	public static int getBondCharNumber (char ch)
	{
		for (int i=0; i < BondChars.length; i++)
		{
			if (ch == BondChars[i])
				return (i);
		}
		return(-1);
	}	
	
	public static int getElementNumber(String symbol)
	{
		for (int i=1; i < elSymbols.length; i++)
		{
			if (symbol.compareTo(elSymbols[i])==0)
				return (i);
		}
		return(-1);
	}
	
	public static int getElementNumberFromChar(char symbol)
	{
		switch (symbol)
		{
		case 'C':
			return 6;
		case 'N':
			return 7;
		case 'O':
			return 8;
		case 'H':
			return 1;	
		case 'B':
			return 5;
		case 'F':
			return 9;
		case 'I':
			return 53;
		case 'P':
			return 15;
		case 'S':
			return 16;
		case 'K':
			return 19;
		case 'V':
			return 23;
		case 'Y':
			return 39;
		case 'W':
			return 74;	
		case 'U':
			return 92;	
			
		default:
			return(-1);
		}
	}
}
