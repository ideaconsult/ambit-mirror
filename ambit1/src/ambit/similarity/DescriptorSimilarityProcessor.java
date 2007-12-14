/* DescriptorSimilarityProcessor.java
 * Author: Nina Jeliazkova
 * Date: Sep 22, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IDescriptor;

import ambit.domain.DataCoverageDistance;
import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitResult;
import ambit.processors.results.FingerprintProfile;
import ambit.ui.data.AmbitResultViewer;

public class DescriptorSimilarityProcessor extends DefaultSimilarityProcessor {
	protected DataCoverageDistance descriptorSimilarity;
	protected List<String> descriptors;
	protected Vector points;
	protected double[][] temporarypoint1 = null;
	protected double[][] temporarypoint2 = null;

	public DescriptorSimilarityProcessor() {
		super();
		setPredicting(false);
		descriptors = new ArrayList<String>();
		descriptorSimilarity = new DataCoverageDistance();
		points = new Vector();
	}

	public void buildInitialize() throws AmbitException {
		super.buildInitialize();
		setResult(null);
		points.clear();
		descriptorSimilarity.clear();
		temporarypoint1 = null;
		temporarypoint2 = null;
	}

	public void setResult(IAmbitResult result) {
		IAmbitResult oldresult = this.summary;
		/*
		//TODO
		if (result == null)
			consensusBitSet = null;
		else
			consensusBitSet = ((FingerprintProfile) result)
					.profileToBitSet(0.01);
		super.setResult(result);
		propertyChangeSupport.firePropertyChange("Result", oldresult, result);
		*/
	}

	
	public void buildCompleted() throws AmbitException {
		descriptorSimilarity.estimate(points);
		temporarypoint1 = new double[1][descriptors.size()];
		temporarypoint2 = new double[1][descriptors.size()];
		//setResult( );
		
		super.buildCompleted();
		// AmbitCONSTANTS.Fingerprint
	}

	protected void getPoint(IAtomContainer ac, double[] point) throws Exception {
		for (int i=0; i < descriptors.size(); i++) {
			point[i] = Float.parseFloat(ac.getProperty(descriptors.get(i)).toString()); 
		}
	}	
	protected double[] getPoint(IAtomContainer ac) throws Exception {
		double[] point = new double[descriptors.size()];
		getPoint(ac, point);
		return point;
	}
	public void incrementalBuild(Object object) throws AmbitException {
		try {
			IAtomContainer ac = (IAtomContainer) object;
			double[] point = getPoint(ac);
			if (point != null) points.add(point); else throw new AmbitException("No data");
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	public double predict(Object object) throws AmbitException {
		if (isPredicting())

			try {
				getPoint((IAtomContainer) object,temporarypoint1[0]);
				double[] result = descriptorSimilarity.assess(temporarypoint1);
				((IAtomContainer) object).setProperty(getSimilarityProperty(),new Double(result[0]));
				return result[0];
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		else
			return super.predict(object);

	}

	public double predict(Object object1, Object object2) throws AmbitException {
		try {
			getPoint((IAtomContainer) object1,temporarypoint1[0]);
			getPoint((IAtomContainer) object2,temporarypoint2[0]);
			//TODO perform transformation!!!!!!!!!!!!!!
			return descriptorSimilarity.calcDistance(temporarypoint1[0],temporarypoint2[0],descriptors.size());
			/**
			double[] result = descriptorSimilarity.assess(temporarypoint1);
			double[] result = descriptorSimilarity.assess(temporarypoint);
			*/
		} catch (Exception x) {
			throw new AmbitException(x);
		}

	}

	public void close() {
		super.close();
		points.clear();
	}
	public String toString() {

		return "Descriptor similarity";
	}
	public void setParameter(Object parameter, Object value) {
	    // TODO Auto-generated method stub
	    
	}
    public Object getParameter(Object parameter) {
        // TODO Auto-generated method stub
        return null;
    }
}
