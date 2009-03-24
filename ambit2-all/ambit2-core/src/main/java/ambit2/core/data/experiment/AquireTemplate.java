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

package ambit2.core.data.experiment;

import ambit2.base.data.Template;
import ambit2.core.config.AmbitCONSTANTS;



public class AquireTemplate extends Template {
	protected boolean simple = true;
    public static String[] AQUIREConditions = {
    		"Endpoint","LatinName","ConcentrationUnits","AquireLocation","Effect","Exposure","TestDuration","TestDurationOp","Tissue",
            "MinDuration","MinDurationOp","MaxDuration","MaxDurationOp","durationunits",
            "ConcentrationType", 
            "ion1",
            "ion2",
            "ReferenceDB","ReferenceType",
            "Author","Title","Source","PublicationYear",
            "TestFormulation","TestRadioLabel","TestCharacteristics",
            "Trend","WaterType","TestLocation","OrganicCarbonType","testcas","Chemical_Name"};

    /*
    public static String[] AQUIREConditionsUnits = {
            "","","","","",
            "durationunits","durationunits","durationunits",
            "",
            "","ConcentrationUnits","ConcentrationUnits","ConcentrationUnits",
            "","ConcentrationUnits","ConcentrationUnits","ConcentrationUnits",
            "","",
            "","","","",
            "","","",
            "","","",""
    };
    */
    public static String[] AQUIREResults = {
    		"concentration1mean","BCF1Mean","concentration1min","concentration1max",
    		"concentration2mean","concentration2min","concentration2max",
            "BCF1MeanOp","BCF1MinOp","BCF1Min","BCF1MaxOp","BCF1Max",     
            "BCF2MeanOp","BCF2Mean","BCF2MinOp","BCF2Min","BCF2MaxOp","BCF2Max"};
    
    public AquireTemplate() {
    	this(false);
    }
	public AquireTemplate(boolean simple) {
		super(AmbitCONSTANTS.AQUIRE);
		setSimple(simple);
	}

	public void setSimple(boolean simple) {
		this.simple = simple;
		if (simple)
			setName("ECOTOX-AQUIRE-SIMPLE");
		else
			setName("ECOTOX-AQUIRE");
		clear();
		int n = AQUIREConditions.length;
		if (simple) n = 3;
        for (int i=0; i < n;i++)
            addFields(AQUIREConditions[i],"",false,false);
        
        n = AQUIREResults.length;
		if (simple) n = 2;
        for (int i=0; i < AQUIREResults.length;i++)
            addFields(AQUIREResults[i],"",true,false);
		
	}
    /*
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        for (int i=0; i < size();i++) {
            b.append(getItem(i));
            b.append(',');
        }
        return b.toString();        
    }
    */

}


