package ambit2.groupcontribution.utils.math;

public class ApplicabilityDomain 
{
	public static MatrixDouble calculateHMatrixUsingInvC(MatrixDouble invC, MatrixDouble At)
	{
		//Calculating leverages - the diagonal elements of H matrix
		//H = At.inv(A'.A).At'  =  At.invC.At'
		MatrixDouble M1 = MathUtilities.Multiply(At, invC);
		MatrixDouble H = MathUtilities.Multiply(M1, At.transposed());
		return H;
	}
	
}
