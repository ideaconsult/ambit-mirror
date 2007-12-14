/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit.database.exception;

import ambit.data.AmbitObject;
import ambit.data.experiment.Study;

/**
 * Exception when importing experimental data {@link ambit.data.experiment.Experiment} without 
 * defined {@link ambit.data.experiment.Study} conditions.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DbStudyConditionsNotDefined extends DbAmbitException {

	public DbStudyConditionsNotDefined(AmbitObject object) {
		super(object);
		// TODO Auto-generated constructor stub
	}

	public DbStudyConditionsNotDefined(AmbitObject object, String arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbStudyConditionsNotDefined(AmbitObject object, Throwable arg0) {
		super(object, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbStudyConditionsNotDefined(AmbitObject object, String arg0,
			Throwable arg1) {
		super(object, arg0, arg1);
	}
	public Study getStudy() {
		if (object instanceof Study)
			return (Study) object;
		else return null;
	}

}


