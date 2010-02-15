package ambit2.fastox.steps.step2;

import java.io.IOException;
import java.io.Writer;

import ambit2.fastox.steps.FastoxStepResource;

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
	}
	@Override
	protected String getDefaultTab() {

		return "Verify structure";
	}

	public void renderFormContent(Writer writer, String key) throws IOException {
		writer.write("<h5>Under development: This page will allow to select/deselect compounds, if multiple available and also allow editing the structures. In this version all found structures will be submitted to the calculations</h5>");
		renderCompounds(writer);
		super.renderFormContent(writer, key);
	}

	@Override
	public void renderResults(Writer writer, String key) throws IOException {

	}


}
