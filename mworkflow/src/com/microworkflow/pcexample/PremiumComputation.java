/*
 * 	 
 *  Copyright (c) 2003 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */
package com.microworkflow.pcexample;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Activity;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Iterative;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

/**
 * This class computes a homeowners' premium the micro-workflow way.
 * This requires:
 * <ol>
 * <li>Setting up the domain objects
 * <li>Setting up the context
 * <li>Building the workflow
 * <li>Running the workflow
 * </ol>
 * The sole purpose of this example is to show how to use the
 * process and execution components of the micro-workflow framework.
 * It doesn't demonstrate any of the advanced workflow features, nor does
 * it show the benefits of flow independence.
 * 
 * @author dam
 */
public class PremiumComputation {

	protected Table baseClassPremium;
	protected Table formFactor;
	protected Table pccFactor;
	protected Table keyFactor;
	protected Table ordinanceOrLawFactor;

	public static void main(String[] args) {
		PremiumComputation pc=new PremiumComputation();
     
		pc.computePremium();
	}

	public void initializeTables() {
		baseClassPremium = new Table();
		baseClassPremium.put(CustomerData.HO_00_03, new Integer(2), new Double(300));
		baseClassPremium.put(CustomerData.HO_00_06, new Integer(2), new Double(100));
		formFactor = new Table();
		formFactor.put(CustomerData.HO_00_03, new Double(1.5));
		formFactor.put(CustomerData.HO_00_06, new Double(1.75));
		pccFactor = new Table();
		pccFactor.put(CustomerData.CONSTRUCTION_TYPE_FRAME, new Integer(8), new Double(1.5));
		pccFactor.put(CustomerData.CONSTRUCTION_TYPE_FRAME, new Integer(9), new Double(1.8));
		pccFactor.put(CustomerData.CONSTRUCTION_TYPE_FRAME, new Integer(10), new Double(2.1));
		pccFactor.put(CustomerData.CONSTRUCTION_TYPE_MASONRY, new Integer(8), new Double(1.1));
		pccFactor.put(CustomerData.CONSTRUCTION_TYPE_MASONRY, new Integer(9), new Double(1.3));
		pccFactor.put(CustomerData.CONSTRUCTION_TYPE_MASONRY, new Integer(10), new Double(1.8));
		keyFactor = new Table();
		keyFactor.put(CustomerData.COVERAGE_A,new Double(10000),new Double(0.45));
		keyFactor.put(CustomerData.COVERAGE_A,new Double(12000),new Double(0.51));
		keyFactor.put(CustomerData.COVERAGE_A,new Double(14000),new Double(0.58));
		keyFactor.put(CustomerData.COVERAGE_A,new Double(16000),new Double(0.67));
		keyFactor.put(CustomerData.COVERAGE_C,new Double(1000),new Double(0.21));
		keyFactor.put(CustomerData.COVERAGE_C,new Double(2000),new Double(0.24));
		keyFactor.put(CustomerData.COVERAGE_C,new Double(3000),new Double(0.28));
		keyFactor.put(CustomerData.COVERAGE_C,new Double(4000),new Double(0.33));
		ordinanceOrLawFactor = new Table();
		ordinanceOrLawFactor.put(new Double(10),new Double(1.1));		
		ordinanceOrLawFactor.put(new Double(20),new Double(1.25));
		ordinanceOrLawFactor.put(new Double(30),new Double(1.38));
		ordinanceOrLawFactor.put(new Double(40),new Double(1.48));
	}

