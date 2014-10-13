var _i5 = {
	    "qaSettings": {
	        "Purpose flag": {
	            "921": {"title" :"key study", "selected" : true},
	            "1590": {"title" :"supporting study", "selected" : true},
	            "1661": {"title" :"weight of evidence"},
	            "8108": {"title" :"disregarded study"},
	            "NOT_SPECIFIED": {"title" :"Not specified"},
	            "null": {"title" :"Not assigned (null)"}
	        },
	        "Study result type": {
	            "1895": {"title" :"experimental result", "selected" : true},
	            "1896": {"title" :"experimental study planned"},
	            "1885": {"title" :"estimated by calculation"},	            
	            "2303": {"title" :"read-across based on grouping of substances (category approach)"},
	            "2304": {"title" :"read-across from supporting substance (structural analogue or surrogate)"},
	            "14": {"title" :"(Q)SAR"},
	            "1342": {"title" :"other:"},
	            "1173": {"title" :"no data"},
	            "NOT_SPECIFIED": {"title" :"Not specified"},
	            "null": {"title" :"Not assigned (null)"}
	        },
	        "Reliability": {
	            "16": {"title" :"1 (reliable without restriction)", "selected" : true},
	            "18": {"title" :"2 (reliable with restrictions)", "selected" : true},
	            "22": {"title" :"3 (not reliable)"},
	            "24": {"title" :"4 (not assignable)"},
	            "1342": {"title" :"other:"},
	            "NOT_SPECIFIED": {"title" :"Not specified"},
	            "null": {"title" :"Not assigned (null)"}
	        },
	        "Reference type": {
	            "1586": {"title" :"study report", "selected" : true},	        	
	            "266":  {"title" :"other company data"},
	            "1443": {"title" :"publication", "selected" : true},
	            "1486": {"title" :"review article or handbook", "selected" : true},
	            "1542": {"title" :"secondary source"},
	            "811": {"title" :"grey literature"},
	            "1342": {"title" :"other:"},
	            "NOT_SPECIFIED": {"title" :"Not specified"},
	            "null": {"title" :"Not assigned (null)"}
	        },
	        "Test material": {
	        	"2480": {"title" :"yes", "selected" : true},
	        	"2158": {"title" :"no"},
	        	"NOT_SPECIFIED" : {"title" :"Not specified"},
	        	"null": {"title" :"Not assigned (null)"}
	        }	        
	    },
	    "selections" : {
	    	"purposeflag" : {
	    		"selectall" : function(value) {
	    			_i5.selections.selectall(value, _i5.qaSettings["Purpose flag"],"#purposeflag");
	    		},
	    		"highQuality" : function(value) {
	    			_i5.selections.highQuality( _i5.qaSettings["Purpose flag"],"#purposeflag");
	    		}
	    	},
	    	"reliability" : {
	    		"selectall" : function(value) {
	    			_i5.selections.selectall(value, _i5.qaSettings["Reliability"],"#reliability");
	    		},
	    		"highQuality" : function(value) {
	    			_i5.selections.highQuality( _i5.qaSettings["Reliability"],"#reliability");
	    		}
	    	},	    
	    	"studyresulttype" : {
	    		"selectall" : function(value) {
	    			_i5.selections.selectall(value, _i5.qaSettings["Study result type"],"#studyresulttype");
	    		},
	    		"highQuality" : function(value) {
	    			_i5.selections.highQuality( _i5.qaSettings["Study result type"],"#studyresulttype");
	    		}
	    	},		 
	    	"referencetype" :  {
	    		"selectall" : function(value) {
	    			_i5.selections.selectall(value, _i5.qaSettings["Reference type"],"#referencetype");
	    		},
	    		"highQuality" : function(value) {
	    			_i5.selections.highQuality( _i5.qaSettings["Reference type"],"#referencetype");
	    		}
	    	},		    	
	    	"testmaterial" : {
	    		"selectall" : function(value) {
	    			_i5.selections.selectall(value, _i5.qaSettings["Test material"],"#testmaterial");
	    		},
	    		"highQuality" : function(value) {
	    			_i5.selections.highQuality( _i5.qaSettings["Test material"],"#testmaterial");
	    		}
	    	},		   	    	
	    	"selectall" : function(value,qa,selector) {
	    		if (qa==undefined || selector===undefined) {
	    			var qa = _i5.qaSettings;
	    		  	_i5.selections.selectall(value,qa["Purpose flag"],"#purposeflag");
	    		  	_i5.selections.selectall(value,qa["Study result type"],"#studyresulttype");
	    		  	_i5.selections.selectall(value,qa["Reliability"],"#reliability");
	    		  	_i5.selections.selectall(value,qa["Reference type"],"#referencetype");
	    		  	_i5.selections.selectall(value,qa["Test material"],"#testmaterial");
	    		} else {
	    	 		var selected = []; //unselect
	    	 		if (value) //select 
			    		$.each(qa,function(index,val) {
			    			selected.push(index);
			    		});
		    		$(selector).val(selected);
	    		}	
	    	},
	    	"highQuality" : function(qa,selector) {
	    		if (qa===undefined || selector==undefined) {
	    			var qa = _i5.qaSettings;
	    		  	_i5.selections.highQuality(qa["Purpose flag"],"#purposeflag");
	    		  	_i5.selections.highQuality(qa["Study result type"],"#studyresulttype");
	    		  	_i5.selections.highQuality(qa["Reliability"],"#reliability");
	    		  	_i5.selections.highQuality(qa["Reference type"],"#referencetype");
	    		  	_i5.selections.highQuality(qa["Test material"],"#testmaterial");
	    		} else {
		    		var selected = [];
		    		$.each(qa,function(index,val) {
		    			if ((val.selected != undefined) && val.selected) {
		    				selected.push(index);
		    			}	
		    		});
		    		$(selector).val(selected);
	    		}
	    	}
	    },
	    "getQAOptions" : function(qa) {
	    	this.getQAOption(qa["Purpose flag"],"#purposeflag");
	    	this.getQAOption(qa["Study result type"],"#studyresulttype");
	    	this.getQAOption(qa["Reliability"],"#reliability");
	    	this.getQAOption(qa["Reference type"],"#referencetype");
	    	this.getQAOption(qa["Test material"],"#testmaterial");
	    	
	    	//_i5.selections.selectall(false);  select all
	    	//_i5.selections.selectall(false); unselect all
	    	
	    },
	    "getQAOption" : function(qa,selector) {
	    	
	    	$.each(qa,function(index,val) {
				$('<option>',
						{
						    value: index,
						    title: index + " : " + val.title,
						    text: val.title
						}).appendTo(selector);
				});
	    	this.selections.highQuality(qa,selector);	    
	    },
	    "ping" : function(url,server,user,pass) {
	    	var p = {
	    	        'server'   : $(server).val(),
	    	        'user' : $(user).val(),
	    	        'pass' : $(pass).val() 
	    	    };
	    	$('#imgping').show();
	    	$("#task_status").text("");
	    	$("#task_errorreport").text("");
	  	  $.ajax({
		        dataType: "json",
		        url: url + "?" + $.param(p),
		        success: function(data, status, xhr) {
		        	$.each(data["task"],function(index, entry) {
		        		$("#task_status").text(entry.name + " " + entry.status);
		        		$("#task_errorreport").text(entry.error);
		        	});
		        	$('#imgping').hide();
		        },
		        error: function(xhr, status, err) {
		        	try {
		        		if (xhr.responseText != undefined) {
				        	var task = jQuery.parseJSON(xhr.responseText);
				        	if (task.length > 0) {
				        		var entry = task[0];
				        		$("#task_status").text(entry.name + " " + entry.status);
				        		$("#task_errorreport").text(entry.error);
				        	}
		        		}
		        		$('#imgping').hide();
		        	} catch (err) {
		        		$("#task_status").text(status);
		        		$("#task_errorreport").text(err);
		        	}
		        },
		        complete: function(xhr, status) {
		        	$('#imgping').hide();
		        }
		     });
	    }
	    
	}