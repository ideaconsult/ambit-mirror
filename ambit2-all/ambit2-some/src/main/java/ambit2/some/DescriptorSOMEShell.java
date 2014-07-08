/* DescriptorMopacShell.java
 * Author: Nina Jeliazkova
 * Date: 2008-12-13 
 * Revision: 1.0 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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
package ambit2.some;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;
import ambit2.base.external.ShellException;
import ambit2.core.data.ArrayResult;
import ambit2.core.data.IStructureDiagramHighlights;
import ambit2.core.data.StringDescriptorResultType;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.some.SOMERawReader.someindex;


/**
 * Used by {@link DescriptorSOMEShell} <br> 
 * Invokes some.exe from Mopac 7.1 http://www.dddc.ac.cn/adme/myzheng/SOME_1_0.tar.gz.<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2010-9-4
 */
public class DescriptorSOMEShell implements IMolecularDescriptor , IStructureDiagramHighlights {

    protected static Logger logger = Logger.getLogger(DescriptorSOMEShell.class.getName());
    protected SOMEShell some_shell;
    protected SOMEVisualizer visualizer;
    

    /**
     * 
     */

    public DescriptorSOMEShell() throws ShellException {
        super();
        some_shell = new SOMEShell();
        
    }

    public String toString() {
    	return "Site Of Metabolism Estimator " + some_shell + " (http://www.dddc.ac.cn/adme/myzheng/SOME_1_0.tar.gz.)";
    }
    
    public String[] getParameterNames() {
        return null;
    }
    public Object[] getParameters() {
        return null;
    }
    public Object getParameterType(String arg0) {
        return "";
    }
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
        	String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"SOME"),
            this.getClass().getName(),
            "$Id: DescriptorSOMEShell.java,v 0.2 2010/09/04 18:24:00 jeliazkova.nina@gmail.com$",
            "http://www.dddc.ac.cn/adme/myzheng/SOME_1_0.tar.gz");
    };
    public void setParameters(Object[] arg0) throws CDKException {
    }
    public DescriptorValue calculate(IAtomContainer arg0) {
    	ArrayResult r = null;
    	try {
    		if ((arg0==null) || (arg0.getAtomCount()==0)) throw new CDKException("Empty molecule!");
    		if (!StructureTypeProcessor.has3DCoordinates(arg0)) throw new CDKException("No 3D coordinates!");
    		logger.info(toString());
	        IAtomContainer newmol = some_shell.runShell(arg0);
	        
	        Object value = newmol.getProperty(SOMEShell.SOME_RESULT);
	        
	        final int[] count = new int[SOMERawReader.someindex.values().length];
	        for (int i=0; i < count.length;i++) count[i] = 0;
	        
	        if (value == null) value = "@SOME results: NONE";
	        else {
	        	SOMEResultsParser parser = new SOMEResultsParser() {
	        		@Override
	        		protected void process(int atomNum, String atomSymbol,
	        				someindex index, double value, boolean star) {
	        			if (star) count[index.ordinal()] ++;
	        		}
	        	};
	        	parser.parseRecord(value.toString());
	        }
	         
	        r = new ArrayResult(new Object[] {
	        		value.toString(),
	        		count[someindex.aliphaticHydroxylation.ordinal()],
	        		count[someindex.aromaticHydroxylation.ordinal()],
	        		count[someindex.NDealkylation.ordinal()],
	        		count[someindex.NOxidation.ordinal()],
	        		count[someindex.ODealkylation.ordinal()],
	        		count[someindex.SOxidation.ordinal()],
	        });
        	
	        return new DescriptorValue(
		        		getSpecification(),
		                getParameterNames(),
		                getParameters(),r,getDescriptorNames());    	        	
   		
    	} catch (Exception x) {
    		r = new ArrayResult(new Object[] {
    			x.getMessage(),
    			0,0,0,0,0,0
    		});
	        return new DescriptorValue(getSpecification(),
	                getParameterNames(),getParameters(),r,getDescriptorNames());    
    	}
        
    }
    public String[] getDescriptorNames() {
    	return new String[] {
    			SOMEShell.SOME_RESULT,
        		someindex.aliphaticHydroxylation.name(),
        		someindex.aromaticHydroxylation.name(),
        		someindex.NDealkylation.name(),
        		someindex.NOxidation.name(),
        		someindex.ODealkylation.name(),
        		someindex.SOxidation.name()
    	};
    }
    public IDescriptorResult getDescriptorResultType() {
    	return new StringDescriptorResultType();
    }
    protected void debug(String message) {
    	logger.info(message);
    }
   
    public BufferedImage getImage(IAtomContainer mol) throws AmbitException {
    	if (visualizer==null) visualizer = new SOMEVisualizer();
    	return visualizer.getImage(mol);
    }
    public BufferedImage getImage(IAtomContainer mol,String ruleID,int width,int height,boolean atomnumbers) throws AmbitException {
    	if (visualizer==null) visualizer = new SOMEVisualizer();
    	return visualizer.getImage(mol,ruleID,width,height,atomnumbers);
    }
    
    
    public Dimension getImageSize() {
    	if (visualizer==null) visualizer = new SOMEVisualizer();
		return visualizer.imageSize;
	}
	public void setImageSize(Dimension imageSize) {
		if (visualizer==null) visualizer = new SOMEVisualizer();
		this.visualizer.imageSize = imageSize;
	}
	
	@Override
	public BufferedImage getLegend(int width, int height) throws AmbitException {
		if (visualizer==null) visualizer = new SOMEVisualizer();
		return visualizer.getLegend(width, height);
	}

}