	public Activity getInitialBasePremiumWorkflow() {
		Primitive getBaseValue =
			new Primitive("BaseClassPremiumTable","baseValue",
				new Performer() {
					public Object execute() throws Exception {
						Table table=(Table)getTarget();
						CustomerData cdata=(CustomerData)get("CustomerData");
						return table.get(cdata.getPolicyType(),cdata.getTerritory());
					}
				}
			);
		Primitive makeBasePremium =
			new Primitive("PremiumFactory","baseClassPremium",
				new Performer() {
					public Object execute() throws Exception {
						Premium premiumFactory=(Premium)getTarget();
						Double value=(Double)get("baseValue");
						return premiumFactory.premiumWith(value);
					}
				}
			);
		Primitive getFormFactor =
			new Primitive("FormFactorTable","formFactor",
				new Performer() {
					public Object execute() throws Exception {
						Table table=(Table)getTarget();
						CustomerData cdata=(CustomerData)get("CustomerData");
						return table.get(cdata.getPolicyType());
					}
				}
			);
		Primitive multFormFactor =
			new Primitive("baseClassPremium","intermediatePremium",
				new Performer() {
					public Object execute() throws Exception {
						Premium basePremium=(Premium)getTarget();
						Double formFactor=(Double)get("formFactor");
						return basePremium.mult(formFactor);
					}
				}
			);
		Primitive getPCCFactor =
			new Primitive("PCCFactorTable","pccFactor",
				new Performer() {
					public Object execute() throws Exception {
						Table table=(Table)getTarget();
						CustomerData cdata=(CustomerData)get("CustomerData");
						return table.get(cdata.getConstructionType(),cdata.getProtectionClass());
					}
				}
			);
		Primitive multPCCFactor =
			new Primitive("intermediatePremium","keyPremium",
				new Performer() {
					public Object execute() throws Exception {
						Premium premium=(Premium)getTarget();
						Double pccFactor=(Double)get("pccFactor");
						return premium.mult(pccFactor);
					}
				}
			);
		Primitive getKeyFactor =
			new Primitive("KeyFactorTable","keyFactor",
				new Performer() {
					public Object execute() throws Exception {
						Table keyFactorTable=(Table)getTarget();
						CustomerData cdata=(CustomerData)get("CustomerData");
						return keyFactorTable.get(cdata.getCoverageType(),cdata.getCoverageAmount());
					}}
			);
		Primitive multKeyFactor =
			new Primitive("keyPremium","initialBasePremium",
				new Performer() {
					public Object execute() throws Exception {
						Premium keyPremium=(Premium)getTarget();
						Double keyFactor=(Double)get("keyFactor");
						return keyPremium.mult(keyFactor);
					}
				}
			);
		return getBaseValue.
				addStep(makeBasePremium).
				addStep(getFormFactor).
				addStep(multFormFactor).
				addStep(getPCCFactor).
				addStep(multPCCFactor).
				addStep(getKeyFactor).
				addStep(multKeyFactor);
	}
	
	public Activity getFinalBasePremiumWorkflow() {
		Primitive getOLFactor =
			new Primitive("Ordinance/LawFactorTable","olFactor",
				new Performer() {
					public Object execute() throws Exception {
						Table table=(Table)getTarget();
						CustomerData cdata=(CustomerData)get("CustomerData");
						return table.get(cdata.getIncreaseOrdinanceOrLawCoverage());
					}}
			);
		Primitive multOLFactor =
			new Primitive("initialBasePremium","finalBasePremium",
				new Performer() {
					public Object execute() throws Exception {
						Premium p=(Premium)getTarget();
						Double factor=(Double)get("olFactor");
						return p.mult(factor);
					}}
			);
		Conditional ifSuperiorConstruction =
			new Conditional(new TestCondition() {
				public boolean evaluate() {
					CustomerData cdata=(CustomerData)get("CustomerData");
					return cdata.isSuperiorConstruction().booleanValue();
				}},
				new Primitive("finalBasePremium","finalBasePremium",
					new Performer() {
						public Object execute() throws Exception {
							Premium p=(Premium)getTarget();
							Double factor=(Double)get("SuperiorConstructionFactor");
							return p.mult(factor);
						}}
				));
		return getOLFactor.addStep(multOLFactor).addStep(ifSuperiorConstruction);
	}
	
