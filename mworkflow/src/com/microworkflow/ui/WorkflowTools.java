package com.microworkflow.ui;

import java.util.ArrayList;

import com.microworkflow.process.Activity;
import com.microworkflow.process.AndJoin;
import com.microworkflow.process.Conditional;
import com.microworkflow.process.Fork;
import com.microworkflow.process.Iterative;
import com.microworkflow.process.JoinActivity;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.While;
import com.microworkflow.process.Workflow;

public abstract class WorkflowTools<T> {
    public Activity[] traverseActivity( Activity[] parentActivity, Sequence activity,int level, boolean expand) {
    	if (activity == null) return parentActivity;
        if (expand || (parentActivity == null)) {
            ArrayList<Activity> a = activity.getComponents();
            Activity[] prev;            	
            if (parentActivity != null)
            	prev = parentActivity;
            else 
            	prev = new Activity[] {activity};
            
            for (int i=0; i < a.size();i++) {
                prev = traverseActivity(prev,a.get(i),level+1,expand);
            }	    	
        return prev;
        } else { 
           process( parentActivity,activity);
           return new Activity[] {activity};
        }

    }

    public Activity[] traverseActivity( Activity[] parentActivity, Fork activity,int level, boolean expand) {
    	if (activity != null) {
    		process( parentActivity,activity);
	        ArrayList<Activity> a = activity.getComponents();
	        
	        Activity[] fork = new Activity[] {activity};
	        
	        for (int i=0; i < a.size();i++) {
	            Activity[] next = traverseActivity(fork,a.get(i),level+1,expand);
	            traverseActivity(next, activity.getBody(),level+1,expand);
	        }  
	    	return new Activity[] {activity.getBody()};
    	}  return parentActivity; 
    }	    
    public Activity[] traverseActivity( Activity[] parentActivity, Primitive activity,int level, boolean expand) {
    	if (activity == null) return parentActivity; 
    	process( parentActivity,activity);
    	return new Activity[] {activity};
    }
    public Activity[] traverseActivity( Activity[] parentActivity, Iterative activity,int level, boolean expand) {
    	if (activity == null) return parentActivity;
    	process( parentActivity,activity);
    	Activity[] start = new Activity[] {activity};
    	Activity[] last = traverseActivity(start, activity.getBody(), level+1,expand);
    	process( last, activity);
    	return start;
    }
    public Activity[] traverseActivity( Activity[] parentActivity, While activity,int level, boolean expand) {
    	if (activity == null) return parentActivity;
    	process( parentActivity,activity);
    	Activity[] start = new Activity[] {activity};
    	if (expand) {
	    	Activity[] last = traverseActivity(start, activity.getBody(), level+1,expand);
	    	process( last, activity);
    	}
    	return start;
    }	    
    
    public Activity[] traverseActivity( Activity[] parentActivity, Conditional activity,int level, boolean expand) {
        if (activity != null) {
    		process( parentActivity,activity);
    		Activity[] test = new Activity[] {activity};
       		Activity[] thenA = traverseActivity(test,activity.getThenBranch(),level+1,expand);
       		Activity[] elseA = traverseActivity(test,activity.getElseBranch(),level+1,expand);
       		Activity[] all = new Activity[thenA.length + elseA.length];
       		for (int i=0; i < thenA.length;i++)
       			all[i] = thenA[i];
       		for (int i=0; i < elseA.length;i++)
       			all[i+thenA.length] = elseA[i];
       		return all;
        } else return parentActivity;
            
    }   
    
    public Activity[] traverseActivity( Activity[] parentActivity, Activity activity,int level, boolean expand) {
        if (activity == null) return parentActivity;
        if (activity instanceof Primitive)
        	return traverseActivity(parentActivity, (Primitive) activity, level, expand);
        else
        if (activity instanceof Sequence)
        	return traverseActivity(parentActivity, (Sequence) activity, level, expand);
        else
        if (activity instanceof Fork)
        	return traverseActivity(parentActivity, (Fork) activity, level, expand);
        else
        if (activity instanceof Conditional)
        	return traverseActivity(parentActivity, (Conditional) activity, level, expand);
        else
        if (activity instanceof Sequence)
        	return traverseActivity(parentActivity, (Sequence) activity, level, expand);
        else
        if (activity instanceof Iterative)
        	return traverseActivity(parentActivity, (Iterative) activity, level,expand);
        else
        if (activity instanceof While)
        	return traverseActivity(parentActivity, (While) activity, level, expand);	        
        else {
        	process(parentActivity, activity);
        	return new Activity[] {activity};
        }
    }

    public abstract T process(Activity[] parentActivity, Activity activity) ;
    /*
     * Example workflow
     */
    public static Workflow createWorkflow() {
    	Activity a = new Primitive(null);
    	a.setName("Start");
    	TestCondition test = new TestCondition() {
    		@Override
    		public boolean evaluate() {
    			// TODO Auto-generated method stub
    			return false;
    		}
    	};
    	
    	Activity thenBranch = new Sequence();
    	thenBranch.setName("then");
    	for (int i=0; i < 3; i++) {
        	Activity sub = new Primitive(null);
        	sub.setName(Integer.toString(i+1));
        	thenBranch.addStep(sub);
    	}

    	Sequence elseBranch = new Sequence();
    	elseBranch.setName("else");
    	
    	Fork fork = new Fork();
    	fork.setName("fork");
    	elseBranch.addStep(fork);
    	for (int i=0; i < 2; i++) {
        	Activity sub = new Primitive(null);
        	sub.setName("Else "+Integer.toString(i+20));
        	elseBranch.addStep(sub);
    	}
    	
    	for (int i=0; i < 4; i++) {
        	if (i==3) {
        		Sequence seq = new Sequence();
        		seq.setName("Seq " + Integer.toString(i+10));
            	for (int j=0; j < 3; j++) {
            		Activity sub = new Primitive(null);
            		sub.setName(Integer.toString(j+100));
            		seq.addStep(sub);
            	}
            	fork.addBranch(seq);
        	} else {
            	Activity sub = new Primitive(null);
            	sub.setName(Integer.toString(i+10));
            	fork.addBranch(sub);
        	}
    	}
    	JoinActivity join = new AndJoin();
    	join.setName("join");
    	fork.setJoin(join);
    	Conditional c = new Conditional(test, thenBranch, elseBranch);

    	Activity more = new Sequence();
    	Primitive p = new Primitive(null);
    	p.setName("more");
    	more.addStep(p);
    	 
		Sequence body = new Sequence();
		body.setName("Iteration ");
    	for (int j=0; j < 3; j++) {
    		Activity sub = new Primitive(null);
    		sub.setName("Iteration "+Integer.toString(j+1));
    		body.addStep(sub);
    	}    	
    	
		Sequence subbody = new Sequence();
		subbody.setName("While ");
    	for (int j=0; j < 3; j++) {
    		Activity sub = new Primitive(null);
    		sub.setName("While "+Integer.toString(j+1));
    		subbody.addStep(sub);
    	}    	
    	While wh = new While();
    	wh.setBody(subbody);
    	body.addStep(wh);
    	
    	Iterative i = new Iterative("",null,"",body);
    	more.addStep(i);
    	
    	p = new Primitive(null);
    	p.setName("next");
    	more.addStep(p);    	


    	
    	p = new Primitive(null);
    	p.setName("end");
    	more.addStep(p);  
    	
    	Workflow w = new Workflow();
    	w.setDefinition(a.addStep(c).addStep(more));
		return w;
	}    
}
