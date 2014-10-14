/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012-2013, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 */
/**

 *  Input: 
 *  opentox object, with opentox.feature array nonempty
 */
function identifiers(opentox, targetEntry) {
	var lookup = {
		cas    : [],
		names  : [],
		tradenames  : [],
		einecs : [],
	    reachdate : [],
		smiles : [],
		inchi : [],
		inchikey : [],
		i5uuid: [],
		misc : []
	};

	if (targetEntry != undefined) {
		lookup = targetEntry["lookup"];
		$.extend(_ambit.search.result["feature"],opentox["feature"]);
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
    			if (opentox.feature[k]) { if (jQuery.inArray(k,lookup.names)<0) lookup.names.push(k); }
    		} else if (value.sameAs == "http://www.opentox.org/api/1.1#ChemicalName") {
	        	if (opentox.feature[k]) { if (jQuery.inArray(k,lookup.names)<0) lookup.names.push(k); }
    		} else if (value.sameAs == "http://www.opentox.org/api/1.1#TradeName") {
	        	if (opentox.feature[k]) { if (jQuery.inArray(k,lookup.names)<0) lookup.tradenames.push(k); }	        	
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#CASRN") { 
	        	if (opentox.feature[k]) { if (jQuery.inArray(k,lookup.cas)<0) lookup.cas.push(k);   }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#EINECS") { 
	        	if (opentox.feature[k]) {if (jQuery.inArray(k,lookup.einecs)<0) lookup.einecs.push(k);   }	        
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#REACHRegistrationDate") { 
	        	if (opentox.feature[k]) {if (jQuery.inArray(k,lookup.reachdate)<0) lookup.reachdate.push(k);   }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#SMILES") { 
	        	if (opentox.feature[k]) {if (jQuery.inArray(k,lookup.smiles)<0) lookup.smiles.push(k);   }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#InChI") { 
	        	if (opentox.feature[k]) {if (jQuery.inArray(k,lookup.inchi)<0) lookup.inchi.push(k);   }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#InChI_std") { 
	        	if (opentox.feature[k]) {if (jQuery.inArray(k,lookup.inchi)<0) lookup.inchi.push(k);   }	        	
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#InChIKey") { 
	        	if (opentox.feature[k]) {if (jQuery.inArray(k,lookup.inchikey)<0) lookup.inchikey.push(k);  }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#InChIKey_std") { 
	        	if (opentox.feature[k]) {if (jQuery.inArray(k,lookup.inchikey)<0) lookup.inchikey.push(k);  }
	        } else if (value.sameAs == "http://www.opentox.org/api/1.1#IUCLID5_UUID") { 
	        	if (opentox.feature[k]) {if (jQuery.inArray(k,lookup.i5uuid)<0) lookup.i5uuid.push(k);  }	        	
	        } else  {
	        	if (jQuery.inArray(k,lookup.misc)<0) lookup.misc.push(k);   
	        }	        
        	/*
	        } else {
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
	    });
    

}
