var config_dataset = {
	"configuration": {
	   "baseFeatures": {
	        "http://www.opentox.org/api/1.1#Diagram": {
	            "title" : "Structure diagram",
	            "render" : function(col) {
	          	    col["mData"] = "compound.diagramUri";
	                col["mRender"] = function(data, type, full) {
	                  return (type != "display") ? "-" :
	                	  (
//	                	  '<input type="checkbox" name="cmp" value="'+full.compound.URI+'"> '+
	                	  '<a target="_blank" href="' + full.compound.URI + '"><img src="' + data + '" class="jtox-ds-smalldiagram"/></a>'
	                	  //'<a target="_blank" href="' + full.compound.URI + '"><img src="http://localhost:8080/ambit2/model/3?media=image/png&dataset_uri=' + full.compound.URI + '" class="jtox-ds-smalldiagram"/></a>'
	                	  );
	                };
	                col["sClass"] = "paddingless";
	                col["sWidth"] = "150px";
	                return col;
	            }
	        },
		"http://www.wikipathways.org/index.php/Pathway" :  {
			"title": "Wiki Pathways",
			"accumulate" : "compound.wikipathway",
			"render" : function(col) {
				col["mRender"] = function(data, type, full) {
					return (type != "display") ? "-" : full.compound.wikipathway;
				};
				return col;
			}
		}
	    },
	    "groups": {
	          "Identifiers" : [
	             "http://www.opentox.org/api/1.1#Diagram", 
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
 		  ]
        }
	}    
}