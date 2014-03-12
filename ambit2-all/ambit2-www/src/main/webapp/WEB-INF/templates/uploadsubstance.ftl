<#include "/html.ftl" >

<head>
<#include "/header_updated.ftl" >


<noscript><link rel="stylesheet" href="${ambit_root}/jquery/fileupload/style.css"></noscript>
<link rel="stylesheet" href="${ambit_root}/jquery/fileupload/jquery.fileupload.css">
<link rel="stylesheet" href="${ambit_root}/jquery/fileupload/jquery.fileupload-ui.css">
<!-- CSS adjustments for browsers with JavaScript disabled -->
<noscript><link rel="stylesheet" href="${ambit_root}/jquery/fileupload/css/jquery.fileupload-noscript.css"></noscript>
<noscript><link rel="stylesheet" href="${ambit_root}/jquery/fileupload/css/jquery.fileupload-ui-noscript.css"></noscript>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					//$( "#selectable" ).selectable( "option", "distance", 18);
					loadHelp("${ambit_root}","substance");
					 $('#fileupload').fileupload({
					        // Uncomment the following to send cross-domain cookies:
					        //xhrFields: {withCredentials: true},
					        url: '${ambit_root}/ui/uploadsubstance'
					    });
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="alpha">
			<div class="remove-bottom h3">
				Substances upload	
			</div>
		    <div class='h6'>
		    		Upload substances as defined in multiple IUCLID 5 .i5d or .i5z files
		    		<br/>
		    		<a href="${ambit_root}/ui/uploadsubstance1">Single .i5z file upload<a>
		    </div>
		</div>
	</div>				
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu_substance.ftl">
<ul >
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
</ul>
</div>

<div class="eleven columns" style="padding:0;" >


<div class='ui-widget-header ui-corner-top'>&nbsp;Import new substance(s)</div>

<!-- Multi file upload (JS needed) -->

<div class='ui-widget-content ui-corner-bottom' id='multiUpload' >	

	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/upload" id="fileupload"  method="POST"   ENCTYPE="multipart/form-data">
	<!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
	

	<div class="fileupload-buttonbar row remove-bottom">
	    <div class="fileupload-buttons sixteen columns">
	        <!-- The fileinput-button span is used to style the file input field as button -->
	        <span class="fileinput-button">
	            <span>Add files...</span>
	            <input type="file" name="files[]" multiple>
	        </span>
	        <button type="submit" class="start">Start upload</button>
	        <button type="reset" class="cancel">Cancel upload</button>
	        <!--
	        <button type="button" class="delete">Delete</button>
	        <input type="checkbox" class="toggle">
	        -->
	        <!-- The global file processing state -->
	        <span class="fileupload-process remove-bottom"></span>
	    </div>
	    <div class="sixteen columns">
        	<span title='Use predefined filtering criteria'>Import only high quality study records (uncheck to import all records)</span> <input type="checkbox" id="qaenabled" name="qaenabled" class="toggle" checked>
	    </div>
	    <!-- The global progress state -->
	    <div class="fileupload-progress fade remove-bottom" style="display:none">
	        <!-- The global progress bar -->
	        <div class="progress" role="progressbar" aria-valuemin="0" aria-valuemax="100"></div>
	        <!-- The extended global progress state -->
	        <div class="progress-extended">&nbsp;</div>
	    </div>
	</div>
	
	
	<!-- The table listing the files available for upload/download -->
	<div role="presentation" border="1" ><div class="files"></div></div>
	
	</form>
	</div>	
</div>




<div class='row add-bottom' style="height:140px;">&nbsp;</div>
<!--"eleven columns"-->
</div>

<#include "/chelp.ftl" >


<#include "/footer.ftl" >
</div> <!-- container -->


<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <div class="template-upload fade row remove-bottom">
        <span class="preview one column">&nbsp;</span>
        <span class="name six columns">{%=file.name%}</span>
        <strong class="error two columns"></strong>
        <span class="size two columns">Processing...</span>
       	<div class="progress two columns"></div>
        <div>
            {% if (!i && !o.options.autoUpload) { %}
                <button class="start" disabled>Start</button>
            {% } %}
            {% if (!i) { %}
                <button class="cancel">Cancel</button>
            {% } %}
        </div>
    </div>
{% } %}
</script>

<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
	<div class="template-upload fade row remove-bottom">
	 	<span class="preview one column remove-bottom">
                {% if (file.thumbnailUrl) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" data-gallery><img src="{%=file.thumbnailUrl%}"></a>
                {% } %}
        &nbsp;</span>
        <span class="name six columns remove-bottom">
                <a href="{%=file.url%}" title="{%=file.name%}" target='_substance'>{%=file.name%}</a>
        </span>
        <span class="size two columns remove-bottom">{%=o.formatFileSize(file.size)%}</span>
        {% if (file.error) { %}
        <div class="four columns remove-bottom"><span class="error">Error</span> {%=file.error%}</div>
        {% } %}        
    </div>
{% } %}
</script>


 
<!-- The Templates plugin is included to render the upload/download listings -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/tmpl.min.js"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality--> 
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/load-image.min.js"></script>
<!-- The basic File Upload plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload.js"></script>
<!-- The File Upload processing plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload-process.js"></script>
<!-- The File Upload image preview & resize plugin -->
<script type='text/javascript'  src="${ambit_root}/jquery/fileupload/jquery.fileupload-image.js"></script>
<!-- The File Upload validation plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload-validate.js"></script>
<!-- The File Upload user interface plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload-ui.js"></script>
<!-- The File Upload jQuery UI plugin -->
<script type='text/javascript' src="${ambit_root}/jquery/fileupload/jquery.fileupload-jquery-ui.js"></script>

</body>
</html>
