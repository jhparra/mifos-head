[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]
[@mifos.header "title" /]
  [@mifos.topNavigationNoSecurity currentTab="Admin" /]
  <!--  Main Content Begins-->
  <div class="content marginAuto" style="margin-left: 5em;">
    <div class="borders span-22">
      <div class="borderbtm span-22">
        <p class="span-17 completeIMG silverheading">[@spring.message "manageLoanProducts.previewLoanProduct.Loanproductinformation"/]</p>
        <p class="span-3 arrowIMG orangeheading last width130px">[@spring.message "reviewAndSubmit"/]</p>
      </div>
      <div class="subcontent ">
        <form method="post" action="previewLoanProducts.ftl" name="previewloanproduct">
          <p class="font15"><span class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.addanewLoanProduct"/]</span>&nbsp;--&nbsp;<span class="orangeheading">[@spring.message "reviewAndSubmit"/]</span></p>
          <p>[@spring.message "reviewtheinformationbelow.ClickSubmit"/]</p>
          [@mifos.showAllErrors "loanProduct.*" /]
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.loanproductdetails"/] </p>
          <div class="span-21 last">
          	<div class="span-20">
          		<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.productinstancename"/]:&nbsp;</span>
          		<span class="span-4">${loanProduct.generalDetails.name}</span>
  			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.shortname"/]:&nbsp;</span>
            	<span class="span-4">${loanProduct.generalDetails.shortName}</span>
  			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.description"/]:&nbsp;</span><br />
            	<span class="span-4">${loanProduct.generalDetails.description}</span>
  			</div>
        	<div class="span-20 ">
        		<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.productcategory"/]:&nbsp;</span>
        		<span class="span-4">${categoryName}</span>
			</div>
            <div class="span-20 last">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.startdate"/]:</span>
            	<span class="span-4">${startDateFormatted}</span>
        	</div>
        	<div class="span-20 last">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.enddate"/]:</span>
            	<span class="span-4">${endDateFormatted}</span>
        	</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.applicablefor"/]:&nbsp;</span>
            	<span class="span-4">${applicableTo}</span>
			</div>
			[#if loanProduct.multiCurrencyEnabled]
			<div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.defineLoanProduct.currency"/]:&nbsp;</span>
            	<span class="span-4">${currencyCode}</span>
			</div>
			[/#if]
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.includeinLoancyclecounter"/]:&nbsp;</span>
            	<span class="span-4">
            	[#if loanProduct.includeInLoanCycleCounter]
            	[@spring.message "boolean.yes"/]
            	[#else]
            	[@spring.message "boolean.no"/]
            	[/#if]
            	</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.calculateLoanAmountas"/]:&nbsp;</span>
            	[#switch loanProduct.selectedLoanAmountCalculationType]
	            	[#case "1"]
	            	<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.sameforallLoans" /]</span>	
	            	[#break]
	            	[#case "2"]
	            	<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.bylastloanAmount" /]</span>
	            	[#break]
					[#case "3"]
					<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.byloanCycle" /]</span>
	            	[#break]
            	[/#switch]
			</div>
          </div>
		  [#switch loanProduct.selectedLoanAmountCalculationType]
	      [#case "1"]
	      <div class="span-20 ">
          	<div class="span-17 bluedivs fontBold paddingLeft">
            	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minloanamount"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxloanamount"/]</span>
                <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultamount"/]</span>
            </div>
            <div class="span-17 paddingLeft">
                <span class="span-4 ">${loanProduct.loanAmountSameForAllLoans.min}</span>
                <span class="span-4 ">${loanProduct.loanAmountSameForAllLoans.max}</span>
                <span class="span-5 last">${loanProduct.loanAmountSameForAllLoans.theDefault}</span>
            </div>
          	<div>&nbsp;</div>
          </div>	
	      [#break]
	      [#case "2"]
	      <div class="span-17 bluedivs fontBold paddingLeft">
	      		<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.lastLoanAmount"/]</span>
            	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minloanamount"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxloanamount"/]</span>
                <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultamount"/]</span>
          </div>
	      <div class="span-20 ">
	      	[#list loanProduct.loanAmountByLastLoanAmount as byLoanAmount]
	      	<div class="span-17 paddingLeft">
	      		<span class="span-4 ">${byLoanAmount.lower} - ${byLoanAmount.upper}</span>
                <span class="span-4 ">${byLoanAmount.min}</span>
                <span class="span-4 ">${byLoanAmount.max}</span>
                <span class="span-5 last">${byLoanAmount.theDefault}</span>
            </div>
            [/#list]
	      	<div>&nbsp;</div>
		  </div>
	      [#break]
		  [#case "3"]
		  <div class="span-20 ">
		  	<div class="span-17 bluedivs fontBold paddingLeft">
	      		<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo"/]</span>
            	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minloanamount"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxloanamount"/]</span>
                <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultamount"/]</span>
          	</div>
	      		<div class="span-20 ">
		      	[#list loanProduct.loanAmountByLoanCycle as byLoanCycle]
		      	<div class="span-17 paddingLeft">
		      	    [#switch byLoanCycle.loanCycleNumber]
		      	    [#case 1]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.zero"/]</span>
	            	[#break]
	            	[#case 2]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.one"/]</span>
	            	[#break]
	            	[#case 3]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.two"/]</span>
	            	[#break]
	            	[#case 4]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.three"/]</span>
	            	[#break]
	            	[#case 5]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.four"/]</span>
	            	[#break]
	            	[#case 6]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.abovefour"/]</span>
	            	[#break]
		      	    [/#switch]
	                <span class="span-4 ">${byLoanCycle.min}</span>
	                <span class="span-4 ">${byLoanCycle.max}</span>
	                <span class="span-5 last">${byLoanCycle.theDefault}</span>
	            </div>
	            [/#list]
	      		<div>&nbsp;</div>
		  	</div>
	      	<div>&nbsp;</div>
		  </div>
		  [#break]
          [/#switch]     

          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.interestrate"/] </p>
          <div class="span-21 last">
	      	<div class="span-20">
	      		<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.interestratetype"/]:&nbsp;</span>
	      		<span class="span-4">${interestRateCalculation}</span>
            </div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.maxInterestrate"/]:&nbsp;</span>
            	<span class="span-4">${loanProduct.maxInterestRate}</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.minInterestrate"/]:&nbsp;</span>
            	<span class="span-4">${loanProduct.minInterestRate}</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.defaultInterestrate"/]:&nbsp;</span>
            	<span class="span-4">${loanProduct.defaultInterestRate}</span>
			</div>
          </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.repaymentSchedule"/]</p>
          <p class="span-20">
          	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.frequencyofinstallments"/]:&nbsp;</span>
          	<span class="span-4">${loanProduct.installmentFrequencyRecurrenceEvery}
          	[#switch loanProduct.installmentFrequencyPeriod]
          		[#case "1"]
	            [@spring.message "manageLoanProducts.defineLoanProduct.week(s)"/]
	            [#break]
	            [#case "2"]
	            [@spring.message "manageLoanProducts.defineLoanProduct.month(s)"/]
	            [#break]
          	[/#switch]
          	</span>
          </p>
          <div class="span-20 ">
          	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.calculateofinstallmentsas"/]:&nbsp;</span>
          	[#switch loanProduct.selectedInstallmentsCalculationType]
	            	[#case "1"]
	            	<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.sameforallLoans" /]</span>	
	            	[#break]
	            	[#case "2"]
	            	<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.bylastloanAmount" /]</span>
	            	[#break]
					[#case "3"]
					<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.byloanCycle" /]</span>
	            	[#break]
           	[/#switch]
		  </div>
		  [#switch loanProduct.selectedInstallmentsCalculationType]
	      [#case "1"]
	      <div class="span-20 ">
	        <div class="span-17 bluedivs fontBold paddingLeft">
            	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minofinstallments"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxofinstallments"/]</span>
                <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultofinstallments"/]</span>
            </div>
            <div class="span-17 paddingLeft">
                <span class="span-4 ">${loanProduct.installmentsSameForAllLoans.min}</span>
                <span class="span-4 ">${loanProduct.installmentsSameForAllLoans.max}</span>
                <span class="span-5 last">${loanProduct.installmentsSameForAllLoans.theDefault}</span>
            </div>
          	<div>&nbsp;</div>
          </div>	
	      [#break]
	      [#case "2"]
	      <div class="span-20 ">
		  		<div class="span-17 bluedivs fontBold paddingLeft">
		      		<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.lastLoanAmount"/]</span>
	            	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minofinstallments"/]</span>
                	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxofinstallments"/]</span>
                	<span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultofinstallments"/]</span>
	          	</div>
		      	<div class="span-20 ">
			      	[#list loanProduct.installmentsByLastLoanAmount as byLoanAmount]
			      	<div class="span-17 paddingLeft">
			      		<span class="span-4 ">${byLoanAmount.lower} - ${byLoanAmount.upper}</span>
		                <span class="span-4 ">${byLoanAmount.min}</span>
		                <span class="span-4 ">${byLoanAmount.max}</span>
		                <span class="span-5 last">${byLoanAmount.theDefault}</span>
		            </div>
		            [/#list]
		        </div>
		      	<div>&nbsp;</div>
		  </div>
	      [#break]
		  [#case "3"]
          <div class="span-20 ">
		  	<div class="span-17 bluedivs fontBold paddingLeft">
	      		<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo"/]</span>
            	<span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.minofinstallments"/]</span>
                <span class="span-4">[@spring.message "manageLoanProducts.previewLoanProduct.maxofinstallments"/]</span>
                <span class="span-5 last">[@spring.message "manageLoanProducts.previewLoanProduct.defaultofinstallments"/]</span>
          	</div>
	      		<div class="span-20 ">
		      	[#list loanProduct.installmentsByLoanCycle as byLoanCycle]
		      	<div class="span-17 paddingLeft">
		      	    [#switch byLoanCycle.loanCycleNumber]
		      	    [#case 1]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.zero"/]</span>
	            	[#break]
	            	[#case 2]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.one"/]</span>
	            	[#break]
	            	[#case 3]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.two"/]</span>
	            	[#break]
	            	[#case 4]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.three"/]</span>
	            	[#break]
	            	[#case 5]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.four"/]</span>
	            	[#break]
	            	[#case 6]
	            	<span class="span-4 ">[@spring.message "manageLoanProducts.defineLoanProduct.loanCycleNo.abovefour"/]</span>
	            	[#break]
		      	    [/#switch]
	                <span class="span-4 ">${byLoanCycle.min}</span>
	                <span class="span-4 ">${byLoanCycle.max}</span>
	                <span class="span-5 last">${byLoanCycle.theDefault}</span>
	            </div>
	            [/#list]
	      		<div>&nbsp;</div>
		  	</div>
	      	<div>&nbsp;</div>
		  </div>
		  [#break]
          [/#switch]
          <div class="span-20 ">
          	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.graceperiodtype"/]:&nbsp;</span>
          	[#switch loanProduct.selectedGracePeriodType]
	            [#case "1"]
	            <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.none" /]</span>	
	            [#break]
	            [#case "2"]
	            <span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.graceonallrepayments" /]</span>
	            [#break]
				[#case "3"]
				<span class="span-4">[@spring.message "manageLoanProducts.defineLoanProduct.principalonlygrace" /]</span>
	            [#break]
           	[/#switch]
		  </div>
          <div class="span-20 ">
          	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.graceperiodduration"/]:&nbsp;</span>
            <span class="span-4">${loanProduct.gracePeriodDurationInInstallments}</span>
		  </div>
          <div class="clear">&nbsp;</div>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.fees"/]</p>
          <p class="span-20">
          	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.feestypes"/]:&nbsp;</span>
          	<span class="span-4">
		    	<ol>
		            [#list fees as fee]
		            <li>${fee}</li>
		            [/#list]
		        </ol>
          	</span>
          </p>
          <p class="fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.accounting"/] </p>
          <div class="span-21 last">
          	<div class="span-20 ">
          		<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.sourcesoffunds"/]:&nbsp;</span>
          		<span class="span-4">
					<ol>
			            [#list funds as fund]
			            <li>${fund}</li>
			            [/#list]
		        	</ol>
          		</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.productGLcode"/]:&nbsp;</span>
            </div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.interest"/]:&nbsp;</span>
                <span class="span-4">${interestGlCode}</span>
			</div>
            <div class="span-20 ">
            	<span class="span-8 fontBold">[@spring.message "manageLoanProducts.previewLoanProduct.principal"/]:&nbsp;</span>
                <span class="span-4">${principalGlCode}</span>
			</div>
            <div class="clear">&nbsp;</div>
            <div class="span-20">
            	<input type="hidden" name="editFormview" value="${editFormview}" />
            	<input class="buttn2" type="submit" name="EDIT" value="[@spring.message "manageLoanProducts.editloanproduct.editLoanproductinformation"/]" />
            </div>
          </div>
          <div class="clear">&nbsp;</div>
          <hr />
          <div class="prepend-9">
            	<input class="buttn" type="submit" name="submit" value="[@spring.message "submit"/]" />
            	<input class="buttn2" type="submit" name="CANCEL" value="[@spring.message "cancel"/]" />
          </div>
          <div class="clear">&nbsp;</div>
        </form>
      </div>
      <!--Subcontent Ends-->
    </div>
  </div>
  <!--Main Content Ends-->
  [@mifos.footer/]