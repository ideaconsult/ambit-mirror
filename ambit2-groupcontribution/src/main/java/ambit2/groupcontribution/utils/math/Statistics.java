package ambit2.groupcontribution.utils.math;

import java.util.List;


public class Statistics 
{
	
	public static double mean(double m[])
	{
		double res = 0;
		for (int i = 0; i < m.length; i++)			
				res+=m[i];		
		
			res = res /(m.length);
		
		return(res);
	}
	
	public static double mean(MatrixDouble m)
	{
		double res = 0;
		
		for (int i = 0; i < m.nRows; i++)
			for (int j = 0; j < m.nColumns; j++)
				res+=m.el[i][j];
		
		if ((m.nRows > 0)&&(m.nColumns > 0))
			res = res /(m.nRows*m.nColumns);
		
		return(res);
	}
	
	
	
	public static double mean(List<Double> v)
	{
		double res = 0;
		for (int i = 0; i < v.size(); i++)			
				res+=v.get(i);
		
		if (v.size()>0)
			res = res /(v.size());
		
		return(res);
	}
	
	public static double corrleationCoefficient(MatrixDouble m1, MatrixDouble m2)
	{
		if ((m1.nRows != m2.nRows)||(m1.nColumns != m2.nColumns))
		{	
			
		}
		
		double res = 0;
		double mean_m1 = mean(m1);
		double mean_m2 = mean(m2);
		double sum_m1 = 0;
		double sum_m2 = 0;
		double sp_m1m2 = 0;
		
		for (int i = 0; i < m1.nRows; i++)
			for (int j = 0; j < m1.nColumns; j++)
			{
				sum_m1 +=(m1.el[i][j]-mean_m1)*(m1.el[i][j]-mean_m1);
				sum_m2 +=(m2.el[i][j]-mean_m2)*(m2.el[i][j]-mean_m2);
				sp_m1m2+=(m1.el[i][j]-mean_m1)*(m2.el[i][j]-mean_m2);
			}
				
		sum_m1 = Math.sqrt(sum_m1);
		sum_m2 = Math.sqrt(sum_m2);
		res = sp_m1m2/(sum_m1*sum_m2);
		
		return(res);
	}
	
	public static double corrleationCoefficient(List<Double> v1, List<Double> v2)
	{
		if (v1.size() != v2.size())
		{	
			//...
		}
		
		double res = 0;
		double mean_v1 = mean(v1);
		double mean_v2 = mean(v2);
		double sum_v1 = 0;
		double sum_v2 = 0;
		double sp_v1v2 = 0;
		double d1, d2;
		
		for (int i = 0; i < v1.size(); i++)			
		{
			d1 = v1.get(i);
			d2 = v2.get(i);
			sum_v1 +=(d1-mean_v1)*(d1-mean_v1);
			sum_v2 +=(d2-mean_v2)*(d2-mean_v2);
			sp_v1v2+=(d1-mean_v1)*(d2-mean_v2);
		}
				
		sum_v1 = Math.sqrt(sum_v1);
		sum_v2 = Math.sqrt(sum_v2);
		res = sp_v1v2/(sum_v1*sum_v2);
		
		return(res);
	}
	
	
	public static double rmsError(MatrixDouble m1, MatrixDouble m2)
	{
		if ((m1.nRows != m2.nRows)||(m1.nColumns != m2.nColumns))
		{
			//...
		}
		
		double res = 0;
		
		for (int i = 0; i < m1.nRows; i++)
			for (int j = 0; j < m1.nColumns; j++)
				res +=(m1.el[i][j]-m2.el[i][j])*(m1.el[i][j]-m2.el[i][j]);
		
		if ((m1.nRows > 0)&&(m1.nColumns > 0))
			res = Math.sqrt(res /(m1.nRows*m1.nColumns));
		
		return(res);
	}
	
