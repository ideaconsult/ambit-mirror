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

package ambit2.similarity.stats;

import java.util.Enumeration;
import java.util.Hashtable;

import ambit2.model.IModelStatistics;



public abstract class ModelValuesHistogram<T extends Comparable,U extends Bin> 
														implements IModelStatistics<T>, 
														IModelHistogram<U> {
	protected String title;
	protected Hashtable<Comparable,Histogram<Bin<T>>> distance_histogram;
	protected ModelStatus status;
	
	public ModelStatus getStatus() {
		return status;
	}

	public void setStatus(ModelStatus status) {
		this.status = status;
	}

	public ModelValuesHistogram(String title) {
		super();
		setTitle(title);
		distance_histogram = new Hashtable<Comparable,Histogram<Bin<T>>>();
	}

	public Enumeration<Comparable> getDatasets() {
		return distance_histogram.keys();
	}

	public Histogram<U> getHistogram(Comparable dataset, int mode) {
		return (Histogram<U> )distance_histogram.get(dataset);
	}
	/*
	public Set <Entry<Comparable,Histogram<Bin<T>>>> entrySet() {
		distance_histogram.keys()
	    return   distance_histogram.entrySet();
    }
    */
	public String toStatus(boolean verbose) {
		if (!verbose) return toString();
  	    StringBuffer b = new StringBuffer(); 
		Enumeration<Comparable> keys = distance_histogram.keys();
		while (keys.hasMoreElements()) {
			Comparable c = keys.nextElement();
			b.append('\n');
			b.append(c.toString());
			b.append("Similarity values histogram");
			b.append('\n');
			b.append(distance_histogram.get(c).toString());
		}
		return b.toString();
	}
	
	protected abstract Bin<T> createBin(T value);
	
	public void addPoint(Comparable dataset, T observed, T predicted) {};
	public void addValue(Comparable dataset, Object subset, T value) {
		Histogram<Bin<T>>  h = distance_histogram.get(dataset);
		Bin<T> bin = createBin(value);
		if (h == null)  {
			h = new Histogram<Bin<T>>();
			distance_histogram.put(dataset,h);
		}
		h.addObject(bin);
	}


	public void clear() {
		distance_histogram.clear();
		
	}


	public void clear(Comparable dataset) {
		
		Histogram<Bin<T>> h = distance_histogram.get(dataset);
		if (h != null) h.clear();
		
	}
	 @Override
	public String toString() {
		return title;

	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isPredicting() {
		return ModelStatus.PREDICTING.equals(getStatus());
	}
}


