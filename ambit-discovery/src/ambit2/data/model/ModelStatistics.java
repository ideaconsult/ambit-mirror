/* ModelStatistics.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-6 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit2.data.model;

import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.editors.ModelStatisticsEditor;
import ambit2.data.AmbitObject;

/**
 * A class encapsulating statistics for a {@link ambit2.data.model.Model}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-6
 */
public class ModelStatistics extends AmbitObject {
	protected double R2 = 0;
	protected double R2adj = 0;
	protected double R = 0;
	protected double F = 0;
	protected double P = 0;
	protected double Q = 0;
	protected double Q2 = 0;
	protected double RMSE = 0;
	protected double RMSE_CV = 0;

    /**
     * 
     */
    public ModelStatistics() {
        this("Statistics");
    }

    /**
     * @param name
     */
    public ModelStatistics(String name) {
        this(name,-1);

    }

    /**
     * @param name
     * @param id
     */
    public ModelStatistics(String name, int id) {
        super(name, id);
		
		R2 = 0;
    }
	public double getR2() {
		return R2;
	}
	public void setR2(double r2) {
		boolean m = isModified() & (r2 != R2);
		R2 = r2;
		setModified(m);
	}
    
    public synchronized double getF() {
        return F;
    }
    public synchronized void setF(double f) {
        F = f;
        setModified(true);
    }
    public synchronized double getP() {
        return P;
    }
    public synchronized void setP(double p) {
        P = p;
        setModified(true);
    }
    public synchronized double getQ() {
        return Q;
    }
    public synchronized void setQ(double q) {
        Q = q;
        setModified(true);
    }
    public synchronized double getQ2() {
        return Q2;
    }
    public synchronized void setQ2(double q2) {
        Q2 = q2;
        setModified(true);
    }
    public synchronized double getR() {
        return R;
    }
    public synchronized void setR(double r) {
        R = r;
        setModified(true);
    }
    public synchronized double getR2adj() {
        return R2adj;
    }
    public synchronized void setR2adj(double r2adj) {
        R2adj = r2adj;
        setModified(true);
    }
    public synchronized double getRMSE() {
        return RMSE;
    }
    public synchronized void setRMSE(double rmse) {
        RMSE = rmse;
        setModified(true);
    }
    public synchronized double getRMSE_CV() {
        return RMSE_CV;
    }
    public synchronized void setRMSE_CV(double rmse_cv) {
        RMSE_CV = rmse_cv;
        setModified(true);
    }
    /* (non-Javadoc)
     * @see ambit2.data.AmbitObject#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        ModelStatistics m = (ModelStatistics) obj;
		return 
			(R2 == m.R2) 
			
			;
    }
	public Object clone()  throws CloneNotSupportedException {

		ModelStatistics m = new ModelStatistics(name,id);
		m.F = F;
		m.P = P;
		m.Q = Q;
		m.Q2 = Q2;
		m.R = R;
		m.R2 = R2;
		m.R2adj = R2adj;
		m.RMSE = RMSE;
		m.RMSE_CV = RMSE_CV;
		return m;
	}
	/* (non-Javadoc)
     * @see ambit2.data.AmbitObject#toString()
     */
    public String toString() {
        StringBuffer b = new StringBuffer();
    	b.append("R2="); b.append(R2);
    	b.append(",Q2="); b.append(Q2);
    	
    	b.append(",RMSE="); b.append(RMSE);
    	b.append(",RMSEcv="); b.append(RMSE_CV);

    	b.append(",R2adj="); b.append(R2adj);
    	b.append(",R2="); b.append(R);
    	
    	b.append(",F="); b.append(F);
    	b.append(",P="); b.append(P);
    	
    	b.append(",Q="); b.append(Q);
    	return b.toString();
    	
    }
    /* (non-Javadoc)
     * @see ambit2.data.AmbitObject#editor()
     */
    public IAmbitEditor editor() {
        return new ModelStatisticsEditor(this);

    }
}