	public static double standardError(MatrixDouble m1, MatrixDouble m2, int p)
	{
		//p is the number of model variables
		
		if ((m1.nRows != m2.nRows)||(m1.nColumns != m2.nColumns))
		{
			return -1; //Incorrect sizes
		}
		
		double res = 0;
		
		for (int i = 0; i < m1.nRows; i++)
			for (int j = 0; j < m1.nColumns; j++)
				res +=(m1.el[i][j]-m2.el[i][j])*(m1.el[i][j]-m2.el[i][j]);
		
		if ((m1.nRows > 0)&&(m1.nColumns > 0))
			res = Math.sqrt(res /(m1.nRows*m1.nColumns - p - 1));
		
		
		return(res);
	}
	
	public static double rmsError(List<Double> v1, List<Double> v2)
	{
		if (v1.size() != v2.size())
		{
			return -1; // Incorrect sizes
		}
		
		double res = 0;
		double d1, d2;
		
		for (int i = 0; i < v1.size(); i++)			
		{	
			d1 = v1.get(i);
			d2 = v2.get(i);
			res +=(d1-d2)*(d1-d2);
		}	
		
		if (v1.size() > 0)
			res = Math.sqrt(res / v1.size());
		
		return(res);
	}
	
	public static double meanAbsoluteError(MatrixDouble m1, MatrixDouble m2)
	{
		if ((m1.nRows != m2.nRows)||(m1.nColumns != m2.nColumns))
		{
			//...
		}
		
		double res = 0;
		
		for (int i = 0; i < m1.nRows; i++)
			for (int j = 0; j < m1.nColumns; j++)
				res +=Math.abs(m1.el[i][j]-m2.el[i][j]);
		
		if ((m1.nRows > 0)&&(m1.nColumns > 0))
			res = res /(m1.nRows*m1.nColumns);
		
		return(res);
	}
	
	public static double meanAbsoluteError(List<Double> v1, List<Double> v2)
	{
		if (v1.size() != v2.size())
		{
			return -1; // Incorrect sizes
		}
		
		double res = 0;
		double d1, d2;
		
		for (int i = 0; i < v1.size(); i++)			
		{	
			d1 = v1.get(i);
			d2 = v2.get(i);
			res += Math.abs(d1-d2);
		}	
		
		if (v1.size() > 0)
			res = res / v1.size();
		
		return(res);
	}
	
	
	//------------------ Contemporary QSAR Statistics ------------------
	
	
	
	
	public static double getTSS(MatrixDouble m1, double meanValue)
	{
		//Total Sum of Squares
		double TSS = 0;
		for (int i = 0; i < m1.nRows; i++)
			for (int j = 0; j < m1.nColumns; j++)			
				TSS += (m1.el[i][j] - meanValue)*(m1.el[i][j] - meanValue);
		
		return(TSS);
	}
	
	public static double getRSS(MatrixDouble m1, MatrixDouble m2)
	{
		//Residual Sum of Squares
		if ((m1.nRows != m2.nRows)||(m1.nColumns != m2.nColumns))
		{
			//...
		}
		
		double RSS = 0;
		
		for (int i = 0; i < m1.nRows; i++)
			for (int j = 0; j < m1.nColumns; j++)
				RSS +=(m1.el[i][j]-m2.el[i][j])*(m1.el[i][j]-m2.el[i][j]);
		
		return(RSS);
	}
	
	
	public static double getR2(MatrixDouble y, MatrixDouble y_model)
	{	
		//This is an alternative formula for R^2 
		double y_mean = mean(y);
		double RSS = getRSS(y, y_model);
		double TSS = getTSS(y,y_mean);
		double R2 = 1 - (RSS/TSS);
		return(R2);
	}
	
	public static double getF(MatrixDouble y, MatrixDouble y_model, int p)
	{
		int n = y.nRows;
		double y_mean = mean(y);
		double RSS = getRSS(y, y_model);
		double TSS = getTSS(y,y_mean);
		double RegSS = TSS - RSS;
		double F =   (RegSS / p) / (RSS / (n - p - 1));
		return(F);
	}
	
