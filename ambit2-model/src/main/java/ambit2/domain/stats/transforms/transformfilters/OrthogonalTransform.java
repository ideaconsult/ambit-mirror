/**
` * Created November 2003
 */
package ambit2.domain.stats.transforms.transformfilters;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * Principal Component Analysis 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class OrthogonalTransform extends TransformFilter{
  private double[] FMeanVector;
  private double[] FScaleVector;
  private int[] FIndices;
  private double[] EigenValues;
  private Matrix EigenVectors;
  private Matrix EigenVectorsInverse = null;
  private double[] FScaleInverse = null;
  public OrthogonalTransform() {
    super();
    EigenValues = null;
    EigenVectors = null;
    FMeanVector = null;
    FScaleVector = null;
    FScaleInverse = null;
    FIndices = null;
  }

  void Clear()  {
    super.Clear();
    EigenValues = null;
    EigenVectors = null;
    FMeanVector = null;
    FScaleVector = null;
    FScaleInverse = null;
    FIndices = null;
  };

  public String toString() {
         String result = "Principal Components. " +
             Integer.toString(FInputDescriptorNumber) + " descriptors ";
         if (FIndices != null)
           result += Integer.toString(FIndices.length) + " components retained";
        result += ".";
        return result;
  }

  public double getMeanValue(int index) { return FMeanVector[index];}

  public double getScale(int index) { return FScaleVector[index];}

  void setInputDescriptorNumber(int n) {
    FInputDescriptorNumber = n;
    FMinVector = new double[n];
    FMaxVector = new double[n];
    FMeanVector = new double[n];
    FScaleVector = new double[n];
    FScaleInverse = new double[n];
  }

  boolean CalcEigenValues(Matrix m) throws Exception {
    FIndices = null;
    for (int i=0; i < m.getRowDimension(); i++ )
    	for (int j=0; j < m.getColumnDimension(); j++ ) {
    		if (Double.isNaN(m.get(i,j)))
    			throw new Exception(String.format("NaN found at [%d,%d]",i,j));
    	}
    SingularValueDecomposition svd = m.svd();
    EigenValues = svd.getSingularValues();
    EigenVectors = svd.getV();

    FIndices = MostImportantDescriptors();
    return true;

  }

  public double[] DescriptorsRank()  {
    double[] rank = new double[FInputDescriptorNumber];
    double sum = 0;
    for (int i = 0; i < FInputDescriptorNumber; i++) {
      sum += EigenValues[i] * EigenValues[i];
    }
    double partialSum = 0;
    int k = 0;
    for (int i = 0; i < FInputDescriptorNumber; i++) {
      partialSum += EigenValues[i]* EigenValues[i];
      rank[i] = partialSum/sum;
    }
    return rank;
  }

  /**
   * MostImportantDescriptors
   * @return indices of most important descriptors
   * ( those with partialsum <= FDescriptorsRankThreshold )
   */
  public int[] MostImportantDescriptors() {
    double rank[] = DescriptorsRank();
    int n = 0;
    for (int i = 0; i < FOutputDescriptorNumber; i++)
       if (rank[i] < FDescriptorsRankThreshold) n++;
       else { n++; break;}

     int result[] = new int[n];
     n = 0;
     for (int i = 0; i < FOutputDescriptorNumber; i++)
        if (rank[i] < FDescriptorsRankThreshold) {
          result[n] = i; n++;
        }  else { result[n] = i; n++; break;}

     return result;
  }


  public boolean InitializeFilter(Matrix points, int inputNo, int outputNo) throws Exception {
  if (! FilterIsEnabled()) {
     FMinVector    = null;
     FMaxVector    = null;
     FMeanVector   = null;
     setInputDescriptorNumber(inputNo);
     setOutputDescriptorNumber(outputNo);
     boolean first = true;
     int N = points.getRowDimension();
     double a[][] = points.getArray();
     for (int i = 0; i < FInputDescriptorNumber; i++) {
         first = true;
         for (int j = 0; j < N; j++) {
       		if (Double.isNaN(a[j][i]))
      			throw new Exception(String.format("NaN found at [%d,%d], while processing matrix(%d,%d)",j,i,N,inputNo));

             if (first || (a[j][i] < FMinVector[i]))
                 FMinVector[i] = a[j][i];
             if (first || (a[j][i] > FMaxVector[i]))
                 FMaxVector[i] = a[j][i];
             FMeanVector[i] += a[j][i];
             first = false;
         }
     }
     	
     if (N > 1)
     for (int i = 0; i < FInputDescriptorNumber; i++) {
       FMeanVector[i] /= N;
       if (Math.abs(FMaxVector[i]-FMinVector[i]) > 0) {
         FScaleVector[i] = 1/(FMaxVector[i]-FMinVector[i]);
         FScaleInverse[i] = FMaxVector[i]-FMinVector[i];
       } else { FScaleVector[i] = 1; FScaleInverse[i] = 1; }
     }

     Matrix m = new Matrix(N,FInputDescriptorNumber);
     double b[][] = m.getArray();
     for (int j = 0; j < N; j++)
     for (int i = 0; i < FInputDescriptorNumber; i++)
       b[j][i] = (a[j][i] - FMeanVector[i]) * FScaleVector[i];

     CalcEigenValues(m);
     m = null;

     FEnabled = true;
     return true;
  } else return true;
  }



  public Matrix TransformPoints(Matrix  points)  {
    Matrix result = substractVector(points,FMeanVector);
    timesVectorEqual(result,FScaleVector);
    return result.times(EigenVectors);
  }
  /**
   * InverseTransformPoints
   * @param points points in the transformed space
   * @return points in the original space
   */
  public Matrix InverseTransformPoints(Matrix points) throws Exception {
//      timesVectorEqual(result,FScaleVector);
      if (EigenVectorsInverse == null) {
    	double determinant = EigenVectors.det();
    	if (determinant == 0) throw new Exception(String.format("Singular matrix %d",determinant));

        EigenVectorsInverse = EigenVectors.inverse();
      }
      
      Matrix result = points.times(EigenVectorsInverse);
      timesVectorEqual(result,FScaleInverse);
      addVectorEqual(result,FMeanVector);
      return result;
  }
}

