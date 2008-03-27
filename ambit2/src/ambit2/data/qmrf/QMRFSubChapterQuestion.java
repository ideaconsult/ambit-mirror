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

package ambit2.data.qmrf;

import ambit2.ui.editors.IAmbitEditor;

public class QMRFSubChapterQuestion extends QMRFSubChapterText {
	protected String question;
	protected String[] options;
	public QMRFSubChapterQuestion(String question, String[] options) {
		this(question,options,"chapter");
	}

	public QMRFSubChapterQuestion(String question,String[] options,String elementID) {
		this(question,options,elementID,"chapter","title","help");
	}

	public QMRFSubChapterQuestion(String question,String[] options,
			String elementID, String chapter,
			String title, String help) {
		super(elementID, chapter, title, help);
		this.question = question;
		this.options = options;
		setMultiline(false);
	}
	@Override
	public IAmbitEditor editor(boolean editable) {
		return new QMRFSubChapterQuestionEditor(this, editable);
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	
}


