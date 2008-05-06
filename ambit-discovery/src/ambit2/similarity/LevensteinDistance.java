/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.similarity;

/**
 * Levenstein (edit) distance between strings. See http://www.nist.gov/dads/HTML/Levenshtein.html
 * @author Nina Jeliazkova
 *
 */
public class LevensteinDistance implements IDistanceFunction<String> {

	public LevensteinDistance() {
		// TODO Auto-generated constructor stub
	}

	public float getDistance(String object1, String object2) throws Exception {
		return getNativeComparison(object1,object2);
	}

	public float getNativeComparison(String s, String t)
			throws Exception {
		  //*****************************
		  // Compute Levenshtein distance
		  int d[][]; // matrix
		  int n; // length of s
		  int m; // length of t
		  int i; // iterates through s
		  int j; // iterates through t
		  char s_i; // ith character of s
		  char t_j; // jth character of t
		  int cost; // cost

		    // Step 1

		    n = s.length ();
		    m = t.length ();
		    if (n == 0) {
		      return m;
		    }
		    if (m == 0) {
		      return n;
		    }
		    d = new int[n+1][m+1];

		    // Step 2

		    for (i = 0; i <= n; i++) {
		      d[i][0] = i;
		    }

		    for (j = 0; j <= m; j++) {
		      d[0][j] = j;
		    }

		    // Step 3

		    for (i = 1; i <= n; i++) {

		      s_i = s.charAt (i - 1);

		      // Step 4

		      for (j = 1; j <= m; j++) {

		        t_j = t.charAt (j - 1);

		        // Step 5

		        if (s_i == t_j) {
		          cost = 0;
		        }
		        else {
		          cost = 1;
		        }

		        // Step 6

		        d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

		      }

		    }

		    // Step 7

		    return d[n][m];

	}


	  //****************************
	  // Get minimum of three values
	  //****************************

	  private int Minimum (int a, int b, int c) {
	  int mi;

	    mi = a;
	    if (b < mi) {
	      mi = b;
	    }
	    if (c < mi) {
	      mi = c;
	    }
	    return mi;

	  }


	
}



