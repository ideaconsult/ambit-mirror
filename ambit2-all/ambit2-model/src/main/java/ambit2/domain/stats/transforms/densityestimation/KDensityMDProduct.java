/**
 * Created November 2003
 */
package ambit2.domain.stats.transforms.densityestimation;

import Jama.Matrix;
import ambit2.domain.stats.Tools;
import ambit2.domain.stats.transforms.transformfilters.TransformFilter;

/**
 * Multivariate density estimation by product of 1D Kernel Density Estimates
 * Up to now not used in Ambit   
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class KDensityMDProduct extends KDensityMD {
//       private FBandwidthMode   : TBandwidthMode;
       private Kernel theKernel = new KNormal();
       private KDensity1D kdeList[] = null;

       public KDensityMDProduct(int number)  {
         super(number);
         kdeList = new KDensity1D[number];
         for (int i = 0 ; i < FNumberOfFactors; i++) kdeList[i] = null;
         setTransformFilter(null);
//               BandwidthMode = bm_ruleOfThumb;
       }
       /*
       destructor TKDEGrid.Destroy;
       {
               ffilter = null;
               inherited;
       };
       */

       /**
        * Clear()
        * deallocates kdeList
        */
       public void clear() {
         if (kdeList != null)
            for (int i =  0; i < FNumberOfFactors; i++)
                 if (kdeList[i] != null)  kdeList[i] = null;
            kdeList = null;
       }

       public double getMinValue(int index) {
               if (kdeList[index] != null)
               return kdeList[index].getMinValue(); else return 0;
       }
       public double getMaxValue(int index) {
               if (kdeList[index] != null)
               return kdeList[index].getMaxValue(); else return 0;
       }

       double getAmplitude(double point[]) {
         double result = 1;
         int i;

         for (int j = 0; j < FNumberOfFactors; j++)
             if ((point[j] < getMinValue(j)) || (point[j] > getMaxValue(j)))
               return 0;

         for (int j = 0; j < FNumberOfImportantFactors; j++) {
               i = FImportantFactors[j];
               if (kdeList[i] != null)
               result = result * kdeList[i].getGridPoint(point[i]);
         }
         return result;
       }
       /**
        * 
        * @return maxAmplitude of the estimated density
        */
       public double maxAmplitude() {
         double result = 1;
         for (int i = 0; i < FNumberOfFactors; i++)
            if (kdeList[i] != null) result = result * kdeList[i].maxAmplitude();
         return result;
       }
       /**
        * isEnabled()
        * @return  true if the density was estimated successfully
        */
       public boolean isEnabled() {
               if (super.isEnabled())
               for (int i = 0; i < FNumberOfFactors; i++)
                   if (kdeList[i] != null)  return true;
               return false;
       }
       /**
        * getBestDescriptors
        * fills in FImportantFactors according to TransformFilter.OrderedDescriptors
        * does nothing if filter is not assigned
        */
       void getBestDescriptors() {
           TransformFilter F = getTransformFilter();
           if (F != null) {
             FImportantFactors = F.MostImportantDescriptors();
             FNumberOfImportantFactors =  FImportantFactors.length;
           }
       }
       /*
       void setBandwidthMode(bandwidth : TBandwidthMode);
       {
               if FBandwidthMode != bandwidth  FIsEnabled = false;
               FBandwidthMode = bandwidth
       };
       */

  /**
   * getKDE
   * @param index
   * @return KDE1
   */
        public KDensity1D getKDE(int index) { return kdeList[index]; }
        /**
         * setKDE
         * @param index
         * @param v
         */
        public void setKDE(int index, KDensity1D v) {
             if (kdeList[index] != null) kdeList[index] = null;
             kdeList[index] = v;
        }
        /**
         * getDensityValue
         * @param point
         * @return the density value
         */
        double getDensityValue(double[] point) {

          TransformFilter F = getTransformFilter();
          double result;
          if (F != null)  {
//              double tmp[] = new double [FNumberOfFactors];
              Matrix tmp = new Matrix(1,FNumberOfFactors);
              double a[][] = tmp.getArray();
              F.TransformPoints(tmp);
              result = 1;

              for (int j = 0; j < FNumberOfFactors; j++)
                  if ((a[0][j] < getMinValue(j)) || (a[0][j] > getMaxValue(j)))
                    return 0;

              for (int j = 0; j < FNumberOfImportantFactors; j++) {
                  int i = FImportantFactors[j];
                  if (kdeList[i] != null)
                  result = result * kdeList[i].getGridPoint(a[0][i]);
              }
              tmp = null;
          } else result = getAmplitude(point);
          return result;
        }
        /**
         * getMostDenseVector
         * @param p
         */
        public void getMostDenseVector(double[] p) {
        for (int i = 0; i < FNumberOfFactors; i++)
            if (kdeList[i] != null)
              /*
             if (kdeList[i].sparceGrid != null) kdeList[i].maxSparceAmplitude(p[i]);
             else
               */
               p[i] = kdeList[i].getMostDenseVector();
            else p[i] = 0;
        }
  /**
  * getMinVector
  * @param minv
  */
        void getMinVector(double minv[])   {
          for (int i = 0; i < FNumberOfFactors; i++)
            if (kdeList[i] != null)  minv[i] = kdeList[i].getMinValue();
            else minv[i] = 0;
        }
        /**
         * getmaxVector
         * @param maxv
         */
        void getMaxVector(double maxv[]) {
          for (int i = 0; i < FNumberOfFactors; i++)
            if (kdeList[i] != null)  maxv[i] = kdeList[i].getMaxValue();
            else maxv[i] = 0;
        }
        public String toString() {
                return "Multivariate probability density as product of 1D densities";
        }

        public boolean ProcessData(Matrix points) {

        int npoints = points.getRowDimension();
        FIsEnabled = false;
        double A[] = new double[npoints];
        double parray[][] = points.getArray();
        for (int p = 0; p< FNumberOfFactors; p++) {
             for (int i = 0; i < npoints; i++) A[i] = parray[i][p];
             KDensity1D akde = null;
             double datarange[] = new double[2];
             if (Tools.ExtendedInterval(A,npoints, datarange))  {
                 akde = new KDensity1D();
                 FIsEnabled = FIsEnabled |
                 akde.estimateDensity(A, npoints, theKernel, 1024, 0,
                                      datarange[0], datarange[1], null);
             }
             kdeList[p] = akde;
        }
        getBestDescriptors();
        A = null;
        return FIsEnabled;
        } //ProcessData
}




