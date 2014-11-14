package ambit2.descriptors.utils;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;



public class GraphMatrices 
{

	public enum DMAtomState
	{
		NOT_REACHED,
		CURRENT_SHPERE,
		PREVIOUS_SHPERE,
		NEXT_SHPERE,
	}
	
	public static int[][] getTopDistanceMatrix (IAtomContainer container)
	{
		int n = container.getAtomCount();
		DMAtomState ats[] = new DMAtomState [n];
		int D[][] = new int[n][n];
		fillMatrix(D,-1);
		fillDiagonal(D,0);
		
		//atoms state
		//0 - not reached, 
		//1 - active atom in the current sphere,
		//2 - in previous spheres or inactive atom from the current sphere,
		//3 - The atom from the next sphere. 3 will become 1 at the end of the cycle


		for (int currAt = 0; currAt < (n-1); currAt++)
		{	
			if (getFrequency(-1, D[currAt], n) > 0)  //If there are -1 values in the this line
			{	
				fillVector(DMAtomState.NOT_REACHED, ats, n);
				ats[currAt] = DMAtomState.CURRENT_SHPERE;

				int dist = 0;
				//System.out.println(" getFrequency(DMAtomState.CURRENT_SHPERE, ats, n) =  " + getFrequency(DMAtomState.CURRENT_SHPERE, ats, n));				
				while (getFrequency(DMAtomState.CURRENT_SHPERE, ats, n)  > 0 )   //Traversing the fragment which starts with atom i, where ats[i] == 1
				{	
					dist++;
					
					for(int i = 0; i < n; i++)
					{	
						if (ats[i] == DMAtomState.CURRENT_SHPERE)    
						{
							IAtom at = container.getAtom(i);
							List<IAtom> neighbors = container.getConnectedAtomsList(at);

							for (int j = 0; j < neighbors.size(); j++)
							{
								IAtom at1 = neighbors.get(j);
								int num = container.getAtomNumber(at1);
								
								if (ats [num] == DMAtomState.NOT_REACHED)
								{									
									
									ats[num] = DMAtomState.NEXT_SHPERE;
									if (currAt < num)  //otherwise disntance D[num][currAt] is already found
									{	
										D [currAt][num] = dist;
										D [num][currAt] = dist;
									}
								}
							}
						} // for i 
					}	

					//State change CURRENT_SHPERE --> PREVIOUS_SHPERE
					for(int i=0; i < n; i++)
					{	
						if (ats[i]== DMAtomState.CURRENT_SHPERE)
							ats[i] = DMAtomState.PREVIOUS_SHPERE;
					}	
					
					//State change NEXT_SHPERE --> CURRENT_SHPERE  
					for(int i = 0; i < n; i++)
					{	
						if (ats[i]==DMAtomState.NEXT_SHPERE)
						{
							ats[i]=DMAtomState.CURRENT_SHPERE;
							//subFrags[i] = currFragNum;
						}
					}	
				}// end of while	
			}	
		}//for(currAt) 

		return (D);
	}
	
	public static int[] getVertexEccentricity(int D[][])
	{	
		int n = D.length;
		int Vecc[] = new int [n];
		for (int i = 0; i < n; i++ )
			Vecc[i] = max(D[i]);
			
		return Vecc;
	}
	
	public static double [][] getAtomWeightedDistanceMatrix (IAtomContainer container,  double atomWeights[])
	{ 
		int n = container.getAtomCount();
		double [][] WDM = new double [n][n];
		
		return WDM;
	}
	
	
	
	

