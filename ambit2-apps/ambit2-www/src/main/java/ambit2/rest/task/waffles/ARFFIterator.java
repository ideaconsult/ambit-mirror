package ambit2.rest.task.waffles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

import org.restlet.data.Reference;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.OpenTox;
import ambit2.rest.rdf.RDFPropertyIterator;

/**
 * Merges input and results ARFF files, as produced by waffles
 * @author nina
 *
 */
public class ARFFIterator extends AbstractBatchProcessor<File[], IStructureRecord>  {
	protected Reference baseReference;
	protected IStructureRecord record = new StructureRecord();
	protected Property[] properties;
	//arff
	protected BufferedReader input;
	protected BufferedReader results;
	protected ArffReader arffInput;
	protected ArffReader arffResults;
	protected Instances dataInput;
	protected Instances dataResults;
	protected Instance inputInstance;
	protected Instance resultInstance;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5391300963330823605L;

	public ARFFIterator(Reference baseReference) {
		super();
		this.baseReference = baseReference;
	}
	
	@Override
	public Iterator<IStructureRecord> getIterator(File[] target)	throws AmbitException {
		try {
			input = new BufferedReader(new FileReader(target[0]));
			results = new BufferedReader(new FileReader(target[1]));
			arffInput = new ArffReader(input, 1);
			arffResults = new ArffReader(results, 1);
			dataInput = arffInput.getStructure();
			dataResults = arffResults.getStructure();
			
			properties = new Property[dataResults.numAttributes()];
			for (int i=0; i < dataResults.numAttributes();i++) {
				Attribute attribute = dataResults.attribute(i);
				RDFPropertyIterator reader = null;
				Property p = null;
				try {
					reader = new RDFPropertyIterator(new Reference(attribute.name()));
					reader.setBaseReference(baseReference);
					while (reader.hasNext()) {
						p = reader.next();
					}
				} catch (Exception x) {
					p = null;
					//p.setId((Integer)OpenTox.URI.feature.getId(attribute.name(), baseReference));
				} finally {
					try {reader.close(); } catch (Exception x) {}
				}
				properties[i] = p;
			}
			
			return new Iterator<IStructureRecord>() {
				public boolean hasNext() {
					try {
						inputInstance = arffInput.readInstance(dataInput);
						resultInstance = arffResults.readInstance(dataResults);
						return (inputInstance != null) && (resultInstance != null);
					} catch (Exception x) {
						return false;
					}
				}
				public IStructureRecord next() {
					record.clear();
					Object[] ids = OpenTox.URI.conformer.getIds(inputInstance.toString(0), baseReference);
					if ((ids != null) && (ids[0]!=null) && (ids[1]!=null)) {
						record.setIdchemical((Integer) ids[0]);
						record.setIdstructure((Integer) ids[1]);
					} else {
						Object id = OpenTox.URI.compound.getId(inputInstance.toString(0), baseReference);
						if (id!=null) {
							record.setIdchemical((Integer) id);
							record.setIdstructure(-1);
						} else {
							//retrieveStructure(uris[index]);
						}
					}
					for (int i=0; i < properties.length;i++) if (properties[i]!=null) {
						if (dataResults.attribute(i).isNumeric())
							record.setRecordProperty(properties[i], resultInstance.value(i));
						else
							record.setRecordProperty(properties[i], resultInstance.toString(i));
					}
					return record;
				}
				public void remove() {
				}
			};			
		} catch (FileNotFoundException x) {
			throw new AmbitException(x);
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public void close() throws Exception {
		try {input.close();} catch (Exception x) {}
		try {results.close();} catch (Exception x) {}
		super.close();
	}
}


