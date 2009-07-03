package ambit2.dbui;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;

import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryStructureByQualityPairLabel;
import ambit2.db.search.structure.QueryStructureByQualityPairLabel.CONSENSUS_LABELS;

public class QueryStructureByConsensusEditor extends 
					QueryEditor<String, String, StringCondition,IStructureRecord, QueryStructureByQualityPairLabel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3250959354184969096L;

	@Override
	protected JComponent createConditionComponent() {
		return null;
	}

	@Override
	protected JComponent createFieldnameComponent() {
		JComboBox box = BasicComponentFactory.createComboBox(
                new SelectionInList<String>(
                		new String[] {
                				CONSENSUS_LABELS.Consensus.toString(),
                				CONSENSUS_LABELS.Majority.toString(),
                				CONSENSUS_LABELS.Ambiguous.toString(),
                				CONSENSUS_LABELS.Unconfirmed.toString(),
                				CONSENSUS_LABELS.Unknown.toString()
                		},
                		presentationModel.getModel("fieldname")));
		AutoCompleteDecorator.decorate(box);
		return box;
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
