var config_substancedataset = {
		/*
	"fnAccumulate": function(fId, oldVal, newVal, features) {
		if (
			features[fId].sameAs == "http://www.opentox.org/echaEndpoints.owl#Carcinogenicity" ||
			features[fId].sameAs == "http://www.wikipathways.org/index.php/Pathway"				
			) {
			if ((oldVal === undefined) || (oldVal == null) || ("" == oldVal)) oldVal = [];
		    if ((newVal === undefined) || ccLib.isNull(newVal)) return oldVal;

		    newVal = {"feature" : fId,  "value" : newVal };

		    if ($.inArray(newVal,oldVal)==-1)
		          oldVal.push(newVal);
	
		    return oldVal;
		} else {
				
		      if (ccLib.isNull(newVal))
		          return oldVal;
		        newVal = newVal.toString();
		        if (ccLib.isNull(oldVal) || newVal.toLowerCase().indexOf(oldVal.toLowerCase()) >= 0)
		          return newVal;
		        if (oldVal.toLowerCase().indexOf(newVal.toLowerCase()) >= 0)
		          return oldVal;
		        return oldVal + ", " + newVal;			
		}
	},		
	*/
	
	
	   "baseFeatures": {
	        "http://www.opentox.org/api/1.1#CASRN" : {
		    	"used" : true,	
					"render" : function(col) {
			          	    col["bVisible"] = false;
			                return col;
			            }    		
		    },	        
		    "http://www.opentox.org/api/1.1#EINECS" : {
		    	"used" : true,	
					"render" : function(col) {
			          	    col["bVisible"] = false;
			                return col;
			            }    		
		    },	    
		    "http://www.opentox.org/api/1.1#IUPACName" : {
		    	"used" : true,	
					"render" : function(col) {
			          	    col["bVisible"] = false;
			                return col;
			            }    		
		    },	        
		    "http://www.opentox.org/api/1.1#SMILES" : {
		    	"used" : true,	
					"render" : function(col) {
			          	    col["bVisible"] = false;
			                return col;
			            }    		
		    },	        
		    "http://www.opentox.org/api/1.1#InChIKey" : {
		    	"used" : true,	
					"render" : function(col) {
			          	    col["bVisible"] = false;
			                return col;
			            }    		
		    },	        
		    "http://www.opentox.org/api/1.1#InChI" : {
		    	"used" : true,	
					"render" : function(col) {
			          	    col["bVisible"] = false;
			                return col;
			            }    		
		    },	        
		    "http://www.opentox.org/api/1.1#REACHRegistrationDate" : {
		    	"used" : true,	
					"render" : function(col) {
			          	    col["bVisible"] = false;
			                return col;
			            }    		
		    },
		    "http://www.opentox.org/api/1.1#TradeName" : {
		    	"title" : "Name1"
		    },
	        "http://www.opentox.org/api/1.1#IUCLID5_UUID" : {
	        	"title" : "UUID"
		    },
			"http://www.opentox.org/api/1.1#CompositionInfo" : {
				  "visibility": "details",
					"title": "Substances",
					"data": "compound.URI",
					"basic": true,
					"render" : function(data, type, full) {
		        return (type != "details") ? "-" : '<span class="jtox-details-composition" data-URI="' + data + '"></span>';
		      }
			}   		    
	    

	    },
	    "groups": function createGroups(miniset, kit) {
	    	 var groups = {
	          "Identifiers" : [
	             "#DetailedInfoRow",
	             "http://www.opentox.org/api/1.1#IUCLID5_UUID"
	          ],
	          
	          "Names": [
	                    "http://www.opentox.org/api/1.1#ChemicalName",
	                    "http://www.opentox.org/api/1.1#TradeName",
		  	],
			 "Substances": [ "http://www.opentox.org/api/1.1#CompositionInfo" ]
	    	 };
	    	 	for (var fId in miniset.feature) {
	    	      	var src = miniset.feature[fId].source;
	    	      	if (!src || !src.type || src.type.toLowerCase() != 'model')
	    	      	  continue;
	    	      	 src = src.URI.substr(src.URI.lastIndexOf('/') + 1);
	    	      	 if (groups[src] === undefined)
	    	      	  groups[src] = [];
	    	      	 groups[src].push(fId);
	    	    	}
	    	    	groups["Substances"] = [ "http://www.opentox.org/api/1.1#CompositionInfo" ];
	    	    	groups["Calculated"] = null;
	    	    	return groups;
	    }	    
    
}