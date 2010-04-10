package ambit2.plugin.pbt;

import ambit2.base.data.SelectionBean;
import ambit2.workflow.UserInteraction;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.Workflow;

/**
 * Workflow to create a new PBT workbook
 * @author nina
 *
 */
public class PBTCreator extends Workflow {
	public PBTCreator() {
		Sequence seq = new Sequence();
		seq.setName("New");
		
		TestCondition test = new TestCondition() {
			@Override
			public boolean evaluate() {
				Object o = getContext().get(PBTWorkBook.PBT_WORKBOOK);
				if (o==null) return false;
				if (o instanceof PBTWorkBook) {
					return ((PBTWorkBook)o).isModified();
				}
				return false;
			}
		};
		Primitive<PBTWorkBook, PBTWorkBook> creator = new Primitive<PBTWorkBook, PBTWorkBook>(
				PBTWorkBook.PBT_WORKBOOK,PBTWorkBook.PBT_WORKBOOK,
				new Performer<PBTWorkBook, PBTWorkBook>() {
					@Override
					public PBTWorkBook execute() throws Exception {
						PBTWorkBook book = new PBTWorkBook();
						book.setRecord(null);
						book.setModified(false);
						return book;
						/*
						Object o = getContext().get(PBTWorkBook.PBT_WORKBOOK);
						if ((o==null)|| !(o instanceof PBTWorkBook))
							return new PBTWorkBook();
						else {
							((PBTWorkBook)o).clear();
							return ((PBTWorkBook)o);
						}
						*/
					}
				}
				);
		creator.setName("Create new PBT workbook");
		
		final String yes = "Yes, ignore changes";
		final String no = "No, cancel the workflow";
        UserInteraction<SelectionBean> confirm = new UserInteraction<SelectionBean>(
        		new SelectionBean<String>(new String[]{yes,no},"There are unsaved changes in the current PBT workbook"),
        		"SELECTION","Ignore changes?");
        confirm.setName("Confirm");
        
		TestCondition verify = new TestCondition() {
			@Override
			public boolean evaluate() {
				Object o = getContext().get("SELECTION");
				if (o==null) return false;
				if (o instanceof SelectionBean) {
					return ((SelectionBean)o).getSelected().equals(yes);
				}
				return false;
			}
		};        
		Conditional c1 = new Conditional(verify,creator,null);
		c1.setName("Confirm");
		Sequence s = new Sequence();
		s.addStep(confirm);
		s.addStep(c1);
		s.setName("s1");
		
		Conditional c = new Conditional(test,s,creator);
		c.setName("Has PBT workbook been modified?");
		seq.addStep(c);
		setDefinition(seq);
	}
	@Override
	public String toString() {

		return "New PBT workbook";
	}
}
