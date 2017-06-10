<#include "/html.ftl">

    <head>
        <#include "/header_updated.ftl">

            <script type='text/javascript' src='${ambit_root}/scripts/i5criteria.js'></script>
            <script type='text/javascript'>
                $(document).ready(function() {
                    loadHelp("${ambit_root}", "uploadsubstance");

                    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance">Substances</a></li>');
                    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance" title="File upload">Import</a></li>');
                    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/updatesubstancei5" title="Retrieve substance(s) from IUCLID5 server">Retrieve substance(s) from IUCLID5 server</a></li>');
                    jQuery("#breadCrumb").jBreadCrumb();
                    jQuery("#welcome").text("Substance Import");
                    $('#option').change(function() {
                        if ("UUID" == $(this).val()) {
                            $('#quuid').show();
                            $('.qextid').hide();
                        } else {
                            $('#quuid').hide();
                            $('.qextid').show();
                        }
                    });
                    $('[name=options]').val('UUID');
                    updateFormValidation("#uploadForm");
                    _i5.getQAOptions(_i5.qaSettings);
                    var purl = $.url();
                    $('#uuid').attr('value', purl.param('uuid') === undefined ? '' : purl.param('uuid'));
                });

                function updateFormValidation(formName) {
                    $(formName).validate({
                        rules: {
                            'uuid': {
                                required: ($('select[name=option]').val() === 'UUID')
                            }

                        },
                        messages: {
                            'uuid': {
                                required: "Please enter a Substance UUID"
                            }
                        }
                    });
                }
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

                    <div class='ui-widget-header ui-corner-top'>&nbsp;Retrieve substance(s) from IUCLID5 server</div>
                    <div class='ui-widget-content ui-corner-bottom'>

                        <div style="margin:5px;padding:5px;">

                            <form action="${ambit_root}/substance" id="uploadForm" method="POST" autocomplete="off">

                                <div class='row remove-bottom'>
                                    <label class='three columns alpha' for="option">Select substance</label><a href='#' class='chelp server'>?</a>
                                    <select class='four columns alpha half-bottom' name='option' id='option'>
		
			<option title='by UUID' value='UUID' selected>by UUID</option>
			<option title='by external identifier stored in IUCLID5' value='EXTID' >by External identifier</option>
		</select>
                                    <div class='nine columns omega'></div>
                                </div>

                                <div class='row remove-bottom' id='quuid'>
                                    <label class='three columns alpha' for="uuid">UUID<em>*</em></label>
                                    <input class='seven columns alpha half-bottom' type="text" id='uuid' value='' name='uuid' title='UUID' size="40">
                                    <div class='six columns omega'></div>
                                </div>
                                <div class='row remove-bottom qextid' style='display:none;'>
                                    <label class='three columns alpha' for="extidtype">External identifier type<em>*</em></label>
                                    <input class='four columns alpha half-bottom' type="text" name='extidtype' title='External identifier type' size="30" value="CompTox">
                                    <div class='nine columns omega'></div>
                                </div>
                                <div class='row remove-bottom qextid' style='display:none;'>
                                    <label class='three columns alpha' for="extidvalue">External identifier value<em>*</em></label>
                                    <input class='four columns alpha half-bottom' type="text" name='extidvalue' title='External identifier value' size="30" value="Ambit Transfer">
                                    <div class='nine columns omega'></div>
                                </div>

                                <#include "/i5quality.ftl">

                                <div class='row remove-bottom'>
                                    <label class='three columns alpha' title=''>&nbsp;</label>
                                    <div class='help'>The IUCLID5 fields are optional, if left empty, the default IUCLID5 server will be used</div>
                                </div>
                                <div class='row remove-bottom'>
                                    <label class='three columns alpha' for="i5server">IUCLID5 server</label><a href='#' class='chelp server'>?</a>
                                    <input class='seven columns alpha half-bottom' type="text" id='i5server' value='' name='i5server' title='IUCLID5 server URI (optional)' size="40">
                                    <div class='six columns omega'>
                                        <a href="javascript:void(0)" onclick="_i5.ping('${ambit_root}/loom/i5/default/ping','#i5server','#i5user','#i5pass')">Check connectivity</a>
                                    </div>
                                </div>
                                <div class='row remove-bottom'>
                                    <label class='three columns alpha' for="i5user">IUCLID5 user</label>
                                    <input class='four columns alpha half-bottom' type="text" id='i5user' value='' name='i5user' title='IUCLID5 user (optional)' size="20">
                                    <div class='three columns omega'>&nbsp;</div>
                                    <div class='six columns omega'>
                                        <img src='${ambit_root}/images/24x24_ambit.gif' id='imgping' style='display:none;'>
                                        <div id='task_status'></div>
                                    </div>
                                </div>
                                <div class='row remove-bottom'>
                                    <label class='three columns alpha' for="i5pass">IUCLID5 password</label>
                                    <input class='four columns alpha half-bottom' type="password" id='i5pass' value='' name='i5pass' title='IUCLID5 password (optional)' size="20">
                                    <div class='three columns omega'>&nbsp;</div>
                                    <div class='six columns omega'>
                                        <div id='task_errorreport'></div>
                                    </div>
                                </div>

                                <div class='row'>
                                    <label class='ten columns alpha'>&nbsp;</label>
                                    <input class='four columns alpha' type='submit' class='submit' value='Submit'>
                                    <div class='two columns omega'></div>
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