function getMyAccount(root,url,readonly,username) {
	var facet = {};	

    $.ajax({
        dataType: "json",
        url: url,
        "cache": false,
        success: function(data, status, xhr) {
        	$.each(data["user"],function(index, entry) {
        		$("#username").text(entry["username"]);
        		if (username == entry["username"]) $("#edit").show(); else $("#edit").hide();
        		$("#useruri").prop("href",entry["uri"]);
        		$("#useruri").text(entry["title"] + " " + entry["firstname"] + " " + entry["lastname"]);
        		$("#email").prop("value",entry["email"]);
        		$("#title").prop("value",entry["title"]);
        		$("#firstname").prop("value",entry["firstname"]);
        		$("#lastname").prop("value",entry["lastname"]);

        		$("#homepage").prop("value",entry["homepage"]);
        		$("#keywords").prop("value",entry["keywords"]);
        		$("#reviewer").attr("checked",entry["reviewer"]);
        	        		
        		var protocolURI = root  + "/user/" + entry.id + "/dataset";
        		var alertURI = root + "/user/" + entry.id + "/alert";
        		
        		$("#protocoluri").prop("href",protocolURI);
        		$("#alerturi").prop("href",alertURI);
        		$("#breadCrumbUser").html(entry["username"]);
        		
        		var sOrg = "";
        		$.each(entry["organisation"],function(index,value) {
        			$("#affiliation").prop("value",value.title);
        			//update allows only one affiliation, although the db model allows multiple
        		});
        	});
        },
        error: function(xhr, status, err) {
        },
        complete: function(xhr, status) {
        }
     });
    return facet;
}


function defineUsersTable(root,url,selector,isadmin) {
	var not = "<span class='ui-icon ui-icon-closethick' title='NOT assigned'></span>";
	var yes = "<span class='ui-icon ui-icon-check' title='Role assigned'></span>";
	var oTable = $(selector).dataTable( {
		"sAjaxDataProp" : "user",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
		        { 
				  "mDataProp": "username",			
				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  var name= "<a href='"+o.aData["uri"]+"' title='"+o.aData["uri"]+"'>"+val+"</a>";
					  if (isadmin) {
						  var sOut = "initAdminPwdForm(";
						  sOut += '"' + o.aData["uri"] + '"';
						  sOut += ",";
						  sOut += '"' + o.aData["username"] + '"';
						  sOut += ")";
						  return name + " <a href='#' title='Password change' onClick='"+sOut+"'><span class='ui-icon ui-icon-person' style='float: right; margin-right: .3em;'></span></a>";
					  }
					  else 
						  return name;
				  }
				},		                 
 				{ 
				  "mDataProp": "id",			
 				  "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 1 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  return o.aData["firstname"] + " " + o.aData["lastname"];
				  }
				},
				{ "mDataProp": "email" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 2 ],
				  "bSearchable" : true,
				  "bSortable" : true,
				  "bUseRendered" : false,
				  "fnRender" : function(o,val) {
					  if ((val==null) || (val===undefined)) return "";
					  return "<a href='mailto:"+encodeURIComponent(o.aData["email"])+"'>"+o.aData["email"]+"</a>";
				  }
				},
				{ "mDataProp": "organisation" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 3 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						    if (val==null) return "";
						    var sOut = "";
				           	$.each(val,function(index, entry) {
				        		sOut += "<a href='"+entry.uri+"' title='"+entry.uri+"'>"+entry.title+"</a>";
				        		sOut += " ";
				        	});
				           	return sOut;
					  }
					},				
				{ "mDataProp": "keywords" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 4 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "fnRender" : function(o,val) {
						  return val;
					  }
				},
				{
				 	  "mDataProp": function (o,val) {
							return   o["reviewer"]?1:-1; //sorting doesn't work for boolean
				  	   },
					  "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 5 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "10%",
					  "fnRender" : function(o,val) {
						  //return "<input type='checkbox' id='"+o.aData["id"]+"' disabled='disabled' "+ (val?"checked":"") +">";
						  return (val>=0)?"<span class='ui-icon ui-icon-check' title='Able to upload data'></span>":"";
					  }
				},
				{ 	  "mDataProp": "roles.ambit_datasetmgr",
				  	  "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 6 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "5%",
					  "fnRender" : function(o,val) {
						  if (val===undefined) return not;
						  if (val==null) return not;
						  if (typeof val === 'string') 
						  try {
							  if (val.indexOf("Revoke Data manager role")>=0) return not;
							  else if (val.indexOf("Grant Data manager role")>=0)  return yes;
							  else return not;
						  } catch (err) { }
						  return (val===true)?yes:not;
					  }
				},
				{ 	  "mDataProp": "roles.ambit_admin",
					  "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 7 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "5%",
					  "fnRender" : function(o,val) {
						  if (val===undefined) return not;
						  if (val==null) return not;
						  if (typeof val === 'string') 
						  try {
							  if (val.indexOf("Revoke Admin role")>=0) return not;
							  else if (val.indexOf("Grant Admin role")>=0)  return yes;
							  else return not;
						  } catch (err) { }
						  return (val===true)?yes:not;
					  }
				},
				{ 	  "mDataProp": "status",
					  "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 8 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "5%"
				}				
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
	            "sProcessing": "<img src='"+root+"/images/progress.gif' border='0'>",
	            "sLoadingRecords": "No user entries found.",
	            "sLengthMenu": 'Display <select>' +
                '<option value="10">10</option>' +
                '<option value="20">20</option>' +
                '<option value="50">50</option>' +
                '<option value="100">100</option>' +
                '<option value="-1">all</option>' +
                '</select> users'	            
	    },
		"fnRowCallback": function( nRow, aData, iDisplayIndex ) {
	          $(nRow).attr("id",aData["username"]);
	          return nRow;
	    }	    
	} );
	return oTable;
}

