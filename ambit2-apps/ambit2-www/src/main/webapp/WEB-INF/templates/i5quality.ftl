<hr/>
       <div class='row remove-bottom'>
                                    <label class='four columns alpha' title='Uncheck to add or replace study records, retaining existing where relevant'>Clear existing study records</label>
                                    <input class='one column omega half-bottom' type="checkbox" id="clearMeasurements" name="clearMeasurements" class="toggle" checked>
                                    <label class='four columns omega' title='Uncheck to add or replace compositions'>Clear existing composition records</label><a href='#' class='chelp clear'>?</a>
                                    <input class='one column omega half-bottom' type="checkbox" id="clearComposition" name="clearComposition" class="toggle" checked>
                                    <div class='six columns omega'></div>
                                </div>
                                <hr/>
                                <div class='row remove-bottom'>
                                    <div class='six columns alpha remove-bottom'>
                                        <label style="float: left; margin-left: .1em;" for="purposeflag">Purpose flag</label><a href='#' class='chelp studypurpose'>?</a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.purposeflag.highQuality()"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.purposeflag.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.purposeflag.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>
                                        <br/>
                                        <select multiple id="purposeflag" name="purposeflag" size='5'></select>
                                    </div>
                                    <div class='six columns omega remove-bottom'>
                                        <label style="float: left; margin-left: .1em;" for="studyresulttype">Study result type</label><a href='#' class='chelp resulttype'>?</a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.highQuality()"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>
                                        <br/>
                                        <select multiple id="studyresulttype" name="studyresulttype" size='5'></select>
                                    </div>

                                    <div class='four columns omega remove-bottom'>
                                        <label title='Use predefined filtering criteria'>Import only high quality study records<a href='#' class='chelp quality'>?</a></label>
                                        <input type="checkbox" id="qaenabled" name="qaenabled" class="toggle" checked>
                                        <span>(uncheck to import all records)</span>
                                    </div>



                                </div>
                                <div class='row remove-bottom'>
                                    <div class='six columns alpha remove-bottom'>
                                        <label style="float: left; margin-left: .1em;" for="reliability">Reliability</label><a href='#' class='chelp reliability'>?</a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.reliability.highQuality()"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.reliability.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.reliability.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>
                                        <br/>
                                        <select multiple id="reliability" name="reliability" size='5'></select>
                                    </div>
                                    <div class='six columns omega remove-bottom'>
                                        <label span style="float: left; margin-left: .1em;" for="referencetype">Reference type</label><a href='#' class='chelp referencetype'>?</a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.referencetype.highQuality()"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.referencetype.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a>
                                        <a href="javascript:void(0)" onclick="_i5.selections.referencetype.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>
                                        <br/>
                                        <select multiple id="referencetype" name="referencetype" size='5'></select>
                                    </div>



                                    <div class='four columns omega remove-bottom'>
                                        <br/>
                                        <a href="javascript:void(0)" onclick="_i5.selections.highQuality()" title='Click to select a predefined set of high quality study criteria'>
			<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span>Select high quality study criteria</a>
                                        <br/>
                                        <a href="javascript:void(0)" onclick="_i5.selections.selectall(true);">
			<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span>Select All</a>
                                        <br/>
                                        <a href="javascript:void(0)" onclick="_i5.selections.selectall(false)">
			<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span>Unselect all</a>
                                    </div>


                                </div>
                                <hr/>