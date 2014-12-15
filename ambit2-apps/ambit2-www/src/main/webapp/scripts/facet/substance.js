var facet = {
		root: null,
		substanceComponent : null,
		endpointAutocomplete : function(tag,endpointquery,maxhits) {
			tag.autocomplete({
			      source: function( request, response ) {
			          $.ajax({
			            url: endpointquery,
			            dataType: "json",
			            data: {
			              media:"application/json",	            	
			              max: maxhits,
			              search: request.term
			            },
			            success: function( data ) {
			              response( $.map( data.facet, function( item ) {
			                return {
			                  label: item.endpoint + " ["+item.count+"]",
			                  value: item.endpoint
			                }
			              }));
			            }
			          });
			        },
			        minLength: 0,
			        open: function() {
				        //$('.ui-autocomplete').css('width', '450px');
				    } 	        
			});			
		},
		interpretationAutocomplete : function(tag,interpretationquery,maxhits) {
			tag.autocomplete({
			      source: function( request, response ) {
			          $.ajax({
			            url: interpretationquery,
			            dataType: "json",
			            data: {
			              media:"application/json",	            	
			              max: maxhits,
			              search: request.term
			            },
			            success: function( data ) {
			              response( $.map( data.facet, function( item ) {
			                return {
			                  label: item.interpretation_result + " ["+item.count+"]",
			                  value: item.interpretation_result
			                }
			              }));
			            }
			          });
			        },
			        minLength: 0,
			        open: function() {
				        //$('.ui-autocomplete').css('width', '450px');
				    } 	        
			});			
		},		
		searchStudy : function (event) {
			var selected = $("input[name^='category']:checked:enabled",'#fsearchForm');
			var params = [{'name' : 'type', 'value' : 'facet'}];
			$.each(selected,function(index,value) {
				var item = {};
				item['name']  = $(value).attr('name');
				item['value'] = $(value).attr('value');
				params.push(item);
			
				var name = 'endpoint.' +item['value'];
				var endpointBox = $("input[name^='"+name+"']",'#fsearchForm');
				var val = endpointBox.val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});
				
				name = 'unit.' +item['value'];
				var unitsBox = $("input[name^='"+name+"']",'#fsearchForm');
				var val = unitsBox.val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});
				
				name = 'lovalue.' +item['value'];
				var endpointvalueBox = $("input[name^='"+name+"']",'#fsearchForm');
				val = endpointvalueBox.val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});
				
				name = 'upvalue.' +item['value'];
				var endpointvalueBox = $("input[name^='"+name+"']",'#fsearchForm');
				val = endpointvalueBox.val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});				

				name = 'loqlf.' +item['value'];
				val = $("select[name^='" + name+"']").val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});		
				
				name = 'upqlf.' +item['value'];
				val = $("select[name^='" + name+"']").val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});		
				
				name = 'iresult.' +item['value'];
				var iresultBox = $("input[name^='"+name+"']",'#fsearchForm');
				var val = iresultBox.val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});
			});
			if (this.substanceComponent != undefined && this.substanceComponent!= null)  {
				var substanceQuery =  this.root + '/substance?' + jQuery.param(params);
				this.substanceComponent.querySubstance(substanceQuery);
				$("#hits").show();
			}	
			if (event!=null) event.preventDefault();
		},
		defineStudySearchFacets : function (root,url,selector) {
			var oTable = $(selector).dataTable( {
				"sAjaxDataProp" : "facet",
				"bProcessing": true,
				"bServerSide": false,
				"bStateSave": false,
				"aaSorting": [[1, 'asc']],
				"aoColumnDefs": [
				        {
				        	"mData": "value" ,
				        	"aTargets": [ 0 ],	
				        	"bSortable" : false,
				        	"bSearchable" : false,
				        	"bUseRendered" : false,
				        	 "fnRender" : function(o,val) {
				        		 return "<input type='checkbox' name='category' value='"+o.aData["subcategory"]+"."+o.aData["endpoint"]+"'>"; 
				        	 }
				        },         
		 				{ "mData": "value" , 
		 				  "asSorting": [ "asc", "desc" ],
						  "aTargets": [ 1 ],	
						  "bSearchable" : true,
						  "bUseRendered" : false,
						  "sClass" : "details-control",
						  "bSortable" : true,
						  "fnRender" : function(o,val) {
							  var sOut = (o.aData["value"]===undefined)? o.aData["uri"]:o.aData["value"];
							  sOut =
								  "<span class='ui-icon ui-icon-folder-collapsed' style='float:left;margin: .1em;' title='Click to show endpoints'></span>"+
								  sOut + " (<a href='"+o.aData["uri"]+"' title='Click to view substances'>S</a>)" +
								  " <span title='number of values'>[" + o.aData["count"] + "]</span>"
								  ;
							  return sOut;
						  }
						}			
					],
				"sDom" : '<"help remove-bottom"><"help">Trt<"help">',
				"bJQueryUI" : true,
				"bPaginate" : true,
				"sPaginationType": "full_numbers",
				"sPaginate" : ".dataTables_paginate _paging",
				"bDeferRender": true,
				"bSearchable": true,
				"sAjaxSource": url,
				"iDisplayLength" : -1,
				"fnDrawCallback" : function() {
					    $(selector + " thead").remove();
			    },
				"oLanguage": {
						"sSearch": "Filter:",
						"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
			            "sLoadingRecords": "No records found."
			    }
			} );
			
			$(selector + ' tbody').on('click','td.details-control span',function() {
				var nTr = $(this).parents('tr')[0];
				if (oTable.fnIsOpen(nTr)) {
					$(this).removeClass("ui-icon-folder-open");
					$(this).addClass("ui-icon-folder-collapsed");
					this.title='Click to show endpoints';
					oTable.fnClose(nTr);
				} else {
					$(this).removeClass("ui-icon-folder-collapsed");
					$(this).addClass("ui-icon-folder-open");
					this.title='Click to close endpoints';
					oTable.fnOpen(nTr, facet.endpointFormatDetails(oTable,nTr,facet.root),	'details');
											       
				}
			});	
			return oTable;
		},
		

		endpointFormatDetails : function ( oTable, nTr ,root ) {
		    var model = oTable.fnGetData( nTr );
		    
		    if (model["config"] === undefined) {
		    	model["config"] = $.extend(true,{}, config_study.columns["_"], config_study.columns[model.endpoint]);
		    }
		    var endpointValueName = "Enter endpoint value";
		    try {
		    	if (model.config.effects.result.sTitle!=undefined)
		    		endpointValueName = model.config.effects.result.sTitle;
		    } catch (err) {}

		    
		    var valueVisible = true;
		    try {
		    	valueVisible = model.config.effects.result.bVisible==undefined || model.config.effects.result.bVisible;
		    } catch (err) { }
		    
		    var endpointVisible = true;
		    try {
		    	endpointVisible = model.config.effects.endpoint.bVisible==undefined || model.config.effects.endpoint.bVisible;
		    } catch (err) { }
		    
		    var textVisible = true;
		    try {
		    	textVisible = model.config.effects.text.bVisible==undefined || model.config.effects.text.bVisible;
		    } catch (err) { }
		    
		    if (textVisible) {
			    try {
			    	if (model.config.effects.text.sTitle!=undefined)
			    		endpointValueName = model.config.effects.text.sTitle;
			    } catch (err) {}
		    }

		    
		    var div = $('<div style="margin:0px;border-style: solid;border-width: 0px;border-color: gray;" />');
		    var id = model.subcategory+"."+model.endpoint;
		    
		    if (endpointVisible) {
			    var endpointName = "Endpoint name";
			    try {
			    	if (model.config.effects.endpoint.sTitle!=undefined)
			    		endpointName = model.config.effects.endpoint.sTitle;
			    } catch (err) {} 		    	
		    	div.append($('<label class="twelve columns alpha" >'+endpointName+'</label>'));
		    }
		    if (valueVisible) {
		    	div.append($('<label class="four columns omega" >Units</label>'));
		    }
		    if (endpointVisible) {
			    var endpointBox = $('<input class="eleven columns alpha endpointname" type="text" title="Endpoint name, optional">');
			    endpointBox.attr("id", "endpoint."+id).attr("name", "endpoint."+id);
			    div.append(endpointBox);

				var params = {
						'top'	: model.subcategory,
						'category':model.endpoint	
						};
				
				var qurl = root+"/query/experiment_endpoints?" +  $.param(params,false);
				facet.endpointAutocomplete(endpointBox,qurl,10);
		    }
		    
		    if (valueVisible) {
			    var unitBox = $('<input class="four columns omega" type="text" title="Units, optional" >');
			    unitBox.attr("id", "unit."+id).attr("name", "unit."+id);
			    div.append(unitBox);
		    
			    div.append($('<label class="sixteen columns">'+endpointValueName+'</label>'));
		    
			    var loQualifierBox = $('<select class="three columns alpha"><option value=">=" selected>&gt;=</option><option value=">">&gt;</option><option value="=">=</option><option value="<=">&lt;=</option></select>');
			    loQualifierBox.attr("id", "loqlf."+id).attr("name", "loqlf."+id);
			    div.append(loQualifierBox);

			    var endpointvalueBox = $('<input class="four columns omega" type="text" title="lower value or exact value. Number expected" >');
			    endpointvalueBox.attr("id", "lovalue."+id).attr("name", "lovalue."+id);
			    div.append(endpointvalueBox);
    
			    var upQualifierBox = $('<select class="three columns omega"><option value="<=" selected>&lt;=</option><option value="<">&lt;</option><option value="=">=</option></select>');
			    upQualifierBox.attr("id", "upqlf."+id).attr("name", "upqlf."+id);
			    div.append(upQualifierBox);
	
			    endpointvalueBox = $('<input class="four columns omega" type="text" title="Upper value. Number expected">');
			    endpointvalueBox.attr("id", "upvalue."+id).attr("name", "upvalue."+id);
			    div.append(endpointvalueBox);
		    }
			if (textVisible) {
				div.append($('<label class="sixteen columns">'+endpointValueName+'</label>'));
				var endpointvalueBox = $('<input class="sixtteen columns" type="text" title="Text expected" >');
			    endpointvalueBox.attr("id", "lovalue."+id).attr("name", "lovalue."+id);
			    div.append(endpointvalueBox);
			}
			
		    //interpretation
		    var interpretationVisible = true;
		    try {
		    	interpretationVisible = 
		    		model.config.interpretation.result == undefined ||
		    		model.config.interpretation.result.bVisible==undefined || 
		    		model.config.interpretation.result.bVisible;
		    } catch (err) { }
		    
			if(interpretationVisible) {
				var interpretationResultName = "Interpretation of the results";
			    try {
			    	if (model.config.interpretation.result.sTitle!=undefined)
			    		interpretationResultName = model.config.interpretation.result.sTitle;
			    } catch (err) {} 					
				div.append($('<label class="sixteen columns">'+interpretationResultName+'</label>'));
				var interpretationResult = $('<input class="sixtteen columns" type="text" title="Text expected" >');
				interpretationResult.attr("id", "iresult."+id).attr("name", "iresult."+id);
			    div.append(interpretationResult);
			    
				var params = {
						'top'	: model.subcategory,
						'category':model.endpoint	
						};
				
				var qurl = root+"/query/interpretation_result?" +  $.param(params,false);
				facet.interpretationAutocomplete(interpretationResult,qurl,10);
			    
			}
		    return div;
		}
}






