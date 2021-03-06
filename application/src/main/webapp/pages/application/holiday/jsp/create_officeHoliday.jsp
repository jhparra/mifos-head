<%--
Copyright (c) 2005-2010 Grameen Foundation USA
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License.

See also http://www.apache.org/licenses/LICENSE-2.0.html for an
explanation of the license and how it is applied.
--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html-el"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/mifos-html" prefix="mifos"%>
<%@ taglib uri="/tags/date" prefix="date"%>
<%@ taglib uri="/mifos/customtags" prefix="mifoscustom"%>
<%@ taglib uri="/mifos/custom-tags" prefix="customtags"%>
<%@ taglib uri="/sessionaccess" prefix="session"%>
<tiles:insert definition=".clientsacclayoutsearchmenu">
	<tiles:put name="body" type="string">
        <span id="page.id" title="create_officeHoliday" />
		<SCRIPT>
            function fun_cancel()
            {
                location.href="AdminAction.do?method=load";
            }
        </SCRIPT>
	  <SCRIPT SRC="pages/framework/js/date.js"></SCRIPT>
	  <script type="text/javascript" src="pages/js/jquery/jquery-1.4.2.min.js"></script>
	  <script type="text/javascript" src="pages/js/jstree/jquery.jstree.js"></script>
  	  <script type="text/javascript" src="pages/application/holiday/js/createHolidays.js"></script>
		<html-el:form method="post"
			action="/holidayAction.do?method=preview"
			onsubmit="return (validateMyForm(holidayFromDate,holidayFromDateFormat,holidayFromDateYY) &&
							  validateMyForm(holidayThruDate,holidayThruDateFormat,holidayThruDateYY))"
			focus="repaymentRuleId">
			<c:set value="${session:getFromSession(sessionScope.flowManager,requestScope.currentFlowKey,'BusinessKey')}" var="BusinessKey" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
			  <tr>
          		<td class="bluetablehead05">
	             <span class="fontnormal8pt">
	          	 	<html-el:link action="AdminAction.do?method=load&randomNUm=${sessionScope.randomNUm}">
						<mifos:mifoslabel name="holiday.labelLinkAdmin" bundle="HolidayUIResources"/>	
					</html-el:link> /
	              </span>
	              <span class="fontnormal8pt">
	              	<html-el:link action="holidayAction.do?method=get&randomNUm=${sessionScope.randomNUm}&currentFlowKey=${requestScope.currentFlowKey}">
          				<mifos:mifoslabel name="holiday.labelLinkViewHolidays" bundle="HolidayUIResources"/>
          			</html-el:link> /
	              </span>
	              <span class="fontnormal8ptbold">
          			<mifos:mifoslabel name="holiday.labelAddHolidayNow" bundle="HolidayUIResources"/>          			        			
          	     </span>
    	        </td>
	    	  </tr>
			</table>
			<table width="95%" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td width="70%" height="24" align="left" valign="top"
						class="paddingL15T15">
					<table width="96%" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="70%" class="headingorange"></span><mifos:mifoslabel
								name="holiday.labelAddHolidayNow" bundle="HolidayUIResources"/> 
								</td>
						</tr>
					</table>
					<br>
					<table width="95%" border="0" cellspacing="4" cellpadding="4">
						<tr>
							<td colspan="2"><font class="fontnormalRedBold"> <html-el:errors
								bundle="HolidayUIResources" /> </font></td>
						</tr>
						<tr>
							<td colspan="2" class="fontnormal"><mifos:mifoslabel
								mandatory="yes" name="holiday.asterisk" isColonRequired="No" bundle="HolidayUIResources"/><br>
							<br>
							</td>
						</tr>
						<tr>
							<td width="24%" align="right" class="fontnormal"><mifos:mifoslabel
								mandatory="yes" name="holiday.HolidayName" isColonRequired="Yes" bundle="HolidayUIResources"/></td>
							<td width="76%">
								<mifos:mifosalphanumtext styleId="holiday.input.name" property="holidayName" name="holidayActionForm" style="width:45%" maxlength="25"/>
							</td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								mandatory="yes" isColonRequired="Yes" name="holiday.HolidayFromDate" bundle="HolidayUIResources"/></td>
							<td class="fontnormal"><date:datetag property="holidayFromDate" /></td>
						</tr>
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								mandatory="no" isColonRequired="Yes" name="holiday.HolidayThruDate" bundle="HolidayUIResources"/></td>
							<td class="fontnormal"><date:datetag property="holidayThruDate"/></td>
						</tr>						
						<tr>
							<td align="right" class="fontnormal"><mifos:mifoslabel
								name="holiday.HolidayRepaymentRule" mandatory="yes" isColonRequired="Yes" bundle="HolidayUIResources"/></td>
							
							<td class="fontnormal"><mifos:select
								name="holidayActionForm" property="repaymentRuleId" styleId="holiday.input.repaymentrule">
								<c:forEach var="RRT" items="${RepaymentRuleType}" >
									<html-el:option value="${RRT.key}">${RRT.value}</html-el:option>
								</c:forEach>
							</mifos:select></td>
							
						</tr>
						<tr>
							<td align="right" valign="top" class="fontnormal"><mifos:mifoslabel
								name="holiday.ApplicableOffices" mandatory="yes" isColonRequired="Yes" bundle="HolidayUIResources"/>
							</td>
							<td>
							    <div id="officeTree">
							    </div>
							</td>
						</tr>
					</table>
					
				   <table width="96%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="blueline">&nbsp;</td>
						</tr>
						<tr>
							<td align="center">&nbsp;</td>
						</tr>
						<tr>
							<td align="center"><c:choose>
								<c:when
									test="${(BusinessKey.accountType.accountTypeId!=1) }"> <!--&& (holidayActionForm.amount == '0.0'||holidayActionForm.amount=='0')}"-->
									<html-el:submit styleClass="buttn" styleId="holiday.button.preview"
										property="Preview"> 
										<mifos:mifoslabel name="holiday.button.preview" bundle="HolidayUIResources"><!--holiday.reviewtransaction"-->
										</mifos:mifoslabel>
									</html-el:submit>
								</c:when>
								<c:otherwise>
									<html-el:submit styleClass="buttn" styleId="holiday.button.preview" property="Preview">
										<mifos:mifoslabel name="holiday.reviewtransaction" bundle="HolidayUIResources">
										</mifos:mifoslabel>
									</html-el:submit>
								</c:otherwise>
							</c:choose> &nbsp;
									<html-el:button styleClass="cancelbuttn" property="Cancel"
										onclick="javascript:fun_cancel()">
										<mifos:mifoslabel name="holiday.button.cancel" bundle="HolidayUIResources"></mifos:mifoslabel>
									</html-el:button></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			<html-el:hidden property="prdOfferingName" value="${param.prdOfferingName}" />
			<html-el:hidden property="input" value="${param.input}" />
			<html-el:hidden property="globalCustNum" value="${param.globalCustNum}" />
			<html-el:hidden property="globalAccountNum" value="${param.globalAccountNum}" />
			<html-el:hidden property="accountType" value="${BusinessKey.accountType.accountTypeId}" />
			<html-el:hidden property="currentFlowKey" value="${requestScope.currentFlowKey}" />
			<html-el:hidden property="selectedOfficeIds" name="holidayActionForm" styleId="selectedOfficeIds"/>
		</html-el:form>
		<html-el:form action="customerAccountAction.do?method=load">
			<html-el:hidden property="globalCustNum" value="${param.globalCustNum}" />
		</html-el:form>
	</tiles:put>
</tiles:insert>