	public static double [] getChemicalAtomEccentricity (double WDM[][])
	{
		int n = WDM.length;
		double CAecc[] = new double [n];
		for (int i = 0; i < n; i++ )
			CAecc[i] = max(WDM[i]);

		return CAecc;
	}
	
	
	public static int[][] getAdditiveAdjacencyMatrix(IAtomContainer container) 
	{
		int n = container.getAtomCount();
		int AAM[][] = new int [n][n];
		GraphMatrices.nullify(AAM);

		for (int k = 0; k < container.getBondCount(); k++)
		{			
			IBond b = container.getBond(k);
			IAtom at0 = b.getAtom(0);
			IAtom at1 = b.getAtom(1);
			int i = container.getAtomNumber(at0);
			int j = container.getAtomNumber(at1);
			//element[i][j] = delta[j] (the topological degree of the "second" atom in the bond)
			AAM [j][i] = container.getConnectedAtomsCount(at0);
			AAM [i][j] = container.getConnectedAtomsCount(at1);
		}

		return AAM;
	}
	
	
	public static double [][] getGeneralizedDistanceMatrix (IAtomContainer container, double alfa)
	{
		int n = container.getAtomCount();
		int [][] D = getTopDistanceMatrix (container);
		double [][] GD = new double [n][n];
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				GD[i][j] = D[i][j] * alfa;
			}
		}
		return (GD);
	}
	

	public static double [][] getReciprocalDistanceMatrix (IAtomContainer container)
	{
		int n = container.getAtomCount();
		int [][] D = getTopDistanceMatrix (container);
		double [][] RD = new double [n][n];
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (D[i][j] == 0)
					RD[i][j] = 0;
				else
					RD[i][j] = 1.0/ D[i][j];
			}
		}
		return (RD);
	}

	public static int [][] getComplementDistanceMatrix (IAtomContainer container)
	{
		int n = container.getAtomCount();
		int [][] D = getTopDistanceMatrix (container);
		int [][] CD = new int [n][n];
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (D[i][j] == 0)
					CD[i][j] = 0;
				else
					CD[i][j] = n - D[i][j];
			}
		}
		return (CD);
	}
	
	public static int[][] getAdjacencyMatrix(IAtomContainer container) 
	{
		int n = container.getAtomCount();
		int [][] AM = new int [n][n];
		GraphMatrices.nullify(AM);


		for (int k = 0; k < container.getBondCount(); k++)
		{
			IBond b = container.getBond(k);
			IAtom at0 = b.getAtom(0);
			IAtom at1 = b.getAtom(1);
			int i = container.getAtomNumber(at0);
			int j = container.getAtomNumber(at1);
			AM [j][i] = 1;
			AM [i][j] = 1;
		}

		return AM;
	}
	
	public static int [][] getDegreeMatrix (IAtomContainer container) 
	{
		int n = container.getAtomCount();
		int [][] deg = new int [n][n];
		int v[] = TopologicalUtilities.getAtomTopDegrees(container);
		nullify(deg);

		for (int i = 0; i < n; i++)
			deg [i][i] = v[i];

		return deg;
	}

	public static int [][] LaplasMatrix (IAtomContainer container) 
	{
		int n=container.getAtomCount();

		int [][] LaplasMatrix = new int [n][n];
		nullify(LaplasMatrix);


		int [][] AMatrix  =  getAdjacencyMatrix(container);
		int[][] DegMatrix = getDegreeMatrix(container);

		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
			{
				LaplasMatrix [i][j] = DegMatrix [i][j]- AMatrix [i][j]; 
			}
		}

		return LaplasMatrix;
	}
	
	public static int[][] SzegedMatrixU (IAtomContainer container)
	{		
		int n = container.getAtomCount();
		int [][] szU = new int [n][n];
		fillDiagonal(szU, 0);

				
		int [][] D  =  getTopDistanceMatrix(container);

		for (int i = 0; i < n; i++)
			for (int j = i+1; j < n; j++)
			{	
				int nij = 0;
				int nji = 0;
				for (int k = 0; k < n; k++)
				{
					if (D[k][i] < D[k][j])
						nij++;
					if (D[k][i] > D[k][j])
						nji++;
				}
				szU [i][j] = nij;
				szU [j][i] = nji;
			}
		return szU;
	}
	
	
	public static int[][] SzegedMatrixP (IAtomContainer container)
	{		
			
		int n = container.getAtomCount();
		int [][] SzP = new int [n][n];
		fillDiagonal(SzP, 0);
						
		int [][] SzU  =  SzegedMatrixU(container);
		
		for (int i = 0; i < n; i++)
			for (int j = i+1; j < n; j++)
			{					
				SzP [i][j] = SzU [i][j] * SzU [j][i];
				SzP [j][i] = SzP [i][j];
			}
		return SzP;
	}
	
	public static int[][] SzegedMatrixE (IAtomContainer container)
	{		
			
		int n = container.getAtomCount();
		int [][] SzE = new int [n][n];
		fillDiagonal(SzE, 0);

			
		int [][] SzP  =  SzegedMatrixP (container);

		for (int k = 0; k < container.getBondCount(); k++)
		{
			IBond b = container.getBond(k);
			IAtom at0 = b.getAtom(0);
			IAtom at1 = b.getAtom(1);
			int i = container.getAtomNumber(at0);
			int j = container.getAtomNumber(at1);
			SzE [i][j] = SzP [i][j];
			SzE [j][i] = SzE [i][j];
		}
		return SzE;
	}
	
	
	
	
	
	
	
	//------------- some basic helper functionality -------------------
	
	
	public static int min(int d[])
	{
		int mind = d[0];
		for (int i = 1; i < d.length; i++)
			if (mind > d[i])
				mind = d[i];
		return mind;
	}
		
	public static double min(double d[])
	{
		double mind = d[0];
		for (int i = 1; i < d.length; i++)
			if (mind > d[i])
				mind = d[i];
		return mind;
	}
	
	public static int max(int d[])
	{
		int maxd = d[0];
		for (int i = 1; i < d.length; i++)
			if (maxd < d[i])
				maxd = d[i];
		return maxd;
	}
	
	public static double max(double d[])
	{
		double maxd = d[0];
		for (int i = 1; i < d.length; i++)
			if (maxd < d[i])
				maxd = d[i];
		return maxd;
	}


	public static int getFrequency(int elValue, int v[], int size)
	{
		int freq = 0;
		for (int i=0; i<size; i++)
		{
			if (v[i] == elValue)
				freq++;
		}
		return freq;
	}
	
	public static int getFrequency(double elValue, double v[], int size)
	{
		int freq = 0;
		for (int i=0; i<size; i++)
		{
			if (v[i] == elValue)
				freq++;
		}
		return freq;
	}

	public static int fillVector(int elValue, int v[], int size)
	{	
		for (int i=0; i<size; i++)
			v[i] = elValue;
		return(0);
	}
	
	
	public static void fillMatrix(int a[][], int value)
	{	
		for (int i = 0; i < a.length; i++)
			for (int j = 0; j < a[i].length; j++)
				a[i][j] = value;
	}
	
	public static void fillMatrix(double a[][], double value)
	{	
		for (int i = 0; i < a.length; i++)
			for (int j = 0; j < a[i].length; j++)
				a[i][j] = value;
	}
	
	public static void nullify(int a[][])
	{
		fillMatrix(a,0);
	}
	
	public static void nullify(double a[][])
	{
		fillMatrix(a,0);
	}
	
	public static void fillDiagonal(int a[][], int value)
	{	
		for (int i = 0; i < a.length; i++)			
				a[i][i] = value;
	}
	
	public static void fillDiagonal(double a[][], double value)
	{	
		for (int i = 0; i < a.length; i++)			
				a[i][i] = value;
	}
	
	public static void fillVector(DMAtomState state, DMAtomState v[], int size)
	{	
		for (int i=0; i<size; i++)
			v[i] = state;
	}
	
	public static int getFrequency(DMAtomState state, DMAtomState v[], int size)
	{
		int freq = 0;
		for (int i=0; i<size; i++)
		{
			if (v[i] == state)
				freq++;
		}
		return freq;
	}
	
	public static String toString(int a[][])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++)
		{	
			for (int j = 0; j < a[i].length; j++)
				sb.append(" "+a[i][j]);
			sb.append("\n");
		}
		return(sb.toString());
	}
	

}
