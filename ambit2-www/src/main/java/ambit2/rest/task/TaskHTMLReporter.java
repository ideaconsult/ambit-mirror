package ambit2.rest.task;

import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.restlet.Request;

import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitResource;
import ambit2.rest.ResourceDoc;
import ambit2.rest.SimpleTaskResource;
import ambit2.rest.algorithm.AllAlgorithmsResource;
import ambit2.rest.reporters.CatalogURIReporter;
import ambit2.rest.task.Task.TaskStatus;

public class TaskHTMLReporter<USERID> extends CatalogURIReporter<UUID> {
	protected ITaskStorage<USERID> storage;
	/**
	 * 
	 */
	private static final long serialVersionUID = 7644836050657868159L;
	public TaskHTMLReporter(ITaskStorage<USERID> storage,Request ref,ResourceDoc doc) {
		super(ref,doc);
		this.storage = storage;
	}
	

	@Override
	public void header(Writer output, Iterator<UUID> query) {
		try {
			String ajax = AmbitResource.js(getRequest().getResourceRef().toString(),baseReference);
			
			String max = getResourceRef().getQueryAsForm().getFirstValue(AbstractResource.max_hits);
			max = max==null?"10":max;
			
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest(),
					getResourceRef(),
					ajax,
					getDocumentation()
					);//,"<meta http-equiv=\"refresh\" content=\"10\">");
			output.write("<h4>Tasks:");
			for (TaskStatus status :TaskStatus.values())
				output.write(String.format("<a href='%s%s?search=%s&%s=%s'>%s</a>&nbsp;",
						baseReference,SimpleTaskResource.resource,status,AbstractResource.max_hits,max,status));
			output.write("</h4><p>");
			
		} catch (Exception x) {
			
		}
	}
	
	@Override
	public void processItem(UUID name, Writer output) {
		Task<TaskResult,USERID> item = storage.findTask(name);
		String t = "";
		String status = "Unknown";
		try {
			t = item.getUri()==null?"":item.getUri().toString();
			status = item.getStatus().toString();
		} catch (Exception x) {
			x.printStackTrace();
			status = "Error";
			t = "";
		} finally {

			try {

				output.write(
				String.format(		
				"<div class=\"ui-widget \" style=\"margin-top: 20px; padding: 0 .7em;\">\n"+
				"<div class=\"ui-widget-header ui-corner-top\"><p><a href='%s%s/%s'>Job</a> started %s&nbsp;</p></div>\n"+
				"<div class=\"ui-widget-content ui-corner-bottom %s\">\n"+
				"<p>Name:&nbsp;<strong>%s</strong></p><p>Status:%s %s <img src=\"%s/images/%s\">&nbsp;<a href='%s'>%s</a>"+
				//"<div class=\"%s ui-corner-all\" style=\"position:relative;width:50%%; right:-40%%; margin: 0; padding: 0 .7em;\">\n"+ 
				"	<p><span class=\"ui-icon %s\" style=\"float: right; margin-right: .3em;\"></span>\n"+
				"	%s:&nbsp;&nbsp;<strong>%s</strong>" +
				"	%s:&nbsp;&nbsp;<strong>%s</strong>" +
				"</p>\n"+
			//	"</div>" +
				"</div></div>\n",

				baseReference.toString(),
				SimpleTaskResource.resource,
				item.getUuid(),
				new Date(item.getStarted()),
				item.isDone()?(item.getError()==null?"":"ui-state-highlight"):"",
				item.getName(),
				item.getError()!=null?"<strong>Error</strong>":item.getTimeCompleted()>0?"Completed":"",
				item.getError()!=null?"":item.getTimeCompleted()>0?new Date(item.getTimeCompleted()):"",
				baseReference.toString(),
				item.isDone()?(item.getError()==null?"tick.png":"cross.png"):"24x24_ambit.gif",
				(item.isDone()&&item.getError()==null)?t:"",(item.isDone()&&item.getError()==null)?"Results available":"",
			
				item.isDone()?(item.getError()==null?"ui-icon-check":"ui-icon-alert"):"ui-icon-info",
				status,item.getError()==null?"":item.getError().getMessage(),
				item.getPolicyError()==null?"":"Policy error",item.getPolicyError()==null?"":item.getPolicyError().getMessage()
				));


			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	};
	
	/*
	@Override
	public void processItem(UUID name, Writer output) {
		Task<TaskResult,USERID> item = storage.findTask(name);
		String t = "";
		String status = "Unknown";
		try {
			t = item.getUri()==null?"":item.getUri().toString();
			status = item.getStatus().toString();
		} catch (Exception x) {
			x.printStackTrace();
			status = "Error";
			t = x.getMessage();
		} finally {
			try {output.write(
					String.format("<tr><td>%s</td><td>%s</td><td><a href='%s%s/%s'>%s</a></td><td><a href='%s'>%s</a></td><td><img src=\"%s/images/%s\"></td><td>%s</td><td>%s</td><td>%s</td></tr>",
							new Date(item.started),
							item.completed>0?item.completed-item.started:"",
							baseReference.toString(),
							SimpleTaskResource.resource,
							item.getUuid(),
							item.getUuid(),
							t,item.getName(),
							baseReference.toString(),
							item.isDone()?"tick.png":"24x24_ambit.gif",
							status,
							item.getError()==null?"":item.getError().getMessage(),
							item.getPolicyError()==null?"":item.getPolicyError().getMessage()		
							)); } catch (Exception x) {
				x.printStackTrace();
			}
		}
	};
	
	*/
	@Override
	public void footer(Writer output, Iterator<UUID> query) {
		try {
			AmbitResource.writeHTMLFooter(output, AllAlgorithmsResource.algorithm, getRequest());
			

			output.flush();
		} catch (Exception x) {
			
		}
	}
}