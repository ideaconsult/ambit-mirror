var _i5 = {
	    "qaSettings": {
	        "Purpose flag": {
	            "921": "key study",
	            "1590": "supporting study",
	            "1661": "weight of evidence",
	            "8108": "disregarded study",
	            "NOT_SPECIFIED": "Not specified"
	        },
	        "Study result type": {
	            "1895": "experimental result",
	            "1896": "experimental study planned",
	            "1885": "estimated by calculation",	            
	            "2303": "read-across based on grouping of substances (category approach)",
	            "2304": "read-across from supporting substance (structural analogue or surrogate)",
	            "14": "(Q)SAR",
	            "1342": "other:",
	            "1173": "no data",
	            "NOT_SPECIFIED": "Not specified"
	        },
	        "Reliability": {
	            "16": "1 (reliable without restriction)",
	            "18": "2 (reliable with restrictions)",
	            "22": "3 (not reliable)",
	            "24": "4 (not assignable)",
	            "1342": "other:",
	            "NOT_SPECIFIED": "Not specified"
	        },
	        "Reference type": {
	            "1586": "study report",	        	
	            "266": "other company data",
	            "1433": "publication",
	            "1486": "review article or handbook",
	            "1542": "secondary source",
	            "811": "grey literature",
	            "1342": "other:",
	            "NOT_SPECIFIED": "Not specified"
	        },
	        "Test material": {
	        	"2480": "yes",
	        	"2158": "no",
	        	"NOT_SPECIFIED" : "Not specified" 
	        }	        
	    },
	    "getQAOptions" : function(qa) {
	    	this.getQAOption(qa["Purpose flag"],"#purposeflag");
	    	this.getQAOption(qa["Study result type"],"#studyresulttype");
	    	this.getQAOption(qa["Reliability"],"#reliability");
	    	this.getQAOption(qa["Reference type"],"#referencetype");
	    	this.getQAOption(qa["Test material"],"#testmaterial");
	    },
	    "getQAOption" : function(qa,selector) {
	    	
	    	$.each(qa,function(index,val) {
				$('<option>',
						{
						    value: index,
						    title: index + " : " + val,
						    text: val
						}).appendTo(selector);
				});
	    }
	}