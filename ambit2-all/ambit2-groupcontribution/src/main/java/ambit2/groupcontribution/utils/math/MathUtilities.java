package ambit2.groupcontribution.utils.math;

public class MathUtilities 
{

	public static double columnsScProd(MatrixDouble m1, int col1, MatrixDouble m2, int col2 )
	{
		double sc_prod = 0;


		if ((col1>=0) && (col1<m1.nColumns) && (col2>=0) &&
			(col2<m2.nColumns) && (m1.nRows==m2.nRows))
		{
			for (int i=0; i < m1.nRows; i++)
				sc_prod+=m1.el[i][col1]*m2.el[i][col2];
		}

		return (sc_prod);
	}

	public static MatrixDouble Multiply(MatrixDouble m1, MatrixDouble m2)
	{
		int MM = m1.nRows;
		int NN = m1.nColumns;
		int KK = m2.nColumns;

		if (NN != m2.nRows); //m1.n_columns m2.n_rows

		MatrixDouble result = new MatrixDouble(MM,KK); //setMatrixSizes(result,MM,KK);

		double sum;

		for (int i=0; i<MM; i++)
   			for (int k=0; k<KK; k++)
			{
      			sum = 0;
      			for (int j=0; j<NN; j++)
					sum = sum +  m1.el[i][j] * m2.el[j][k];

				result.el[i][k] = sum;
			}

		return result;
	}

}
