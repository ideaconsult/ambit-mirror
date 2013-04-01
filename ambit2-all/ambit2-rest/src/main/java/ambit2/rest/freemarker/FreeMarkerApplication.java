package ambit2.rest.freemarker;

import org.restlet.ext.freemarker.ContextTemplateLoader;

import ambit2.rest.TaskApplication;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

public class FreeMarkerApplication<USERID> extends TaskApplication<USERID> {
	   private Configuration configuration;
		protected String versionShort = "";
		public String getVersionShort() {
			return versionShort;
		}

		public void setVersionShort(String versionShort) {
			this.versionShort = versionShort;
		}
		protected String versionLong = "";

	    
		public String getVersionLong() {
			return versionLong;
		}

		public void setVersionLong(String versionLong) {
			this.versionLong = versionLong;
		}

		public Configuration getConfiguration() {
			return configuration;
		}

		public void setConfiguration(Configuration configuration) {
			this.configuration = configuration;
		}
		protected void initFreeMarkerConfiguration() {
			configuration = new Configuration();
			
	        ContextTemplateLoader templatesLoader = new ContextTemplateLoader(getContext(),"war:///WEB-INF/templates/");
	        TemplateLoader[] loaders = new TemplateLoader[] { templatesLoader};
	        MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
	        configuration.setTemplateLoader(mtl);
	        configuration.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER); 
		}
}
