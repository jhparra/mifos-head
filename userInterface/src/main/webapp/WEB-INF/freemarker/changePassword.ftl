[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "login" /]
<span id="page.id" title="changePassword"/>
<div class="container">&nbsp;
    <!--Header-->
    <div class="topAlign append-1"><a href="j_spring_security_logout">[@spring.message "changePassword.logOut"/]</a>
    </div>
    <span class="logo"></span>

    <div class="orangeline" style="margin-top:75px;">&nbsp;</div>
    <div class="content" title="Change Password">
        <div class="borders span-14 sideMarginAuto" style="float:none;">
            <div class="subcontent paddingLeft">
                <!-- <div class="page-content"> -->

                <!-- <div id="formsection">-->
                <form method="post" action="changePassword.ftl" id="changepassword.form">
                    <div class=" orangeheading">[@spring.message "changePassword.changePassword"/]</div>
                    <p>[@spring.message "changePassword.enterCurrentPasswordAndThenChooseYourNewPasswordClickSubmitWhenYouAreDone"/]</p>

                    <div class="error">
                    [@spring.bind "formBean" /]
  					[@spring.showErrors "<br>" /]
                    </div>
   
                    <div class="span-10 "><span class="rightAlign span-5">
					<label for="changePassword.input.oldPassword">[@spring.message"changePassword.oldPassword"/]</label></span>
                    [@spring.bind "formBean.oldPassword" /]
                        <input type="password" id="changePassword.input.oldPassword" name="${spring.status.expression}"
                               value="${spring.status.value?default("")}"/>
                    [@spring.showErrors "<br>" /]
                    </div>

                    <div class="span-10 "><span class="rightAlign span-5">
				    <label for="changePassword.input.newPassword">[@spring.message"changePassword.newPassword"/]</label></span>
                    [@spring.bind "formBean.newPassword" /]
                        <input type="password" id="changePassword.input.newPassword" name="${spring.status.expression}"
                               value="${spring.status.value?default("")}"/>
                    [@spring.showErrors "<br>" /]
                    </div>

                    <div class="span-10 "><span class="rightAlign span-5">
					<label for="changePassword.input.confirmPassword">[@spring.message"changePassword.newPasswordConfirmation"/]</label></span>
                    [@spring.bind "formBean.newPasswordConfirmed" /]
                        <input type="password" id="changePassword.input.confirmPassword"
                               name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                    [@spring.showErrors "<br>" /]
                    </div>
            </div>
            <div class=" clear"></div>
            <div class="buttonsSubmitCancel margin20leftright margin20top30bottom">
                <input type="submit" class="buttn" id="changePassword.button.submit" name="submit"
                       value="[@spring.message "submit"/]"/>
                <input type="submit" id="CANCEL" class="buttn2" name="CANCEL" value="[@spring.message "cancel"/]"/>
            </div>
            </form>
        </div>
        <!-- page-content -->
    </div>
</div>
[@mifos.footer /]   