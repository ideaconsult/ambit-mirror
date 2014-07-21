<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  <script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>
  <script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
  <script type='text/javascript'>

$(document)
		.ready(
				function() {
					    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="Admin">Admin</a></li>');
					    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/policy" title="Policy administration">OpenSSO policies</a></li>');
    					jQuery("#breadCrumb").jBreadCrumb();
    					jQuery("#welcome").text("Welcome to AMBIT");
    					
    					var purl = $.url();
						$('#uri').attr('value',purl.param('search')===undefined?'':purl.param('search'));
						$('#search').attr('value',purl.param('search')===undefined?'':purl.param('search'));
		
    					definePolicyTable("${ambit_root}","${ambit_request_json}","#policy");
    					loadHelp("${ambit_root}","policy");
				});
</script>
</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="one column" style="padding:0 2px 2px 2px 0;margin-right:0;" >
&nbsp;
</div>


		
		<!-- Page Content
		================================================== -->
<div class="eleven columns remove-bottom" style="padding:0;" >
		
	<div class="row remove-bottom ui-widget-header ui-corner-top">&nbsp;Policies</div>
	<div class="ui-widget-content ui-corner-bottom add-bottom ">
	<div class="row" style="padding:0;" >
		
			<table id='policy'  class='ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>URI</th>
			<th>Policy ID</th>
			<th>XACML</th>
			<th></th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>

	</div>		
	</div>
			
			
    <div class="row remove-bottom ui-widget-header ui-corner-top">&nbsp;Create a new policy</div>
   	<div class="ui-widget-content ui-corner-bottom">
		<div style='margin:5px;padding:5px;'>
		<form method='post' action='${ambit_root}/admin/policy' autocomplete='off'>
		
			<div class="row remove-bottom">		
				<label class='three columns alpha' >URI</label> 
				<input class='eight columns omega' type='text' size='40' name='uri' id='uri' value='' title='URI to create policy for"'>
			</div>

			<div class="row">		
				<label class='three columns alpha' >Methods</label> 
				<div class='eight columns omega' >
					<input type=CHECKBOX title='Allow reading the content of the resource at the specified URI' name='GET' GET>GET
					<input type=CHECKBOX title='Allow creating new resources under specified URI' name='POST' POST>POST
					<input type=CHECKBOX title='Allow updating the content of the resource at the specified URI' name='PUT' PUT>PUT
					<input type=CHECKBOX title='Allow deleting the resource at the specified URI' name='DELETE' DELETE>DELETE
				</div>		
			</div>

			<div class="row remove-bottom ">		
				<label class='three columns alpha' >Applies to:</label>
				<div class='five columns omega' > 
					<select name='type' class='five columns omega' >
						<option title='The policy will allow access to a user only' value='user' selected>user</option>
						<option title='The policy will allow access to a group only' value='group' >group</option>
					</select>
				</div>
				<label class='three columns alpha' >User/Group name</label>			
				<div class='five columns omega' >
					 <input type="text" name='name' title='User (OpenTox user) or group name (e.g. member or partner)' size="60"> 
				</div>
			</div>

			<div class="row remove-bottom">		
				<label class='three columns alpha'>&nbsp;</label>		
				<input class='five columns omega'  type="submit" value="Submit"><a href='#' class='chelp loginhelp'></a>
			</div>
		</form>
		
		</div>
		
	</div>
		
</div>
		<!-- Right column and footer
		================================================== -->

<div class="three columns" style='padding:0;margin:0;'>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; font-style:bold;'>
Policy search
</div>
<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<form method='GET' name='searchform' id='searchform' action='${ambit_root}/admin/policy' style='padding:0;margin:0;'>
	<input type='text'  id='search' name='search' value='' tabindex='1' >	
	<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>			
</form>				
</div>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
</div>

<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
</div>
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
</div>		
</div>

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>