/*
function TKDEGrid.toStatus(var slist : TStringList) : string;
{
        result = inherited toStatus(slist);
        if not Enabled  slist.add('Probability density NOT estimated.')
        else if length(FIndices) != length(kdelist)
                slist.add(Format('%d out of %d descriptors retained',[length(findices),length(kdelist)]));

};
*/

/*
function TKDEGrid.to3DGrid(xnumbers : array of int ;
                        xmin,xmax : array of double;
                        currentPoint : TDoubleVector;
                        GridResolution : int ) : T3DDoubleMatrix;
var p : array [0..2] of TDoubleVector;
    i,j,k : int ;
    s, threshold : double;
    ok : boolean;
{

ok = (kdelist != null);
for (int i =  low(xnumbers) to high(xnumbers) do
    ok = ok and (xnumbers[i] >= 0) and (xnumbers[i] < FNumberOfFactors);
if ok  {

        InitializeMatrix(result,GridResolution,GridResolution,GridResolution);
        s = 1; threshold = 1;
        for (int i =  low(xnumbers) to high(xnumbers) do
          with kdeList[xnumbers[i]] do {
               SetSparceGrid(xmin[i],xmax[i],GridResolution);
               s = s * sparceStep;
               threshold = MaxSparceAmplitude * threshold;
               p[i] = SparceGrid;
          };

        if s == 0  s = 1;
        if assigned(HPDLevels)
        threshold = MultivariateHPDLevels[1,1]
        else
        threshold = 0;
        for (int i =  low(p[0]) to high(p[0]) do
            for j = low(p[1]) to high(p[1]) do
              for k = low(p[2]) to high(p[2]) do {
                  result[i,j,k] = p[0,i]*p[1,j]*p[2,k] ;
//                  if (result[i,j,k]) < threshold
//                     result[i,j,k] = 0
              };

} else
        result = null;
};



function TKDEGrid.to2DGrid(xnumbers : array of int ;
                        xmin,xmax : array of double;
                        currentPoint : TDoubleVector;
                        GridResolution : int ) : TDoubleMatrix;

{

if FFilter != null
   result = inherited to2dGrid(xnumbers,xmin,xmax,currentpoint,GridResolution)
else
   result = to2DGridUnfiltered(xnumbers,xmin,xmax,currentpoint,GridResolution);
};

function TKDEGrid.to2DGridUnfiltered(xnumbers : array of int ;
                        xmin,xmax : array of double;
                        currentPoint : TDoubleVector;
                        GridResolution : int ) : TDoubleMatrix;

var p : array [0..1] of TDoubleVector;
    i,j,k : int ;
    s, threshold : double;
    ok : boolean;
{
  ok = (kdelist != null);
  for (int i =  low(xnumbers) to high(xnumbers) do
      ok = ok and (xnumbers[i] >= 0) and (xnumbers[i] < FNumberOfFactors);
  if ok  {

          InitializeMatrix(result,GridResolution,GridResolution);
          s = 1; threshold = 1;
          for (int i =  low(xnumbers) to high(xnumbers) do
            with kdeList[xnumbers[i]] do {
                 SetSparceGrid(xmin[i],xmax[i],GridResolution);
                 s = s * sparceStep;
                 threshold = MaxSparceAmplitude * threshold;
                 p[i] = SparceGrid;
            };

          for (int i =  low(p[0]) to high(p[0]) do
              for j = low(p[1]) to high(p[1]) do {
                  result[i,j] = p[0,i]*p[1,j];
              };
  } else
          result = null;
};


function TKDEGrid.to1DGridUnfiltered(xnumbers : array of int ;
                                xmin,xmax : array of double;
                                currentPoint : TDoubleVector;
                                GridResolution : int ) : TDoubleVector;
var
    i,j,k : int ;
    ok : boolean;
{
  result = null;
  ok = (kdelist != null);
  for (int i =  low(xnumbers) to high(xnumbers) do
      ok = ok and (xnumbers[i] >= 0) and (xnumbers[i] < FNumberOfFactors);
  if ok  {

          setlength(result,GridResolution);
          for (int i =  low(xnumbers) to high(xnumbers) do
            with kdeList[xnumbers[i]] do {
                 SetSparceGrid(xmin[i],xmax[i],GridResolution);
                for j = low(SparceGrid) to high(SparceGrid) do
                        result[j] = SparceGrid[j];

            };
  } else
          result = null;

};
function TKDEGrid.to1DGrid(xnumbers : array of int ;
                        xmin,xmax : array of double;
                        currentPoint : TDoubleVector;
                        GridResolution : int ) : TDoubleVector;
{
    result = inherited to1dgrid(xnumbers,xmin,xmax,currentpoint,GridResolution);
};

*/

