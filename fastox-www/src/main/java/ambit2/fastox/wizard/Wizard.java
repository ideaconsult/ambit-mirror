package ambit2.fastox.wizard;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.WelcomeResource;
import ambit2.fastox.steps.step1.Step1;
import ambit2.fastox.steps.step2.Step2;
import ambit2.fastox.steps.step3.Step3;
import ambit2.fastox.steps.step4.Step4;
import ambit2.fastox.steps.step5.Step5;
import ambit2.fastox.steps.step6.Step6;

public class Wizard {
	public enum WizardMode {
		A {
			@Override
			public Wizard getWizard() {
				if (startFromStructure == null) {
					startFromStructure = new Wizard(
					 new WizardStep[] {
								new WizardStep(0,"Welcome","ToxPredict, an OpenTox demo application",WelcomeResource.class),
								new Step1(),
								new Step2(),
								new Step3(),
								new Step4(),
								new Step5(),
								new Step6()
								
							}
					 );
				}
				return startFromStructure;
			}
			@Override
			public String getResource() {
				return "A";
			}
			@Override
			public String getTitle() {
				return "Find chemical compounds";
			}
		},
		B {
			@Override
			public Wizard getWizard() {
				if (startFromEndpoint == null) {
					startFromEndpoint = new Wizard(
					 new WizardStep[] {
								new WizardStep(0,"Welcome","ToxPredict, an OpenTox demo application",WelcomeResource.class),
	
								new Step1(),
								new Step2(),
								new Step3(),
								new Step4(),
								new Step5(),
								new Step6()
								
							}
					 );
				}
				return startFromEndpoint;
			}
			@Override
			public String getResource() {
				return "B";
			}			
			@Override
			public String getTitle() {
				return "Find models";
			}
			
		};
		public abstract Wizard getWizard();
		public abstract String getResource();
		public abstract String getTitle();
		@Override
		public String toString() {
			return getResource();
		}
	}
	protected static Wizard startFromStructure = null;
	protected static Wizard startFromEndpoint = null;
	protected WizardStep[] steps;
	public static String dataset_service = "http://ideaconsult.net:8180/ambit2/dataset";
	public static String compound_service = "http://ideaconsult.net:8180/ambit2/compound";
	public static String feature_service = "http://ideaconsult.net:8180/ambit2/feature";
	public static String model_service = "http://ideaconsult.net:8180/ambit2/model";
	public static String ontology_service = "http://ideaconsult.net:8180/ontology";
	
	protected Wizard(WizardStep[] steps) {
		this.steps= steps;
		for (int i=0; i < steps.length;i++)
			steps[i].setIndex(i);
	}
	public int size() {
		return steps.length;
	}
	public static Wizard getInstance(WizardMode mode) {
		return mode.getWizard();
	}
	public WizardStep getStep(int index) throws ResourceException {
		if ((index <0) || (index > steps.length)) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Invalid step number %d",index));
		return steps[index];
	}
	public WizardStep nextStep(WizardStep step) throws ResourceException {
		if (step.getIndex()==0) return getStep(step.getIndex()+2);
		else return (step.getIndex()+1)>=steps.length?null:getStep(step.getIndex()+1);
	}
	public WizardStep prevStep(WizardStep step) throws ResourceException {
		return (step.getIndex()-1<0)?null:getStep(step.getIndex()-1);
	}
	public WizardStep firstStep(WizardStep step) throws ResourceException {
		return getStep(0);
	}	
	public WizardStep lastStep(WizardStep step) throws ResourceException {
		return getStep(steps.length-1);
	}	
}
