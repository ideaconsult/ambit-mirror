package ambit2.groupcontribution.utils.math;

//import java.lang.Math;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

public class MatrixDouble
{

	static String fillSpaces[] =
	{
		"",
		" ",
		"  ",
		"   ",
		"    ",
		"     ",
		"      ",
		"       ",
		"        ",
		"         ",
		"          "
	};
		
	public double [][] el;

	public int nRows;
	public int nColumns;
	
	
	
	public MatrixDouble()
	{
		el = new double [10][10];
		nRows = 10;
		nColumns = 10;
	}

	public MatrixDouble(int rows, int columns)
	{
		nRows = rows;
		nColumns = columns;
		el = new double [rows][columns];
	}


	public MatrixDouble(MatrixDouble m)
	{
		paste(m);
	}
	
	/*
	public MatrixDouble(MatrixInt m)
	{
		paste(m);
	}
	*/
	
	
	public MatrixDouble(List<Double> v)
	{
		el = new double [v.size()][1];
		nRows = v.size();
		nColumns = 1;
		
		for (int i = 0; i < nRows; i++)
			el[i][0] = v.get(i).doubleValue();
	}
	
	
	
	/*
	public void paste(MatrixInt m)
	{
		nRows = m.nRows;
		nColumns = m.nColumns;
		el = new double [nRows][nColumns];

		for (int i = 0;i<nRows; i++)
  		for (int j = 0; j<nColumns; j++)
  			el[i][j] = m.el[i][j];
	}
	*/

	public void paste(MatrixDouble m)
	{
		nRows = m.nRows;
		nColumns = m.nColumns;
		el = new double [nRows][nColumns];

		for (int i = 0;i<nRows; i++)
  		for (int j = 0; j<nColumns; j++)
  			el[i][j] = m.el[i][j];
	}

	
	public void nullify()
	{
  	fillWith(0.0);
	}

	public void fillWith(double d)
	{
  	for (int i = 0;i<nRows; i++)
  		for (int j = 0; j<nColumns; j++)
  			el[i][j] = d;
	}

	public void fillDiagonalWith(double d)
	{
  	int k;

		if (nRows <= nColumns)
			k = nRows;
		else
			k = nColumns;

		for (int i = 0; i<k; i++)
  			el[i][i] = d;
	}

	public void setUnitMatrix()
	{
  	fillWith(0.0);
  	fillDiagonalWith(1.0);
	}

	public double norm()
	{
		double res = 0.0;

		for(int i=0; i<nRows; i++ )
			for(int j=0; j < nColumns; j++ )
	   			res += el[i][j]*el[i][j];

		return Math.sqrt(res);
	}
	
	public double sumOfElements()
	{
		double sum = 0;
		for(int i = 0; i < nRows; i++)
			for(int j = 0; j < nColumns; j++)
				sum+= el[i][j];
		return(sum);
	}
	
	public double maxElement()
	{
		double max = el[0][0];
		for(int i = 0; i < nRows; i++)
			for(int j = 0; j < nColumns; j++)
			{	
				if (el[i][j] > max);
					max = el[i][j]; 
			}	
					
		return(max);
	}
	
	public double sumOfColumnElements(int col)
	{
		double sum = 0;
		for(int i = 0; i < nRows; i++)			
				sum+= el[i][col];
		return(sum);
	}
	
	public double sumOfRowElements(int row)
	{
		double sum = 0;
		for(int i = 0; i < nColumns; i++)			
				sum+= el[row][i];
		return(sum);
	}


	public double columnNorm(int col)
	{
		double norm = 0;


		if ((col>=0) && (col<nColumns))
		{
			for (int i=0; i < nRows; i++)
				norm+=el[i][col]*el[i][col];

			norm = Math.sqrt(norm);
		}

		return (norm);
	}


	public MatrixDouble transposed()
	{
		MatrixDouble t = new MatrixDouble(nColumns, nRows);
		for (int i = 0; i < t.nRows; i++)
			for (int j = 0; j < t.nColumns; j++)
				t.el[i][j] = el[j][i];

		return(t);
	}

	
	public void swapRows(int row, int swap)
	{
		double temp;
		for(int j = 0; j < nColumns; j++)
		{
			temp = el[row][j];
			el[row][j] = el[swap][j];
			el[swap][j] = temp;
		}
	}