/**
 * Inline editing of user roles
 * @param root
 * @param oTable
 */
function makeEditableUsersTable(root,oTable) {
	oTable.makeEditable({
		"aoColumns": [
		      null,null,null,null,null,null,
              {
                  type:'select'	,
                  loadtext: 'loading...',
                  indicator: 'Updating Data manager role ...',
                  tooltip: 'Double click to edit the data manager role',
                  loadtext: 'loading...',
                  data: "{'':'Please select...', true:'Grant Data manager role',false:'Revoke Data manager role'}",
                  onblur: 'cancel',
                  submit: 'Save changes'
              },
              {
                  type:'select'	,
                  loadtext: 'loading...',
                  indicator: 'Updating admin role ...',
                  tooltip: 'Double click to edit the admin role',
                  loadtext: 'loading...',
                  data: "{'':'Please select...', true:'Grant Admin role',false:'Revoke Admin role'}",
                  onblur: 'cancel',
                  submit: 'Save changes'
              },
              {
                  type:'select'	,
                  loadtext: 'loading...',
                  indicator: 'Updating user account status ...',
                  tooltip: 'Double click to disable or enable user account',
                  loadtext: 'loading...',
                  data: "{'':'Please select...', 'disabled':'disabled','confirmed':'confirmed'}",
                  onblur: 'cancel',
                  submit: 'Save changes'
              }               
		 ],
	     sUpdateURL: root+"/admin/role?method=PUT"
	});
}


function defineOrganisationTable(root,url) {

	var oTable = $('#organisation').dataTable( {
		"sAjaxDataProp" : "group",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
 				{ "mDataProp": "groupname" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  var name = o.aData["title"];
					  if (o.aData["uri"]==null) return name;
					  return "<a href='"+o.aData["uri"]+"' title='"+o.aData["uri"]+"'>"+name+"</a>";
				  }
				}
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',		
		"sSearch": "Filter:",
		"bJQueryUI" : true,
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
	            "sProcessing": "<img src='"+root+"/images/progress.gif' border='0'>",
	            "sLoadingRecords": "No records found."
	    },
		"fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
		    $.ajax( {
		        "dataType": 'json',
		        "type": "GET",
		        "url": sSource,
		        "data": aoData,
		        "contentType" : "application/json",
		        "success": fnCallback,
		        "timeout": 15000,  
		        "error" : function( xhr, textStatus, error ) {
		        	oSettings.oApi._fnProcessingDisplay( oSettings, false );
		        }		        
		    } );
		}		    
	} );
	
	return oTable;
}

