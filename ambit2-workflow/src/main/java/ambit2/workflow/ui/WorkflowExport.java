package ambit2.workflow.ui;

import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.microworkflow.process.Activity;
import com.microworkflow.process.AndJoin;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Fork;
import com.microworkflow.process.Iterative;
import com.microworkflow.process.JoinActivity;
import com.microworkflow.process.NullActivity;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.While;
import com.microworkflow.process.Workflow;
import com.microworkflow.ui.WorkflowTools;

/**
 * An attempt to export workflow as XPDL file
 * http://www.bpm-research.com/forum/index.php?showtopic=677
 * http://www.workflowpatterns.com/evaluations/standard/xpdl.php
 * @author nina
 *
 */
public class WorkflowExport extends WorkflowConvertor<Document,Element> {
	protected static String ns_xpdl="http://www.wfmc.org/2002/XPDL1.0";
	protected static String xpdl_id = "Id";
	protected static String xpdl_name = "Name";
	protected static String xpdl_description = "Description";	
	protected static String xpdl_type = "Type";	
	protected static String xpdl_activity = "Activity";		
	protected static String xpdl_activities = "Activities";	
	protected static String xpdl_transitions = "Transitions";	
	protected static String xpdl_transition = "Transition";	
	protected static String xpdl_transitionRestrictions = "TransitionRestrictions";
	protected static String xpdl_transitionRestriction = "TransitionRestriction";
	protected static String xpdl_transitionRefs = "TransitionRefs";	
	protected static String xpdl_transitionRef = "TransitionRef";	
	protected static String xpdl_split = "Split";
	protected static String xpdl_extendedAttrs = "ExtendedAttributes";	
	protected static String xpdl_extendedAttr = "ExtendedAttribute";	
	
		
	
	public WorkflowExport() {
		
	}
	@Override
	public void customizeElement(Element element, Conditional activity) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void customizeElement(Element element, Iterative activity) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void customizeElement(Element element, NullActivity activity) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void customizeElement(Element element, Sequence activity) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void customizeElement(Element element, While activity) {
		// TODO Auto-generated method stub
		
	}
	public void customizeElement(Element element, JoinActivity activity) {
        Element restrictions = result.createElementNS(ns_xpdl,xpdl_transitionRestrictions);
        Element restriction = result.createElementNS(ns_xpdl,xpdl_transitionRestriction);
        Element join = result.createElementNS(ns_xpdl,"Join");
        join.setAttribute(xpdl_type, (activity instanceof AndJoin)?"AND":"OR");
        restriction.appendChild(join);
        restrictions.appendChild(restriction);
        element.appendChild(restrictions);		
	}	
	public void customizeElement(Element element, Fork activity) {
	}		
	
	@Override
	public Activity[] traverseActivity(Activity[] parentActivity,
			Fork activity, int level, boolean expand) {
		Activity[] a = super.traverseActivity(parentActivity, activity, level, expand);

		Element element = lookup.get(activity);
		if (element != null) {
	        Element restrictions = result.createElementNS(ns_xpdl,xpdl_transitionRestrictions);
	        Element restriction = result.createElementNS(ns_xpdl,xpdl_transitionRestriction);
	        Element split = result.createElementNS(ns_xpdl,xpdl_split);
	        split.setAttribute(xpdl_type, "AND");
	        
	        Element refs = result.createElementNS(ns_xpdl,xpdl_transitionRefs);
	        
			ArrayList<Activity> children = activity.getComponents();
			for (int i=0; i < children.size();i++) {
				Element e = lookup.get(children.get(i));
				if (e != null) {
					Element ref = result.createElementNS(ns_xpdl,xpdl_transitionRef);
					ref.setAttribute(xpdl_id, e.getAttribute(xpdl_id));
					refs.appendChild(ref);
				}
			}
	
	        split.appendChild(refs);
	        restriction.appendChild(split);
	        restrictions.appendChild(restriction);
	        element.appendChild(restrictions);			
		}
		return a;
	}
	public void customizeElement(Element element, Primitive activity) {
        Element attributes = result.createElementNS(ns_xpdl,xpdl_extendedAttrs);		
        Element attr;
        if (activity.getPerformer() != null) {

	        attr = result.createElementNS(ns_xpdl,xpdl_extendedAttr);
	        attr.setAttribute("class", activity.getPerformer().getClass().getName());
	        attributes.appendChild(attr);
	        
        }
        attr = result.createElementNS(ns_xpdl,xpdl_extendedAttr);
        attr.setAttribute("activity_class", activity.getClass().getName());
        attributes.appendChild(attr);	        
        element.appendChild(attributes);		
        
	}
	@Override
	public Element process(Activity[] parentActivity, Activity activity) {
		Element element = lookup.get(activity);
        if (element == null) {
	        element = result.createElementNS(ns_xpdl,xpdl_activity);
	        element.setAttribute(xpdl_name,activity.getName());
	        element.setAttribute(xpdl_description,activity.toString());	        
	        element.setAttribute(xpdl_id,Integer.toString(lookup.size()+1));     
	        
	        customizeElement(element, activity);

	        lookup.put(activity,element);	        
        }
        NodeList list = result.getElementsByTagNameNS(ns_xpdl,xpdl_activities);
        for (int i=0; i < list.getLength();i++)
        	list.item(i).appendChild(element);
        
        if (parentActivity != null) {
        	list = result.getElementsByTagNameNS(ns_xpdl,xpdl_transitions);
        	for (int j=0; j < parentActivity.length;j++)
        		for (int i=0; i < list.getLength();i++) {
        			Element parent = lookup.get(parentActivity[j]); 
        			if (parent != null) {
	        	        Element transition = result.createElementNS(ns_xpdl,xpdl_transition);
	        	        transition.setAttribute(xpdl_id,parent.getAttribute(xpdl_id)+"_"+element.getAttribute(xpdl_id));	        	        
	        	        transition.setAttribute("From",parent.getAttribute(xpdl_id));
	        	        transition.setAttribute("To",element.getAttribute(xpdl_id));
	        	        list.item(i).appendChild(transition);	        	        
        			}
        		}
        }


		return element;
	}
	@Override
	protected Document init(Workflow workflow) throws Exception {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);      
	        factory.setValidating(true);        
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        
	        Document doc = builder.newDocument();
	        
