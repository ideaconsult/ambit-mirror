var config_dataset = {
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
	        /*
		"http://www.opentox.org/echaEndpoints.owl#Carcinogenicity" : {
				"title" : "Carcinogenicity",
				"accumulate" : false,
				"data" : "compound.carcinogenicity",
				"process" : function(entry, featureId, features) {
					if (ccLib.isNull(entry.compound.carcinogenicity)) entry.compound.carcinogenicity = [];
					entry.compound.carcinogenicity.push(
							{
							"uri"	: featureId,
							"title" : features[featureId].originalTitle,	
							"value" : entry.values[featureId]	
							}
							);
				},				
				"render" : function(col) {
					col["mData"] = "compound.carcinogenicity";
					col["mRender"] = function(data, type, full) {
						if (type != "display") return "-";
						var sOut = "";
						$.each(data,function(index,fvalue) {
							sOut += '<a href="'+fvalue.uri+ '" target="carc">' + fvalue.title + "="+ fvalue.value + '</a><br/>';
						});
	                	return sOut;
		                };
		            return col;    
				}				
		},	        
	*/
	        /*
            "http://www.wikipathways.org/index.php/Pathway" :  {
                "title": "Wiki Pathways",
                "accumulate" : "compound.wikipathway",
                "process" : function(entry, featureId, features) {
                        if (ccLib.isNull(entry.compound.wikipathway)) entry.compound.wikipathway = [];
                        if ( entry.values[featureId]!= undefined)
                                entry.compound.wikipathway.push(
                                        {
                                        "uri"   : featureId,
                                        "title" : features[featureId].originalTitle,
                                        "value" : entry.values[featureId]
                                        }
                                        );
                },
                "column" : function(col) {
                        var root = "http://apps.ideaconsult.net:8080/ambit2/ui/_dataset?dataset_uri=";
                        col["mData"] = "compound.wikipathway";
                        col["mRender"] = function(data, type, full) {

                                if (type != "display") return "-";
                                var sOut = "<ul>";
                                $.each(data,function(index,fvalue) {
                                        sOut += '<li><a href="'+fvalue.title+ '" title="'+fvalue.title+'" target="wp">'
                                        + fvalue.value + '</a> ';

                                        var q =  'http://apps.ideaconsult.net:8080/ambit2/query/compound/search/all?search='+
                                                encodeURIComponent(fvalue.value) +
                                                "&feature_uris[]=http://apps.ideaconsult.net:8080/ambit2/dataset/1967255/feature";
                                        sOut += '<a href="'+ root + encodeURIComponent(q)+'" title="Structures participating in this pathway">'+
                                                '<span class="ui-icon ui-icon-search" style="float: right; margin-right: .3em;"></span>'+
                                                '</a>';
                                        sOut += "</li>";
                                });
                                sOut += "</ul>";
                        return sOut;
                        };
                    return col;
                }
        },
        */
		"http://www.opentox.org/api/1.1#CompositionInfo" : {
			  "visibility": "details",
				"title": "Substances",
				"data": "compound.URI",
				"basic": true,
				"render" : function(data, type, full) {
					return (type != "details") ? "-" : '<span class="jtox-details-composition" data-URI="' + data + '"></span>';
				}
		},        
		"http://www.opentox.org/api/1.1#Similarity" : {
			"title": "Similarity",
			"data" : "compound.metric", // you need to point where is the information located
			"accumulate": true,
			"render" : function(data, type, full) {
				return data;
			}
		}
	    },
	    "groups": function createGroups(miniset, kit) {
	    	 var groups = {
	          "Identifiers" : [
	             "http://www.opentox.org/api/1.1#Diagram",
	             "#DetailedInfoRow",
	             "http://www.opentox.org/api/1.1#CASRN", 
	             "http://www.opentox.org/api/1.1#EINECS",
	             "http://www.opentox.org/api/1.1#IUCLID5_UUID"
	          ],
	          
	          "Names": [
	                    "http://www.opentox.org/api/1.1#ChemicalName",
	                    "http://www.opentox.org/api/1.1#TradeName",
	                    "http://www.opentox.org/api/1.1#IUPACName",
	                    "http://www.opentox.org/api/1.1#SMILES",
	                    "http://www.opentox.org/api/1.1#InChIKey",
	                    "http://www.opentox.org/api/1.1#InChI",
	                    "http://www.opentox.org/api/1.1#REACHRegistrationDate"
 		  	],
 		  	"Similarity": [ "http://www.opentox.org/api/1.1#Similarity" ],
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
	    	        groups["Data"] = function (name, miniset) {
	    	            var arr = [];
	    	            for (var f in miniset.feature) {
	    	              if (!miniset.feature[f].used && !miniset.feature[f].basic)
	    	                arr.push(f);
	    	            }
	    	            return arr;
	    	          }	    	    	
	    	    	return groups;
        }
    
}