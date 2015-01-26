package ambit2.db.search;

import net.idea.modbcum.i.IQueryCondition;



public class SetCondition  implements IQueryCondition {
		
		/**
     * 
     */
    private static final long serialVersionUID = -3634401756080837607L;
		public enum conditions {
			in {
				
			},
			not_in {
				@Override
				public String getSQL() {
					return "not in";
				}
			};
			public String getSQL() {
				return toString();
			}
			
		}
		protected conditions condition = conditions.in;
		public SetCondition(conditions c) {
			condition = c;
		}
		public String getName() {
			return getSQL();
		}

		public String getSQL() {
			return condition.getSQL();
		}
		@Override
		public String toString() {
		
		return  getSQL();
		}
	}