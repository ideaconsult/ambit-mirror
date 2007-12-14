/**
 * Created on 2005-3-22
 *
 */
package ambit.data.model;

import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorFactory;
import ambit.data.descriptors.DescriptorGroups;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.exceptions.AmbitException;


/**
 * Creates several example {@link Model}
 * @author Nina Jeliazkova
 * Modified: 2005-4-6
 *
 * Copyright (C) 2005,2006  AMBIT project http://ambit.acad.bg
 *
 * Contact: nina@acad.bg
 * 
 */
public class ModelFactory {

	/**
	 * 
	 */
	public ModelFactory() {
		super();
	}

	public static Model createEmptyModel() {
		Model model = new Model("", new ModelType("Linear Regression"));
		model.setReference(ReferenceFactory.createEmptyReference());
		Descriptor d = DescriptorFactory.createEmptyDescriptor();
		d.setOrderInModel(1);
		model.addDescriptor(d); 		
		return model;
	}
	
	public static Model createBCFGrammaticaModel() {
		Model model = new Model("P. Grammatica BCF model", new ModelType("Linear Regression"));
		LiteratureEntry ref = ReferenceFactory.createBCFGrammaticaReference();
		
		model.setReference(ref);
		
		Descriptor d = DescriptorFactory.createvIm();
		d.setOrderInModel(0); model.addDescriptor(d);
		
		d = DescriptorFactory.createnHAcc();
		d.setOrderInModel(1); model.addDescriptor(d);
		
		d = DescriptorFactory.createMATS2m();
		d.setOrderInModel(2); model.addDescriptor(d);
		
		d = DescriptorFactory.createGATS2e();
		d.setOrderInModel(3); model.addDescriptor(d);
		
		d = DescriptorFactory.createH6p();
		d.setOrderInModel(4); model.addDescriptor(d);
		
		
		model.setKeywords("Bioconcentration factor; QSAR");
		
		return model;
	}
	
	public static Model createBCFWINModel() {
		Model model = new Model("BCFWIN model", new ModelType("Linear Regression"));
		LiteratureEntry ref = ReferenceFactory.createBCFWinReference();
		
		model.setReference(ref);
		
		Descriptor d = DescriptorFactory.createLogP(ref);
		d.setOrderInModel(0);
		model.addDescriptor(d); 
		
		model.setKeywords("Bioconcentration factor; QSAR");
		
		return model;
	}
	public static Model createKOWWINModel() {
		Model model = new Model("KOWWIN model", new ModelType("Linear Regression"));
		LiteratureEntry ref = ReferenceFactory.createKOWWinReference();
		
		model.setReference(ref);
		
		Descriptor d = DescriptorFactory.createLogP(ref);
		d.setOrderInModel(0);
		model.addDescriptor(d); 
		
		model.setKeywords("Octanol Water partitioning coefficient; QSAR");
		
		return model;
	}
	
	public static void loadDebnathMutagenicityQSAR(Model model) throws AmbitException {
		/*
		Study study = ExperimentFactory.createAMES("Carcinogenicity",new DefaultTemplate("AMES"));
		ExperimentFactory.setSalmonellaTA98(study);
		model.setStudy(study);
		*/
		model.setReference(ReferenceFactory.createDebnathReference());
		
		LiteratureEntry ref = ReferenceFactory.createEmptyReference();
		
		Descriptor d = DescriptorFactory.createLogP(ref);
		d.setOrderInModel(0);
		model.addDescriptor(d); 
		
	
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(DescriptorFactory.createDescriptorElectronicGroup());
		d = DescriptorFactory.createDescriptor("EHOMO","eV",g,ref);
		d.setReference(ref);
		d.setUnits("eV");
		d.setOrderInModel(1);
		model.addDescriptor(d);

		d = DescriptorFactory.createDescriptor("ELUMO","eV",g,ref);
		d.setReference(ref);
		d.setOrderInModel(2);
		model.addDescriptor(d);
		
		d = new Descriptor("IL",ref);
		d.setRemark("It is 1 for compounds containing three or more fused rings, and 0 for all other");
		g = new DescriptorGroups();
		g.addItem(DescriptorFactory.createDescriptorIndicatorGroup());
		d.setDescriptorGroups(g);
		d.setOrderInModel(3);
		model.addDescriptor(d);
		
		model.setEquation("log TA98 = 1.08 log P + 1.28 EHOMO 0.73 ELUMO + 1.46 IL + 7.20");
		model.setKeywords("Aromatic amines; 2-Aminonaphthalene; 2-Aminofluorene; 4-Aminobiphenyl; Salmonella; Mutagenicity; Alkyl substituents; QSAR");
		
		
	}
	public static Model createDebnathMutagenicityQSAR() throws AmbitException {
		Model model = new Model("Debnath Mutagenicity model", new ModelType("Linear Regression"));
		loadDebnathMutagenicityQSAR(model);
		return model;
	}
}
