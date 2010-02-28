package ambit2.fastox.steps.step2;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.RemoteTaskPool;

/**
 * Browse & check structure
 * @author nina
 *
 */
public class Step2Resource extends FastoxStepResource {
	protected String compound = null;
	public enum structure_mode {
		d1 {
			public String getName() {
				return "1D";
			}
		},
		d2 {
			public String getName() {
				return "2D";
			}
		},
		d3 {
			public String getName() {
				return "3D";
			}
		};	
		public abstract String getName();
	};		
	public enum structure_browser {
		prev {
			public String getName() {
				return "Previous compound";
			}
			@Override
			public String getShortcut() {
				return "<";
			}
		},
		next {
			public String getName() {
				return "Next compound";
			}
			@Override
			public String getShortcut() {
				return ">";
			}
		};	
		public abstract String getName();
		public abstract String getShortcut();
	};		


	public Step2Resource() {
		super(2);
		helpResource = "step2.html";
	}
	@Override
	protected String getDefaultTab() {

		return "Verify structure";
	}

	public void renderFormContent(Writer writer, String key) throws IOException {
		renderCompounds(writer);
		super.renderFormContent(writer, key);
	}

	@Override
	public void renderResults(Writer writer, String key) throws IOException {

	}

	@Override
	protected Representation processMultipartForm(Representation entity,
			Variant variant) throws ResourceException {
		RemoteTaskPool pool = new RemoteTaskPool();
		try {
			RemoteTask task = new RemoteTask(wizard.getService(SERVICE.dataset),variant.getMediaType(),entity,Method.POST,null);
			
			pool.add(task);
			pool.run();
			if (Status.SUCCESS_OK.equals(task.getStatus())) {
				session.setDatasetURI(task.getResult().toString());
				return get(variant);	
			} else throw new ResourceException(task.getStatus(),task.getResult().toString());
			
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(x);
		} finally {
			pool.clear();
		}
	}

}

