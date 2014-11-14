package ambit2.descriptors.utils;

import java.io.IOException;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.descriptors.IsotopeFactory;


public class TopologicalUtilities 
{
	public static String MP_ATOM_TOP_DEGREES = "MP_ATOM_TOP_DEGREES";
	
	
	public static int[] calcAtomTopDegrees(IAtomContainer container) 
	{
		return calcAtomTopDegrees(container, false);
	}
	
	public static int[] calcAtomTopDegrees(IAtomContainer container, boolean ExlcudeExplicitH) 
	{
		if (container == null) 
			return null;

		if (container.getAtomCount() == 0)
			return new int[0];
		
		int n = container.getAtomCount();
		int m = container.getBondCount();
		
		int [] dv = new int [n];
		
		for (int i = 0; i < n; i++)
			dv[i] = 0;
		
		for (int k = 0; k < m; k++)
		{
			IBond b = container.getBond(k);
			IAtom at0 = b.getAtom(0);
			IAtom at1 = b.getAtom(1);
			
			if (ExlcudeExplicitH)
			{
				if (at0.getSymbol().equals("H"))
					continue;
				if (at1.getSymbol().equals("H"))
					continue;
			}
			
			int ind0 = container.getAtomNumber(at0);
			int ind1 = container.getAtomNumber(at1);
			
			dv [ind0]++;
			dv [ind1]++;
		}
		
		return dv;
	}
		
	public static int[] getAtomTopDegrees(IAtomContainer container) 
	{
		int[] degrees = (int[])container.getProperty(MP_ATOM_TOP_DEGREES);
		
		if (degrees == null)
			degrees = calcAtomTopDegrees(container); 
			
		return degrees;
	}
	
	public static double [] getMadanChemicalDegree (IAtomContainer container) throws IOException
	{
		int n = container.getAtomCount();
		double [] madanDegree = new double [n];		
				
		IsotopeFactory isoFact = IsotopeFactory.getInstance(container.getBuilder());
		
		for (int i = 0; i < n; i++)
		{	
			List<IAtom> list = container.getConnectedAtomsList(container.getAtom(i));			
			madanDegree[i] = 0;

			for (int m = 0; m < list.size(); m ++)
			{
				IAtom atom = list.get(m);
				madanDegree [i] += isoFact.getMajorIsotope(atom.getSymbol()).getExactMass() / 12.0107;
			}
		}	
		return madanDegree;
	}
	
	public static double [] getRelativeAtomicWeights (IAtomContainer container) throws IOException
	{
		int n = container.getAtomCount();
		double [] relAtWeight = new double [n];		
				
		IsotopeFactory isoFact = IsotopeFactory.getInstance(container.getBuilder());
		
		for (int i = 0; i < n; i++)
		{	
				IAtom atom = container.getAtom(i);
				relAtWeight [i] = isoFact.getMajorIsotope(atom.getSymbol()).getExactMass() / 12.0107;
		}
			
		return relAtWeight;
	}
		
	
	public static double[] DecimalAdjVector_A10 (IAtomContainer container) 
	{
		int [][]dA  =  GraphMatrices.getAdjacencyMatrix(container);

		int n = container.getAtomCount();
		double a10[] = new double[n];
		double aij;

		for (int i = 0; i < n; i++)
		{
			a10[i] = 0.0;
			double pow2 = 1.0;  //2^0
			for (int j = n-1; j >= 0; j--)
			{
				aij = dA [i][j];
				if(aij == 1)
					a10[i] += pow2;

				pow2 *= 2; //pow2 = 2^(n-j)
			}
		}

		return a10;
	}

	public static int DecimalAdjVector_A1 (IAtomContainer container) 
	{
		double [] a10 = DecimalAdjVector_A10(container);		

		int n = container.getAtomCount();
		int A1 = 0;
		int sum = 0;

		for (int i = 0; i < n; i++)
			sum += a10[i];
		A1 = sum;

		return A1;
	}

	public static int DecimalAdjVector_A2 (IAtomContainer container) 
	{
		double [] a10 = DecimalAdjVector_A10(container);
		int[] dv =  getAtomTopDegrees(container);

		int n = container.getAtomCount();
		int A2 = 0;
		int sum = 0;

		for (int i = 0; i < n; i++)
		{
			sum +=dv[i]* a10[i];
			A2 = sum;
		}

		return A2;
	}

