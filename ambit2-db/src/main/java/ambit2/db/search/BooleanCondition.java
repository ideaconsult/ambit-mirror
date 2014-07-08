/* BooleanCondition.java
 * Author: nina
 * Date: Apr 10, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.search;

import java.util.Hashtable;
import java.util.Map;

import net.idea.modbcum.i.IQueryCondition;

public class BooleanCondition implements IQueryCondition {
	private static Map<String,BooleanCondition> instances = null;
	
	public enum BOOLEAN_CONDITION {
		B_YES {
			@Override
			public String toString() {
				return "Yes";
			}
			@Override
			public String getSQL() {
				return "true";
			}
		},
		B_NO {
			@Override
			public String toString() {
				return "No";
			}
			@Override
			public String getSQL() {
				return "false";
			}
		};

		public String getName() {
			return toString();
		}
		public abstract String getSQL();
		public String getParam(String value) {
			return value;
		}
	}

	protected BOOLEAN_CONDITION value;
	
	protected BooleanCondition(BOOLEAN_CONDITION value) {
		this.value = value;
	}	
	public String getParam(String value) {
		return this.value.getParam(value);
	}
	public String getName() {
		return value.toString();
	}

	public String getSQL() {
		return value.getSQL();
	}

	public static Map<String,BooleanCondition> getAlowedValues() {
		if (instances == null) {
			Map<String,BooleanCondition> list = new Hashtable<String,BooleanCondition>();
			for (BOOLEAN_CONDITION c : BOOLEAN_CONDITION.values())
				list.put(c.getName(),new BooleanCondition(c));
			instances = list;
		}
		return instances;
	}

	public static BooleanCondition getInstance(String condition) {
		try {
			BooleanCondition c = getAlowedValues().get(condition);
			if (c != null) return c;
		} catch (Exception x) {
			

		}
		return getAlowedValues().get(BOOLEAN_CONDITION.B_YES.toString());
	}
	@Override
	public String toString() {
		return getSQL();
	}

}
