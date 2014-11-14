package ambit2.db.reporters;

import java.io.StringWriter;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.io.CMLWriter;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveStructure;

/**
 * Writes query results as CML .  TODO The output is not correct for multiple molecules!!!!
<pre>
<?xml version="1.0" encoding="ISO-8859-1"?>
<list dictRef="cdk:moleculeSet" xmlns="http://www.xml-cml.org/schema">
  <molecule id="m1">
    <atomArray>
      <atom id="1" elementType="C" formalCharge="0" hydrogenCount="3">
        <scalar title="PropSecond_ID1" dataType="xsd:string">1</scalar>
        <scalar title="PropFirst_ID1" dataType="xsd:string">1</scalar>
      </atom>
      <atom id="2" elementType="C" formalCharge="0" hydrogenCount="3">
        <scalar title="PropFirst_ID2" dataType="xsd:string">2</scalar>
        <scalar title="PropSecond_ID2" dataType="xsd:string">2</scalar>
      </atom>
    </atomArray>
    <bondArray>
      <bond id="b1" atomRefs2="2 1" order="S"/>
    </bondArray>
  </molecule>
  <molecule id="m1">
    <atomArray>
      <atom id="1" elementType="C" formalCharge="0" hydrogenCount="3"/>
      <atom id="2" elementType="C" formalCharge="0" hydrogenCount="3"/>
    </atomArray>
    <bondArray>
      <bond id="b1" atomRefs2="2 1" order="S"/>
    </bondArray>
  </molecule>
</list>
</pre>
 * @author nina
 *
 * @param <Q>
 */
public class CMLReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureReporter< Q, Writer> {
	protected MoleculeReader reader = new MoleculeReader();

	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	public CMLReporter() {
		getProcessors().clear();
		RetrieveStructure r = new RetrieveStructure();
		r.setPage(0);
		r.setPageSize(1);
		getProcessors().add(new ProcessorStructureRetrieval(r));		
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
	}
	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			StringWriter w = new StringWriter();
			if (STRUC_TYPE.NANO.name().equals(item.getFormat())) { //this is CML already
				output.write(item.getContent());
				output.write("\n");
			} else {
				CMLWriter cmlwriter = new CMLWriter(w); 
				cmlwriter.write(reader.process(item));
				cmlwriter.close();
				String b = w.toString();
				int  p1 = b.indexOf("<?xml");
				int p2 = b.indexOf("?>");
				if ((p1>=0) && (p2>p1))
					output.write(b.substring(p2+2));
				else output.write(b);
			}
			
			
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return null;
	}

	public void open() throws DbAmbitException {
		
	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			if (getLicenseURI()!=null) {
				//Not specified by CML, but XML parsers should be forgiving :)
				output.write(String.format("\n<license>%s</license>\n",getLicenseURI()));
			}
			output.write("\n</list>");
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}		
	}

	@Override
	public void header(Writer output, Q query) {
		try {
			output.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
			//output.write("<list dictRef=\"cdk:moleculeSet\" xmlns=\"http://www.xml-cml.org/schema\">");
			output.write("<list xmlns=\"http://www.xml-cml.org/schema\">\n");
		} catch (Exception x) {}


	};
	@Override
	public String getFileExtension() {
		return "cml";
	}
}