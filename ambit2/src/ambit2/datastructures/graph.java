/**
 * Created on 2004-12-1
 *
 */
package ambit2.datastructures;

import java.util.Vector;

/**
 * This is a test
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class graph {
	Vector edges = null; 
	int[] parent = null;
	/**
	 * 
	 */
	public graph() {
		makeExample();
	}
	private void makeExample() {
		edges = new Vector();
		edges.add(new Edge(1,2,20));
		edges.add(new Edge(0,1,5));
		edges.add(new Edge(0,2,30));
		edges.add(new Edge(1,3,10));
		edges.add(new Edge(3,5,20));
		edges.add(new Edge(3,4,5));
		edges.add(new Edge(4,5,15));
		edges.add(new Edge(2,4,15));         
		edges.add(new Edge(2,3,10));		
	}
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < edges.size(); i++) {
			b.append(edges.get(i).toString() + "\n");
		}
		return b.toString();
	}
	
	/*
	 * This function returns true if the two given vertices are in the same component.
	 * In addition, if they are not in the same component and the union flag is set,
	 * they are put into the same component.
	 */
	private boolean find(int v1,int v2, boolean union) {
		int i = v1;	while (parent[i] > 0) i = parent[i];
		int j = v2;	while (parent[j] > 0) j = parent[j];
		if (union && (i!=j)) parent[j] = i;
		return (i != j);
	}
	private boolean fastfind(int v1,int v2, boolean union) {
		int i = v1;	while (parent[i] > 0) i = parent[i];
		int j = v2;	while (parent[j] > 0) j = parent[j];
		int t ;
		while (parent[v1] > 0) {
			t = v1; v1 = parent[v1]; parent[t] = i;
		}
		while (parent[v2] > 0) {
			t = v2; v2 = parent[v2]; parent[t] = j;
		}		
		if (union && (i!=j)) {
			if (parent[j] < parent[i]) {
				parent[j] = parent[j] + parent[i]-1;
				parent[i] = j; 
			} else {
				parent[i] = parent[i] + parent[j]-1;
				parent[j] = i;
			}
		}
		return (i != j);
	}
	private void findInit(int V) {
		if (parent == null) {
			parent = new int[V+1];
			for (int i = 1; i <= V; i++) {
				parent[i] = 0;
			}
		}
	}
	/**
	 * Kruskal's algorithm:
	 *    sort the edges of G in increasing order by length
	 *    keep a subgraph S of G, initially empty
	 *    for each edge e in sorted order
	 *        if the endpoints of e are disconnected in S
	 *                add e to S
	 *                    return S
	 *
	 * The line testing whether two endpoints are disconnected looks like 
	 * it should be slow (linear time per iteration, or O(mn) total). 
	 * But actually there are some complicated data structures that let us
	 * perform each test in close to constant time; this is known as the
	 * union-find problem. 
	 * The slowest part turns out to be the sorting step, which takes O(m logger n) time.
	  */
	public Vector Kruskal(int V, int E) {
		Vector MST = new Vector();

		Heap pq = new Heap(E);
		//sort the edges in increasing order by length
		for (int i = 0; i < E; i++)
			pq.insert((Edge) edges.get(i));
		
		System.out.println(pq);
		//keep a subgraph S of G, initially empty
		
		Edge edge = null;
		findInit(V);
		//for each edge e in sorted order
		int i = 0;
		while (i<(V-1)) {
			edge = (Edge) pq.removeLargest();
			//System.out.println("remove "+edge);
			//if the endpoints of e are disconnected in S
			if (fastfind(edge.getV1()+1,edge.getV2()+1,true)) {
				i++;
				MST.add(edge);
			}
		}
		System.out.println(i+"MST "+pq.toString());
		return MST;


	}

	public static void main(String[] argv) {
		graph g = new graph();
		System.out.println(g.Kruskal(6,9));
	}
	
}