	public MatrixDouble inverse(double eps)
	{
		if (nRows != nColumns)
			return(null);

		MatrixDouble Imat = new MatrixDouble(nRows,nRows);
		MatrixDouble B = new MatrixDouble(this);



		// This is the pascal Procedure TD_Regression.Invert_Mat_B from IRSS projects

		int i,j,k,imax,jmax;
		double factor,ch,amax;
		double Max;
		boolean Fl_DetB;

		imax = 0;
		jmax = 0;

		int NN = nRows;
		//t.resize(NN);	//vector<unsigned char> t;    // Array[1..48] of Byte;
		int t[] = new int [nRows];


		Fl_DetB = true;

		// Imat = I
		for(i=0; i<NN; i++)		// For i := 1 to NoPr do
			for(j=0; j<NN; j++)		//	For j := 1 to NoPr do
			{
				if (i!=j)
					Imat.el[i][j] = 0;
				else
					Imat.el[i][j] = 1;
			}


		for(i=0; i<NN; i++)
			t[i]=0;



		for(i=0; i<NN; i++)
		{
			amax=0;
			Max=0;
  		for(j=0; j<NN; j++)
				for(k=0; k<NN; k++)
				{
      			if ((t[j]==0)&&(t[k]==0))
        			if (Math.abs(B.el[j][k])>Max)
						{
							Max=Math.abs(B.el[j][k]);
							amax=B.el[j][k];
							imax=j;
							jmax=k;
						}
				}

			t[jmax]=1;


			// Check for det B  = 0
			if (Math.abs(amax) < eps)
			{
				Fl_DetB = false;
				break; //for i
			}


			// Change rows imax and jmax
			if (imax != jmax)
			{
				for(j=0; j<NN; j++)
				{
					ch=B.el[imax][j];
					B.el[imax][j]=B.el[jmax][j];
					B.el[jmax][j]=ch;
					ch=Imat.el[imax][j];
					Imat.el[imax][j]=Imat.el[jmax][j];
					Imat.el[jmax][j]=ch;
				}
			}


			//Multiplication and substraction of rows
			//The Column No jmax becomes (0,0,..,0,1,0,..,0)T
			//1 is at a position jmax
			for(j=0; j<NN; j++)
			{
				factor=B.el[j][jmax];
				for(k=0; k<NN; k++)
				{
					if (j != jmax)
					{
						Imat.el[j][k]=Imat.el[j][k]-Imat.el[jmax][k]*factor/amax;
						B.el[j][k]=B.el[j][k]-B.el[jmax][k]*factor/amax;
					}
				}
			}


			for(j=0; j<NN; j++)
			{
				Imat.el[jmax][j]=Imat.el[jmax][j]/amax;
				B.el[jmax][j]=B.el[jmax][j]/amax;
			}

		} //End of for(i)    end; { of For i := 1 to NoPr }


		if (Fl_DetB)
			return (Imat);
		else
			return(null);

	}//inverse



	public void copyRowFrom(int destRow, MatrixDouble sourceMatr, int sourceRow)
	{
		int n;

		if (nColumns >= sourceMatr.nColumns)
			n = sourceMatr.nColumns;
		else
			n = nColumns;

		for (int i=0; i < n; i++)
			el[destRow][i] = sourceMatr.el[sourceRow][i];
	}


	public void copyColumnFrom(int destColumn, MatrixDouble sourceMatr, int sourceColumn)
	{
		int n;

		if (nRows >= sourceMatr.nRows)
			n = sourceMatr.nRows;
		else
			n = nRows;

		for (int i=0; i < n; i++)
			el[i][destColumn] = sourceMatr.el[i][sourceColumn];
	}
	
	public boolean checkForZeroColumn(int col)
	{
		for (int i = 0; i < nRows; i++)
			if (el[i][col] != 0.0)
				return (false);
		return true;
	}
	
	public boolean checkForZeroRow(int row)
	{
		for (int i = 0; i < nColumns; i++)
			if (el[row][i] != 0.0)
				return (false);
		return true;
	}
	
	public int[] getZeroColumns()
	{
		Vector<Integer> v = new Vector<Integer>();
		for (int i = 0; i < nColumns; i++)
		{
			if (checkForZeroColumn(i))
				v.add(new Integer(i));
		}
		
		int res[] = new int[v.size()];
		for (int i = 0; i < res.length; i++)
			res[i] = v.get(i).intValue();
		
		return (res);
	}



	public static String fill(int numSpaces)
	{
		if (numSpaces > 0)
			return fillSpaces[numSpaces];
		else
			return("");
	}


	public String toString(int digits, int decDigits)
	{
		NumberFormat df = DecimalFormat.getNumberInstance();
		df.setMaximumIntegerDigits(digits);
		df.setMaximumFractionDigits(decDigits);
		df.setGroupingUsed(false);


		StringBuffer s = new StringBuffer();
		for (int i = 0; i<nRows; i++)
		{
			for (int j = 0; j<nColumns; j++)
			{
				String fs = df.format(el[i][j]);
				s.append(fill(digits+decDigits-fs.length())+fs+ "  ");
			}

			s.append("\n");
		}

		return(s.toString());
	}	
	
}