	        Element pkg = doc.createElementNS(ns_xpdl,"Package");
	        pkg.setAttribute(xpdl_name, "Test");
	        Element pkgHeader = doc.createElementNS(ns_xpdl,"PackageHeader");
	        pkg.appendChild(pkgHeader);
	        
	        Element version = doc.createElementNS(ns_xpdl,"XPDLVersion");
	        version.appendChild(doc.createTextNode("1.0"));
	        
	        Element vendor = doc.createElementNS(ns_xpdl,"Vendor");
	        vendor.appendChild(doc.createTextNode(getClass().getName()));
	        
	        Element created = doc.createElementNS(ns_xpdl,"Created");
	        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	        created.appendChild(doc.createTextNode(dateFormat.format(new java.util.Date())));
	        
	        Element description = doc.createElementNS(ns_xpdl,"Description");
	        description.appendChild(doc.createTextNode(workflow.toString()));	        

	        pkgHeader.appendChild(version);
	        pkgHeader.appendChild(vendor);
	        pkgHeader.appendChild(created);
	        pkgHeader.appendChild(description);	  	        
	        
	        Element cclass = doc.createElementNS(ns_xpdl,"ConformanceClass");
	        cclass.setAttribute("GraphConformance", "NON_BLOCKED");
	        pkg.appendChild(cclass);	 	     
	        
	        Element participants = doc.createElementNS(ns_xpdl,"Participants");
	        Element participant = doc.createElementNS(ns_xpdl,"Participant");
	        Element participantType = doc.createElementNS(ns_xpdl,"ParticipantType");
	        participantType.setAttribute(xpdl_type, "SYSTEM");
	        participants.appendChild(participant);
	        participant.appendChild(participantType);
	        
	        pkg.appendChild(cclass);		        
	        pkg.appendChild(participants);
	        
	        Element processes = doc.createElementNS(ns_xpdl,"WorkflowProcesses");
	        Element process = doc.createElementNS(ns_xpdl,"WorkflowProcess");	        
	        processes.appendChild(process);		        
	        
	        
	        doc.appendChild(pkg);
	        pkg.appendChild(pkgHeader);
	        pkg.appendChild(processes);
	        process.appendChild(doc.createElementNS(ns_xpdl,xpdl_activities));
	        process.appendChild(doc.createElementNS(ns_xpdl,xpdl_transitions));

	        return doc;
	}

	@Override
	public synchronized void write(Workflow workflow, Writer writer) throws Exception {
		Document doc = write(workflow);
    	DOMSource domSource = new DOMSource(doc);
    	StreamResult streamResult = new StreamResult(writer);
    	TransformerFactory tf = TransformerFactory.newInstance();
    	Transformer serializer = tf.newTransformer();
    	serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
    	serializer.setOutputProperty(OutputKeys.INDENT,"yes");
    	serializer.transform(domSource, streamResult);		
	}
}
