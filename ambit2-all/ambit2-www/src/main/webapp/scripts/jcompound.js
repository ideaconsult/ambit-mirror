/**
 *  Input: 
 *  opentox object, with opentox.feature array nonempty
 */
function identifiers(opentox, targetEntry) {
	var lookup = {
		cas    : [],
		names  : [],
		einecs : [],
	    reachdate : [],
		smiles : [],
		inchi : [],
		inchikey : [],
		misc : []
	};

	if (targetEntry != undefined) {
		lookup = targetEntry["lookup"];
	}
	//names
	$.each(opentox.dataEntry, function(k, value) {
		value["lookup"] = lookup;
		if (targetEntry != undefined) {
			$.extend(targetEntry["values"],value["values"]);
		}
	    value["getValue"] =  function(dataEntry,type) {
			var sOut = "";
			$.each(this[type], function(index, value) { 
			  sOut += dataEntry.values[value];
			  sOut += " ";
			});
			return sOut;
	    };    		
	});	

    $.each(opentox.feature, function(k, value) {
    		if (value.sameAs == "http://www.opentox.org/api/1.1#IUPACName") {
    			if (opentox.feature[k]) { if (lookup.names.indexOf(k)<0) lookup.names.push(k); }
    		} else if (value.sameAs == "http://www.opentox.org/api/1.1#ChemicalName") {
	        	if (opentox.feature[k]) { if (lookup.names.indexOf(k)<0) lookup.names.push(k); }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#CASRN") { 
	        	if (opentox.feature[k]) { if (lookup.cas.indexOf(k)<0) lookup.cas.push(k);   }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#EINECS") { 
	        	if (opentox.feature[k]) {if (lookup.einecs.indexOf(k)<0) lookup.einecs.push(k);   }	        
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#REACHRegistrationDate") { 
	        	if (opentox.feature[k]) {if (lookup.reachdate.indexOf(k)<0) lookup.reachdate.push(k);   }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#SMILES") { 
	        	if (opentox.feature[k]) {if (lookup.smiles.indexOf(k)<0) lookup.smiles.push(k);   }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#InChI") { 
	        	if (opentox.feature[k]) {if (lookup.inchi.indexOf(k)<0) lookup.inchi.push(k);   }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#InChIKey") { 
	        	if (opentox.feature[k]) {if (lookup.inchikey.indexOf(k)<0) lookup.inchikey.push(k);   
	        } else  {
	        	if (opentox.feature[k]) if (lookup.misc.indexOf(k)<0) lookup.misc.push(k);   
	        }	        
        	/*
	        } else {
	        	//console.log(k);
	        	count[4]++;
	        	if (count[4]<=200) { //TODO check how it works with many columns
		        	var thclass = "property";
		        	var visible = false;
		        	if (value.sameAs == "http://www.opentox.org/api/1.1#SMILES") { thclass = " smiles"; visible = false; }
		        	else if (value.sameAs == "http://www.opentox.org/api/1.1#InChI") { thclass = " inchi"; visible = false; }
		        	else if (value.sameAs.indexOf("http://www.opentox.org/echaEndpoints.owl")>=0) { thclass += " endpoint"; visible = opentox["showEndpoints"]; }	
					var source = opentox.feature[k]["source"]["type"];
					if (source=="Algorithm" || source=="Model") { thclass += " calculated"; visible |= opentox["showCalculated"]; }	
	        	}
	        	*/
	        }
	    });
    


}