	public static double getQ2F1(MatrixDouble y, MatrixDouble y_model, double y_training_mean)
	{	
		//Q2F1 = 1 - PRESS / SS_EXT(Y_TRAIN_MEAN)
		double RRESS = getRSS(y, y_model);
		double TSS = getTSS(y, y_training_mean);
		double Q2F1 = 1 - (RRESS/TSS);
		return(Q2F1);
	}
	
	public static double getQ2F2(MatrixDouble y, MatrixDouble y_model)
	{	
		//Q2F2 = 1 - PRESS / SS_EXT(Y_MEAN)
		double y_mean = mean(y);
		double RRESS = getRSS(y, y_model);
		double TSS = getTSS(y, y_mean);
		double Q2F2 = 1 - (RRESS/TSS);
		return(Q2F2);
	}
	
	public static double getQ2F3(MatrixDouble y, MatrixDouble y_model, MatrixDouble y_training)
	{	
		//Q2F3 = 1 - PRESS / SS_EXT(Y_MEAN)
		double y_training_mean = mean(y_training);
		double RRESS = getRSS(y, y_model);
		double TSS = getTSS(y_training, y_training_mean);
		double Q2F3 = 1 - ((RRESS/(y.nRows*y.nColumns))/(TSS/(y_training.nRows*y_training.nColumns)));
		return(Q2F3);
	}
	
	
	public static double getConcordanceCorrelationCoefficient(MatrixDouble m1, MatrixDouble m2)
	{
		//R_concordance(x,y) = 2Sxy / ( Sx^2 + Sy^2 + (x_mean - y_mean)^2 )
		
		if ((m1.nRows != m2.nRows)||(m1.nColumns != m2.nColumns))
		{	
			//DO something
		}
		
		
		double mean_m1 = mean(m1);
		double mean_m2 = mean(m2);
		double sum_m1 = 0;
		double sum_m2 = 0;
		double sp_m1m2 = 0;
		
		for (int i = 0; i < m1.nRows; i++)
			for (int j = 0; j < m1.nColumns; j++)
			{
				sum_m1 +=(m1.el[i][j]-mean_m1)*(m1.el[i][j]-mean_m1);
				sum_m2 +=(m2.el[i][j]-mean_m2)*(m2.el[i][j]-mean_m2);
				sp_m1m2+=(m1.el[i][j]-mean_m1)*(m2.el[i][j]-mean_m2);
			}
		
		
		int n = m1.nRows*m1.nColumns;
		double res = 2*sp_m1m2 / (sum_m1 + sum_m2 + n*(mean_m1 - mean_m2)*(mean_m1 - mean_m2)); 
				
		return(res);
		
	}
	
	public static double getConcordanceCorrelationCoefficient(List<Double> v1, List<Double> v2)
	{
		//R_concordance(x,y) = 2Sxy / ( Sx^2 + Sy^2 + (x_mean - y_mean)^2 )
		
		double mean_v1 = mean(v1);
		double mean_v2 = mean(v2);
		double sum_v1 = 0;
		double sum_v2 = 0;
		double sp_v1v2 = 0;
		double d1, d2;
		
		for (int i = 0; i < v1.size(); i++)			
		{
			d1 = v1.get(i);
			d2 = v2.get(i);
			sum_v1 +=(d1-mean_v1)*(d1-mean_v1);
			sum_v2 +=(d2-mean_v2)*(d2-mean_v2);
			sp_v1v2+=(d1-mean_v1)*(d2-mean_v2);
		}
		
		int n = v1.size();
		double res = 2*sp_v1v2 / (sum_v1 + sum_v2 + n*(mean_v1 - mean_v2)*(mean_v1 - mean_v2)); 
				
		return(res);
	}
	
	
	
	
}

