package ambit2.stats.datastructures;

/**
 * <p>Title: Test</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Nina Nikolova
 * @version 1.0
 */

public class Sort {
  public Sort() {
  }

  private void qSortPartial(double a[], int L, int R) {

    int i = L;
    int j = R;
    double w;
    int m= (int) Math.floor((L + R) / 2);
    double x = a[m];

    while (true) {
      while (a[i] < x) i++;
      while (x < a[j]) j--;
      if (i <= j) {
              w = a[i]; a[i] = a[j];
              a[j] = w;
              i ++; j--;
      }
      if (i >= j) break;
    }
     if (L < j) qSortPartial(a,L,j);
     if (i < R) qSortPartial(a,i,R);

  } //qSort partial

  /**
   * QuickSort of array of double
   * @param a array of double
   * @param n length of the array
   */
  public void QuickSortArray(double a[], int n)
  {
	 if (n==0) return; 
     qSortPartial(a,0,n-1);

  } //QuickSortArray

}