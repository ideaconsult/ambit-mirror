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

package ambit2.database.aquire;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.data.experiment.AQUIREStudy;
import ambit2.data.experiment.Experiment;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;

public class AquireExperiment extends Experiment {

	public AquireExperiment() {
		this(AmbitCONSTANTS.AQUIRE,false);
	}

	public AquireExperiment(String name,boolean simpleTemplate) {
		this(name,-1,simpleTemplate);
	}

	public AquireExperiment(String name, int id, boolean simpleTemplate) {
		super(name, id);
		setStudy(new AQUIREStudy(simpleTemplate));
		setReference(ReferenceFactory.createECOTOXReference());
	}
	public AquireExperiment(IMolecule molecule,boolean simpleTemplate) {
		this();
		setStudy(new AQUIREStudy(simpleTemplate));
		setReference(ReferenceFactory.createECOTOXReference());		
		setMolecule(molecule);
	}

	public AquireExperiment(IMolecule molecule, AQUIREStudy study,
			LiteratureEntry reference) {
		super(molecule, study, reference);
	}

	public AquireExperiment(IMolecule molecule, AQUIREStudy study,
			LiteratureEntry reference, Object field, Object result)
			throws AmbitException {
		super(molecule, study, reference, field, result);
	}
	/**

	 * @param field
	 * @return
	 */
	public Object retrieveResult(Object field) {
		try {
			return getResult(field);
		} catch (Exception x) {
			return "";
		}
	}
	public String toString() {
		StringBuffer b = new StringBuffer();
		String endpoint = retrieveResult("Endpoint").toString();
		String bcf = retrieveBCFResult(endpoint, false);
		if ("".equals(bcf)) {
			b.append(endpoint);
			b.append('=');			
			b.append(retrieveResult("concentration1mean"));
			b.append(',');
			b.append(retrieveResult("ConcentrationUnits"));
			b.append(' ');
			b.append(retrieveResult("LatinName"));
			//b.append(' ');
			//b.append(retrieveReferense());
		} else {
			b.append(bcf);
			b.append(' ');
			b.append(retrieveResult("LatinName"));
		}
				
		return b.toString();
	}
    public String toString(boolean verbose) {
    	if (!verbose) return toString();
        StringBuffer b = new StringBuffer();
        
            try {
            b.append("CAS Registry No.\t");
            b.append(IdentifiersProcessor.hyphenateCAS(getResult("testcas").toString()));
            b.append('\n');
            } catch (Exception x) {
                
            }
            b.append("Chemical Name\t");
            b.append(retrieveResult("Chemical_Name"));
            b.append('\n');
            if (retrieveResult("TestFormulation")!=null) {
                b.append("\tFormulation\t");
                b.append(retrieveResult("TestFormulation"));
                b.append('\n');
            }
            if (retrieveResult("TestRadioLabel")!=null) {
                b.append("\tRadio label\t");
                b.append(retrieveResult("TestRadioLabel"));
                b.append('\n');
            }
            if (retrieveResult("TestCharacteristics") != null) {
                b.append("\tCharacteristics\t");
                b.append(retrieveResult("TestCharacteristics"));
                b.append('\n');            
            }
            
            String endpoint = retrieveResult("Endpoint").toString();
            b.append("Endpoint\t");
            b.append(endpoint);
            b.append('\n');  

            Object e = retrieveResult("LatinName");
            if (e != null) {
                b.append("Species:\t");
                b.append(e);
                b.append('\n');
            }
            
            e = retrieveResult("Effect");
            if (e != null) {
                b.append("Effect:\t");
                b.append(e);
                b.append('\n');
            }
            e = retrieveResult("trend");
            if (e != null) {
                b.append("Trend:\t");
                b.append(e);
                b.append('\n');
            }
            e = retrieveResult("tissue");
            if (e != null) {
                b.append("Tissue/Response Site:\t");
                b.append(e);
                b.append('\n');
            }            

            if (retrieveResult("ion1").equals(""));
            else {
                b.append("Ion1");
                b.append(retrieveResult("ion1"));
                b.append('\n');
            }

            b.append("Concentration Type");
            b.append('\t');
            b.append(retrieveResult("ConcentrationType"));
            b.append('\n');
            
            b.append("Concentration 1:");
            String units = retrieveResult("ConcentrationUnits").toString();
            if (!retrieveResult("concentration1min").equals("NR")) {
                b.append("\tMin\t");
                b.append(retrieveResult("concentration1min"));
                b.append(' ');
                b.append(units);
            }
            if (!retrieveResult("concentration1max").equals("NR")) {
                b.append("\tMax\t");
                b.append(retrieveResult("concentration1max"));
                b.append(' ');
                b.append(units);
            }
            if (!retrieveResult("concentration1mean").equals("NR")) {
                b.append("\tMean\t");
                b.append(retrieveResult("concentration1mean"));
                b.append(' ');
                b.append(units);
            }
            b.append('\n');


            if (retrieveResult("concentration2min").equals("NR") && 
                    retrieveResult("concentration2max").equals("NR") && retrieveResult("concentration2mean").equals("NR")) ;
            else {
                if (retrieveResult("ion2").equals(""));
                else {
                    b.append("Ion2");
                    b.append(retrieveResult("ion2"));
                    b.append('\n');
                }
                
                b.append("Concentration 2:");
                units = retrieveResult("ConcentrationUnits").toString(); 
                b.append("\tMin\t");
                b.append(retrieveResult("concentration2min"));
                b.append(' ');
                b.append(units);
                b.append("\tMax\t");
                b.append(retrieveResult("concentration2max"));
                b.append(' ');
                b.append(units);
                b.append("\tMean\t");
                b.append(retrieveResult("concentration2mean"));
                b.append(' ');
                b.append(units);
                b.append('\n');
            }
                        
            b.append("Organic Carbon Type:\t");
            b.append(retrieveResult("OrganicCarbonType"));
            b.append('\n');

            
            b.append("Water Type:\t");
            b.append(retrieveResult("WaterType"));
            b.append('\n');
            
            b.append("Test Location:\t");
            b.append(retrieveResult("TestLocation"));
            b.append('\n');              

            b.append("Duration:");
            units = retrieveResult("durationunits").toString();
            if (!retrieveResult("MinDuration").equals("NR")) {
                b.append("\tMin\t");
                b.append(retrieveResult("MinDurationOp"));
                b.append(retrieveResult("MinDuration"));
                b.append(' ');
                b.append(units);
            }
            if (!retrieveResult("MaxDuration").equals("NR")) {
                b.append("\tMax\t");
                b.append(retrieveResult("MaxDurationOp"));
                b.append(retrieveResult("MaxDuration"));
                b.append(' ');
                b.append(units);
            }
            if (!retrieveResult("TestDuration").equals("NR")) {
                b.append("\t");
                b.append(retrieveResult("TestDurationOp"));
                b.append(retrieveResult("TestDuration"));
                b.append(' ');
                b.append(units);
            }
            b.append('\n');             

            b.append("Exposure\t");
            b.append(retrieveResult("Exposure"));
            b.append('\n');              
            
            b.append(retrieveBCFResult(endpoint,verbose));
            b.append("REFERENCE\n");
            if (retrieveResult("ReferenceType").equals(""));
            else {
            b.append("Reference Type\t");
            b.append(retrieveResult("ReferenceType"));
            b.append('\n');
            }
            b.append(retrieveReferense());
            b.append('\n');                 
            
            b.append('\n');
            b.append('\n');
            b.append('\n');
            return b.toString();
            
    }
    public String retrieveReferense() {
    	StringBuffer b = new StringBuffer();
        b.append(retrieveResult("Author"));
        b.append(',');
        b.append(retrieveResult("Title"));
        b.append(',');
        b.append(retrieveResult("Source"));    	
        b.append(',');
        b.append(retrieveResult("PublicationYear"));
        return b.toString();
    }
    public String retrieveBCFResult(String endpoint,boolean verbose) {
    	
    	
    	   if (endpoint.startsWith("BCF")) {
    		   StringBuffer b = new StringBuffer();
    		   if (verbose) {
	               b.append("BCF1 (min,max,mean)");
	               b.append('\t');
	               b.append('\t');
	               b.append(retrieveResult("BCF1MinOp"));
	               b.append(retrieveResult("BCF1Min"));
	               b.append('\t');
	               b.append(retrieveResult("BCF1MaxOp"));
	               b.append(retrieveResult("BCF1Max"));
	               b.append('\t');
	               b.append(retrieveResult("BCF1MeanOp"));
	               b.append(retrieveResult("BCF1Mean"));
	               b.append('\n');
	   
	               b.append("BCF2 (min,max,mean)");
	               b.append('\t');
	               b.append('\t');
	               b.append(retrieveResult("BCF2MinOp"));
	               b.append(retrieveResult("BCF2Min"));
	               b.append('\t');
	               b.append(retrieveResult("BCF2MaxOp"));
	               b.append(retrieveResult("BCF2Max"));
	               b.append('\t');
	               b.append(retrieveResult("BCF2MeanOp"));
	               b.append(retrieveResult("BCF2Mean"));
	               b.append('\n');
    		   } else {
    			   b.append(endpoint);
    			   b.append('=');
    			   b.append(retrieveResult("BCF1MeanOp"));
	               b.append(retrieveResult("BCF1Mean"));
    		   }
    		   return b.toString();
           } else return "";
    	   
    }
    @Override
    public IAmbitEditor editor(boolean editable) {
    	return new IAmbitEditor() {
    		public JComponent getJComponent() {
    			JTextPane e = new JTextPane();
    			e.setEditable(false);
    			e.setText(getExplanation());
    			return new JScrollPane(e);
    		}
    		public boolean isEditable() {
    			// TODO Auto-generated method stub
    			return false;
    		}
    		public void setEditable(boolean editable) {
    			// TODO Auto-generated method stub
    			
    		}
    		public boolean view(Component parent, boolean editable, String title) throws AmbitException {
    			// TODO Auto-generated method stub
    			return false;
    		}
    	};
    }
    private String getExplanation() {
    	return toString(true);
    }
    @Override
    public int compareTo(Object o) {
    	AquireExperiment e = (AquireExperiment) o;
		int r = retrieveResult("Endpoint").toString().compareTo(e.retrieveResult("Endpoint").toString());
		return r;
    }
}


