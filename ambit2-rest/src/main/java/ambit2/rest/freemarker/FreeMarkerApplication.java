package ambit2.rest.freemarker;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.ext.freemarker.ContextTemplateLoader;

import ambit2.rest.TaskApplication;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;

public class FreeMarkerApplication<USERID> extends TaskApplication<USERID> implements IFreeMarkerApplication<Configuration> {
	   private Configuration configuration;

		protected String gaCode = null;

		
		public String getGACode() {
			return gaCode;
		}
		
	   protected String profile = "default";

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}
		protected String versionShort = "";
		@Override
		public String getVersionShort() {
			return versionShort;
		}

		public void setVersionShort(String versionShort) {
			this.versionShort = versionShort;
		}
		protected String versionLong = "";

	    @Override
		public String getVersionLong() {
			return versionLong;
		}

		public void setVersionLong(String versionLong) {
			this.versionLong = versionLong;
		}
		@Override
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
		/**
		 * msec
		 * @return
		 */
		public String getAjaxTimeout() {
			return "10000";
		}
		public boolean isSimilarityOrder() {
			return true;
		}
		
		public boolean isEnableEmailVerification() {
			return true;
		}
		protected boolean changeLineSeparators = false; 
		@Override
		public boolean isChangeLineSeparators() {
			return changeLineSeparators;
		}
}
