package ambit2.rest;

import org.restlet.Application;
import org.restlet.Context;

/**
 * This is used as a servlet component instead of the core one, to be able to attach protocols 
 * @author nina
 *
 */
public class AmbitComponent extends RESTComponent {


		public AmbitComponent() {
			this(null);
		}
		public AmbitComponent(Context context,Application[] applications) {
			super(context,applications);
			
		
		}
		public AmbitComponent(Context context,boolean standalone) {
			this(context,new Application[]{new AmbitApplication(true)});
		}
		public AmbitComponent(Context context) {
			this(context,new Application[]{new AmbitApplication()});
		}
		
		

}
