[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[#assign mifostag=JspTaglibs["/tags/mifos-html"]]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
   <!--  Main Content Begins-->
<div class="content definePageMargin">
    <div class="borders margin20lefttop width90prc">
        <div class="borderbtm width100prc height25px">
            <p class="span-17 timelineboldorange arrowIMG  padding20left"
               style="width:50%">[@spring.message "manageProducts.defineNewCategory.productcategoryinformation"/]</p>

            <p class="span-3 timelineboldorange arrowIMG1 last padding20left10right width130px"
               style="float:right">[@spring.message "reviewAndSubmit" /]</p>
        </div>
        <div class="margin20lefttop">
		<form method="post" action="defineNewCategory.ftl" name="defineNewCategory">
		<p class="font15">
			<span class="fontBold">[@spring.message "admin.definenewcategory" /]</span>&nbsp;-&nbsp;<span class="orangeheading">[@spring.message "manageProducts.defineNewCategory.enterProductcategoryinformation" /]</span>
		</p>
        <p>&nbsp;&nbsp;</p>
        <div>[@spring.message "manageProducts.defineNewCategory.completethefieldsbelow.ThenclickPreview.ClickCanceltoreturntoAdminwithoutsubmittinginformation" /]</div>
        <div><span class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired." /] </div>
        <p>&nbsp;&nbsp;</p>
        [@mifos.showAllErrors "formBean.*"/]
        <p class="fontBold margin10bottom">[@spring.message "manageProducts.defineNewCategory.categoryDetails" /]</p>
        <div class="prepend-3  span-21 last">
        	<div class="span-20 ">
        		<span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineNewCategory.productType" /]</span>
        		<span class="span-4">
        			[#--[@spring.bind "formBean.productTypeId"/]
        			<select id="${spring.status.expression}" name="${spring.status.expression}">
        				<option value="" [#if spring.status.value?exists != 1 && spring.status.value?if_exists != 2] selected=="selected"[/#if] >[@spring.message "--Select--"/]</option>
        				<option value="1" [#if spring.status.value?if_exists == 1] selected=="selected"[/#if] >[@spring.message "Loan-Loan"/]</option>
        				<option value="2" [#if spring.status.value?if_exists == 2] selected=="selected"[/#if] >[@spring.message "Savings-Savings"/]</option>
        			</select>--]
        			[@spring.bind "formBean.productTypeId" /]
					    <select id="${spring.status.expression}" name="${spring.status.expression}">
					        <option value="" [@spring.checkSelected ""/]>${springMacroRequestContext.getMessage("--Select--")}</option>
					        [#if typeList?is_hash]
					            [#list typeList?keys as value]
					            <option value="${value?html}"[@spring.checkSelected value/]>
                                    <!-- ftldefinelabels - hack to populate select -->
                                    [@mifostag.mifoslabel name="${springMacroRequestContext.getMessage(typeList[value]?html)}"  /]
                                </option>
					            [/#list]
					        [#else]
					            [#list typeList as value]
					                <option value="${value?html}"[@spring.checkSelected value/]>
                                        [@mifostag.mifoslabel name="${springMacroRequestContext.getMessage(value?html)}"  /]
                                    </option>
					            [/#list]
					        [/#if]
					    </select>
        			
        		</span>
			</div>
			<p>&nbsp;&nbsp;</p>
            <div class="span-20 ">
            	<span class="span-4 rightAlign"><span class="red">* </span>[@spring.message "manageProducts.defineNewCategory.categoryName" /]</span>
            	<span class="span-4">
            		[@spring.bind "formBean.productCategoryName"/]
    				<input type="text" name="${spring.status.expression}" id="${spring.status.expression}" value="${spring.status.value?default("")}" />
            	</span>
  			</div>
  			<p>&nbsp;&nbsp;</p>
            <div class="span-20 ">
            	<span class="span-4 rightAlign">[@spring.message "manageProducts.defineNewCategory.categoryDescription" /]</span>
            	<span>
            	[@spring.bind "formBean.productCategoryDesc"/]
            		<textarea cols="50" rows="6" name="${spring.status.expression}" id="${spring.status.expression}">${spring.status.value?default("")}</textarea>
            	[@spring.showErrors "<br/>"/]
            	</span>[@spring.bind "formBean.productCategoryStatusId"/]
    				<input type="hidden" name="${spring.status.expression}" id="${spring.status.expression}" value="1" />
  			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <div class="buttonsSubmitCancel margin20right">
          	<input class="buttn" type="submit" name="PREVIEW" value="[@spring.message "preview"/]"/>
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