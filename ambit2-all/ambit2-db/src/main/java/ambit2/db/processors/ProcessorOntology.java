package ambit2.db.processors;

import java.sql.Connection;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;
import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.db.UpdateExecutor;
import ambit2.db.update.dictionary.CreateDictionary;
import ambit2.db.update.dictionary.DeleteDictionary;
import ambit2.db.update.dictionary.TemplateAddProperty;
import ambit2.db.update.dictionary.TemplateDeleteProperty;

public class ProcessorOntology extends AbstractDBProcessor<Object, Dictionary> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3423393486307151753L;
	public enum SIDE {
		LEFT {
			@Override
			public String display() {
				return ">";
			}
		},
		RIGHT {
			@Override
			public String display() {
				return "<";
			}
		};
		public abstract String display();
		};
	protected Dictionary[] template;
	public enum OP {MOVE,DELETE,CREATE,SELECT};
	protected UpdateExecutor executor;
	protected OP operation = OP.MOVE;
	protected SIDE currentSide = SIDE.LEFT;
	
	public SIDE getCurrentSide() {
		return currentSide;
	}
	public void setCurrentSide(SIDE currentSide) {
		this.currentSide = currentSide;
	}
	public SIDE getOtherSide() {
		return (currentSide==SIDE.LEFT)?SIDE.RIGHT:SIDE.LEFT;
	}	
	
	public OP getOperation() {
		return operation;
	}
	public void setOperation(OP operation) {
		this.operation = operation;
	}
	public ProcessorOntology() {
		executor = new UpdateExecutor();
		template = new Dictionary[SIDE.values().length];
	}
	
	public Dictionary setTemplate(Dictionary dictionary,SIDE side) throws AmbitException {
		template[side.ordinal()] = dictionary;
		return dictionary;
	}
	protected Dictionary moveProperty(Property property,SIDE source,SIDE destination) throws AmbitException {
		TemplateDeleteProperty delete = new TemplateDeleteProperty();
		TemplateAddProperty add = new TemplateAddProperty();
		delete.setGroup(getTemplate(source));
		delete.setObject(property);
		add.setGroup(getTemplate(destination));
		add.setObject(property);
		executor.process(delete);
		executor.process(add);
		return add.getGroup();
		
	}
	protected Dictionary getTemplate(SIDE side) {
		return template[side.ordinal()];
	}
	protected Dictionary createProperty(Property property,SIDE side) throws AmbitException {
		TemplateAddProperty add = new TemplateAddProperty();
		add.setGroup(getTemplate(side));
		add.setObject(property);
		executor.process(add);
		return getTemplate(side);
	}	
	protected Dictionary deleteProperty(Property property,SIDE side) throws AmbitException {
		TemplateDeleteProperty delete = new TemplateDeleteProperty();
		delete.setGroup(getTemplate(side));
		delete.setObject(property);
		executor.process(delete);
		return getTemplate(side);
	}	
	protected Dictionary moveTemplate(Dictionary d,SIDE source,SIDE destination) throws AmbitException {
		DeleteDictionary delete = new DeleteDictionary();
		delete.setObject(d); //new Dictionary(d.getTemplate(),getTemplate(source).getTemplate()));
		CreateDictionary add = new CreateDictionary();
		Dictionary newEntry = new Dictionary();
		newEntry.setTemplate(d.getTemplate());
		newEntry.setParentTemplate(getTemplate(destination).getTemplate());
		add.setObject(newEntry);
		executor.process(delete);
		executor.process(add);
		return newEntry;
	}	
	protected Dictionary createTemplate(Dictionary d,SIDE side) throws AmbitException {
		CreateDictionary add = new CreateDictionary();
		Dictionary newEntry = new Dictionary();
		newEntry.setTemplate(d.getTemplate());
		newEntry.setParentTemplate(getTemplate(side).getTemplate());
		add.setObject(newEntry);
		executor.process(add);
		return newEntry;
	}		
	public Dictionary process(Object target) throws AmbitException {
		Connection c = getConnection();
		try {
			c.setAutoCommit(true);			
			executor.setConnection(getConnection());
			executor.open();
			switch (getOperation()) {
			case MOVE: {
				if (target instanceof Dictionary)	return moveTemplate((Dictionary)target,getCurrentSide(),getOtherSide());
				else if (target instanceof Property)	return moveProperty((Property)target,getCurrentSide(),getOtherSide());
			}
			case CREATE: {
				if (target instanceof Dictionary)	return createTemplate((Dictionary)target,getCurrentSide());		
				else if (target instanceof Property)	return createProperty((Property)target,getCurrentSide());
			}			
			case DELETE: {
				if (target instanceof Dictionary) ;
				else
				if (target instanceof Property)	return deleteProperty((Property)target,getCurrentSide());				
			}			
			case SELECT: {
				if (target instanceof Dictionary) return setTemplate((Dictionary)target,getCurrentSide());				
			}		
			default:
			}
			///
			//c.commit();
		} catch (Exception x) {
			//try {c.rollback();} catch (Exception e) {}
			throw new AmbitException(x);
		} finally {
//			setConnection(null);
			//try {executor.close();} catch (Exception e) {}
			//dont' want to close connection!
		}
		return null;
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}