	public static int DecimalAdjVector_A3 (IAtomContainer container) 
	{
					
		int d[] =  dVector (container);
		int n = container.getAtomCount();
		int A3 = 0;
		int sum = 0;
		for (int i = 0; i < n; i++)
		{
			sum+=d[i];
		}
		A3 = sum;

		return A3;
	}
	
	
	private static int[] dVector (IAtomContainer container)
	{		
		double a10 [] =  DecimalAdjVector_A10(container);
		int[][] dD  =  GraphMatrices.getTopDistanceMatrix(container);	

		int n = container.getAtomCount();
		int d[] = new int[n];
		for (int i = 0; i < d.length; i++)
			d[i] = 0;

		int DM=0;

		for (int i = 0; i < dD.length; i++)
		{
			for (int j = 0; j < dD.length; j++)
			{
				DM= dD[i][j];
				d[i] += DM*a10[i];
			}
		}

		return d;
	}
	
	public static double [] AtomClusteringCoefficient (IAtomContainer container) 
	{
		int[][] A = GraphMatrices.getAdjacencyMatrix(container);
		int[] dv = getAtomTopDegrees(container);

		int n = container.getAtomCount();
		double acc[] = new double[n];

		for (int i = 0; i < n; i++)
		{
			if (dv[i] <= 1)  //delta1 = 0 or deltai = 1
				acc[i] = 0;
			else
			{
				int bi = getAtomNeighborsInterconnectivity(i, A);
				acc[i] = 2.0*bi /(1.0*dv[i]*(dv[i]-1));
			}
		}

		return acc;
	}

	//check return result type 'int' ?
	static int getAtomNeighborsInterconnectivity(int i, int[][] A)
	{		
		int sum = 0;

		for ( i = 0; i < A.length; i++)
			for (int j = 0; j < A.length; j++)
			{
				if (A[i][j] == 0)
					continue;

				for (int m = 0; m < A.length; m++)
				{
					if (m != i)
						sum+= A[j][m] * A[m][i];
				}
			}

		int bi = sum/2;

		return bi;
	}
	
	
	public static double OverallDegreeOfClustering (IAtomContainer container)
	{
		double acc [] =  AtomClusteringCoefficient(container);

		int n = container.getAtomCount();
		double sum = 0.0;
		for (int i = 0; i < n; i++)
			sum += acc[i];

		if (n > 0)
			sum = sum /n;

		return sum;
	}
	
	public static int[] DistanceDegreeVector (IAtomContainer container)  //DS[i]
	{
		int [][]D  =  GraphMatrices.getTopDistanceMatrix(container);
		int n = container.getAtomCount();
		int ddv[] = new int[n];

		for(int i = 0; i < n; i++)
		{
			int sum = 0;
			for(int j = 0 ; j < n; j++)
				sum = sum + D[i][j];
			
			ddv[i] = sum;
		}

		return ddv;
	}

	public static int[] SquareDistanceDegreeVector (IAtomContainer container)
	{
		int [][]D  =  GraphMatrices.getTopDistanceMatrix(container);

		int n = container.getAtomCount();
		int sddv[] = new int[n];

		for(int i = 0; i < n; i++)
		{
			int sum = 0;
			for(int j = 0 ; j < n; j++)
				sum = sum + D[i][j]*D[i][j];
			
			sddv[i] = sum;
		}

		return sddv;
	}
	
	public static double [] ClosenessCentrality (IAtomContainer container)
	{		

		int n = container.getAtomCount();
		double CC[] = new double[n];
		
		for (int i = 0; i < n; i++)
		{
			CC[i] = 0.0;
			double A = container.getAtomCount();
			int vdd [] =  DistanceDegreeVector(container);
			
			CC[i]=(A-1)/vdd[i];
		}
		return CC;
	}
	
	public static double[] D_t (IAtomContainer container)
	{		
			
		int[] dv = getAtomTopDegrees(container);
		int ddv[] = DistanceDegreeVector(container);
		
		int n = container.getAtomCount();
		double t[] = new double[n];
		double sum = 0.0;
		for(int i = 0; i < n; i++)
		{
			t[i] = 0.0;
			
		sum = ddv[i] - dv[i];
		
		t[i] = sum;
		}
		return t;
	}

	
	
	
	
	
}
	
