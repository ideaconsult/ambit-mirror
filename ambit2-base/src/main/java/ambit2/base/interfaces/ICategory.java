/* ICategory.java
 * Author: nina
 * Date: Apr 1, 2013
 * Revision: 2.4.11-SNAPSHOT 
 * 
 * Copyright (C) 2005-2013  Ideaconsult Ltd.
 * 
 * Contact: jeliazkova.nina@gmail.com
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
package ambit2.base.interfaces;
/**
 * 
 * Interface to set the semantic interpretation of a property value
 *
 */
public interface ICategory {
	/**
	 * Assigns whether the category denotes toxicity or not.
	 * @author nina
	 *
	 */
	public enum CategoryType  {
		ToxicCategory {
			@Override
			public CategoryType getNegative() {
				return NontoxicCategory;
			}
		},
		NontoxicCategory {
			@Override
			public CategoryType getNegative() {
				return ToxicCategory;
			}
		},
		InconclusiveCategory;
		public CategoryType getNegative() {return InconclusiveCategory; };
	};
	String getName();
	void setName(String name);
	/**
	 * Return one of {@link CategoryType}
	 * @return
	 */
	CategoryType getCategoryType();
	void setCategoryType(CategoryType categoryType);
	CategoryType getNegativeCategoryType();
}
