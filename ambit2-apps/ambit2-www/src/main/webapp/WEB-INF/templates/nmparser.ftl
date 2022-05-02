<#include "/html.ftl">

    <head>
        <#include "/header_updated.ftl">

            <script type='text/javascript' src='${ambit_root}/scripts/i5criteria.js'></script>

            <script type='text/javascript'>
                $(document)
                    .ready(
                        function() {
                            loadHelp("${ambit_root}", "nmparser");

                            jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/nmparser" title="nmdataparser">nmdataparser</a></li>');

                            jQuery("#breadCrumb").jBreadCrumb();
                            jQuery("#welcome").text("Parser");

                        });
            </script>

    </head>

    <body>

        <div class="container" style="margin:0;padding:0;">

            <!-- banner -->
            <#include "/banner_crumbs.ftl">

                <div class="one column remove-bottom" style="padding:0;">
                    &nbsp;</div>

                <!-- Page Content
		================================================== -->

                <div class="thirteen columns remove-bottom" style="padding:0;">

                    <div class='ui-widget-header ui-corner-top'>&nbsp;Parse Excel file into AMBIT data model (JSON)</div>
                    <div class='ui-widget-content ui-corner-bottom'>

                        <div style="margin:5px;padding:5px;">
                            <form action="${ambit_root}/nmparser" id="uploadForm" method="POST" ENCTYPE="multipart/form-data">

                                <div class='row remove-bottom'>
                                    <label class='five columns alpha' for="file">Excel file<a href='#' class='chelp nmparser'>?</a><em>*</em></label>
                                    <input class='eight columns alpha half-bottom' type="file" name="files[]" title='Parse Excel file into AMBIT JSON' size="60">
                                    <div class='three columns omega'></div>
                                </div>
                                <div class='row remove-bottom'>
                                    <label class='five columns alpha' for="file">JSON map for XLS/XLSX file <a href='#' class='chelp nmparser'>?</a><em>*</em></label>
                                    <input class='eight columns alpha half-bottom' type="file" name="jsonconfig" title='JSON configuration' size="60">
                                    <div class='three columns omega'></div>
                                </div>
                  				<div class='row remove-bottom'>
                                    <label class='five columns alpha' for="file">Expand map<a href='#' class='chelp nmparser'>?</a></label>
                                    <input class='eight columns alpha half-bottom' type="file" name="expandconfig" title='Expand map' size="60">
                                    <div class='three columns omega'></div>
                                </div>      
   
                                <div class='row remove-bottom'>
                                	<label class='five columns alpha' for="file">Prefix<a href='#' class='chelp nmparser'>?</a></label>
                                    <input class='three columns alpha half-bottom' name="prefix" title='Prefix' size="4" value="XLSX">
                                    
                                    <label class='five columns alpha'>&nbsp;</label>
                                    <input class='three columns alpha' type='submit' class='submit' value='Submit'>
                                    <div class='ten columns omega'></div>
                                </div>
                            </form>
                        </div>
                    </div>


                </div>
                


                <#include "/chelp.ftl">
				<div class="three columns remove-bottom" style="padding:0;"></div>
				<
				<blockquote  class="ten columns">
				<pre>
                curl -v -F prefix="TEST" -F expandconfig=@expand.json -F jsonconfig=@BET.json  -F files[]=@BET.xlsx http://localhost:9090/ambit2/nmparser > test.json
				</pre>
				</blockquote >
				
                    <#include "/footer.ftl">
        </div>
        <!-- container -->
    </body>

    </html>