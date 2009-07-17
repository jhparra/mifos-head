require 'watir'
require 'English'
require 'modules/common/inputs'
require 'win32ole'
require 'modules/common/TestClass'
require 'modules/logger/example_logger1'
require 'mysql'
require 'test/unit/assertions'

#include module watir
include Watir
#Testing saving creation and edit savings
class SavingsAccountCreateEdit < TestClass
  #connecting to database and getting office_id, customerid and productid 
  def data_connection(typeid,activeid,savingstype)
    begin   
      name_login=$validname
      @@type_id=typeid
      @@active_id=activeid
      dbquery("select o.search_id,o.office_id from office o,personnel p where o.office_id=p.office_id and p.login_name='"+$validname+"'")
      @@search_id=dbresult[0]
      dbquery("select office_id,display_name from office where status_id=1 and office_level_id=5 and search_id like '"+@@search_id+"%'")
      @@office_id=dbresult[0]
      display_name=dbresult[1]
      if (@@office_id=="1") then
        get_next_data
        @@office_id=dbresult[0]
        display_name=dbresult[1]
      end     
      dbquery("select display_name,customer_id,global_cust_num from customer where customer_level_id="+typeid+" and status_id="+activeid+" and branch_id="+@@office_id)
      @@client_name=dbresult[0]
      @@cust_id=dbresult[1]
      @@global_cust_num=dbresult[2]
      dbquery("select a.prd_offering_id,a.prd_offering_name,b.recommended_amnt_unit_id  from prd_offering a,savings_offering b where b.prd_offering_id=a.prd_offering_id and a.prd_applicable_master_id="+typeid+" and a.prd_category_id=2 and b.savings_type_id="+savingstype+" and a.offering_status_id=2")
      #dbquery("select a.prd_offering_id,a.prd_offering_name from prd_offering a,savings_offering b where b.prd_offering_id=a.prd_offering_id and a.prd_applicable_master_id="+typeid+" and a.prd_category_id=2 and b.savings_type_id=1 and a.offering_status_id=2")
      @@prod_name=dbresult[1]
      @@prod_id=dbresult[0]
      @@recommended_amnt_unit=dbresult[2]

      rescue =>excp
      quit_on_error(excp)
    end
  end 
  
  #reading the data from excel sheet
  def read_savings_values(rowid,sheetid)
    if sheetid==1 then
      @ammount=arrval[rowid+=1].to_i.to_s
      @cammount=arrval[rowid+=1].to_i.to_s
      @typeid=arrval[rowid+=1].to_i.to_s
      @activeid=arrval[rowid+=1].to_i.to_s
      @status=arrval[rowid+=1]
      @nammount=arrval[rowid+=1].to_i.to_s
      @validationammount=arrval[rowid+=1].to_i.to_s
      @savings_type=arrval[rowid+=1].to_i.to_s
    end
    if @status=="partial" then
      @status="Save for later"
    elsif @status=="pending"
      @status="Submit for approval"
    end
  end
  def Ammount()
    @ammount
  end
  def Cammount()
    @cammount
  end
  def Typeid() 
    @typeid
  end
  def Activeid()
    @activeid
  end
  def Status()
    @status
  end
  def Nammount()
    @nammount
  end
  def Validationammount()
    @validationammount
  end
  def Savings_type
  @savings_type
  end
  #loging into Mifos and get some values like groupname.clientname,centername and savings account name from DB             
  def savings_login()
    begin
      start
      login($validname,$validpwd)
      db_connect()
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=55 and locale_id=1")
      @@lookup_savings=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=82 and locale_id=1")
      @@lookup_name_client=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=83 and locale_id=1")
      @@lookup_name_group=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=84 and locale_id=1")
      @@lookup_name_center=dbresult[0]
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #calling the method load_properties from test class to read properties from properties file
  def properties_load()
    begin
      @@groupprop=load_properties("modules/propertis/GroupUIResources.properties")
      @@meetingprop=load_properties("modules/propertis/Meeting.properties")
      @@adminprop=load_properties("modules/propertis/adminUIResources.properties")
      @@menuprop=load_properties("modules/propertis/MenuResources.properties")
      @@custprop=load_properties("modules/propertis/CustomerUIResources.properties")
      @@centerprop=load_properties("modules/propertis/CenterUIResources.properties")
      @@savingsprop=load_properties("modules/propertis/SavingsUIResources.properties")
      @@accountprop=load_properties("modules/propertis/accountsUIResources.properties")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #Getting the lables from properties file
  def geting_lables_from_proprtis()
    begin
      @@savingsaccount_label=@@menuprop['label.create']+" "+@@lookup_savings+" "+@@savingsprop['Savings.account']
      @@savingsaccount_link=@@menuprop['label.create']+" "+@@lookup_savings+" "+@@menuprop['label.account']
      @@mandatory_without_client=@@accountprop['errors.nosearchstring']
      @@savings_select_client=@@savingsaccount_link+" - "+@@accountprop['accounts.SelectACustomer']
      @@button_search=@@accountprop['accounts.search']
      @@savings_select_mmaccount=@@savingsaccount_label+" - "+@@savingsprop['Savings.Enter']+" "+@@lookup_savings+" "+@@savingsprop['Savings.accountInformation']
      @@mandatory_without_product=string_replace_message(@@savingsprop['errors.mandatoryselect'],"{0}",@@savingsprop['Savings.productInstance'])
      @@button_continue=@@accountprop['accounts.continue']
      @@button_preview=@@accountprop['accounts.preview']
      @@button_submit=@@accountprop['accounts.submit']
      @@edit_savings_button=@@savingsprop['Savings.Edit']+" "+@@lookup_savings+" "+@@savingsprop['Savings.accountInformation']
      @@savings_success=@@savingsprop['Savings.successfullyCreated']+" "+@@lookup_savings
      @@edit_account_status=@@accountprop['loanedit_acc_status']
      @@view_savings_details_now=@@savingsprop['Savings.View']+" "+@@lookup_savings+" "+@@savingsprop['Savings.account']+" "+@@savingsprop['Savings.detailsNow']
      @@view_all_account_activity=@@savingsprop['Savings.viewAllAccountActivity']
      @@button_return=@@savingsprop['Savings.returnToAccountDetails']
      @@account_statement_as_of=@@savingsprop['Savings.Accountstatementasof']
      @@view_transaction_history=@@savingsprop['Savings.viewTransactionHistory']
      @@edit_account_information=@@savingsprop['Savings.EditAccountInformation']
      @@view_status_history=@@savingsprop['Savings.viewStatusHistory']
      @@close_account=@@savingsprop['Savings.closeAccount']
      @@view_deposit_dew_details=@@savingsprop['Savings.viewDepositDueDetails']
      @@deposit_dew_details=@@savingsprop['Savings.depositduedetails']
      @@savings_account_close_msg=@@savingsprop['Savings.reviewDetails']+" "+@@savingsprop['Savings.clickSubmitIfSatisfied']+" "+@@savingsprop['Savings.clickCancelToReturn']+" "+@@savingsprop['Savings.detailsWithOutClosing']
      @@make_depositorwithdrawal=@@savingsprop['Savings.makeDepositWithdrawl']
      @@button_reviewtransaction=@@savingsprop['Savings.reviewTransaction']
      @@button_search=@@centerprop['button.Search']
      @@total_amnt_due_on=@@savingsprop['Savings.totalAmountDue']
      @@type_of_deposit=@@savingsprop['Savings.typeOfDeposits']
  
      rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking for Clients & Accounts link whether it exists or not
  def check_savings_account_creation_link_exist()
    begin
      $ie.link(:text,"Clients & Accounts").click
      assert($ie.contains_text(@@savingsaccount_link))
      $logger.log_results("Link Check","Create Margin Money Account","Existed","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Link Check","Create Margin Money Account","Not existed","failed")
    rescue =>excp
      quit_on_error(excp)  
    end
  end
  #checking for Create Savings Account link working or not
  def click_savings_account_link_working()
    begin
      $ie.link(:text,@@savingsaccount_link).click
      assert($ie.contains_text(@@savings_select_client))
      $logger.log_results("Create Margin Money Account - Select client","should exist","existed","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Create Margin Money Account - Select client","should exist","Not existed","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end	
  #checking for mandatory fields in select client page
  def check_mandatory_conditions_in_select_client_page()
    begin
      $ie.button(:value,@@button_search).click
      assert($ie.contains_text(@@mandatory_without_client))
      $logger.log_results("Mandatory Check when no client selected","N/A","N/A","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check when no client selected","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #selcting the client while creating savings account
  def select_client_while_creating_savings_account(typeid,activeid,savingstype)
    begin
      data_connection(typeid,activeid,savingstype)
      $ie.text_field(:name,"searchString").set(@@client_name)
      $ie.button(:value,@@button_search).click
      #$ie.goto($test_site+"/savingsAction.do?method=getPrdOfferings&customerId="+@@cust_id)
      $ie.link(:text,@@client_name+":ID"+@@global_cust_num).click
      assert($ie.contains_text(@@savings_select_mmaccount))
      $logger.log_results("Create Margin Money Account - Enter Margin Money account information","should exist","existed","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Create Margin Money Account - Enter Margin Money account information","should exist","Not existed","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking the mandatory in select savings instance page
  def checking_mandatory_conditions_in_select_savings_page()
    begin
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@mandatory_without_product))
      $logger.log_results("Mandatory Check when no product selected","N/A","N/A","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory Check when no product selected","N/A","N/A","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #selecting the savings instance while creating savings account
  def select_savings_product_while_creating_savings_account()
    begin
      $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@prod_id)
      $ie.button(:value,@@button_continue).click
      assert($ie.contains_text(@@savings_select_mmaccount))
      $logger.log_results("Page Should redirect to  Enter Margin Money account information","N/A","redirected","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Page Should redirect to  Enter Margin Money account information","N/A","not redirected","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking mandatory conditions in create savings account enter account information page and 
  #when there is no product instance selected
  def check_mandatory_conditions_in_savings_creation_page_when_no_product_selected()
    begin
      $ie.select_list(:name,"selectedPrdOfferingId").select_value("")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@mandatory_without_product))
      $logger.log_results("Mandatory check with out Margin Money instance","should display error","displaying","Passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Mandatory check with out Margin Money instance","should display error","Not displaying","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking validations of custom field
  #enter nore data in that and check boundary condition
  def validate_additional_field_in_savings_creation_page(validationammount)
    begin
      $ie.select_list(:name,"selectedPrdOfferingId").select_value(@@prod_id)
      $ie.text_field(:name,"customField[0].fieldValue").set(validationammount)
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(validationammount))
      $logger.log_results("Validation Cheking","Should done properly","Checking","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Validation Cheking","Should done properly","Checking","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking the ammount in the preview page 
  #whether it is showing the ammount which you enter in the previous page
  def click_preview_from_savings_creation_page(ammount)
    begin
      $ie.button(:name,"editButton").click
      $ie.text_field(:name,"recommendedAmount").set(ammount)
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@prod_name))
      $logger.log_results(@@prod_name,"text","exists","passed")
    rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results(@@prod_name,"text","exists","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #edit data from preview page while creating savings account
  def edit_from_preview_page(cammount)
    begin
      @@c_ammount=cammount
      $ie.button(:name,"editButton").click
      $ie.text_field(:name,"recommendedAmount").set(cammount)
      $ie.button(:value,@@button_preview).click
      $logger.log_results(cammount,"data","exists","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results(cammount,"data","exists","passed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #Submitting the savings account data from review&submit page
  def click_submit_data(status)
    begin
      $ie.button(:value,status).click
      assert($ie.contains_text(@@savings_success))
      $logger.log_results("Margin Money Account","Creation","successfull","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Margin Money Account","Creation","successfull","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #data base check for the created savings account
  def database_check()
    begin
      dbquery("select recommended_amount,prd_offering_id from savings_account where prd_offering_id="+@@prod_id)
      d_ammount=dbresult[0]
      d_prdid=dbresult[1]
      if d_prdid==@@prod_id  then
        $logger.log_results("Database values","Matching","successfully","passed")
      else
        $logger.log_results("Database values","Matching","successfully","failed")
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #Navigating to Savings details page
  def go_to_savings_details_page
    begin
      $ie.link(:text,@@view_savings_details_now).click
      dbquery("select global_account_num,account_state_id from account where account_id=(select max(account_id) from account)")
      @@account_id=dbresult[0]
      @@status_id=dbresult[1]
      assert($ie.contains_text(@@account_id))
      $logger.log_results("View Margin Money Account Details Now","Link","working","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Margin Money Account Details Now","Link","working","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #Id's for status change
  def statusChange
    begin
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=7")
      @@status_partial_name=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=8")
      @@status_pending_name=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=13")
      @@lookup_active=dbresult[0]
      dbquery("SELECT lookup_value FROM lookup_value_locale where lookup_id=14")
      @@lookup_inactive=dbresult[0]
      if @@status_id=="16"
        change_statusInactive
      elsif @@status_id=="13"
        change_statusPending
      elsif @@status_id=="14"	
        change_statusActive
      elsif @@status_id=="18"
        change_statusActive
      end
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #changing status to Inactive
  def change_statusInactive
    begin
      $ie.link(:text,@@edit_account_status).click
      $ie.radio(:name,"newStatusId","18").set
      $ie.text_field(:name,"notes").set("aaaaa")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@lookup_inactive))
      $logger.log_results("Status Change","preview","page loaded","passed")
      $ie.button(:value,@@button_submit).click
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status Change","preview","page loaded","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #changing status to Active
  def change_statusActive
    begin
      $ie.link(:text,@@edit_account_status).click
      $ie.radio(:name,"newStatusId","16").set
      $ie.text_field(:name,"notes").set("aaaaa")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@lookup_active))
      $logger.log_results("Status Change","preview","page loaded","passed")
      $ie.button(:value,@@button_submit).click
      check_view_deposit_due_details_link_exist
   #   check_view_deposit_due_details_link_functionality
      check_view_status_history_link_functionality_active
      check_close_account_link_exist
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status Change","preview","page loaded","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #changing status to pending
  def change_statusPending
    begin
      $ie.link(:text,@@edit_account_status).click
      $ie.radio(:name,"newStatusId","14").set
      $ie.text_field(:name,"notes").set("aaaaa")
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(@@status_pending_name))
      $logger.log_results("Status Change","preview","page loaded","passed")
      $ie.button(:value,@@button_submit).click
      check_view_status_history_link_functionality_pending
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Status Change","preview","page loaded","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking the view all account activity link functionality
  def check_view_all_Account_activity_link_functionality
    begin
      $ie.link(:text,@@view_all_account_activity).click
      assert($ie.contains_text(@@account_statement_as_of))
      $logger.log_results("View Account Activity","page","loaded","passed")
      $ie.button(:value,@@button_return).click
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Account Activity","page","loaded","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking the View transaction history link functionality
  def check_view_transaction_history_link_functionality
    begin
      $ie.link(:text,@@view_transaction_history).click
      check_text=@@account_id+" - "+@@savingsprop['Savings.Transactionhistory']
      assert($ie.contains_text(check_text))
      $logger.log_results("View transaction history","page","loaded","passed")
      $ie.button(:value,@@button_return).click
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("View transaction history","page","loaded","failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #editing data from savings details page
  def edit_savingsaccount_from_savings_details_page(nammount)
    begin
      $ie.link(:text,@@edit_account_information).click
      $ie.text_field(:name,"recommendedAmount").set(nammount)	
      $ie.button(:value,@@button_preview).click
      assert($ie.contains_text(nammount))
      $logger.log_results("Edit","ammountfrom","details page","passed")
      $ie.button(:value,@@button_submit).click
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Edit","ammountfrom","details page","passed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking for view staus history link
  def check_for_view_status_history_link_exist()
    begin
      assert($ie.contains_text(@@view_status_history))
      $logger.log_results("View status history","Link Should Exist","Existed","passed")
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("View status history","Link Should Exist","Not Existed","Failed")
    rescue =>excp
      quit_on_error(excp)
    end
  end
  #checking the view staus history page aftre changing that status to pending
  def check_view_status_history_link_functionality_pending()
    begin
      $ie.link(:text,@@view_status_history).click
      assert($ie.contains_text(@@status_partial_name))and assert($ie.cotains_text(@@status_pending_name))
      $logger.log_results("View Status History is displaying proper data","N/A","N/A","Passed")    
      $ie.button(:value,@@button_return).click
    rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Status History  is displaying proper data","N/A","N/A","Failed")    
      $ie.button(:value,@@button_return).click
    rescue =>excp
      quit_on_error(excp)
    end 
  end
  #checking the view staus history page aftre changing that status to active
  def check_view_status_history_link_functionality_active()
    begin
      $ie.link(:text,@@view_status_history).click
      assert($ie.contains_text(@@status_pending_name))and assert($ie.cotains_text(@@lookup_active))
      $logger.log_results("View Status History  is displaying proper data","N/A","N/A","Passed")    
      $ie.button(:value,@@button_return).click
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("View Status History  is displaying proper data","N/A","N/A","Failed")    
      $ie.button(:value,@@button_return).click
      rescue =>excp
      quit_on_error(excp)
    end 
  end
  
  #check for close account link after changing the account status to active
  def check_close_account_link_exist
    begin
      assert($ie.contains_text(@@close_account))
      check_close_account_link_functionality()
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Close account","link","not existed","Failed")
      rescue =>excp
      quit_on_error(excp)
    end
  end
  # changing status to close
  def check_close_account_link_functionality
    begin
      $ie.link(:text,@@close_account).click
      $ie.select_list(:name,"paymentTypeId").select_value("1")
      if (@@type_id=="2" and @@recommended_amnt_unit.to_i==1)
        $ie.select_list(:name,"customerId").select("Non-specified")
      end
      $ie.text_field(:name,"notes").set("aaaaa")
      $ie.button(@@button_preview).click
      assert($ie.contains_text(@@savings_account_close_msg))
      $logger.log_results("Account","close","status","Passed")
      $ie.button(:value,@@button_submit).click
      rescue  Test::Unit::AssertionFailedError=>e
      $logger.log_results("Account","close","status","Failed")
      rescue =>excp
      quit_on_error(excp)
    end
  end
  #check for view deposit due details link exist
  def check_view_deposit_due_details_link_exist()
        if(@savings_type.to_i==1) then
            begin
              assert($ie.contains_text(@@view_deposit_dew_details))
              check_view_deposit_due_details_link_functionality()
              $logger.log_results(@@view_deposit_dew_details,"should exist","existed","Passed")
              rescue  Test::Unit::AssertionFailedError=>e
              $logger.log_results(@@view_deposit_dew_details,"should exist","not existed","Failed")
              rescue =>excp
              quit_on_error(excp)
           end  
        elsif(@savings_type.to_i==2) then
           begin
              assert(!($ie.contains_text(@@view_deposit_dew_details)))
              $logger.log_results(@@view_deposit_dew_details,"Should not exist","does not exist","Passed")
              rescue  Test::Unit::AssertionFailedError=>e
              $logger.log_results(@@view_deposit_dew_details,"Should not exist","existed","Failed")
              rescue =>excp
              quit_on_error(excp)
          end  
        end 
        
  
  end   
  #check for view deposit due details link working or not
  def check_view_deposit_due_details_link_functionality()
    begin
            $ie.link(:text,@@view_deposit_dew_details).click
            begin
              assert($ie.contains_text(@@deposit_dew_details))
              $logger.log_results(@@view_deposit_dew_details,"should work","Working","Passed")
              $ie.button(:value,@@button_return).click
              rescue  Test::Unit::AssertionFailedError=>e
              $logger.log_results(@@view_deposit_dew_details,"should work","not Working","Failed")	   
              $ie.button(:value,@@button_return).click
              rescue =>excp
              quit_on_error(excp)
            end  
      end  
    end
 
  def mandatory_membername_validation()
  begin
    dbquery("select global_account_num,account_state_id,account_id from account where account_type_id=2 and  account_state_id in (14,16) and customer_id in (select customer_id from customer where customer_level_id=3)")
    globalacctnum=dbresult[0]
    account_state=dbresult[1]
    @@account_id=dbresult[2]
    $ie.link(:text,"Clients & Accounts").click
    $ie.text_field(:name,"searchString").set(globalacctnum)
    $ie.button(:value,@@button_search).click
    $ie.link(:text,"Account # "+globalacctnum).click
      if(account_state.to_i==14) #state change required only for pending status
       # statusChange()
      $ie.link(:text,@@edit_account_status).click
      $ie.radio(:name,"newStatusId","16").set
      $ie.text_field(:name,"notes").set("aaaaa")
      $ie.button(:value,@@button_preview).click()
      $ie.button(:value,@@button_submit).click()
      end
      
    #added as part of bug#690
    check_nextmeeting_date()
    #added as part of bug#870
    check_typeofdeposit(@@account_id)
    
    $ie.link(:text,@@make_depositorwithdrawal).click
        begin
        assert($ie.contains_text(globalacctnum+" - "+@@make_depositorwithdrawal))
        $logger.log_results(@@make_depositorwithdrawal+ " link","should work","Working","Passed")    
        $ie.select_list(:name,"trxnTypeId").select("Deposit")
        $ie.text_field(:name,"amount").set("500")
        $ie.select_list(:name,"paymentTypeId").select("Cash")
        $ie.button(:value,@@button_reviewtransaction).click
               begin
               assert($ie.contains_text("Please specify Client name."))
               $logger.log_results("Mandatory check for member name","Do not select the Member name","Validating","passed")    
               rescue Test::Unit::AssertionFailedError=>e
               $logger.log_results("Mandatory check for member name","Do not select the Member name","Not Validating","failed")    
                rescue =>excp
               quit_on_error(excp)
               end
        rescue Test::Unit::AssertionFailedError=>e
        $logger.log_results(@@make_depositorwithdrawal+" link","should work","not working","failed")    
        rescue =>excp
        quit_on_error(excp)
       end
   end    
 end # end of mandatory_membername_validation()
 
 def check_nextmeeting_date()
   begin
     dbquery("Select distinct(date_format(action_date,'%d/%m/%Y')),sum(deposit),SUM(Deposit_paid)  from Saving_Schedule where Account_Id ="+@@account_id+" and action_date >= current_date and installment_Id = (Select min(installment_id) from Saving_Schedule where action_date >= current_date and Account_Id ="+@@account_id+") group by action_date")
     nextmeetingdate=dbresult[0]
     totaldepositdue=dbresult[1]
     totaldepositpaid=dbresult[2]
     deposit_topay=totaldepositdue.to_f - totaldepositpaid.to_f
     assert($ie.contains_text(@@total_amnt_due_on+" "+nextmeetingdate+": "+deposit_topay.to_f.to_s))
     $logger.log_results("Bug#690-Total amount due display","Should display the total amount due","Displaying","passed")         
     rescue Test::Unit::AssertionFailedError=>e
     $logger.log_results("Bug#690-Total amount due display","Should display the total amount due","Not Displaying","failed")               
     rescue=>excp
     quit_on_error(excp)
   end   
 end # end of check_nextmeeting_date()
 
 def check_savings_accountstate_after_depositwithdrawal()
    begin
      dbquery("select global_account_num,account_state_id,account_id from account where account_type_id=2 and  account_state_id =16 and customer_id in (select customer_id from customer where customer_level_id=3)")
      globalacctnum=dbresult[0]
      account_state=dbresult[1]
      account_id=dbresult[2]
      $ie.link(:text,"Clients & Accounts").click
      $ie.text_field(:name,"searchString").set(globalacctnum)
      $ie.button(:value,@@button_search).click
      $ie.link(:text,"Account # "+globalacctnum).click
      change_statusInactive()
      $ie.link(:text,@@make_depositorwithdrawal).click
      $ie.select_list(:name,"customerId").select("Non-specified")
      $ie.select_list(:name,"trxnTypeId").select("Deposit")
      $ie.text_field(:name,"amount").set("500")
      $ie.select_list(:name,"paymentTypeId").select("Cash")
      $ie.button(:value,@@button_reviewtransaction).click
      $ie.button(:value,@@button_submit).click    
      dbquery("select account_state_id from account where account_type_id=2 and  account_id="+account_id+"")    
      account_state=dbresult[0]
      assert(account_state.to_i==16)
      $logger.log_results("Bug#497-Inactive Savings account status change to active on making a deposit/withdrawal","Make a deposit/withdrawal from savings account","The status of the account should change to active","passed")         
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Bug#497-Inactive Savings account status change to active on making a deposit/withdrawal","Make a deposit/withdrawal from savings account","The status of the account did not change to active","failed")         
      rescue=>excp
      quit_on_error(excp)
    end
    
 end # end of check_savings_accountstate_after_depositwithdrawal()
 
 def check_typeofdeposit(account_id)
    begin
      dbquery("select savings_type_id from savings_account where account_id="+account_id+"")
      savingstype=dbresult[0]
      if(savingstype.to_i==1) then 
      deposit_type="Mandatory"
      elsif(savingstype.to_i==2) then 
      deposit_type="Voluntary"
      end
#      @@type_of_deposit
      assert($ie.contains_text(@@type_of_deposit+": "+deposit_type))
      $logger.log_results("Bug#870-Type of deposit display","Select any savings account","The type of deposit displayed","passed")         
      rescue Test::Unit::AssertionFailedError=>e
      $logger.log_results("Bug#870-Type of deposit display","Select any savings account","The type of deposit is not displayed","failed")         
      rescue=>excp
      quit_on_error(excp)
   end   
 end # end of check_typeofdeposit()
end #end of class
  
 

class SavingsAccountTest 
  savingsobj=SavingsAccountCreateEdit.new
  filename=File.expand_path( File.dirname($PROGRAM_NAME))+"/data/testdata.xls"
  savingsobj.open(filename,1)
  rowid=-1  
  colid=1
  #logging into mifos
  savingsobj.savings_login
  #loading and getting the values from properties file
  savingsobj.properties_load
  savingsobj.geting_lables_from_proprtis
  while(rowid<$maxrow*$maxcol-1)
    #reading the values form Excel and pass it to methods where it requires
    savingsobj.read_savings_values(rowid,1)
    savingsobj.check_savings_account_creation_link_exist
    savingsobj.click_savings_account_link_working
    savingsobj.check_mandatory_conditions_in_select_client_page
    savingsobj.select_client_while_creating_savings_account(savingsobj.Typeid,savingsobj.Activeid,savingsobj.Savings_type)
    savingsobj.checking_mandatory_conditions_in_select_savings_page
    savingsobj.select_savings_product_while_creating_savings_account
    savingsobj.check_mandatory_conditions_in_savings_creation_page_when_no_product_selected()
    savingsobj.validate_additional_field_in_savings_creation_page(savingsobj.Validationammount)
    savingsobj.click_preview_from_savings_creation_page(savingsobj.Ammount)
    savingsobj.edit_from_preview_page(savingsobj.Cammount)
    savingsobj.click_submit_data(savingsobj.Status)
    savingsobj.database_check
    savingsobj.go_to_savings_details_page
    savingsobj.edit_savingsaccount_from_savings_details_page(savingsobj.Nammount)
    savingsobj.check_view_transaction_history_link_functionality
    savingsobj.check_for_view_status_history_link_exist()
    savingsobj.check_view_all_Account_activity_link_functionality
    savingsobj.statusChange
    rowid+=$maxcol
  end
  savingsobj.mandatory_membername_validation()
  savingsobj.check_savings_accountstate_after_depositwithdrawal()
  savingsobj.mifos_logout()
end