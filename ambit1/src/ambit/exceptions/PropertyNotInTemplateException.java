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

package ambit.exceptions;

import ambit.data.experiment.StudyTemplate;

public class PropertyNotInTemplateException extends AmbitException {
	public static final String MESSAGE="Property not in template!"; 
	Object key;
	StudyTemplate template;
	public PropertyNotInTemplateException() {
		this(MESSAGE,null,null,null);
	}
	public PropertyNotInTemplateException(Object key, StudyTemplate template) {
		this(MESSAGE,null,key,template);
	}
	public PropertyNotInTemplateException(String arg0) {
		this(arg0,null,null,null);
	}

	public PropertyNotInTemplateException(Throwable arg0) {
		this(null,arg0,null,null);
	}

	public PropertyNotInTemplateException(String arg0, Throwable arg1, Object key, StudyTemplate template) {
		super(arg0, arg1);
		this.key = key;
		this.template = template;
	}
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public StudyTemplate getTemplate() {
		return template;
	}
	public void setTemplate(StudyTemplate template) {
		this.template = template;
	}

}


