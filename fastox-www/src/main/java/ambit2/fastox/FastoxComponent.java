package ambit2.fastox;

import org.restlet.Application;
import org.restlet.Context;

import ambit2.rest.RESTComponent;

/**
 * This is used as a servlet component instead of the core one, to be able to attach protocols 
 * @author nina
 *
 */
public class FastoxComponent extends RESTComponent {
		public FastoxComponent() {
			this(null);
		}
		public FastoxComponent(Context context,Application[] applications) {
			super(context,applications);
			
		
		}
		public FastoxComponent(Context context) {
			this(context,new Application[]{new FastoxApplication()});
		}
		
		

}