function defineAlertsTable(root,url) {

	var oTable = $('#alerts').dataTable( {
		"sAjaxDataProp" : "alert",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
 				{ "mDataProp": "created" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "fnRender" : function(o,val) {
					  return new Date(val).toDateString();
				  }
				},
 				{ "mDataProp": "content" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 1 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "sWidth":"35%",
					  "fnRender" : function(o,val) {
						  var q = root + o.aData["title"] + "?" + o.aData["content"];
						  return "<a href='"+q+"' title='Go to search results'>"+(val.replace("pagesize=100","").replace(/&/g," ").replace(/xmet_/g," "))+"</a>\n";
					  }
				},
 				{ "mDataProp": "frequency" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 2 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return val;
					  }
				},
 				{ "mDataProp": "sent" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 3 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return new Date(val).toDateString();
					  }
				},
 				{ "mDataProp": "uri" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 4 ],	
					  "bSearchable" : true,
					  "bUseRendered" : false,
					  "bSortable" : true,
					  "fnRender" : function(o,val) {
						  return "<form method='POST' action='"+val+"?method=DELETE'><input class='remove-bottom' type='submit' value='Delete'></form>\n";
					  }
				}					
			],
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',			
		"sSearch": "Filter:",
		"bJQueryUI" : true,
		"bPaginate" : true,
		"bDeferRender": true,
		"bSearchable": true,
		"sAjaxSource": url,
		"oLanguage": {
				"sSearch": "Filter:",
	            "sProcessing": "<img src='"+root+"/images/progress.gif' border='0'>",
	            "sLoadingRecords": "No records found.",
	            "sLengthMenu": 'Display <select>' +
                '<option value="10">10</option>' +
                '<option value="20">20</option>' +
                '<option value="50">50</option>' +
                '<option value="100">100</option>' +
                '<option value="-1">all</option>' +
                '</select> alerts'	            
	    },
		"fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {
		    $.ajax( {
		        "dataType": 'json',
		        "type": "GET",
		        "url": sSource,
		        "data": aoData,
		        "contentType" : "application/json",
		        "success": fnCallback,
		        "timeout": 15000,  
		        "error" : function( xhr, textStatus, error ) {
		        	oSettings.oApi._fnProcessingDisplay( oSettings, false );
		        }		        
		    } );
		}	    
	} );
	return oTable;
}


function setAutocompleteOrgs(root,id) {
		$(id).autocomplete({
			source: function (request, response) {
				$.ajax({
	                url: root + "/organisation?media=application/json&search="+request.term,
	                contentType: "application/json; charset=utf-8",
	                dataType: "json",
	                success: function (data) {
	                    response(
	                    $.map(data.group, function (item) {
	                        return {
	                            label: item.title,
	                            value: item.title
	                        }
	                    }));
	                }
	            })
			},
            minLength: 2	            
        });
}
function initAdminPwdForm(uri,name) {
	$(".pwdchange").show();
	$("#uri").val(uri);
	$("#username").val(name);
	$("#pwd1").val("");
	$("#pwd2").val("");
	
}
function adminPwdForm(selector) {
	$(selector).validate({
		rules : {
			'uri': {
				required : true,
				minlength: 6
			},
			'username': {
				required : true,
				minlength: 3
			},					
			'pwd1': {
				required : true,
				minlength: 6
			},
			'pwd2': {
				required : true,
				minlength: 6,
				equalTo: "#pwd1"
			}
			
		},
		messages : {
			'uri'   : {
				required: "Please provide user URI"
			},
			'username'   : {
				required: "Please provide user name"
			},							
			'pwd1'   : {
				required: "Please provide a new password",
				minlength: "Your password must be at least 6 characters long"
			},
			'pwd2'   : {
				required: "Please provide a new password",
				minlength: "Your password must be at least 6 characters long",
				equalTo: "Please enter the same password as above"
			}			
		}
	});
}