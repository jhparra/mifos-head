[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#import "macros.ftl" as mifosMacros]
[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<!--  Main Content Begins-->
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left" style="width:50%">[@spring.message "fundinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right width130px" style="float:right">[@spring.message "reviewAndSubmit"/]</p>
        </div>
        <div>
            <form method="POST" action="defineNewFund.ftl" name="organizationPreferences.definenewfund.defineNewFund">
                <div class="margin20lefttop">
                    <p class="font11pt">
                        <span class="fontBold">[@spring.message "organizationPreferences.definenewfund.defineanewfund"/]&nbsp;-</span>
                        <span class="orangeheading">[@spring.message "organizationPreferences.definenewfund.enterfundinformation"/]</span>
                    </p>

                    <div class="font9pt">[@spring.message "organizationPreferences.definenewfund.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminpagewithoutsubmittinginformation"/]</div>
                    <div class="font9pt"><span
                            class="red">*</span>[@spring.message "fieldsmarkedwithanasteriskarerequired"/] </div>
                [@mifos.showAllErrors "formBean.*"/]
                    <br/>

                    <p class="fontBold margin10topbottom">[@spring.message "organizationPreferences.definenewfund.funddetails"/] </p>

                    <div class="prepend-3  span-21 last">
                        <div class="span-20 "><span class="span-3 rightAlign"><span
                                class="red">* </span>[@spring.message "organizationPreferences.definenewfund.name"/]</span><span
                                class="span-5">&nbsp;
                        [@spring.bind "formBean.name"/]
                            <input type="text" name="${spring.status.expression}" id="${spring.status.expression}"
                                   value="${spring.status.value?if_exists}"/>
    				</span>
                        </div>
                        <div class="span-20 "><span class="span-3 rightAlign"><span
                                class="red">* </span>[@spring.message "organizationPreferences.definenewfund.fundCode"/]:</span><span
                                class="span-5">&nbsp;
                        [@mifos.formSingleSelectWithPrompt "formBean.codeId", code,"--selectone--" /]
				    </span>
                        </div>
                    </div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="buttonsSubmitCancel margin20leftright" >
                    <input class="buttn" type="submit" name="preview" value="[@spring.message "preview"/]"/>
                    <input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
                <div class="clear">&nbsp;</div>
            </form>
        </div>
        <!--Subcontent Ends-->
    </div>
</div>
<!--Main Content Ends-->
[@mifos.footer/]