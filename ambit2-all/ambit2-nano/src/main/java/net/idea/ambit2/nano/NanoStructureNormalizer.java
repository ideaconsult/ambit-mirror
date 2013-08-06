/* NanoStructureNormalizer.java
 * Author: nina
 * Date: June 13, 2013
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2013  Ideaconsult Ltd.
 * 
 * Contact: www.ideaconsult.net
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

package net.idea.ambit2.nano;

import java.io.IOException;

import nu.xom.ParsingException;

import org.bitbucket.nanojava.data.Nanomaterial;
import org.bitbucket.nanojava.data.measurement.ICondition;
import org.bitbucket.nanojava.data.measurement.IMeasurement;
import org.bitbucket.nanojava.data.measurement.MeasurementValue;
import org.bitbucket.nanojava.io.Deserializer;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.processors.StructureNormalizer;


/**
 * As {@link StructureNormalizer} but supports nanomaterials
 * @author nina
 *
 */
public class NanoStructureNormalizer extends StructureNormalizer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -796323273484515707L;
	public NanoStructureNormalizer() {
		super();
	}
	public IStructureRecord process(IStructureRecord structure)	throws AmbitException {
		if ((structure.getFormat()!=null) && MOL_TYPE.NANO.name().equals(structure.getFormat())) {
			//process nanomaterial
			try {
				return normalizeNano(structure);
			} catch (IOException x) {
				throw new AmbitException(x);
			} catch (ParsingException x) {
				throw new AmbitException(x);
			}
			
		} else return super.process(structure);
			
		
	}

	/**
	 * Parses the nano CML and sets the properties
	 * @param structure
	 * @return
	 * @throws IOException
	 * @throws ParsingException
	 */
	public static IStructureRecord normalizeNano(IStructureRecord structure)	throws IOException, ParsingException {
		Nanomaterial nanomaterial = Deserializer.fromCMLString(structure.getContent()); 
		return nm2structure(nanomaterial);
	}
	public static IStructureRecord nm2structure(Nanomaterial nanomaterial) {
		return nm2structure(nanomaterial,new StructureRecord());
	}
	public static IStructureRecord nm2structure(Nanomaterial nanomaterial,IStructureRecord structure)  {
		structure.setFormula(nanomaterial.getChemicalComposition()==null?null:  MolecularFormulaManipulator.getHillString(nanomaterial.getChemicalComposition()));
		LiteratureEntry ref = new LiteratureEntry("NanoMaterialMeasurement", "nano");
		ref.setType(_type.Dataset);
		if (nanomaterial.getType()!=null) {
			Property property = new Property("MaterialType",ref);
			property.setLabel("http://www.opentox.org/nano#Material");
			structure.setProperty(property, nanomaterial.getType());
		}
		if (nanomaterial.getSize()!=null) {
			Property property = new Property("Size", nanomaterial.getSize().getUnit().toString(),ref);
			property.setLabel("http://www.opentox.org/nano#Size");
			nmm2property(nanomaterial.getSize(),property,structure);
		}
		if (nanomaterial.getZetaPotential()!=null) {
			Property property = new Property("zetaPotential", nanomaterial.getZetaPotential().getUnit().toString(),ref);
			property.setLabel("http://www.opentox.org/nano#zetaPotential");
			nmm2property(nanomaterial.getZetaPotential(),property,structure);
		}
		for (int i=0;i< nanomaterial.getLabels().size();i++) {
			ref = new LiteratureEntry(String.format("NanoMaterialLabel%d",i+1), "nano");
			ref.setType(_type.Dataset);
			Property property = new Property("Name", ref);
			property.setLabel(Property.opentox_Name);
			structure.setProperty(property,nanomaterial.getLabels().get(i));	
		}
		structure.setType(STRUC_TYPE.NANO);
		structure.setIdchemical(-1);
		structure.setIdstructure(-1);
		structure.setInchi(null);
		structure.setInchiKey(null);
		structure.setSmiles(null);
		return structure;
	}
	
	public static void nmm2property(IMeasurement measurement, Property property, IStructureRecord structure) {
		if (measurement instanceof MeasurementValue) {
			structure.setProperty(property,((MeasurementValue)measurement).getValue());
			//structure.setProperty(property,((MeasurementValue)measurement).getError()); ?
		} else {
			structure.setProperty(property,((MeasurementValue)measurement).toString());
		}
		//Ideally, add conditions as property annotations
		if (measurement.getConditions()!=null) 
			for (ICondition condition : measurement.getConditions()) {
				PropertyAnnotation pa = new PropertyAnnotation();
				pa.setType("condition");
				pa.setObject(condition.toString());
			}
	}
}
