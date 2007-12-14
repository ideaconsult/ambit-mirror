/* Modeltest.java
 * Author: Nina Jeliazkova
 * Date: Sep 3, 2006 
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

package ambit.test.data.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import junit.framework.TestCase;
import ambit.data.experiment.DefaultTemplate;
import ambit.data.experiment.ExperimentFactory;
import ambit.data.experiment.Study;
import ambit.data.experiment.StudyTemplate;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.data.model.Model;
import ambit.data.model.ModelFactory;
import ambit.data.molecule.MolProperties;
import ambit.data.molecule.MolPropertiesFactory;
import ambit.ui.data.QSARMolPropertiesPanel;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 3, 2006
 */
public class Modeltest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Modeltest.class);
    }
    public Object roundTrip(Serializable model) throws Exception {
        saveProperties(new FileOutputStream("testModel.conf"),model);
        return loadProperties(new FileInputStream("testModel.conf"));
    }
    public void testRoundTripModel() {
        try {
            Model model = ModelFactory.createDebnathMutagenicityQSAR();
            Model newModel = (Model) roundTrip(model);
            assertEquals(model,newModel);
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }
    public void testRoundTripStudy() {
        try {
            Study model = ExperimentFactory.createLC50Fish96hr();
            Study newStudy = (Study) roundTrip(model);
            assertEquals(model,newStudy);
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }
    public void testRoundTripStudyTemplate() {
        try {
            StudyTemplate model = new DefaultTemplate();
            StudyTemplate newStudy = (StudyTemplate) roundTrip(model);
            assertEquals(model,newStudy);
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }
    public void testRoundTripLiteratureEntry() {
        try {
            LiteratureEntry model = ReferenceFactory.createDebnathReference();
            LiteratureEntry newStudy = (LiteratureEntry) roundTrip(model);
            assertEquals(model,newStudy);
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }
    public void testRoundTripMolProperties() {
        try {
            MolProperties props = MolPropertiesFactory.createDebnathModelProperties();
            QSARMolPropertiesPanel p = new QSARMolPropertiesPanel(props);
            //MolPropertiesPanel p = new MolPropertiesPanel(props);
            MolProperties newStudy = (MolProperties) roundTrip(props);
            assertEquals(props,newStudy);
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }    
	protected void saveProperties(OutputStream out, Serializable object) throws Exception {
	    ObjectOutputStream s = new  ObjectOutputStream(out);
	    s.writeObject(object);
	    s.close();
	}
	protected Object loadProperties(InputStream in) throws Exception {
	    Object o = new ObjectInputStream(in).readObject();
	    in.close();
	    return o;
	}
}