	public Activity getDebitAndCreditWorkflow() {
		Primitive getPremiumValue =
			new Primitive("finalBasePremium","premiumValue",
				new Performer(){
					public Object execute() throws Exception {
						Premium p=(Premium)getTarget();
						return p.getValue();
					}}
			);
		Primitive computeDebitOrCredit =
			new Primitive("endorsement","debitOrCredit",
				new Performer(){
					public Object execute() throws Exception {
						Endorsement endorsement=(Endorsement)getTarget();
						Double basePremium=(Double)get("premiumValue");
						return endorsement.computeDebitOrCreditFor(basePremium);
					}});
		Primitive addToPremium =
			new Primitive("finalBasePremium",
				new Performer(){
					public Object execute() throws Exception {
						Premium p=(Premium)getTarget();
						Double debitOrCredit=(Double)get("debitOrCredit");
						p.addDebitOrCredit(debitOrCredit);
						return null;
					}});
		Iterative computeDebitsAndCredits =
			new Iterative(
				"CustomerData",
				new Performer(){
					public Object execute() throws Exception {
						CustomerData cdata=(CustomerData)getTarget();
						return cdata.getEndorsements();
					}},
				"endorsement",
				computeDebitOrCredit.addStep(addToPremium));
		Primitive addDebitsAndCredits =
			new Primitive("finalBasePremium","finalPremium",
				new Performer(){
					public Object execute() throws Exception {
						Premium p=(Premium)getTarget();
						return p.adjustValue();
					}});
		return getPremiumValue.
				addStep(computeDebitsAndCredits).
				addStep(addDebitsAndCredits);
	}
	
	

	public void computePremium() {
		initializeTables();
		CustomerData cdata=new CustomerData();
		cdata.setPolicyType(CustomerData.HO_00_03);
		cdata.setConstructionType(CustomerData.CONSTRUCTION_TYPE_MASONRY);
		cdata.setCoverageAmount(new Double(16000));
		cdata.setCoverageType(CustomerData.COVERAGE_A);
		cdata.setProtectionClass(new Integer(9));
		cdata.setTerritory(new Integer(2));
		cdata.setIncreaseOrdinanceOrLawCoverage(new Double(10));
		cdata.setSuperiorConstruction(new Boolean(true));
		cdata.addEdorsement(new Deductible(1000));
		cdata.addEdorsement(new MPDiscount(10));
		cdata.addEdorsement(new PDDiscount(0.02));
		WorkflowContext wc=new WorkflowContext();
		wc.put("CustomerData",cdata);
		wc.put("BaseClassPremiumTable",baseClassPremium);
		wc.put("FormFactorTable",formFactor);
		wc.put("PCCFactorTable",pccFactor);
		wc.put("KeyFactorTable",keyFactor);
		wc.put("Ordinance/LawFactorTable",ordinanceOrLawFactor);
		wc.put("SuperiorConstructionFactor",new Double(0.8));
		wc.put("PremiumFactory",new Premium());
        
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINEST);
        Logger.getLogger("com.microworkflow.execution.WorkflowContext").addHandler(ch);
        Logger.getLogger("com.microworkflow.execution.WorkflowContext").setLevel(Level.FINEST);
        Logger.getLogger("com.microworkflow.execution.Scheduler").addHandler(ch);
        Logger.getLogger("com.microworkflow.execution.Scheduler").setLevel(Level.FINEST);
        Logger.getLogger("com.microworklfow.process.workflow").addHandler(ch);
        Logger.getLogger("com.microworklfow.process.workflow").setLevel(Level.FINEST);        
        
        
		Workflow premiumWorkflow=new Workflow();
		premiumWorkflow.setDefinition(getInitialBasePremiumWorkflow().
									  addStep(getFinalBasePremiumWorkflow()).
									  addStep(getDebitAndCreditWorkflow()));
		WorkflowContext result=premiumWorkflow.executeWith(wc);
		Premium p=(Premium)(wc.get("finalPremium"));
		System.out.println("Premium = " + p.getValue());
	}

}
