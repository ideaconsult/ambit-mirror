<#include "/html.ftl">

    <head>
        <#include "/header_updated.ftl">

            <script type='text/javascript' src='${ambit_root}/scripts/i5criteria.js'></script>

            <script type='text/javascript'>
                $(document)
                    .ready(
                        function() {
                            loadHelp("${ambit_root}", "uploadenm");

                            jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance">Substances</a></li>');
                            jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadenm" title="File upload">Import</a></li>');
                            jQuery("#breadCrumb").jBreadCrumb();
                            jQuery("#welcome").text("Substance Import");

                            _i5.getQAOptions(_i5.qaSettings);
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

                <div class="ten columns remove-bottom" style="padding:0;">

                    <div class='ui-widget-header ui-corner-top'>&nbsp;Import new substance(s)</div>
                    <div class='ui-widget-content ui-corner-bottom'>

                        <div style="margin:5px;padding:5px;">
                            <form action="${ambit_root}/substance" id="uploadForm" method="POST" ENCTYPE="multipart/form-data">

                                <div class='row remove-bottom'>
                                    <label class='five columns alpha' for="file">File eNanoMapper Turtle (.ttl) <a href='#' class='chelp turtle'>?</a> </label>
                                    <input class='eight columns alpha half-bottom' type="file" name="files[]" title='Add new substance(s) ' size="60">
                                    <div class='three columns omega'></div>
                                </div>
   
      
                                <div class='row remove-bottom'>
                                    <div class='five columns omega remove-bottom'>&nbsp;</div>
                                    <label class='five columns alpha'>&nbsp;</label>
                                    <input class='three columns alpha' type='submit' class='submit' value='Submit'>
                                    <div class='three columns omega'></div>
                                </div>
                            </form>
                        </div>
                    </div>


                </div>


                <#include "/chelp.ftl">

                    <#include "/footer.ftl">
        </div>
        <!-- container -->
    </body>

    </html>