package ambit2.fastox.wizard;

import java.io.InputStream;
import java.util.Properties;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.WelcomeResource;
import ambit2.fastox.steps.step1.Step1;
import ambit2.fastox.steps.step2.Step2;
import ambit2.fastox.steps.step3.Step3;
import ambit2.fastox.steps.step5.Step5;
import ambit2.fastox.steps.step6.Step6;

public class Wizard {
	protected Properties properties;
	public enum SERVICE {
		ontology {
			@Override
			public String getDefaultValue() {
				return "http://apps.ideaconsult.net:8080/ontology";
			}
		},
		dataset {
			@Override
			public String getDefaultValue() {
				return "http://apps.ideaconsult.net:8080/ambit2/dataset";
			}
		},
		compound {
			@Override
			public String getDefaultValue() {
				return "http://apps.ideaconsult.net:8080/ambit2/compound";
			}
		},	
	
		model {
			@Override
			public String getDefaultValue() {
				return "http://apps.ideaconsult.net:8080/ambit2/model";
			}			
		},
		algorithm {
			@Override
			public String getDefaultValue() {
				return "http://apps.ideaconsult.net:8080/ambit2/algorithm";
			}					
		},
		feature {
			@Override
			public String getDefaultValue() {
				return "http://apps.ideaconsult.net:8080/ambit2/feature";
			}					
		},
		application {
			@Override
			public String getDefaultValue() {
				return "http://apps.ideaconsult.net:8080/ambit2";
			}					
		},		
		lookup {
			@Override
			public String getDefaultValue() {
				return String.format("%s/query/lookup",SERVICE.application);
			}					
		};		

		public String getKey() {
			return String.format("toxpredict.service.%s",toString());
		}
		public Reference getValue(Properties properties) {
			try {
				if (properties == null) return null;
				Object o = properties.get(getKey());
				return o==null?null:new Reference(o.toString());
			} catch (Exception x) {
				return new Reference(getDefaultValue());
			}
		}
		public abstract String getDefaultValue();
	}
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
		/*
		task {
			@Override
			public String getResource() {
				return "task";
			}
			@Override
			public String getTitle() {
				return "Jobs";
			}
			@Override
			public Wizard getWizard() {
				if (taskWizard == null) {
					taskWizard = new Wizard(
					 new WizardStep[] {
								new WizardStep(0,"Welcome","ToxPredict, an OpenTox demo application",WelcomeResource.class),
								new TaskStep(),
								
							}
					 );
				}
				return taskWizard;
			}

		};
		*/
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
	protected static Wizard taskWizard = null;
	protected WizardStep[] steps;

	
	protected Wizard(WizardStep[] steps) {
		super();
		properties = initProperties();
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
	protected Properties initProperties() {
		Properties p = new Properties();
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/fastox/config/config.prop");
		try {
			p.load(in);
			return p;
		} catch (Exception x) {
			return null;
		} finally {
			try {in.close();} catch (Exception x) {}
		}
		
	}
	public Reference getService(SERVICE service) {
		return service.getValue(properties);
	}
}