/*
void TKDEGrid.LoadFromStream(Stream: TStream);
var i,n : int ;
    d : word;
{
  Clear;
  Stream.ReadBuffer(d,2);
  if d == KDE_DELIMITER  {
    Stream.ReadBuffer(n , sizeof(int ));
    if n > 0  {
      setlength(kdeList,n);
      for (int i =  low(kdeList) to high(kdeList) do {
          kdeList[i] = TKDE.Create;
          kdeList[i].LoadFromStream(Stream);
      };
      Stream.ReadBuffer(d,2);
    };
  };

};

void TKDEGrid.SaveToStream(Stream: TStream);
var n,i : int ;
{
  Stream.WriteBuffer(KDE_DELIMITER,2);
  n = length(kdeList);
  Stream.WriteBuffer(n , sizeof(int ));
  for (int i =  low(kdeList) to high(kdeList) do
      kdeList[i].SaveToStream(Stream);
  Stream.WriteBuffer(KDE_DELIMITER,2);
};


function TKDEGrid.HellingerDistance(kdegrid : TKdeGrid) : TDoubleVector;
var  i : int ;
{

        setlength(result,length(kdelist));
        GlobalLogger.OnStatus('PCA transformation matrix should be the same (or null) in both data sets, otherwise comparison is wrong',-1);
        for i  = low(kdelist) to high(kdelist) do {
            if (kde[i] == null) or ( kdegrid.kde[i] == null)
                result[i] = 0
            else
                result[i] = kde[i].HellingerDistance(kdegrid.kde[i]);
//            GlobalLogger.OnStatus(format('Component %d%s%5.3f',[i+1,chr(9),result[i]]),-1);
        };
};

function TKDEGrid.Kernel2Points(k : string; index : int ; n : int ) : TDoubleVector;
{
if kde[index] != null  result = kde[index].Kernel2Points(k,n)
else result = null;
};

*/



