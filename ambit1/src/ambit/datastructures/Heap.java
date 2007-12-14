/*
 * Created on 2004-12-1
 *
 * 
 * Robert Sedgewick, Algorithms
 */
package ambit.datastructures;

/**
* Insert a new item.
* Remove the largest item.
* Replace the largest item with a new item (unless the new item is larger).
* Change the priority of an item.
* Delete an arbitrary specified item.
* Join two priority queues into one large one.
* (If records can have duplicate keys, we take “largest” to mean “any record
* with the largest key value.“)
 */

public class Heap {
	private Comparable[] pq;
	private int N;   
	/**
	 * 
	 */
	public Heap(int capacity) {
		pq = new Comparable[capacity + 1];
        N = 0;
	}
	public Heap() {
		this(0);
	}
	private void resize() {
        Comparable[] temp = new Comparable[2*pq.length];
        for (int i = 0; i <= N; i++) temp[i] = pq[i];
        pq = temp;
    }
	private void exchange(int i , int j) {
        Comparable tmp = pq[i];
	    pq[i] = pq[j];
	    pq[j] = tmp;
	}
	
	private void upheap(int k) {
		Comparable v;
		v = pq[k]; 
		//pq[0]= null;
		while ((k>1) && (pq[k/2]).compareTo(v) < 0) {
			pq[k]=pq[k/2]; 
			k =k/2; 
		}	
		pq[k] = v;
	}

	private void downHeap(int k) {
		int i,j;
		Comparable v = pq[k];
		while (k<= (N /2)) {
			j = k+k;
			if (j<N)
				if ((pq[j]).compareTo(pq[j+1]) < 0) j++;
			
			if ( v.compareTo(pq[j])>0) break;
			pq[k]=pq[j]; 
			k=j;
		}
		pq[k] = v;
	}
	/**
	 * The delete operation for an arbitrary element from the heap and 
	 * the change operation can also be implemented by using a simple 
	 * combination of the methods above. For example, if the priority 
	 * of the element at position k is raised, then upheap can be called, 
	 * and if it is lowered then downheap does the job. On the other hand,
	 * the join operation is far more difficult and seems to require a 
	 * much more sophisticated data structure.
	 */
	public boolean isEmpty() { return N == 0; }
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int i = 1; i <= N; i++) {
			b.append("["+i+"]");			
			b.append(pq[i].toString());
			b.append("\t");
		}	
		return b.toString();
	}
	
	/**
	 * 
	 * @param v Comparable
	 * the insert operation on heap. 
	 * Since this operation will increase the size of the heap by one, N
	 * must be incremented. Then the record to be inserted is put into pq[N], 
	 * but this may violate the heap property. If the heap property is violated 
	 * (the new node is greater than its father), then the violation can be 
	 * fixed by exchanging the new node with its father.
	 */
	public void insert (Comparable v) {
		if (N >= pq.length - 1) resize();
		N++;
		pq[N] = v;
		upheap(N);
	}	
	
	/**
	 * The “remove the largest” operation involves almost the same processor.
	 * Since the heap will be one element smaller after the operation, it is necessary
	 * to decrement N, leaving no place for the element that was stored in the last
	 * position. But the largest element is to be removed, so the remove operation
	 * amounts to a replace, using the element that was in pq[N].
	 * @return Comparable
	 */
	public Comparable removeLargest() {
		if (N == 0) return null;
		exchange(1,N);
		Comparable largest = pq[N--];
		downHeap(1);
		return largest;
	}
	/**
	 * 
	 * @param v Comparable
	 * @return Comparable
	 */
	public Comparable replace(Comparable v) {
		pq[0] = v;
		downHeap(0);
		return pq[0];
	}
    /***********************************************************************
     * Test routine.
     **********************************************************************/
     public static void main(String[] args) {
         Heap pq = new Heap();
         pq.insert(new Rational(1,4));
         pq.insert(new Rational(3,5));
         pq.insert(new Rational(2,7));
         pq.insert(new Rational(3,4));
         System.out.println("Priority Queue test");
         while (!pq.isEmpty()) {
            System.out.println(pq.toString());
            System.out.println(pq.removeLargest());
         }    

         pq.insert(new Edge(1,2,20));
         pq.insert(new Edge(0,1,5));
         pq.insert(new Edge(0,2,30));
         pq.insert(new Edge(1,3,10));
         pq.insert(new Edge(3,5,20));
         pq.insert(new Edge(3,4,5));
         pq.insert(new Edge(4,5,15));
         pq.insert(new Edge(2,4,15));         
         pq.insert(new Edge(2,3,10));         
         
         System.out.println("Priority Queue test");
         System.out.println(pq.toString());
         
     }

}
