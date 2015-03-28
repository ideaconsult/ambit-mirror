package ambit2.core.io;

import java.util.ArrayList;
import java.util.UUID;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.ReliabilityParams;

public abstract class StringArrayHeader<CATEGORIES> {
	protected ArrayList<StringArrayHeader> header;
	public ArrayList<StringArrayHeader> getHeader() {
		return header;
	}
	public void setHeader(ArrayList<StringArrayHeader> header) {
		this.header = header;
	}
	protected String[] lines;
	enum _lines {
		endpointcategory,
		technology,
		endpoint,
		medium,
		result,
		units
	}
	protected String prefix = "CSV6-";
	public StringArrayHeader(String prefix,int nlines) {
		lines =  new String[nlines==0?1:nlines];
		this.prefix = prefix;
	}
	public StringArrayHeader(String prefix,int nlines,String value) {
		this(prefix,nlines);
		lines[_lines.endpointcategory.ordinal()] = value;
	}
	public void setValue(int nline,int column, String value) {
		lines[nline] = value;
	}
	public String getValue(int nline) {
		return lines[nline];
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		String dot = "";
		for (String line : lines) {
			b.append(dot);
			b.append(line);
			dot = ".";
		}	
		return b.toString();
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	public abstract void assign(SubstanceRecord record, Object value);
		
	
	protected ProtocolApplication<Protocol, IParams, String, IParams, String> getExperiment(CATEGORIES categories,SubstanceRecord record,Protocol protocol,
				ReliabilityParams reliability) {
		String experimentUUID = prefix+UUID.nameUUIDFromBytes(
				(record.getSubstanceUUID() + lines[_lines.technology.ordinal()]+lines[_lines.endpoint.ordinal()]).getBytes()
				);
		
		if (record.getMeasurements()!=null)
			for (ProtocolApplication<Protocol, IParams, String, IParams, String> experiment : record.getMeasurements()) {
				if (experimentUUID.equals(experiment.getDocumentUUID())) return experiment;
			}
		ProtocolApplication<Protocol, IParams, String, IParams, String> experiment = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		experiment.setReliability(reliability);
		experiment.setParameters(new Params());
		experiment.getParameters().put("testmat_form", null);
		experiment.getParameters().put("Type of method",null);
		setCitation(experiment);
		experiment.setDocumentUUID(experimentUUID);
		return experiment;
	}
	protected void setCitation(ProtocolApplication<Protocol, IParams, String, IParams, String> experiment) {
		experiment.setReferenceYear("-");
		experiment.setReference("Unknown");
	}
	protected String getConditionValue(String cell) {
		return cell;
	}
	
}