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

package ambit.data.qmrf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import ambit.data.AmbitObject;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;
import ambit.ui.EditorPanel;
import ambit.ui.UITools;

import com.l2fprod.common.swing.JButtonBar;

public class QMRFPanel extends JPanel implements IAmbitObjectListener {
	protected QMRFObject qmrf = null;
	protected JButtonBar toolbar;
	protected ArrayList<ShowChapterAction> actions;
	protected EditorPanel editorPanel;
	protected Hashtable<String,String> chapterIcons;
	
	
	public QMRFPanel(QMRFObject qmrf) {
		super(new BorderLayout());
		toolbar = new JButtonBar(JButtonBar.VERTICAL);
		editorPanel = new EditorPanel();
		chapterIcons = new Hashtable<String,String>();
		chapterIcons.put("1","ambit/ui/images/application_form.png");
		chapterIcons.put("2","ambit/ui/images/user_suit.png");
		chapterIcons.put("3","ambit/ui/images/experiment_16.png");
		chapterIcons.put("4","ambit/ui/images/chart_curve.png");
		chapterIcons.put("5","ambit/ui/images/chart_pie.png");
		chapterIcons.put("6","ambit/ui/images/benzene_16.jpg");
		chapterIcons.put("7","ambit/ui/images/benzene_16.jpg");
		chapterIcons.put("8","ambit/ui/images/book_open.png");
		chapterIcons.put("9","ambit/ui/images/attachment.png");
		actions = new ArrayList<ShowChapterAction>();
		setQmrf(qmrf);
		add(toolbar,BorderLayout.WEST);
		add(editorPanel,BorderLayout.CENTER);
		setPreferredSize(new Dimension(400,10*50));
	}
	public QMRFObject getQmrf() {
		return qmrf;
	}
	public void setQmrf(QMRFObject qmrf) {
        if (this.qmrf != null) this.qmrf.removeAmbitObjectListener(this);
		this.qmrf = qmrf;
        this.qmrf.addAmbitObjectListener(this);
		
		Iterator<QMRFChapter> chapters = qmrf.chaptersIterator();
		toolbar.removeAll();
		toolbar.validate();
        
		actions.clear();

		ShowChapterAction action = new ShowChapterAction(qmrf,editorPanel,UITools.createImageIcon("ambit/ui/images/qmrf.png"));
		ButtonGroup group = new ButtonGroup();
		JToggleButton b = new JToggleButton(action);
		group.add(b);
		toolbar.add(b);
		actions.add(action);
        
		
		while (chapters.hasNext()) {
			QMRFChapter chapter = chapters.next();
			//

			String icon = chapterIcons.get(chapter.getChapter());
			if (icon == null) icon = "ambit/ui/images/draw_16.png";
			action = new ShowChapterAction(chapter,editorPanel,UITools.createImageIcon(icon));
			b = new JToggleButton(action);
			b.setToolTipText(chapter.getTitle());
            actions.add(action);
			group.add(b);
			toolbar.add(b);
		}
		/*
        Iterator<Catalog> e = qmrf.getCatalogs().values().iterator();
		while (e.hasNext()) {
            
			action = new ShowChapterAction(e.next(),editorPanel,UITools.createImageIcon("ambit/ui/images/database.png"));
			b = new JToggleButton(action);
			group.add(b);
			toolbar.add(b);
			actions.add(action);		
		}
		*/
        if (actions.size()> 0)
            actions.get(0).actionPerformed(null);
        
        
		
	}
    public void ambitObjectChanged(AmbitObjectChanged event) {
        if (event.getObject() instanceof QMRFObject) {
            //System.out.println(event.toString());
            //validate();
            if (!((QMRFObject) event.getObject()).isModified())
                setQmrf((QMRFObject) event.getObject());
        }    

        
    }

}

class ShowChapterAction extends AbstractAction {
	protected AmbitObject object;
	protected EditorPanel editorPanel;
	public ShowChapterAction(AmbitObject object,EditorPanel editorPanel, Icon icon) {
		super(object.toString(),icon);
		setObject(object);
		setEditorPanel(editorPanel);
	}

	public void actionPerformed(ActionEvent e) {

		editorPanel.setEditor(object.editor(object.isEditable()));
	}

	public EditorPanel getEditorPanel() {
		return editorPanel;
	}
	public void setEditorPanel(EditorPanel editorPanel) {
		this.editorPanel = editorPanel;
	}
	public AmbitObject getObject() {
		return object;
	}
	public void setObject(AmbitObject object) {
		this.object = object;
	}
}