/*
unit pca;
{$DEFINE _PCA_JACOBI}
interface
uses datadeclarations, classes, TransformFilter;
typeValue

TPCA = class(TTransformFilter)
private
    FIndices         : TIntegerVector;
    FEigenSingular   : boolean;
    meanvector       : TDoubleVector;
    eigenvalues      : TDoubleVector;
    eigenvectors     : TDoubleMatrix;
    EigenVectorsInverse : TDoubleMatrix;
    function getEigenValue(index : integer) : double;
    procedure setThreshold(percent : double);
protected
    function FilterIsEnabled : boolean; override;
public
  constructor Create;override;
  destructor Destroy; override;
  procedure Clear; override;
  function InitializeFilter(
                points : TDoubleMatrix; inputNo, outputNo : integer) : boolean;overload ; override;
  function InitializeFilter(
                        points : TDoubleMatrix; inputNo, outputNo : integer;
                        pointsToProcess : TIntegerVector;
                        notmark : integer) : boolean; overload; override;

  procedure setMean(mean : TDoubleVector);
  procedure SetEigenVectors(evectors : TDoubleMatrix;
                evalues : TDoubleVector;
                evectorsInverse : TDoubleMatrix);
  procedure Assign(apca : TTransformFilter); override;
  function PCA(covariance : TDoubleMatrix): boolean;
  function TransformPoints(points : TDoublematrix; var outcome : TDoubleMatrix) : boolean; override;
  function TransformPoint(point : TDoubleVector; var outcome : TDoubleVector) : boolean;  override;
  function TransformPointsInPlace(var points : TDoublematrix) : boolean;  override;
  function TransformPointInPlace(var point : TDoubleVector) : boolean;  override;

  function InverseTransformPoints(pcapoints : TDoublematrix; var outcome : TDoubleMatrix) : boolean; override;

  function EigenValuesExtracted : boolean;
  function EigenMatrixInverted : boolean;
  property EigenSingular : boolean read FEigenSingular;

  function toString : string; override;
  procedure Print(var slist : TStringList; delimiter : char); override;
  procedure toStatus(var slist : TSTringList); override;
  function OrderedDescriptors : TIntegerVector; override;
  function DescriptorsRank(numbers : TIntegerVector; var rank : TDoubleVector): boolean; overload; override;
  function DescriptorsRank : TDoubleVector;  overload;override;
  property EigenValue[index : integer] : double read getEigenValue;
  property eVectors : TDoubleMatrix  read Eigenvectors;
  property eVectorsInverse : TDoubleMatrix  read EigenvectorsInverse;
  property eValues : TDoubleVector  read Eigenvalues;

end;

implementation

uses  matrix,  eigenval , logger, forms, sysutils,
        svdcmp;

const
C_RANKThreshold = 1.0;

constructor TPCA.Create;
begin
        eigenvalues := nil;
        EigenVectorsInverse := nil;
        EigenVectors := nil;
        FIndices         := nil;
end;

destructor TPCA.Destroy;
begin
        meanVector := nil;
        Clear;
end;


procedure TPCA.Clear;
begin
        FEigenSingular   := true;
        if EigenVectorsInverse <> nil then
           finalizematrix(EigenVectorsInverse);
        if eigenvectors <> nil then
        finalizeMatrix(eigenvectors);
        eigenvalues := nil;
        FIndices         := nil;
end;

procedure TPCA.setMean(mean : TDoubleVector);
begin
        FInputDescriptorNumber := length(mean);
        FOutputDescriptorNumber := FInputDescriptorNumber;
        meanvector := nil;
        if mean <> nil then meanvector := copyVector(mean);
end;

procedure TPCA.Assign(apca : TTransformFilter);
begin
    if apca is TPCA then begin
            Clear;
            setmean(TPCA (apca).meanvector);
            SetEigenVectors(TPCA (apca).evectors, TPCA (apca).evalues, TPCA (apca).evectorsinverse);
            FEnabled := apca.isEnabled;
    end;
end;


/*

function TPCA.EigenValuesExtracted : boolean;
begin
      result := (EigenVectors <> nil) and (EigenValues <> nil);
end;

function TPCA.EigenMatrixInverted : boolean;
begin
        result := EigenVectorsInverse <> nil;
end;

procedure TPCA.toStatus(var slist : TStringList);
begin
      slist.Add(tostring);
      if EigenValuesExtracted then
         slist.Add('Principal components extracted.')
      else
         slist.Add('Principal components not extracted.');

      if EigenSingular then slist.Add('Eigen matrix singular.');
      if EigenMatrixInverted then slist.Add('Eigen matrix inverted.')
      else slist.Add('Eigen matrix not inverted.');
end;

function TPCA.toString : string;
begin
        result := 'Principal components extraction (data ortogonalization)';
end;

function TPCA.getEigenValue(index : integer) : double;
begin
if (EigenValues <> nil) and (index >= low(EigenValues)) and (index <= high(EigenValues)) then
    result := EigenValues[index]
else result := 0;
end;

procedure TPCA.Print(var slist : TStringList; delimiter : char);
var i : integer;
begin
        if EigenVectors <> nil then begin
            slist.add('[EIGENVECTORS MATRIX]');
            for i := low(EigenVectors) to high(EigenVectors) do
                slist.add(PrintDoubleVector(EigenVectors[i],delimiter,'%e'));
        end;
        if EigenValues <> nil then begin
            slist.add('[EIGENVALUES]');
            slist.add(PrintDoubleVector(EigenValues,delimiter,'%e'));
        end;
        if EigenVectorsInverse <> nil then begin
            slist.add('[INVERTED EIGENVECTORS MATRIX]');
            for i := low(EigenVectorsInverse) to high(EigenVectorsInverse) do
                slist.add(PrintDoubleVector(EigenVectorsInverse[i],delimiter,'%e'));
        end;

end;

function TPCA.TransformPointsInPlace(var points : TDoublematrix) : boolean;
begin
        result := TransformPoints(points,points);
end;

function TPCA.TransformPointInPlace(var point : TDoubleVector) : boolean;
begin
        result := TransformPoint(point,point);
end;

function TPCA.TransformPoints(points : TDoublematrix; var outcome : TDoubleMatrix) : boolean;
var i,n,j : integer;
    tmp : TDoubleVector;
    p100 : integer;
begin
        GlobalLogger.OnStatus('PCA transformation ...',0);
        GlobalLogger.OnStatus(DateTimeToStr(now)+ tostring + 'starts ...',1);
        result := false;
        if length(points) < 0 then exit;

        if not isEnabled then exit;
        //outcome is the same dimensionality as points, the same matrix could be used
        //(transformation can be done in place)
        i :=  length(eigenvectors);
        if (eigenvectors = nil) or (i = 0) or (length(points[0]) <> i) then exit;

        n := length(points);
        p100 := round(n / 100);
        if p100 = 0 then p100 := 1;

        setlength(tmp,i);
        if FIndices = nil then
            for i := low(points) to high(points) do begin

                SubstractVectors(points[i],meanvector,tmp);
                MultiplyMatrixVector(EigenVectors,tmp,outcome[i]);
                AddVectors(outcome[i],meanvector,outcome[i]);
                if (i mod p100) = 0 then Application.ProcessMessages;
                GlobalLogger.OnStatus(Format('%d of %d processed.',[i+1,n]),1);
            end
        else
            for i := low(points) to high(points) do begin
                SubstractVectors(points[i],meanvector,FIndices,tmp);
                MultiplyMatrixVector(EigenVectors,tmp,FIndices,outcome[i]);
                AddVectors(outcome[i],meanvector,FIndices,outcome[i]);
                if (i mod p100) = 0 then Application.ProcessMessages;
                GlobalLogger.OnStatus(Format('%d of %d processed.',[i+1,n]),1);

                j := 0;
                for n := low(outcome[i]) to high(outcome[i]) do begin
                    if (n = findices[j]) then begin
                        if j < high(findices) then
                        inc(j);  continue
                    end
                    else outcome[i,n] := 0;
                end;

            end;
        tmp := nil;
        result := true;
        GlobalLogger.OnStatus(DateTimeToStr(now)+ tostring + ' done.',1);
end;



function TPCA.TransformPoint(point : TDoubleVector; var outcome : TDoubleVector) : boolean;
var i,j,n : integer;
    tmp : TDoubleVector;
begin
        result := false;
        if not isEnabled then begin
                for i := low(point) to high(point) do outcome[i] := point[i];
                result := true;
                exit;
        end;

        //outcome is the same dimensionality as points, the same matrix could be used
        //(transformation can be done in place)
        i :=  length(eigenvectors);

        setlength(tmp,i);
        if FIndices = nil then begin
            SubstractVectors(point,meanvector,tmp);
            MultiplyMatrixVector(EigenVectors,tmp,outcome);
            AddVectors(outcome,meanvector,outcome);
        end else begin
            SubstractVectors(point,meanvector,findices,tmp);
            MultiplyMatrixVector(EigenVectors,tmp,findices,outcome);
            AddVectors(outcome,meanvector,findices,outcome);

                j := 0;
                for n := low(outcome) to high(outcome) do begin
                    if (n = findices[j]) then begin
                        if j < high(findices) then
                        inc(j);  continue
                    end
                    else outcome[n] := 0;
                end;

        end;

        tmp := nil;
        result := true;
end;


function TPCA.InverseTransformPoints(pcapoints : TDoublematrix; var outcome : TDoubleMatrix) : boolean;
var i : integer;
    tmp : TDoubleVector;
begin
        GlobalLogger.OnStatus('PCA inverse transformation ...',0);
        //outcome is the same dimensionality as points, the same matrix could be used
        //(transformation can be done in place)
        result := false;
        i :=  length(EigenVectorsInverse);
        if (EigenVectorsInverse = nil) or (i = 0) or (length(pcapoints[0]) <> i) then exit;

        setlength(tmp,i);
        for i := low(pcapoints) to high(pcapoints) do begin
            SubstractVectors(pcapoints[i],meanvector,tmp);
            MultiplyMatrixVector(EigenVectorsInverse,tmp,outcome[i]);
            AddVectors(outcome[i],meanvector,outcome[i]);
        end;
        tmp := nil;
        result := true;
        GlobalLogger.OnStatus('PCA inverse transformation done',0);
end;

function TPCA.FilterIsEnabled : boolean;
begin
        result := FEnabled and EigenValuesExtracted {and EigenMatrixInverted};
end;

function TPCA.OrderedDescriptors : TIntegerVector;
var i,c : integer;
      xd : TDoubleVector;
begin
        c := length(eigenvalues);
        setlength(result,c);
        for i := 0 to length(eigenvalues)-1 do result[i] := i;
        XD := copyvector(eigenvalues);
        sortWithNumbers(xd,result,c);
        xd := nil;
end;

procedure TPCA.setThreshold(percent : double);
var xn :  TIntegerVector;
    i : integer;
    s,e : double;
begin
        s := 0;FDescriptorsRankThreshold := 0;
        for i := low(eigenvalues) to high(eigenvalues) do s := s + eigenvalues[i];
        if s > 0 then begin
          e := 0;
          xn := OrderedDescriptors;
          for i := high(xn) downto low(xn) do begin
              e := e + eigenvalues[xn[i]]/s;
              if e >= percent then begin
                 FDescriptorsRankThreshold := eigenvalues[xn[i]]/s;
                 break;
              end;
          end;
          xn := nil;
        end;
end;
function TPCA.DescriptorsRank(numbers : TIntegerVector; var rank : TDoubleVector) : boolean;
var i : integer;
    s : double;
begin
        s := 0;
        for i := low(eigenvalues) to high(eigenvalues) do s := s + eigenvalues[i];
        if s = 0 then s := 1;
        for i := low(numbers) to high(numbers) do rank[i] := eigenvalues[numbers[i]] / s;
        result := true;
        setThreshold(C_RANKThreshold);
end;

function TPCA.DescriptorsRank : TDoubleVector;
var i : integer;
    s : double;
begin
        setlength(result,FInputDescriptorNumber);
        s := 0;
        for i := low(eigenvalues) to high(eigenvalues) do s := s + eigenvalues[i];
        if s = 0 then s := 1;
        for i := low(eigenvalues) to high(eigenvalues) do
                result[i] := eigenvalues[i] / s;
        setThreshold(C_RANKThreshold);
end;

end.
*/