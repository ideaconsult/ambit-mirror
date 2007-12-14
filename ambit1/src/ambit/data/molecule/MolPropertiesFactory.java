/* MolPropertiesFactory.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-21 
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

package ambit.data.molecule;

import ambit.data.experiment.DefaultTemplate;
import ambit.data.experiment.Study;
import ambit.data.experiment.StudyTemplate;
import ambit.data.literature.ReferenceFactory;
import ambit.data.model.Model;
import ambit.exceptions.AmbitException;

/**
 * Examples how to create {@link ambit.data.molecule.MolProperties}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-21
 */
public class MolPropertiesFactory {

    /**
     * 
     */
    protected MolPropertiesFactory() {
        super();
    }
    public static MolProperties createDebnathModelProperties() throws AmbitException {
        MolProperties props = new MolProperties();
        props.getProperties().put("eHomo","-10");
        props.getProperties().put("log_P","-2");
        props.getProperties().put("IL","0");
        props.getProperties().put("eLumo","-8");
        props.getProperties().put("CAS","50-00-0");
        props.getProperties().put("SMILES","C=O");
        props.getProperties().put("Compound","xxx");
        props.getProperties().put("Obs","2.62");
        props.getProperties().put("Pred","2.62");
        
        props.moveToIdentifiers("CAS");
        props.moveToIdentifiers("SMILES");
        props.moveToIdentifiers("Compound");
        
        props.moveToDescriptors("eHomo");
        props.moveToDescriptors("eLumo");
        props.moveToDescriptors("log_P");
        props.moveToDescriptors("IL");
        
        props.moveToQSAR("Pred");
        
        StudyTemplate t = new DefaultTemplate("Mutagenicity");
            Model m = props.getQsarModel();
            m.setReference(ReferenceFactory.createDebnathReference());
            m.setName("Debnath Mutagenicity model");
            m.setN_Points(88);
            m.setKeywords("Mutagenicity, aromatic amines, salmonella, TA98");
            
            Study s = m.getStudy();
            s.setTemplate(t);
            s.setName("Mutagenicity of aromatic amines");
            s.setStudyCondition("Species","Salmonella");
            s.setStudyCondition("Endpoint","log(TA98)");
        
        
            props.moveToExperimental("Obs",t.getField("Result"));

        return props;

    }

}
